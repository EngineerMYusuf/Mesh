package myusuf.mesh;

import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class humNode extends AppCompatActivity {

    HttpAdapter h;
    SharedPreferences dataBase;
    String getData = "";
    String myData = "";
    String p;
    TextView info;
    String response;
    EditText dataToSend;
    String bitDataToSend;
    String setData;
    int finalNum;

    public static String intToEightBit(int number) {
        String binaryString;

        if (number < 2) {
            binaryString = "0000000" + Integer.toBinaryString(number);
        } else if (number < 4) {
            binaryString = "000000" + Integer.toBinaryString(number);
        } else if (number < 8) {
            binaryString = "00000" + Integer.toBinaryString(number);
        } else if (number < 16) {
            binaryString = "0000" + Integer.toBinaryString(number);
        } else if (number < 32) {
            binaryString = "000" + Integer.toBinaryString(number);
        } else if (number < 64) {
            binaryString = "00" + Integer.toBinaryString(number);
        } else if (number < 128) {
            binaryString = "0" + Integer.toBinaryString(number);
        } else {
            binaryString = Integer.toBinaryString(number);
        }
        return binaryString;
    }

    public static String reverser(String s) {
        String reverse = "";
        for (int i = s.length() - 1; i >= 0; i--) {
            reverse = reverse + s.charAt(i);
        }
        return s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hum_node);
        h = new HttpAdapter();

        dataBase = getSharedPreferences("MeshData", Context.MODE_PRIVATE);
        h = new HttpAdapter();
        int num = 0;
        String myData;
        num = getIntent().getIntExtra("WHICH_NODE", num);
        getData = "00000001" + intToEightBit(num) + "00000000000000000000000000000000000000000000000000000000";

        p = String.valueOf(num);
        myData = dataBase.getString(p, "");

        TextView name = findViewById(R.id.humnodeID);
        info = findViewById(R.id.humnodeData);
        Log.d("progress", "We are in node: " + num + "'s page");

        dataToSend = findViewById(R.id.tempDataToSend);

        finalNum = num;
        // Test Button
        Button send = findViewById(R.id.humSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitDataToSend = intToEightBit(Integer.valueOf(dataToSend.getText().toString()));
                setData = "00000010" + intToEightBit(finalNum) + bitDataToSend + "000000000000000000000000000000000000000000000000";
                SendHTTP s = new SendHTTP();
                s.execute(setData);                                                                     // ToDo add what to send
            }
        });

        // Test Button
        // ToDo Put the AsyncTask getHTTP here to get data from it
        ImageButton receive = findViewById(R.id.humReceive);
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Recieve Clicked");
                SendHTTP s = new SendHTTP();
                s.execute(getData);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public class SendHTTP extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Log.d("progress", "In SendHTTP task");
                String data = strings[0];
                Log.d("progress", "Sending: " + data);
                response = h.sendData(data);
                if (response.substring(0, 8).equals("00010000")) {
                    Log.d("Get HTTP", "Found a connection table message: " + response);
                    dataBase.edit().putString("CONN_TABLE", response.substring(8)).apply();
                } else if (response.substring(0, 8).equals("00010001")) {
                    Log.d("Get HTTP", "Found an answer message.");
                    int koo = Integer.parseInt(response.substring(8, 16), 2);
                    String p = String.valueOf(koo);
                    dataBase.edit().putString(p, response.substring(16)).apply();
                    Log.d("HTTP response", "Saved at: " + p);
                } else {
                    Log.d("HTTP response", "I dont understand this: " + response);
                }
                info.setText(dataBase.getString(String.valueOf(finalNum),""));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

