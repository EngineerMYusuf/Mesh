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
import android.widget.TextView;

import java.io.IOException;

public class motorNode extends AppCompatActivity {

    HttpAdapter h;
    SharedPreferences dataBase;
    String getData = "";
    String myData = "";
    String p;
    TextView info;
    RefreshData r;

    public static String intToEightBit(int number) {
        String binaryString = "";

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
        setContentView(R.layout.activity_motor_node);
        h = new HttpAdapter();

        dataBase = getSharedPreferences("MeshData", Context.MODE_PRIVATE);
        int num = 0;
        String myData;
        num = getIntent().getIntExtra("WHICH_NODE", num);
        getData = "00000001" + intToEightBit(num) + "00000000000000000000000000000000000000000000000000000000";
        GetHTTP g = new GetHTTP();
        p = String.valueOf(num);
        myData = dataBase.getString(p,"");
        if(myData.equals("")){
            g.execute();
        }
        TextView name = (TextView) findViewById(R.id.motornodeID);
        info = (TextView) findViewById(R.id.motornodeData);
        Log.d("progress", "You want node: " + num);

        // Test Button
        Button send = findViewById(R.id.motorSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Send Clicked");
                SendHTTP s = new SendHTTP();
                s.execute("");                                                                      // ToDo add what to send
            }
        });
        r = new RefreshData();
        r.run();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        r.interrupt();
        finish();
    }

    public class RefreshData extends Thread {
        public void run() {
            while (true) {
                GetHTTP g = new GetHTTP();
                g.execute();
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myData = dataBase.getString(p, "");
                info.setText(myData);
            }
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

    public class GetHTTP extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("progress", "In GetHTTP task");
            String response = "";
            try {
                response = h.getData();
                String[] packets = response.split(",");
                for (String rep : packets) {
                    if (rep.substring(0, 8).equals("00010000")) {
                        Log.d("Get HTTP", "Found a connection table message.");
                        dataBase.edit().putString("CONN_TABLE", rep).apply();
                    } else if (rep.substring(0, 8).equals("00000000")) {
                        Log.d("Get HTTP", "Found an answer message.");
                        int koo = Integer.parseInt(rep.substring(8, 16), 2);
                        String p = String.valueOf(koo);
                        dataBase.edit().putString(p, rep.substring(16)).apply();
                    }
                }
                Log.d("progress", "Got: " + response);
            } catch (Exception e) {
            }
            this.publishProgress(response);
            return null;
        }

        protected void onProgressUpdate(String... values) {
        }
    }

}

