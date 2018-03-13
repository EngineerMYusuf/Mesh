package myusuf.mesh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import static java.lang.Math.*;

public class Topology extends AppCompatActivity {
    String[] nodeData;
    String myNodeData;
    int[] nodeType;
    int myNodeType;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        String s = getIntent().getStringExtra("CONNECTION_TABLE");
        int kn = 0;
        kn = getIntent().getIntExtra("KN",kn);
        nodeData = getIntent().getStringArrayExtra("NODE_DATA");
        nodeType = getIntent().getIntArrayExtra("NODE_TYPE");
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
        paletV = findViewById(R.id.pale);
        kn = paletV.getKn();
        float[][] coordinates = paletV.getCoordinates();

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
}
