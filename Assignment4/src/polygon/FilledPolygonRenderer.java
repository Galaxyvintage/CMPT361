package polygon;

import client.interpreter.SimpInterpreter;
import geometry.Point3DH;

import geometry.Vertex3D;
import shading.FaceShader;
import shading.PixelShader;
import shading.VertexShader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;


import java.util.ArrayList;

public class FilledPolygonRenderer implements PolygonRenderer {
    private static final int FIRST_VERTEX = 0;
    private static final int SECOND_VERTEX = 1;
    private static final int LEFT = -1;
    private static final int RIGHT = 1;
    private static final int COVERAGE = 1;

    public static FilledPolygonRenderer make() { return new FilledPolygonRenderer();}

    // TODO: Need to optimize and clean up
    public void drawPolygon(Polygon polygon,
                            Drawable drawable,
                            FaceShader faceShader,
                            VertexShader vertexShader,
                            PixelShader pixelShader,
                            SimpInterpreter.ShadingStyle shadingStyle) {

        ArrayList<Polygon> polygons = polygon.triangulate();

        for (int k = 0; k < polygons.size(); k++) {
            Polygon currentPolygon = polygons.get(k);
            ArrayList<Vertex3D> vertices = new ArrayList<>();
            for(int i = 0; i < currentPolygon.length(); i++) {
                Vertex3D v = currentPolygon.get(i);
                double Z = v.getZ();
                v = v.replacePoint(new Point3DH(v.getX(),
                                                v.getY(),
                                                1/Z));
                vertices.add(v);
            }

            currentPolygon = Polygon.make(vertices.toArray(new Vertex3D[vertices.size()]));
            currentPolygon = faceShader.shade(currentPolygon);
            Chain LChain = currentPolygon.leftChain();
            Chain RChain = currentPolygon.rightChain();

            int LLength = LChain.length();
            int RLength = RChain.length();

            Vertex3D LTopVertex = LChain.get(FIRST_VERTEX);
            Vertex3D LBotVertex = LChain.get(LLength - 1);
            Vertex3D RTopVertex = RChain.get(FIRST_VERTEX);
            Vertex3D RBotVertex = RChain.get(RLength - 1);
            Vertex3D MidVertex;

            int MidVertexSide;
            // Finding which vertex is the mid vertex
            if (LLength > RLength) {
                MidVertexSide = LEFT;
                MidVertex = LChain.get(SECOND_VERTEX);
            } else if (RLength > LLength) {
                MidVertexSide = RIGHT;
                MidVertex = RChain.get(SECOND_VERTEX);
            } else {
                MidVertexSide = LEFT;
                MidVertex = LTopVertex;
            }

            LTopVertex = vertexShader.shade(currentPolygon, LTopVertex);
            LBotVertex = vertexShader.shade(currentPolygon, LBotVertex);
            RTopVertex = vertexShader.shade(currentPolygon, RTopVertex);
            RBotVertex = vertexShader.shade(currentPolygon, RBotVertex);
            MidVertex = vertexShader.shade(currentPolygon, MidVertex);


            double LSlope;
            double RSlope;
            double LZSlope;
            double RZSlope;

            double LDeltaX, LDeltaY, LDeltaZ;
            double RDeltaX, RDeltaY, RDeltaZ;

            // RGB
            double LDeltaR, LDeltaG, LDeltaB;
            double RDeltaR, RDeltaG, RDeltaB;

            double LRedSlope, LGreenSlope, LBlueSlope;
            double RRedSlope, RGreenSlope, RBlueSlope;

            // Normal
            double LDeltaNX, LDeltaNY, LDeltaNZ;
            double RDeltaNX, RDeltaNY, RDeltaNZ;

            double LNXSlope, LNYSlope, LNZSlope;
            double RNXSlope, RNYSlope, RNZSlope;


            // Camera space
            double LDeltaCSX, LDeltaCSY, LDeltaCSZ;
            double RDeltaCSX, RDeltaCSY, RDeltaCSZ;

            double LCSXSlope, LCSYSlope, LCSZSlope;
            double RCSXSlope, RCSYSlope, RCSZSlope;


            // Mid to Bottom (Switching DDA)
            double DeltaX, DeltaY, DeltaZ;
            double DeltaR, DeltaG, DeltaB;
            double DeltaNX, DeltaNY, DeltaNZ;
            double DeltaCSX, DeltaCSY, DeltaCSZ;


            double LX = LTopVertex.getIntX();
            double RX = RTopVertex.getIntX();
            double LZ = LTopVertex.getZ();
            double RZ = LTopVertex.getZ();

            double LRed = 1; double LGreen = 1; double LBlue = 1;
            double RRed = 1; double RGreen = 1; double RBlue = 1;

            if(LTopVertex.interpolants.contains(Vertex3D.Interpolant.VERTEX_COLOR)) {
                LRed = LTopVertex.getColor().getR();
                LGreen = LTopVertex.getColor().getG();
                LBlue = LTopVertex.getColor().getB();
                RRed = RTopVertex.getColor().getR();
                RGreen = RTopVertex.getColor().getG();
                RBlue = RTopVertex.getColor().getB();
            }

            double LNX = 0; double LNY = 0; double LNZ = 0;
            double RNX = 0; double RNY = 0; double RNZ = 0;
            if(LTopVertex.interpolants.contains(Vertex3D.Interpolant.NORMAL)) {
                LNX = LTopVertex.getNormal().getX();
                LNY = LTopVertex.getNormal().getY();
                LNZ = LTopVertex.getNormal().getZ();

                RNX = RTopVertex.getNormal().getX();
                RNY = RTopVertex.getNormal().getY();
                RNZ = RTopVertex.getNormal().getZ();
            }

            double LCSX = 0; double LCSY = 0; double LCSZ = 0;
            double RCSX = 0; double RCSY = 0; double RCSZ = 0;
            if(LTopVertex.interpolants.contains(Vertex3D.Interpolant.CAMERASPACE)) {
                LCSX = LTopVertex.getCameraPoint().getX();
                LCSY = LTopVertex.getCameraPoint().getY();
                LCSZ = LTopVertex.getCameraPoint().getZ();

                RCSX = RTopVertex.getCameraPoint().getX();
                RCSY = RTopVertex.getCameraPoint().getY();
                RCSZ = RTopVertex.getCameraPoint().getZ();
            }

            boolean isBotHorizontal = false;
            Vertex3D a, b, c;

            if (MidVertexSide == LEFT) {
                a = MidVertex;
                b = RBotVertex;
                c = LBotVertex;
                if (MidVertex.getIntY() == RBotVertex.getIntY()) {
                    isBotHorizontal = true;
                }

            } else {
                a = LBotVertex;
                b = MidVertex;
                c = RBotVertex;
                if (MidVertex.getIntY() == LBotVertex.getIntY()) {
                    isBotHorizontal = true;
                }
            }

            LDeltaX = a.getIntX() - LTopVertex.getIntX();
            LDeltaY = a.getIntY() - LTopVertex.getIntY();
            LDeltaZ = a.getZ() - LTopVertex.getZ();


            RDeltaX = b.getIntX() - RTopVertex.getIntX();
            RDeltaY = b.getIntY() - RTopVertex.getIntY();
            RDeltaZ = b.getZ() - RTopVertex.getZ();


            DeltaX = c.getIntX() - MidVertex.getIntX();
            DeltaY = c.getIntY() - MidVertex.getIntY();
            DeltaZ = c.getZ() - MidVertex.getZ();

            if(LTopVertex.interpolants.contains(Vertex3D.Interpolant.VERTEX_COLOR)) {
                LDeltaR = a.getColor().getR() - LTopVertex.getColor().getR();
                LDeltaG = a.getColor().getG() - LTopVertex.getColor().getG();
                LDeltaB = a.getColor().getB() - LTopVertex.getColor().getB();
                RDeltaR = b.getColor().getR() - RTopVertex.getColor().getR();
                RDeltaG = b.getColor().getG() - RTopVertex.getColor().getG();
                RDeltaB = b.getColor().getB() - RTopVertex.getColor().getB();
                DeltaR = c.getColor().getR() - MidVertex.getColor().getR();
                DeltaG = c.getColor().getG() - MidVertex.getColor().getG();
                DeltaB = c.getColor().getB() - MidVertex.getColor().getB();
            } else {
                LDeltaR = 0;
                LDeltaG = 0;
                LDeltaB = 0;
                RDeltaR = 0;
                RDeltaG = 0;
                RDeltaB = 0;
                DeltaR = 0;
                DeltaG = 0;
                DeltaB = 0;
            }

            if(LTopVertex.interpolants.contains(Vertex3D.Interpolant.NORMAL)) {
                LDeltaNX = a.getNormal().getX() - LTopVertex.getNormal().getX();
                LDeltaNY = a.getNormal().getY() - LTopVertex.getNormal().getY();
                LDeltaNZ = a.getNormal().getZ() - LTopVertex.getNormal().getZ();

                RDeltaNX = b.getNormal().getX() - RTopVertex.getNormal().getX();
                RDeltaNY = b.getNormal().getY() - RTopVertex.getNormal().getY();
                RDeltaNZ = b.getNormal().getZ() - RTopVertex.getNormal().getZ();

                DeltaNX = c.getNormal().getX() - MidVertex.getNormal().getX();
                DeltaNY = c.getNormal().getY() - MidVertex.getNormal().getY();
                DeltaNZ = c.getNormal().getZ() - MidVertex.getNormal().getZ();
            } else {
                LDeltaNX = 0;
                LDeltaNY = 0;
                LDeltaNZ = 0;
                RDeltaNX = 0;
                RDeltaNY = 0;
                RDeltaNZ = 0;
                DeltaNX = 0;
                DeltaNY = 0;
                DeltaNZ = 0;
            }


            if(LTopVertex.interpolants.contains(Vertex3D.Interpolant.CAMERASPACE)) {
                LDeltaCSX = a.getCameraPoint().getX() - LTopVertex.getCameraPoint().getX();
                LDeltaCSY = a.getCameraPoint().getY() - LTopVertex.getCameraPoint().getY();
                LDeltaCSZ = a.getCameraPoint().getZ() - LTopVertex.getCameraPoint().getZ();

                RDeltaCSX = b.getCameraPoint().getX() - RTopVertex.getCameraPoint().getX();
                RDeltaCSY = b.getCameraPoint().getY() - RTopVertex.getCameraPoint().getY();
                RDeltaCSZ = b.getCameraPoint().getZ() - RTopVertex.getCameraPoint().getZ();

                DeltaCSX = c.getCameraPoint().getX() - MidVertex.getCameraPoint().getX();
                DeltaCSY = c.getCameraPoint().getY() - MidVertex.getCameraPoint().getY();
                DeltaCSZ = c.getCameraPoint().getZ() - MidVertex.getCameraPoint().getZ();
            } else {
                LDeltaCSX = 0;
                LDeltaCSY = 0;
                LDeltaCSZ = 0;
                RDeltaCSX = 0;
                RDeltaCSY = 0;
                RDeltaCSZ = 0;
                DeltaCSX = 0;
                DeltaCSY = 0;
                DeltaCSZ = 0;
            }


            // Handle Top horizontal cases
            if ((int) RDeltaY == 0) {
                RSlope = 0;
                RZSlope = 0;
                RRedSlope = 0;
                RGreenSlope = 0;
                RBlueSlope = 0;
                RNXSlope = 0;
                RNYSlope = 0;
                RNZSlope = 0;
                RCSXSlope = 0;
                RCSYSlope = 0;
                RCSZSlope = 0;
            } else {
                RSlope = RDeltaX / RDeltaY;
                RZSlope = RDeltaZ / RDeltaY;
                RRedSlope = RDeltaR / RDeltaY;
                RGreenSlope = RDeltaG / RDeltaY;
                RBlueSlope = RDeltaB / RDeltaY;
                RNXSlope = RDeltaNX / RDeltaY;
                RNYSlope = RDeltaNY / RDeltaY;
                RNZSlope = RDeltaNZ / RDeltaY;
                RCSXSlope = RDeltaCSX / RDeltaY;
                RCSYSlope = RDeltaCSY / RDeltaY;
                RCSZSlope = RDeltaCSZ / RDeltaY;
            }

            if ((int) LDeltaY == 0) {
                LSlope = 0;
                LZSlope = 0;
                LRedSlope = 0;
                LGreenSlope = 0;
                LBlueSlope = 0;
                LNXSlope = 0;
                LNYSlope = 0;
                LNZSlope = 0;
                LCSXSlope = 0;
                LCSYSlope = 0;
                LCSZSlope = 0;
            } else {
                LSlope = LDeltaX / LDeltaY;
                LZSlope = LDeltaZ / LDeltaY;
                LRedSlope = LDeltaR / LDeltaY;
                LGreenSlope = LDeltaG / LDeltaY;
                LBlueSlope = LDeltaB / LDeltaY;
                LNXSlope = LDeltaNX / LDeltaY;
                LNYSlope = LDeltaNY / LDeltaY;
                LNZSlope = LDeltaNZ / LDeltaY;
                LCSXSlope = LDeltaCSX / LDeltaY;
                LCSYSlope = LDeltaCSY / LDeltaY;
                LCSZSlope = LDeltaCSZ / LDeltaY;
            }


            int TopY = LTopVertex.getIntY();
            int BotY = LBotVertex.getIntY();
            int MidY = MidVertex.getIntY();
            for (int j = TopY; j >= BotY; j--) {
                // Switch slope when MidY is reached
                if (j == MidY) {
                    if (MidVertexSide == LEFT) {
                        LDeltaX = DeltaX;
                        LDeltaY = DeltaY;
                        LDeltaZ = DeltaZ;

                        LX = MidVertex.getIntX();
                        LZ = MidVertex.getZ();

                        LSlope = LDeltaX / LDeltaY;
                        LZSlope = LDeltaZ / LDeltaY;

                        if(LTopVertex.interpolants.contains(Vertex3D.Interpolant.VERTEX_COLOR)) {
                            LDeltaR = DeltaR;
                            LDeltaG = DeltaG;
                            LDeltaB = DeltaB;

                            LRed = MidVertex.getColor().getR();
                            LGreen = MidVertex.getColor().getG();
                            LBlue = MidVertex.getColor().getB();

                            LRedSlope = LDeltaR / LDeltaY;
                            LGreenSlope = LDeltaG / LDeltaY;
                            LBlueSlope = LDeltaB / LDeltaY;
                        }

                        if(LTopVertex.interpolants.contains(Vertex3D.Interpolant.NORMAL)) {
                            LDeltaNX = DeltaNX;
                            LDeltaNY = DeltaNY;
                            LDeltaNZ = DeltaNZ;

                            LNX = MidVertex.getNormal().getX();
                            LNY = MidVertex.getNormal().getY();
                            LNZ = MidVertex.getNormal().getZ();

                            LNXSlope = LDeltaNX / LDeltaY;
                            LNYSlope = LDeltaNY / LDeltaY;
                            LNZSlope = LDeltaNZ / LDeltaY;
                        }

                        if(LTopVertex.interpolants.contains(Vertex3D.Interpolant.CAMERASPACE)) {
                            LDeltaCSX = DeltaCSX;
                            LDeltaCSY = DeltaCSY;
                            LDeltaCSZ = DeltaCSZ;

                            LCSX = MidVertex.getCameraPoint().getX();
                            LCSY = MidVertex.getCameraPoint().getY();
                            LCSZ = MidVertex.getCameraPoint().getZ();

                            LCSXSlope = LDeltaCSX / LDeltaY;
                            LCSYSlope = LDeltaCSY / LDeltaY;
                            LCSZSlope = LDeltaCSZ / LDeltaY;
                        }
                    } else {
                        RDeltaX = DeltaX;
                        RDeltaY = DeltaY;
                        RDeltaZ = DeltaZ;

                        RX = MidVertex.getIntX();
                        RZ = MidVertex.getZ();

                        RSlope = RDeltaX / RDeltaY;
                        RZSlope = RDeltaZ / RDeltaY;

                        if(LTopVertex.interpolants.contains(Vertex3D.Interpolant.VERTEX_COLOR)) {
                            RDeltaR = DeltaR;
                            RDeltaG = DeltaG;
                            RDeltaB = DeltaB;

                            RRed = MidVertex.getColor().getR();
                            RGreen = MidVertex.getColor().getG();
                            RBlue = MidVertex.getColor().getB();

                            RRedSlope = RDeltaR / RDeltaY;
                            RGreenSlope = RDeltaG / RDeltaY;
                            RBlueSlope = RDeltaB / RDeltaY;
                        }


                        if(LTopVertex.interpolants.contains(Vertex3D.Interpolant.NORMAL)) {
                            RDeltaNX = DeltaNX;
                            RDeltaNY = DeltaNY;
                            RDeltaNZ = DeltaNZ;

                            RNX = MidVertex.getNormal().getX();
                            RNY = MidVertex.getNormal().getY();
                            RNZ = MidVertex.getNormal().getZ();

                            RNXSlope = RDeltaNX / RDeltaY;
                            RNYSlope = RDeltaNY / RDeltaY;
                            RNZSlope = RDeltaNZ / RDeltaY;
                        }

                        if(LTopVertex.interpolants.contains(Vertex3D.Interpolant.CAMERASPACE)) {
                            RDeltaCSX = DeltaCSX;
                            RDeltaCSY = DeltaCSY;
                            RDeltaCSZ = DeltaCSZ;

                            RCSX = MidVertex.getCameraPoint().getX();
                            RCSY = MidVertex.getCameraPoint().getY();
                            RCSZ = MidVertex.getCameraPoint().getZ();

                            RCSXSlope = RDeltaCSX / RDeltaY;
                            RCSYSlope = RDeltaCSY / RDeltaY;
                            RCSZSlope = RDeltaCSZ / RDeltaY;
                        }
                    }
                }

                int X1 = (int) Math.round(LX);
                int X2 = (int) Math.round(RX);
                double Z1 = LZ;

                double R1 = LRed;
                double G1 = LGreen;
                double B1 = LBlue;

                double NX1 = LNX;
                double NY1 = LNY;
                double NZ1 = LNZ;

                double CSX1 = LCSX;
                double CSY1 = LCSY;
                double CSZ1 = LCSZ;

                Color color;
                Vertex3D v;
                // FIXME: Implicit back-face culling
                for (int i = X1; i <= X2 - 1; i++) {
                    if (isBotHorizontal && j == BotY) {
                        // Don't render horizontal bot
                        break;
                    }
                    color = new Color(R1, G1, B1);
                    v =  new Vertex3D((double)i, (double)j, (double)(1/Z1), color);

                    if(LTopVertex.interpolants.contains(Vertex3D.Interpolant.CAMERASPACE)) {
                        v = v.replaceCameraPoint(new Point3DH(CSX1, CSY1, CSZ1));
                        v.setNormal(new Point3DH(NX1, NY1, NZ1).normalize());
                    }

                    color = pixelShader.shade(currentPolygon, v);
                    drawable.setPixelWithCoverage(i, j, 1/Z1, color.asARGB(), COVERAGE);
                    Z1 += ((RZ - LZ) / (double) (X2 - X1));

                    R1 += ((RRed - LRed) / (double) (X2 - X1));
                    G1 += ((RGreen - LGreen) / (double) (X2 - X1));
                    B1 += ((RBlue - LBlue) / (double) (X2 - X1));

                    NX1 += ((RNX - LNX)) / (double) (X2 - X1);
                    NY1 += ((RNY - LNY)) / (double) (X2 - X1);
                    NZ1 += ((RNZ - LNZ)) / (double) (X2 - X1);

                    CSX1 += ((RCSX - LCSX)) / (double) (X2 - X1);
                    CSY1 += ((RCSY - LCSY)) / (double) (X2 - X1);
                    CSZ1 += ((RCSZ - LCSZ)) / (double) (X2 - X1);
                }

                // 2d x & z
                LX -= LSlope;
                RX -= RSlope;
                LZ -= LZSlope;
                RZ -= RZSlope;

                // rgb
                LRed -= LRedSlope;
                LGreen -= LGreenSlope;
                LBlue -= LBlueSlope;
                RRed -= RRedSlope;
                RGreen -= RGreenSlope;
                RBlue -= RBlueSlope;

                // normal
                LNX -= LNXSlope;
                LNY -= LNYSlope;
                LNZ -= LNZSlope;
                RNX -= RNXSlope;
                RNY -= RNYSlope;
                RNZ -= RNZSlope;

                // camera space
                LCSX -= LCSXSlope;
                LCSY -= LCSYSlope;
                LCSZ -= LCSZSlope;
                RCSX -= RCSXSlope;
                RCSY -= RCSYSlope;
                RCSZ -= RCSZSlope;
            }
        }
    }
}
