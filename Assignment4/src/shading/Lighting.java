package shading;

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
    Transformation worldToLight;

    public Lighting(double r, double g, double b, double A, double B, Color ambient, Transformation worldToLight) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.A = A;
        this.B = B;
        this.ambient = ambient;
        this.worldToLight = worldToLight;
    }
}
