package de.jml.external.util.math;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Vector<N extends Number> {

    private final N[] vals;

    @SafeVarargs
    public Vector(N... values) {
        this.vals = values;
    }

    @NotNull
    public N get(int index) {
        return vals[index];
    }

    public int dimensions() {
        return vals.length;
    }

    @Override
    public String toString() {
        return "V" + Arrays.toString(vals)
                .replaceAll("\\[", "(")
                .replaceAll("]", ")");
    }
}
