package myusuf.mesh;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
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
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    TextView peripheralTextView;

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

        // HTTP Stuff

        TextView data = (TextView) findViewById(R.id.data);
        peripheralTextView = (TextView) findViewById(R.id.PeripheralTextView);
        peripheralTextView.setMovementMethod(new ScrollingMovementMethod());


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

        Button topology = (Button) findViewById(R.id.topology);
        topology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            launchActivity();
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


        // BLE Stuff

        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }

        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }

        // Scan Switch
        Switch scan = (Switch) findViewById(R.id.scan);
        boolean scanState = scan.isChecked();

        scan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    startScanning();
                } else {
                    stopScanning();
                }
            }
        });
    }
    private void launchActivity() {

        String data = "101100101001000";
        Log.d("progress", "Launching Topology");
        Intent intent = new Intent(this, Topology.class);
        intent.putExtra("CONNECTION_TABLE", data);
        startActivity(intent);
        Log.d("progress", "Skipped");
    }
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d("progress", "Scan Result");

            peripheralTextView.append("Device Name: " + result.getDevice().getName() + " rssi: " + result.getRssi() + "\n");
            Log.d("progress", "Scanned and found: " + result.getDevice().getName() + " @ " + result.getDevice().getAddress());
            // auto scroll for text view
            final int scrollAmount = peripheralTextView.getLayout().getLineTop(peripheralTextView.getLineCount()) - peripheralTextView.getHeight();
            // if there is no need to scroll, scrollAmount will be <=0
            if (scrollAmount > 0)
                peripheralTextView.scrollTo(0, scrollAmount);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("progress", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
    public void startScanning() {
        Log.d("progress", "Start Scanning");
        peripheralTextView.setText("");
        StartScanTask t = new StartScanTask();
        t.execute();
    }


    public void stopScanning() {
        Log.d("progress", "Stopped Scanning");
        peripheralTextView.append("Stopped Scanning");
        StopScanTask t = new StopScanTask();
        t.execute();
    }

    private class StartScanTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("progress", "Running startScan");
            btScanner.startScan(leScanCallback);
            return null;
        }
    }
    private class StopScanTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("progress", "Running stopScan");
            btScanner.stopScan(leScanCallback);
            return null;
        }
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
            TextView u = findViewById(R.id.data);
            u.setText(values[0]);
        }
    }
}