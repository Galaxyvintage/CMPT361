package client.testPages;

import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;


public class ParallelogramLineTest {
    private final LineRenderer renderer;
    private final Drawable panel;

    public ParallelogramLineTest(Drawable panel, LineRenderer renderer) {
        this.panel = panel;
        this.renderer = renderer;
        render();
    }

    private void render() {
        for(int p = 0; p <=50; ++p) {

            // line 1
            int x1 = 20;
            int y1 = 80 + p;

            int x2 = 150;
            int y2 = 150 + p;

            Vertex3D p1 = new Vertex3D(x1, y1, 0, Color.WHITE);
            Vertex3D p2 = new Vertex3D(x2, y2, 0, Color.WHITE);
            renderer.drawLine(p1, p2, panel);

            // line 2
            int x3 = 160 + p;
            int y3 = 270;

            int x4 = 240 + p;
            int y4 = 40;

            Vertex3D p3 = new Vertex3D(x3, y3, 0, Color.WHITE);
            Vertex3D p4 = new Vertex3D(x4, y4, 0, Color.WHITE);
            renderer.drawLine(p3, p4, panel);
        }
    }
}

