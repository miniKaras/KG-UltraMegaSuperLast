package com.cgvsu.model;

import java.util.ArrayList;

public class Polygon {

    private ArrayList<Integer> vertexIndices;
    private ArrayList<Integer> textureVertexIndices;
    private ArrayList<Integer> normalIndices;

    public Polygon() {
        this.vertexIndices = new ArrayList<>();
        this.textureVertexIndices = new ArrayList<>();
        this.normalIndices = new ArrayList<>();
    }

    public void setVertexIndices(ArrayList<Integer> vertexIndices) {
        if (vertexIndices.size() < 3) {
            throw new IllegalArgumentException("A polygon must have at least 3 vertex indices.");
        }
        this.vertexIndices = vertexIndices;
    }

    public void setTextureVertexIndices(ArrayList<Integer> textureVertexIndices) {
        if (textureVertexIndices.size() < 3) {
            throw new IllegalArgumentException("A polygon must have at least 3 texture vertex indices.");
        }
        this.textureVertexIndices = textureVertexIndices;
    }

    public void setNormalIndices(ArrayList<Integer> normalIndices) {
        if (normalIndices.size() < 3) {
            throw new IllegalArgumentException("A polygon must have at least 3 normal indices.");
        }
        this.normalIndices = normalIndices;
    }

    public ArrayList<Integer> getVertexIndices() {
        return new ArrayList<>(vertexIndices);
    }

    public ArrayList<Integer> getTextureVertexIndices() {
        return new ArrayList<>(textureVertexIndices);
    }

    public ArrayList<Integer> getNormalIndices() {
        return new ArrayList<>(normalIndices);
    }
}
