package windowing.drawable;

public class ColoredDrawable extends DrawableDecorator {
    private int argbColor;

    public ColoredDrawable(Drawable delegate, int argbColor) {
        super(delegate);
        this.argbColor = argbColor;
    }

    @Override
    public void clear() {
        delegate.fill(argbColor, Double.MAX_VALUE);
    }
}
