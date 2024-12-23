package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cgvsu.checkbox.Greed;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.rasterization.TriangleRasterization;
import com.cgvsu.texture.ImageToText;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javax.vecmath.*;

import com.cgvsu.model.Model;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final List<Model> meshes,
            final int width,
            final int height
    ) {
        // Инициализируем Z-буфер
        double[][] ZBuffer = new double[width][height];
        for (double[] row : ZBuffer) {
            Arrays.fill(row, Double.MAX_VALUE);
        }

        // Создаём единичную матрицу модели (пока без трансформаций)
        Matrix4f modelMatrix = rotateScaleTranslate();
        // Получаем видовую матрицу из камеры
        Matrix4f viewMatrix = camera.getViewMatrix();
        // Получаем проекционную матрицу из камеры
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        // Итоговая матрица MVP = model * view * projection
        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(projectionMatrix);

        // Проходим по всем моделям
        for (Model mesh : meshes) {
            // Если у модели указана текстура, но ещё не загружена — загружаем
            if (mesh.pathTexture != null && mesh.imageToText == null) {
                mesh.imageToText = new ImageToText();
                mesh.imageToText.loadImage(mesh.pathTexture);
            }

            // Отрисовываем полигоны модели
            final int nPolygons = mesh.polygons.size();
            for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {

                // Количество вершин в текущем полигоне
                final int nVerticesInPolygon = mesh.polygons
                        .get(polygonInd)
                        .getVertexIndices()
                        .size();

                // Массивы для хранения Z-координат и атрибутов (нормалей, текстур)
                double[] vz = new double[nVerticesInPolygon];
                Vector3f[] normals = new Vector3f[nVerticesInPolygon];
                Vector2f[] textures = new Vector2f[nVerticesInPolygon];

                // Список для хранения проекций вершин (Point2f)
                ArrayList<Point2f> resultPoints = new ArrayList<>();

                // Обрабатываем каждую вершину полигона
                for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {

                    // Получаем текущую вершину из модели
                    Vector3f vertex = mesh.vertices.get(
                            mesh.polygons
                                    .get(polygonInd)
                                    .getVertexIndices()
                                    .get(vertexInPolygonInd)
                    );

                    // Получаем нормаль для этой вершины
                    normals[vertexInPolygonInd] = mesh.normals.get(
                            mesh.polygons
                                    .get(polygonInd)
                                    .getVertexIndices()
                                    .get(vertexInPolygonInd)
                    );

                    // Если есть текстура, то берём текстурные координаты
                    if (mesh.pathTexture != null) {
                        textures[vertexInPolygonInd] = mesh.textureVertices.get(
                                mesh.polygons
                                        .get(polygonInd)
                                        .getTextureVertexIndices()
                                        .get(vertexInPolygonInd)
                        );
                    }

                    // Переводим Vector3f (из пакета com.cgvsu.math) в javax.vecmath.Vector3f
                    javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(vertex.x, vertex.y, vertex.z);

                    // Умножаем на итоговую матрицу, получаем координаты в клип-пространстве
                    javax.vecmath.Vector3f transformedVertex = multiplyMatrix4ByVector3(
                            modelViewProjectionMatrix,
                            vertexVecmath
                    );

                    // Сохраняем Z-координату (после умножения)
                    vz[vertexInPolygonInd] = transformedVertex.z;

                    // Проецируем 3D-координаты на экран (Point2f)
                    Point2f resultPoint = vertexToPoint(transformedVertex, width, height);
                    resultPoints.add(resultPoint);
                }

                // Для отрисовки треугольника предполагаем, что в полигоне 3 вершины
                // (так как модель уже триангулирована или мы работаем только с треугольниками)
                int[] coorX = {
                        (int) resultPoints.get(0).x,
                        (int) resultPoints.get(1).x,
                        (int) resultPoints.get(2).x
                };
                int[] coorY = {
                        (int) resultPoints.get(0).y,
                        (int) resultPoints.get(1).y,
                        (int) resultPoints.get(2).y
                };

                // Вызываем растеризацию треугольника
                TriangleRasterization.draw(
                        graphicsContext,
                        coorX,
                        coorY,
                        new Color[] { mesh.color, mesh.color, mesh.color },
                        ZBuffer,
                        vz,
                        normals,
                        textures,
                        new double[]{ viewMatrix.m02, viewMatrix.m12, viewMatrix.m22 },
                        mesh
                );

                // Если включён режим сетки (isActiveGrid), то рисуем ребра треугольника
                if (mesh.isActiveGrid) {
                    Greed.drawLine(coorX[0], coorY[0], coorX[1], coorY[1],
                            ZBuffer, vz, coorX, coorY, graphicsContext);
                    Greed.drawLine(coorX[0], coorY[0], coorX[2], coorY[2],
                            ZBuffer, vz, coorX, coorY, graphicsContext);
                    Greed.drawLine(coorX[2], coorY[2], coorX[1], coorY[1],
                            ZBuffer, vz, coorX, coorY, graphicsContext);
                }
            }
        }
    }
}
