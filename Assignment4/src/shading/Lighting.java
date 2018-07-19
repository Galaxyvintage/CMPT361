package shading;

import geometry.Point3DH;
import geometry.Transformation;
import windowing.graphics.Color;

public class Lighting {
    // intensities
    double r;
    double g;
    double b;
    double A;
    double B;
    Color ambient;
    Point3DH cameraSpaceLocation
            ;

    public Lighting(double r, double g, double b, double A, double B, Color ambient, Point3DH cameraSpaceLocation) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.A = A;
        this.B = B;
        this.ambient = ambient;
        this.cameraSpaceLocation = cameraSpaceLocation;
    }
}
