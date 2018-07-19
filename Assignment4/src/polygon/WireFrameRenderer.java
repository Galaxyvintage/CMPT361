package polygon;


import geometry.Vertex;
import geometry.Vertex3D;
import line.LineRenderer;
import shading.FaceShader;
import windowing.drawable.Drawable;
import shading.Shader;


public class WireFrameRenderer implements PolygonRenderer {
    private LineRenderer renderer;
    private WireFrameRenderer(LineRenderer renderer) {
        this.renderer = renderer;
    }

    public static WireFrameRenderer make(LineRenderer renderer) { return new WireFrameRenderer(renderer);}
    public void drawPolygon(Polygon polygon, Drawable drawable, FaceShader faceShader, Shader ambientShader){
        for(int i = 0; i < polygon.numVertices; i++) {
            Vertex3D p1 = polygon.get(i);
            Vertex3D p2 = polygon.get(i + 1);
            renderer.drawLine(p1, p2, drawable);
        }
    }
}
