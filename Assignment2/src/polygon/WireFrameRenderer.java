package polygon;


import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;


public class WireFrameRenderer implements PolygonRenderer {
    private static final int FIRST_VERTEX = 0;
    private static final int SECOND_VERTEX = 1;
    private static final int LEFT = -1;
    private static final int RIGHT = 1;
    private static final int COVERAGE = 1;

    private LineRenderer renderer;

    private WireFrameRenderer(LineRenderer renderer) {
        this.renderer = renderer;
    }

    public static WireFrameRenderer make(LineRenderer renderer) { return new WireFrameRenderer(renderer);}
    public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader){
        for(int i = 0; i < polygon.numVertices; i++) {
            Vertex3D p1 = polygon.get(i);
            Vertex3D p2 = polygon.get(i + 1);
            renderer.drawLine(p1, p2, drawable);
        }
    }
}
