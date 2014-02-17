/**
 * 
 */
package fi.seco.collections.collection;

import java.nio.ByteBuffer;

/**
 * Thread-local wrapper for byte buffers
 * 
 * @author jiemakel
 * 
 */
public class ByteBufferThreadLocal extends ThreadLocal<ByteBuffer> {
	private final ByteBuffer src;

	public ByteBufferThreadLocal(ByteBuffer src) {
		this.src = src;
	}

	@Override
	protected synchronized ByteBuffer initialValue() {
		return src.duplicate();
	}
}
