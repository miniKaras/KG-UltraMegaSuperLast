package com.cgvsu.math;

import javax.vecmath.*;

import static java.lang.Math.*;

public class AffineTransformations {

    public static Matrix4f modelMatrix(double tx, double ty, double tz,
                                       double alpha, double beta, double gamma,
                                       double sx, double sy, double sz) {

        Matrix4f transitionMatrix = translationMatrix(tx, ty, tz);
        Matrix4f rotationMatrix = makeMatrix4f(rotationMatrix(alpha, beta, gamma));
        Matrix4f scaleMatrix = makeMatrix4f(scaleMatrix(sx, sy, sz));

        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.mul(transitionMatrix, rotationMatrix);
        modelMatrix.mul(scaleMatrix);

        return modelMatrix;
    }

    public static Matrix3f scaleMatrix(double sx, double sy, double sz) {
        Matrix3f scaleMatrix = new Matrix3f();
        scaleMatrix.setElement(0, 0, (float) sx);
        scaleMatrix.setElement(1, 1, (float) sy);
        scaleMatrix.setElement(2, 2, (float) sz);
        return scaleMatrix;
    }

    public static Matrix3f rotationAroundAxisMatrix(double alpha, AXIS axis) {
        Matrix3f rotationMatrix = new Matrix3f();

        float cosAlpha = (float) cos(alpha);
        float sinAlpha = (float) sin(alpha);

        switch (axis) {
            case X:
                rotationMatrix.set(new float[]{
                        1, 0, 0,
                        0, cosAlpha, -sinAlpha,
                        0, sinAlpha, cosAlpha
                });
                break;
            case Y:
                rotationMatrix.set(new float[]{
                        cosAlpha, 0, sinAlpha,
                        0, 1, 0,
                        -sinAlpha, 0, cosAlpha
                });
                break;
            case Z:
                rotationMatrix.set(new float[]{
                        cosAlpha, -sinAlpha, 0,
                        sinAlpha, cosAlpha, 0,
                        0, 0, 1
                });
                break;
        }

        return rotationMatrix;
    }

    public static Matrix3f rotationMatrix(double alpha, double beta, double gamma) {
        Matrix3f Rx = rotationAroundAxisMatrix(alpha, AXIS.X);
        Matrix3f Ry = rotationAroundAxisMatrix(beta, AXIS.Y);
        Matrix3f Rz = rotationAroundAxisMatrix(gamma, AXIS.Z);

        Matrix3f rotationMatrix = new Matrix3f();
        rotationMatrix.mul(Rz, Ry);
        rotationMatrix.mul(rotationMatrix, Rx);

        return rotationMatrix;
    }

    public static Matrix4f translationMatrix(double tx, double ty, double tz) {
        Matrix4f T4 = new Matrix4f();
        T4.setIdentity();
        T4.setColumn(3, new float[]{(float) tx, (float) ty, (float) tz, 1});
        return T4;
    }

    public static Matrix4f makeMatrix4f(Matrix3f matrix3f) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.set(matrix3f);
        return matrix4f;
    }

    public enum AXIS {
        X,
        Y,
        Z
    }
}
