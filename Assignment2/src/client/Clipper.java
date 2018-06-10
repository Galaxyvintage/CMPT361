package client;

public class Clipper {
    private int nearZ;
    private int farZ;

    private int leftX;
    private int rightX;

    private int leftY;
    private int rightY;

    public Clipper(int nearZ, int farZ, int leftX, int rightX, int leftY, int rightY) {
        this.nearZ = nearZ;
        this.farZ = farZ;
        this.leftX = leftX;
        this.rightX = rightX;
        this.leftY = leftY;
        this.rightY = rightY;
    }
}
