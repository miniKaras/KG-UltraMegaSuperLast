package com.cgvsu.rasterization;

import com.cgvsu.checkbox.Greed;
import com.cgvsu.checkbox.Lighting;
import com.cgvsu.checkbox.Texture;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import static com.cgvsu.checkbox.Lighting.getGradientCoordinatesRGB;

public class TriangleRasterization {

    // Небольшая константа для сравнения с плавающей точкой
    private static final double EPSILON = 1e-7;

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
            Model mesh
    ) {
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        // Отсортируем вершины по оси Y
        sort(coordX, coordY, deepZ, normals, textures, color);

        // Отрисовка верхней части треугольника (между coordY[0] и coordY[1])
        for (int y = coordY[0]; y <= coordY[1]; y++) {
            // Линейная интерполяция X-координат
            int xl = (coordY[1] - coordY[0] == 0)
                    ? coordX[0]
                    : (y - coordY[0]) * (coordX[1] - coordX[0]) / (coordY[1] - coordY[0]) + coordX[0];
            int xr = (coordY[0] - coordY[2] == 0)
                    ? coordX[2]
                    : (y - coordY[2]) * (coordX[0] - coordX[2]) / (coordY[0] - coordY[2]) + coordX[2];

            // Убедимся, что xl <= xr
            if (xl > xr) {
                int tempX = xl;
                xl = xr;
                xr = tempX;
            }

            // Проходимся по X
            for (int x = xl; x <= xr; x++) {
                if (x >= 0 && y >= 0 && x < zBuff.length && y < zBuff[0].length) {
                    double[] barizentric = barizentricCoordinates(x, y, coordX, coordY);

                    // Проверяем NaN и сумму барицентрических координат
                    if (!Double.isNaN(barizentric[0])
                            && !Double.isNaN(barizentric[1])
                            && !Double.isNaN(barizentric[2])
                            && Math.abs(barizentric[0] + barizentric[1] + barizentric[2] - 1) < EPSILON) {

                        double zNew = interpolateCoordinatesZBuffer(barizentric, deepZ);

                        // Проверяем Z-буфер
                        if (zBuff[x][y] <= zNew) {
                            continue;
                        }

                        // Расчёт цвета по градиенту
                        int[] rgb = getGradientCoordinatesRGB(barizentric, color);

                        // Если включена текстура, применяем её
                        if (mesh.isActiveTexture) {
                            Texture.texture(barizentric, textures, mesh, rgb);
                        }

                        // Если включено освещение, рассчитываем свет
                        if (mesh.isActiveLighting) {
                            Lighting.light(barizentric, normals, light, rgb);
                        }

                        zBuff[x][y] = zNew;
                        pixelWriter.setColor(x, y, Color.rgb(rgb[0], rgb[1], rgb[2]));
                    }
                }
            }
        }

        // Отрисовка нижней части треугольника (между coordY[1] и coordY[2])
        for (int y = coordY[1]; y <= coordY[2]; y++) {
            int xl = (coordY[2] - coordY[1] == 0)
                    ? coordX[1]
                    : (y - coordY[1]) * (coordX[2] - coordX[1]) / (coordY[2] - coordY[1]) + coordX[1];
            int xr = (coordY[0] - coordY[2] == 0)
                    ? coordX[2]
                    : (y - coordY[2]) * (coordX[0] - coordX[2]) / (coordY[0] - coordY[2]) + coordX[2];

            if (xl > xr) {
                int tempX = xl;
                xl = xr;
                xr = tempX;
            }

            for (int x = xl; x <= xr; x++) {
                if (x >= 0 && y >= 0 && x < zBuff.length && y < zBuff[0].length) {
                    double[] barizentric = barizentricCoordinates(x, y, coordX, coordY);

                    if (!Double.isNaN(barizentric[0])
                            && !Double.isNaN(barizentric[1])
                            && !Double.isNaN(barizentric[2])
                            && Math.abs(barizentric[0] + barizentric[1] + barizentric[2] - 1) < EPSILON) {

                        double zNew = interpolateCoordinatesZBuffer(barizentric, deepZ);
                        if (zBuff[x][y] <= zNew) {
                            continue;
                        }

                        int[] rgb = getGradientCoordinatesRGB(barizentric, color);
                        if (mesh.isActiveTexture) {
                            Texture.texture(barizentric, textures, mesh, rgb);
                        }
                        if (mesh.isActiveLighting) {
                            Lighting.light(barizentric, normals, light, rgb);
                        }

                        zBuff[x][y] = zNew;
                        pixelWriter.setColor(x, y, Color.rgb(rgb[0], rgb[1], rgb[2]));
                    }
                }
            }
        }
    }

    public static double determinator(int[][] arr) {
        // Вычисляем определитель 3x3
        return arr[0][0] * arr[1][1] * arr[2][2]
                + arr[1][0] * arr[0][2] * arr[2][1]
                + arr[0][1] * arr[1][2] * arr[2][0]
                - arr[0][2] * arr[1][1] * arr[2][0]
                - arr[0][0] * arr[1][2] * arr[2][1]
                - arr[0][1] * arr[1][0] * arr[2][2];
    }

    public static double[] barizentricCoordinates(int x, int y, int[] arrX, int[] arrY) {
        // Общий определитель
        final double generalDeterminant = determinator(
                new int[][]{arrX, arrY, new int[]{1, 1, 1}}
        );

        // Коэффициенты (alfa, betta, gamma)
        final double alfa = Math.abs(
                determinator(new int[][]{
                        new int[]{x,      arrX[1], arrX[2]},
                        new int[]{y,      arrY[1], arrY[2]},
                        new int[]{1,           1,      1}
                }) / generalDeterminant
        );
        final double betta = Math.abs(
                determinator(new int[][]{
                        new int[]{arrX[0], x,      arrX[2]},
                        new int[]{arrY[0], y,      arrY[2]},
                        new int[]{1,           1,      1}
                }) / generalDeterminant
        );
        final double gamma = Math.abs(
                determinator(new int[][]{
                        new int[]{arrX[0], arrX[1], x},
                        new int[]{arrY[0], arrY[1], y},
                        new int[]{1,           1,      1}
                }) / generalDeterminant
        );

        return new double[]{alfa, betta, gamma};
    }

    public static void sort(
            int[] coordX,
            int[] coordY,
            double[] deepZ,
            Vector3f[] normals,
            Vector2f[] textures,
            Color[] color
    ) {
        // Тройная проверка для сортировки
        if (coordY[0] > coordY[1]) {
            reverse(0, 1, coordX, coordY, deepZ, normals, textures, color);
        }
        if (coordY[0] > coordY[2]) {
            reverse(0, 2, coordX, coordY, deepZ, normals, textures, color);
        }
        if (coordY[1] > coordY[2]) {
            reverse(1, 2, coordX, coordY, deepZ, normals, textures, color);
        }
    }

    private static void reverse(
            int i,
            int j,
            int[] coordX,
            int[] coordY,
            double[] deepZ,
            Vector3f[] normals,
            Vector2f[] textures,
            Color[] color
    ) {
        // Меняем местами Y
        int tempY = coordY[i];
        coordY[i] = coordY[j];
        coordY[j] = tempY;

        // Меняем местами X
        int tempX = coordX[i];
        coordX[i] = coordX[j];
        coordX[j] = tempX;

        // Меняем местами цвета
        Color colorTemp = color[i];
        color[i] = color[j];
        color[j] = colorTemp;

        // Меняем местами глубину Z
        double zBuf = deepZ[i];
        deepZ[i] = deepZ[j];
        deepZ[j] = zBuf;

        // Меняем местами нормали
        Vector3f normal = normals[i];
        normals[i] = normals[j];
        normals[j] = normal;

        // Меняем местами текстурные координаты
        Vector2f texture = textures[i];
        textures[i] = textures[j];
        textures[j] = texture;
    }

    public static double interpolateCoordinatesZBuffer(final double[] baristicCoords, final double[] deepZ) {
        // Вычисляем Z путём линейной комбинации
        return baristicCoords[0] * deepZ[0]
                + baristicCoords[1] * deepZ[1]
                + baristicCoords[2] * deepZ[2];
    }
}
