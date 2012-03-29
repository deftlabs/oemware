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
import com.deftlabs.core.util.ThreadBase;

// Java
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.nio.channels.Selector;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The generic datagram server. The contract is that you must call
 * the startup/shutdown methods.
 */
public class DatagramServer extends ThreadBase {

    private final DatagramSocket _socket;
    private final Selector _selector;
    private final DatagramChannel _channel;

    private final DatagramMessageFilter _filter;
    private final DatagramMessageHandler _handler;

    private final DatagramMessage _message;

    protected String _bindAddress;
    protected int _port;

    private final int _bufferSize;
    private final int _bufferOffset;

    private static final Logger LOG = Logger.getLogger(ThreadBase.class.getName());

    /**
     * Construct a new server with the params.
     * @param pHandler The message handler.
     * @param pBufferSize The message buffer size.
     * @param pBufferOffset The buffer offset.
     * @param pBindAddress The bind address.
     * @param pPort The port.
     * @throws IOException
     */
    public DatagramServer(  final DatagramMessageHandler pHandler,
                            final int pBufferSize,
                            final int pBufferOffset,
                            final String pBindAddress,
                            final int pPort)
        throws IOException
    {
        this(   pHandler,
                null,
                pBufferSize,
                pBufferOffset,
                pBindAddress,
                pPort);
    }

    /**
     * Construct a new server with the params.
     * @param pHandler The message handler.
     * @param pFilter The message filter.
     * @param pBufferSize The message buffer size.
     * @param pBufferOffset The buffer offset.
     * @param pBindAddress The bind address.
     * @param pPort The port.
     * @throws IOException
     */
    public DatagramServer(  final DatagramMessageHandler pHandler,
                            final DatagramMessageFilter pFilter,
                            final int pBufferSize,
                            final int pBufferOffset,
                            final String pBindAddress,
                            final int pPort)

        throws IOException
    {
        _handler = pHandler;
        _filter = pFilter;
        _bufferSize = pBufferSize;
        _bufferOffset = pBufferOffset;

        _message = new DatagramMessage(_bufferSize, _bufferOffset);

        _bindAddress = pBindAddress;
        _port = pPort;

        _selector = Selector.open();
        _channel = DatagramChannel.open();
        _channel.configureBlocking(false);
        _channel.register(_selector, SelectionKey.OP_READ);
        _socket = _channel.socket();
    }

    /**
     * Bind to the port.
     */
    public final void beforeStart() {
        try {
            LOG.log(Level.FINE, "binding to: " + _bindAddress + " - port: " + _port);

            _socket.bind(new InetSocketAddress(_bindAddress, _port));

        } catch (final IOException ioe) {
            StringBuilder error = new StringBuilder("error binding to: '");
            error.append(_bindAddress);
            error.append("' - on port: '");
            error.append(_port);
            error.append("'");
            throw new IllegalStateException(error.toString(), ioe);
        }
    }

    /**
     * Read the message, call the handler and write the response
     * if necessary.
     * @param pKey The selection key.
     */
    private final void processMessage(final SelectionKey pKey) {
        try {

            pKey.interestOps(pKey.interestOps() | SelectionKey.OP_WRITE);
            _message.reset();

            _message.socketAddress
                = (InetSocketAddress)_channel.receive(_message.buffer);

            if (_message.socketAddress == null) return;

            if (_filter != null) {
                if (!_filter.execute(_message)) return;
            }

            // Execute the message handler and write the buffer
            // if we need to.
            if (_handler.execute(_message)) {
                if (!_running) return;
                //System.out.println("bytes written: " + _channel.send(_message.buffer, _message.socketAddress));
                _channel.send(_message.buffer, _message.socketAddress);
            }

        } catch (Throwable t) { if (LOG.isLoggable(Level.SEVERE)) logError(t);
        } finally { if (!_running) return;
            pKey.interestOps(pKey.interestOps() & (~SelectionKey.OP_WRITE));
        }
    }

    /**
     * Called to shutdown the datagram server :-^
     */
    public synchronized void shutdown() {
        super.shutdown();
        if (!_running) return;
        try {
            if (_selector != null) _selector.close();
            if (_channel != null) _channel.close();
            if (_channel != null) _channel.disconnect();
        } catch (Throwable t) { logError(t); }
    }

    public final String getBindAddress() { return _bindAddress; }
    public final void setBindAddress(final String pV) { _bindAddress = pV; }

    public final int getPort() { return _port; }
    public final int getBufferSize() { return _bufferSize; }

    public final void run() {
        while (_running) {
            try {
                if (_selector.select() == 0) continue;
                if (!_running) continue;

                final Iterator<SelectionKey> iter = _selector.selectedKeys().iterator();

                while (iter.hasNext()) {
                    final SelectionKey key = iter.next();
                    iter.remove();
                    if (!_running) continue;
                    processMessage(key);
                }

            } catch (final Throwable t) { if (LOG.isLoggable(Level.SEVERE)) logError(t);}
        }
    }

    private final void logError(final Throwable t) {
        StringBuilder error = new StringBuilder("thread name: ");
        error.append(getName());
        error.append(" - bind address: ");
        error.append(_bindAddress);
        error.append(" - port: ");
        error.append(_port);
        if (t.getMessage() != null) {
            error.append(" - message: ");
            error.append(t.getMessage());
        }
        LOG.log(Level.SEVERE, error.toString(), t);
    }
}

