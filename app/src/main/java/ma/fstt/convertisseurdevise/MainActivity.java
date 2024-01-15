package ma.fstt.convertisseurdevise;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    TextView convertFromDropdownTextView, convertToDropdownTextView, conversionText;
    EditText amountToConvert;
    ArrayList<String> arrayList;
    Dialog fromDialog, toDialog;
    Button conversionButton;
    String convertFromValue, convertToValue, conversionValue;
    String apiKey= "79f79346266d0bcaa9581d08";
    Retrofit retrofit ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        convertFromDropdownTextView = findViewById(R.id.convert_from_dropdown_menu);
        convertToDropdownTextView = findViewById(R.id.convert_to_dropdown_menu);
        conversionButton = findViewById(R.id.conversionButton);
        conversionText = findViewById(R.id.conversionText);
        amountToConvert = findViewById(R.id.amountToConvertValueEdit);

        retrofit  = new Retrofit.Builder()
                .baseUrl("https://v6.exchangerate-api.com/v6/2439844c56f3bc1c66a6e5f7/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        fetchCurrencyCodes(); //RESULTAT = REMPLIR ARRAY LIST AVEC LES DEVISES


        convertFromDropdownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDialog = new Dialog(MainActivity.this);
                fromDialog.setContentView(R.layout.from);
                fromDialog.getWindow().setLayout(650, 800);
                fromDialog.show();

                EditText editText = fromDialog.findViewById(R.id.edit_text);
                ListView listView = fromDialog.findViewById(R.id.list_view);


                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);


                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                          convertFromDropdownTextView.setText(adapter.getItem(i));
                          fromDialog.dismiss();
                          convertFromValue = adapter.getItem(i);
                    }
                });
            }
        });

        convertToDropdownTextView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                toDialog = new Dialog(MainActivity.this);
                toDialog.setContentView(R.layout.to);
                toDialog.getWindow().setLayout(650,800);
                toDialog.show();


                EditText editText = toDialog.findViewById(R.id.edit_text);
                ListView listView = toDialog.findViewById(R.id.list_view);


                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        convertToDropdownTextView.setText(adapter.getItem(i));
                        fromDialog.dismiss();
                        convertToValue = adapter.getItem(i);
                    }
                });
            }
        });

        conversionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Double amountToConvert = Double.valueOf(MainActivity.this.amountToConvert.getText().toString());
//                Log.d("con:",convertFromValue);
//                Log.d("con1:",convertToValue);
//                Log.d("con2:", String.valueOf(amountToConvert));
                getConversionRate(convertFromValue,convertToValue,amountToConvert);
            }
        });
    }

    private String getConversionRate(String convertFromValue, String convertToValue, Double amountToConvert) {

            CurrencyConverterService service = retrofit.create(CurrencyConverterService.class);
            Call<ConversionResponse> call = service.convertCurrency(amountToConvert,convertFromValue, convertToValue);

            call.enqueue(new Callback<ConversionResponse>() {
                @Override
                public void onResponse(Call<ConversionResponse> call, Response<ConversionResponse> response) {
                    if (response.isSuccessful()) {
                        ConversionResponse conversionResponse = response.body();
                        if ("success".equals(conversionResponse.getResult())) {
//                            Log.d("suceess", String.valueOf(amountToConvert));
                            double conversionRate = conversionResponse.getConversionRate();
//                            Log.d("suceess", String.valueOf(conversionRate));
                            double convertedAmount = amountToConvert * conversionRate;

                            conversionText.setText(String.format("%.2f", convertedAmount));
                        } else {
                            Log.d("failure", String.valueOf(amountToConvert));
                        }
                    } else {
                        // API errors
                    }
                }

                @Override
                public void onFailure(Call<ConversionResponse> call, Throwable t) {
                    // network errors
                }
            });



        return conversionValue;
    }

    private void fetchCurrencyCodes() {

        CurrencyConverterService service = retrofit.create(CurrencyConverterService.class);
        Call<CurrencyResponse> call = service.getCurrencyCodes("2439844c56f3bc1c66a6e5f7");

        call.enqueue(new Callback<CurrencyResponse>() {//EXECUTER L'OBJET CALL
            @Override
            public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                if (response.isSuccessful()) {
                    CurrencyResponse currencyResponse = response.body();
                    if (currencyResponse != null) {
                        List<List<String>> supportedCodes = currencyResponse.getSupportedCodes();
                        // Initialize the ArrayList if not already done
                        if (arrayList == null) {
                            arrayList = new ArrayList<>();
                        }


                        if (supportedCodes != null) {
                            for (List<String> currencyInfo : supportedCodes) {
                                String currencyCode = currencyInfo.get(0);
                                arrayList.add(currencyCode);
                            }
                        } else {
                            Log.e("CurrencyResponse", "Supported codes are null");

                            // Display a message to the user
                            Toast.makeText(MainActivity.this, "Error fetching currency codes", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<CurrencyResponse> call, Throwable t) {

            }

        });

    }

}