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
	private static final int NUM_PAGES = 13;
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
    		this.filename = args[0];
    		hasArgument = true;
		}
		this.drawable = drawable;
		this.renderers = new RendererTrio();
		createDrawables();
	}

	private void createDrawables() {
		image = new TranslatingDrawable(drawable, point(0, 0), dimensions(750, 750));
        image = new InvertedYDrawable(image);
		image = new ColoredDrawable(image, ARGB_WHITE);

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
			case 10: filename = "pageJ";	 break;
			case 11: filename = "pageK";	 break;
//			case 12: filename = "pageL";	 break;
			case 12: filename = "pageM";	 break;
			case 0:  filename = "pageN";	 break;

			default: defaultPage();
				return;
		}
		interpreter = new SimpInterpreter("simp/" + filename + ".simp", fullPanel, renderers);
		interpreter.interpret();
	}

    private void defaultPage() {
        image.clear();
        fullPanel.clear();
    }
}
