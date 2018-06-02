package client.testPages;


import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import polygon.Shader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class centeredTriangleTest {

    private static final double[] COLORS = {1.0, 0.85, 0.7, 0.55, 0.4, 0.25};
    private static final int RADIUS = 275;

    private final PolygonRenderer renderer;
    private final Drawable panel;
    private Vertex3D center;

    public centeredTriangleTest(Drawable panel, PolygonRenderer renderer) {
        this.panel = panel;
        this.renderer = renderer;
        makeCenter();
        render();
    }

    private void makeCenter() {
        int centerX = panel.getWidth() / 2;
        int centerY = panel.getHeight() / 2;
        center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
    }

    private Vertex3D radialPoint(double radius, double angle) {
        double x = center.getX() + radius * Math.cos(angle);
        double y = center.getY() + radius * Math.sin(angle);
        return new Vertex3D(x, y, 0, Color.WHITE);
    }

    private void drawRegularTriangle() {
        Double angle = (2.0 * Math.PI) / 3;
        Vertex3D p1 = radialPoint(RADIUS, angle * 0.0);
        Vertex3D p2 = radialPoint(RADIUS, angle * 1.0);
        Vertex3D p3 = radialPoint(RADIUS, angle * 2.0);
        Polygon p = Polygon.makeEnsuringCounterClockwise(p1, p2, p3);
        double r, g, b;
        r = g = b = COLORS[0];
        Shader s = (c) -> new Color(r, g, b);
        renderer.drawPolygon(p, panel, s);
    }

    private Polygon rotate(Polygon p) {
        return null;
    }

    private void render() {
        drawRegularTriangle();
    }
}
