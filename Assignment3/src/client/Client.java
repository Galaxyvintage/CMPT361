package client;


import client.interpreter.SimpInterpreter;
import client.testPages.*;
import geometry.Point2D;
import model.Cylinder;
import model.Icosahedron;
import windowing.PageTurner;
import windowing.drawable.*;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;


public class Client implements PageTurner {
	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int NUM_PAGES = 10;
	private static final Dimensions PANEL_SIZE = new Dimensions(650, 650);

	private boolean hasArgument = false;
	private String filename;
	private final Drawable drawable;
	private int pageNumber = 0;
	
	private Drawable image;
	private Drawable fullPanel;
	private Drawable depthCueingDrawable;

	private RendererTrio renderers;
	private SimpInterpreter interpreter;

    Client(Drawable drawable, String[] args) {
    	if(args.length > 0) {
    		// Bad magic number....but this will do for now...
    		this.filename = args[0];
    		hasArgument = true;
		}
		this.drawable = drawable;
		this.renderers = new RendererTrio();
		createDrawables();
//		Icosahedron icosahedron = new Icosahedron();
//		icosahedron.outputFaces();
//		Cylinder cylinder = new Cylinder();
//		cylinder.outputFaces();
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
		if(hasArgument) {
			argumentNextPage();
		}
		else {
			noArgumentNextPage();
		}
	}

	private void argumentNextPage() {
		image.clear();
		fullPanel.clear();

		interpreter = new SimpInterpreter("simp/" + filename + ".simp", fullPanel, renderers);
		interpreter.interpret();
	}

	public void noArgumentNextPage() {
		System.out.println("PageNumber " + (pageNumber + 1));
		pageNumber = (pageNumber + 1) % NUM_PAGES;

		image.clear();
		fullPanel.clear();
		String filename;

		switch(pageNumber) {
			case 1:  filename = "pageA";	 break;
			case 2:  filename = "pageB";	 break;
			case 3:	 filename = "pageC";	 break;
			case 4:  filename = "pageD";	 break;
			case 5:  filename = "pageE";	 break;
			case 6:  filename = "pageF";	 break;
			case 7:  filename = "pageG";	 break;
			case 8:  filename = "pageH";	 break;
			case 9:  filename = "pageI";	 break;
			case 0:  filename = "tomsPageJ";	 break;

			default: defaultPage();
				return;
		}
		// TODO: implement clippper and replace this
        depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.RED);
		interpreter = new SimpInterpreter("simp/" + filename + ".simp", depthCueingDrawable, renderers);
		interpreter.interpret();
	}

//	@Override
//	public void nextPage() {
//		Drawable depthCueingDrawable;
//		System.out.println("PageNumber " + (pageNumber + 1));
//		pageNumber = (pageNumber + 1) % NUM_PAGES;
//
//		image.clear();
//		fullPanel.clear();
//
//		switch(pageNumber) {
//			case 1:  new MeshPolygonTest(fullPanel, renderers.getWireframeRenderer(), MeshPolygonTest.USE_PERTURBATION);
//				break;
//			case 2:  new MeshPolygonTest(fullPanel, renderers.getFilledRenderer(), MeshPolygonTest.USE_PERTURBATION);
//				break;
//			case 3:	 new CenteredTriangleTest(fullPanel, renderers.getFilledRenderer());
//				break;
//			case 4:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.GREEN);
//				interpreter = new SimpInterpreter("simp/page4.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
//			case 5:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.RED);
//				interpreter = new SimpInterpreter("simp_old/page5.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
//			case 6:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.WHITE);
//				System.out.println("Working Directory = " + System.getProperty("user.dir"));
//				interpreter = new SimpInterpreter("simp_old/page6.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
//			case 7:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.WHITE);
//                interpreter = new SimpInterpreter("simp_old/page7.simp", depthCueingDrawable, renderers);
//                interpreter.interpret();
//                break;
//
//			case 0:  depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.WHITE);
//				interpreter = new SimpInterpreter("simp_old/page8.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//			default:
//			    defaultPage();
//				break;
//		}
//	}
//
    private void defaultPage() {
        image.clear();
        fullPanel.clear();
    }
}
