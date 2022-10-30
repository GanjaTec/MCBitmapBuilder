package de.jml.external.util.math;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public final class Vectors {

    @NotNull
    public static <N extends Number> Vector<N> add(@NotNull Vector<N> a, @NotNull Vector<N> b) {
        int aDims = a.dimensions(),
            bDims = b.dimensions(),
            max = Math.max(aDims, bDims);
        if (aDims == 0 || bDims == 0) {
            throw new ArithmeticException("Vector has no values!");
        }
        if (aDims < bDims) {
            a = fillZeros(a, bDims);
        } else if (aDims > bDims) {
            b = fillZeros(b, aDims);
        }
        Class<? extends Number> clazz = a.get(0).getClass();
        if (clazz == Integer.class || clazz == Byte.class || clazz == Short.class) {
            return addInts(a, b, max);

        } else if (clazz == Long.class) {
            return addLongs(a, b, max);

        } else if (clazz == Float.class) {
            return addFloats(a, b, max);

        } else /* clazz == Double.class */ {
            return addDoubles(a, b, max);
        }
    }

    private static <N extends Number> Vector<N> addBytes(@NotNull Vector<N> a, @NotNull Vector<N> b, int dims) {
        return addInts(a, b, dims);
    }

    private static <N extends Number> Vector<N> addShorts(@NotNull Vector<N> a, @NotNull Vector<N> b, int dims) {
        return addInts(a, b, dims);
    }

    private static <N extends Number> Vector<N> addInts(@NotNull Vector<N> a, @NotNull Vector<N> b, int dims) {
        Integer[] newVals = new Integer[dims];
        for (int i = 0; i < dims; i++) {
            newVals[i] = a.get(i).intValue() + b.get(i).intValue();
        }
        return new Vector<>((N[]) newVals);
    }

    private static <N extends Number> Vector<N> addLongs(@NotNull Vector<N> a, @NotNull Vector<N> b, int dims) {
        Long[] newVals = new Long[dims];
        for (int i = 0; i < dims; i++) {
            newVals[i] = a.get(i).longValue() + b.get(i).longValue();
        }
        return new Vector<>((N[]) newVals);
    }

    private static <N extends Number> Vector<N> addFloats(@NotNull Vector<N> a, @NotNull Vector<N> b, int dims) {
        Float[] newVals = new Float[dims];
        for (int i = 0; i < dims; i++) {
            newVals[i] = a.get(i).floatValue() + b.get(i).floatValue();
        }
        return new Vector<>((N[]) newVals);
    }

    private static <N extends Number> Vector<N> addDoubles(@NotNull Vector<N> a, @NotNull Vector<N> b, int dims) {
        Double[] newVals = new Double[dims];
        for (int i = 0; i < dims; i++) {
            newVals[i] = a.get(i).doubleValue() + b.get(i).doubleValue();
        }
        return new Vector<>((N[]) newVals);
    }


    private static <N extends Number> Vector<N> fillZeros(@NotNull Vector<N> vector, int newLength) {
        Number[] newVals = new Number[newLength];
        for (int i = 0; i < newVals.length; i++) {
            newVals[i] = i < vector.dimensions() ? vector.get(i) : 0;
        }
        return new Vector<>((N[]) newVals);

    }

}
