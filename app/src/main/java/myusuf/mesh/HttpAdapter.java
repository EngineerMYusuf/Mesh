package myusuf.mesh;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpAdapter {

    HttpAdapter() {

    }

    String sendData(String data) throws IOException {
        String url = "http://192.168.137.241/user.txt";
        String result= "";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.addRequestProperty("Content-Type", "text/plain" + "POST");
        con.setDoOutput(true);
        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Length", Integer.toString(data.length()));
        con.getOutputStream().write(data.getBytes("UTF8"));
        try {
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {

                StringBuffer response;
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
                result = response.toString();
            } else if (responseCode == 420) {
                Log.d("HTTP", "Empty Body Sent");
            } else {
                Log.d("HTTP", "HTTP Issue");
            }

        } catch (Exception e) {
            Log.d("HTTP", "Exception: " + e + " in sendHTTP");
        }
        return result;

    }

    public String getData() throws IOException {

        String result;
        String url = "http://192.168.137.5/network.txt";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        try {
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {

                StringBuffer response;
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
                result = response.toString();

                return result;
            } else {
                Log.d("HTTP", "HTTP Issue");
            }
        } catch (Exception ignored) {
        }

        return "";
    }


}
