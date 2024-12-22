package com.cgvsu.model;

import java.util.ArrayList;
import java.util.List;

public class Polygon {

    private List<Integer> vertexIndices;
    private List<Integer> textureVertexIndices;
    private List<Integer> normalIndices;

    public Polygon() {
        vertexIndices = new ArrayList<>();
        textureVertexIndices = new ArrayList<>();
        normalIndices = new ArrayList<>();
    }

    public void setVertexIndices(List<Integer> vertexIndices) {
        if (vertexIndices.size() < 3) {
            throw new IllegalArgumentException("A polygon must have at least 3 vertex indices.");
        }
        this.vertexIndices = new ArrayList<>(vertexIndices);
    }

    public void setTextureVertexIndices(List<Integer> textureVertexIndices) {
        if (textureVertexIndices.size() < 3) {
            throw new IllegalArgumentException("A polygon must have at least 3 texture vertex indices.");
        }
        this.textureVertexIndices = new ArrayList<>(textureVertexIndices);
    }

    public void setNormalIndices(List<Integer> normalIndices) {
        if (normalIndices.size() < 3) {
            throw new IllegalArgumentException("A polygon must have at least 3 normal indices.");
        }
        this.normalIndices = new ArrayList<>(normalIndices);
    }

    public List<Integer> getVertexIndices() {
        return new ArrayList<>(vertexIndices);
    }

    public List<Integer> getTextureVertexIndices() {
        return new ArrayList<>(textureVertexIndices);
    }

    public List<Integer> getNormalIndices() {
        return new ArrayList<>(normalIndices);
    }
}
