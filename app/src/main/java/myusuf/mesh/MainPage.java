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

import java.io.IOException;

public class MainPage extends AppCompatActivity {
    SharedPreferences dataBase;
    HttpAdapter h;
    String maint = "000000010000000011111111111111111111111111111111111111111111111111111111";

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
        Button provisioning = findViewById(R.id.goToProvisioning);
        provisioning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Going to Provisioning");
                Class c = Provisioning.class;
                launchActivity(c);
            }
        });

        // Topology Button
        final Button topology = findViewById(R.id.goToTopology);
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


        final Button maintenance = findViewById(R.id.doMaintenance);
        maintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Doing Maintenance");
                SendHTTP s = new SendHTTP();
                s.execute(maint);
            }
        });

        Button reset = findViewById(R.id.resetSystem);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBase.edit().clear().apply();
            }
        });
    }

    public void launchActivity(Class c){
        Intent intent = new Intent(this, c);
        if(c.equals(Topology.class)){

        }
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
                h.sendData(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
