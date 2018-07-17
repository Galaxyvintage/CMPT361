package client.testPages;


import geometry.Point3DH;
import geometry.Transformation;
import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import shader.Shader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import java.util.Random;

public class CenteredTriangleTest {

    private static final double[] COLORS = {1.0, 0.85, 0.7, 0.55, 0.4, 0.25};
    private static final int RADIUS = 275;
    private static final int MAX_Z = -1;
    private static final int MIN_Z = -199;
    private static final double DEFAULT_ANGLE = (2.0 * Math.PI) / 3; // 120 degrees / (2 * pi)/3 rad

    private final PolygonRenderer renderer;
    private final Drawable panel;
    private Vertex3D center;

    public CenteredTriangleTest(Drawable panel, PolygonRenderer renderer) {
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

    private void drawRegularTriangle(Color color) {

        Random rnd = new Random();
        Double rndRotate = rnd.nextDouble() % DEFAULT_ANGLE;
        Double rndZ = (double)(rnd.nextInt(MAX_Z - MIN_Z) + MIN_Z);

        Vertex3D v1 = radialPoint(RADIUS, DEFAULT_ANGLE * 0.0);
        Vertex3D v2 = radialPoint(RADIUS, DEFAULT_ANGLE * 1.0);
        Vertex3D v3 = radialPoint(RADIUS, DEFAULT_ANGLE * 2.0);

        // Replace old z value with a random z value
        v1 = v1.replacePoint(new Point3DH(v1.getX(), v1.getY(), rndZ));
        v2 = v2.replacePoint(new Point3DH(v2.getX(), v2.getY(), rndZ));
        v3 = v3.replacePoint(new Point3DH(v3.getX(), v3.getY(), rndZ));

        Transformation t = Transformation.identity();

        t.preMultiply(Transformation.translate(-center.getIntX(), -center.getIntY(), 0));
        t.preMultiply(Transformation.rotate(0, 0, rndRotate));
        t.preMultiply(Transformation.translate(center.getIntX(), center.getIntY(), 0));

        v1 = t.multiplyVertex(v1);
        v2 = t.multiplyVertex(v2);
        v3 = t.multiplyVertex(v3);
        Polygon p = Polygon.makeEnsuringCounterClockwise(v1, v2, v3);

        Shader s = (c) -> color;
        renderer.drawPolygon(p, panel, s);
    }

    private void render() {
        for(int i = 0; i < COLORS.length; i++) {
            double r, g, b;
            r = g = b = COLORS[i];
            Color c = new Color(r, g, b);
            drawRegularTriangle(c);
        }
    }
}
