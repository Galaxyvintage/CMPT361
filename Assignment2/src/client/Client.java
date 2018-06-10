package client;


import client.interpreter.SimpInterpreter;
import client.testPages.*;
import geometry.Point2D;
import windowing.PageTurner;
import windowing.drawable.*;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;


public class Client implements PageTurner {
	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int NUM_PAGES = 6;
	private static final Dimensions PANEL_SIZE = new Dimensions(650, 650);

	private final Drawable drawable;
	private int pageNumber = 0;
	
	private Drawable image;
	private Drawable fullPanel;
	private Drawable depthCueingDrawable;

	private RendererTrio renderers;
	private SimpInterpreter interpreter;

    Client(Drawable drawable) {
		this.drawable = drawable;
		this.renderers = new RendererTrio();
		createDrawables();
	}

	private void createDrawables() {
		image = new TranslatingDrawable(drawable, point(0, 0), dimensions(750, 750));
		image = new ColoredDrawable(image, ARGB_WHITE);
		image = new InvertedYDrawable(image);
		fullPanel = new TranslatingDrawable(image, point(50, 50), PANEL_SIZE);
        fullPanel = new ZBufferDrawable(fullPanel);
	}

	private Point2D point(int x, int y) {
		return new Point2D(x, y);
	}	
	private Dimensions dimensions(int x, int y) {
		return new Dimensions(x, y);
	}


	@Override
	public void nextPage() {
		Drawable depthCueingDrawable;
		System.out.println("PageNumber " + (pageNumber + 1));
		pageNumber = (pageNumber + 1) % NUM_PAGES;

		image.clear();
		fullPanel.clear();

		switch(pageNumber) {
			case 1:  new MeshPolygonTest(fullPanel, renderers.getWireframeRenderer(), MeshPolygonTest.USE_PERTURBATION);
				break;
			case 2:  new MeshPolygonTest(fullPanel, renderers.getFilledRenderer(), MeshPolygonTest.USE_PERTURBATION);
				break;
			case 3:	 new CenteredTriangleTest(fullPanel, renderers.getFilledRenderer());
				break;
//			case 4:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.GREEN);
//				interpreter = new SimpInterpreter("tomsPage4.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
//			case 5:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.RED);
//				interpreter = new SimpInterpreter("tomsPage5.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
			case 4:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.WHITE);
				System.out.println("Working Directory = " + System.getProperty("user.dir"));
				interpreter = new SimpInterpreter("simp/myTest.simp", depthCueingDrawable, renderers);
				interpreter.interpret();
				break;

//			case 5:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.WHITE);
//				interpreter = new SimpInterpreter("simp/page7.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
//			case 6:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.WHITE);
//				interpreter = new SimpInterpreter("simp/page8.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
			default: defaultPage();
				break;
		}
	}

    private void defaultPage() {
        image.clear();
        fullPanel.clear();
    }
}
