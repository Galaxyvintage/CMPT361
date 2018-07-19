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
        Point3DH l = cameraSpaceLocation.normalize();
        Point3DH n = cameraSpacePoint.getNormal().normalize();
        Point3DH r = n.scale(2 * n.dot(l)).subtract(l);

        double NL = n.dot(l);
        double VR = cameraSpacePoint.getPoint3D().normalize().dot(r);
        Color kDNL = kDiffuse.scale(NL);
        double kSVRP = kSpecular * Math.pow(VR, specularExponent);
        Color color = new Color(kDNL.getR() + kSVRP, kDNL.getG() + kSVRP, kDNL.getB() + kSVRP);
        return intensity.scale(fatti).multiply(color);
    }
}
