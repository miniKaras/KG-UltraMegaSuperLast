package com.cgvsu.checkbox;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import static com.cgvsu.rasterization.TriangleRasterization.barizentricCoordinates;
import static com.cgvsu.rasterization.TriangleRasterization.interpolateCoordinatesZBuffer;

public class Greed {

    public static void drawLine(
            int x0, int y0, int x1, int y1,
            final double[][] zBuff,
            double[] deepZ,
            int[] coordX,
            int[] coordY,
            GraphicsContext graphicsContext) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = (x0 < x1) ? 1 : -1;
        int sy = (y0 < y1) ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if (isValidPixel(x0, y0, zBuff)) {
                double[] barizentric = barizentricCoordinates(x0, y0, coordX, coordY);

                if (isValidBarizentric(barizentric)) {
                    double zNew = interpolateCoordinatesZBuffer(barizentric, deepZ);

                    if (Math.abs(zBuff[x0][y0] - zNew) <= 1e-7f) {
                        pixelWriter.setColor(x0, y0, Color.BLACK); // Set pixel color
                    }
                }
            }

            if (x0 == x1 && y0 == y1) break;

            int e2 = err * 2;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    private static boolean isValidPixel(int x, int y, final double[][] zBuff) {
        return x >= 0 && y >= 0 && x < zBuff.length && y < zBuff[0].length;
    }

    private static boolean isValidBarizentric(double[] barizentric) {
        for (double b : barizentric) {
            if (Double.isNaN(b)) {
                return false;
            }
        }
        return true;
    }
}
