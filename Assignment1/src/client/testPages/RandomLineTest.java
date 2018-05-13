package client.testPages;

import geometry.Line;
import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import java.util.Random;


public class RandomLineTest {
    private static final int MAX_SIZE = 300;
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
            renderer.drawLine(line.x, line.y, panel);
        }
    }

    private static Line[] createRandomLines() {
        Line[] lines = new Line[MAX_SIZE];
        Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());
        for (int i = 0; i < NUM_LINES; ++i) {
            int x1 = rnd.nextInt(MAX_SIZE);
            int y1 = rnd.nextInt(MAX_SIZE);

            int x2 = rnd.nextInt(MAX_SIZE);
            int y2 = rnd.nextInt(MAX_SIZE);

            Vertex3D p1 = new Vertex3D(x1, y1, 0, Color.WHITE);
            Vertex3D p2 = new Vertex3D(x2, y2, 0, Color.WHITE);

            lines[i] = new Line(p1, p2);
        }
        return lines;
    }
}

