package shading;

import geometry.Point3DH;
import geometry.Transformation;
import geometry.Vertex3D;
import windowing.graphics.Color;

public class Lighting {
    // intensities
    Color intensity;
    double A;
    double B;
    Color ambient;
    Point3DH cameraSpaceLocation
            ;

    public Lighting(Color intensity, double A, double B, Color ambient, Point3DH cameraSpaceLocation) {
        this.intensity = intensity;
        this.A = A;
        this.B = B;
        this.ambient = ambient;
        this.cameraSpaceLocation = cameraSpaceLocation;
    }

    public Color Light(Vertex3D cameraSpacePoint, Color kDiffuse, double kSpecular, double specularExponent) {
        return null;
    }
}
