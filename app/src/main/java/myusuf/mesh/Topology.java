package myusuf.mesh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;


public class Topology extends AppCompatActivity {
    String[] nodeData;
    String myNodeData;
    int[] nodeType;
    int myNodeType;
    HttpAdapter h;
    int kn;
    SharedPreferences dataBase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        h = new HttpAdapter();
        dataBase = getSharedPreferences("MeshData", Context.MODE_PRIVATE);

        // ToDo get these in this activity(This will be deleted)
        // Intent Extras
        String s = getIntent().getStringExtra("CONNECTION_TABLE");
        kn = getIntent().getIntExtra("KN",kn);
        nodeData = getIntent().getStringArrayExtra("NODE_DATA");
        nodeType = getIntent().getIntArrayExtra("NODE_TYPE");




        // Test Button
        Button update = (Button) findViewById(R.id.updateTopology);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToDo Send GET topology
            }
        });


        // ToDo process the topology to get connection table


        String[][] conn = new String[kn][2];
        for (int i = 0; i < kn; i++) {
            conn[i][0] = s.substring(kn * i * 2, kn * i * 2 + kn);
            conn[i][1] = s.substring(kn * i * 2 + kn, kn * i * 2 + 2 * kn);
        }

        //Log.d("progress", "In Topology");
        setContentView(R.layout.activity_topology);
        super.onCreate(savedInstanceState);
        MyCanvas paletV = findViewById(R.id.pale);
        //Log.d("progress", "Previous kn: " + paletV.getKn());
        paletV.setKn(kn);
        paletV.setConnections(conn);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        MyCanvas paletV = findViewById(R.id.pale);
        float touchX;
        float touchY;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int kn = paletV.getKn();
            float[][] coordinates = paletV.getCoordinates();
            touchX = event.getX();
            touchY = event.getY() - 150;
            for(int i = 0; i < kn; i++){
                //Log.d("progress", "Coordinates X: " + coordinates[i][0]  + " Y: " + coordinates[i][1]);
            }
            boolean isItIn;
            for (int i = 0; i < kn; i++) {
                isItIn = ((touchX < coordinates[i][0] + 44) && (coordinates[i][0] - 44 < touchX)) &&
                        ((touchY < coordinates[i][1] + 44) && (coordinates[i][1] - 44 < touchY));
                if (isItIn) {
                    //Log.d("progress", "You touched: " + i + "th node");
                    myNodeData = nodeData[i];
                    myNodeType = nodeType[i];
                    startNodes(i);
                    return true;
                }
            }
            //Log.d("progress", "Pressed X: " + touchX + " Y: " + touchY);

            return true;
        }
        return false;
    }

    private void startNodes(int node) {
        // ToDo consider which type of node is pressed and send to appropriate activity
        Intent intent = new Intent(this, tempNode.class);
        intent.putExtra("WHICH_NODE", node);
        intent.putExtra("NODE_DATA",myNodeData);
        intent.putExtra("NODE_TYPE",myNodeType);
        startActivity(intent);
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
                Log.d("progress", "Got: " + response);
            } catch (Exception e) {
            }
            this.publishProgress(response);
            return null;
        }

        protected void onProgressUpdate(String... values) {
            // ToDo You have the data now send to processing
        }
    }
}
