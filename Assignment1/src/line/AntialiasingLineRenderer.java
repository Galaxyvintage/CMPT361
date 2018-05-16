package line;

import geometry.Line;
import geometry.Vertex3D;
import windowing.drawable.Drawable;


public class AntialiasingLineRenderer implements LineRenderer {
    private static final int COVERAGE = 1;
    private static final double LINE_WIDTH = 1;
    private static final double RADIUS = 0.5;
    private static final double RADIUS_SQUARE = RADIUS * RADIUS;
    private static final int SAMPLE_BEGIN = -1;
    private static final int SAMPLE_END = 1;


    // use the static factory make() instead of constructor.
    private AntialiasingLineRenderer() {}

    @Override
    public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
        Line l = new Line(p1, p2);
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();

        double x = p1.getX();
        double y = p1.getY();
//        System.out.println("x: " + x);
//        System.out.println("y: " + y);


        double xIncrement = deltaX/deltaX;
        double yIncrement = deltaY/deltaX;

        int x_round = (int)Math.round(x);
        int y_round = (int)Math.round(y);


        int argbColor = p1.getColor().asARGB();
        double ratio;
        ratio = sampleArea(x_round, y_round, l);
        drawable.setPixelWithCoverage(x_round, y_round, 0.0, argbColor, ratio);
        for (int i = SAMPLE_BEGIN; i <= SAMPLE_END; i++) {
            ratio = sampleArea(x_round, y_round + i , l);
            drawable.setPixelWithCoverage(x_round, y_round + i, 0.0, argbColor, ratio);
        }

        for(int k = 0; k <= deltaX; k++) {
            x += xIncrement;
            y += yIncrement;
            x_round = (int)Math.round(x);
            y_round = (int)Math.round(y);

            for (int i = SAMPLE_BEGIN; i <= SAMPLE_END; i++) {
                ratio = sampleArea(x_round, y_round + i, l);
                drawable.setPixelWithCoverage(x_round, y_round + i, 0.0, argbColor, ratio);
            }
        }
    }

    public static LineRenderer make() {
        return new AnyOctantLineRenderer(new AntialiasingLineRenderer());
    }

    private double findPointLineDistance(double x, double y, Line l) {
        double x_1 = l.p1.getX();
        double y_1 = l.p1.getY();
        double x_2 = l.p2.getX();
        double y_2 = l.p2.getY();

        double Y2MinusY1 = y_2 - y_1;
        double X2MinusX1 = x_2 - x_1;

        double area = Math.abs((x - x_2) * (y_1 - y) - (x - x_1) * (y_2 - y));
        double base = Math.sqrt(Y2MinusY1 * Y2MinusY1 + X2MinusX1 * X2MinusX1);
        double height = area / base;
        double distance = height - LINE_WIDTH / 2.0;
        return distance;
    }

    private double sampleArea(double x, double y, Line l) {
        double d = findPointLineDistance(x, y, l);
        System.out.println("d: " + d);
        double d_abs = Math.abs(d);
        double theta = Math.acos(d_abs / RADIUS);

        double circleArea = Math.PI * RADIUS_SQUARE;
        double triangleArea = d_abs * Math.sqrt((RADIUS_SQUARE - d_abs * d_abs));
        double wedgeArea = ( 1 -  theta / Math.PI) * circleArea;

        double ret;
        if (d >= LINE_WIDTH / 2.0) {
            ret = 0;
        } else if (d <= 0) {
            ret = ((triangleArea + wedgeArea) / circleArea);
        } else {
            ret = 1.0 - ((triangleArea + wedgeArea) / circleArea);
        }
        System.out.println("ret: " + ret);
        return ret;
    }
}
