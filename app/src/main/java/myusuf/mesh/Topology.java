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
    HttpAdapter h;
    int kn;
    SharedPreferences dataBase;
    String[] types;
    String[] conn;
    MyCanvas paletV;
    String oldConnTable = "";
    String response;
    float[][] coordinates;
    boolean dataCame = false;

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

        // Test Button
        ImageButton update = findViewById(R.id.updateTopology);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pk.quit();
                Log.d("button", "You pressed update Topology");
                dataCame = false;
                SendHTTP s = new SendHTTP();
                s.execute(requestConnTable);
                oldConnTable = dataBase.getString("CONN_TABLE", oldConnTable);
                drawConnections(dataBase.getString("CONN_TABLE", oldConnTable));
                while (dataCame) {
                    try {
                        wait(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                dataCame = false;
                //drawConnections("00001000" + "00101001" + "01010010" + "00100110" + "11000000" + "00010001" + "00110000" + "01000100");
                //paletV.invalidate();
            }
        });

        paletV.setKn(kn);
        oldConnTable = dataBase.getString("CONN_TABLE", oldConnTable);
        drawConnections(dataBase.getString("CONN_TABLE", oldConnTable));
        paletV.invalidate();

    }

    public void drawConnections(String conntable) {
        if (conntable.length() < 8) {
            return;
        }
        if (!(kn == 0)) {
            Log.d("kn Reporter","Kn is: " + kn);
            paletV.setKn(kn);
            conn = new String[kn];
            for (int i = 0; i < kn; i++) {                                                           // Will be later changed to i < kn
                conn[i] = conntable.substring(kn * i, 8 * i + 8);                                    //  Will be later changed to (kn * i * 2, kn * i * 2 + kn)
                conn[i] = reverser(conn[i]);
                Log.d("Connections", "Connections: " + conn[i]);
            }
            paletV.setConnections(conn);
        }
    }

    public void startNodesTop(int node, Class c) {
       // pk.quit();
        paletV.setNodeTouched(0);
        Intent intent = new Intent(this, c);
        Log.d("startNodesTopology", c + " ," + node);
        intent.putExtra("WHICH_NODE", node);
        startActivity(intent);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        MyCanvas paletV = findViewById(R.id.pale);
        float touchX;
        float touchY;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int kn = paletV.getKn();
            coordinates = paletV.getCoordinates();
            touchX = event.getX();
            touchY = event.getY();

            Log.d("touched", "Coordinates X: " + touchX + " Y: " + touchY);
            boolean isItIn;
            for (int i = 0; i < kn; i++) {
                Log.d("The Node is in", String.valueOf(coordinates[i][0]) + ",,," + String.valueOf(coordinates[i][1]));
                isItIn = ((touchX < coordinates[i][0] + 44) && (coordinates[i][0] - 44 < touchX)) &&
                        ((touchY < coordinates[i][1] + 44) && (coordinates[i][1] - 44 < touchY));
                if (isItIn) {
                    Log.d("touched", "Touched");
                    Log.d("types", dataBase.getString("types", ""));
                    String[] type = dataBase.getString("types", "").split(",");
                    String hn = type[i];
                    Log.d("Topology", "The type you want is: " + hn);
                    if (hn.equals("1")) {
                        startNodesTop(i, tempNode.class);
                    } else if (hn.equals("2")) {
                        startNodesTop(i, humNode.class);
                    } else if (hn.equals("3")) {
                        startNodesTop(i, motorNode.class);
                    }
                }
            }
        }
        return true;
    }

    public class SendHTTP extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onPostExecute(Void result) {
            oldConnTable = dataBase.getString("CONN_TABLE", oldConnTable);
            drawConnections(dataBase.getString("CONN_TABLE", oldConnTable));
            paletV.invalidate();
        }
        @Override
        protected Void doInBackground(String... strings) {
            try {
                dataCame = false;
                Log.d("progress", "In SendHTTP task");
                String data = strings[0];
                Log.d("progress", "Sending: " + data);
                response = h.sendData(data);
                Log.d("HTTP","Response: " + response);
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
                dataCame = true;


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}
