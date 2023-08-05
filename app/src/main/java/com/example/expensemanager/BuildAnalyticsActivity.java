package com.example.expensemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensemanager.databinding.ActivityAddExpenseBinding;
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

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BuildAnalyticsActivity extends AppCompatActivity {
//    ActivityMainBinding binding;
    private ExpenseModel expenseModel;
    private ExpensesAdapter expensesAdapter;
    private long income = 0, expense = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        expenseModel=(ExpenseModel) getIntent().getSerializableExtra("model");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.analytics_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.weekDays) {
            showWeekDaysChart();
        }
        if(id==R.id.weekly) {
            showWeeklyChart();
        }
        if(id==R.id.monthly) {
            showMonthlyChart();
        }
        return false;
    }

    private void showMonthlyChart() {
    }

    private void showWeeklyChart() {
    }

    private void showWeekDaysChart() {
    }


    private Map<String, Long> categories = new HashMap<>();


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
                            Toast.makeText(BuildAnalyticsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getData();
    }
//
//    private void getData() {
//        FirebaseFirestore
//                .getInstance()
//                .collection("expenses")
//                .whereEqualTo("uid", FirebaseAuth.getInstance().getUid())
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                          @Override
//                                          public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                              expensesAdapter.clear();
//                                              categories.clear();
//                                              List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments();
//                                              for(DocumentSnapshot ds:dsList){
//                                                  ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);
//                                                  if(expenseModel.getType().equals("Income")) {
//                                                      income+= expenseModel.getAmount();
//                                                  } else {
//                                                      expense+= expenseModel.getAmount();
//                                                      long amount = 0;
//                                                      if (categories != null) {
//                                                          if (categories.containsKey(expenseModel.getCategory())) {
//                                                              amount = categories.get(expenseModel.getCategory());
//                                                          }
//                                                          amount += expenseModel.getAmount();
//                                                      }
//                                                      categories.put(expenseModel.getCategory(), amount);
//                                                  }
//                                                  expensesAdapter.add(expenseModel);
//                                              }
//                                              setUpGraph();
//                                          }
//                                      }
//                );
//    }

    private void setUpGraph() {
        int colorIndex = 0;
        int len_colors = MainActivity.colors.length;
        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorsList = new ArrayList<>();
        if (expense != 0) {
            for (String key : categories.keySet()) {
                colorIndex += 1 % len_colors;
                pieEntryList.add(new PieEntry(categories.get(key), key));
                colorsList.add(getResources().getColor(MainActivity.colors[colorIndex]));
                System.out.println(key + " --- " + categories.get(key) + " --- " + MainActivity.colors[colorIndex]);
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
//        binding.pieChart.setCenterTextSize(10);
//        binding.pieChart.setEntryLabelColor(getResources().getColor(R.color.white));
//
//        binding.pieChart.setData(pieData);
//        binding.pieChart.invalidate();

    }

}
