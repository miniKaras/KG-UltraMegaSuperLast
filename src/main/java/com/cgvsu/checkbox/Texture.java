package com.cgvsu.checkbox;

import com.cgvsu.math.Vector2f;
import com.cgvsu.model.Model;

public class Texture {

    public static double[] getGradientCoordinatesTexture(double[] barizentric, Vector2f[] texture) {
        // Для удобства вычислим координаты отдельно
        double tx = (barizentric[0] * texture[0].x)
                + (barizentric[1] * texture[1].x)
                + (barizentric[2] * texture[2].x);

        double ty = (barizentric[0] * texture[0].y)
                + (barizentric[1] * texture[1].y)
                + (barizentric[2] * texture[2].y);

        return new double[] { tx, ty };
    }

    public static void texture(double[] barizentric, Vector2f[] textures, Model mesh, int[] rgb){
        // Получаем координаты текстуры
        double[] texture = getGradientCoordinatesTexture(barizentric, textures);

        // Рассчитываем целочисленные координаты в текстуре
        int u = (int) Math.round(texture[0] * (mesh.imageToText.wight - 1));
        int v = (int) Math.round(texture[1] * (mesh.imageToText.height - 1));

        // Проверяем, что координаты не вышли за границы
        if (u < mesh.imageToText.wight && v < mesh.imageToText.height) {
            // Заполняем rgb данными пикселя из текстуры
            rgb[0] = mesh.imageToText.pixelData[u][v][0];
            rgb[1] = mesh.imageToText.pixelData[u][v][1];
            rgb[2] = mesh.imageToText.pixelData[u][v][2];
        }
    }
}
