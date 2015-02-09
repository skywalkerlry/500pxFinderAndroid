package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import bean.Api500px;

/**
 * Created by ruoyan on 2/6/15.
 */
public class ConnectionUtils {

    public static String getFeed() {
        String path = Api500px.getBasicUrl()+"&consumer_key="+Api500px.getConsumerKey();
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
                System.out.println("code is: "+responseCode);
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
