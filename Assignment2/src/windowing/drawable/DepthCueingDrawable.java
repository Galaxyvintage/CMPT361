package windowing.drawable;


import windowing.graphics.Color;

public class DepthCueingDrawable extends DrawableDecorator {

    private final int nearZ;
    private final int farZ;
    private final Color nearColor;
    private final Color farColor = Color.BLACK;

    public DepthCueingDrawable(Drawable delegate, int nearZ,  int farZ, Color color) {
        super(delegate);
        this.nearZ = nearZ;
        this.farZ = farZ;
        this.nearColor = color;
    }

    // Fixme: clean up when using actual clipper instead of pixel clipping
    @Override
    public void setPixel(int x, int y, double z, int argbColor) {
        // interpolate Color based on z
        double height = delegate.getDimensions().getHeight();
        double width = delegate.getDimensions().getWidth();
        if(z < farZ || z >= nearZ || x < 0 || x >= width || y < 0 || y >= height ) {
            return;
        }

        double ratio = (z / (farZ - nearZ)) % 1.0;
        Color depthColor = nearColor.add(farColor.subtract(nearColor).scale(ratio));
        delegate.setPixel(x, y, z, depthColor.asARGB());
    }

    @Override
    public void setPixelWithCoverage(int x, int y, double z, int argbColor, double coverage) {
        double height = delegate.getDimensions().getHeight();
        double width = delegate.getDimensions().getWidth();
        if(z < farZ || z >= nearZ || x < 0 || x >= width || y < 0 || y >= height ) {
            return;
        }

        double ratio = (z / (farZ - nearZ)) % 1.0;
        Color depthColor = nearColor.add(farColor.subtract(nearColor).scale(ratio));
        delegate.setPixelWithCoverage(x, y, z, depthColor.asARGB(), coverage);
    }
}
