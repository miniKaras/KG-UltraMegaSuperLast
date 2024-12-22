package com.cgvsu.math;

// This class is part of a custom library for linear algebra operations
public class Vector3f {

    // Precision constant for floating-point comparisons
    public static final float EPSILON = 1e-7f;

    // Vector components
    public float x, y, z;

    // Constructor to initialize the vector
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Check if two vectors are approximately equal
    public boolean equals(Vector3f other) {
        return Math.abs(this.x - other.x) < EPSILON &&
                Math.abs(this.y - other.y) < EPSILON &&
                Math.abs(this.z - other.z) < EPSILON;
    }

    // Cross product: this = var1 x var2
    public void mul(Vector3f var1, Vector3f var2) {
        this.x = var1.y * var2.z - var1.z * var2.y;
        this.y = -(var1.x * var2.z - var1.z * var2.x);
        this.z = var1.x * var2.y - var1.y * var2.x;
    }

    // Subtraction: this = var2 - var1
    public void sub(Vector3f var1, Vector3f var2) {
        this.x = var2.x - var1.x;
        this.y = var2.y - var1.y;
        this.z = var2.z - var1.z;
    }

    // Addition: this = this + var1
    public void add(Vector3f var1) {
        this.x += var1.x;
        this.y += var1.y;
        this.z += var1.z;
    }

    // Scalar division: this = this / n
    public void div(float n) {
        if (n != 0) {
            this.x /= n;
            this.y /= n;
            this.z /= n;
        } else {
            throw new ArithmeticException("Cannot divide by zero");
        }
    }

    // Dot product: returns the scalar result of this . other
    public float dot(Vector3f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    // Normalize the vector (make its magnitude equal to 1)
    public void normalize() {
        float length = length();
        if (length > EPSILON) {
            this.div(length);
        } else {
            throw new ArithmeticException("Cannot normalize a zero vector");
        }
    }

    // Get the length (magnitude) of the vector
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    // Static method to create a zero vector (0, 0, 0)
    public static Vector3f zero() {
        return new Vector3f(0, 0, 0);
    }

    // Static method to create a unit vector along the X-axis
    public static Vector3f unitX() {
        return new Vector3f(1, 0, 0);
    }

    // Static method to create a unit vector along the Y-axis
    public static Vector3f unitY() {
        return new Vector3f(0, 1, 0);
    }

    // Static method to create a unit vector along the Z-axis
    public static Vector3f unitZ() {
        return new Vector3f(0, 0, 1);
    }

    // Override toString() for better debugging and visualization
    @Override
    public String toString() {
        return "Vector3f{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
