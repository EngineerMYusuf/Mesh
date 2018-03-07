package myusuf.mesh;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Provisioning extends AppCompatActivity {

    private void sendData(String data) throws MalformedURLException, ProtocolException, IOException {
        // My code
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
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()))) {
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

    private String getData() throws IOException {

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
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()))) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provisioning);
        // CheckScan t = new CheckScan();

        TextView data = (TextView) findViewById(R.id.data);


        // Test Button
        Button testing = (Button) findViewById(R.id.testing);
        testing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Send Clicked");
                SendHTTP t = new SendHTTP();
                Log.d("progress", "Executing: 00010001");
                t.execute("00010001");
            }
        });

        // Test Button
        Button receive = (Button) findViewById(R.id.receive);
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Recieve Clicked");
                GetHTTP t = new GetHTTP();
                t.execute();

            }
        });


        //t.start();

        // Scan Switch
        Switch scan = (Switch) findViewById(R.id.scan);
        boolean scanState = scan.isChecked();

        scan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {

                } else {

                }
            }
        });
    }

/*
    public class CheckScan extends Thread {
        @Override
        public void run() {
            Switch scan = (Switch) findViewById(R.id.scan);
            // ToDo set up scan.
            // ToDo open scan.
            if (scan.isChecked()) {
                // ToDo add device if new.
            }
        }
    }
*/
private class SendHTTP extends AsyncTask<String, Integer, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        try {
            Log.d("progress", "In SendHTTP task");
            String data = strings[0];
            Log.d("progress", "Sending: " + data);
            sendData(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

    private class GetHTTP extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("progress", "In GetHTTP task");
            String response = "";
            try {
                response = getData();
            } catch (Exception e) {
            }
            Log.d("progress", "Got: " + response);
            this.publishProgress(response);
            return null;
        }

        protected void onProgressUpdate(String... values) {
            TextView u = findViewById(R.id.data);
            u.setText(values[0]);
        }
    }
}