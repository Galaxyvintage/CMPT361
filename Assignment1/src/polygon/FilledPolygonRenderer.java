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
        double LX = LTopVertex.getIntX();
        double RX = RTopVertex.getIntX();

        if (LLength > RLength) {
            MidVertexSide = LEFT;
            MidVertex = LChain.get(SECOND_VERTEX);
        } else {
            MidVertexSide = RIGHT;
            MidVertex = RChain.get(SECOND_VERTEX);
        }

        boolean isTopHorizontal = false;
        if(MidVertexSide == LEFT) {
            double LDeltaX = MidVertex.getIntX() - LTopVertex.getIntX();
            double LDeltaY = MidVertex.getIntY() - LTopVertex.getIntY();
            double RDeltaX = RBotVertex.getIntX() - RTopVertex.getIntX();
            double RDeltaY = RBotVertex.getIntY() - RTopVertex.getIntY();

            if((int)LDeltaY == 0) {
                LSlope = 0;
                isTopHorizontal = true;
            } else {
                LSlope = LDeltaX / LDeltaY;
            }
            RSlope = RDeltaX / RDeltaY;
        } else {
            double LDeltaX = LBotVertex.getIntX() - LTopVertex.getIntX();
            double LDeltaY = LBotVertex.getIntY() - LTopVertex.getIntY();
            double RDeltaX = MidVertex.getIntX() - RTopVertex.getIntX();
            double RDeltaY = MidVertex.getIntY() - RTopVertex.getIntY();

            if((int)RDeltaY == 0) {
                RSlope = 0;
                isTopHorizontal = true;
            } else {
                RSlope = RDeltaX / RDeltaY;
            }
            LSlope = LDeltaX / LDeltaY;
        }

        int TopY = LTopVertex.getIntY();
        int BotY = LBotVertex.getIntY();
        int MidY = MidVertex.getIntY();
        int flag = 0;
        for(int j = TopY; j >= BotY; j--) {

            if(j == MidY && flag == 0) {
                flag = 1;
                if(MidVertexSide == LEFT) {
                    double LDeltaX = LBotVertex.getIntX() - MidVertex.getIntX();
                    double LDeltaY = LBotVertex.getIntY() - MidVertex.getIntY();
                    LX = MidVertex.getIntX();
                    LSlope = LDeltaX / LDeltaY;

                } else {
                    double RDeltaX = RBotVertex.getIntX() - MidVertex.getIntX();
                    double RDeltaY = RBotVertex.getIntY() - MidVertex.getIntY();
                    RX = MidVertex.getIntX();
                    RSlope = RDeltaX / RDeltaY;
                }
            }

            int X1 = (int)Math.round(LX);
            int X2 = (int)Math.round(RX);

            for(int i = X1; i <= X2 - 1; i++) {
                if(isTopHorizontal && j == TopY) {
                    // leaving horizontal top
                    break;
                }
                drawable.setPixelWithCoverage(i, j , 0, color.asARGB(), COVERAGE);
            }

            LX -= LSlope;
            RX -= RSlope;
        }
    }
}
