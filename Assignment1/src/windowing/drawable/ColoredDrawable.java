package windowing.drawable;

public class ColoredDrawable extends DrawableDecorator {

    public ColoredDrawable(Drawable delegate, int argbColor) {
        super(delegate);
        delegate.fill(argbColor, Double.MAX_VALUE);
    }
}
