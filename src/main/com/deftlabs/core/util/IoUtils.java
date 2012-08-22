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
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * The io utils.
 */
public class IoUtils {

    /**
     * Read the input stream into a byte array. Be careful with this method. If
     * the amount of data in the input stream is too much, you'll run out of
     * memory. If an empty input stream is passed, an empty byte array is
     * returned.
     * @param pInputStream The stream.
     * @param pReadSize The number of bytes to attempt to read at a time.
     * @return The byte array containing all the data.
     * @throws IOException
     */
    public static final byte [] readInputStream(final InputStream pInputStream,
                                                final int pReadSize)
        throws IOException
    {
        if (pInputStream == null) throw new IllegalArgumentException("Null input stream.");
        if (pReadSize < 1) throw new IllegalArgumentException("Read size is: " + pReadSize);

        final ByteArrayOutputStream out = new ByteArrayOutputStream(pReadSize);
        final byte [] buffer = new byte[pReadSize];

        int read = 0;
        while (true) {
            read = pInputStream.read(buffer);
            if (read == -1) break;
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    /**
     * Read the input stream into a byte array. Make sure you're buffer
     * is large enough or an exception will be thrown.
     * @param pInputStream The stream.
     * @param pBuffer The buffer.
     * @param pReadSize The number of bytes to attempt to read at a time.
     * @return The number of bytes read from the input stream.
     * @throws IOException
     */
    public static final int readInputStream(final InputStream pInputStream,
                                            final byte [] pBuffer,
                                            final int pReadSize)
        throws IOException
    {
        int read = 0;
        int totalRead = 0;
        while (true) {
            read = pInputStream.read(pBuffer, totalRead, pReadSize);
            if (read == -1) break;
            totalRead += read;
        }
        return totalRead;
    }
}

