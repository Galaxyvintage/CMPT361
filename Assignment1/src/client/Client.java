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

//import notProvided.client.testpages.MeshPolygonTest;
//import notProvided.client.testpages.RandomPolygonTest;
//import notProvided.client.testpages.StarburstPolygonTest;
//import notProvided.line.AntialiasingLineRenderer;
//import notProvided.polygon.FilledPolygonRenderer;
//import notProvided.polygon.WireframePolygonRenderer;


public class Client implements PageTurner {
	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int NUM_PAGES = 5;
	private static final double GHOST_COVERAGE = 0.14;

	private static final int NUM_PANELS = 4;
	private static final Dimensions PANEL_SIZE = new Dimensions(300, 300);
	private static final Point2D[] lowCornersOfPanels = {
            new Point2D( 50,  50),
            new Point2D(400,  50),
	        new Point2D( 50, 400),
			new Point2D(400, 400),
	};
	
	private final Drawable drawable;
	private int pageNumber = 0;
	
	private Drawable image;
	private Drawable[] panels;
	private Drawable[] ghostPanels;					// use transparency and write only white

	private LineRenderer lineRenderers[];
	private PolygonRenderer polygonRenderer;

    Client(Drawable drawable) {
		this.drawable = drawable;	
		createDrawables();
		createRenderers();
	}

	private void createDrawables() {
		image = new TranslatingDrawable(drawable, point(0, 0), dimensions(750, 750));
		image = new ColoredDrawable(image, ARGB_WHITE);
		
		createPanels();
		createGhostPanels();
	}

	private void createPanels() {
		panels = new Drawable[NUM_PANELS];
		
		for(int index = 0; index < NUM_PANELS; index++) {
			panels[index] = new TranslatingDrawable(image, lowCornersOfPanels[index], PANEL_SIZE);
		}
	}

	private void createGhostPanels() {
		ghostPanels = new Drawable[NUM_PANELS];
		
		for(int index = 0; index < NUM_PANELS; index++) {
			Drawable drawable = panels[index];
			ghostPanels[index] = new GhostWritingDrawable(drawable, GHOST_COVERAGE);
		}
	}

	private Point2D point(int x, int y) {
		return new Point2D(x, y);
	}	
	private Dimensions dimensions(int x, int y) {
		return new Dimensions(x, y);
	}
	private void createRenderers() {
		lineRenderers = new LineRenderer[4];
		lineRenderers[0] = DDALineRenderer.make();
		lineRenderers[1] = BresenhamLineRenderer.make();
		lineRenderers[2] = AlternatingLineRenderer.make();
		lineRenderers[3] = AntialiasingLineRenderer.make();
		polygonRenderer = FilledPolygonRenderer.make();

	}
	
	@Override
	public void nextPage() {
		System.out.println("PageNumber " + (pageNumber + 1));
		pageNumber = (pageNumber + 1) % NUM_PAGES;
		
		switch(pageNumber) {
		case 1:  lineDrawerPage(StarburstLineTest::new);
				 break;
		case 2:  lineDrawerPage(ParallelogramLineTest::new);
				 break;
		case 3:	 lineDrawerPage(RandomLineTest::new);
				 break;
		case 4:  polygonDrawerPage(panels);
				 break;
		case 0:	 polygonDrawerPage(ghostPanels);		// will be fifth page.  5 == 0 (mod 5)
				 break;
		default:
		    break;
		}
	}

	@FunctionalInterface
	private interface TestPerformer {
	    void perform(Drawable drawable, LineRenderer renderer);
	}
	private void lineDrawerPage(TestPerformer test) {
    	image.clear();
		for(int panelNumber = 0; panelNumber < panels.length; panelNumber++) {
			panels[panelNumber].clear();
			test.perform(panels[panelNumber], lineRenderers[panelNumber]);
		}
	}

	public void polygonDrawerPage(Drawable[] panelArray) {
		image.clear();
		for(Drawable panel: panels) {		// 'panels' necessary here.  Not panelArray, because clear() uses setPixel.
			panel.clear();
		}
		new StarburstPolygonTest(panelArray[0], polygonRenderer);
		new MeshPolygonTest(panelArray[1], polygonRenderer, MeshPolygonTest.NO_PERTURBATION);
		new MeshPolygonTest(panelArray[2], polygonRenderer, MeshPolygonTest.USE_PERTURBATION);
//		new RandomPolygonTest(panelArray[3], polygonRenderer);
	}
}
