package com.example.zver.qrcogeapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.interfaces.RSAPublicKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.security.Key;


public class GenerateQRCode extends AppCompatActivity {

    ImageView image;
    OkHttpClient okHttpClient;
    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);
        final EditText etUrl = (EditText) findViewById(R.id.etUrl);
        final CheckBox cbFlag = (CheckBox) findViewById(R.id.cbFlag);
        final EditText etSecretKey = (EditText) findViewById(R.id.etSecretKey);
        image = (ImageView) findViewById(R.id.image);
        Button btnGenerateQrCode = (Button) findViewById(R.id.btnGenerateQRCode);
        btnGenerateQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean flag = cbFlag.isChecked();
                String secretKey = etSecretKey.getText().toString();
                String url = etUrl.getText().toString();

                //generate token
                String token = Jwts.builder()
                        .claim("url", url)
                        .claim("flag", flag)
                        .signWith(SignatureAlgorithm.HS256, secretKey)
                        .compact();

                //generate qr code
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try{
                    BitMatrix bitMatrix = multiFormatWriter.encode(token, BarcodeFormat.QR_CODE, 600, 600);
                    BarcodeEncoder barcodeEncoer = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoer.createBitmap(bitMatrix);
                    image.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                //add token in db
                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://taxiapp2017.16mb.com/qr/addnewtoken.php").newBuilder();
                urlBuilder.addQueryParameter("secretkey", secretKey);
                urlBuilder.addQueryParameter("token", token);
                String urladress = urlBuilder.build().toString();
                okHttpClient = new OkHttpClient();
                request = new Request.Builder().url(urladress).build();
                //получение ответа от сервера
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    String responsedData;

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        responsedData = response.body().string();
                        GenerateQRCode.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    JSONObject json = new JSONObject(responsedData);
                                    if (json == null){
                                        Toast.makeText(GenerateQRCode.this, "Пустой ответ от сервера", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(GenerateQRCode.this, json.toString(), Toast.LENGTH_SHORT).show();
                                        String success = json.getString("success");
                                        if (success == "0") {
                                            Toast.makeText(GenerateQRCode.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (success == "2") {
                                                Toast.makeText(GenerateQRCode.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                try {
                                                    Toast.makeText(GenerateQRCode.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                                } catch (MissingClaimException e) {
                                                    Toast.makeText(GenerateQRCode.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                } catch (IncorrectClaimException e) {
                                                    Toast.makeText(GenerateQRCode.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                } catch (Exception e) {
                                                    Toast.makeText(GenerateQRCode.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(GenerateQRCode.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

    }
}
