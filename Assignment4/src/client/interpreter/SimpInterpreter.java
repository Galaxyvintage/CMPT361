package client.interpreter;

import java.util.ArrayList;
import java.util.Stack;


import client.Clipper;
import client.RendererTrio;
import geometry.*;
import polygon.Polygon;
import polygon.PolygonRenderer;
import shading.*;
import windowing.drawable.DepthCueingDrawable;
import windowing.drawable.Drawable;
import windowing.drawable.TranslatingDrawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;


public class SimpInterpreter {

    private static final int NUM_TOKENS_FOR_POINT = 3;
	private static final int NUM_TOKENS_FOR_COMMAND = 1;
	private static final int NUM_TOKENS_FOR_COLORED_VERTEX = 6;
	private static final int NUM_TOKENS_FOR_UNCOLORED_VERTEX = 3;
	private static final int VIEW_PLANE = -1; // d = -1;
	private static final char COMMENT_CHAR = '#';


	private static double WORLD_LOW_X = -100;
	private static double WORLD_HIGH_X = 100;
	private static double WORLD_LOW_Y = -100;
	private static double WORLD_HIGH_Y = 100;
	private static double WORLD_NEAR_Z = 0;
	private static double WORLD_FAR_Z = -200;

	private double kSpecular = 0.3;
	private double exponentSpecular = 0.8;
    private ShaderStyle shaderStyle;

    private Transformation CTM = Transformation.identity();
    private Transformation worldToView = Transformation.identity();
    private Transformation projectedToScreen = Transformation.identity();
    private Stack<Transformation> transformationStack;
    private ArrayList<Lighting> lightSources = new ArrayList<>();
    private RenderStyle renderStyle;
    private RendererTrio renderers;

	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;

	private Color defaultColor = Color.WHITE;
	private Color ambientLight = Color.BLACK;

	private Drawable drawable;
	private Clipper clipper;

    private Shader ambientShader;
    private FaceShader faceShader;
    private VertexShader vertexShader;
    private PixelShader pixelShader;

	public enum RenderStyle {
		FILLED,
		WIREFRAME;
	}

    public enum
    ShaderStyle {
        FLAT,
        GOURAUD,
        PHONG;
    };
	public SimpInterpreter(String filename,
			Drawable drawable,
			RendererTrio renderers) {
		this.drawable = drawable;
        this.renderers = renderers;
		this.defaultColor = Color.WHITE;

        transformationStack = new Stack<>();
        makeProjectedToScreenTransform(drawable.getDimensions());
		reader = new LineBasedReader(filename);
		readerStack = new Stack<>();
		renderStyle = RenderStyle.FILLED;
		shaderStyle = ShaderStyle.FLAT;
		ambientShader = c  -> ambientLight.multiply(c);
		faceShader = polygon -> polygon;
	}

	public Transformation getCTM() {
	    return this.CTM;
    }

    public RenderStyle getRenderStyle() {
	    return this.renderStyle;
    }

    public Vertex3D projectToScreen(Vertex3D vertex) {
        double z = vertex.getZ();
        Point3DH cameraPoint = vertex.getCameraPoint();
        Vertex3D out = projectedToScreen.multiplyVertex(vertex);
        out = out.euclidean();
        out = out.replacePoint(new Point3DH(out.getX(), out.getY(), z));
        out = out.replaceCameraPoint(cameraPoint);
        return out;
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

    private void makeProjectedToScreenTransform(Dimensions dimensions) {
        double scaleX;
        double scaleY;
        double transX;
        double transY;
        double transXToCenter = -1.0 * (WORLD_HIGH_X + WORLD_LOW_X) / 2.0;
        double transYToCenter = -1.0 * (WORLD_HIGH_Y + WORLD_LOW_Y) / 2.0;
        scaleX = dimensions.getWidth() / (WORLD_HIGH_X - WORLD_LOW_X);
        scaleY = dimensions.getHeight() / (WORLD_HIGH_Y - WORLD_LOW_Y);
        transX = dimensions.getWidth() / 2.0;
        transY = dimensions.getHeight() / 2.0;


        projectedToScreen = Transformation.identity();
        projectedToScreen.postMultiply(Transformation.translate(transX, transY, 0));
        projectedToScreen.postMultiply(Transformation.scale(scaleX, scaleY, 1));
        projectedToScreen.postMultiply(Transformation.translate(transXToCenter, transYToCenter, 0));
        projectedToScreen.postMultiply(Transformation.perspective(VIEW_PLANE));
    }

    private void interpretCommand(String[] tokens) {
		switch(tokens[0]) {
		case "{" :      push();    break;
		case "}" :      pop();     break;
		case "wire" :   wire();    break;
		case "filled" : filled();  break;
//		case "phong":   phong();   break;
//		case "gouraud": gouraud(); break;
		case "flat":    flat();    break;

		case "file" :		interpretFile(tokens);		break;
		case "scale" :		interpretScale(tokens);		break;
		case "translate" :	interpretTranslate(tokens);	break;
		case "rotate" :		interpretRotate(tokens);	break;
		case "line" :		interpretLine(tokens);		break;
		case "polygon" :	interpretPolygon(tokens);	break;
		case "camera" :		interpretCamera(tokens);	break;
		case "surface" :	interpretSurface(tokens);	break;
		case "ambient" :	interpretAmbient(tokens);	break;
		case "depth" :		interpretDepth(tokens);		break;
		case "obj" :		interpretObj(tokens);		break;
		case "light" : interpretLight(tokens);


		default :
			System.err.println("bad input line: " + tokens);
			break;
		}
	}

	private void push() {
		Transformation t = new Transformation(CTM);
        transformationStack.push(t);
        CTM = transformationStack.peek();
	}

	private void pop() {
        transformationStack.pop();
        if(!transformationStack.empty()) {
            CTM = transformationStack.peek();
        } else {
            CTM = worldToView;
        }
	}

	private void wire() {
        renderStyle = RenderStyle.WIREFRAME;
	}

	private void filled() {
        renderStyle = RenderStyle.FILLED;
	}

	private void flat() {
	    shaderStyle = ShaderStyle.FLAT;
	    faceShader = polygon -> {
	        Vertex3D v1= polygon.get(0);
            Vertex3D v2= polygon.get(1);
            Vertex3D v3= polygon.get(2);

            Point3DH point1 = v1.getCameraPoint().euclidean();
            Point3DH point2 = v2.getCameraPoint().euclidean();
	        Point3DH point3 = v3.getCameraPoint().euclidean();
	        Vertex3D center = new Vertex3D(point1.add(point2).add(point3).scale(1.0/3.0), Color.WHITE);
	        Point3DH normal;

	        // TODO : face normal
            if(v1.hasNormal() && v2.hasNormal() && v3.hasNormal()) {
                Point3DH n1 = v1.getNormal();
                Point3DH n2 = v2.getNormal();
                Point3DH n3 = v3.getNormal();
                Vertex3D n = new Vertex3D(n1.add(n2).add(n3).scale(1.0/3.0), Color.WHITE);
                normal = n.normalize().getPoint3D();
            } else {
                Vertex3D a = v2.subtract(v1);
                Vertex3D b = v3.subtract(v1);
                Vertex3D n= a.cross(b);
                n = n.normalize();
                normal = n.normalize().getPoint3D();
            }
            center.setNormal(normal);

            Color faceColor = ambientLight.multiply(defaultColor);
            for(int i = 0; i < lightSources.size(); i++) {
                Lighting lighting = lightSources.get(i);
                faceColor = faceColor.add(lighting.light(center, defaultColor, kSpecular, exponentSpecular));
            }
            polygon.faceColor = faceColor;
	        return polygon;
        };
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
		CTM.postMultiply(Transformation.scale(sx, sy, sz));
	}

	private void interpretTranslate(String[] tokens) {
		double tx = cleanNumber(tokens[1]);
		double ty = cleanNumber(tokens[2]);
		double tz = cleanNumber(tokens[3]);
        CTM.postMultiply(Transformation.translate(tx, ty, tz));
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
        CTM.postMultiply(Transformation.rotate(rotateX, rotateY, rotateZ));
	}

	private static double cleanNumber(String string) {
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
		// object space to view space
		Vertex3D p1 = CTM.multiplyVertex(vertices[0]);
		Vertex3D p2 = CTM.multiplyVertex(vertices[1]);
		line(p1, p2);
	}

	private void interpretPolygon(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 3, 1);
        // object space to view space
        Vertex3D p1 = CTM.multiplyVertex(vertices[0]);
        Vertex3D p2 = CTM.multiplyVertex(vertices[1]);
        Vertex3D p3 = CTM.multiplyVertex(vertices[2]);
        polygon(p1, p2, p3);
	}

	private void interpretCamera(String[] tokens) {
        WORLD_LOW_X  = cleanNumber(tokens[1]);
        WORLD_LOW_Y  = cleanNumber(tokens[2]);
        WORLD_HIGH_X = cleanNumber(tokens[3]);
        WORLD_HIGH_Y = cleanNumber(tokens[4]);
        WORLD_NEAR_Z = cleanNumber(tokens[5]);
        WORLD_FAR_Z  = cleanNumber(tokens[6]);

		worldToView = CTM.adjoint();
		for(int i = 0; i < transformationStack.size(); i++) {
            Transformation t = transformationStack.elementAt(i);
            t.preMultiply(worldToView);
            transformationStack.set(i, t);
        }
        CTM = transformationStack.peek();

		if(WORLD_HIGH_X - WORLD_LOW_X != WORLD_HIGH_Y - WORLD_LOW_Y) {
		    double x = WORLD_HIGH_X - WORLD_LOW_X;
		    double y = WORLD_HIGH_Y - WORLD_LOW_Y;
		    double aspectRatio = x/y;
            double originX = 0;
            double originY = 0;
		    double width;
		    double height;

		    if(aspectRatio > 1) {
		        width = drawable.getWidth();
		        height = width / aspectRatio;
                originY = (drawable.getHeight() - height) / 2.0;
            } else {
		        height = drawable.getHeight();
		        width = height * aspectRatio;
                originX = (drawable.getWidth() - width) / 2.0;
            }
            drawable = new TranslatingDrawable(drawable, new Point2D(originX, originY), new Dimensions((int)width, (int)height));
        }

        makeProjectedToScreenTransform(drawable.getDimensions());

		// clipping XY in device space????
        // NOTE!!!: I am clipping with respect to the drawable......
        clipper = new Clipper(0,
                              0,
                              drawable.getWidth() - 1,
                              drawable.getHeight() - 1,
                              WORLD_NEAR_Z,
                              WORLD_FAR_Z);
	}

    private void interpretSurface(String[] tokens) {
        double r = cleanNumber(tokens[1]);
        double g = cleanNumber(tokens[2]);
        double b = cleanNumber(tokens[3]);
        double ks = cleanNumber(tokens[4]);
        double kp = cleanNumber(tokens[5]);

        defaultColor = new Color(r, g, b);
        kSpecular = ks;
        exponentSpecular = kp;
    }

    private void interpretAmbient(String[] tokens) {
        double r = cleanNumber(tokens[1]);
        double g = cleanNumber(tokens[2]);
        double b = cleanNumber(tokens[3]);
        ambientLight = new Color(r, g, b);
    }

    private void interpretDepth(String[] tokens) {
        double near = cleanNumber(tokens[1]);
        double far = cleanNumber(tokens[2]);
        double r = cleanNumber(tokens[3]);
        double g = cleanNumber(tokens[4]);
        double b = cleanNumber(tokens[5]);
        drawable = new DepthCueingDrawable(drawable, near, far, new Color(r, g, b));
    }

    private void interpretObj(String[] tokens) {
        String quotedFilename = tokens[1];
        int length = quotedFilename.length();
        assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"';
        String filename = quotedFilename.substring(1, length-1);
	    objFile("simp/" + filename + ".obj");
    }

    private void interpretLight(String[] tokens) {
        double r = cleanNumber(tokens[1]);
        double g = cleanNumber(tokens[2]);
        double b = cleanNumber(tokens[3]);
        double A = cleanNumber(tokens[4]);
        double B = cleanNumber(tokens[5]);
        Color color = new Color(r, g, b);
        Lighting lighting = new Lighting(color, A, B, ambientLight, CTM.multiplyPoint(new Point3DH(0, 0, 0)));
        lightSources.add(lighting);
    }

    private void objFile(String filename) {
        ObjReader objReader = new ObjReader(filename, defaultColor);
        objReader.read();
        objReader.render(this);
    }

    public static Point3DH interpretPoint(String[] tokens, int startingIndex) {
        double x = cleanNumber(tokens[startingIndex]);
        double y = cleanNumber(tokens[startingIndex + 1]);
        double z = cleanNumber(tokens[startingIndex + 2]);
        return new Point3DH(x, y, z);
    }

    public static Color interpretColor(String[] tokens, int startingIndex) {
        double r = cleanNumber(tokens[startingIndex]);
        double g = cleanNumber(tokens[startingIndex + 1]);
        double b = cleanNumber(tokens[startingIndex + 2]);
        return new Color(r, g, b);
    }

    public static Point3DH interpretPointWithW(String[] tokens, int startingIndex) {
        double x = cleanNumber(tokens[startingIndex]);
        double y = cleanNumber(tokens[startingIndex + 1]);
        double z = cleanNumber(tokens[startingIndex + 2]);
        double w = cleanNumber(tokens[startingIndex + 3]);
        Point3DH point = new Point3DH(x, y, z, w);
        return point;
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

	private void line(Vertex3D p1, Vertex3D p2) {
        Line line  = new Line(p1, p2);
        line = clipper.clipZ(line);
        if(line != null) {
            Vertex3D screenP1 = projectToScreen(line.p1);
            Vertex3D screenP2 = projectToScreen(line.p2);
            renderers.getLineRenderer().drawLine(screenP1, screenP2, drawable);
        }
	}

	public void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
 	    Polygon polygon = Polygon.make(p1, p2, p3);
	    Polygon clipped = clipper.clipZ(polygon);

	    if(clipped != null) {
            ArrayList<Vertex3D> vertices = new ArrayList<>();
            // transform clipped vertices
            for (int i = 0; i < clipped.length(); i++) {
                Vertex3D v = projectToScreen(clipped.get(i));
                vertices.add(v);
            }

            polygon = Polygon.make(vertices.toArray(new Vertex3D[vertices.size()]));
            clipped = clipper.clipX(polygon);
            if(clipped != null) {
                clipped = clipper.clipY(clipped);
            }

            if(clipped != null) {
                PolygonRenderer renderer;
                if (renderStyle == RenderStyle.FILLED) {
                    renderer = renderers.getFilledRenderer();
                    renderer.drawPolygon(clipped, drawable, faceShader, ambientShader);
                } else {
                    renderer = renderers.getWireframeRenderer();
                    renderer.drawPolygon(clipped, drawable);
                }
            }
        }
	}

    public void polygon(Polygon polygon) {
        Polygon clipped = clipper.clipZ(polygon);

        if(clipped != null) {
            ArrayList<Vertex3D> vertices = new ArrayList<>();
            // transform clipped vertices
            for (int i = 0; i < clipped.length(); i++) {
                Vertex3D v = projectToScreen(clipped.get(i));
                vertices.add(v);
            }

            polygon = Polygon.make(vertices.toArray(new Vertex3D[vertices.size()]));
            clipped = clipper.clipX(polygon);
            if(clipped != null) {
                clipped = clipper.clipY(clipped);
            }

            if(clipped != null) {
                PolygonRenderer renderer;
                if (renderStyle == RenderStyle.FILLED) {
                    renderer = renderers.getFilledRenderer();
                    renderer.drawPolygon(clipped, drawable, faceShader, ambientShader);
                } else {
                    renderer = renderers.getWireframeRenderer();
                    renderer.drawPolygon(clipped, drawable);
                }
            }
        }
    }


}
