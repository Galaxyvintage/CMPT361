package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class DDALineRenderer implements LineRenderer {
    private static final int COVERAGE = 1;
    // use the static factory make() instead of constructor.
    private DDALineRenderer() {}

    // Assume octant 1
    @Override
    public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
        // coordinate
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        double deltaZ = p2.getZ() - p1.getZ();

        // color
        double deltaR = p2.getColor().getR() - p1.getColor().getR();
        double deltaG = p2.getColor().getG() - p1.getColor().getG();
        double deltaB = p2.getColor().getB() - p1.getColor().getB();

        double x = p1.getX();
        double y = p1.getY();
        double z = p1.getZ();

        double r = p1.getColor().getR();
        double g = p1.getColor().getG();
        double b = p1.getColor().getB();


        int x_round = (int)Math.round(x);
        int y_round = (int)Math.round(y);
        int argbColor = new Color(r, g, b).asARGB();
        drawable.setPixelWithCoverage(x_round, y_round, z, argbColor, COVERAGE);

        int steps;
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            steps = (int)Math.abs(deltaX);
        }
        else {
            steps = (int)Math.abs(deltaY);
        }

        if(steps != 0) {
            double xIncrement = deltaX / steps;
            double yIncrement = deltaY / steps;
            double zIncrement = deltaZ / steps;
            double rIncrement = deltaR / steps;
            double gIncrement = deltaG / steps;
            double bIncrement = deltaB / steps;

            for (int k = 0; k < steps; k++) {
                x += xIncrement;
                y += yIncrement;
                z += zIncrement;
                r += rIncrement;
                g += gIncrement;
                b += bIncrement;

                x_round = (int) Math.round(x);
                y_round = (int) Math.round(y);
                argbColor = new Color(r, g, b).asARGB();
                drawable.setPixelWithCoverage(x_round, y_round, z, argbColor, COVERAGE);
            }
        }
    }

    public static LineRenderer make() {
        return new AnyOctantLineRenderer(new DDALineRenderer());
    }
}
