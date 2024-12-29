package com.cgvsu.objreader;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ObjReader {

	private static final String OBJ_VERTEX_TOKEN = "v";
	private static final String OBJ_TEXTURE_TOKEN = "vt";
	private static final String OBJ_NORMAL_TOKEN = "vn";
	private static final String OBJ_FACE_TOKEN = "f";


	public static Model read(final String fileContent) {
		Model result = new Model();

		try (Scanner scanner = new Scanner(fileContent)) {
			int lineInd = 0;
			while (scanner.hasNextLine()) {
				lineInd++;
				final String line = scanner.nextLine().trim();

				if (line.isBlank() || line.startsWith("#")) {
					continue;
				}

				parseLine(line, lineInd, result);
			}
		}
		return result;
	}

	private static void parseLine(final String line, final int lineInd, final Model model) {
		List<String> wordsInLine = new ArrayList<>(Arrays.asList(line.split("\\s+")));
		if (wordsInLine.isEmpty()) return;

		final String token = wordsInLine.remove(0);

		switch (token) {
			case OBJ_VERTEX_TOKEN -> model.vertices.add(parseVertex(wordsInLine, lineInd));
			case OBJ_TEXTURE_TOKEN -> model.textureVertices.add(parseTextureVertex(wordsInLine, lineInd));
			case OBJ_NORMAL_TOKEN -> model.normals.add(parseNormal(wordsInLine, lineInd));
			case OBJ_FACE_TOKEN -> model.polygons.add(parseFace(wordsInLine, lineInd));
			default -> {
			}
		}
	}


	protected static Vector3f parseVertex(final List<String> wordsInLine, final int lineInd) {
		try {
			if (wordsInLine.size() < 3) {
				throw new ObjReaderException("Слишком мало аргументов вершин. Ожидается как минимум 3 (x, y, z).", lineInd);
			}
			float x = Float.parseFloat(wordsInLine.get(0));
			float y = Float.parseFloat(wordsInLine.get(1));
			float z = Float.parseFloat(wordsInLine.get(2));

			if (wordsInLine.size() >= 4) {
				float w = Float.parseFloat(wordsInLine.get(3));
			}

			return new Vector3f(x, y, z);

		} catch (NumberFormatException e) {
			throw new ObjReaderException("Не удалось проанализировать значение с плавающей точкой для вершины.", lineInd, e);
		}
	}

	protected static Vector2f parseTextureVertex(final List<String> wordsInLine, final int lineInd) {
		try {
			if (wordsInLine.size() < 2) {
				throw new ObjReaderException("Слишком мало аргументов вершин текстуры. Ожидается как минимум 2 (u, v).", lineInd);
			}
			float u = Float.parseFloat(wordsInLine.get(0));
			float v = Float.parseFloat(wordsInLine.get(1));

			if (wordsInLine.size() >= 3) {
				float w = Float.parseFloat(wordsInLine.get(2));
			}

			return new Vector2f(u, v);

		} catch (NumberFormatException e) {
			throw new ObjReaderException("Не удалось проанализировать плавающее значение для вершины текстуры.", lineInd, e);
		}
	}

	protected static Vector3f parseNormal(final List<String> wordsInLine, final int lineInd) {
		try {
			if (wordsInLine.size() < 3) {
				throw new ObjReaderException("Слишком мало нормальных аргументов. Ожидаемо 3.", lineInd);
			}
			float x = Float.parseFloat(wordsInLine.get(0));
			float y = Float.parseFloat(wordsInLine.get(1));
			float z = Float.parseFloat(wordsInLine.get(2));

			return new Vector3f(x, y, z);

		} catch (NumberFormatException e) {
			throw new ObjReaderException("Не удалось преобразовать плавающее значение в нормальное.", lineInd, e);
		}
	}

	protected static Polygon parseFace(final List<String> wordsInLine, final int lineInd) {
		ArrayList<Integer> vertexIndices = new ArrayList<>();
		ArrayList<Integer> textureVertexIndices = new ArrayList<>();
		ArrayList<Integer> normalIndices = new ArrayList<>();

		for (String faceElement : wordsInLine) {
			parseFaceElement(faceElement, vertexIndices, textureVertexIndices, normalIndices, lineInd);
		}

		Polygon polygon = new Polygon();
		polygon.setVertexIndices(vertexIndices);
		polygon.setTextureVertexIndices(textureVertexIndices);
		polygon.setNormalIndices(normalIndices);
		return polygon;
	}


	protected static void parseFaceElement(
			final String faceElement,
			final List<Integer> vertexIndices,
			final List<Integer> textureVertexIndices,
			final List<Integer> normalIndices,
			final int lineInd
	) {
		try {
			String[] indices = faceElement.split("/");
			switch (indices.length) {
				case 1 -> {
					vertexIndices.add(parseIndex(indices[0], lineInd));
				}
				case 2 -> {
					vertexIndices.add(parseIndex(indices[0], lineInd));
					textureVertexIndices.add(parseIndex(indices[1], lineInd));
				}
				case 3 -> {
					vertexIndices.add(parseIndex(indices[0], lineInd));
					if (!indices[1].isEmpty()) {
						textureVertexIndices.add(parseIndex(indices[1], lineInd));
					}
					if (!indices[2].isEmpty()) {
						normalIndices.add(parseIndex(indices[2], lineInd));
					}
				}
				default -> {
					throw new ObjReaderException("Недопустимый формат элемента грани: " + faceElement, lineInd);
				}
			}
		} catch (NumberFormatException e) {
			throw new ObjReaderException("Не удалось проанализировать целочисленное значение в элементе полигон: " + faceElement, lineInd, e);
		}
	}

	private static int parseIndex(final String indexString, final int lineInd) {
		int index = Integer.parseInt(indexString);
		return index - 1;
	}

	private ObjReader() {
	}
}
