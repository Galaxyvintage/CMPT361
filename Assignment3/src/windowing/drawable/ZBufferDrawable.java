package windowing.drawable;


public class ZBufferDrawable extends DrawableDecorator {

    private static final double DEFAULT_Z = -200;
    private double[][] zBuffer;

    public ZBufferDrawable(Drawable delegate) {
        super(delegate);
        int width = delegate.getWidth();
        int height = delegate.getHeight();
        resetBuffer(width, height);
    }

    @Override
    public void setPixel(int x, int y, double z, int argbColor) {
        if (z > zBuffer[x][y]) {
            delegate.setPixel(x, y, z, argbColor);
            zBuffer[x][y] = z;
        }
        else {
//            System.out.println(z);
        }
    }

    @Override
    public void clear() {
        super.clear();
        resetBuffer(super.getWidth(), super.getHeight());
    }

    private void resetBuffer(int width, int height) {
        if(zBuffer == null) {
            zBuffer = new double[width][height];
        }

        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                zBuffer[i][j] = DEFAULT_Z;
            }
        }
    }
}
