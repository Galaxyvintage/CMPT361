package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;

public class BresenhamLineRenderer implements LineRenderer {
    private static final int COVERAGE = 1;
    // use the static factory make() instead of constructor.
    private BresenhamLineRenderer() {}

    // Reference: Computer Graphics with OpenGL 4th edition page 134
    @Override
    public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
        int deltaX = p2.getIntX() - p1.getIntX();
        int deltaY = p2.getIntY() - p1.getIntY();

        int x = p1.getIntX();
        int y = p1.getIntY();
        int p = 2 * deltaY - deltaX;
        int twoDeltaYMinusDeltaX = 2 * deltaY - 2 * deltaX;

        int argbColor = p1.getColor().asARGB();
        drawable.setPixelWithCoverage(x, y, 0.0, argbColor, COVERAGE);

        /*
            given the line f(x) = y = mx + b
            d_lower is the distance between (x_next, y_current) and (x_next, f(x_current))
            d_upper is the distance between (x_next, y_next) and (x_next, f(x_next))

            d_lower = y - y_current
                    = m * (x_current + 1) + b - y_current
            d_upper = y_next - y
                    = y_next - m * (x_current + 1) - b

            d_upper - d_lower = 2 * m * (x_current + 1)  - 2 * y_current + 2 * b - 1

            substituting m = deltaY/deltaX and multiplying the whole equation by deltaX
            p_current = deltaX * (d_upper - d_lower) = 2 * deltaY * (x_current + 1)  -
                                                       2 * deltaX * y_current + deltaX *
                                                       (2 * b - 1)
            p_next = deltaX * (d_upper - d_lower) = 2 * deltaY * (x_next + 1)  -
                                                    2 * deltaX * y_next + deltaX *
                                                    (2 * b - 1)

            p_next - p_current = 2 * deltaY * (x_next - x_current) - 2 * deltaX * (y_next - y_current)                                           (2 * b - 1)

            p_next = p_current + 2 * deltaY - 2 * deltaX * (y_next - y)
            if p_current < 0:
                y_next - y would be zero
                p_next = p_current + 2 * deltaY
            else:
                y_next - y would be one
                p_next = p_current + 2 * deltaY - 2 * deltaX
        */

        for (int i = 0; i < deltaX; i++) {
            x++;
            if (p < 0) {
                p = p + 2 * deltaY;
            } else {
                y++;
                p = p + twoDeltaYMinusDeltaX;
            }
            drawable.setPixelWithCoverage(x, y, 0.0, argbColor, COVERAGE);
        }
    }

    public static LineRenderer make() {
        return new AnyOctantLineRenderer(new BresenhamLineRenderer());
    }
}
