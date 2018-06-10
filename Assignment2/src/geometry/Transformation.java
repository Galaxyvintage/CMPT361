package geometry;

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

    public void translate(double transX, double transY, double transZ) {
        Transformation t = Transformation.identity();
        t.transformationMatrix[0][MATRIX_SIZE - 1] = transX;
        t.transformationMatrix[1][MATRIX_SIZE - 1] = transY;
        t.transformationMatrix[2][MATRIX_SIZE - 1] = transZ;
        this.postMulitply(t);
    }

    public void scale(double scaleX, double scaleY, double scaleZ) {
        Transformation t = Transformation.identity();
        t.transformationMatrix[0][0] = scaleX;
        t.transformationMatrix[1][1] = scaleY;
        t.transformationMatrix[2][2] = scaleZ;
        this.postMulitply(t);
    }

    // Input unit: rad
    public void rotate(double rotateX, double rotateY, double rotateZ) {
        Transformation rotateXTransformation = Transformation.identity();
        Transformation rotateYTransformation = Transformation.identity();
        Transformation rotateZTransformation = Transformation.identity();

        if(rotateX != 0) {
            double a = Math.cos(rotateX);
            double b = Math.sin(rotateX);
            rotateXTransformation.transformationMatrix[1][1] = a;
            rotateXTransformation.transformationMatrix[1][2] = -b;
            rotateXTransformation.transformationMatrix[2][1] = b;
            rotateXTransformation.transformationMatrix[2][2] = a;
            this.postMulitply(rotateXTransformation);
        }

        if(rotateY != 0) {
            double a = Math.cos(rotateY);
            double b = Math.sin(rotateY);
            rotateYTransformation.transformationMatrix[0][0] = a;
            rotateYTransformation.transformationMatrix[0][2] = b;
            rotateYTransformation.transformationMatrix[2][0] = -b;
            rotateYTransformation.transformationMatrix[2][2] = a;
            this.postMulitply(rotateYTransformation);
        }

        if(rotateZ != 0) {
            double a = Math.cos(rotateZ);
            double b = Math.sin(rotateZ);
            rotateZTransformation.transformationMatrix[0][0] = a;
            rotateZTransformation.transformationMatrix[0][1] = -b;
            rotateZTransformation.transformationMatrix[1][0] = b;
            rotateZTransformation.transformationMatrix[1][1] = a;
            this.postMulitply(rotateZTransformation);
        }
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

    private void preMulitply(Transformation t) {
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

    private void postMulitply(Transformation t) {
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
}
