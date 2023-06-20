package com.zenyte.utils;

import java.util.concurrent.atomic.AtomicLong;

public final class ConcurrentQueue<T> {

    private final int capacity;
    private final AtomicLong rw;
    private final Object[] queue;


    public ConcurrentQueue(final int capacity) {
        this.capacity = capacity;
        this.rw = new AtomicLong(0);
        this.queue = new Object[capacity];
    }

    public boolean insert(final T obj) {
        long v;
        int read, write;
        do {
            v = rw.get();

            read = (int) ((v));
            write = (int) ((v >> 32));

            if ((write++ - read) >= capacity)
                return false;

            queue[write % capacity] = obj; // so if take() is called straight after write we return element.
        }
        while (!rw.compareAndSet(v, read + ((long) write << 32)));
        queue[write % capacity] = obj;
        return true;
    }

    @SuppressWarnings("unchecked")
    public T take() {
        long v;
        int read, write;
        T elem;
        do {
            v = rw.get();

            read = (int) ((v));
            write = (int) ((v >> 32));

            if (read++ >= write)
                return null;

            elem = (T) queue[read % capacity];
            queue[read % capacity] = null;
        }
        while (!rw.compareAndSet(v, read + ((long) write << 32)));
        return elem;
    }

    @SuppressWarnings("unchecked")
	public T peek() {
        final long v = rw.get();
        final int read = (int) ((v)) + 1;
        return (T) queue[read % capacity];
    }

    public int capacity() {
        return capacity;
    }

    public int size() {
        final long v = rw.get();
        final int read = (int) ((v));
        final int write = (int) ((v >> 32));
        return write - read;
    }

}