package polygon;

import client.interpreter.SimpInterpreter;
import shading.FaceShader;
import shading.PixelShader;
import shading.VertexShader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public interface PolygonRenderer {
	// assumes polygon is ccw.
	public void drawPolygon(Polygon polygon, Drawable drawable,
							FaceShader faceShader,
							VertexShader vertexShader,
							PixelShader pixelShade,
							SimpInterpreter.ShadingStyle shadingStyle);

	default public void drawPolygon(Polygon polygon, Drawable panel) {
		drawPolygon(polygon, panel, ply->ply, (p,v)->v, (p,v)->Color.WHITE, SimpInterpreter.ShadingStyle.FLAT);
	};
}
