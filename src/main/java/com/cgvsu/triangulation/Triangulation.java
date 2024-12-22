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
            for (int vertex = 1; vertex < polygon.getVertexIndices().size() - 1; vertex++) {
                Polygon polygonResult = new Polygon();

                ArrayList<Integer> vertexIndices = (ArrayList<Integer>) getVertexes(
                        polygon.getVertexIndices(),
                        0, vertex, vertex + 1);
                polygonResult.setVertexIndices(vertexIndices);

                if (!polygon.getTextureVertexIndices().isEmpty()) {
                    ArrayList<Integer> textureVertexIndices = (ArrayList<Integer>) getVertexes(
                            polygon.getTextureVertexIndices(),
                            0, vertex, vertex + 1);
                    polygonResult.setTextureVertexIndices(textureVertexIndices);
                }

                if (!polygon.getNormalIndices().isEmpty()) {
                    ArrayList<Integer> normalIndices = (ArrayList<Integer>) getVertexes(
                            polygon.getNormalIndices(),
                            0, vertex, vertex + 1);
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
    public static double determinator(int[][] arr) {
        return arr[0][0] * arr[1][1] * arr[2][2] + arr[1][0] * arr[0][2] * arr[2][1] +
                arr[0][1] * arr[1][2] * arr[2][0] - arr[0][2] * arr[1][1] * arr[2][0] -
                arr[0][0] * arr[1][2] * arr[2][1] - arr[0][1] * arr[1][0] * arr[2][2];
    }

    public static double[] barizentricCoordinates(int x, int y, int[] arrX, int[] arrY) {
        final double generalDeterminant = determinator(new int[][]{arrX, arrY, new int[]{1, 1, 1}});
        final double alfa = Math.abs(determinator(
                new int[][]{new int[]{x, arrX[1], arrX[2]}, new int[]{y, arrY[1], arrY[2]}, new int[]{1, 1, 1}}) /
                generalDeterminant);
        final double betta = Math.abs(determinator(
                new int[][]{new int[]{arrX[0], x, arrX[2]}, new int[]{arrY[0], y, arrY[2]}, new int[]{1, 1, 1}}) /
                generalDeterminant);
        final double gamma = Math.abs(determinator(
                new int[][]{new int[]{arrX[0], arrX[1], x}, new int[]{arrY[0], arrY[1], y}, new int[]{1, 1, 1}}) /
                generalDeterminant);
        return new double[]{alfa, betta, gamma};
    }
}
