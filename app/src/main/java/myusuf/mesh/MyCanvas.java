package myusuf.mesh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class MyCanvas extends View {
    String data = "";
    int kn = 0;
    float touchX = 0;
    float touchY = 0;
    float[][] coordinates = new float[2 * kn][2];
    String[][] connections = new String[kn][2];

    Paint paintLarge = null;
    Paint paintSmallStrong = null;
    Paint paintSmallWeak = null;
    Paint node = null;
    Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.transnode);

    public MyCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintLarge = new Paint();
        paintSmallStrong = new Paint();
        paintSmallWeak = new Paint();
        node = new Paint();
    }

    public float getTouchY() {
        return touchY;
    }

    public float getTouchX() {
        return touchX;
    }

    public float[][] getCoordinates() {
        return coordinates;
    }

    public int getKn() {
        return kn;
    }

    public void setKn(int kn) {
        //Log.d("progress", "Old kn: " + this.kn + " New kn: " + kn);
        this.kn = kn;
        coordinates = new float[2 * kn][2];
    }

    public String[][] getConnections() {
        return connections;
    }

    public void setConnections(String[][] connections) {
        for(int i = 0; i < kn; i++){
            //Log.d("progress", "connection at " + i + ": " + connections[i][0] + " and " + connections[i][1]);
        }
        this.connections = connections;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.d("progress", "Data in onDraw: " + data);
        int orgX = this.getWidth() / 2;
        int orgY = this.getHeight() / 2;
        //Log.d("progress", "orgX: " + orgX);
        //Log.d("progress", "orgY: " + orgY);
        int bigRadius;
        bigRadius = 300;
        paintLarge.setStyle(Paint.Style.STROKE);
        paintSmallStrong.setStyle(Paint.Style.FILL);
        paintSmallWeak.setStyle(Paint.Style.FILL);

        paintSmallWeak.setStrokeWidth(20);
        paintSmallStrong.setStrokeWidth(20);

        paintLarge.setColor(Color.WHITE);
        paintSmallStrong.setColor(Color.WHITE);
        paintSmallWeak.setColor(Color.WHITE);

        canvas.drawPaint(paintLarge);
        canvas.drawPaint(paintSmallStrong);
        canvas.drawPaint(paintSmallWeak);

        // Use Color.parseColor to define HTML colors
        paintLarge.setColor(Color.parseColor("#CD5C5C"));
        paintSmallStrong.setColor(Color.parseColor("#01FF08"));
        paintSmallWeak.setColor(Color.parseColor("#FF0801"));

        float xVal = 0;
        float yVal = 0;
        double radi = 0;
        //Log.d("progress", "Drawing");
        canvas.drawCircle(orgX, orgY, bigRadius, paintLarge);
        // Drawing circles
        for (int i = 0; i < kn; i++) {
            radi = toRadians(i * (360) / kn);
            xVal = (float) cos(radi);
            yVal = (float) sin(radi);
            coordinates[i][0] = orgX + xVal * bigRadius;
            coordinates[i][1] = orgY + yVal * bigRadius;
        }

        // Drawing the lines
        for (int i = 0; i < kn; i++) {
            for (int j = 0; j < kn; j++) {
                if (connections[i][0].charAt(j) == '1') {
                    if (connections[i][1].charAt(j) == '1'){
                        canvas.drawLine(coordinates[i][0],coordinates[i][1],coordinates[j][0],coordinates[j][1],paintSmallStrong);
                    }
                    else{
                        canvas.drawLine(coordinates[i][0],coordinates[i][1],coordinates[j][0],coordinates[j][1],paintSmallWeak);
                    }
                }
            }
        }
        for (int i = 0; i < kn; i++) {
            canvas.drawBitmap(img,coordinates[i][0] -50,coordinates[i][1] -50,paintSmallStrong);
            //Log.d("progress", "Drew: " + coordinates[i][0] + " and " + coordinates[i][1]);
        }





    }
}