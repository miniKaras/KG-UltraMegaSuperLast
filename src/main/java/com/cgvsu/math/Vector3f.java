package com.cgvsu.math;

public class Vector3f {
    public static final float EPSILON = 1e-7f;
    public float x, y, z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Vector3f other) {
        return Math.abs(this.x - other.x) < EPSILON &&
                Math.abs(this.y - other.y) < EPSILON && Math.abs(this.z - other.z) < EPSILON;
    }

    public void mul(Vector3f var1, Vector3f var2) {
        this.x = var1.y * var2.z - var1.z * var2.y;
        this.y = -(var1.x * var2.z - var1.z * var2.x);
        this.z = var1.x * var2.y - var1.y * var2.x;
    }

    public void sub(Vector3f var1, Vector3f var2) {
        this.x = var2.x - var1.x;
        this.y = var2.y - var1.y;
        this.z = var2.z - var1.z;
    }

    public void add(Vector3f var1) {
        this.x += var1.x;
        this.y += var1.y;
        this.z += var1.z;
    }

    public void div(float n) {
        if (n != 0) {
            this.x /= n;
            this.y /= n;
            this.z /= n;
        } else {
            throw new ArithmeticException("Cannot divide by zero");
        }
    }

    public float dot(Vector3f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public void normalize() {
        float length = length();
        if (length > EPSILON) {
            this.div(length);
        } else {
            throw new ArithmeticException("Cannot normalize a zero vector");
        }
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public static Vector3f zero() {
        return new Vector3f(0, 0, 0);
    }

    public static Vector3f unitX() {
        return new Vector3f(1, 0, 0);
    }

    public static Vector3f unitY() {
        return new Vector3f(0, 1, 0);
    }

    public static Vector3f unitZ() {
        return new Vector3f(0, 0, 1);
    }

    @Override
    public String toString() {
        return "Vector3f{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}