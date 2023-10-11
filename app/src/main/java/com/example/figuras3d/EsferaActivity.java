package com.example.figuras3d;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class EsferaActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EsferaView esferaView = new EsferaView(this);
        setContentView(esferaView);
    }

    private class EsferaView extends View {

        private Paint paint;
        private float rotationAngle = 0f;
        private float scaleFactor = 1f;
        private GestureDetector gestureDetector;
        private ScaleGestureDetector scaleGestureDetector;

        public EsferaView(Context context) {
            super(context);
            paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    rotationAngle += distanceX / 2;
                    invalidate();
                    return true;
                }
            });

            scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    scaleFactor *= detector.getScaleFactor();
                    scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f)); // Limitar el factor de escala
                    invalidate();
                    return true;
                }
            });
        }
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            scaleGestureDetector.onTouchEvent(event);
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            int radius = 100;

            canvas.save();
            canvas.translate(centerX, centerY);
            canvas.rotate(rotationAngle);
            canvas.scale(scaleFactor, scaleFactor);

            // Dibuja la esfera en el lienzo
            canvas.drawCircle(centerX, centerY, radius, paint);
            canvas.restore();
        }
    }
}
