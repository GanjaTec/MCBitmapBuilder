package de.jml.external.util;

import java.time.Instant;

import static java.lang.Math.abs;

public class Time {

    public static long nowMillis() {
        return System.currentTimeMillis();
    }

    public static Instant nowInstant() {
        return Instant.now();
    }

    public static long elapsedMillis(long sinceMillis) {
        return timeDiff(nowMillis(), sinceMillis);
    }

    public static long elapsedSecs(long sinceMillis) {
        return elapsedMillis(sinceMillis) / 1000;
    }

    public static long timeDiff(long now, long then) {
        return abs(now - then);
    }

}
