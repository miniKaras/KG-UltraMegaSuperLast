package com.cgvsu.GUI;

import com.cgvsu.math.AffineTransformations;
import com.cgvsu.math.TranslationModel;
import com.cgvsu.model.DeleteVertices;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.ObjWriter.ObjWriter;
import com.cgvsu.model.Model;

import javax.vecmath.Matrix4f;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Scene {

    private final List<Model> meshes;

    public Scene() {
        this.meshes = new ArrayList<>();
    }

    public void addModel(Model model) {
        this.meshes.add(model);
    }

    public void deleteModel(int modelIndex) {
        if (modelIndex < 0 || modelIndex >= meshes.size()) {
            throw new RuntimeException("Неверный индекс модели для удаления!");
        }
        meshes.remove(modelIndex);
    }

    public Model getActiveModel() {
        for (Model m : meshes) {
            if (m.isActive) {
                return m;
            }
        }
        return !meshes.isEmpty() ? meshes.get(0) : null;
    }

    public void setActiveModel(int modelIndex) {
        if (modelIndex < 0 || modelIndex >= meshes.size()) {
            throw new RuntimeException("Неверный индекс модели для установки активности!");
        }
        for (Model m : meshes) {
            m.isActive = false;
        }
        meshes.get(modelIndex).isActive = true;
    }

    public Model openModel(Path filePath) throws IOException {
        String fileContent = Files.readString(filePath);
        Model mesh = ObjReader.read(fileContent);
        mesh.triangulate();
        mesh.normalize();
        return mesh;
    }

    public void saveModel(Path filePath, boolean transformSave) {
        Model active = getActiveModel();
        if (active == null) {
            throw new RuntimeException("Нет активной модели для сохранения!");
        }

        String filename = filePath.toString();
        if (!filename.endsWith(".obj")) {
            filename += ".obj";
        }
        ObjWriter.write(active, filename);
    }

    public void transformActiveModel(
            float tx, float ty, float tz,
            float rx, float ry, float rz,
            float sx, float sy, float sz
    ) {
        Model active = getActiveModel();
        if (active == null) {
            throw new RuntimeException("Нет активной модели для трансформации!");
        }
        Matrix4f transposeMatrix = AffineTransformations.modelMatrix(
                tx, ty, tz,
                rx, ry, rz,
                sx, sy, sz
        );
        TranslationModel.move(transposeMatrix, active);
    }

    public void toggleGrid(int modelIndex) {
        if (modelIndex < 0 || modelIndex >= meshes.size()) {
            throw new RuntimeException("Неверный индекс модели!");
        }
        Model m = meshes.get(modelIndex);
        m.isActiveGrid = !m.isActiveGrid;
    }

    public void toggleLighting(int modelIndex) {
        if (modelIndex < 0 || modelIndex >= meshes.size()) {
            throw new RuntimeException("Неверный индекс модели!");
        }
        Model m = meshes.get(modelIndex);
        m.isActiveLighting = !m.isActiveLighting;
    }

    public void toggleTexture(int modelIndex, String texturePath) {
        if (modelIndex < 0 || modelIndex >= meshes.size()) {
            throw new RuntimeException("Неверный индекс модели!");
        }
        Model m = meshes.get(modelIndex);

        if (m.pathTexture == null && texturePath != null && !texturePath.isEmpty()) {
            m.pathTexture = texturePath;
        }

        if (m.pathTexture == null) {
            m.isActiveTexture = false;
        } else {
            m.isActiveTexture = !m.isActiveTexture;
        }
    }

    public void deleteVerticesFromActiveModel(String parseVertexString) {
        Model active = getActiveModel();
        if (active == null) {
            throw new RuntimeException("Нет активной модели для удаления вершин!");
        }

        List<Integer> vertexIndices = parseVertexIndices(active, parseVertexString);
        if (vertexIndices.isEmpty()) {
            throw new RuntimeException("Не найдено корректных индексов для удаления!");
        }

        Model newModel = DeleteVertices.deleteVerticesFromModel(active, vertexIndices);
        newModel.normalize();

        int index = meshes.indexOf(active);
        meshes.set(index, newModel);

        newModel.isActive = true;
    }

    private List<Integer> parseVertexIndices(Model model, String vertexString) {
        if (model == null) return new ArrayList<>();

        String[] parts = vertexString.trim().split("\\s+");
        List<Integer> resultIndices = new ArrayList<>();

        for (String part : parts) {
            try {
                int idx = Integer.parseInt(part);
                if (idx >= 0 && idx < model.vertices.size()) {
                    resultIndices.add(idx);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return resultIndices;
    }

    public List<Model> getMeshes() {
        return meshes;
    }
}
