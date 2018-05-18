package line;

import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;

// switches endpoints so that it only has to deal with the first four octants.

public class AnyOctantLineRenderer implements LineRenderer {
	private LineRenderer singleOctantRenderer;

	public AnyOctantLineRenderer(LineRenderer singleOctantRenderer) {
		super();
		this.singleOctantRenderer = singleOctantRenderer;
	}

	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		if(inOctantsItoIV(p1, p2)) {
			drawUpwardsLine(p1, p2, drawable);
		}
		else {
			drawUpwardsLine(p2, p1, drawable);
		}
	}

//	public ArrayList<Vertex3D> getLine(Vertex3D p1, Vertex3D p2) {
//		if(inOctantsItoIV(p1, p2)) {
//			return getLineHelper(p1, p2);
//		}
//		else {
//			return getLineHelper(p2, p1);
//		}
//	}

	public boolean inOctantsItoIV(Vertex3D p1, Vertex3D p2) {
		return p2.getY() > p1.getY();
	}

	public void drawUpwardsLine(Vertex3D q1, Vertex3D q2, Drawable drawable) {
		Octant octant = findOctant(q1, q2);
		Vertex3D transformedQ1 = octant.toOctant1(q1);
		Vertex3D transformedQ2 = octant.toOctant1(q2);
		singleOctantRenderer.drawLine(transformedQ1, transformedQ2, octant.invertingDrawable(drawable));
	}
//
//	public ArrayList<Vertex3D> getLineHelper(Vertex3D q1, Vertex3D q2) {
//		Octant octant = findOctant(q1, q2);
//		Vertex3D transformedQ1 = octant.toOctant1(q1);
//		Vertex3D transformedQ2 = octant.toOctant1(q2);
//		ArrayList<Vertex3D> linePixel = singleOctantRenderer.getLine(transformedQ1, transformedQ2);
//
//		for(int i = 0; i < linePixel.size(); i++) {
//			int x = linePixel[i].getIntX();
//			int y = linePixel[i].getIntY();
//			x = octant.inverseX(x, y);
//			y = octant.inverseY(x, y);
//		}
//	}
	

	private Octant findOctant(Vertex q1, Vertex q2) {
		int vx = q2.getIntX() - q1.getIntX();
		int vy = q2.getIntY() - q1.getIntY();
		return Octant.octantForVector(vx, vy);
	}
}
