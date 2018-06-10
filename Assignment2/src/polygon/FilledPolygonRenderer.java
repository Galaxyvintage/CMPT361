package polygon;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class FilledPolygonRenderer implements PolygonRenderer {
    private static final int FIRST_VERTEX = 0;
    private static final int SECOND_VERTEX = 1;
    private static final int LEFT = -1;
    private static final int RIGHT = 1;
    private static final int COVERAGE = 1;

    public static FilledPolygonRenderer make() { return new FilledPolygonRenderer();}

    // TODO: Need to optimize and clean up

    public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader){

        Chain LChain = polygon.leftChain();
        Chain RChain = polygon.rightChain();

        int LLength = LChain.length();
        int RLength = RChain.length();

        Vertex3D LTopVertex = LChain.get(FIRST_VERTEX);
        Vertex3D LBotVertex = LChain.get(LLength - 1);
        Vertex3D RTopVertex = RChain.get(FIRST_VERTEX);
        Vertex3D RBotVertex = RChain.get(RLength - 1);
        Vertex3D MidVertex;

        int MidVertexSide;
        double LSlope;
        double RSlope;
        double LZSlope;
        double RZSlope;

        double LX = LTopVertex.getIntX();
        double RX = RTopVertex.getIntX();
        double LZ = LTopVertex.getIntZ();
        double RZ = LTopVertex.getIntZ();


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

        // Shading vertex color
        Color c;
        c = vertexShader.shade(LTopVertex.getColor());
        LTopVertex = LTopVertex.replaceColor(c);
        c = vertexShader.shade(LBotVertex.getColor());
        LBotVertex = LBotVertex.replaceColor(c);

        c = vertexShader.shade(RTopVertex.getColor());
        RTopVertex = RTopVertex.replaceColor(c);
        c = vertexShader.shade(RBotVertex.getColor());
        RBotVertex = RBotVertex.replaceColor(c);

        c = vertexShader.shade(MidVertex.getColor());
        MidVertex = MidVertex.replaceColor(c);


        // Left and Right RGB slopes and initial values
        double LRedSlope, LGreenSlope, LBlueSlope;
        double RRedSlope, RGreenSlope, RBlueSlope;
        double LRed   = LTopVertex.getColor().getR();
        double LGreen = LTopVertex.getColor().getG();
        double LBlue  = LTopVertex.getColor().getB();
        double RRed   = RTopVertex.getColor().getR();
        double RGreen = RTopVertex.getColor().getG();
        double RBlue  = RTopVertex.getColor().getB();

        // Assuming all vertices have the same z value
        double z = LTopVertex.getZ();

        double LDeltaX, LDeltaY, LDeltaZ;
        double LDeltaR, LDeltaG, LDeltaB;

        double RDeltaX, RDeltaY, RDeltaZ;
        double RDeltaR, RDeltaG, RDeltaB;

        // Mid to Bottom (Switching DDA)
        double DeltaX, DeltaY, DeltaZ;
        double DeltaR, DeltaG, DeltaB;

        boolean isBotHorizontal = false;
        if(MidVertexSide == LEFT) {
            LDeltaX = MidVertex.getIntX() - LTopVertex.getIntX();
            LDeltaY = MidVertex.getIntY() - LTopVertex.getIntY();
            LDeltaZ = MidVertex.getIntZ() - LTopVertex.getIntZ();
            LDeltaR = MidVertex.getColor().getR() - LTopVertex.getColor().getR();
            LDeltaG = MidVertex.getColor().getG() - LTopVertex.getColor().getG();
            LDeltaB = MidVertex.getColor().getB() - LTopVertex.getColor().getB();

            RDeltaX = RBotVertex.getIntX() - RTopVertex.getIntX();
            RDeltaY = RBotVertex.getIntY() - RTopVertex.getIntY();
            RDeltaZ = RBotVertex.getIntZ() - RTopVertex.getIntZ();
            RDeltaR = RBotVertex.getColor().getR() - RTopVertex.getColor().getR();
            RDeltaG = RBotVertex.getColor().getG() - RTopVertex.getColor().getG();
            RDeltaB = RBotVertex.getColor().getB() - RTopVertex.getColor().getB();

            DeltaX = LBotVertex.getIntX() - MidVertex.getIntX();
            DeltaY = LBotVertex.getIntY() - MidVertex.getIntY();
            DeltaZ = LBotVertex.getIntZ() - MidVertex.getIntZ();
            DeltaR = LBotVertex.getColor().getR() - MidVertex.getColor().getR();
            DeltaG = LBotVertex.getColor().getG() - MidVertex.getColor().getG();
            DeltaB = LBotVertex.getColor().getB() - MidVertex.getColor().getB();

            if(MidVertex.getIntY() == RBotVertex.getIntY()) {
                isBotHorizontal = true;
            }

        } else {
            LDeltaX = LBotVertex.getIntX() - LTopVertex.getIntX();
            LDeltaY = LBotVertex.getIntY() - LTopVertex.getIntY();
            LDeltaZ = LBotVertex.getIntZ() - LTopVertex.getIntZ();
            LDeltaR = LBotVertex.getColor().getR() - LTopVertex.getColor().getR();
            LDeltaG = LBotVertex.getColor().getG() - LTopVertex.getColor().getG();
            LDeltaB = LBotVertex.getColor().getB() - LTopVertex.getColor().getB();

            RDeltaX = MidVertex.getIntX() - RTopVertex.getIntX();
            RDeltaY = MidVertex.getIntY() - RTopVertex.getIntY();
            RDeltaZ = MidVertex.getIntZ() - RTopVertex.getIntZ();
            RDeltaR = MidVertex.getColor().getR() - RTopVertex.getColor().getR();
            RDeltaG = MidVertex.getColor().getG() - RTopVertex.getColor().getG();
            RDeltaB = MidVertex.getColor().getB() - RTopVertex.getColor().getB();

            DeltaX = RBotVertex.getIntX() - MidVertex.getIntX();
            DeltaY = RBotVertex.getIntY() - MidVertex.getIntY();
            DeltaZ = RBotVertex.getIntZ() - MidVertex.getIntZ();
            DeltaR = RBotVertex.getColor().getR() - MidVertex.getColor().getR();
            DeltaG = RBotVertex.getColor().getG() - MidVertex.getColor().getG();
            DeltaB = RBotVertex.getColor().getB() - MidVertex.getColor().getB();

            if(MidVertex.getIntY() == LBotVertex.getIntY()) {
                isBotHorizontal = true;
            }
        }


        // Handle Top horizontal cases
        if((int)RDeltaY == 0) {
            RSlope = 0;
            RZSlope = 0;
            RRedSlope = 0;
            RGreenSlope = 0;
            RBlueSlope = 0;
        } else {
            RSlope = RDeltaX / RDeltaY;
            RZSlope = RDeltaZ / RDeltaY;
            RRedSlope = RDeltaR / RDeltaY;
            RGreenSlope = RDeltaG / RDeltaY;
            RBlueSlope = RDeltaB / RDeltaY;
        }

        if((int)LDeltaY == 0) {
            LSlope = 0;
            LZSlope = 0;
            LRedSlope = 0;
            LGreenSlope = 0;
            LBlueSlope = 0;
        } else {
            LSlope = LDeltaX / LDeltaY;
            LZSlope = LDeltaZ / LDeltaY;
            LRedSlope = LDeltaR / LDeltaY;
            LGreenSlope = LDeltaG / LDeltaY;
            LBlueSlope = LDeltaB / LDeltaY;
        }

        int TopY = LTopVertex.getIntY();
        int BotY = LBotVertex.getIntY();
        int MidY = MidVertex.getIntY();
        for(int j = TopY; j >= BotY; j--) {
            // Switch slope when MidY is reached
            if(j == MidY) {
                if(MidVertexSide == LEFT) {
                    LDeltaX = DeltaX;
                    LDeltaY = DeltaY;
                    LDeltaZ = DeltaZ;
                    LDeltaR = DeltaR;
                    LDeltaG = DeltaG;
                    LDeltaB = DeltaB;

                    LX = MidVertex.getIntX();
                    LZ = MidVertex.getIntZ();
                    LRed = MidVertex.getColor().getR();
                    LGreen = MidVertex.getColor().getG();
                    LBlue = MidVertex.getColor().getB();

                    LSlope = LDeltaX / LDeltaY;
                    LZSlope = LDeltaZ / LDeltaY;
                    LRedSlope = LDeltaR / LDeltaY;
                    LGreenSlope = LDeltaG / LDeltaY;
                    LBlueSlope = LDeltaB / LDeltaY;

                } else {
                    RDeltaX = DeltaX;
                    RDeltaY = DeltaY;
                    RDeltaZ = DeltaZ;
                    RDeltaR = DeltaR;
                    RDeltaG = DeltaG;
                    RDeltaB = DeltaB;

                    RX = MidVertex.getIntX();
                    RZ = MidVertex.getIntZ();
                    RRed = MidVertex.getColor().getR();
                    RGreen = MidVertex.getColor().getG();
                    RBlue = MidVertex.getColor().getB();

                    RSlope = RDeltaX / RDeltaY;
                    RZSlope = RDeltaZ / RDeltaY;
                    RRedSlope = RDeltaR / RDeltaY;
                    RGreenSlope = RDeltaG / RDeltaY;
                    RBlueSlope = RDeltaB / RDeltaY;
                }
            }

            int X1 = (int)Math.round(LX);
            int X2 = (int)Math.round(RX);
            double R1 = LRed;
            double G1 = LGreen;
            double B1 = LBlue;
            double Z1 = LZ;

            for(int i = X1; i <= X2 - 1; i++) {
                if(isBotHorizontal && j == BotY) {
                    // Don't render horizontal bot
                    break;
                }
                Color color = new Color(R1, G1, B1);
                drawable.setPixelWithCoverage(i, j, Z1, color.asARGB(), COVERAGE);
                Z1 += ((RZ - LZ) / (double)(X2 - X1));
                R1 += ((RRed - LRed) / (double)(X2 - X1));
                G1 += ((RGreen - LGreen) / (double)(X2 - X1));
                B1 += ((RBlue - LBlue) / (double)(X2 - X1));

            }

            LX -= LSlope;
            RX -= RSlope;
            LZ -= LZSlope;
            RZ -= RZSlope;
            LRed   -= LRedSlope;
            LGreen -= LGreenSlope;
            LBlue  -= LBlueSlope;
            RRed   -= RRedSlope;
            RGreen -= RGreenSlope;
            RBlue  -= RBlueSlope;
        }
    }
}
