package com.cgvsu.checkbox;

import com.cgvsu.math.Vector3f;
import javafx.scene.paint.Color;

public class Lighting {

    private static final double K = 0.5;

    public static void calculateLight(int[] rgb, double[] light, Vector3f normal) {
        double l = computeLightIntensity(light, normal);

        rgb[0] = adjustColorComponent(rgb[0], l);
        rgb[1] = adjustColorComponent(rgb[1], l);
        rgb[2] = adjustColorComponent(rgb[2], l);
    }

    private static double computeLightIntensity(double[] light, Vector3f normal) {
        double l = -(light[0] * normal.x + light[1] * normal.y + light[2] * normal.z);
        return Math.max(0, l);
    }

    private static int adjustColorComponent(int colorComponent, double intensity) {
        return Math.min(255, (int) (colorComponent * (1 - K) + colorComponent * K * intensity));
    }

    public static int[] getGradientCoordinatesRGB(final double[] baristicCoords, final Color[] color) {
        int r = interpolateColorComponent(baristicCoords, color, Color::getRed);
        int g = interpolateColorComponent(baristicCoords, color, Color::getGreen);
        int b = interpolateColorComponent(baristicCoords, color, Color::getBlue);

        return new int[]{r, g, b};
    }

    private static int interpolateColorComponent(final double[] baristicCoords, final Color[] color, java.util.function.ToDoubleFunction<Color> channelExtractor) {
        return Math.min(255, (int) Math.abs(
                channelExtractor.applyAsDouble(color[0]) * 255 * baristicCoords[0] +
                        channelExtractor.applyAsDouble(color[1]) * 255 * baristicCoords[1] +
                        channelExtractor.applyAsDouble(color[2]) * 255 * baristicCoords[2]
        ));
    }

    public static Vector3f smoothingNormal(final double[] baristicCoords, final Vector3f[] normals) {
        return new Vector3f(
                (float) (baristicCoords[0] * normals[0].x + baristicCoords[1] * normals[1].x + baristicCoords[2] * normals[2].x),
                (float) (baristicCoords[0] * normals[0].y + baristicCoords[1] * normals[1].y + baristicCoords[2] * normals[2].y),
                (float) (baristicCoords[0] * normals[0].z + baristicCoords[1] * normals[1].z + baristicCoords[2] * normals[2].z)
        );
    }

    public static void light(final double[] barizentric, final Vector3f[] normals, double[] light, int[] rgb) {
        Vector3f smooth = smoothingNormal(barizentric, normals);
        calculateLight(rgb, light, smooth);
    }
}
