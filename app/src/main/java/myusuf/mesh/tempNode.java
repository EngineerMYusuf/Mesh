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
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;

import static java.lang.Thread.sleep;

public class tempNode extends AppCompatActivity {

    HttpAdapter h;
    SharedPreferences dataBase;
    String getData = "";
    String myData = "";
    String p;
    String bitDataToSend;
    TextView info;
    EditText dataToSend;
    String setData;
    int finalNum;
    //RefreshData r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_node);

        dataBase = getSharedPreferences("MeshData", Context.MODE_PRIVATE);
        h = new HttpAdapter();
        int num = 0;
        String myData;
        num = getIntent().getIntExtra("WHICH_NODE", num);
        getData = "00000001" + intToEightBit(num) + "00000000000000000000000000000000000000000000000000000000";

        p = String.valueOf(num);

        dataToSend = findViewById(R.id.tempDataToSend);

        TextView name = (TextView) findViewById(R.id.tempnodeID);
        info = (TextView) findViewById(R.id.tempnodeData);

        info.setText(dataBase.getString(p, "o"));
        Log.d("progress", "You want node: " + num);
        finalNum = num;

        // Test Button
        Button send = findViewById(R.id.tempSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Send Clicked");
                bitDataToSend = intToEightBit(Integer.valueOf(dataToSend.getText().toString()));
                setData = "00000010" + intToEightBit(finalNum) + bitDataToSend + "000000000000000000000000000000000000000000000000";
                SendHTTP s = new SendHTTP();
                s.execute(setData);
            }
        });

        // Test Button
        Button receive = findViewById(R.id.tempReceive);
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Recieve Clicked");
                GetHTTP  g = new GetHTTP();
                g.execute();
            }
        });
        setContentView(R.layout.activity_temp_node);
        /*
        r = new RefreshData();
        r.execute();*/
    }


    public void SendOnClick(View view){
        Log.d("progress", "Send Clicked");

        if(dataToSend.getText().toString().equals("")){
            return;
        }
        bitDataToSend = intToEightBit(Integer.valueOf(dataToSend.getText().toString()));
        setData = "00000010" + intToEightBit(finalNum) + bitDataToSend + "000000000000000000000000000000000000000000000000";
        SendHTTP s = new SendHTTP();
        s.execute(setData);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //r.cancel(true);
        finish();
    }


    public class RefreshData extends AsyncTask<Void, Void, Void>{

        private boolean done = false;

        public void quit(){
            done = true;
        }
        @Override
        protected Void doInBackground(Void... params) {
            while(!done){
                GetHTTP g = new GetHTTP();
                g.execute();
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myData = dataBase.getString(p,"");
                info.setText(myData);
            }
            return null;
        }
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

    public static String intToEightBit(int number){
        String binaryString = "";

        if(number < 2){
            binaryString = "0000000" + Integer.toBinaryString(number);
        }
        else if(number < 4){
            binaryString = "000000" + Integer.toBinaryString(number);
        }
        else if(number < 8){
            binaryString = "00000" + Integer.toBinaryString(number);
        }
        else if(number < 16){
            binaryString = "0000" + Integer.toBinaryString(number);
        }
        else if(number < 32){
            binaryString = "000" + Integer.toBinaryString(number);
        }
        else if(number < 64){
            binaryString = "00" + Integer.toBinaryString(number);
        }
        else if(number < 128){
            binaryString = "0" + Integer.toBinaryString(number);
        }
        else {
            binaryString = Integer.toBinaryString(number);
        }
        return binaryString;
    }
    public static String reverser(String s){
        String reverse = "";
        for(int i = s.length() - 1; i >= 0; i--)
        {
            reverse = reverse + s.charAt(i);
        }
        return s;
    }

    public class GetHTTP extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("tempNode", "In GetHTTP task");
            String response = "";
            try {
                response = h.getData();
                String[] packets = response.split(",");
                for (String rep : packets) {
                    if (rep.substring(0, 8).equals("00010000")) {
                        Log.d("Get HTTP", "Found a connection table message.");
                        dataBase.edit().putString("CONN_TABLE", rep).apply();
                    }
                    else if(rep.substring(0,8).equals("00010001")){
                        Log.d("Get HTTP", "Found an answer message.");
                        int koo = Integer.parseInt(rep.substring(8,16),2);
                        String p = String.valueOf(koo);
                        dataBase.edit().putString(p,rep.substring(16)).apply();
                    }
                }
                Log.d("progress", "Got: " + response);
            } catch (Exception e) {
            }
            this.publishProgress(response);
            return null;
        }
        protected void onProgressUpdate(String... values) {
            info.setText(dataBase.getString(p,""));
        }
    }

}

