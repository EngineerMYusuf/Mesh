package myusuf.mesh;

import android.annotation.SuppressLint;
import android.content.Context;
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
import static java.lang.Math.*;

public class Topology extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        String s = getIntent().getStringExtra("CONNECTION_TABLE");
        Log.d("progress", "In Topology");
        setContentView(R.layout.activity_topology);
        super.onCreate(savedInstanceState);
        MyCanvas paletV = findViewById(R.id.pale);
        Log.d("progress", "Previous kn: " + paletV.getKn());
        paletV.setKn(3);
        paletV.setData(s);

    }

/*
    public boolean onTouchEvent(MotionEvent event) {
        // MotionEvent object holds X-Y values
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            MyCanvas palet = findViewById(R.id.palet);
            int h = palet.getHeight();
            int w = palet.getWidth();
            Log.d("progress", "Height : " + String.valueOf(h) + " x " + String.valueOf(w));
            Log.d("progress", "Touch coordinates : " + String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
            //if(event.getX()< ){

            //}
        }

        return super.onTouchEvent(event);
    }
*/

}
