package com.cgvsu.normalize;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class Normalize {

    public static List<Vector3f> normale(final List<Vector3f> vertices, final List<Polygon> polygons) {
        List<Vector3f> result = new ArrayList<>(vertices.size());

        for (Vector3f vertex : vertices) {
            Vector3f normal = calculateVertexNormal(vertex, vertices, polygons);
            result.add(normal);
        }

        return result;
    }

    private static Vector3f calculateVertexNormal(Vector3f vertex, List<Vector3f> vertices, List<Polygon> polygons) {
        int countP = 0;
        Vector3f normal = new Vector3f(0, 0, 0);

        for (Polygon polygon : polygons) {
            if (isVertexInPolygon(vertex, vertices, polygon)) {
                countP++;
                normal.add(computeFaceNormal(
                        vertices.get(polygon.getVertexIndices().get(0)),
                        vertices.get(polygon.getVertexIndices().get(1)),
                        vertices.get(polygon.getVertexIndices().get(2))));
            }
        }

        if (countP > 0) {
            normal.div(countP);
            normalizeVector(normal);
        }

        return normal;
    }

    private static boolean isVertexInPolygon(Vector3f vertex, List<Vector3f> vertices, Polygon polygon) {
        return vertices.get(polygon.getVertexIndices().get(0)).equals(vertex) ||
                vertices.get(polygon.getVertexIndices().get(1)).equals(vertex) ||
                vertices.get(polygon.getVertexIndices().get(2)).equals(vertex);
    }

    private static Vector3f computeFaceNormal(Vector3f v1, Vector3f v2, Vector3f v3) {
        Vector3f a = new Vector3f(0, 0, 0);
        a.sub(v1, v2);

        Vector3f b = new Vector3f(0, 0, 0);
        b.sub(v1, v3);

        Vector3f normal = new Vector3f(0, 0, 0);
        normal.mul(a, b);

        return normal;
    }

    private static void normalizeVector(Vector3f vector) {
        double length = Math.sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z);
        if (length != 0) {
            vector.div((float)length);
        }
    }
}
