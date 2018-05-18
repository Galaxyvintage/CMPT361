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

        Color color = LBotVertex.getColor();
        int MidVertexSide;
        double LSlope;
        double RSlope;
        double LX = LTopVertex.getX();
        double RX = RTopVertex.getX();

        if (LLength > RLength) {
            MidVertexSide = LEFT;
            MidVertex = LChain.get(SECOND_VERTEX);
        } else {
            MidVertexSide = RIGHT;
            MidVertex = RChain.get(SECOND_VERTEX);
        }

        if(MidVertexSide == LEFT) {
            double LDeltaX = MidVertex.getX() - LTopVertex.getX();
            double LDeltaY = MidVertex.getY() - LTopVertex.getY();
            double RDeltaX = RBotVertex.getX() - RTopVertex.getX();
            double RDeltaY = RBotVertex.getY() - RTopVertex.getY();
            if(LDeltaY == 0) {
                LSlope = 0;
            } else {
                LSlope = LDeltaX / LDeltaY;
            }

            if(RDeltaY == 0) {
                RSlope = 0;
            } else {
                RSlope = RDeltaX / RDeltaY;
            }
        } else {
            double LDeltaX = LBotVertex.getX() - LTopVertex.getX();
            double LDeltaY = LBotVertex.getY() - LTopVertex.getY();
            double RDeltaX = MidVertex.getX() - RTopVertex.getX();
            double RDeltaY = MidVertex.getY() - RTopVertex.getY();

            if(LDeltaY == 0) {
                LSlope = 0;
            } else {
                LSlope = LDeltaX / LDeltaY;
            }

            if(RDeltaY == 0) {
                RSlope = 0;
            } else {
                RSlope = RDeltaX / RDeltaY;
            }
        }

        int TopY = LTopVertex.getIntY();
        int BotY = LBotVertex.getIntY();
        int MidY = MidVertex.getIntY();

        int flag = 0;
        for(int i = TopY; i > BotY; i--) {
            if(i <= MidY && flag == 0) {
                flag = 1;
                if(MidVertexSide == LEFT) {
                    double LDeltaX = LBotVertex.getX() - MidVertex.getX();
                    double LDeltaY = LBotVertex.getY() - MidVertex.getY();
                    LX = MidVertex.getX();

                    if(LDeltaY == 0) {
                        LSlope = 0;
                    } else {
                        LSlope = LDeltaX / LDeltaY;
                    }
                } else {
                    double RDeltaX = RBotVertex.getX() - MidVertex.getX();
                    double RDeltaY = RBotVertex.getY() - MidVertex.getY();
                    RX = MidVertex.getX();
                    if(RDeltaY == 0) {
                        RSlope = 0;
                    } else {
                        RSlope = RDeltaX / RDeltaY;
                    }
                }
            }

            int X1 = (int)Math.round(LX);
            int X2 = (int)Math.round(RX);
            LX -= LSlope;
            RX -= RSlope;

            for(int j = X1; j <= X2 - 1; j++) {
                drawable.setPixelWithCoverage(j, i , 0, color.asARGB(), COVERAGE);
            }
        }
    }
}
