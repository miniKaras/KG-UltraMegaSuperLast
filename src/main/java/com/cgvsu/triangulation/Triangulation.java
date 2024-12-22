package com.cgvsu.triangulation;

import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Triangulation {

    public static List<Polygon> triangulate(List<Polygon> polygons) {
        int vertexCount = calculateVertexCount(polygons);
        List<Polygon> result = new ArrayList<>(vertexCount);

        for (Polygon polygon : polygons) {
            processPolygon(polygon, result);
        }

        return result;
    }

    private static int calculateVertexCount(List<Polygon> polygons) {
        int count = 0;
        for (Polygon polygon : polygons) {
            count += polygon.getVertexIndices().size() - 2;
        }
        return count;
    }

    private static void processPolygon(Polygon polygon, List<Polygon> result) {
        for (int vertex = 1; vertex < polygon.getVertexIndices().size() - 1; vertex++) {
            Polygon polygonResult = new Polygon();

            polygonResult.setVertexIndices(
                    (ArrayList<Integer>) getVertexes(polygon.getVertexIndices(), 0, vertex, vertex + 1)
            );

            if (!polygon.getTextureVertexIndices().isEmpty()) {
                polygonResult.setTextureVertexIndices(
                        (ArrayList<Integer>) getVertexes(polygon.getTextureVertexIndices(), 0, vertex, vertex + 1)
                );
            }

            if (!polygon.getNormalIndices().isEmpty()) {
                polygonResult.setNormalIndices(
                        (ArrayList<Integer>) getVertexes(polygon.getNormalIndices(), 0, vertex, vertex + 1)
                );
            }

            result.add(polygonResult);
        }
    }

    public static List<Integer> getVertexes(List<Integer> vertexes, int v1, int v2, int v3) {
        return new ArrayList<>(Arrays.asList(
                vertexes.get(v1),
                vertexes.get(v2),
                vertexes.get(v3)
        ));
    }
}
