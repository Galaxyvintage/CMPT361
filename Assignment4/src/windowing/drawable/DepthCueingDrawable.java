package windowing.drawable;


import windowing.graphics.Color;

public class DepthCueingDrawable extends DrawableDecorator {

    private final double nearZ;
    private final double farZ;
    private final Color farColor;

    public DepthCueingDrawable(Drawable delegate, double nearZ,  double farZ, Color color) {
        super(delegate);
        this.nearZ = nearZ;
        this.farZ = farZ;
        this.farColor = color;
    }

    @Override
    public void setPixel(int x, int y, double z, int argbColor) {
        double csz = z;
        int depthColor;
        if(csz >= nearZ)
            depthColor = argbColor;
        else if(csz <= farZ) {
            depthColor = farColor.asARGB();
        } else {
            double ratio = (csz / (farZ - nearZ)) % 1.0;
            Color nearColor = Color.fromARGB(argbColor);
            depthColor= nearColor.add(farColor.subtract(nearColor).scale(ratio)).asARGB();
        }
        delegate.setPixel(x, y, csz, depthColor);
    }

    @Override
    public void setPixelWithCoverage(int x, int y, double z, int argbColor, double coverage) {
        double csz = z;
        double depthColor;
        if(csz >= nearZ) {
            depthColor = argbColor;
        } else if(csz <= farZ) {
            depthColor = farColor.asARGB();
        } else {
            double ratio = (csz / (farZ - nearZ)) % 1.0;
            Color nearColor = Color.fromARGB(argbColor);
            depthColor= nearColor.add(farColor.subtract(nearColor).scale(ratio)).asARGB();
        }
        delegate.setPixelWithCoverage(x, y, csz, (int)depthColor, coverage);
    }
}
