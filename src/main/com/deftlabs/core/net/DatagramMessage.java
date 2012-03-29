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

package com.deftlabs.core.net;

// OEMware
import com.deftlabs.core.util.ByteUtils;

// Java
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;

/**
 * The datagram message object.
 */
public final class DatagramMessage {

    public final int bufferSize;
    public final int offset;

    /**
     * Choose your poison. Byte buffers can be really sloooow. Up to
     * eight times slower.
     */
    public final ByteBuffer buffer;

    public final byte [] data;

    InetSocketAddress socketAddress;

    private volatile int _sourceAddress = 0;

    DatagramMessage(final int pBufferSize, final int pOffset) {

        if (pBufferSize < 1) throw new IllegalArgumentException("invalid size: " + pBufferSize);

        bufferSize = pBufferSize;
        offset = pOffset;

        // Allocate the buffer and setup the data link.
        buffer = ByteBuffer.allocate(bufferSize);

        data = buffer.array();
    }

    /**
     * Returns the source address in int format.
     * @return The source address.
     */
    public final int getSourceAddress() {
        if (_sourceAddress == 0)
        { _sourceAddress = ByteUtils.getInt(socketAddress.getAddress().getAddress(), 0 ); }

        return _sourceAddress;
    }

    final void reset() {
        buffer.clear();
        buffer.position(offset);
        _sourceAddress = 0;
    }
}

