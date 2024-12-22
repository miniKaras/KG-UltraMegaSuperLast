package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cgvsu.checkbox.Greed;
import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.rasterization.TriangleRasterization;
import com.cgvsu.texture.ImageToText;
import javafx.scene.canvas.GraphicsContext;

import javax.vecmath.*;

import com.cgvsu.model.Model;
import javafx.scene.paint.Color;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final List<Model> meshes,
            final int width,
            final int height) {
        double[][] ZBuffer = new double[width][height];
        for (double[] longs : ZBuffer) {
            Arrays.fill(longs, Double.MAX_VALUE);
        }

        Matrix4f modelMatrix = rotateScaleTranslate();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(projectionMatrix);

        for (Model mesh : meshes) {
            if (mesh.pathTexture != null && mesh.imageToText == null) {
                mesh.imageToText = new ImageToText();
                mesh.imageToText.loadImage(mesh.pathTexture);
            }

            final int nPolygons = mesh.polygons.size();

            for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
                final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();
                javax.vecmath.Vector3f v;
                double[] vz = new double[nVerticesInPolygon];
                Vector3f[] normals = new Vector3f[3];
                Vector2f[] textures = new Vector2f[3];

                ArrayList<Point2f> resultPoints = new ArrayList<>();

                for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                    Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));
                    normals[vertexInPolygonInd] = mesh.normals.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

                    if (mesh.pathTexture != null) {
                        textures[vertexInPolygonInd] = mesh.textureVertices.get(mesh.polygons.get(polygonInd).getTextureVertexIndices().get(vertexInPolygonInd));
                    }

                    javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(vertex.x, vertex.y, vertex.z);
                    v = multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath);
                    vz[vertexInPolygonInd] = v.z;
                    Point2f resultPoint = vertexToPoint(v, width, height);
                    resultPoints.add(resultPoint);
                }

                int[] coorX = new int[]{(int) resultPoints.get(0).x, (int) resultPoints.get(1).x, (int) resultPoints.get(2).x};
                int[] coorY = new int[]{(int) resultPoints.get(0).y, (int) resultPoints.get(1).y, (int) resultPoints.get(2).y};

                TriangleRasterization.draw(
                        graphicsContext,
                        coorX,
                        coorY,
                        new Color[]{mesh.color, mesh.color, mesh.color},
                        ZBuffer,
                        vz,
                        normals,
                        textures,
                        new double[]{viewMatrix.m02, viewMatrix.m12, viewMatrix.m22},
                        mesh);

                if (mesh.isActiveGrid) {
                    Greed.drawLine(coorX[0], coorY[0], coorX[1], coorY[1], ZBuffer, vz, coorX, coorY, graphicsContext);
                    Greed.drawLine(coorX[0], coorY[0], coorX[2], coorY[2], ZBuffer, vz, coorX, coorY, graphicsContext);
                    Greed.drawLine(coorX[2], coorY[2], coorX[1], coorY[1], ZBuffer, vz, coorX, coorY, graphicsContext);
                }
            }
        }
    }
}
