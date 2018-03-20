package myusuf.mesh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.io.IOException;

import static java.lang.Thread.sleep;


public class Topology extends AppCompatActivity {
    final String requestConnTable = "000000010000000000000000000000000000000000000000000000000000000000000000";
    String nodeData;
    String myNodeData;
    String nodeType;
    int myNodeType;
    HttpAdapter h;
    int kn;
    SharedPreferences dataBase;
    String[] types;
    String[] conn;
    MyCanvas paletV;
    String oldConnTable = "";
    GetConnectionTable t;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        h = new HttpAdapter();
        dataBase = getSharedPreferences("MeshData", Context.MODE_PRIVATE);

        setContentView(R.layout.activity_topology);
        super.onCreate(savedInstanceState);
        paletV = findViewById(R.id.pale);

        oldConnTable = dataBase.getString("CONN_TABLE", "");

        kn = dataBase.getInt("kn", 1);
        String s = dataBase.getString("types", "0,");
        types = s.split(",");

        paletV.setTypes(types);

        // Test Button
        ImageButton update = findViewById(R.id.updateTopology);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("button", "You pressed update topology");
                t = new GetConnectionTable();
                t.run();
                paletV.invalidate();
            }
        });/*
        SendToNode pk = new SendToNode();
        pk.execute();
*/

        paletV.setKn(kn);
        paletV.invalidate();

    }


    public void drawConnections(String conntable) {
        if(conntable.length() < 8){
            return;
        }
        if (!(kn == 0) && !(kn > 8)) {
            paletV.setKn(kn);
            conn = new String[8];
            for (int i = 0; i < 8; i++) {                                                           // Will be later changed to i < kn

                conn[i] = conntable.substring(8 * i, 8 * i + 8);                                    //  Will be later changed to (kn * i * 2, kn * i * 2 + kn)
                conn[i] = reverser(conn[i]);
                Log.d("Connections", "Connections: " + conn[i] );
            }
            paletV.setConnections(conn);
            paletV.invalidate();
        }
    }
    public static String reverser(String s){
        String reverse = "";
        for(int i = s.length() - 1; i >= 0; i--)
        {
            reverse = reverse + s.charAt(i);
        }
        return reverse;
    }

/*
    public class SendToNode extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            while(true){
                Log.d("checking if touched","checking");
                if(!(paletV.getNodeTouched() == 0)){
                    Log.d("touched", "Touched");
                    Log.d("types", dataBase.getString("types",""));
                    switch (dataBase.getString("types","")){
                        case "1":
                            startNodes(paletV.getNodeTouched(),tempNode.class);
                        case "2":
                            startNodes(paletV.getNodeTouched(),humNode.class);
                        case "3":
                            startNodes(paletV.getNodeTouched(),motorNode.class);
                        case "":

                    }
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

*/
    private void startNodes(int node, Class c) {
        Intent intent = new Intent(this, c);
        intent.putExtra("WHICH_NODE", node);
        startActivity(intent);
    }
    public class GetConnectionTable extends Thread {
        public void run() {
            SendHTTP s = new SendHTTP();
            s.execute(requestConnTable);
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GetHTTP g = new GetHTTP();
            g.execute();
            Log.d("HTTP Send", "sent request for connection table");
            if (dataBase.getString("CONN_TABLE","").length() != kn) {
                drawConnections(dataBase.getString("CONN_TABLE", oldConnTable));
            }
        }
    }
    public void sendToDraw(String s){
        if(!(s.equals(""))){
            drawConnections(s);
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
                        dataBase.edit().putString("CONN_TABLE", rep.substring(8)).apply();
                    }
                    else if(rep.substring(0,8).equals("00010001")){
                        Log.d("Get HTTP", "Found an answer message.");
                        int koo = Integer.parseInt(rep.substring(8,16),2);
                        String p = String.valueOf(koo);
                        dataBase.edit().putString(p,rep.substring(16)).apply();
                    }
                }
                Log.d("progress", "Got: " + response);
                sendToDraw(dataBase.getString("CONN_TABLE",""));
            } catch (Exception e) {
            }
            this.publishProgress(response);
            return null;
        }

        protected void onProgressUpdate(String... values) {
        }
    }
}
