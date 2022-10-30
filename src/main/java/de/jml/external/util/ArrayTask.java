package de.jml.external.util;

@FunctionalInterface
public interface ArrayTask<T> {

    void accept(T[] arr);

}
