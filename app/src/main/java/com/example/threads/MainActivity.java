package com.example.threads;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private Button ipBut;
    private TextView resultTextView;

    private Button imgBut;
    private ImageView imageApi;
    Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageApi = findViewById(R.id.imageApi);

        ipBut = findViewById(R.id.ipBut);
        resultTextView = findViewById(R.id.resultTextView);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        ipBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Realizar la solicitud HTTP en un hilo en segundo plano
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String apiUrl = "https://api.myip.com";
                            String imageUrl = "https://randomfox.ca/images/122.jpg";
                            Bitmap downloadedBitmap = downloadImage(imageUrl);

                            String response = getDataFromUrl(apiUrl);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    resultTextView.setText(response);
                                    imageApi.setImageBitmap(bitmap);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    String error = ""; // string field
    private String getDataFromUrl(String demoIdUrl) {

        String result = null;
        int resCode;
        InputStream in;
        try {
            URL url = new URL(demoIdUrl);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            resCode = httpsConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpsConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();
                result = sb.toString();
            } else {
                error += resCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Bitmap downloadImage(String imageUrl) {

        try {
            InputStream in = new java.net.URL(imageUrl).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bitmap;
    }


}
