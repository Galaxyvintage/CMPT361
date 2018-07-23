package client.interpreter;

import java.util.ArrayList;
import java.util.List;

import geometry.Point3DH;
import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

class ObjReader {
	private static final char COMMENT_CHAR = '#';
	private static final int NOT_SPECIFIED = -1;

	private class ObjVertex {
	    private int vertexIndex;
        private int textureIndex;
        private int normalIndex;
        ObjVertex(int vertexIndex, int textureIndex, int normalIndex) {
            this.vertexIndex = vertexIndex;
            this.textureIndex = textureIndex;
            this.normalIndex = normalIndex;
        }

        private int getVertexIndex() {
            return vertexIndex;
        }

        private int getTextureIndex() {
            return textureIndex;
        }

        private int getNormalIndex() {
            return normalIndex;
        }
	}
	private class ObjFace extends ArrayList<ObjVertex> {
		private static final long serialVersionUID = -4130668677651098160L;
	}	
	private LineBasedReader reader;
	
	private List<Vertex3D> objVertices;
	private List<Vertex3D> transformedVertices;
	private List<Point3DH> objNormals;
    private List<Point3DH> transformedNormals;
	private List<ObjFace> objFaces;

	private Color defaultColor;
	
	ObjReader(String filename, Color defaultColor) {
        this.reader = new LineBasedReader(filename);
		this.objVertices = new ArrayList<>();
		this.transformedVertices = new ArrayList<>();
		this.objNormals = new ArrayList<>();
		this.transformedNormals = new ArrayList<>();
		this.objFaces = new ArrayList<>();
		this.defaultColor = defaultColor;
	}

	public void render(SimpInterpreter interpreter) {
        transformVertices(interpreter);
        transformNormals(interpreter);

        SimpInterpreter.RenderStyle style = interpreter.getRenderStyle();
        for(ObjFace face: objFaces) {
            Polygon polygon = polygonForFace(face);
            ArrayList<Polygon> polygons = new ArrayList<>();
            if(style == SimpInterpreter.RenderStyle.WIREFRAME) {
                polygons.add(polygon);
            } else {
                polygons = polygon.triangulate();
            }

            for(Polygon p: polygons) {
                interpreter.polygon(p);
            }
        }
	}
	
	private Polygon polygonForFace(ObjFace face) {
        Polygon result = Polygon.makeEmpty();
        for(ObjVertex objVertex: face) {
            Vertex3D vertex = transformedVertices.get(objVertex.getVertexIndex() - 1);
//            if(objVertex.getNormalIndex() != 0) {
                Point3DH normal = transformedNormals.get(objVertex.getNormalIndex() - 1);
                vertex.setNormal(normal);
//            }
            result.add(vertex);
        }
		return result;
	}

	public void read() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretObjLine(line);
		}
	}
	private void interpretObjLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretObjCommand(tokens);
			}
		}
	}

	private void interpretObjCommand(String[] tokens) {
		switch(tokens[0]) {
		case "v" :
		case "V" :
			interpretObjVertex(tokens);
			break;
		case "vn":
		case "VN":
			interpretObjNormal(tokens);
			break;
		case "f":
		case "F":
			interpretObjFace(tokens);
			break;
		default:	// do nothing
			break;
		}
	}
	private void interpretObjFace(String[] tokens) {
		ObjFace face = new ObjFace();
		
		for(int i = 1; i<tokens.length; i++) {
			String token = tokens[i];
			String[] subtokens = token.split("/");
			
			int vertexIndex  = objIndex(subtokens, 0, objVertices.size());
			int textureIndex = objIndex(subtokens, 1, 0);
			int normalIndex  = objIndex(subtokens, 2, objNormals.size());

			ObjVertex vertex = new ObjVertex(vertexIndex, textureIndex, normalIndex);
			face.add(vertex);
		}
		objFaces.add(face);
	}

	private int objIndex(String[] subtokens, int tokenIndex, int baseForNegativeIndices) {
        int result;
        if(subtokens.length <= tokenIndex) {
            return 0;
        }

        String token = subtokens[tokenIndex];
        if(token.isEmpty()) {
        	return 0;
		}

        int index = Integer.parseInt(subtokens[tokenIndex]);
        if(index < 0 ) {
            result = baseForNegativeIndices + index;
        } else {
            result = index;
        }
        return result;
	}

	private void interpretObjNormal(String[] tokens) {
		int numArgs = tokens.length - 1;
		if(numArgs != 3) {
			throw new BadObjFileException("vertex normal with wrong number of arguments : " + numArgs + ": " + tokens);				
		}
		Point3DH normal = SimpInterpreter.interpretPoint(tokens, 1);
		objNormals.add(normal);
	}
	private void interpretObjVertex(String[] tokens) {
		int numArgs = tokens.length - 1;
		Point3DH point = objVertexPoint(tokens, numArgs);
		Color color = objVertexColor(tokens, numArgs);
		Vertex3D v = new Vertex3D(point, color);
		objVertices.add(v);
	}

	private Color objVertexColor(String[] tokens, int numArgs) {
		if(numArgs == 6) {
			return SimpInterpreter.interpretColor(tokens, 4);
		}
		if(numArgs == 7) {
			return SimpInterpreter.interpretColor(tokens, 5);
		}
		return defaultColor;
	}

	private Point3DH objVertexPoint(String[] tokens, int numArgs) {
		if(numArgs == 3 || numArgs == 6) {
			return SimpInterpreter.interpretPoint(tokens, 1);
		}
		else if(numArgs == 4 || numArgs == 7) {
			return SimpInterpreter.interpretPointWithW(tokens, 1);
		}
		throw new BadObjFileException("vertex with wrong number of arguments : " + numArgs + ": " + tokens);
	}

	private void transformVertices(SimpInterpreter interpreter) {
	    for(Vertex3D vertex: objVertices) {
	        Vertex3D v = interpreter.getCTM().multiplyVertex(vertex);
	        transformedVertices.add(v);
        }
    }

    private void transformNormals(SimpInterpreter interpreter) {
        for(Point3DH point: objNormals) {
            Point3DH p = interpreter.getCTM().multiplyPoint(point);
            transformedNormals.add(p);
        }
    }
}