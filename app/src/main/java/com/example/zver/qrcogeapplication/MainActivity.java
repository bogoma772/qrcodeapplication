package com.example.zver.qrcogeapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.crypto.SignatureValidator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Activity activity;
    OkHttpClient okHttpClient;
    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnScane = (Button) findViewById(R.id.btnScane);
        Button btnGenerate = (Button) findViewById(R.id.btnGenerate);
        btnScane.setOnClickListener(this);
        btnGenerate.setOnClickListener(this);
        activity = this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnScane: {
                //scane QR code
                IntentIntegrator intentIntegrator = new IntentIntegrator(activity);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setPrompt("Scan");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setBarcodeImageEnabled(false);
                intentIntegrator.initiateScan();

            }
            break;
            case R.id.btnGenerate: {
                Intent intent = new Intent(this, GenerateQRCode.class);
                startActivity(intent);
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //проверить token
                CheckQRCode(result.getContents().toString());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void CheckQRCode (final String token) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://taxiapp2017.16mb.com/qr/checkqrcode.php").newBuilder();
        urlBuilder.addQueryParameter("token", token);
        String url = urlBuilder.build().toString();
        okHttpClient = new OkHttpClient();
        request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            String responsedData;

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                responsedData = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(responsedData);
                            if (json == null) {
                                Toast.makeText(MainActivity.this, "JSON не парсируется", Toast.LENGTH_SHORT).show();
                            } else {
                                String success = json.getString("success");
                                if (success == "404") {

                                } else {
                                    if (success == "400") {

                                    } else {
                                        Toast.makeText(MainActivity.this, success, Toast.LENGTH_SHORT).show();
                                        try {
                                            String key = "gelicon";
                                            Jws jwtClaims =
                                                    Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(success);
                                            String subject = jwtClaims.getBody().toString();
                                            GetTokenData(subject, token);
                                        } catch (SignatureException e) {
                                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    
    private void GetTokenData(String response, String token){
        try{
            JSONObject answer = new JSONObject(response);
            if (answer != null){
                String key = answer.getString("sk");
                byte[] key2 = Base64.decode(key, Base64.DEFAULT);
                Jws jwtClaims =
                        Jwts.parser().setSigningKey(key2).parseClaimsJws(token);
                String subject = jwtClaims.getBody().toString();
                Toast.makeText(this, "+", Toast.LENGTH_SHORT).show();
                ParseData(subject);
            }else{
                Toast.makeText(this, "Пустой ответ от сервера", Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e){
            
        }
    }
    
    private void ParseData(String data){
        try{
            JSONObject jsonData = new JSONObject(data);
            if (jsonData != null){
                String url = jsonData.getString("url");
                Boolean flag = jsonData.getBoolean("flag");
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("flag", flag);
                startActivity(intent);
            }else {
                Toast.makeText(this, "Пустой ответ от сервера", Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
