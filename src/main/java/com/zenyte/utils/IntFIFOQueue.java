package com.zenyte.utils;

import java.util.NoSuchElementException;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;

/**
 * A type-specific array-based FIFO queue, supporting also deque operations.
 *
 * <p>
 * Instances of this class represent a FIFO queue using a backing array in a circular way. The array is enlarged and shrunk as needed. You
 * can use the {@link #trim()} method to reduce its memory usage, if necessary.
 *
 * <p>
 * This class provides additional methods that implement a <em>deque</em> (double-ended queue).
 */
public class IntFIFOQueue extends IntArrayFIFOQueue {

	private static final long serialVersionUID = 5116943494589427178L;

	public IntFIFOQueue(final int capacity) {
		super(capacity);
	}

	/**
	 * Creates a new empty queue with standard {@linkplain #INITIAL_CAPACITY initial capacity}.
	 */
	public IntFIFOQueue() {
		this(INITIAL_CAPACITY);
	}

	public int peek(final int index) {
		if (start == end) {
			throw new NoSuchElementException();
		}
		int arrayIndex = start + index;
		if (arrayIndex >= length) {
			arrayIndex -= length;
		}
		if (arrayIndex == end) {
			throw new NoSuchElementException();
		}
		return array[arrayIndex];
	}

}
