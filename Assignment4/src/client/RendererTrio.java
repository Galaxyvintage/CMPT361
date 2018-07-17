package client;

import line.DDALineRenderer;
import line.LineRenderer;
import polygon.FilledPolygonRenderer;
import polygon.WireFrameRenderer;

public class RendererTrio {
    public LineRenderer getLineRenderer() {
        return DDALineRenderer.make();
    }

    public FilledPolygonRenderer getFilledRenderer() {
        return FilledPolygonRenderer.make();
    }

    public WireFrameRenderer getWireframeRenderer() {
        return WireFrameRenderer.make(getLineRenderer());
    }
}
