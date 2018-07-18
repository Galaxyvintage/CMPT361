package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;

public class  DDALineRenderer implements LineRenderer {
    private static final int COVERAGE = 1;
    // use the static factory make() instead of constructor.
    private DDALineRenderer() {}

    @Override
    public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
        double deltaX = p2.getIntX() - p1.getIntX();
        double deltaY = p2.getIntY() - p1.getIntY();

        double x = p1.getIntX();
        double y = p1.getIntY();

        int x_round = (int)Math.round(x);
        int y_round = (int)Math.round(y);
        int argbColor = p1.getColor().asARGB();
        drawable.setPixelWithCoverage(x_round, y_round, 0.0, argbColor, COVERAGE);

        double steps;
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            steps = Math.abs(deltaX);
        }
        else {
            steps = Math.abs(deltaY);
        }

        if(steps != 0) {
            double xIncrement = deltaX / steps;
            double yIncrement = deltaY / steps;
            for (int k = 0; k < steps; k++) {
                x += xIncrement;
                y += yIncrement;

                x_round = (int) Math.round(x);
                y_round = (int) Math.round(y);
                drawable.setPixelWithCoverage(x_round, y_round, 0.0, argbColor, COVERAGE);
            }
        }
    }

    public static LineRenderer make() {
        return new AnyOctantLineRenderer(new DDALineRenderer());
    }
}
