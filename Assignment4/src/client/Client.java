package client;


import client.interpreter.SimpInterpreter;
import geometry.Point2D;
import windowing.PageTurner;
import windowing.drawable.*;
import windowing.graphics.Dimensions;


public class Client implements PageTurner {
	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int NUM_PAGES = 16;
	private static final Dimensions PANEL_SIZE = new Dimensions(650, 650);

	private boolean hasArgument = false;
	private String filename;
	private final Drawable drawable;
	private int pageNumber = 0;
	
	private Drawable image;
	private Drawable fullPanel;

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
			case 1:  filename = "page-a1";	 break;
			case 2:  filename = "page-a2";	 break;
			case 3:	 filename = "page-a3";	 break;
			case 4:  filename = "page-b1";	 break;
			case 5:  filename = "page-b2";	 break;
			case 6:  filename = "page-b3";	 break;
			case 7:  filename = "page-c1";	 break;
			case 8:  filename = "page-c2";	 break;
			case 9:  filename = "page-c3";	 break;
			case 10: filename = "page-d";	 break;
			case 11: filename = "page-e";	 break;
			case 12: filename = "page-f1";	 break;
			case 13:  filename = "page-f2";	 break;
			case 14:  filename = "page-g";	 break;
			case 15:  filename = "page-h";	 break;
			case 0:  filename = "page-i";	 break;


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
