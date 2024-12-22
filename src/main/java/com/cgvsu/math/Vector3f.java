package com.cgvsu.math;

public class Vector3f {

    public Vector3f() {
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Vector3f other) {
        // todo: желательно, чтобы это была глобальная константа
        final float eps = 1e-7f;
        return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps && Math.abs(z - other.z) < eps;
    }

    public float x, y, z;

    public final void add(Vector3f var1) {
        this.x += var1.x;
        this.y += var1.y;
        this.z += var1.z;
    }

    public final void del(float num) {
        this.x /= num;
        this.y /= num;
        this.z /= num;
    }

    public final void normalize() {
        float dist = (float) Math.sqrt(x*x + y*y + z*z);
        if (Math.abs(dist) == 0){
            return;
        }
        del(dist);
    }

    public void sub(Vector3f var1, Vector3f var2) {
        this.x = var2.x - var1.x;
        this.y = var2.y - var1.y;
        this.z = var2.z - var1.z;
    }

    public void mult(Vector3f var1, Vector3f var2) {
        this.x = var2.x * var1.x;
        this.y = var2.y * var1.y;
        this.z = var2.z * var1.z;
    }
}
