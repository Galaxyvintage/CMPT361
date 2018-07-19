package polygon;

import shading.FaceShader;
import shading.Shader;
import windowing.drawable.Drawable;

public interface PolygonRenderer {
	// assumes polygon is ccw.
	public void drawPolygon(Polygon polygon, Drawable drawable, FaceShader faceShader, Shader vertexShader);

	default public void drawPolygon(Polygon polygon, Drawable panel) {
		drawPolygon(polygon, panel, p->p,  c -> c);
	};
}
