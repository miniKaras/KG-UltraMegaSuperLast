package com.cgvsu.model;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.normalize.Normalize;
import com.cgvsu.texture.ImageToText;
import com.cgvsu.triangulation.Triangulation;

import javafx.scene.paint.Color;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;

public class Model {

    public ArrayList<Vector3f> vertices = new ArrayList<>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<>();
    public ArrayList<Vector3f> normals = new ArrayList<>();
    public ArrayList<Polygon> polygons = new ArrayList<>();
    public ArrayList<Polygon> polygonsWithoutTriangulation = new ArrayList<>();
    public boolean isActive = true;
    public boolean isActiveGrid = false;
    public boolean isActiveTexture = false;
    public String pathTexture = null;
    public boolean isActiveLighting = false;
    public Color color = Color.GRAY;
    public ImageToText imageToText = null;
    private Matrix4f transformMatrix = new Matrix4f();

    public Model() {
        transformMatrix.setIdentity();
    }

    public Matrix4f getTransformMatrix() {
        return transformMatrix;
    }

    public void setTransformMatrix(Matrix4f transformMatrix) {
        this.transformMatrix = transformMatrix;
    }

    public void triangulate() {
        polygonsWithoutTriangulation = polygons;

        polygons = (ArrayList<Polygon>) Triangulation.triangulate(polygons);
    }

    public void normalize() {
        normals = (ArrayList<Vector3f>) Normalize.normale(vertices, polygons);
    }
}
