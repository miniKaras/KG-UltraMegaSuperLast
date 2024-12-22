package com.cgvsu.render_engine;

import java.util.ArrayList;
import com.cgvsu.math.Vector3f;
import javafx.scene.canvas.GraphicsContext;
import javax.vecmath.*;
import com.cgvsu.model.Model;
import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height) {

        Matrix4f modelMatrix = rotateScaleTranslate();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(projectionMatrix);

        for (int polygonInd = 0; polygonInd < mesh.polygons.size(); ++polygonInd) {
            renderPolygon(graphicsContext, mesh, modelViewProjectionMatrix, polygonInd, width, height);
        }
    }

    private static void renderPolygon(
            final GraphicsContext graphicsContext,
            final Model mesh,
            final Matrix4f modelViewProjectionMatrix,
            final int polygonInd,
            final int width,
            final int height) {

        final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();
        ArrayList<Point2f> resultPoints = new ArrayList<>();

        for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
            Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));
            javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(vertex.x, vertex.y, vertex.z);

            Point2f resultPoint = vertexToPoint(
                    multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath),
                    width,
                    height
            );
            resultPoints.add(resultPoint);
        }

        drawPolygonEdges(graphicsContext, resultPoints);
    }

    private static void drawPolygonEdges(
            final GraphicsContext graphicsContext,
            final ArrayList<Point2f> resultPoints) {

        final int nVerticesInPolygon = resultPoints.size();

        for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
            graphicsContext.strokeLine(
                    resultPoints.get(vertexInPolygonInd - 1).x,
                    resultPoints.get(vertexInPolygonInd - 1).y,
                    resultPoints.get(vertexInPolygonInd).x,
                    resultPoints.get(vertexInPolygonInd).y
            );
        }

        if (nVerticesInPolygon > 0) {
            graphicsContext.strokeLine(
                    resultPoints.get(nVerticesInPolygon - 1).x,
                    resultPoints.get(nVerticesInPolygon - 1).y,
                    resultPoints.get(0).x,
                    resultPoints.get(0).y
            );
        }
    }
}
