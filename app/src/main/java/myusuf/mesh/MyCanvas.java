package myusuf.mesh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static android.support.v4.content.ContextCompat.startActivity;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class MyCanvas extends View {

    String data = "";
    int kn = 0;
    float touchX = 0;
    float touchY = 0;
    float[][] coordinates = new float[16][2];
    String[] connections = new String[kn];
    String[] types;
    SharedPreferences dataBase;
    int nodeTouched = 0;
    Class aClass;

    Paint paintLarge = null;
    Paint paintSmallStrong = null;
    Paint paintSmallWeak = null;
    Paint node = null;
    Bitmap darkblue = BitmapFactory.decodeResource(getResources(), R.drawable.darkblue);

    Bitmap green = BitmapFactory.decodeResource(getResources(), R.drawable.green);

    Bitmap lightblue = BitmapFactory.decodeResource(getResources(), R.drawable.lightblue);

    Bitmap lime = BitmapFactory.decodeResource(getResources(), R.drawable.lime);

    Bitmap magenta = BitmapFactory.decodeResource(getResources(), R.drawable.magenta);

    Bitmap orange = BitmapFactory.decodeResource(getResources(), R.drawable.orange);

    Bitmap purple = BitmapFactory.decodeResource(getResources(), R.drawable.purple);

    Bitmap red = BitmapFactory.decodeResource(getResources(), R.drawable.red);

    Bitmap yellow = BitmapFactory.decodeResource(getResources(), R.drawable.yellow);

    public MyCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintLarge = new Paint();
        paintSmallStrong = new Paint();
        paintSmallWeak = new Paint();
        node = new Paint();
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public float[][] getCoordinates() {
        int orgX = this.getWidth() / 2;
        int orgY = this.getHeight() / 2;
        float xVal = 0;
        float yVal = 0;
        double radi = 0;
        int bigRadius;
        bigRadius = 300;
        // Drawing circles
        for (int i = 0; i < kn; i++) {
            radi = toRadians(i * (360) / kn);
            xVal = (float) cos(radi);
            yVal = (float) sin(radi);
            coordinates[i][0] = orgX + xVal * bigRadius;
            coordinates[i][1] = orgY + yVal * bigRadius;
            Log.d("drawing", "Drew at " + coordinates[i][0] + " : " + coordinates[i][1]);
        }
        return coordinates;
    }

    public int getKn() {
        return kn;
    }

    public void setKn(int kn) {
        this.kn = kn;
        coordinates = new float[16][2];
    }

    public String[] getConnections() {
        return connections;
    }

    public void setConnections(String[] connections) {
        for (int i = 0; i < kn; i++) {
            Log.d("progress", "connection at " + i + ": " + connections[i]);
        }
        this.connections = connections;
    }

    public int getNodeTouched() {
        return this.nodeTouched;
    }

    public void setNodeTouched(int i) {
        this.nodeTouched = i;
    }

    public Class getClasses() {
        return this.aClass;
    }

    public void startNodes(int i) {
        this.nodeTouched = i;
        Log.d("touched", "Set node touched to: " + this.nodeTouched);
    }


/*
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
            touchY = event.getY();

            Log.d("touched", "Coordinates X: " + touchX + " Y: " + touchY);
            boolean isItIn;
            for (int i = 0; i < kn; i++) {
                isItIn = ((touchX < coordinates[i][0] + 44) && (coordinates[i][0] - 44 < touchX)) &&
                        ((touchY < coordinates[i][1] + 44) && (coordinates[i][1] - 44 < touchY));
                if (isItIn) {
                    Log.d("progress", "You touched: " + i + "th node");
                    startNodes(i);
                }
            }
        }
        return true;
    }
*/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d("draw", "in drawing");
        int orgX = this.getWidth() / 2;
        int orgY = this.getHeight() / 2;

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
            Log.d("drawing", "Drew at " + coordinates[i][0] + " : " + coordinates[i][1]);
        }

        // Drawing the lines
        if ((!(kn == 0) && !(kn > 9)) && connections.length > 0) {
            Log.d("Drawing", "I have " + kn + " nodes registered. Length of connection table is: " + connections.length);
            for (int i = 0; i < kn; i++) {
                Log.d("con", connections[i]);
                for (int j = 0; j < kn; j++) {
                    if (connections[i].charAt(j) == '1') {
                        canvas.drawLine(coordinates[i][0], coordinates[i][1], coordinates[j][0], coordinates[j][1], paintSmallStrong);
                    }
                }
            }
        }
        Log.d("draw Success?", "kn: " +kn );
        for (int i = 0; i < kn; i++) {
            Log.d("types", "Type  " + i + ": " + types[i]);
            switch (types[i]) {
                case "0":
                    canvas.drawBitmap(magenta, coordinates[i][0] - 50, coordinates[i][1] - 50, paintSmallStrong);
                    break;
                case "1":
                    canvas.drawBitmap(red, coordinates[i][0] - 50, coordinates[i][1] - 50, paintSmallStrong);
                    break;
                case "2":
                    canvas.drawBitmap(lightblue, coordinates[i][0] - 50, coordinates[i][1] - 50, paintSmallStrong);
                    break;
                case "3":
                    canvas.drawBitmap(green, coordinates[i][0] - 50, coordinates[i][1] - 50, paintSmallStrong);
                    break;
            }
        }
    }
}