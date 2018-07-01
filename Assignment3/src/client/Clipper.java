package client;


import geometry.Line;
import geometry.Point3DH;
import geometry.Vertex;
import geometry.Vertex3D;
import polygon.Polygon;

import java.util.ArrayList;

// For assignment 3
public class Clipper {

    private double leftX;
    private double botY;
    private double rightX;
    private double topY;


    private double nearZ;
    private double farZ;

    public Clipper(double leftX, double botY, double rightX, double topY, double nearZ, double farZ) {
        this.leftX = leftX;
        this.botY = botY;
        this.rightX = rightX;
        this.topY = topY;
        this.nearZ = nearZ;
        this.farZ = farZ;
    }

    public Line clipXY(Line line) {
        Line ret = line;
        ret = clipXHelper(ret, leftX);
        ret = clipXHelper(ret, rightX);
        ret = clipYHelper(ret, topY);
        ret = clipYHelper(ret, botY);
        return ret;
    }

    public Polygon clipX(Polygon polygon) {
        boolean allLessThanLeft = true;
        boolean allGreaterThanRight = true;

        for(int i = 0; i < polygon.length(); i++) {
            Vertex3D v = polygon.get(i);
            if(i == 0) {
                allLessThanLeft = (v.getX() < leftX);
                allGreaterThanRight =  (v.getX() > rightX);
            } else {
                allLessThanLeft = allLessThanLeft && (v.getX() < leftX);
                allGreaterThanRight = allGreaterThanRight && (v.getX() > rightX);
            }
        }

        if(allLessThanLeft || allGreaterThanRight) {
            return null;
        }

        Polygon p = polygon;
        p = clipXHelper(p, leftX);
        p = clipXHelper(p, rightX);
        return p;
    }


    public Polygon clipY(Polygon polygon) {
        boolean allLessThanBot = true;
        boolean allGreaterThanTop = true;

        for(int i = 0; i < polygon.length(); i++) {
            Vertex3D v = polygon.get(i);
            if(i == 0) {
                allLessThanBot = (v.getY() < botY);
                allGreaterThanTop = (v.getY() > topY);
            } else {
                allLessThanBot = allLessThanBot && (v.getY() < botY);
                allGreaterThanTop = allGreaterThanTop && (v.getY() > topY);
            }
        }

        if(allLessThanBot || allGreaterThanTop) {
            return null;
        }

        Polygon p = polygon;
        p = clipYHelper(p, topY);
        p = clipYHelper(p, botY);
        return p;
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

        if ((v1.getZ() > nearZ && v2.getZ() > nearZ && v3.getZ() > nearZ) ||
            (v1.getZ() < farZ && v2.getZ() < farZ && v3.getZ() < farZ)) {
            return null;
        }

        Polygon p = polygon;
        p = clipZHelper(p, nearZ);
        p = clipZHelper(p, farZ);
        return p;
    }

    private Line clipXHelper(Line line, double plane) {
        if (line == null) {
            return null;
        }

        Vertex3D v1 = line.p1;
        Vertex3D v2 = line.p2;
        v1 = v1.euclidean();
        v2 = v2.euclidean();

        double x1 = v1.getX();
        double x2 = v2.getX();
        boolean isFlipped;

        Line ret;
        if ((x1 < leftX && x2 < leftX) || (x1 > rightX && x2 > rightX)) {
            // Don't render
            ret = null;
        } else {
            Vertex3D left;
            Vertex3D right;

            // Note: we want counterclockwise
            left = (x1 > x2) ? v2 : v1;
            right = (x1 > x2) ? v1 : v2;
            isFlipped = (left == v2);

            x1 = left.getX();
            x2 = right.getX();

            double deltaX;
            double deltaY;
            double deltaZ;
            double deltaXX;
            double deltaYY;
            double deltaZZ;

            if (plane == leftX || plane == rightX) {
                deltaX = right.getX() - left.getX();
                deltaY = right.getY() - left.getY();
                deltaZ = right.getZ() - left.getZ();
                if (plane == leftX && x1 < leftX) {
                    deltaXX =  leftX - x1;
                    deltaYY = (deltaY / deltaX) * deltaXX;
                    deltaZZ = (deltaZ / deltaX) * deltaXX;
                    left = left.replacePoint(new Point3DH(leftX, left.getY() + deltaYY, left.getZ() + deltaZZ));
                }

                if (plane == rightX && x2 > rightX) {
                    deltaXX =  rightX - x1;
                    deltaYY = (deltaY / deltaX) * deltaXX;
                    deltaZZ = (deltaZ / deltaX) * deltaXX;
                    right = right.replacePoint(new Point3DH(rightX, left.getY() + deltaYY, left.getZ() + deltaZZ));
                }
            }

            // Maintaining orders for polygon clipping....
            if (isFlipped) {
                ret = new Line(right, left);
            } else {
                ret = new Line(left, right);
            }
        }
        return ret;
    }

    private Line clipYHelper(Line line, double plane) {
        if (line == null) {
            return null;
        }

        Vertex3D v1 = line.p1;
        Vertex3D v2 = line.p2;
        v1 = v1.euclidean();
        v2 = v2.euclidean();

        double y1 = v1.getY();
        double y2 = v2.getY();
        boolean isFlipped;

        Line ret;
        if ((y1 < botY && y2 < botY) || (y1 > topY && y2 > topY)) {
            // Don't render
            ret = null;
        } else {
            Vertex3D first;
            Vertex3D second;

            // Note: we want counterclockwise
            first = (y1 > y2) ? v2 : v1;
            second = (y1 > y2) ? v1 : v2;
            isFlipped = (first == v2);

            y1 = first.getY();
            y2 = second.getY();

            double deltaX;
            double deltaY;
            double deltaZ;

            double deltaXX;
            double deltaYY;
            double deltaZZ;

            if (plane == botY || plane == topY) {
                deltaX = second.getX() - first.getX();
                deltaY = second.getY() - first.getY();
                deltaZ = second.getZ() - first.getZ();

                if (plane == botY && y1 < botY) {
                    deltaYY =  botY - y1;
                    deltaXX = (deltaX / deltaY) * deltaYY;
                    deltaZZ = (deltaZ / deltaY) * deltaYY;
                    first = first.replacePoint(new Point3DH(first.getX() + deltaXX, botY, first.getZ() + deltaZZ));
                }

                if (plane == topY && y2 > topY) {
                    deltaYY =  topY - y1;
                    deltaXX = (deltaX / deltaY) * deltaYY;
                    deltaZZ = (deltaZ / deltaY) * deltaYY;
                    second = second.replacePoint(new Point3DH(first.getX() + deltaXX, topY, first.getZ() + deltaZZ));
                }
            }

            // Maintaining orders for polygon clipping....
            if (isFlipped) {
                ret = new Line(second, first);
            } else {
                ret = new Line(first, second);
            }
        }
        return ret;
    }

    private Line clipZHelper(Line line, double plane) {
        if (line == null) {
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
        if ((z1 < farZ && z2 < farZ) || (z1 > nearZ && z2 > nearZ)) {
            // dont render
            ret = null;
        } else {
            Vertex3D near;
            Vertex3D far;

            // Note: looking down at negative Z
            near = (z1 < z2) ? v2 : v1;
            far = (z1 < z2) ? v1 : v2;
            isFlipped = (v2 == near);

            z1 = near.getZ();
            z2 = far.getZ();

            // clip far plane
            if (plane == nearZ || plane == farZ) {
                double deltaZ;
                double deltaZZ;

                double deltaX;
                double deltaY;
                double deltaXX;
                double deltaYY;

                deltaX = far.getX() - near.getX();
                deltaY = far.getY() - near.getY();
                deltaZ = z2 - z1;
                if (plane == farZ && z2 < farZ) {
                    deltaZZ = farZ - z1;
                    deltaXX = (deltaX / deltaZ) * deltaZZ;
                    deltaYY = (deltaY / deltaZ) * deltaZZ;
                    far = far.replacePoint(new Point3DH(near.getX() + deltaXX, near.getY() + deltaYY, farZ));
                }

                if (plane == nearZ && z1 > nearZ) {
                    deltaZZ = nearZ - z1;
                    deltaXX = (deltaX / deltaZ) * deltaZZ;
                    deltaYY = (deltaY / deltaZ) * deltaZZ;
                    near = near.replacePoint(new Point3DH(near.getX() + deltaXX, near.getY() + deltaYY, nearZ));
                }
            }

            // Maintaining orders for polygon clipping....
            if (isFlipped) {
                ret = new Line(far, near);
            } else {
                ret = new Line(near, far);
            }
        }
        return ret;
    }

    private Polygon clipXHelper(Polygon polygon, double plane) {
        ArrayList<Vertex3D> vertices = new ArrayList<>();

        Vertex3D v1;
        Vertex3D v2;
        Line line;
        String state1 = "";
        String state2 = "";

        for (int i = 0; i < polygon.length(); i++) {
            v1 = polygon.get(i);
            v2 = polygon.get(i + 1);
            if (plane == rightX) {
                state1 = (v1.getX() < rightX) ? "in" : "out";
                state2 = (v2.getX() < rightX) ? "in" : "out";
            } else if (plane == leftX) {
                state1 = (v1.getX() > leftX) ? "in" : "out";
                state2 = (v2.getX() > leftX) ? "in" : "out";
            }

            switch (state1 + "_" + state2) {
                case "in_in":
                    vertices.add(v2);
                    break;
                case "in_out":
                    line = new Line(v1, v2);
                    line = clipXHelper(line, plane);
                    vertices.add(line.p2);
                    break;
                case "out_out":
                    // skip
                    break;
                case "out_in":
                    line = new Line(v1, v2);
                    line = clipXHelper(line, plane);
                    vertices.add(line.p1);
                    vertices.add(line.p2);
                    break;
            }
        }
        return Polygon.make(vertices.toArray(new Vertex3D[vertices.size()]));
    }

    private Polygon clipYHelper(Polygon polygon, double plane) {
        ArrayList<Vertex3D> vertices = new ArrayList<>();

        Vertex3D v1;
        Vertex3D v2;
        Line line;
        String state1 = "";
        String state2 = "";

        for (int i = 0; i < polygon.length(); i++) {
            v1 = polygon.get(i);
            v2 = polygon.get(i + 1);
            if (plane == topY) {
                state1 = (v1.getY() < topY) ? "in" : "out";
                state2 = (v2.getY() < topY) ? "in" : "out";
            } else if (plane == botY) {
                state1 = (v1.getY() > botY) ? "in" : "out";
                state2 = (v2.getY() > botY) ? "in" : "out";
            }

            switch (state1 + "_" + state2) {
                case "in_in":
                    vertices.add(v2);
                    break;
                case "in_out":
                    line = new Line(v1, v2);
                    line = clipYHelper(line, plane);
                    vertices.add(line.p2);
                    break;
                case "out_out":
                    // skip
                    break;
                case "out_in":
                    line = new Line(v1, v2);
                    line = clipYHelper(line, plane);
                    vertices.add(line.p1);
                    vertices.add(line.p2);
                    break;
            }
        }
        return Polygon.make(vertices.toArray(new Vertex3D[vertices.size()]));
    }

    private Polygon clipZHelper(Polygon polygon, double plane) {
        ArrayList<Vertex3D> vertices = new ArrayList<>();

        Vertex3D v1;
        Vertex3D v2;
        Line line;
        String state1 = "";
        String state2 = "";

        for (int i = 0; i < polygon.length(); i++) {
            v1 = polygon.get(i);
            v2 = polygon.get(i + 1);
            if (plane == nearZ) {
                state1 = (v1.getZ() < nearZ) ? "in" : "out";
                state2 = (v2.getZ() < nearZ) ? "in" : "out";
            } else if (plane == farZ) {
                state1 = (v1.getZ() > farZ) ? "in" : "out";
                state2 = (v2.getZ() > farZ) ? "in" : "out";
            }

            switch (state1 + "_" + state2) {
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
