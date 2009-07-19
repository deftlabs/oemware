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

package oemware.core.net;

// OEMware
import oemware.core.util.ByteUtils;
import oemware.core.util.NumberUtils;

// Java
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The generic shared datagram message handler. This takes the message, looks
 * at the id and then dispatches to the correct handler.<br/><br/>
 *
 * This is not to be used for long running actions or actions that require
 * high throughput.
 * 
 * @author Ryan Nitz
 * @version $Id: SharedUdpMessageHandler.java 37 2008-07-20 20:27:15Z oemware $
 */
public final class SharedUdpMessageHandler implements DatagramMessageHandler {

    private final Map<Short, DatagramMessageHandler> mHandlers 
        = new ConcurrentHashMap<Short, DatagramMessageHandler>();

    private final ThreadLocalIdBuffer mIdBufferContainer = new ThreadLocalIdBuffer();

    /**
     * Called to execute the handler.
     * @param pDatagramMessage The message.
     * @return True if the buffer should be written.
     */
    public boolean execute(final DatagramMessage pDatagramMessage) {
        final ByteBuffer buffer = mIdBufferContainer.getBuffer();
        buffer.put(pDatagramMessage.data, 0, 2);

        final DatagramMessageHandler handler 
            = mHandlers.get(NumberUtils.getShort(buffer.getShort(0)));

        if (handler == null) return false;
        return handler.execute(pDatagramMessage);
    }

    public Map<Short, DatagramMessageHandler> getHandlers() { return mHandlers;}
    public void setHandlers(final Map<NetMessageId, DatagramMessageHandler> pHandlers) {
        for (final NetMessageId netMsgId : pHandlers.keySet()) { 
            mHandlers.put(netMsgId.id(), pHandlers.get(netMsgId));
        }
    }

    /**
     * The thread local id buffer.
     */
    private final class ThreadLocalIdBuffer extends ThreadLocal {
        public final Object initialValue() { return ByteBuffer.allocate(2); }
        public final ByteBuffer getBuffer() {
            final ByteBuffer buffer = (ByteBuffer)super.get();
            buffer.clear();
            return buffer;
        }
    }
}

