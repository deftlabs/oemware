/**
 * (C) Copyright 2007, Deft Labs.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

