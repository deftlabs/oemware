/**
 * (C) Copyright 2007, Deft Labs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deftlabs.core.util;

// Java
import java.nio.ByteBuffer;

/**
 * The byte utils. All byte and byte buffer utilities should go here.
 */
public final class ByteUtils extends ByteNumberUtils {

    /**
     * Clone a byte buffer. This doesn't work on direct buffers. The mark isn't
     * passed to the new buffer. The size of the new buffer is based on the
     * limit and not the capacity.
     * @param pBuffer The buffer.
     * @return The new object.
     */
    public static final ByteBuffer copyByteBuffer(final ByteBuffer pBuffer) {
        if (pBuffer.isDirect()) {
            throw new IllegalArgumentException("direct buffers not supported");
        }

        final ByteBuffer buffer = ByteBuffer.allocate(pBuffer.limit());
        buffer.order(pBuffer.order());
        buffer.put(pBuffer.array());
        buffer.position(pBuffer.position());
        return buffer;
    }

    /**
     * Appends the second array to the first array. The buffer1 object
     * is modified to include the buffer2 object.
     * @param buffer1 The first buffer.
     * @param buffer2 The second buffer.
     * @return The new array.
     */
    public static final byte [] appendArray(final byte [] buffer1,
                                            final byte [] buffer2)
    {
        final byte [] newBuffer = new byte[buffer1.length + buffer2.length];
        int pos = 0;
        System.arraycopy(buffer1, 0, newBuffer, pos, buffer1.length);
        pos += buffer1.length;
        System.arraycopy(buffer2, 0, newBuffer, pos, buffer2.length);
        return newBuffer;
    }

    /**
     * Removes X bytes from the end of the array. This creates a new
     * smaller array.
     * @param buffer The buffer.
     * @param pNum The number of bytes to remove.
     */
    public static final byte [] trimArray(  final byte [] buffer,
                                            final int pNum)
    {
        final byte [] newBuffer = new byte[buffer.length - pNum];
        System.arraycopy(buffer, 0, newBuffer, 0, newBuffer.length);
        return newBuffer;
    }
}

