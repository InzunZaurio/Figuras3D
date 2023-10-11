package com.example.figuras3d;

import android.graphics.Path;
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

public class CilindroActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CilindroView cilindroView = new CilindroView(this);
        setContentView(cilindroView);
    }

    private class CilindroView extends View {

        private Paint paint;
        private float rotationAngle = 0f;
        private float scaleFactor = 1f;
        private GestureDetector gestureDetector;
        private ScaleGestureDetector scaleGestureDetector;

        public CilindroView(Context context) {
            super(context);
            paint = new Paint();
            paint.setColor(Color.GREEN);
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

            int centerX = getWidth()/8 ;
            int centerY = getHeight()/8 ;
            int depth = 500; // Profundidad del cilindro
            int radius = 100;
            int numEdges = 50; // Número de aristas en el cilindro

            float[] upperXPoints = new float[numEdges];
            float[] upperYPoints = new float[numEdges];
            float[] lowerXPoints = new float[numEdges];
            float[] lowerYPoints = new float[numEdges];

            for (int i = 0; i < numEdges; i++) {
                float angle = (float) (2 * Math.PI * i / numEdges);
                upperXPoints[i] = centerX + radius * (float) Math.cos(angle);
                upperYPoints[i] = centerY + radius * (float) Math.sin(angle);
                lowerXPoints[i] = upperXPoints[i] + depth;
                lowerYPoints[i] = upperYPoints[i];
            }

            // Dibuja el cilindro en el lienzo
            Path path = new Path();
            path.moveTo(upperXPoints[0], upperYPoints[0]);
            for (int i = 1; i < numEdges; i++) {
                path.lineTo(upperXPoints[i], upperYPoints[i]);
            }
            path.close();

            Path lowerPath = new Path();
            lowerPath.moveTo(lowerXPoints[0], lowerYPoints[0]);
            for (int i = 1; i < numEdges; i++) {
                lowerPath.lineTo(lowerXPoints[i], lowerYPoints[i]);
            }
            lowerPath.close();

            canvas.save();
            canvas.translate(centerX, centerY); // Centrar el cilindro en el centro del lienzo
            canvas.rotate(rotationAngle);
            canvas.scale(scaleFactor, scaleFactor);

            // Dibujar las aristas del cilindro
            canvas.drawPath(path, paint);
            canvas.drawPath(lowerPath, paint);

            // Dibujar las caras del cilindro
            for (int i = 0; i < numEdges; i++) {
                canvas.drawLine(upperXPoints[i], upperYPoints[i], lowerXPoints[i], lowerYPoints[i], paint);
            }

            canvas.restore();
        }
    }
}
