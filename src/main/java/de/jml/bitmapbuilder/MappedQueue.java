package de.jml.bitmapbuilder;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MappedQueue<K, V> {

    private final Queue<Entry<K, V>> q;

    public MappedQueue(boolean threadSafe) {
        this.q = threadSafe ? new ConcurrentLinkedQueue<>() : new ArrayDeque<>();
    }

    public void push(K key, V value) {
        q.add(new Entry<>(key, value));
    }

    @Nullable
    public Entry<K, V> poll() {
        return q.poll();
    }

    public int elements() {
        return q.size();
    }

    public boolean isEmpty() {
        return elements() == 0;
    }

    public static class Entry<K, V> {
        private final K key;
        private final V val;

        public Entry(K key, V val) {
            this.key = key;
            this.val = val;
        }

        public K key() {
            return key;
        }

        public V value() {
            return val;
        }
    }

}
