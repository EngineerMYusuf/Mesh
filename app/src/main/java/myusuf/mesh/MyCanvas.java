package myusuf.mesh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class MyCanvas extends View {
    String data = "";
    int kn = 0;
    float touchX = 0;
    float touchY = 0;
    float[][] coordinates = new float[2 * kn][2];

    Paint paintLarge = null;
    Paint paintSmallStrong = null;
    Paint paintSmallWeak = null;

    public MyCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintLarge = new Paint();
        paintSmallStrong = new Paint();
        paintSmallWeak = new Paint();
    }

    public int getKn() {
        return kn;
    }

    public void setKn(int kn) {
        Log.d("progress", "Old kn: " + this.kn + " New kn: " + kn);
        this.kn = kn;
        coordinates = new float[2 * kn][2];
    }

    public float getTouchX() {
        return touchX;
    }

    public float getTouchY() {
        return touchY;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        Log.d("progress", "Old data: " + this.data + " New data: " + data);
        this.data = data;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchX = event.getX();
            touchY = event.getY();
            boolean isItIn;
            for (int i = 0; i < kn; i++) {
                isItIn = ((touchX < coordinates[i][0] + 44) && (coordinates[i][0] - 44 < touchX)) &&
                        ((touchY < coordinates[i][1] + 44) && (coordinates[i][1] - 44 < touchY));
                if (isItIn) {
                    Log.d("progress", "You touched: " + i + "th node");
                }
            }
            Log.d("progress", "Pressed X: " + touchX + " Y: " + touchY);

            return true;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("progress", "Data in onDraw: " + data);
        int orgX = this.getWidth() / 2;
        int orgY = this.getHeight() / 2;
        Log.d("progress", "orgX: " + orgX);
        Log.d("progress", "orgY: " + orgY);
        int bigRadius;
        int smallRadius = 30;
        bigRadius = 300;
        paintLarge.setStyle(Paint.Style.STROKE);
        paintSmallStrong.setStyle(Paint.Style.FILL);
        paintLarge.setColor(Color.WHITE);
        paintSmallStrong.setColor(Color.WHITE);
        canvas.drawPaint(paintLarge);
        canvas.drawPaint(paintSmallStrong);
        // Use Color.parseColor to define HTML colors
        paintLarge.setColor(Color.parseColor("#CD5C5C"));
        paintSmallStrong.setColor(Color.parseColor("#CD5C5C"));
        float xVal = 0;
        float yVal = 0;
        double radi = 0;
        Log.d("progress", "Drawing");
        for (int i = 0; i < kn; i++) {
            radi = toRadians(i * (360) / kn);
            xVal = (float) cos(radi);
            yVal = (float) sin(radi);
            coordinates[i][0] = orgX + xVal * bigRadius;
            coordinates[i][1] = orgY + yVal * bigRadius;
            canvas.drawCircle(orgX + xVal * bigRadius, orgY + yVal * bigRadius, smallRadius, paintSmallStrong);
            Log.d("progress", "Drew: " + coordinates[i][0] + " and " + coordinates[i][1]);
        }
        canvas.drawCircle(orgX, orgY, smallRadius, paintSmallStrong);
        canvas.drawCircle(orgX, orgY, bigRadius, paintLarge);
    }
}