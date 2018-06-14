package client.interpreter;

import java.util.Stack;

//import notProvided.client.Clipper;

import client.RendererTrio;
import geometry.Point3DH;
import geometry.Vertex3D;
import geometry.Transformation;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;


public class SimpInterpreter {
	private static final int NUM_TOKENS_FOR_POINT = 3;
	private static final int NUM_TOKENS_FOR_COMMAND = 1;
	private static final int NUM_TOKENS_FOR_COLORED_VERTEX = 6;
	private static final int NUM_TOKENS_FOR_UNCOLORED_VERTEX = 3;
	private static final char COMMENT_CHAR = '#';
	private RenderStyle renderStyle;
	private RendererTrio renderers;

	private Transformation CTM;
	private Transformation worldToScreen;
	private Stack<Transformation> transformationStack;

	private static int WORLD_LOW_X = -100;
	private static int WORLD_HIGH_X = 100;
	private static int WORLD_LOW_Y = -100;
	private static int WORLD_HIGH_Y = 100;

	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;

	private Color defaultColor = Color.WHITE;
	private Color ambientLight = Color.BLACK;

	private Drawable drawable;
	private Transformation cameraToScreen;
//	private Clipper clipper;

	public enum RenderStyle {
		FILLED,
		WIREFRAME;
	}
	public SimpInterpreter(String filename,
			Drawable drawable,
			RendererTrio renderers) {
		this.drawable = drawable;
        this.renderers = renderers;
		this.defaultColor = Color.WHITE;

		makeWorldToScreenTransform(drawable.getDimensions());

        transformationStack = new Stack<>();
        CTM = Transformation.identity();

		reader = new LineBasedReader(filename);
		readerStack = new Stack<>();
		renderStyle = RenderStyle.FILLED;
	}

	private void makeWorldToScreenTransform(Dimensions dimensions) {
		// [A2]
		// World space -> View space:
		//    1) Camera is fixed(Looking down z value)
		//    2) Parallel Projection
		// Do not thing, just ignoring Z values

		// View space -> Screen space:
		//    Scale (200 to 650) and translate so that the origin is in the middle of the screen
		//worldToScreen = ;
        double scaleX = (double)dimensions.getWidth() / (double)(WORLD_HIGH_X - WORLD_LOW_X) ;
        double scaleY = (double)dimensions.getHeight() / (double)(WORLD_HIGH_Y - WORLD_LOW_Y);
        double transX = (double)dimensions.getWidth() / 2.0;
        double transY = (double)dimensions.getHeight() / 2.0;

        worldToScreen = Transformation.identity();
        worldToScreen.translate(transX, transY, 0);
        worldToScreen.scale(scaleX, scaleY, 1);
	}

	public void interpret() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretLine(line);
			while(!reader.hasNext()) {
				if(readerStack.isEmpty()) {
					return;
				}
				else {
					reader = readerStack.pop();
				}
			}
		}
	}

	public void interpretLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretCommand(tokens);
			}
		}
	}

	private void interpretCommand(String[] tokens) {
		switch(tokens[0]) {
		case "{" :      push();   break;
		case "}" :      pop();    break;
		case "wire" :   wire();   break;
		case "filled" : filled(); break;

		case "file" :		interpretFile(tokens);		break;
		case "scale" :		interpretScale(tokens);		break;
		case "translate" :	interpretTranslate(tokens);	break;
		case "rotate" :		interpretRotate(tokens);	break;
		case "line" :		interpretLine(tokens);		break;
		case "polygon" :	interpretPolygon(tokens);	break;
//		case "camera" :		interpretCamera(tokens);	break;
//		case "surface" :	interpretSurface(tokens);	break;
//		case "ambient" :	interpretAmbient(tokens);	break;
//		case "depth" :		interpretDepth(tokens);		break;
//		case "obj" :		interpretObj(tokens);		break;

		default :
			System.err.println("bad input line: " + tokens);
			break;
		}
	}

	private void push() {
        transformationStack.push(new Transformation(CTM));
        CTM = transformationStack.peek();
	}

	private void pop() {
        transformationStack.pop();
        if(!transformationStack.empty()) {
            CTM = transformationStack.peek();
        } else {
            CTM = Transformation.identity();
        }
	}

	private void wire() {
        renderStyle = RenderStyle.WIREFRAME;
	}

	private void filled() {
        renderStyle = RenderStyle.FILLED;
	}

	// this one is complete.
	private void interpretFile(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"';
		String filename = quotedFilename.substring(1, length-1);
		file("simp/" + filename + ".simp");
	}

	private void file(String filename) {
		readerStack.push(reader);
		reader = new LineBasedReader(filename);
	}

	private void interpretScale(String[] tokens) {
		double sx = cleanNumber(tokens[1]);
		double sy = cleanNumber(tokens[2]);
		double sz = cleanNumber(tokens[3]);
		CTM.scale(sx, sy, sz);
	}

	private void interpretTranslate(String[] tokens) {
		double tx = cleanNumber(tokens[1]);
		double ty = cleanNumber(tokens[2]);
		double tz = cleanNumber(tokens[3]);
        CTM.translate(tx, ty, tz);
	}

	private void interpretRotate(String[] tokens) {
		String axisString = tokens[1];
		double angleInDegrees = cleanNumber(tokens[2]);
        double angleInRad = (angleInDegrees / 360) * (2.0 * Math.PI);

		double rotateX = 0;
		double rotateY = 0;
		double rotateZ = 0;

		if (axisString.equalsIgnoreCase("X")) {
		    rotateX = angleInRad;
        } else if (axisString.equalsIgnoreCase("Y")) {
		    rotateY = angleInRad;
        } else if (axisString.equalsIgnoreCase("Z")) {
		    rotateZ = angleInRad;
        }
        CTM.rotate(rotateX, rotateY, rotateZ);
	}

	private double cleanNumber(String string) {
		return Double.parseDouble(string);
	}

	private enum VertexColors {
		COLORED(NUM_TOKENS_FOR_COLORED_VERTEX),
		UNCOLORED(NUM_TOKENS_FOR_UNCOLORED_VERTEX);

		private int numTokensPerVertex;

		private VertexColors(int numTokensPerVertex) {
			this.numTokensPerVertex = numTokensPerVertex;
		}
		public int numTokensPerVertex() {
			return numTokensPerVertex;
		}
	}

	private void interpretLine(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 2, 1);
		// object space to world space
		Vertex3D p1 = CTM.mulitplyVertex(vertices[0]);
		Vertex3D p2 = CTM.mulitplyVertex(vertices[1]);
		line(p1, p2);
	}

	private void interpretPolygon(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 3, 1);
        // object space to world space
        Vertex3D p1 = CTM.mulitplyVertex(vertices[0]);
        Vertex3D p2 = CTM.mulitplyVertex(vertices[1]);
        Vertex3D p3 = CTM.mulitplyVertex(vertices[2]);
        polygon(p1, p2, p3);
	}

	public Vertex3D[] interpretVertices(String[] tokens, int numVertices, int startingIndex) {
		VertexColors vertexColors = verticesAreColored(tokens, numVertices);
		Vertex3D vertices[] = new Vertex3D[numVertices];

		for(int index = 0; index < numVertices; index++) {
			vertices[index] = interpretVertex(tokens, startingIndex + index * vertexColors.numTokensPerVertex(), vertexColors);
		}
		return vertices;
	}

	public VertexColors verticesAreColored(String[] tokens, int numVertices) {
		return hasColoredVertices(tokens, numVertices) ? VertexColors.COLORED :
														 VertexColors.UNCOLORED;
	}

	public boolean hasColoredVertices(String[] tokens, int numVertices) {
		return tokens.length == numTokensForCommandWithNVertices(numVertices);
	}

	public int numTokensForCommandWithNVertices(int numVertices) {
		return NUM_TOKENS_FOR_COMMAND + numVertices*(NUM_TOKENS_FOR_COLORED_VERTEX);
	}

	private Vertex3D interpretVertex(String[] tokens, int startingIndex, VertexColors colored) {
		Point3DH point = interpretPoint(tokens, startingIndex);

		Color color = defaultColor;
		if(colored == VertexColors.COLORED) {
			color = interpretColor(tokens, startingIndex + NUM_TOKENS_FOR_POINT);
		}
		return new Vertex3D(point, color);
	}

	public Point3DH interpretPoint(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		return new Point3DH(x, y, z);
	}

	public Color interpretColor(String[] tokens, int startingIndex) {
		double r = cleanNumber(tokens[startingIndex]);
		double g = cleanNumber(tokens[startingIndex + 1]);
		double b = cleanNumber(tokens[startingIndex + 2]);
		return new Color(r, g, b);
	}

	private void line(Vertex3D p1, Vertex3D p2) {
		Vertex3D screenP1 = transformToScreen(p1);
		Vertex3D screenP2 = transformToScreen(p2);
		renderers.getLineRenderer().drawLine(screenP1, screenP2, drawable);

	}

	private void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
		Vertex3D screenP1 = transformToScreen(p1);
		Vertex3D screenP2 = transformToScreen(p2);
		Vertex3D screenP3 = transformToScreen(p3);

        PolygonRenderer renderer;
        if(renderStyle == RenderStyle.FILLED) {
            renderer = renderers.getFilledRenderer();
        } else {
            renderer = renderers.getWireframeRenderer();
        }
        Polygon p = Polygon.make(screenP1, screenP2, screenP3);
        renderer.drawPolygon(p, drawable);
	}

	private Vertex3D transformToScreen(Vertex3D vertex) {
	    return worldToScreen.mulitplyVertex(vertex);
	}

//	private Vertex3D transformToCamera(Vertex3D vertex) {
//	    // [A2] Camera is fixed at looking down z axis....
//        return vertex;
//	}
}
