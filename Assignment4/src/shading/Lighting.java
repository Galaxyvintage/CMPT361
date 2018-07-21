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
        Point3DH l = cameraSpaceLocation.subtract(cameraSpacePoint.getPoint3D());
        l = l.euclidean().normalize();
        Point3DH n = cameraSpacePoint.getNormal();
        n = n.euclidean().normalize();

        double NL = n.dot(l);
        if (NL <= 0) {
            NL = 0;
        }

        Point3DH r = (n.scale(-2 * NL)).add(l).normalize();

        double VR = cameraSpacePoint.getPoint3D().normalize().dot(r);
        if (VR <= 0) {
            VR = 0;
        }

        Color color;
        if(NL <= 0) {
            color = Color.BLACK;
        } else {
            Color kDNL = kDiffuse.scale(NL);
            double kSVRP = kSpecular * Math.pow(VR, specularExponent);
            color = new Color(kDNL.getR() + kSVRP, kDNL.getG() + kSVRP, kDNL.getB() + kSVRP);
        }
        double di = Math.abs(NL) / Math.abs(n.norm());
        double fatti =  1 / (A + B * di);
        return (intensity.scale(fatti)).multiply(color);
    }
}
