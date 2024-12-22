package com.cgvsu.model;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.triangulation.Triangulation;

import java.util.*;

public class Model {

    public ArrayList<Vector3f> vertices = new ArrayList<>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<>();
    public ArrayList<Vector3f> normals = new ArrayList<>();
    public ArrayList<Polygon> polygons = new ArrayList<>();
    public ArrayList<Polygon> polygonsWithoutTriangulation = new ArrayList<>();

    public void triangulate() {
        polygonsWithoutTriangulation = new ArrayList<>(polygons);
        polygons = new ArrayList<>(Triangulation.triangulate(polygons));
    }

    public void calculateNormals() {
        ArrayList<Vector3f> newNormals = new ArrayList<>();

        for (Vector3f vertex : vertices) {
            Vector3f normalSum = new Vector3f(0, 0, 0);
            int polygonCount = 0;

            for (Polygon polygon : polygons) {
                if (isVertexInPolygon(vertex, polygon, 0)) {
                    normalSum.add(getFaceNormal(polygon, 0, 1, 2));
                    polygonCount++;
                } else if (isVertexInPolygon(vertex, polygon, 1)) {
                    normalSum.add(getFaceNormal(polygon, 1, 0, 2));
                    polygonCount++;
                } else if (isVertexInPolygon(vertex, polygon, 2)) {
                    normalSum.add(getFaceNormal(polygon, 2, 1, 0));
                    polygonCount++;
                }
            }

            if (polygonCount > 0) {
                normalSum.del(polygonCount);
                normalSum.normalize();
            }

            newNormals.add(normalSum);
        }

        normals = newNormals;
    }

    private boolean isVertexInPolygon(Vector3f vertex, Polygon polygon, int index) {
        return vertices.get(polygon.getVertexIndices().get(index)).equals(vertex);
    }

    public Vector3f getFaceNormal(Polygon polygon, int v1, int v2, int v3) {
        Vector3f edge1 = new Vector3f();
        edge1.sub(vertices.get(polygon.getVertexIndices().get(v2)), vertices.get(polygon.getVertexIndices().get(v1)));

        Vector3f edge2 = new Vector3f();
        edge2.sub(vertices.get(polygon.getVertexIndices().get(v3)), vertices.get(polygon.getVertexIndices().get(v1)));

        Vector3f normal = new Vector3f();
        normal.mult(edge1, edge2);
        return normal;
    }
}



