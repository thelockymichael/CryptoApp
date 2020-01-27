package com.milo.cryptoprice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class PricesHomeFragment extends Fragment {

    /**
     * The Crypto currency prices.
     */
    static HashMap<String, Double> cryptoCurrencyPrices = new HashMap<>();

    private TextView bitcoinPriceTextView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<TextView> cryptoPricesTextViews;

    private ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_prices_home, null);

        Log.i("Cryptocurrency", cryptoCurrencyPrices.toString());


        bitcoinPriceTextView = root.findViewById(R.id.bitcoinPriceEurTextView);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);

        scrollView = root.findViewById(R.id.scrollView);

        cryptoPricesTextViews = new ArrayList<>(Arrays.asList((TextView) root.findViewById(R.id.bitcoinPriceEurTextView),
                (TextView) root.findViewById(R.id.ethereumPriceEurTextView), (TextView) root.findViewById(R.id.ripplePriceEurTextView),
                (TextView) root.findViewById(R.id.bitcoinCashPriceEurTextView), (TextView) root.findViewById(R.id.eosPriceEurTextView),
                (TextView) root.findViewById(R.id.liteCoinPriceEurTextView), (TextView) root.findViewById(R.id.stellarPriceEurTextView)));

        getCryptoCurrencyPrices();

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {


                    @Override
                    public void onRefresh() {
                        Log.i("Refresh", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        getCryptoCurrencyPrices();
                    }
                }
        );


        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {


            @Override
            public void onScrollChanged() {

                int scrollY = scrollView.getScrollY();
                int scrollX = scrollView.getScrollX();

                if (scrollY == 0)
                    swipeRefreshLayout.setEnabled(true);
                else
                    swipeRefreshLayout.setEnabled(false);

                Log.i("COORDS: ", "X: " + scrollX + ", " + "Y: " + scrollY);
            }
        });
        return root;
    }


    public void getCryptoCurrencyPrices() {
        try {

            DownloadTask task = new DownloadTask();

            task.execute("https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH,XRP,BCH,EOS,LTC,XLM&tsyms=EUR");

        } catch (Exception e) {
            e.printStackTrace();


        }
    }


    /**
     * The type Download task.
     */

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                Log.i("result", result);

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);

            try {

                JSONObject jsonObject = new JSONObject(json.trim());

                Iterator<String> keys = jsonObject.keys();

                int index = 0;
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (jsonObject.get(key) instanceof JSONObject) {
                        Double bitcoinEur = Double.parseDouble((((JSONObject) jsonObject.get(key)).getString("EUR")));
                        String emptySeperator = String.format("%,.2f", bitcoinEur);
                        cryptoPricesTextViews.get(index).setText("€ " + emptySeperator);

                        cryptoCurrencyPrices.put(key, bitcoinEur);

                        index++;
                    }
                }

                swipeRefreshLayout.setRefreshing(false);

            } catch (Exception e) {
                e.printStackTrace();

                for (int i = 0; i < cryptoPricesTextViews.size(); i++) {
                    cryptoPricesTextViews.get(i).setText("—");
                }

                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Error")
                        .setMessage("Could not fetch price information. Are you connected to the internet?")
                        .setPositiveButton("Try again", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getCryptoCurrencyPrices();
                            }
                        })
                        .setNegativeButton("OK", null)
                        .show();

            }
        }
    }
}
