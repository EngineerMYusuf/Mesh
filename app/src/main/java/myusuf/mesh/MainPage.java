package myusuf.mesh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainPage extends AppCompatActivity {
    SharedPreferences dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        dataBase = getSharedPreferences("MeshData", Context.MODE_PRIVATE);


        // Provisioning Button
        Button provisioning = (Button) findViewById(R.id.goToProvisioning);
        provisioning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Going to Provisioning");
                Class c = Provisioning.class;
                launchActivity(c);
            }
        });

        // Topology Button
        final Button topology = (Button) findViewById(R.id.goToTopology);
        topology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("progress", "Going to Topology");
                Class c = Topology.class;
                launchActivity(c);
            }
        });
    }

    public void launchActivity(Class c){
        Intent intent = new Intent(this, c);
        if(c.equals(Topology.class)){

        }
        startActivity(intent);
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
}
