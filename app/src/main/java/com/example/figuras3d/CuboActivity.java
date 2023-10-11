package com.example.figuras3d;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.hardware.*;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
public class CuboActivity extends AppCompatActivity {
    Perspectiva perspectiva;

    public class Dimension {
        public int height, width;

        public Dimension(int w, int h) {
            this.width = w;
            this.height = h;
        }

        public final void set(Dimension d) {
            this.width = d.width;
            this.height = d.height;
        }
    }

    class Obj {
        float f12d, objSize;
        float phi = 1.3f, rho, theta = 0.3f;
        float v11, v12, v13, v21, v22, v23, v32, v33, v43;
        Point2D[] vScr = new Point2D[8];
        Point3D[] f13w = new Point3D[8];

        Obj() {
            CuboActivity cuboActivity = CuboActivity.this;
            this.f13w[0] = new Point3D(1.0d, -1.0d, -1.0d);
            this.f13w[1] = new Point3D(1.0d, 1.0d, -1.0d);
            this.f13w[2] = new Point3D(-1.0d, 1.0d, -1.0d);
            this.f13w[3] = new Point3D(-1.0d, -1.0d, -1.0d);
            this.f13w[4] = new Point3D(1.0d, -1.0d, 1.0d);
            this.f13w[5] = new Point3D(1.0d, 1.0d, 1.0d);
            this.f13w[6] = new Point3D(-1.0d, 1.0d, 1.0d);
            this.f13w[7] = new Point3D(-1.0d, -1.0d, 1.0d);
            this.objSize = (float) Math.sqrt(12.0d);
            this.rho = this.objSize * 5.0f;
        }

        void initPersp() {
            float costh = (float) Math.cos((double) this.theta), sinth = (float) Math.sin((double)
                    this.theta);
            float cosph = (float) Math.cos((double) this.phi), sinph = (float) Math.sin((double)
                    this.phi);
            this.v11 = -sinth;
            this.v12 = (-cosph) * costh;
            this.v13 = sinph * costh;
            this.v21 = costh;
            this.v22 = (-cosph) * sinth;
            this.v23 = sinph * sinth;
            this.v32 = sinph;
            this.v33 = cosph;
            this.v43 = -this.rho;
        }

        void eyeAndScreen() {
            initPersp();
            for (int i = 0; i < 8; i++) {
                Point3D paint = this.f13w[i];
                float z = (((this.v13 * paint.f18x) + (this.v23 * paint.f19y)) + (this.v33 *
                        paint.f20z)) + this.v43;
                this.vScr[i] = new Point2D(((-this.f12d) * ((this.v11 * paint.f18x) + (this.v21 *
                        paint.f19y))) / z, ((-this.f12d) * (((this.v12 * paint.f18x) + (this.v22 * paint.f19y)) + (this.v32
                        * paint.f20z))) / z);
            }
        }
    }

    class ObjCilin {
        float f12d2, objSize2;
        float rho2, theta2 = 0.3f;
        float v112, v122, v132, v212, v222, v232, v322, v332, v432;
        Point2D[] vScr2; // Usamos 360 puntos para un cono más suave
        Point3D[] f13w2 = new Point3D[360]; // 360 puntos para el cono

        ObjCilin() {
            this.objSize2 = 2.0f; // Tamaño del cono
            this.rho2 = this.objSize2 / 2.0f; // Radio del cono
            this.vScr2 = new Point2D[360]; // Inicializa el array vScr con 360 elementos

            for (int a = 0; a < 360; a++) {
                double thetaRad = Math.toRadians(a);
                double x = Math.cos(thetaRad) * this.rho2;
                double y = Math.sin(thetaRad) * this.rho2;
                double z = -a/50.0f; // Controla la altura del cono (ajusta el divisor para cambiar la inclinación)
                this.f13w2[a] = new Point3D(x, y, z);
                this.vScr2[a] = new Point2D(0, 0); // Inicializa cada elemento del array vScr
            }
        }

        void initPersp() {
            float costh = (float) Math.cos((double) this.theta2), sinth = (float) Math.sin((double) this.theta2);
            this.v112 = costh;
            this.v122 = 0;
            this.v132 = sinth;
            this.v212 = sinth * sinth - costh * costh;
            this.v222 = 2 * costh * sinth;
            this.v232 = sinth * costh + sinth * costh;
            this.v322 = sinth;
            this.v332 = -costh;
            this.v432 = -this.rho2;
        }

        void eyeAndScreen() {
            initPersp();
            for (int i = 0; i < 360; i++) {
                Point3D paint = this.f13w2[i];
                float z = (this.v132 * paint.f18x + this.v232 * paint.f19y + this.v332 * paint.f20z) + this.v432;
                this.vScr2[i] = new Point2D(((-this.f12d2) * (this.v112 * paint.f18x + this.v212 * paint.f19y)) / z,
                        ((-this.f12d2) * (this.v122 * paint.f18x + this.v222 * paint.f19y + this.v322 * paint.f20z)) / z);
            }
        }
    }



    public class Perspectiva extends View implements SensorEventListener {
        int centerX, centerY, centerXC, centerYC;
        Dimension dim, dimC;
        Sensor gyro;
        int maxX, maxY, minMaxXY, maxXC, maxYC, minMaxXYC;
        Obj obj = new Obj();
        ObjCilin objCilin = new ObjCilin();
        Paint paint = new Paint();
        SensorManager sensorManager;
        int f14x = 180, f15y = 180; /* renamed from: x, renamed from: y */
        @SuppressLint("WrongConstant")
        public Perspectiva(Context c) {
            super(c);
            this.sensorManager = (SensorManager) CuboActivity.this.getSystemService("sensor");
            this.gyro = this.sensorManager.getDefaultSensor(11);
            this.sensorManager.registerListener(this, this.gyro, 0);
            this.paint.setAntiAlias(true);
        }
        protected void onDraw(Canvas c) {
            super.onDraw(c);
            this.paint.setColor(Color.parseColor("#E5E5E5"));
            c.drawPaint(this.paint);
            this.dim = new Dimension(getWidth() / 2, getHeight());
            this.maxX = this.dim.width - 1;
            this.maxY = this.dim.height - 1;
            this.minMaxXY = Math.min(this.maxX, this.maxY);
            this.centerX = this.maxX / 2;
            this.centerY = this.maxY / 5;
            this.obj.f12d = (this.obj.rho * ((float) this.minMaxXY)) / this.obj.objSize;
            this.obj.eyeAndScreen();
            line(c, 0, 1); line(c, 1, 2); line(c, 2, 3); line(c, 3, 0);
            line(c, 4, 5); line(c, 5, 6); line(c, 6, 7); line(c, 7, 4);
            line(c, 0, 4); line(c, 1, 5); line(c, 2, 6); line(c, 3, 7);

            //CONO

            this.dimC = new Dimension(getWidth() / 2, getHeight());
            this.maxXC = this.dimC.width - 1;
            this.maxYC = this.dimC.height - 1;
            this.minMaxXYC = Math.min(this.maxXC, this.maxYC);
            this.centerXC = this.maxXC + 400;
            this.centerYC = this.maxYC / 5;
            this.objCilin.f12d2 = (this.objCilin.rho2 * ((float) this.minMaxXYC)) / this.objCilin.objSize2;
            this.objCilin.eyeAndScreen();
            for (int i = 0;i < 359; i++) {
                lineCono(c, i, 0);
            }

            //PRISMA
            this.dim = new Dimension(getWidth() / 2, getHeight());
            this.maxX = this.dim.width - 1;
            this.maxY = this.dim.height - 1;
            this.minMaxXY = Math.min(this.maxX, this.maxY);
            this.centerX = this.maxX / 2;
            this.centerY = this.maxY / 2;
            this.obj.f12d = (this.obj.rho * ((float) this.minMaxXY)) / this.obj.objSize;
            this.obj.eyeAndScreen();
            line(c, 0, 1); line(c, 1, 2); line(c, 2, 3); line(c, 3, 0);
            line(c, 4, 5); line(c, 5, 6); line(c, 6, 7); line(c, 7, 4);
            line(c, 0, 4); line(c, 1, 5); line(c, 2, 6); line(c, 3, 7);

            //ESFERA
            this.dim = new Dimension(getWidth() / 2, getHeight());
            this.maxX = this.dim.width - 1;
            this.maxY = this.dim.height - 1;
            this.minMaxXY = Math.min(this.maxX, this.maxY);
            this.centerX = this.maxX + 200;
            this.centerY = this.maxY / 2;
            this.obj.f12d = (this.obj.rho * ((float) this.minMaxXY)) / this.obj.objSize;
            this.obj.eyeAndScreen();
            line(c, 0, 1); line(c, 1, 2); line(c, 2, 3); line(c, 3, 0);
            line(c, 4, 5); line(c, 5, 6); line(c, 6, 7); line(c, 7, 4);
            line(c, 0, 4); line(c, 1, 5); line(c, 2, 6); line(c, 3, 7);
        }
        int iX(float x) {
            return (int) Math.floor((double) (((float) this.centerX) + x));
        }
        int iY(float y) {
            return (int) Math.floor((double) (((float) this.centerY) - y));
        }
        void line(Canvas ca, int i, int j) {
            this.paint.setColor(Color.parseColor("#1B8677"));//ColorRojo #ff0000 //ColorVerde #2BF321
            Point2D po = this.obj.vScr[i];
            Point2D q = this.obj.vScr[j];
            ca.drawLine((float) iX(po.f16x), (float) iY(po.f17y), (float) iX(q.f16x), (float)
                    iY(q.f17y), this.paint);
        }

        void lineCono(Canvas ca, int i, int j){
            this.paint.setColor(Color.parseColor("#1B8677"));//ColorRojo #ff0000 //ColorVerde #2BF321
            Point2D po2 = this.objCilin.vScr2[i];
            Point2D q2 = this.objCilin.vScr2[j];
            ca.drawLine(po2.f16x + this.centerXC, this.centerYC - po2.f17y, q2.f16x + this.centerXC, this.centerYC - q2.f17y, this.paint);
        }
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] matrizDeRotacion = new float[16];
            SensorManager.getRotationMatrixFromVector(matrizDeRotacion, sensorEvent.values);
            float[] remapeoMatrizDeRotacion = new float[16];
            SensorManager.remapCoordinateSystem(matrizDeRotacion, 1, 3, remapeoMatrizDeRotacion);
            float[] orientations = new float[3];
            SensorManager.getOrientation(remapeoMatrizDeRotacion, orientations);
            for (int i = 0; i < 3; i++) {
                orientations[i] = (float) Math.toDegrees((double) orientations[i]);
            }
            this.obj.theta = ((float) (getWidth() / 2)) / (((float) this.f14x) + orientations[0]);
            this.obj.phi = ((float) getHeight()) / (((float) this.f15y) + orientations[1]);
            this.obj.rho = (this.obj.phi / this.obj.theta) * ((float) getHeight());
            this.centerX = (int) (((float) this.f14x) + orientations[0]);
            this.centerY = (int) (((float) this.f15y) + orientations[1]);
            invalidate();

            this.objCilin.theta2 = ((float) (getWidth() / 2)) / (((float) this.f14x) + orientations[0]);
            this.objCilin.rho2 = (this.obj.phi / this.obj.theta) * ((float) getHeight());
            this.centerX = (int) (((float) this.f14x) + orientations[0]);
            this.centerY = (int) (((float) this.f15y) + orientations[1]);
            invalidate();
        }
        public void onAccuracyChanged(Sensor sensor, int i) { }
    }
    class Point2D {
        float f16x, f17y;
        Point2D(float x, float y) {
            this.f16x = x;
            this.f17y = y;
        }
    }
    class Point3D {
        float f18x, f19y, f20z;
        Point3D(double x, double y, double z) {
            this.f18x = (float) x;
            this.f19y = (float) y;
            this.f20z = (float) z;
        }
    }
    @SuppressLint("WrongConstant")
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setRequestedOrientation(1);
        this.perspectiva = new Perspectiva(this);
        setContentView(this.perspectiva);
    }
}
