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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;

public class Provisioning extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    ListView devicesList;
    ArrayAdapter<String> adapter;
    ArrayList<String> myList;
    String callbackString;
    SharedPreferences dataBase;
    Switch scan;

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (!(result.getDevice().getName() == null)) {
                Log.d("progress", "Scanned and found: " + result.getDevice().getName() + " @ " + result.getDevice().getAddress());
                for (int i = 0; i < myList.size() + 1; i++) {
                    if(i == myList.size()){
                        Log.d("progress", "Found new Device called: " + result.getDevice().getAddress());
                        addItemToList(result.getDevice().getName() + " @ " + result.getDevice().getAddress());
                        break;
                    }
                    callbackString = result.getDevice().getName() + " @ " + result.getDevice().getAddress();
                    if (callbackString.equals(myList.get(i))) {
                        Log.d("progress", "I saw you before Mr. " + result.getDevice().getAddress());
                        break;
                    }
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provisioning);



        dataBase = getSharedPreferences("MeshData", Context.MODE_PRIVATE);

        devicesList = findViewById(android.R.id.list);
        myList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myList);
        devicesList.setAdapter(adapter);
        // BLE Stuff

        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);


        if (btManager.getAdapter() != null && !btManager.getAdapter().isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();
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
        scan = (Switch) findViewById(R.id.scan);
        //boolean scanState = scan.isChecked();

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

        devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                Log.d("progress", "You have clicked the ID: " + id + " Position: " + position);
                goToRegistration(position);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        scan.setChecked(false);
        finish();
    }

    public void goToRegistration(int pos){
        scan.setChecked(false);
        Intent intent = new Intent(this,Registration.class);
        String s = myList.get(pos);
        String[] str = s.split("@ ");
        intent.putExtra("NODE_ADDRESS", str[1]);
        startActivity(intent);
    }



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
        StartScanTask t = new StartScanTask();
        t.execute();
    }


    public void stopScanning() {
        Log.d("progress", "Stopped Scanning");
        StopScanTask t = new StopScanTask();
        t.execute();
    }

    public void addItemToList(String o) {
        Log.d("progress", "adding: " + o);
        myList.add(o);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, myList);

        devicesList.setAdapter(adapter);
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
}