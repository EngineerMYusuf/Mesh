package myusuf.mesh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class Registration extends AppCompatActivity {

    SharedPreferences dataBase;
    int myKn;
    String deviceName;
    String deviceType;
    String names;
    String types;
    String lowE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        String address = getIntent().getStringExtra("NODE_ADDRESS");
        Log.d("progress", address);

        dataBase = getSharedPreferences("MeshData", Context.MODE_PRIVATE);

        final EditText deviceNameInput = findViewById(R.id.deviceNameInput);
        final EditText deviceTypeInput = findViewById(R.id.deviceTypeInput);
        final CheckBox lowEnergy = findViewById(R.id.lowEnergy);

        Button add = (Button) findViewById(R.id.addDeviceButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myKn = dataBase.getInt("kn", 0);
                myKn++;
                dataBase.edit().putInt("kn", myKn).apply();
                Log.d("process", "kn is: " + dataBase.getInt("kn", 0));

                deviceName = deviceNameInput.getText().toString();
                Log.d("process", "Name is: " + deviceName);

                deviceType = deviceTypeInput.getText().toString().trim();
                Log.d("process", "Name is: " + deviceType);

                names = dataBase.getString("names", "");
                names = names + deviceName + ",";
                dataBase.edit().putString("names",names).apply();

                types = dataBase.getString("types", "");
                types = types + deviceType + ",";
                dataBase.edit().putString("types",types).apply();

                lowE = dataBase.getString("lowEnergy","");
                if(lowEnergy.isChecked()){
                    lowE = lowE + "1,";
                }
                else{
                    lowE = lowE + "0,";
                }
                dataBase.edit().putString("lowEnergy",lowE).apply();

            }
        });

    }

    public void launchActivity() {
        Intent intent = new Intent(this, MainPage.class);
        startActivity(intent);
    }
}
