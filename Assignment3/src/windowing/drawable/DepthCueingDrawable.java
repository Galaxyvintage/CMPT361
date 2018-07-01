package windowing.drawable;


import windowing.graphics.Color;

public class DepthCueingDrawable extends DrawableDecorator {

    private final int nearZ;
    private final int farZ;
    private final Color farColor;

    public DepthCueingDrawable(Drawable delegate, int nearZ,  int farZ, Color color) {
        super(delegate);
        this.nearZ = nearZ;
        this.farZ = farZ;
        this.farColor = color;
    }

    // Fixme: clean up when using actual clipper instead of pixel clipping
    @Override
    public void setPixel(int x, int y, double z, int argbColor) {
        double csz = z;
        double depthColor;
        if(csz >= nearZ)
            depthColor = argbColor;
        if(csz <= farZ) {
            depthColor = farColor.asARGB();
        } else {
            double ratio = (csz / (farZ - nearZ)) % 1.0;
            Color nearColor = Color.fromARGB(argbColor);
            depthColor= nearColor.add(farColor.subtract(nearColor).scale(ratio)).asARGB();
        }
        delegate.setPixel(x, y, z, (int)depthColor);
    }

    @Override
    public void setPixelWithCoverage(int x, int y, double z, int argbColor, double coverage) {
        double csz = z;
        double depthColor;
        if(csz >= nearZ)
            depthColor = argbColor;
        if(csz <= farZ) {
            depthColor = farColor.asARGB();
        } else {
            double ratio = (csz / (farZ - nearZ)) % 1.0;
            Color nearColor = Color.fromARGB(argbColor);
            depthColor= nearColor.add(farColor.subtract(nearColor).scale(ratio)).asARGB();
        }
        delegate.setPixelWithCoverage(x, y, z, (int)depthColor, coverage);
    }
}
