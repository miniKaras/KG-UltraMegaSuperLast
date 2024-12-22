package com.cgvsu.rasterization;

import com.cgvsu.checkbox.Lighting;
import com.cgvsu.checkbox.Texture;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class TriangleRasterization {

    public static void draw(
            final GraphicsContext graphicsContext,
            final int[] coordX,
            final int[] coordY,
            final Color[] color,
            final double[][] zBuff,
            final double[] deepZ,
            Vector3f[] normals,
            Vector2f[] textures,
            double[] light,
            Model mesh) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        sortVertices(coordX, coordY, deepZ, normals, textures, color);

        rasterizeTriangle(graphicsContext, pixelWriter, coordX, coordY, color, zBuff, deepZ, normals, textures, light, mesh);
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

    private static void rasterizeTriangle(
            GraphicsContext graphicsContext,
            PixelWriter pixelWriter,
            int[] coordX,
            int[] coordY,
            Color[] color,
            double[][] zBuff,
            double[] deepZ,
            Vector3f[] normals,
            Vector2f[] textures,
            double[] light,
            Model mesh) {

        rasterizeHalfTriangle(coordX, coordY, color, zBuff, deepZ, normals, textures, light, mesh, pixelWriter, coordY[0], coordY[1]);
        rasterizeHalfTriangle(coordX, coordY, color, zBuff, deepZ, normals, textures, light, mesh, pixelWriter, coordY[1], coordY[2]);
    }

    private static void rasterizeHalfTriangle(
            int[] coordX,
            int[] coordY,
            Color[] color,
            double[][] zBuff,
            double[] deepZ,
            Vector3f[] normals,
            Vector2f[] textures,
            double[] light,
            Model mesh,
            PixelWriter pixelWriter,
            int startY,
            int endY) {

        for (int y = startY; y <= endY; y++) {
            int xl = interpolateEdge(y, coordY[0], coordY[1], coordX[0], coordX[1]);
            int xr = interpolateEdge(y, coordY[0], coordY[2], coordX[0], coordX[2]);

            if (xl > xr) {
                int temp = xl;
                xl = xr;
                xr = temp;
            }

            fillScanline(xl, xr, y, coordX, coordY, color, zBuff, deepZ, normals, textures, light, mesh, pixelWriter);
        }
    }

    private static int interpolateEdge(int y, int y0, int y1, int x0, int x1) {
        return (y1 - y0 == 0) ? x0 : (y - y0) * (x1 - x0) / (y1 - y0) + x0;
    }

    private static void fillScanline(
            int xl,
            int xr,
            int y,
            int[] coordX,
            int[] coordY,
            Color[] color,
            double[][] zBuff,
            double[] deepZ,
            Vector3f[] normals,
            Vector2f[] textures,
            double[] light,
            Model mesh,
            PixelWriter pixelWriter) {

        for (int x = xl; x <= xr; x++) {
            if (isWithinBounds(x, y, zBuff)) {
                double[] barycentric = calculateBarycentricCoordinates(x, y, coordX, coordY);

                if (isValidBarycentric(barycentric)) {
                    double zNew = interpolateCoordinatesZBuffer(barycentric, deepZ);

                    if (zBuff[x][y] <= zNew) {
                        continue;
                    }

                    int[] rgb = Lighting.getGradientCoordinatesRGB(barycentric, color);

                    if (mesh.isActiveTexture) {
                        Texture.applyTexture(barycentric, textures, mesh, rgb);
                    }

                    if (mesh.isActiveLighting) {
                        Lighting.light(barycentric, normals, light, rgb);
                    }

                    zBuff[x][y] = zNew;
                    pixelWriter.setColor(x, y, Color.rgb(rgb[0], rgb[1], rgb[2]));
                }
            }
        }
    }

    private static boolean isWithinBounds(int x, int y, double[][] zBuff) {
        return x >= 0 && y >= 0 && x < zBuff.length && y < zBuff[0].length;
    }

    private static boolean isValidBarycentric(double[] barycentric) {
        return !Double.isNaN(barycentric[0]) &&
                !Double.isNaN(barycentric[1]) &&
                !Double.isNaN(barycentric[2]) &&
                Math.abs(barycentric[0] + barycentric[1] + barycentric[2] - 1) < 1e-7f;
    }

    public static double interpolateCoordinatesZBuffer(final double[] barycentricCoords, final double[] deepZ) {
        return barycentricCoords[0] * deepZ[0] + barycentricCoords[1] * deepZ[1] + barycentricCoords[2] * deepZ[2];
    }

    public static double[] calculateBarycentricCoordinates(int x, int y, int[] coordX, int[] coordY) {
        double generalDet = determinant(new int[][] { coordX, coordY, new int[] { 1, 1, 1 } });
        double alpha = Math.abs(determinant(new int[][] { new int[] { x, coordX[1], coordX[2] }, new int[] { y, coordY[1], coordY[2] }, new int[] { 1, 1, 1 } }) / generalDet);
        double beta = Math.abs(determinant(new int[][] { new int[] { coordX[0], x, coordX[2] }, new int[] { coordY[0], y, coordY[2] }, new int[] { 1, 1, 1 } }) / generalDet);
        double gamma = Math.abs(determinant(new int[][] { new int[] { coordX[0], coordX[1], x }, new int[] { coordY[0], coordY[1], y }, new int[] { 1, 1, 1 } }) / generalDet);

        return new double[] { alpha, beta, gamma };
    }

    private static double determinant(int[][] matrix) {
        return matrix[0][0] * matrix[1][1] * matrix[2][2] + matrix[1][0] * matrix[0][2] * matrix[2][1] + matrix[0][1] * matrix[1][2] * matrix[2][0]
                - matrix[0][2] * matrix[1][1] * matrix[2][0] - matrix[0][0] * matrix[1][2] * matrix[2][1] - matrix[0][1] * matrix[1][0] * matrix[2][2];
    }

    public static void sortVertices(int[] coordX, int[] coordY, double[] deepZ, Vector3f[] normals, Vector2f[] textures, Color[] color) {
        if (coordY[0] > coordY[1]) {
            swap(0, 1, coordX, coordY, deepZ, normals, textures, color);
        }
        if (coordY[0] > coordY[2]) {
            swap(0, 2, coordX, coordY, deepZ, normals, textures, color);
        }
        if (coordY[1] > coordY[2]) {
            swap(1, 2, coordX, coordY, deepZ, normals, textures, color);
        }
    }

    private static void swap(int i, int j, int[] coordX, int[] coordY, double[] deepZ, Vector3f[] normals, Vector2f[] textures, Color[] color) {
        int tempY = coordY[i];
        coordY[i] = coordY[j];
        coordY[j] = tempY;

        int tempX = coordX[i];
        coordX[i] = coordX[j];
        coordX[j] = tempX;

        double tempZ = deepZ[i];
        deepZ[i] = deepZ[j];
        deepZ[j] = tempZ;

        Color tempColor = color[i];
        color[i] = color[j];
        color[j] = tempColor;

        Vector3f tempNormal = normals[i];
        normals[i] = normals[j];
        normals[j] = tempNormal;

        Vector2f tempTexture = textures[i];
        textures[i] = textures[j];
        textures[j] = tempTexture;
    }
}