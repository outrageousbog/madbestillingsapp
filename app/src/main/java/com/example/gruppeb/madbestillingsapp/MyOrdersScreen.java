package com.example.gruppeb.madbestillingsapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.gruppeb.madbestillingsapp.Adapter.MyOrdersScreenAdapter;
import com.example.gruppeb.madbestillingsapp.Connector.Connector;
import com.example.gruppeb.madbestillingsapp.Model.MyOrdersScreenModel;
import com.example.gruppeb.madbestillingsapp.Domain.Dishes.Order;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MyOrdersScreen extends AppCompatActivity{

    RecyclerView recyclerView;
    MyOrdersScreenAdapter adapter;

    List<MyOrdersScreenModel> dishesFromOrdersList;

    Toolbar mToolbarOrders;
    TextView mNumberOfOrdersCountTextView;

    private String strDate;
    private String roomNumberStringFromExtra;
    private int roomNumberStringFromExtraToInt;

    ProgressDialog progressDialog;

    Connector mConnector; //Database connector

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders_screen);
        setTitle(getString(R.string.my_orders_title));

        mToolbarOrders = findViewById(R.id.my_OrdersToolbar);
        setSupportActionBar(mToolbarOrders);

        //RecyclerView, dishesFromOrdersList
        dishesFromOrdersList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView_MyOrdersRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mNumberOfOrdersCountTextView = findViewById(R.id.textView_numberOfOrdersCount);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String roomNumberFromIntent;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                roomNumberFromIntent = null;
            } else {
                roomNumberFromIntent = extras.getString("roomNumber");
                roomNumberStringFromExtra = roomNumberFromIntent;
                roomNumberStringFromExtraToInt = Integer.parseInt(roomNumberStringFromExtra);
            }
        } else {
            roomNumberFromIntent = (String) savedInstanceState.getSerializable("roomNumber");
        }

        progressDialog = new ProgressDialog(this);

        mConnector = new Connector(); //Database

        MyOrdersScreen.MyOrdersListAsyncTaskStatement mMyOrdersListAsyncTaskStatement = new MyOrdersScreen.MyOrdersListAsyncTaskStatement();
        mMyOrdersListAsyncTaskStatement.execute();
    }

    /**
     * https://developer.android.com/training/appbar/actions#java
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * https://gist.githubusercontent.com/pacificregmi/00927e29c4c0f9eae414/raw/eff50094306248331fdf570e3c5f13e57fe85b69/MainActivity.java
     */
    public void getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-d");
        strDate = mdformat.format(calendar.getTime());
        strDate += "%";
        System.out.print(strDate);
    }

    private class MyOrdersListAsyncTaskStatement extends AsyncTask<String, String, String> {
        private int orderIDInDBStringFromQuery;
        private String orderMenuInDBStringFromQuery, orderBreadTypeInDBStringFromQuery, orderDateInDBStringFromQuery;
        private String errorMessage = "";
        private boolean isSuccess = false;

        ArrayList<Integer> orderIDInDBStringFromQueryList;
        ArrayList<String> orderMenuInDBStringFromQueryList, orderBreadTypeInDBStringFromQueryList, orderDateInDBStringFromQueryList;

        private int conditionInt = 1;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Indlæser");
            progressDialog.show();
            getCurrentDate();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if (conditionInt < 0)
                errorMessage = getString(R.string.login_login_message_errorhashappend);
            else {
                try {
                    Connection con = mConnector.CONN();
                    if (con == null) {
                        errorMessage = getString(R.string.login_login_message_checkinternet);
                    } else {
                        orderIDInDBStringFromQueryList = new ArrayList<Integer>();
                        orderMenuInDBStringFromQueryList = new ArrayList<String>();
                        orderBreadTypeInDBStringFromQueryList = new ArrayList<String>();
                        orderDateInDBStringFromQueryList = new ArrayList<String>();

                        System.out.println("Forbindelse til DB er aktiv.");

                        String query = " SELECT * FROM Orders WHERE roomNumber='" + Order.ROOM_NUMBER + "' AND orderDate LIKE '" + strDate + "'";

                        Statement stmt = con.createStatement();

                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {
                            //i = Placement of column in table
                            orderIDInDBStringFromQuery = rs.getInt(1); //orderID
                            orderMenuInDBStringFromQuery = rs.getString(3); //orderMenu
                            orderBreadTypeInDBStringFromQuery = rs.getString(4); //breadType
                            orderDateInDBStringFromQuery = rs.getString(5); //orderDate

                            orderIDInDBStringFromQueryList.add(orderIDInDBStringFromQuery);
                            orderMenuInDBStringFromQueryList.add(orderMenuInDBStringFromQuery);
                            orderBreadTypeInDBStringFromQueryList.add(orderBreadTypeInDBStringFromQuery);
                            orderDateInDBStringFromQueryList.add(orderDateInDBStringFromQuery);

                            isSuccess = true;
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                for (int i = 0; i < orderMenuInDBStringFromQueryList.size(); i++) {
                                    int orderIDFromDB = orderIDInDBStringFromQueryList.get(i);
                                    String orderMenuFromDB = orderMenuInDBStringFromQueryList.get(i);
                                    String orderBreadTypeFromDB = orderBreadTypeInDBStringFromQueryList.get(i);
                                    String orderDateFromDB = orderDateInDBStringFromQueryList.get(i);

                                    dishesFromOrdersList.add(new MyOrdersScreenModel(orderIDFromDB, orderMenuFromDB, orderBreadTypeFromDB, orderDateFromDB));
                                }

                                adapter = new MyOrdersScreenAdapter(getApplicationContext(), dishesFromOrdersList);
                                recyclerView.setAdapter(adapter);

                            }
                        });

                    }

                } catch (Exception ex) {
                    isSuccess = false;
                    errorMessage = "Exceptions" + ex;
                }
            }
            return errorMessage;
        }

        @Override
        protected void onPostExecute(String s) {
            if (isSuccess) {
            }
            progressDialog.hide();
        }
    }

}
