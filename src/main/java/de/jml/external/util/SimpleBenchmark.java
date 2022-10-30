package de.jml.external.util;

import org.jetbrains.annotations.NotNull;

import static de.jml.external.util.Time.*;

public class SimpleBenchmark {

    private static final String PREFIX = "[Benchmark] ";

    public static void benchmark(@NotNull Runnable target) {
        long start, millis, secs;

        try {
            start = nowMillis();
            target.run();
            millis = elapsedMillis(start);
            secs = elapsedSecs(start);
            System.out.println(PREFIX + "Benchmark successfully done!");
            System.out.println(PREFIX + "Elapsed Time: " + millis + " ms / " + secs + " s");
        } catch (Exception all) {
            System.out.println("[Benchmark] Exception occurred while benchmarking!");
            all.printStackTrace();
        }

    }

}
