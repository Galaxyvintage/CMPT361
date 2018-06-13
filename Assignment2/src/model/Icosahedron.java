package model;

import geometry.Vertex;
import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/*
References:
http://blog.andreaskahler.com/2009/06/creating-icosphere-mesh-in-code.html
*/
public class Icosahedron {

    private static final int NUM_BASE_VERTICES = 12;
    private static final double TAO = (1.0 + Math.sqrt(5.0)) / 2;
    private ArrayList<Vertex3D> vertices;
    private ArrayList<Polygon> faces;

    public Icosahedron() {
        initVertices();
        initFaces();
        refineFaces();
    }

    public void outputFaces() {
        try {
            String path = "simp/myIcosahedron.simp";
            File outputFile = new File(path);
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            PrintWriter writer = new PrintWriter(path);

            for(Polygon p: faces){
                ArrayList<String> s = new ArrayList<>();
                s.add("polygon");
                s.add(p.get(0).toString());
                s.add(p.get(1).toString());
                s.add(p.get(2).toString());
                String output = String.join(" ", s);
                writer.println(output);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refineFaces() {
        int recursionLevel = 2;
        for ( int i = 0; i < recursionLevel; i++) {
            ArrayList<Polygon> newFaces = new ArrayList<>();
           for(Polygon p: faces) {
               Vertex3D a = getMidPoint(p.get(0), p.get(1));
               Vertex3D b = getMidPoint(p.get(1), p.get(2));
               Vertex3D c = getMidPoint(p.get(2), p.get(0));
               newFaces.add(Polygon.makeEnsuringCounterClockwise(p.get(0), a, c));
               newFaces.add(Polygon.makeEnsuringCounterClockwise(p.get(1), b, a));
               newFaces.add(Polygon.makeEnsuringCounterClockwise(p.get(2), c, b));
               newFaces.add(Polygon.makeEnsuringCounterClockwise(a, b, c));
           }
           faces = newFaces;
        }
    }

    private void initVertices() {
        vertices = new ArrayList<>();
        Vertex3D v;
        v = new Vertex3D(-1, TAO, 0, Color.WHITE);
        vertices.add(v.normalize());
        v = new Vertex3D(1, TAO, 0, Color.WHITE);
        vertices.add(v.normalize());
        v = new Vertex3D(-1, -TAO, 0, Color.WHITE);
        vertices.add(v.normalize());
        v = new Vertex3D(1, -TAO, 0, Color.WHITE);
        vertices.add(v.normalize());
        v = new Vertex3D(0, -1, TAO, Color.WHITE);
        vertices.add(v.normalize());
        v = new Vertex3D(0, 1, TAO, Color.WHITE);
        vertices.add(v.normalize());
        v = new Vertex3D(0, -1, -TAO, Color.WHITE);
        vertices.add(v.normalize());
        v = new Vertex3D(0, 1, -TAO, Color.WHITE);
        vertices.add(v.normalize());
        v = new Vertex3D(TAO, 0, -1, Color.WHITE);
        vertices.add(v.normalize());
        v = new Vertex3D(TAO, 0, 1, Color.WHITE);
        vertices.add(v.normalize());
        v = new Vertex3D(-TAO, 0, -1, Color.WHITE);
        vertices.add(v.normalize());
        v = new Vertex3D(-TAO, 0, 1, Color.WHITE);
        vertices.add(v.normalize());
    }

    private void initFaces() {
        faces = new ArrayList<>();
        Polygon p;
        p = Polygon.makeEnsuringCounterClockwise(vertices.get(0), vertices.get(11), vertices.get(5));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(0), vertices.get(5), vertices.get(1));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(0), vertices.get(1), vertices.get(7));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(0), vertices.get(7), vertices.get(10));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(0), vertices.get(10), vertices.get(11));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(1), vertices.get(5), vertices.get(9));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(5), vertices.get(11), vertices.get(4));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(11), vertices.get(10), vertices.get(2));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(10), vertices.get(7), vertices.get(6));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(7), vertices.get(1), vertices.get(8));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(3), vertices.get(9), vertices.get(4));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(3), vertices.get(4), vertices.get(2));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(3), vertices.get(2), vertices.get(6));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(3), vertices.get(6), vertices.get(8));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(3), vertices.get(8), vertices.get(9));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(4), vertices.get(9), vertices.get(5));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(2), vertices.get(4), vertices.get(11));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(6), vertices.get(2), vertices.get(10));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(8), vertices.get(6), vertices.get(7));
        faces.add(p);

        p = Polygon.makeEnsuringCounterClockwise(vertices.get(9), vertices.get(8), vertices.get(1));
        faces.add(p);
    }

    private Vertex3D getMidPoint(Vertex3D v1,  Vertex3D v2) {

        double midX = (v1.getX() + v2.getX()) / 2.0;
        double midY = (v1.getY() + v2.getY()) / 2.0;
        double midZ = (v1.getZ() + v2.getZ()) / 2.0;
        Color midColor = v1.getColor();
        Vertex3D middle = new Vertex3D(midX, midY, midZ, midColor);
        middle = middle.normalize();
        vertices.add(middle);
        return middle;
    }
}




