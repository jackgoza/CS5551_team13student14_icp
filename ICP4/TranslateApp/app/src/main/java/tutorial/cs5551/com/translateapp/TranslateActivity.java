package tutorial.cs5551.com.translateapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TranslateActivity extends AppCompatActivity {
    String API_URL = "https://api.fullcontact.com/v2/person.json?";
    String API_KEY = "b29103a702edd6a";
    final String yandexKey = "trnsl.1.1.20151023T145251Z.bf1ca7097253ff7e.c0b0a88bea31ba51f72504cc0cc42cf891ed90d2";
    final String en = "en";
    final String es = "es";
    Map<String, String> languageToAbbreviation;
    FloatingActionButton closeButton;
    String sourceText;
    TextView outputTextView;
    Context mContext;
    Spinner sourceSpinner;
    Spinner destSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_translate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        outputTextView = (TextView) findViewById(R.id.txt_Result);
        this.closeButton = (FloatingActionButton) findViewById(R.id.fab);
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToLogin();
            }
        });

        initSpinners();
    }

    private void initSpinners() {
        languageToAbbreviation = new HashMap<>();
        languageToAbbreviation.put("English", "en");
        languageToAbbreviation.put("Spanish", "es");

        sourceSpinner = (Spinner) findViewById(R.id.sourceLanguageSpinner);
        destSpinner = (Spinner) findViewById(R.id.destLanguageSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.mContext, android.R.layout.simple_spinner_dropdown_item, languageToAbbreviation.keySet().toArray(new String[languageToAbbreviation.keySet().size()]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(adapter);
        destSpinner.setAdapter(adapter);
        sourceSpinner.setSelection(0);
        destSpinner.setSelection(0);

        sourceSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(16);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        destSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(16);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }

    public void populateLanguages() {
        final String langURL = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key="
                + yandexKey +
                "&ui=en";

        final OkHttpClient client = new OkHttpClient();
        try {
            final Request request = new Request.Builder().url(langURL).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println(e.getCause());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final JSONObject jsonResult;
                    final String result = response.body().string();
                    try {
                        jsonResult = new JSONObject(result);
                        System.out.println(jsonResult);
                    } catch (final JSONException je) {
                        System.out.println(je.getMessage());
                    }
                }
            });
        } catch (final Exception e) {

        }
    }

    public void translateText(View v) {
        TextView sourceTextView = (TextView) findViewById(R.id.txt_Email);

        sourceText = sourceTextView.getText().toString();
        final String source = languageToAbbreviation.get(sourceSpinner.getSelectedItem().toString());
        final String dest = languageToAbbreviation.get(destSpinner.getSelectedItem().toString());

        Toast.makeText(mContext, source + " :: " + dest, Toast.LENGTH_SHORT).show();

        String getURL = "https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                "key=" + yandexKey + "&text=" + sourceText + "&" +
                "lang=" + source + "-" + dest + "&[format=plain]&[options=1]&[callback=set]";//The API service URL
        final String response1 = "";
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(getURL)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final JSONObject jsonResult;
                    final String result = response.body().string();
                    try {
                        jsonResult = new JSONObject(result);
                        JSONArray convertedTextArray = jsonResult.getJSONArray("text");
                        final String convertedText = convertedTextArray.get(0).toString();
                        Log.d("okHttp", jsonResult.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                outputTextView.setText(convertedText);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (Exception ex) {
            outputTextView.setText(ex.getMessage());

        }

    }

    public void goBackToLogin() {
        //This code redirects the from login page to the home page.
        Intent redirect = new Intent(this, LoginActivity.class);
        startActivity(redirect);
    }
}
