package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;

public class DDALineRenderer implements LineRenderer {
    // use the static factory make() instead of constructor.
    private DDALineRenderer() {}

    @Override
    public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
        double deltaX = p2.getIntX() - p1.getIntX();
        double deltaY = p2.getIntY() - p1.getIntY();

        double x = p1.getIntX();
        double y = p1.getIntY();

        double yIncrement = deltaY/deltaX;
        int argbColor = p1.getColor().asARGB();

        for(int k = 0; k <= deltaX; k++) {
            x += 1;
            y += yIncrement;
            drawable.setPixel((int)Math.round(x), (int)Math.round(y), 0.0, argbColor);
        }
    }

    public static LineRenderer make() {
        return new AnyOctantLineRenderer(new DDALineRenderer());
    }
}
