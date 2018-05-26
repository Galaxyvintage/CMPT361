package client.testPages;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import java.util.Random;

public class MeshPolygonTest {
    public static final int NO_PERTURBATION = -1;
    public static final int USE_PERTURBATION = 1;
    private static final double MARGIN_PERCENT= 0.04;
    private static final int NUM_POINT = 10;
    private static final int[] SHIFT_RANGE = {-12, 12};
    private static final int[][] X_SHIFT = createRandomShift();
    private static final int[][] Y_SHIFT = createRandomShift();

    private final PolygonRenderer renderer;
    private final Drawable panel;
    private final int perturbation;

    public MeshPolygonTest(Drawable panel, PolygonRenderer renderer, int perturbation) {
        this.panel = panel;
        this.renderer = renderer;
        this.perturbation = perturbation;
        render();
    }

    private void render() {
        int panelHeight = panel.getHeight();
        int panelWidth = panel.getWidth();
        int hMargin = (int)Math.round((double)panelHeight * MARGIN_PERCENT);
        int wMargin = (int)Math.round((double)panelWidth * MARGIN_PERCENT);
        int squareHeight = (panelHeight - hMargin * 2) / (NUM_POINT - 1);
        int squareWidth = (panelWidth - wMargin * 2) / (NUM_POINT - 1);

        Random rnd = new Random();
        Vertex3D[][] vertices = new Vertex3D[NUM_POINT][NUM_POINT];

        int x;
        int y;

        y = hMargin;
        for(int i = 0; i < NUM_POINT; i++) {
            x = wMargin;
            for(int j = 0; j < NUM_POINT; j++) {
                int x_rnd = 0;
                int y_rnd = 0;
                if(perturbation == USE_PERTURBATION) {
//                    x_rnd = rnd.nextInt(SHIFT_RANGE[1] - SHIFT_RANGE[0] + 1) + SHIFT_RANGE[0];
//                    y_rnd = rnd.nextInt(SHIFT_RANGE[1] - SHIFT_RANGE[0] + 1) + SHIFT_RANGE[0];
                    x_rnd = X_SHIFT[i][j];
                    y_rnd = Y_SHIFT[i][j];
                }
                vertices[i][j] = new Vertex3D(x + x_rnd,y + y_rnd, 0, Color.WHITE);
                x += squareWidth;
            }
            y += squareHeight;
        }

        Color color;
        Vertex3D a, b, c, d;

        for(int i = 0; i < NUM_POINT - 1; i++) {
            for(int j = 0; j < NUM_POINT - 1; j++) {
                //connect (min x, min y) to (max x, max y)

                color = Color.random(rnd);
                a = vertices[i][j].replaceColor(color);
                b = vertices[i+1][j].replaceColor(color);
                d = vertices[i+1][j+1].replaceColor(color);

                // Polygon abd
                Polygon p1 = Polygon.makeEnsuringCounterClockwise(a, b, d);
                renderer.drawPolygon(p1, panel);

                color = Color.random(rnd);
                a = vertices[i][j].replaceColor(color);
                c = vertices[i][j+1].replaceColor(color);
                d = vertices[i+1][j+1].replaceColor(color);

                // Polygon acd
                Polygon p2 = Polygon.makeEnsuringCounterClockwise(a, c, d);
                renderer.drawPolygon(p2, panel);
            }
        }
    }

    private static int[][] createRandomShift() {
        Random rnd = new Random();
        int[][] array = new int[NUM_POINT][NUM_POINT];
        for(int i = 0; i < NUM_POINT; i++) {
            for(int j = 0; j < NUM_POINT; j++) {
                int rnd_shift = rnd.nextInt(SHIFT_RANGE[1] - SHIFT_RANGE[0] + 1) + SHIFT_RANGE[0];
                array[i][j] = rnd_shift;
            }
        }
        return array;
    }
}
