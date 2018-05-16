package client.testPages;

import geometry.Line;
import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import java.util.Random;


public class RandomLineTest {
    private static final int PIXEL_RANGE = 300;
    private static final int NUM_LINES = 30;
    private static final Line[] lines = createRandomLines();

    private final LineRenderer renderer;
    private final Drawable panel;

    public RandomLineTest(Drawable panel, LineRenderer renderer) {
        this.panel = panel;
        this.renderer = renderer;
        render();
    }

    private void render() {
        for(int i = 0; i < NUM_LINES; ++i) {
            Line line = lines[i];
            renderer.drawLine(line.p1, line.p2, panel);
        }
    }

    private static Line[] createRandomLines() {
        Line[] lines = new Line[NUM_LINES];
        Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());
        for (int i = 0; i < NUM_LINES; ++i) {
            int x1 = rnd.nextInt(PIXEL_RANGE);
            int y1 = rnd.nextInt(PIXEL_RANGE);

            int x2 = rnd.nextInt(PIXEL_RANGE);
            int y2 = rnd.nextInt(PIXEL_RANGE);

            Color color = Color.random(rnd);
            Vertex3D p1 = new Vertex3D(x1, y1, 0, color);
            Vertex3D p2 = new Vertex3D(x2, y2, 0, color);

            lines[i] = new Line(p1, p2);
        }
        return lines;
    }
}

