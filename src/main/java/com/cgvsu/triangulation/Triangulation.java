package com.cgvsu.triangulation;

import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Triangulation {

    public static List<Polygon> triangulate(List<Polygon> polygons) {
        int vertexCount = 0;
        for (Polygon polygon : polygons) {
            vertexCount += polygon.getVertexIndices().size() - 2;
        }

        List<Polygon> result = new ArrayList<>(vertexCount);

        for (Polygon polygon : polygons) {
            final int nVertices = polygon.getVertexIndices().size();
            for (int vertex = 1; vertex < nVertices - 1; vertex++) {
                Polygon polygonResult = new Polygon();

                ArrayList<Integer> vertexIndices = (ArrayList<Integer>) getVertexes(
                        polygon.getVertexIndices(),
                        0,
                        vertex,
                        vertex + 1
                );
                polygonResult.setVertexIndices(vertexIndices);

                if (!polygon.getTextureVertexIndices().isEmpty()) {
                    ArrayList<Integer> textureVertexIndices = (ArrayList<Integer>) getVertexes(
                            polygon.getTextureVertexIndices(),
                            0,
                            vertex,
                            vertex + 1
                    );
                    polygonResult.setTextureVertexIndices(textureVertexIndices);
                }

                if (!polygon.getNormalIndices().isEmpty()) {
                    ArrayList<Integer> normalIndices = (ArrayList<Integer>) getVertexes(
                            polygon.getNormalIndices(),
                            0,
                            vertex,
                            vertex + 1
                    );
                    polygonResult.setNormalIndices(normalIndices);
                }

                result.add(polygonResult);
            }
        }
        return result;
    }

    public static List<Integer> getVertexes(List<Integer> vertexes, int v1, int v2, int v3) {
        return new ArrayList<>(Arrays.asList(
                vertexes.get(v1),
                vertexes.get(v2),
                vertexes.get(v3)
        ));
    }
}
