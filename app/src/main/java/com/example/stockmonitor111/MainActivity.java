package com.example.stockmonitor111;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    ListView stockList;
    ArrayList<String> stockDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stockList = findViewById(R.id.fetchedData);

        FetchDataPrices task = new FetchDataPrices();
        task.execute();
    }


    public class FetchDataPrices extends AsyncTask<String, String, String> {

        @Override
        public String doInBackground(String... strings) {
            final String data = loadFromWeb("https://financialmodelingprep.com/api/company/price/AAPL,INTC,IBM,GOOGL,FB,NOK,RHT?datatype=json");
            if (data != null) {
                stockDataList = parseStockData(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, stockDataList);
                        stockList.setAdapter(arrayAdapter);
                    }
                });
            }
            return data;
        }

        private ArrayList<String> parseStockData(String data) {
            stockDataList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(data);
                Iterator<String> it = jsonObject.keys();
                while (it.hasNext()) {
                    String stockName = it.next();
                    if (jsonObject.get(stockName) instanceof JSONObject) {
                        JSONObject stock = jsonObject.getJSONObject(stockName);
                        double stockPrice = stock.getDouble("price");
                        stockDataList.add(" " + stockName + ": " + stockPrice + " USD");
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return stockDataList;
        }

        public String loadFromWeb(String urlString) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(connection.getInputStream());
                String htmlText = FetchData.fromStream(in);
                return htmlText;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

