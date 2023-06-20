package com.zenyte.network.io.security;


/**
 * A pair of two {@link IsaacRandom} random number generators used as a stream
 * cipher. One takes the role of an encoder for this endpoint, the other takes
 * the role of a decoder for this endpoint.
 * @author Graham
 */
public final class ISAACCipherPair {

	/**
	 * The random number generator used to encode data.
	 */
	private final ISAACCipher encodingRandom;

	/**
	 * The random number generator used to decode data.
	 */
	private final ISAACCipher decodingRandom;

	/**
	 * Creates the pair of random number generators.
	 * @param encodingRandom The random number generator used for encoding.
	 * @param decodingRandom The random number generator used for decoding.
	 */
	public ISAACCipherPair(ISAACCipher encodingRandom, ISAACCipher decodingRandom) {
		this.encodingRandom = encodingRandom;
		this.decodingRandom = decodingRandom;
	}

	/**
	 * Gets the random number generator used for encoding.
	 * @return The random number generator used for encoding.
	 */
	public ISAACCipher getEncodingRandom() {
		return encodingRandom;
	}

	/**
	 * Gets the random number generator used for decoding.
	 * @return The random number generator used for decoding.
	 */
	public ISAACCipher getDecodingRandom() {
		return decodingRandom;
	}

}
