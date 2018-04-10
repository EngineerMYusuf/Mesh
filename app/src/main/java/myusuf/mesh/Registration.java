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
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.IOException;

public class Registration extends AppCompatActivity {

    SharedPreferences dataBase;
    int myKn;
    String deviceName;
    String deviceType;
    String deviceConn;
    String names;
    String types;
    String lowE;
    int myLE;
    HttpAdapter h;
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        final String address = getIntent().getStringExtra("NODE_ADDRESS");
        Log.d("progress", address);

        h = new HttpAdapter();
        dataBase = getSharedPreferences("MeshData", Context.MODE_PRIVATE);

        final EditText deviceNameInput = findViewById(R.id.deviceNameInput);
        final EditText deviceTypeInput = findViewById(R.id.deviceTypeInput);
        final EditText deviceConnInput = findViewById(R.id.deviceConnInput);
        final CheckBox lowEnergy = findViewById(R.id.lowEnergy);

        // ToDo add connections to other nodes. Should I notify any nodes that you arrived?

        Button add = findViewById(R.id.addDeviceButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myKn = dataBase.getInt("kn", 1);
                myKn++;
                dataBase.edit().putInt("kn", myKn).apply();
                int id = Integer.parseInt(deviceNameInput.getText().toString());
                Log.d("process", "kn is: " + dataBase.getInt("kn", 0));

                deviceName = deviceNameInput.getText().toString();

                deviceType = deviceTypeInput.getText().toString().trim();

                deviceConn = deviceConnInput.getText().toString().trim();

                names = dataBase.getString("names", "");
                names = names + deviceName + ",";
                dataBase.edit().putString("names", names).apply();

                types = dataBase.getString("types", "0,");
                types = types + deviceType + ",";
                dataBase.edit().putString("types", types).apply();

                lowE = dataBase.getString("lowEnergy", "");
                if (lowEnergy.isChecked()) {
                    lowE = lowE + "1,";
                    myLE = 1;
                } else {
                    lowE = lowE + "0,";
                    myLE = 0;
                }

                dataBase.edit().putString("lowEnergy", lowE).apply();

                sendProvisioning(address, Integer.parseInt(deviceConn), id, myLE);

                finish();

            }
        });

    }

    public String strToBin(String s) {
        byte[] bytes = s.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            binary.append(' ');
        }
        return binary.toString().replace(" ","");
    }

    public static String getBinAddress(String s){
        String binary = "";
        String p;
        int k;
        for(int i = 0; i < s.length(); i++){
            p = "" + s.charAt(i);
            if(p.equals("A")){
                k = 10;
            }
            else if(p.equals("B")){
                k = 11;
            }
            else if(p.equals("C")){
                k = 12;
            }
            else if(p.equals("D")){
                k = 13;
            }
            else if(p.equals("E")){
                k = 14;
            }
            else if(p.equals("F")){
                k = 15;
            }
            else {
                k = Integer.parseInt(p);
            }
            binary = binary + intToFourBit(k);
        }
        return binary;
    }

    public static String intToFourBit(int number){
        String binaryString = "";

        if(number < 2){
            binaryString = "000" + Integer.toBinaryString(number);
        }
        else if(number < 4){
            binaryString = "00" + Integer.toBinaryString(number);
        }
        else if(number < 8){
            binaryString = "0" + Integer.toBinaryString(number);
        }
        else {
            binaryString = Integer.toBinaryString(number);
        }
        return binaryString;
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

    public void sendProvisioning(String address, int connTo, int id, int le) {
        String[] addressArr = address.split(":");

        String sum = addressArr[5] + addressArr[4] + addressArr[3] + addressArr[2] + addressArr[1] + addressArr[0];

        Log.d("provisioning", "Address: " + sum + "Connected to: " + connTo + "ID: " + id + "LE: " + le);
        String binAddress = getBinAddress(sum);
        String binID = intToEightBit(id);
        String binLE;
        if(le == 0){
            binLE = "00000000";
        }
        else{
            binLE = "11111111";
        }
        Log.d("sending packet", "Sending to Bluetooth: " + binAddress + binID + binLE);
        String mp = "00000000" + binAddress + binID + binLE;
        SendHTTP s = new SendHTTP();
        s.execute(mp);
        Log.d("xd" , "Length: " + mp.length());


    }

    public void launchActivity() {
        Intent intent = new Intent(this, MainPage.class);
        startActivity(intent);
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
/* Just in case Simple SendHTTP
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
*/
}
