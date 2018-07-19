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

    public Color light(Vertex3D cameraSpacePoint, Color kDiffuse, double kSpecular, double specularExponent) {

        double fatti =  1 /  (A + B * cameraSpaceLocation.distance(cameraSpacePoint.getPoint3D()));
        Point3DH L = cameraSpaceLocation.normalize();
        Point3DH N = cameraSpacePoint.normalize().getPoint3D();


        //        intensity.multiply(fatti).multiply()

        return null;
    }
}
