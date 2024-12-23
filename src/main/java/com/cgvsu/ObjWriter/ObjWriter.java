package com.cgvsu.ObjWriter;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ObjWriter {

    private static final String OBJ_VERTEX_TOKEN = "v";
    private static final String OBJ_TEXTURE_TOKEN = "vt";
    private static final String OBJ_NORMAL_TOKEN = "vn";
    private static final String OBJ_FACE_TOKEN = "f";
    private static final String FILE_NAME = "model.obj";

    public static void write(Model model, String filename) {
        ArrayList<Vector3f> vertices = model.vertices;
        ArrayList<Vector2f> textureVertices = model.textureVertices;
        ArrayList<Vector3f> normals = model.normals;
        ArrayList<Polygon> polygons = model.polygons;

        try (FileWriter writer = new FileWriter(filename, false)) {
            for (Vector3f node : vertices) {
                writer.write(writeVertices(node));
            }
            for (Vector3f node : normals) {
                writer.write(writeNormals(node));
            }
            for (Vector2f node : textureVertices) {
                writer.write(writeTextureVertices(node));
            }
            for (Polygon polygon : polygons) {
                writer.write(writePolygons(polygon));
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static String writeVertices(Vector3f node) {
        return OBJ_VERTEX_TOKEN + " " + node.x + " " + node.y + " " + node.z + "\n";
    }

    public static String writeTextureVertices(Vector2f node) {
        return OBJ_TEXTURE_TOKEN + " " + node.x + " " + node.y + "\n";
    }

    public static String writeNormals(Vector3f node) {
        return OBJ_NORMAL_TOKEN + " " + node.x + " " + node.y + " " + node.z + "\n";
    }

    public static String writePolygons(Polygon polygon) {
        ArrayList<Integer> vertex = polygon.getVertexIndices();
        ArrayList<Integer> textures = polygon.getTextureVertexIndices();
        ArrayList<Integer> normals = polygon.getNormalIndices();

        StringBuilder result = new StringBuilder(OBJ_FACE_TOKEN);
        if (textures.isEmpty() && normals.isEmpty()) {
            for (Integer coord : vertex) {
                result.append(" ").append(coord + 1);
            }
        } else if (normals.isEmpty()) {
            for (int i = 0; i < vertex.size(); i++) {
                result.append(" ").append(vertex.get(i) + 1).append("/").append(textures.get(i) + 1);
            }
        } else if (textures.isEmpty()) {
            for (int i = 0; i < vertex.size(); i++) {
                result.append(" ").append(vertex.get(i) + 1).append("//").append(normals.get(i) + 1);
            }
        } else {
            for (int i = 0; i < vertex.size(); i++) {
                result.append(" ").append(vertex.get(i) + 1).append("/").append(textures.get(i) + 1).append("/").append(normals.get(i) + 1);
            }
        }
        return result.append("\n").toString();
    }


    public static void write(Model model) {
        write(model, FILE_NAME);
    }

}
