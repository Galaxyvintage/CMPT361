package client.testPages;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import java.util.Random;

public class RandomPolygonTest {
    private static final int NUM_TRIANGLE = 20;
    private static final int RANGE = 300;
    private static final int COVERAGE = 1;

    private static final Vertex3D[] vertex = createRandomVertices();
    private final PolygonRenderer renderer;
    private final Drawable panel;


    public RandomPolygonTest(Drawable panel, PolygonRenderer renderer) {
        this.panel = panel;
        this.renderer = renderer;
        render();
    }

    private void render() {
        for(int i = 0; i < NUM_TRIANGLE; i++) {
            Polygon polygon = Polygon.makeEnsuringCounterClockwise(vertex[i * 3], vertex[i * 3 + 1], vertex[i * 3 + 2]);
            renderer.drawPolygon(polygon, panel);
        }
    }

    private static Vertex3D[] createRandomVertices() {
        Random rnd = new Random();
        Vertex3D[] ret = new Vertex3D[NUM_TRIANGLE * 3];

        for(int i = 0; i < NUM_TRIANGLE; i++) {
            int x1 = rnd.nextInt(RANGE);
            int y1 = rnd.nextInt(RANGE);

            int x2 = rnd.nextInt(RANGE);
            int y2 = rnd.nextInt(RANGE);

            int x3 = rnd.nextInt(RANGE);
            int y3 = rnd.nextInt(RANGE);

            Color color = Color.random(rnd);
            Vertex3D p1 = new Vertex3D(x1, y1, 0, color);
            Vertex3D p2 = new Vertex3D(x2, y2, 0, color);
            Vertex3D p3 = new Vertex3D(x3, y3, 0, color);
            ret[i * 3 ] = p1;
            ret[i * 3 + 1] = p2;
            ret[i * 3 + 2] = p3;
        }
        return ret;
    }
}
