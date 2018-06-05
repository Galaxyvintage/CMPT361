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
        double LX = LTopVertex.getIntX();
        double RX = RTopVertex.getIntX();

        if (LLength > RLength) {
            MidVertexSide = LEFT;
            MidVertex = LChain.get(SECOND_VERTEX);
        } else {
            MidVertexSide = RIGHT;
            MidVertex = RChain.get(SECOND_VERTEX);
        }

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

        
        double LRedSlope, LGreenSlope, LBlueSlope;
        double RRedSlope, RGreenSlope, RBlueSlope;
        double LRed   = LTopVertex.getColor().getR();
        double LGreen = LTopVertex.getColor().getG();
        double LBlue  = LTopVertex.getColor().getB();
        double RRed   = RTopVertex.getColor().getR();
        double RGreen = RTopVertex.getColor().getG();
        double RBlue  = RTopVertex.getColor().getB();

        int TopY = LTopVertex.getIntY();
        int BotY = LBotVertex.getIntY();
        int MidY = MidVertex.getIntY();

        boolean isTopHorizontal = false;
        if(MidVertexSide == LEFT) {
            double LDeltaX = MidVertex.getIntX() - LTopVertex.getIntX();
            double LDeltaY = MidVertex.getIntY() - LTopVertex.getIntY();
            double RDeltaX = RBotVertex.getIntX() - RTopVertex.getIntX();
            double RDeltaY = RBotVertex.getIntY() - RTopVertex.getIntY();
            double LDeltaR = MidVertex.getColor().getR() - LTopVertex.getColor().getR();
            double LDeltaG = MidVertex.getColor().getG() - LTopVertex.getColor().getG();
            double LDeltaB = MidVertex.getColor().getB() - LTopVertex.getColor().getB();
            double RDeltaR = RBotVertex.getColor().getR() - RTopVertex.getColor().getR();
            double RDeltaG = RBotVertex.getColor().getG() - RTopVertex.getColor().getG();
            double RDeltaB = RBotVertex.getColor().getB() - RTopVertex.getColor().getB();


            if((int)LDeltaY == 0) {
                LSlope = 0;
                LRedSlope = 0;
                LGreenSlope = 0;
                LBlueSlope = 0;
                isTopHorizontal = true;
            } else {
                LSlope = LDeltaX / LDeltaY;
                LRedSlope = LDeltaR / LDeltaY;
                LGreenSlope = LDeltaG / LDeltaY;
                LBlueSlope = LDeltaB / LDeltaY;
            }
            RSlope = RDeltaX / RDeltaY;
            RRedSlope = RDeltaR / RDeltaY;
            RGreenSlope = RDeltaG / RDeltaY;
            RBlueSlope = RDeltaB / RDeltaY;

        } else {
            double LDeltaX = LBotVertex.getIntX() - LTopVertex.getIntX();
            double LDeltaY = LBotVertex.getIntY() - LTopVertex.getIntY();
            double RDeltaX = MidVertex.getIntX() - RTopVertex.getIntX();
            double RDeltaY = MidVertex.getIntY() - RTopVertex.getIntY();
            double LDeltaR = LBotVertex.getColor().getR() - LTopVertex.getColor().getR();
            double LDeltaG = LBotVertex.getColor().getG() - LTopVertex.getColor().getG();
            double LDeltaB = LBotVertex.getColor().getB() - LTopVertex.getColor().getB();
            double RDeltaR = MidVertex.getColor().getR() - RTopVertex.getColor().getR();
            double RDeltaG = MidVertex.getColor().getG() - RTopVertex.getColor().getG();
            double RDeltaB = MidVertex.getColor().getB() - RTopVertex.getColor().getB();


            if((int)RDeltaY == 0) {
                RSlope = 0;
                RRedSlope = 0;
                RGreenSlope = 0;
                RBlueSlope = 0;
                isTopHorizontal = true;
            } else {
                RSlope = RDeltaX / RDeltaY;
                RRedSlope = RDeltaR / RDeltaY;
                RGreenSlope = RDeltaG / RDeltaY;
                RBlueSlope = RDeltaB / RDeltaY;
            }
            LSlope = LDeltaX / LDeltaY;
            LRedSlope = LDeltaR / LDeltaY;
            LGreenSlope = LDeltaG / LDeltaY;
            LBlueSlope = LDeltaB / LDeltaY;
        }

        for(int j = TopY; j >= BotY; j--) {
            if(j == MidY) {
                if(MidVertexSide == LEFT) {
                    double LDeltaX = LBotVertex.getIntX() - MidVertex.getIntX();
                    double LDeltaY = LBotVertex.getIntY() - MidVertex.getIntY();
                    double LDeltaR = LBotVertex.getColor().getR() - MidVertex.getColor().getR();
                    double LDeltaG = LBotVertex.getColor().getG() - MidVertex.getColor().getG();
                    double LDeltaB = LBotVertex.getColor().getB() - MidVertex.getColor().getB();

                    LX = MidVertex.getIntX();
                    LRed = MidVertex.getColor().getR();
                    LGreen = MidVertex.getColor().getG();
                    LBlue = MidVertex.getColor().getB();

                    LSlope = LDeltaX / LDeltaY;
                    LRedSlope = LDeltaR / LDeltaY;
                    LGreenSlope = LDeltaG / LDeltaY;
                    LBlueSlope = LDeltaB / LDeltaY;

                } else {
                    double RDeltaX = RBotVertex.getIntX() - MidVertex.getIntX();
                    double RDeltaY = RBotVertex.getIntY() - MidVertex.getIntY();
                    double RDeltaR = RBotVertex.getColor().getR() - MidVertex.getColor().getR();
                    double RDeltaG = RBotVertex.getColor().getG() - MidVertex.getColor().getG();
                    double RDeltaB = RBotVertex.getColor().getB() - MidVertex.getColor().getB();

                    RX = MidVertex.getIntX();
                    RRed = MidVertex.getColor().getR();
                    RGreen = MidVertex.getColor().getG();
                    RBlue = MidVertex.getColor().getB();

                    RSlope = RDeltaX / RDeltaY;
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

            for(int i = X1; i <= X2 - 1; i++) {
                if(isTopHorizontal && j == TopY) {
                    // Don't render horizontal top
                    break;
                }
                Color color = new Color(R1, G1, B1);
                drawable.setPixelWithCoverage(i, j , 0, color.asARGB(), COVERAGE);

                R1 += ((RRed - LRed) / (double)(X2 - X1));
                G1 += ((RGreen - LGreen) / (double)(X2 - X1));
                B1 += ((RBlue - LBlue) / (double)(X2 - X1));
            }
            LX -= LSlope;
            RX -= RSlope;

            LRed   -= LRedSlope;
            LGreen -= LGreenSlope;
            LBlue  -= LBlueSlope;
            RRed   -= RRedSlope;
            RGreen -= RGreenSlope;
            RBlue  -= RBlueSlope;
        }
    }
}
