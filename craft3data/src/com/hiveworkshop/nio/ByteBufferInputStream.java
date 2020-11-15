package com.hiveworkshop.nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Simple InputStream wrapper for ByteBuffer.
 * <p>
 * This class is not thread safe.
 * <p>
 * https://stackoverflow.com/questions/4332264/wrapping-a-bytebuffer-with-an-inputstream
 */
public class ByteBufferInputStream extends InputStream {

    ByteBuffer buf;

    public ByteBufferInputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    public int read() {
        if (!buf.hasRemaining()) {
            return -1;
        }
        return buf.get() & 0xFF;
    }

    public int read(byte[] bytes, int off, int len) {
        if (!buf.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, buf.remaining());
        buf.get(bytes, off, len);
        return len;
    }
}
