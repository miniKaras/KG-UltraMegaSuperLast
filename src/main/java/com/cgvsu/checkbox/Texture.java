package com.cgvsu.checkbox;

import com.cgvsu.math.Vector2f;
import com.cgvsu.model.Model;

public class Texture {

    public static double[] getGradientCoordinatesTexture(double[] barizentric, Vector2f[] texture) {
        double x = (barizentric[0] * texture[0].x) + (barizentric[1] * texture[1].x) + (barizentric[2] * texture[2].x);
        double y = (barizentric[0] * texture[0].y) + (barizentric[1] * texture[1].y) + (barizentric[2] * texture[2].y);
        return new double[] {x, y};
    }

    public static void applyTexture(double[] barizentric, Vector2f[] textures, Model mesh, int[] rgb) {
        double[] textureCoordinates = getGradientCoordinatesTexture(barizentric, textures);
        int u = (int) Math.round(textureCoordinates[0] * (mesh.imageToText.width - 1));
        int v = (int) Math.round(textureCoordinates[1] * (mesh.imageToText.height - 1));

        if (isValidTextureCoordinate(u, v, mesh.imageToText.width, mesh.imageToText.height)) {
            rgb[0] = mesh.imageToText.pixelData[u][v][0];
            rgb[1] = mesh.imageToText.pixelData[u][v][1];
            rgb[2] = mesh.imageToText.pixelData[u][v][2];
        }
    }

    private static boolean isValidTextureCoordinate(int u, int v, int width, int height) {
        return u >= 0 && v >= 0 && u < width && v < height;
    }
}
