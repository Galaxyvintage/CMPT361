package client;


import client.testPages.*;
import geometry.Point2D;
import line.*;
import polygon.FilledPolygonRenderer;
import polygon.PolygonRenderer;
import windowing.PageTurner;
import windowing.drawable.ColoredDrawable;
import windowing.drawable.Drawable;
import windowing.drawable.GhostWritingDrawable;
import windowing.drawable.TranslatingDrawable;
import windowing.graphics.Dimensions;


public class Client implements PageTurner {
	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int NUM_PAGES = 5;
	private static final double GHOST_COVERAGE = 0.14;

	private static final int NUM_PANELS = 5;
	private static final Dimensions PANEL_SIZE = new Dimensions(650, 650);

	
	private final Drawable drawable;
	private int pageNumber = 0;
	
	private Drawable image;
	private Drawable fullPanel;
//	private Drawable depthCueingDrawable;
//
//	private LineRenderer lineRenderers[];
//	private PolygonRenderer polygonRenderer;

    Client(Drawable drawable) {
		this.drawable = drawable;	
		createDrawables();
	}

	private void createDrawables() {
		image = new TranslatingDrawable(drawable, point(0, 0), dimensions(750, 750));
		image = new ColoredDrawable(image, ARGB_WHITE);
		fullPanel = new TranslatingDrawable(image, point(50, 50), PANEL_SIZE);
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
//			case 1:  new MeshPolygonTest(fullPanel, wireframeRenderer, MeshPolygonTest.USE_PERTURBATION);
//				break;
//			case 2:  new MeshPolygonTest(fullPanel, polygonRenderer, MeshPolygonTest.USE_PERTURBATION);
//				break;
//			case 3:	 centeredTriangleTest(fullPanel, polygonRenderer);
//				break;
//
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
//			case 6:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.WHITE);
//				interpreter = new SimpInterpreter("page6.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
//			case 7:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.WHITE);
//				interpreter = new SimpInterpreter("page7.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
//			case 0:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.WHITE);
//				interpreter = new SimpInterpreter("page8.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
			default: defaultPage();
				break;
		}
	}

//
//	@FunctionalInterface
//	private interface TestPerformer {
//	    void perform(Drawable drawable, LineRenderer renderer);
//	}
//	private void lineDrawerPage(TestPerformer test) {
//    	image.clear();
//		for(int panelNumber = 0; panelNumber < panels.length; panelNumber++) {
//			panels[panelNumber].clear();
//			test.perform(panels[panelNumber], lineRenderers[panelNumber]);
//		}
//	}
//
//	public void polygonDrawerPage(Drawable[] panelArray) {
//		image.clear();
//		for(Drawable panel: panels) {		// 'panels' necessary here.  Not panelArray, because clear() uses setPixel.
//			panel.clear();
//		}
//		new StarburstPolygonTest(panelArray[0], polygonRenderer);
//		new MeshPolygonTest(panelArray[1], polygonRenderer, MeshPolygonTest.NO_PERTURBATION);
//		new MeshPolygonTest(panelArray[2], polygonRenderer, MeshPolygonTest.USE_PERTURBATION);
//		new RandomPolygonTest(panelArray[3], polygonRenderer);
//	}

    public void defaultPage() {
        image.clear();
        fullPanel.clear();
    }
}
