package de.jml.external.util.math;

import de.jml.external.util.ArrayTask;
import de.jml.external.util.Matrix;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

public final class IntMatrix implements Matrix<Integer> {

    private final Vector<int[]> matrix;
    public final int width, height;

    public IntMatrix(int cols, int rows) {
        this.matrix = new Vector<>(cols);
        for (int col = 0; col < cols; col++) {
            matrix.add(new int[rows]);
        }
        this.width = cols;
        this.height = rows;
    }

    public IntMatrix(int[][] data) throws IOException {
        this.width = data.length;
        if (width == 0 || data[0].length == 0) {
            throw new IOException("Could not create Matrix, width and height must be > 0");
        }
        this.height = data[0].length;
        this.matrix = new Vector<>(width);
        for (int col = 0; col < width; col++) {
            matrix.add(col, data[col]);
        }
    }

    public int get(int row, int col) {
        return matrix.get(col)[row];
    }

    @NotNull
    public synchronized IntMatrix peekCol(@NotNull ArrayTask<Integer> doForEach, boolean parallel) {
        forEachCol(doForEach, parallel);
        return this;
    }

    public synchronized void forEachCol(@NotNull ArrayTask<Integer> doForEach, boolean parallel) {
        if (parallel) {
            matrix.parallelStream().forEach(col -> {
                Integer[] boxed = Arrays.stream(col).boxed().toArray(Integer[]::new);
                doForEach.accept(boxed);
            });
        } else {
            Integer[] boxed;
            for (int[] col : matrix) {
                boxed = Arrays.stream(col).boxed().toArray(Integer[]::new);
                doForEach.accept(boxed);
            }
        }

    }


}
