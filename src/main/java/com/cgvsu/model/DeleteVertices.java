package com.cgvsu.model;

import com.cgvsu.math.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DeleteVertices {

    public static Model deleteVerticesFromModel(Model model, List<Integer> vertexIndices) {
        List<Integer> vertexIndicesToDelete = vertexIndices.stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        deleteVertices(model.vertices, vertexIndicesToDelete);
        deleteDanglingPolygons(model.polygons, vertexIndices);
        shiftIndicesInPolygons(model.polygons, vertexIndices);

        return model;
    }

    private static void deleteVertices(List<Vector3f> modelVertices, List<Integer> vertexIndicesToDelete) {
        for (Integer index : vertexIndicesToDelete) {
            modelVertices.remove(index.intValue());
        }
    }

    private static void deleteDanglingPolygons(List<Polygon> modelPolygons, List<Integer> vertexIndicesToDelete) {
        modelPolygons.removeIf(polygon -> polygon.getVertexIndices().stream()
                .anyMatch(vertexIndicesToDelete::contains));
    }

    private static void shiftIndicesInPolygons(List<Polygon> modelPolygons, List<Integer> vertexIndicesToDelete) {
        List<Integer> sortedVertexIndicesToDelete = new ArrayList<>(vertexIndicesToDelete);
        sortedVertexIndicesToDelete.sort(Comparator.reverseOrder());

        for (Polygon polygon : modelPolygons) {
            ArrayList<Integer> newVertexIndices = new ArrayList<>();

            for (int polygonVertexIndex : polygon.getVertexIndices()) {
                int offset = countIndicesLessThan(polygonVertexIndex, sortedVertexIndicesToDelete);
                newVertexIndices.add(polygonVertexIndex - offset);
            }

            polygon.setVertexIndices(newVertexIndices);
        }
    }

    private static int countIndicesLessThan(int index, List<Integer> sortedVertexIndicesToDelete) {
        int count = 0;
        for (int vertexIndex : sortedVertexIndicesToDelete) {
            if (index >= vertexIndex) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
}
