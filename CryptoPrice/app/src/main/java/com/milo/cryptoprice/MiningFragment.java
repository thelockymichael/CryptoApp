package com.milo.cryptoprice;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;

public class MiningFragment extends Fragment {

    // EditTexts
    private EditText hashingPowerEditText, costPerKwhEditText,
            powerConsumptionEdiText, poolFeeEditText;

    private GridLayout miningProfitabilityGridLayout;
    private Spinner hashesPerSecondDropDownMenu;

    private Double bitCoinDifficulty, bitCoinBlockReward;

    // TextViews
    private TextView minedTodayTextView, minedThisWeekTextView, minedThisMonthTextView, minedThisYearTextView;
    private TextView profitPerDayTextView, profitPerWeekTextView, profitPerMonthTextView, profitPerYearTextView;
    private TextView powerCostDayTextView, powerCostWeekTextView, powerCostMonthTextView, powerCostYearTextView;

    private Double bitCoinProfit;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_mining_right, null);

/*
        H = Hashrate (hashes / second)
        D = Difficulty (Reference for values below)
        B = Reward per Block (Reference for value below)
        N = Number of days per month (default = 30)
        S = Number of seconds per day (S = 60 * 60 * 24 = 86400)
 */

        // Step 1 set text to current bitcoin mined
/*
        Double btcCoinProfitFormula = N * B * H * 86400 / D * 2^32
*/
        // Well done on your job!

        // Setting up power cost TextViews
        powerCostDayTextView = root.findViewById(R.id.powerCostDayTextView);
        powerCostWeekTextView = root.findViewById(R.id.powerCostWeekTextView);
        powerCostMonthTextView = root.findViewById(R.id.powerCostMonthTextView);
        powerCostYearTextView = root.findViewById(R.id.powerCostYearTextView);

        // Setting up profit TextViews
        profitPerDayTextView = root.findViewById(R.id.profitPerDayTextView);
        profitPerWeekTextView = root.findViewById(R.id.profitPerWeekTextView);
        profitPerMonthTextView = root.findViewById(R.id.profitPerMonthTextView);
        profitPerYearTextView = root.findViewById(R.id.profitPerYearTextView);

        minedTodayTextView = root.findViewById(R.id.minedTodayTextView);
        minedThisWeekTextView = root.findViewById(R.id.minedThisWeekTextView);
        minedThisMonthTextView = root.findViewById(R.id.minedThisMonthTextView);
        minedThisYearTextView = root.findViewById(R.id.minedThisYearTextView);


        hashingPowerEditText = root.findViewById(R.id.hashingPowerEditText);
        costPerKwhEditText = root.findViewById(R.id.costPowerKwhEditText);
        powerConsumptionEdiText = root.findViewById(R.id.powerConsumptionEditText);
        poolFeeEditText = root.findViewById(R.id.poolFeeEditText);
        hashesPerSecondDropDownMenu = root.findViewById(R.id.hashesPerSecondDropDownMenu);

        ArrayList<String> keys = new ArrayList(EnumSet.allOf(HashesPerSecond.class));

        ArrayAdapter<String> adapter = new ArrayAdapter(root.getContext(), android.R.layout.simple_list_item_1, keys);

        hashesPerSecondDropDownMenu.setAdapter(adapter);
        hashesPerSecondDropDownMenu.setSelection(0);

        bitCoinProfit = 0.0;

        hashesPerSecondDropDownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateProfit(bitCoinProfit, (HashesPerSecond) parent.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        hashingPowerEditText.setText("");

        hashingPowerEditText.addTextChangedListener(new
                                                            TextWatcher() {
                                                                @Override
                                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                                }

                                                                @Override
                                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                                    try {
                                                                        calculateProfit(Double.parseDouble(s.toString()),
                                                                                (HashesPerSecond) hashesPerSecondDropDownMenu.getSelectedItem());

                                                                        Log.i("BTC", bitCoinBlockReward + ", " + bitCoinDifficulty);

                                                                        Log.i("SELECTED ITEM: ", "" + hashesPerSecondDropDownMenu.getSelectedItem());
                                                                        Log.i("BTC", String.valueOf(Math.pow(2, 32)));
                                                                    } catch (Exception e) {
                                                                    }

                                                                }

                                                                @Override
                                                                public void afterTextChanged(Editable s) {
                                                                }
                                                            });

        getBitcoinDifficulty();

        getBitcoinReward();

        return root;
    }

    private void calculateProfit(Double hashRate, HashesPerSecond hashesPerSecond) {


        switch (hashesPerSecond) {
            case Hashes:
                hashRate = hashRate * 1000;
                break;
            case Kilohashes:
                hashRate = hashRate * 1000000;
                break;
            case Gigahashes:
                hashRate = hashRate * 1000000000;
                break;
            case Terahashes:
                hashRate = hashRate * 1000000000000.0;
                break;
        }
        try {
            bitCoinProfit = (hashRate * bitCoinBlockReward * 86400) / (bitCoinDifficulty * Math.pow(2, 32));

            Log.i("PROFIT", "" + bitCoinProfit);

            // Inserting data into GridLayout's boxes

            // Amount of bitcoin mined
            minedTodayTextView.setText("Mined/day\n" + String.format("%.8f", bitCoinProfit));
            minedThisWeekTextView.setText("Mined/week\n" + String.format("%.8f", bitCoinProfit * 7));
            minedThisMonthTextView.setText("Mined/month\n" + String.format("%.8f", bitCoinProfit * 30.436875));
            minedThisYearTextView.setText("Mined/year\n" + String.format("%.8f", bitCoinProfit * 30.436875 * 12));

            // Calculate bitcoin profit

            Double poolFeeValue = Double.parseDouble(poolFeeEditText.getText().toString());
            profitPerDayTextView.setText("Profit per day\n€ " + String.format("%.2f", bitCoinProfit * PricesHomeFragment.cryptoCurrencyPrices.get("BTC")));
            profitPerWeekTextView.setText("Profit per week\n€ " + String.format("%.2f", 7 * bitCoinProfit * PricesHomeFragment.cryptoCurrencyPrices.get("BTC")));
            profitPerMonthTextView.setText("Profit per month\n€ " + String.format("%.2f", 30.436875 * bitCoinProfit * PricesHomeFragment.cryptoCurrencyPrices.get("BTC")));
            profitPerYearTextView.setText("Profit per year\n€ " + String.format("%.2f", 30.436875 * 12 * bitCoinProfit * PricesHomeFragment.cryptoCurrencyPrices.get("BTC")));

            // Calculate power cost



        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void getBitcoinDifficulty() {
        try {
            downloadBitCoinDifficulty task = new downloadBitCoinDifficulty();
            task.execute("https://bitminter.com/api/pool/round");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBitcoinReward() {
        try {
            downloadBlockReward task = new downloadBlockReward();
            task.execute("https://bitminter.com/api/pool/blocks");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class downloadBitCoinDifficulty extends AsyncTask<String, Void, String> {

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

                while (keys.hasNext()) {
                    String key = keys.next();
                    if (jsonObject.get(key) instanceof JSONObject) {

                        String bitCoinInfo = ((JSONObject) jsonObject.get(key)).getString("BTC");

                        JSONObject jsonObject2 = new JSONObject(bitCoinInfo);
                        bitCoinDifficulty = Double.parseDouble(jsonObject2.getString("difficulty"));

                        Log.i("result", bitCoinInfo);
                        Log.i("result", bitCoinDifficulty.toString());
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class downloadBlockReward extends AsyncTask<String, Void, String> {

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
        public void onPostExecute(String json) {
            super.onPostExecute(json);

            try {

                JSONArray jsonArray = new JSONArray(json);
                Double minValue = Double.parseDouble(jsonArray.getJSONObject(0).getString("income"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonPart = jsonArray.getJSONObject(i);

                    if (Double.parseDouble(jsonPart.getString("income"))
                            < minValue) {
                        minValue = Double.parseDouble(jsonArray.getJSONObject(i)
                                .getString("income"));
                    }
                }

                bitCoinBlockReward = minValue;

                Log.i("result", minValue.toString());

            } catch (
                    Exception e) {
                e.printStackTrace();
            }
        }
    }
}