package com.cgvsu.checkbox;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import static com.cgvsu.rasterization.TriangleRasterization.barizentricCoordinates;
import static com.cgvsu.rasterization.TriangleRasterization.interpolateCoordinatesZBuffer;

public class Greed {
    private static final double EPSILON = 1e-7;

    public static void drawLine(int x0, int y0, int x1, int y1,
                                final double[][] zBuff,
                                double[] deepZ,
                                int[] coordX, int[] coordY,
                                GraphicsContext graphicsContext) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        final int dx = Math.abs(x1 - x0);
        final int dy = Math.abs(y1 - y0);
        final int sx = (x0 < x1) ? 1 : -1;
        final int sy = (y0 < y1) ? 1 : -1;

        int err = dx - dy;

        while (true) {
            double[] barizentric = barizentricCoordinates(x0, y0, coordX, coordY);

            if (x0 >= 0 && y0 >= 0 && x0 < zBuff.length && y0 < zBuff[0].length) {
                if (!Double.isNaN(barizentric[0]) &&
                        !Double.isNaN(barizentric[1]) &&
                        !Double.isNaN(barizentric[2])) {

                    double zNew = interpolateCoordinatesZBuffer(barizentric, deepZ);
                    if (Math.abs(zBuff[x0][y0] - zNew) <= EPSILON) {
                        pixelWriter.setColor(x0, y0, Color.BLACK); // Цвет линии можно изменить при желании
                    }
                }
            }

            if (x0 == x1 && y0 == y1) {
                break;
            }

            int e2 = err << 1;

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
}
