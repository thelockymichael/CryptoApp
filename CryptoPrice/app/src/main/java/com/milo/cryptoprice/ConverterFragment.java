package com.milo.cryptoprice;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ConverterFragment extends Fragment {

    // Spinners
    private Spinner firstSpinnerView;
    private Spinner secondSpinnerView;

    // EditTexts
    private EditText firstEditText;
    private EditText secondEditText;

    // Currently selected cryptos
    private Double firstCryptoDouble;
    private Integer firstCurrentlySelectedCryptoCurrency = 0;

    private Double secondCryptoDouble;
    private Integer secondCurrentlySelectedCryptoCurrency = 3;

    // Swap ImageView Button
    private ImageView swapImageView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_converter_middle, null);

        Log.i("Main", PricesHomeFragment.cryptoCurrencyPrices.get("BCH").toString());


        firstSpinnerView = root.findViewById(R.id.firstSpinnerView);
        secondSpinnerView = root.findViewById(R.id.secondSpinnerView);

        firstEditText = root.findViewById(R.id.firstEditText);
        secondEditText = root.findViewById(R.id.secondEditText);

        swapImageView = root.findViewById(R.id.swapImageView);

        ArrayList<String> keys = new ArrayList(PricesHomeFragment.cryptoCurrencyPrices.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter(root.getContext(), android.R.layout.simple_list_item_1, keys);

        firstSpinnerView.setAdapter(adapter);

        secondSpinnerView.setAdapter(adapter);

        // Set first item in both dropdown menus
        firstSpinnerView.setSelection(firstCurrentlySelectedCryptoCurrency);
        secondSpinnerView.setSelection(secondCurrentlySelectedCryptoCurrency);

        firstSpinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("View", parent.getItemAtPosition(position).toString());
                firstCryptoDouble = PricesHomeFragment.cryptoCurrencyPrices.get(parent.getItemAtPosition(position));
                firstEditText.setHint(parent.getItemAtPosition(position).toString());
                firstCurrentlySelectedCryptoCurrency = position;

                Log.i("Item", "Position: " + position + "\n Id: " + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                firstCryptoDouble = PricesHomeFragment.cryptoCurrencyPrices.get(parent.getItemAtPosition(0));
                firstEditText.setHint(parent.getItemAtPosition(0).toString());
            }
        });

        secondSpinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("View", parent.getItemAtPosition(position).toString());

                secondCryptoDouble = PricesHomeFragment.cryptoCurrencyPrices.get(parent.getItemAtPosition(position));
                secondEditText.setHint(parent.getItemAtPosition(position).toString());
                secondCurrentlySelectedCryptoCurrency = position;

                Log.i("Item", "Position2: " + position + "\n Id2: " + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                firstCryptoDouble = PricesHomeFragment.cryptoCurrencyPrices.get(parent.getItemAtPosition(3));
                firstEditText.setHint(parent.getItemAtPosition(3).toString());
            }
        });

        firstEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("TEXT", s.toString());
                try {
                    if (!s.toString().trim().equals("")) {
                        Double result = Double.parseDouble(s.toString()) * firstCryptoDouble / secondCryptoDouble;
                        secondEditText.setText(result.toString());
                    } else {
                        secondEditText.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        swapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstSpinnerView.setSelection(secondCurrentlySelectedCryptoCurrency);
                secondSpinnerView.setSelection(firstCurrentlySelectedCryptoCurrency);

                String text1 = firstEditText.getText().toString();
                String text2 = secondEditText.getText().toString();

                firstEditText.setText(text2);
                secondEditText.setText(text1);


            }
        });

        return root;
    }
}