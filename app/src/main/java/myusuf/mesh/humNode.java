package myusuf.mesh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class humNode extends AppCompatActivity {
    SharedPreferences dataBase;

    HttpAdapter h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hum_node);
        h = new HttpAdapter();

        dataBase = getSharedPreferences("MeshData", Context.MODE_PRIVATE);

        int num = 0;
        int myType = 0;
        String myData;
        num = getIntent().getIntExtra("WHICH_NODE", num);
        TextView name = (TextView) findViewById(R.id.humnodeID);
        TextView type = (TextView) findViewById(R.id.humnodeType);
        TextView info = (TextView) findViewById(R.id.humnodeData);
        Log.d("progress", "You want node: " + num);
        name.setText(String.valueOf(num));
        type.setText(String.valueOf(myType));

        // Test Button
        Button send = (Button) findViewById(R.id.humSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Send Clicked");
                SendHTTP s = new SendHTTP();
                s.execute("00010001");
                // ToDo Send the appropriate packet
                Log.d("progress", "Executing: 00010001");
            }
        });

        // Test Button
        // ToDo Put the AsyncTask getHTTP here to get data from it
        Button receive = (Button) findViewById(R.id.humReceive);
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Recieve Clicked");
                GetHTTP g = new GetHTTP();
                g.execute();
            }
        });
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
        intent.putExtra("NODE_DATA", datas);
        intent.putExtra("NODE_TYPE", types);
        intent.putExtra("CONNECTION_TABLE", data);
        intent.putExtra("KN", kn);
        startActivity(intent);
        Log.d("progress", "Skipped");
    }

    public class SendHTTP extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Log.d("progress", "In SendHTTP task");
                String data = strings[0];
                Log.d("progress", "Sending: " + data);
                h.sendData(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class GetHTTP extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("progress", "In GetHTTP task");
            String response = "";
            try {
                response = h.getData();
                Log.d("progress", "Got: " + response);
            } catch (Exception e) {
            }
            this.publishProgress(response);
            return null;
        }

        protected void onProgressUpdate(String... values) {
            // ToDo You have the data now send to processing
        }
    }

}

