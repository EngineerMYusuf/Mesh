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
import android.widget.ImageButton;

import java.io.IOException;

import static java.lang.Thread.sleep;


public class Topology extends AppCompatActivity {
    final String requestConnTable = "000000010000000000000000000000000000000000000000000000000000000000000000";

    HttpAdapter h;
    int kn;
    SharedPreferences dataBase;
    String[] types;
    String[] conn;
    MyCanvas paletV;
    String oldConnTable = "";
    GetConnectionTable t;
    //SendToNode pk;

    public static String reverser(String s) {
        String reverse = "";
        for (int i = s.length() - 1; i >= 0; i--) {
            reverse = reverse + s.charAt(i);
        }
        return reverse;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //pk.quit();
        finish();
    }

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

        Button getConnTable = findViewById(R.id.getConnTable);
        getConnTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("button", "You pressed get Table");
                t = new GetConnectionTable();
                t.execute();
            }
        });

        // Test Button
        ImageButton update = findViewById(R.id.updateTopology);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("button", "You pressed update Topology");
                drawConnections(dataBase.getString("CONN_TABLE", oldConnTable));
                paletV.invalidate();
            }
        });
        //pk = new SendToNode();
       // pk.execute();

        paletV.setKn(kn);
        paletV.invalidate();

    }

    public void drawConnections(String conntable) {
        if (conntable.length() < 8) {
            return;
        }
        if (!(kn == 0) && !(kn > 8)) {
            paletV.setKn(kn);
            conn = new String[8];
            for (int i = 0; i < 8; i++) {                                                           // Will be later changed to i < kn
                conn[i] = conntable.substring(8 * i, 8 * i + 8);                                    //  Will be later changed to (kn * i * 2, kn * i * 2 + kn)
                conn[i] = reverser(conn[i]);
                Log.d("Connections", "Connections: " + conn[i]);
            }
            paletV.setConnections(conn);
            //paletV.invalidate();
        }
    }

    private void startNodesTop(int node, Class c) {
        //pk.quit();
        paletV.setNodeTouched(0);
        Intent intent = new Intent(this, c);
        Log.d("startNodesTopology", c + " ," + node);
        intent.putExtra("WHICH_NODE", node);
        startActivity(intent);
    }

    public void sendToDraw(String s) {
        if (!(s.equals(""))) {
            drawConnections(s);
        }
    }
/*
    public class SendToNode extends AsyncTask<Void, Void, Void> {
        private boolean done = false;

        public void quit() {
            done = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (!done) {
                Log.d("checking if touched", "checking");
                int k = paletV.getNodeTouched();
                if (!(k == 0)) {
                    Log.d("touched", "Touched");
                    Log.d("types", dataBase.getString("types", ""));
                    String[] type = dataBase.getString("types", "").split(",");
                    String hn = type[k];
                    Log.d("Topology", "The type you want is: " + hn);
                    if (hn.equals("1")) {
                        startNodesTop(k, tempNode.class);
                    } else if (hn.equals("2")) {
                        startNodesTop(k, humNode.class);
                    } else if (hn.equals("3")) {
                        startNodesTop(k, motorNode.class);
                    }
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }
*/
    public class GetConnectionTable extends AsyncTask<Void, Void, Void> {                                                // ToDo Convert to AsyncTask

        @Override
        protected Void doInBackground(Void... voids) {
            SendHTTP s = new SendHTTP();
            s.execute(requestConnTable);
            Log.d("HTTP Send", "sent request for connection table");
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GetHTTP g = new GetHTTP();
            g.execute();
            //if (dataBase.getString("CONN_TABLE", "").length() != kn) {
            //drawConnections(dataBase.getString("CONN_TABLE", oldConnTable));
            //}
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //paletV.invalidate();
            return null;
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
            Log.d("Topology", "In GetHTTP task");
            String response = "";
            try {
                response = h.getData();
                String[] packets = response.split(",");
                for (String rep : packets) {
                    if (rep.substring(0, 8).equals("00010000")) {
                        Log.d("Get HTTP", "Found a connection table message.");
                        dataBase.edit().putString("CONN_TABLE", rep.substring(8)).apply();
                    } else if (rep.substring(0, 8).equals("00010001")) {
                        Log.d("Get HTTP", "Found an answer message.");
                        int koo = Integer.parseInt(rep.substring(8, 16), 2);
                        String p = String.valueOf(koo);
                        dataBase.edit().putString(p, rep.substring(16)).apply();
                    }
                }
                Log.d("progress", "Got: " + response);
                sendToDraw(dataBase.getString("CONN_TABLE", ""));
                oldConnTable = dataBase.getString("CONN_TABLE","");
            } catch (Exception e) {
            }
            this.publishProgress(response);
            return null;
        }

        protected void onProgressUpdate(String... values) {
            sendToDraw(dataBase.getString("CONN_TABLE", ""));

        }
    }
}
