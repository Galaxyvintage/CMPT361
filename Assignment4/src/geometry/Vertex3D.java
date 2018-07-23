package geometry;

import windowing.graphics.Color;

public class Vertex3D implements Vertex {

	protected Point3DH point;
	protected Point3DH cameraPoint;
	protected Color color;
	private boolean hasNormal = false;
	protected Point3DH normal;
	
	public Vertex3D(Point3DH point, Color color) {
		super();
		this.point = point;
		this.cameraPoint = point;
		this.color = color;
	}

	public Vertex3D(Point3DH point, Point3DH cameraPoint, Color color) {
		super();
		this.point = point;
		this.cameraPoint = cameraPoint;
		this.color = color;
	}

	public Vertex3D(double x, double y, double z, Color color) {
		this(new Point3DH(x, y, z), color);
	}

	public Vertex3D() {
	}

	public double getX() {
		return point.getX();
	}
	public double getY() {
		return point.getY();
	}
	public double getZ() {
		return point.getZ();
	}
	public double getCameraSpaceZ() {
		return getZ();
	}
	public Point getPoint() {
		return point;
	}
	public Point3DH getPoint3D() {
		return point;
	}
	public Point3DH getCameraPoint() {return cameraPoint;}
	
	public int getIntX() {
		return (int) Math.round(getX());
	}
	public int getIntY() {
		return (int) Math.round(getY());
	}
	public int getIntZ() {
		return (int) Math.round(getZ());
	}
	
	public Color getColor() {
		return color;
	}

	public Vertex3D rounded() {
		return new Vertex3D(point.round(), color);
	}
	public Vertex3D add(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point.add(other3D.getPoint()),
				            color.add(other3D.getColor()));
	}
	public Vertex3D subtract(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point.subtract(other3D.getPoint()),
				            color.subtract(other3D.getColor()));
	}
	public Vertex3D scale(double scalar) {
		return new Vertex3D(point.scale(scalar),
				            color.scale(scalar));
	}
	public Vertex3D cross(Vertex3D other) {
	    double x = point.getY() * other.getZ() - point.getZ() * other.getY();
	    double y = point.getZ() * other.getX() - point.getX() * other.getZ();
	    double z = point.getX() * other.getY() - point.getY() * other.getX();
	    return new Vertex3D(new Point3DH(x, y, z), color);
    }

    public boolean hasNormal() {
        return hasNormal;
    }

    public void setNormal(Point3DH normal) {
		this.normal = normal;
		hasNormal = true;
	}

	public Point3DH getNormal() {
	    return normal;
    }

	public Vertex3D replacePoint(Point3DH newPoint) {
		Vertex3D v = new Vertex3D(newPoint, cameraPoint, color);
		if(this.hasNormal) {
			v.setNormal(this.normal);
		}
		return v;
	}
	public Vertex3D replaceColor(Color newColor) {
		Vertex3D v = new Vertex3D(point, cameraPoint, newColor);
		if(this.hasNormal) {
			v.setNormal(this.normal);
		}
		return v;
	}

	public Vertex3D replaceCameraPoint(Point3DH newCameraPoint) {
		Vertex3D v = new Vertex3D(point, newCameraPoint, color);
		if(this.hasNormal) {
			v.setNormal(this.normal);
		}
		return v;
    }

	public Vertex3D euclidean() {
		Point3DH euclidean = getPoint3D().euclidean();
		return replacePoint(euclidean);
	}
	
	public String toString() {
		return "(" + getX() + ", " + getY() + ", " + getZ() + ", " + getColor().toIntString() + ")";
	}
	public String toIntString() {
		return "(" + getIntX() + ", " + getIntY() + getIntZ() + ", " + ", " + getColor().toIntString() + ")";
	}

	public Vertex3D normalize() {
		Vertex3D v = new Vertex3D(point.normalize(), cameraPoint, color);
		if(this.hasNormal) {
			v.setNormal(this.normal);
		}
		return v;
	}
}
