package com.cgvsu.texture;

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageToText {

    public int[][][] pixelData;
    public int width;
    public int height;

    public void loadImage(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));

            if (img == null) {
                throw new IOException("Image loading failed. The file may not be an image.");
            }

            initializePixelData(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializePixelData(BufferedImage img) {
        width = img.getWidth();
        height = img.getHeight();
        pixelData = new int[width][height][3];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int[] rgb = getPixelData(img, i, j);
                int flippedX = width - 1 - i;
                int flippedY = height - 1 - j;

                pixelData[flippedX][flippedY][0] = rgb[0];
                pixelData[flippedX][flippedY][1] = rgb[1];
                pixelData[flippedX][flippedY][2] = rgb[2];
            }
        }
    }

    private int[] getPixelData(BufferedImage img, int x, int y) {
        int argb = img.getRGB(x, y);

        return new int[] {
                (argb >> 16) & 0xff, // red
                (argb >> 8) & 0xff,  // green
                argb & 0xff          // blue
        };
    }
}
