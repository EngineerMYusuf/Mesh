package myusuf.mesh;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class humNode extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hum_node);

        int num = 0;
        int myType = 0;
        String myData;
        num = getIntent().getIntExtra("WHICH_NODE",num);
        TextView name = (TextView) findViewById(R.id.humnodeID);
        TextView type = (TextView) findViewById(R.id.humnodeType);
        TextView info = (TextView) findViewById(R.id.humnodeData);
        Log.d("progress","You want node: " + num );
        name.setText(String.valueOf(num));
        type.setText(String.valueOf(myType));

        // Test Button
        Button send = (Button) findViewById(R.id.humSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Send Clicked");
                SendHTTP t = new SendHTTP();
                // ToDo Send the appropriate packet
                Log.d("progress", "Executing: 00010001");
                t.execute("00010001");
            }
        });

        // Test Button
        Button receive = (Button) findViewById(R.id.humReceive);
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Recieve Clicked");
                GetHTTP t = new GetHTTP();
                t.execute();
                // ToDo Process the packet to get the data and send to TextView
            }
        });
    }

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
            //TextView u = findViewById(R.id.data);
            //u.setText(values[0]);
        }
    }

    private void launchActivity() {

        String data = "00110011" + "00010001";   // This means 0th node is connected to 2,3,6,7 and 3,7 is strong
        data = data + "00001100" + "00000100";   // 1
        data = data + "10000000" + "00000000";   // 2
        data = data + "10000100" + "10000000";   // 3
        data = data + "01000000" + "00000000";   // 4
        data = data + "01010010" + "01000010";   // 5
        data = data + "10000000" + "00000100";   // 6
        data = data + "10000000" + "10000000";   // 7
        int kn = 8;
        Log.d("progress", "Launching Topology");
        Intent intent = new Intent(this, Topology.class);
        String[] datas = new String[kn];
        datas[0] = "It's soooo hot here";
        datas[1] = "ZZZzzzzzz.....";
        datas[2] = "Huh?!  Who's there?";
        datas[3] = "Dude dude dude. I just saw this lamp stare at me";
        datas[4] = "See, I can touch my nose with my tongue";
        datas[5] = "Maaaan. The government always controls the media maaaan.";
        datas[6] = "Dude it feels like we are just puppets.";
        datas[7] = "Darling you looook peeerfect tonight Na Na Naaaa.";
        int[] types = new int[kn];
        types[0] = 1;
        types[1] = 2;
        types[2] = 5;
        types[3] = 1;
        types[4] = 4;
        types[5] = 4;
        types[6] = 3;
        types[7] = 99;
        intent.putExtra("NODE_DATA",datas);
        intent.putExtra("NODE_TYPE",types);
        intent.putExtra("CONNECTION_TABLE", data);
        intent.putExtra("KN",kn);
        startActivity(intent);
        Log.d("progress", "Skipped");
    }

}

