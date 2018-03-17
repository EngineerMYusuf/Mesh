package myusuf.mesh;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpAdapter {

    public HttpAdapter() {

    }

    public void sendData(String data) throws MalformedURLException, ProtocolException, IOException {
        String url = "http://192.168.137.5/user.txt";

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
            } else {
                Log.d("progress", "HTTP Issue in sendData");
            }
        } catch (Exception e) {
            Log.d("progress", "Exception: " + e + " in sendHTTP");
        }

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
                System.out.println("HTTP issue");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return "";
    }

}
