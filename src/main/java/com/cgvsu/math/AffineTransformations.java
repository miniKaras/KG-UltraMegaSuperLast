package com.cgvsu.math;

import javax.vecmath.*;

public class AffineTransformations {
    public static Matrix4f modelMatrix(double tx, double ty, double tz, double alpha, double beta, double gamma,
                                       double sx, double sy, double sz) {
        Matrix4f transitionMatrix = translationMatrix(tx, ty, tz);
        Matrix4f rotationMatrix = rotationMatrix(alpha, beta, gamma);
        Matrix4f scaleMatrix = scaleMatrix(sx, sy, sz);
        // Порядок умножения изменён для работы с векторами-столбцами
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.mul(scaleMatrix, rotationMatrix);
        modelMatrix.mul(transitionMatrix);
        return modelMatrix;
    }

    public static Matrix4f scaleMatrix(double sx, double sy, double sz) {
        Matrix4f scaleMatrix = new Matrix4f();
        scaleMatrix.setIdentity();
        scaleMatrix.m00 = (float) sx;
        scaleMatrix.m11 = (float) sy;
        scaleMatrix.m22 = (float) sz;
        return scaleMatrix;
    }

    public static Matrix4f rotationMatrix(double alpha, double beta, double gamma) {
        Matrix4f Rx = rotationAroundAxisMatrix(alpha, AXIS.X);
        Matrix4f Ry = rotationAroundAxisMatrix(beta, AXIS.Y);
        Matrix4f Rz = rotationAroundAxisMatrix(gamma, AXIS.Z);
        // Изменён порядок умножения для работы с векторами-столбцами
        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.mul(Ry, Rx);
        rotationMatrix.mul(Rz);
        return rotationMatrix;
    }

    public static Matrix4f rotationAroundAxisMatrix(double angle, AXIS axis) {
        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.setIdentity();
        float cosA = (float) Math.cos(angle);
        float sinA = (float) Math.sin(angle);
        switch (axis) {
            case X:
                rotationMatrix.m11 = cosA;
                rotationMatrix.m12 = -sinA;
                rotationMatrix.m21 = sinA;
                rotationMatrix.m22 = cosA;
                break;
            case Y:
                rotationMatrix.m00 = cosA;
                rotationMatrix.m02 = sinA;
                rotationMatrix.m20 = -sinA;
                rotationMatrix.m22 = cosA;
                break;
            case Z:
                rotationMatrix.m00 = cosA;
                rotationMatrix.m01 = -sinA;
                rotationMatrix.m10 = sinA;
                rotationMatrix.m11 = cosA;
                break;
        }
        return rotationMatrix;
    }

    public static Matrix4f translationMatrix(double tx, double ty, double tz) {
        Matrix4f translationMatrix = new Matrix4f();
        translationMatrix.setIdentity();
        translationMatrix.m03 = (float) tx;
        translationMatrix.m13 = (float) ty;
        translationMatrix.m23 = (float) tz;
        return translationMatrix;
    }

    public static void transformVector(Vector4f vector, Matrix4f matrix) {        // Умножение вектора-столбца на матрицу
        float x = vector.x * matrix.m00 + vector.y * matrix.m01 + vector.z * matrix.m02 + vector.w * matrix.m03;
        float y = vector.x * matrix.m10 + vector.y * matrix.m11 + vector.z * matrix.m12 + vector.w * matrix.m13;
        float z = vector.x * matrix.m20 + vector.y * matrix.m21 + vector.z * matrix.m22 + vector.w * matrix.m23;
        float w = vector.x * matrix.m30 + vector.y * matrix.m31 + vector.z * matrix.m32 + vector.w * matrix.m33;
        vector.getClass();
    }

    public static Matrix4f worldMatrix(double tx, double ty, double tz, double alpha, double beta, double gamma,
                                       double sx, double sy, double sz) {        // Метод для аффинных преобразований из локальных в мировые координаты
        Matrix4f translation = translationMatrix(tx, ty, tz);
        Matrix4f rotation = rotationMatrix(alpha, beta, gamma);
        Matrix4f scaling = scaleMatrix(sx, sy, sz);
        Matrix4f worldMatrix = new Matrix4f();
        worldMatrix.mul(translation, rotation);
        worldMatrix.mul(scaling);
        return worldMatrix;
    }

    public static void testAffineTransformations() {
        Vector4f localVector = new Vector4f(1, 1, 1, 1);
        Matrix4f worldTransform = worldMatrix(2, 3, 4, Math.PI / 4, Math.PI / 4, Math.PI / 4, 1.5, 1.5, 1.5);
        System.out.println("Local Vector: " + localVector);
        transformVector(localVector, worldTransform);
        System.out.println("World Vector: " + localVector);
    }

    public enum AXIS {
        X, Y, Z
    }
}