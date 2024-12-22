package com.cgvsu.math;

import com.cgvsu.model.Model;
import com.cgvsu.math.Vector3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

public class TranslationModel {

    public static void move(Matrix4f transformationMatrix, Model model) {
        for (Vector3f vertex : model.vertices) {
            mul(vertex, transformationMatrix);
        }
        model.normalize();
    }

    public static void mul(Vector3f vertex, Matrix4f transformationMatrix) {
        Vector4f vector4f = new Vector4f(vertex.x, vertex.y, vertex.z, 1);

        vertex.x = vector4f.x * transformationMatrix.m00 + vector4f.y * transformationMatrix.m01 + vector4f.z * transformationMatrix.m02 + vector4f.w * transformationMatrix.m03;
        vertex.y = vector4f.x * transformationMatrix.m10 + vector4f.y * transformationMatrix.m11 + vector4f.z * transformationMatrix.m12 + vector4f.w * transformationMatrix.m13;
        vertex.z = vector4f.x * transformationMatrix.m20 + vector4f.y * transformationMatrix.m21 + vector4f.z * transformationMatrix.m22 + vector4f.w * transformationMatrix.m23;
    }
}
