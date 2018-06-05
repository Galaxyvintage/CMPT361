package geometry;

public class Transformation {

    // Assume square matrices
    private static final int MATRIX_SIZE = 4;
    private double[][] transformationMatrix;
    private Transformation() {
        this.transformationMatrix = new double[MATRIX_SIZE][MATRIX_SIZE];
    }

    public static Transformation identity() {
        Transformation t = new Transformation();
        for(int i = 0; i < MATRIX_SIZE; i++) {
            t.transformationMatrix[i][i] = 1;
        }
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

    public static Transformation rotate(double rotateX, double rotateY, double rotateZ) {
        return null;
    }

    public Transformation preMulitply(Transformation t) {
        Transformation result = new Transformation();
        for(int k = 0; k < MATRIX_SIZE; k++) {
            for (int i = 0; i < MATRIX_SIZE; i++) {
                double r = t.transformationMatrix[i][k];
                for (int j = 0; j < MATRIX_SIZE; j++){
                    result.transformationMatrix[i][j] += r * this.transformationMatrix[k][j];
                }
            }
        }
        return result;
    }

    public Transformation postMulitply(Transformation t) {
        Transformation result = new Transformation();
        for(int k = 0; k < MATRIX_SIZE; k++) {
            for (int i = 0; i < MATRIX_SIZE; i++) {
                double r = this.transformationMatrix[i][k];
                for (int j = 0; j < MATRIX_SIZE; j++){
                    result.transformationMatrix[i][j] += r * t.transformationMatrix[k][j];
                }
            }
        }
        return result;
    }

    public Vertex3D mulitplyVertex(Vertex3D v) {
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
        return new Vertex3D(new Point3DH(newX, newY, newZ, newW), v.getColor());
    }
}
