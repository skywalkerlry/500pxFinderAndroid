package com.ruoyan.myapplication.backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ruoyan on 2/6/15.
 */
public class ConnectionUtils {

    private static final String BASIC_URL = "https://api.500px.com/v1/photos?feature=popular";
    private static final String CONSUMER_KEY = "m7DfdVZZJA85VGRL6jF7O58LRFV03pKBjstmMfRY";

    public static String getFeed() {
        String path = BASIC_URL+"&consumer_key="+CONSUMER_KEY;
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(path);
            if (url != null) {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                int responseCode = httpURLConnection.getResponseCode();

                if (responseCode == 200)
                    inputStream = httpURLConnection.getInputStream();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultInString(inputStream);
    }

    private static String resultInString (InputStream inputStream) {
        String jsonString = "";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len = 0;
        byte[] data = new byte[1024];
        try {
            while ((len = inputStream.read(data)) != -1)
                outputStream.write(data,0,len);
            jsonString = new String(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonString;
    }
}
