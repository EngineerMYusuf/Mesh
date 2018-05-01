package myusuf.mesh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;

public class MainPage extends AppCompatActivity {
    SharedPreferences dataBase;
    HttpAdapter h;
    String maint = "000000100000000011111111111111111111111111111111111111111111111111111111";
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        h = new HttpAdapter();

        dataBase = getSharedPreferences("MeshData", Context.MODE_PRIVATE);

        // Provisioning Button
        ImageButton provisioning = findViewById(R.id.goToProvisioning);
        provisioning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Going to Provisioning");
                Class c = Provisioning.class;
                launchActivity(c);
            }
        });

        // Topology Button
        final ImageButton topology = findViewById(R.id.goToTopology);
        topology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Going to Topology");
                Class c = Topology.class;
                launchActivity(c);
            }
        });

        final EditText id1 = findViewById(R.id.id1);
        final EditText id2 = findViewById(R.id.id2);

        final Button publish = findViewById(R.id.publish);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int first = Integer.parseInt(id1.getText().toString());
                int second = Integer.parseInt(id2.getText().toString());
                String p1 = intToEightBit(first);
                String p2 = intToEightBit(second);
                SendHTTP s = new SendHTTP();
                s.execute("00000010" + p1 + p2 + "000000000000000000000000000000000000000000000000");
            }
        });

        final Button bind = findViewById(R.id.bind);
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int first = Integer.parseInt(id1.getText().toString());
                int second = Integer.parseInt(id2.getText().toString());
                String p1 = intToEightBit(first);
                String p2 = intToEightBit(second);
                SendHTTP s = new SendHTTP();
                s.execute("00000011" + p1 + p2 + "000000000000000000000000000000000000000000000000");
            }
        });


        final ImageButton maintenance = findViewById(R.id.doMaintenance);
        maintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Doing Maintenance");
                SendHTTP s = new SendHTTP();
                s.execute(maint);
            }
        });

        ImageButton reset = findViewById(R.id.resetSystem);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBase.edit().clear().apply();
            }
        });
    }

    public void launchActivity(Class c){
        Intent intent = new Intent(this, c);
        startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Class c = help.class;
                launchActivity(c);
                return true;
        }
        return false;
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
