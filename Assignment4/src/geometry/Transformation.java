package geometry;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class Transformation {

    // Assume square matrices
    private static final int MATRIX_SIZE = 4;
    private double[][] transformationMatrix;
    private Transformation() {
        this.transformationMatrix = new double[MATRIX_SIZE][MATRIX_SIZE];
    }

    public Transformation(Transformation t) {
        this.transformationMatrix = new double[MATRIX_SIZE][MATRIX_SIZE];
        for(int i = 0; i < MATRIX_SIZE; i++) {
            for(int j = 0; j < MATRIX_SIZE; j++) {
                this.transformationMatrix[i][j] = t.transformationMatrix[i][j];
            }
        }
    }

    public static Transformation identity() {
        Transformation t = new Transformation();
        for(int i = 0; i < MATRIX_SIZE; i++) {
            t.transformationMatrix[i][i] = 1;
        }
        return t;
    }

    public static Transformation perspective(double d) {
        Transformation t = Transformation.identity();
        t.transformationMatrix[MATRIX_SIZE - 1][2] = 1.0 / d;
        t.transformationMatrix[MATRIX_SIZE - 1][3] = 0;
        return t;
    }

    public static Transformation translate(double transX, double transY, double transZ) {
        Transformation t = Transformation.identity();
        t.transformationMatrix[0][MATRIX_SIZE - 1] = transX;
        t.transformationMatrix[1][MATRIX_SIZE - 1] = transY;
        t.transformationMatrix[2][MATRIX_SIZE - 1] = transZ;
        return t;
    }

    public static Transformation scale(double scaleX, double scaleY, double scaleZ) {
        Transformation t = Transformation.identity();
        t.transformationMatrix[0][0] = scaleX;
        t.transformationMatrix[1][1] = scaleY;
        t.transformationMatrix[2][2] = scaleZ;
        return t;
    }

    // Input unit: rad
    public static Transformation rotate(double rotateX, double rotateY, double rotateZ) {
        Transformation rotateXTransformation = Transformation.identity();
        Transformation rotateYTransformation = Transformation.identity();
        Transformation rotateZTransformation = Transformation.identity();
        Transformation result = Transformation.identity();

        if(rotateX != 0) {
            double a = Math.cos(rotateX);
            double b = Math.sin(rotateX);
            rotateXTransformation.transformationMatrix[1][1] = a;
            rotateXTransformation.transformationMatrix[1][2] = -b;
            rotateXTransformation.transformationMatrix[2][1] = b;
            rotateXTransformation.transformationMatrix[2][2] = a;
            result.postMultiply(rotateXTransformation);
        }

        if(rotateY != 0) {
            double a = Math.cos(rotateY);
            double b = Math.sin(rotateY);
            rotateYTransformation.transformationMatrix[0][0] = a;
            rotateYTransformation.transformationMatrix[0][2] = b;
            rotateYTransformation.transformationMatrix[2][0] = -b;
            rotateYTransformation.transformationMatrix[2][2] = a;
            result.postMultiply(rotateYTransformation);
        }

        if(rotateZ != 0) {
            double a = Math.cos(rotateZ);
            double b = Math.sin(rotateZ);
            rotateZTransformation.transformationMatrix[0][0] = a;
            rotateZTransformation.transformationMatrix[0][1] = -b;
            rotateZTransformation.transformationMatrix[1][0] = b;
            rotateZTransformation.transformationMatrix[1][1] = a;
            result.postMultiply(rotateZTransformation);
        }
        return result;
    }

    public Point3DH multiplyPoint(Point3DH p) {
        double[][] t = this.transformationMatrix;

        double x = p.getX();
        double y = p.getY();
        double z = p.getZ();
        double w = p.getW();

        double newX = t[0][0] * x + t[0][1] * y + t[0][2] * z + t[0][3] * w;
        double newY = t[1][0] * x + t[1][1] * y + t[1][2] * z + t[1][3] * w;
        double newZ = t[2][0] * x + t[2][1] * y + t[2][2] * z + t[2][3] * w;
        double newW = t[3][0] * x + t[3][1] * y + t[3][2] * z + t[3][3] * w;
        return new Point3DH(newX, newY, newZ, newW);
    }

    public Vertex3D multiplyVertex(Vertex3D v) {
        double[][] t = this.transformationMatrix;

        Point3DH p = v.getPoint3D();
        double x = p.getX();
        double y = p.getY();
        double z = p.getZ();
        double w = p.getW();

        double newX = t[0][0] * x + t[0][1] * y + t[0][2] * z + t[0][3] * w;
        double newY = t[1][0] * x + t[1][1] * y + t[1][2] * z + t[1][3] * w;
        double newZ = t[2][0] * x + t[2][1] * y + t[2][2] * z + t[2][3] * w;
        double newW = t[3][0] * x + t[3][1] * y + t[3][2] * z + t[3][3] * w;
        Vertex3D newV = new Vertex3D(new Point3DH(newX, newY, newZ, newW), v.getColor());
        if(v.hasNormal()) {
            newV.setNormal(v.getNormal());
        }
        return newV;
    }

    // TODO: Fix preMultiply and postMultiply to reflect the actuual operation lol...right now A.preMulitply(B) means B * A
    public void preMultiply(Transformation t) {
        Transformation result = new Transformation();
        for(int k = 0; k < MATRIX_SIZE; k++) {
            for (int i = 0; i < MATRIX_SIZE; i++) {
                double r = t.transformationMatrix[i][k];
                for (int j = 0; j < MATRIX_SIZE; j++){
                    result.transformationMatrix[i][j] += r * this.transformationMatrix[k][j];
                }
            }
        }
        this.transformationMatrix = result.transformationMatrix;
    }

    public void postMultiply(Transformation t) {
        Transformation result = new Transformation();
        for(int k = 0; k < MATRIX_SIZE; k++) {
            for (int i = 0; i < MATRIX_SIZE; i++) {
                double r = this.transformationMatrix[i][k];
                for (int j = 0; j < MATRIX_SIZE; j++){
                    result.transformationMatrix[i][j] += r * t.transformationMatrix[k][j];
                }
            }
        }
        this.transformationMatrix = result.transformationMatrix;
    }

    public Transformation adjoint() {
        Transformation t = identity();
        int i,j;
        for (i=0; i<4; i++) {
            for (j=0; j<4; j++) {
                t.transformationMatrix[i][j] = COFACTOR_4x4_IJ(j, i);
            }
        }
        return t;
    }

    public Transformation cofactor() {
        Transformation t = identity();
        int i,j;

        for (i=0; i<4; i++) {
            for (j=0; j<4; j++) {
                t.transformationMatrix[i][j] = COFACTOR_4x4_IJ(i, j);
            }
        }
        return t;
    }

    // reference: https://github.com/markkilgard/glut/blob/master/lib/gle/vvector.h
    private double COFACTOR_4x4_IJ(int i, int j) {
        int[] ii = new int[4];
        int[] jj = new int[4];
        int k;
        double[][] m = this.transformationMatrix;
        double fac;

        for (k=0; k<i; k++) ii[k] = k;
        for (k=i; k<3; k++) ii[k] = k + 1;
        for (k=0; k<j; k++) jj[k] = k;
        for (k=j; k<3; k++) jj[k] = k + 1;

        (fac) =  m[ii[0]][jj[0]] * (m[ii[1]][jj[1]] * m[ii[2]][jj[2]] - m[ii[1]][jj[2]] * m[ii[2]][jj[1]]);
        (fac) -= m[ii[0]][jj[1]] * (m[ii[1]][jj[0]] * m[ii[2]][jj[2]] - m[ii[1]][jj[2]] * m[ii[2]][jj[0]]);
        (fac) += m[ii[0]][jj[2]] * (m[ii[1]][jj[0]] * m[ii[2]][jj[1]] - m[ii[1]][jj[1]] * m[ii[2]][jj[0]]);

        /* compute sign */
        k = i + j;
        if ( k != (k / 2) * 2) {
            (fac) = -(fac);
        }

        return fac;
    }
}

