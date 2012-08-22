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

