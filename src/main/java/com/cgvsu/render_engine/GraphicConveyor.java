package com.cgvsu.render_engine;

import javax.vecmath.*;

public class GraphicConveyor {

    public static Matrix4f rotateScaleTranslate() {
        float[] matrix = new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
        return new Matrix4f(matrix);
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f forward = new Vector3f();
        forward.sub(target, eye);

        Vector3f right = new Vector3f();
        right.cross(up, forward);

        Vector3f newUp = new Vector3f();
        newUp.cross(forward, right);

        forward.normalize();
        right.normalize();
        newUp.normalize();

        // Transposed view matrix
        float[] matrix = new float[]{
                right.x, newUp.x, forward.x, 0,
                right.y, newUp.y, forward.y, 0,
                right.z, newUp.z, forward.z, 0,
                -right.dot(eye), -newUp.dot(eye), -forward.dot(eye), 1
        };
        return new Matrix4f(matrix);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4f projection = new Matrix4f();
        float fovScale = (float) (1.0F / Math.tan(fov * 0.5F));

        projection.m00 = fovScale;
        projection.m11 = fovScale / aspectRatio;
        projection.m22 = (farPlane + nearPlane) / (farPlane - nearPlane);
        projection.m23 = 1.0F;
        projection.m32 = 2 * (nearPlane * farPlane) / (nearPlane - farPlane);

        return projection;
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        float x = (vertex.x * matrix.m00) + (vertex.y * matrix.m10) + (vertex.z * matrix.m20) + matrix.m30;
        float y = (vertex.x * matrix.m01) + (vertex.y * matrix.m11) + (vertex.z * matrix.m21) + matrix.m31;
        float z = (vertex.x * matrix.m02) + (vertex.y * matrix.m12) + (vertex.z * matrix.m22) + matrix.m32;
        float w = (vertex.x * matrix.m03) + (vertex.y * matrix.m13) + (vertex.z * matrix.m23) + matrix.m33;

        return new Vector3f(x / w, y / w, z / w);
    }

    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        float x = vertex.x * width + width / 2.0F;
        float y = -vertex.y * height + height / 2.0F;
        return new Point2f(x, y);
    }
}
