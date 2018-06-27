package client;


import geometry.Line;
import geometry.Point3DH;
import geometry.Vertex3D;
import polygon.Polygon;

import java.util.ArrayList;

// For assignment 3
public class Clipper {

    private double view;
    private double leftX;
    private double rightX;
    private double leftY;
    private double rightY;

    private double nearZ;
    private double farZ;

    public Clipper(double view, double leftX, double rightX, double leftY, double rightY, double nearZ, double farZ) {
        this.view = view;
        this.leftX = leftX;
        this.rightX = rightX;
        this.leftY = leftY;
        this.rightY = rightY;
        // TODO: Need to ask for clarification????
//        this.nearZ = (nearZ > view) ? view : nearZ;
        this.nearZ = nearZ;
        this.farZ = farZ;
    }

    public Line clipZ(Line line) {
        Line ret = line;
        ret = clipZHelper(ret, nearZ);
        ret = clipZHelper(ret, farZ);
        return ret;
    }

    public Polygon clipZ(Polygon polygon) {

        Vertex3D v1;
        Vertex3D v2;
        Vertex3D v3;

        v1 = polygon.get(0);
        v2 = polygon.get(1);
        v3 = polygon.get(2);

        if((v1.getZ() > nearZ && v2.getZ() > nearZ && v3.getZ() > nearZ ) ||
           (v1.getZ() < farZ  && v2.getZ() < farZ  && v3.getZ() < farZ)) {
            return null;
        }

        Polygon p = polygon;
        p = clipZHelper(p, nearZ);
        p = clipZHelper(p, farZ);
        return p;
    }

    private Line clipZHelper(Line line, double plane) {
        if(line == null) {
            return null;
        }

        Vertex3D v1 = line.p1;
        Vertex3D v2 = line.p2;
        v1 = v1.euclidean();
        v2 = v2.euclidean();

        double z1 = v1.getZ();
        double z2 = v2.getZ();
        boolean isFlipped;

        Line ret;
        if((z1 < farZ && z2 < farZ) || (z1 > nearZ && z2 > nearZ)) {
            // dont render
            ret = null;
        } else {
            Vertex3D near;
            Vertex3D far;

            // Note: looking down at negative Z
            near =  (z1 < z2) ? v2 : v1;
            far = (z1 < z2) ? v1 : v2;
            isFlipped = (v2 == near);

            z1 = near.getZ();
            z2 = far.getZ();

            // clip far plane
            if(plane == farZ && z2 < farZ) {
                double deltaZ = z2 - z1;
                double deltaZZ = farZ - z1;

                double deltaX = far.getX() - near.getX();
                double deltaY = far.getY() - near.getY();

                double deltaXX = (deltaX / deltaZ) * deltaZZ;
                double deltaYY = (deltaY / deltaZ) * deltaZZ;
                far = far.replacePoint(new Point3DH(near.getX() + deltaXX, near.getY() + deltaYY, farZ));
                z2 = farZ;
            }

            // clip near plane
            if(plane == nearZ && z1 > nearZ) {
                double deltaZ = z2 - z1;
                double deltaZZ = nearZ - z1;

                double deltaX = far.getX() - near.getX();
                double deltaY = far.getY() - near.getY();

                double deltaXX = (deltaX / deltaZ) * deltaZZ;
                double deltaYY = (deltaY / deltaZ) * deltaZZ;
                near = near.replacePoint(new Point3DH(near.getX() + deltaXX, near.getY() + deltaYY, nearZ));
                z1 = nearZ;
            }

            // Maintaining orders for polygon clipping....
            if (isFlipped) {
                ret = new Line(far, near);
            } else {
                ret = new Line(near, far);
            }
        }
        return ret ;
    }

    private Polygon clipZHelper(Polygon polygon, double plane) {
        ArrayList<Vertex3D> vertices = new ArrayList<>();
        // original vertices
        Vertex3D v1;
        Vertex3D v2;

        // vertices after clipping
        Vertex3D u1;
        Vertex3D u2;

        Line line;
        String state1 = "";
        String state2 = "";

        for(int i = 0; i < polygon.length(); i++) {
            v1 = polygon.get(i);
            v2 = polygon.get(i+1);
            if(plane == nearZ) {
                state1 = (v1.getZ() < nearZ) ? "in" : "out";
                state2 = (v2.getZ() < nearZ) ? "in" : "out";
            } else if (plane == farZ){
                state1 = (v1.getZ() > farZ) ? "in" : "out";
                state2 = (v2.getZ() > farZ) ? "in" : "out";
            }

            switch(state1 + "_" + state2) {
                case "in_in":
                    vertices.add(v2);
                    break;
                case "in_out":
                    line = new Line(v1, v2);
                    line = clipZHelper(line, plane);
                    vertices.add(line.p2);
                    break;
                case "out_out":
                    // skip
                    break;
                case "out_in":
                    line = new Line(v1, v2);
                    line = clipZHelper(line, plane);
                    vertices.add(line.p1);
                    vertices.add(line.p2);
                    break;
            }
        }
        return Polygon.make(vertices.toArray(new Vertex3D[vertices.size()]));
    }



}
