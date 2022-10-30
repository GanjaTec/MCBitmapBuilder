package de.jml.external.util;

import org.jetbrains.annotations.NotNull;

public interface Matrix<T> {

    void forEachCol(@NotNull ArrayTask<T> doForEach, boolean parallel);

}
