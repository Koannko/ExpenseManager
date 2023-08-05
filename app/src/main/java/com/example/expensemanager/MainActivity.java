package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.expensemanager.databinding.ActivityMainBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnItemsClick{
    ActivityMainBinding binding;
    private ExpensesAdapter expensesAdapter;
    private long income = 0, expense = 0;

    private Map<String, Long> categories = new HashMap<>();
    Button buildAnaliticsButton;

    protected static final int[] colors = new int[]{
            R.color.barbie_mint,
            R.color.barbie_gold,
            R.color.barbie_light_blue,
            R.color.barbie_pink,
            R.color.barbie_coral,
            R.color.barbie_yellow,
            R.color.barbie_light_pink,
            R.color.barbie_purple,
            R.color.barbie_lavender,
            R.color.barbie_blue,
            R.color.barbie_silver,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        buildAnaliticsButton = (Button) findViewById(R.id.buildAnalitics);
        expensesAdapter=new ExpensesAdapter(this, this);
        binding.recycler.setAdapter(expensesAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
        Intent analyticsIntent = new Intent(MainActivity.this, BuildAnalyticsActivity.class);
        binding.addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {
                intent.putExtra("type", "Income");
                startActivity(intent);
            }
        });
        binding.addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {
                intent.putExtra("type", "Expense");
                startActivity(intent);
            }
        });
        buildAnaliticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {
                startActivity(analyticsIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please");
        progressDialog.setMessage("Wait");
        progressDialog.setCancelable(false);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            progressDialog.show();
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressDialog.cancel();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.cancel();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        income=0;expense=0;
        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.buildAnalitics) {
            Intent intent1 = new Intent(this,BuildAnalyticsActivity.class);
            this.startActivity(intent1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void getData() {
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                      @Override
                      public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                          expensesAdapter.clear();
                          categories.clear();
                          List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments();
                          for(DocumentSnapshot ds:dsList){
                              ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);
                              if(expenseModel.getType().equals("Income")) {
                                  income+= expenseModel.getAmount();
                              } else {
                                  expense+= expenseModel.getAmount();
                                  long amount = 0;
                                  if (categories != null) {
                                      if (categories.containsKey(expenseModel.getCategory())) {
                                          amount = categories.get(expenseModel.getCategory());
                                      }
                                      amount += expenseModel.getAmount();
                                  }
                                  categories.put(expenseModel.getCategory(), amount);
                              }
                              expensesAdapter.add(expenseModel);
                          }
                          setUpGraph();
                      }
                  }
                );
    }

    private void setUpGraph() {
        int colorIndex = 0;
        int len_colors = colors.length;
        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorsList = new ArrayList<>();
        if (expense != 0) {
            for (String key : categories.keySet()) {
                colorIndex += 1 % len_colors;
                pieEntryList.add(new PieEntry(categories.get(key), key));
                colorsList.add(getResources().getColor(colors[colorIndex]));
                System.out.println(key + " --- " + categories.get(key) + " --- " + colors[colorIndex]);
            }
        }
//        if (expense != 0) {
//            pieEntryList.add(new PieEntry(income, "Expense"));
//            colorsList.add(getResources().getColor(R.color.red));
//        }
        PieDataSet pieDataSet=new PieDataSet(pieEntryList, String.valueOf(income-expense));
        pieDataSet.setColors(colorsList);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.black));
        pieDataSet.setValueTextSize(10);


        PieData pieData = new PieData(pieDataSet);
        binding.pieChart.setCenterTextSize(10);
        binding.pieChart.setEntryLabelColor(getResources().getColor(R.color.white));

        binding.pieChart.setData(pieData);
        binding.pieChart.invalidate();

    }

    @Override
    public void onClick(ExpenseModel expenseModel) {
        Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
        intent.putExtra("model", expenseModel);
        startActivity(intent);
    }
}