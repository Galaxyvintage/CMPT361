package model;

import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Cylinder {
    private static final int NUM_RAYS = 90;
    private static final int LEVELS = 10;
    private static final int RADIUS = 1;
    private Vertex3D center = new Vertex3D(0, 0, 0, Color.WHITE);
    private ArrayList<ArrayList<Vertex3D>> levelVertices;
    private ArrayList<Polygon> faces;

    public Cylinder() {
        levelVertices = new ArrayList<>();
        faces = new ArrayList<>();
        initVertices();
        initFaces();
        outputFaces();
    }

    public void outputFaces() {
        try {
            String path = "simp/cylinder.simp";
            File outputFile = new File(path);
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            PrintWriter writer = new PrintWriter(path);

            for (Polygon p : faces) {
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

    private void initVertices() {
        double angleDifference = (2.0 * Math.PI) / NUM_RAYS;
        double angle = 0.0;
        for(int i = 0; i < LEVELS; i++) {
            // Raise center by 1 level
            center = new Vertex3D(0, i, 0, Color.WHITE);

            ArrayList<Vertex3D> vertices = new ArrayList<>();
            for(int j = 0; j < NUM_RAYS; j++) {
                Vertex3D v = radialPoint(RADIUS, angle);
                vertices.add(v);
                angle += angleDifference;
            }
            levelVertices.add(vertices);
        }
    }

    private void initFaces() {
        for(int i = 0; i < levelVertices.size() - 1; i++) {
            ArrayList<Vertex3D> currentLevel = levelVertices.get(i);
            ArrayList<Vertex3D> nextLevel = levelVertices.get(i+1);

            Vertex3D currentCenter = new Vertex3D(0, i, 0, Color.WHITE);
            Vertex3D nextCenter = new Vertex3D(0, i+1, 0, Color.WHITE);

            for(int j = 0; j < currentLevel.size() - 1; j++) {
                /*
                      +---+
                     /|   |
                    / +---|
                   / /    |
                  / /     |
              u1 +-------+  u1
                 |/       |
                 +--------+
              v1             v2
                */

                Vertex3D v1 = currentLevel.get(j);
                Vertex3D v2 = currentLevel.get(j+1);
                Vertex3D u1 = nextLevel.get(j);
                Vertex3D u2 = nextLevel.get(j+1);

                Polygon p1 = Polygon.make(u1, v1, v2);
                Polygon p2 = Polygon.make(u1, v2, u2);
                Polygon p3 = Polygon.make(currentCenter, v1, v2);
                Polygon p4 = Polygon.make(nextCenter, u1, u2);
                faces.add(p1);
                faces.add(p2);
                faces.add(p3);
                faces.add(p4);
            }
        }

    }

    private Vertex3D radialPoint(double radius, double angle) {
        double x = center.getX() + radius * Math.cos(angle);
        double z = center.getZ() + radius * Math.sin(angle);
        return new Vertex3D(x, center.getY(), z, Color.WHITE);
    }
}