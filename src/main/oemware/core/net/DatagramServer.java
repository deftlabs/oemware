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
import oemware.core.ServiceConf;
import oemware.core.ThreadBase;
import oemware.core.ServiceException;

// Java
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.nio.channels.Selector;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

// Jakarta Commons
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The generic datagram server. The contract is that you must call
 * the startup/shutdown methods.
 *
 * @author Ryan Nitz
 * @version $Id: DatagramServer.java 37 2008-07-20 20:27:15Z oemware $
 */
public class DatagramServer extends ThreadBase {

    private final DatagramSocket mSocket;
    private final Selector mSelector; 
    private final DatagramChannel mChannel;

    private final DatagramMessageFilter mFilter;
    private final DatagramMessageHandler mHandler;

    private final DatagramMessage mMessage;

    protected String mBindAddress;
    protected int mPort;

    private final int mBufferSize;
    private final int mBufferOffset;

    private final Log mDsLogger = LogFactory.getLog(DatagramServer.class);

    /**
     * Construct a new server with the params.
     * @param pHandler The message handler.
     * @param pFilter The message filter.
     * @param pBufferSize The message buffer size.
     * @param pBufferOffset The buffer offset.
     * @throws ServiceException 
     */
    public DatagramServer(  final DatagramMessageHandler pHandler, 
                            final DatagramMessageFilter pFilter,
                            final int pBufferSize,
                            final int pBufferOffset) 
        throws ServiceException 
    {
        this(   pHandler, 
                pFilter,
                null,
                pBufferSize, 
                pBufferOffset, 
                null, 
                null,
                false);
    }

    /**
     * Construct a new server with the params.
     * @param pHandler The message handler.
     * @param pServiceConf The service configuration object.
     * @param pBufferSize The message buffer size.
     * @param pBufferOffset The buffer offset.
     * @param pBindAddressParamName The key in the config.
     * @param pPortParamName The key in the config.
     * @param pAppendInstanceId If true the instance id is appended
     * to the port number provided. 
     * @throws ServiceException 
     */
    public DatagramServer(  final DatagramMessageHandler pHandler, 
                            final ServiceConf pServiceConf,
                            final int pBufferSize,
                            final int pBufferOffset,
                            final String pBindAddressParamName,
                            final String pPortParamName,
                            final boolean pAppendInstanceId)
        throws ServiceException 
    { 
        this(   pHandler, 
                null, 
                pServiceConf, 
                pBufferSize, 
                pBufferOffset, 
                pBindAddressParamName, 
                pPortParamName, 
                pAppendInstanceId); 
    }

    /**
     * Construct a new server with the params.
     * @param pHandler The message handler.
     * @param pFilter The message filter.
     * @param pServiceConf The service configuration object.
     * @param pBufferSize The message buffer size.
     * @param pBufferOffset The buffer offset.
     * @param pBindAddressParamName The key in the config.
     * @param pPortParamName The key in the config.
     * @param pAppendInstanceId If true the instance id is appended
     * to the port number provided. 
     * @throws ServiceException 
     */
    public DatagramServer(  final DatagramMessageHandler pHandler, 
                            final DatagramMessageFilter pFilter, 
                            final ServiceConf pServiceConf,
                            final int pBufferSize,
                            final int pBufferOffset,
                            final String pBindAddressParamName,
                            final String pPortParamName,
                            final boolean pAppendInstanceId)
        throws ServiceException 
    {
        mHandler = pHandler; 
        mFilter = pFilter;
        mBufferSize = pBufferSize;
        mBufferOffset = pBufferOffset;
        
        mMessage = new DatagramMessage(mBufferSize, mBufferOffset);

        if (!StringUtils.isBlank(pBindAddressParamName)) {
            mBindAddress = pServiceConf.getProperty(pBindAddressParamName);
        }

        if (!StringUtils.isBlank(pPortParamName)) {
            if (pAppendInstanceId) {
                String port 
                    = pServiceConf.getProperty(pPortParamName) 
                    + pServiceConf.getInstanceId();

                mPort = Integer.parseInt(port);
            } else { 
                mPort = pServiceConf.getIntProperty(pPortParamName);
            }
        }

        try {

            mSelector = Selector.open();
            mChannel = DatagramChannel.open();
            mChannel.configureBlocking(false);
            mChannel.register(mSelector, SelectionKey.OP_READ);
            mSocket = mChannel.socket();

        } catch (IOException ioe) { throw new ServiceException(ioe); }
    }

    /**
     * Construct a new server with the params.
     * @param pHandler The message handler.
     * @param pBufferSize The message buffer size.
     * @throws ServiceException 
     */
    public DatagramServer(  final DatagramMessageHandler pHandler, 
                            final DatagramMessageFilter pFilter,
                            final int pBufferSize) 
        throws ServiceException 
    { this(pHandler, pFilter, pBufferSize, 0); }

    /**
     * Bind to the port.
     * @throws ServiceException
     */
    public final void beforeStart() throws ServiceException {
        try {
            mDsLogger.debug("binding to: " 
                            + mBindAddress 
                            + " - port: " 
                            + mPort);

            mSocket.bind(new InetSocketAddress(mBindAddress, mPort));
        } catch (IOException ioe) { 

            StringBuilder error = new StringBuilder("error binding to: '");
            error.append(mBindAddress);
            error.append("' - on port: '");
            error.append(mPort);
            error.append("'");

            throw new ServiceException(error.toString(), ioe);
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
            mMessage.reset();

            mMessage.socketAddress 
                = (InetSocketAddress)mChannel.receive(mMessage.buffer);

            if (mMessage.socketAddress == null) return;

            if (mFilter != null) {
                if (!mFilter.execute(mMessage)) return;
            }

            // Execute the message handler and write the buffer
            // if we need to.
            if (mHandler.execute(mMessage)) {
                if (!mRunning) return;
                //System.out.println("bytes written: " + mChannel.send(mMessage.buffer, mMessage.socketAddress));
                mChannel.send(mMessage.buffer, mMessage.socketAddress);
            }

        } catch (Throwable t) { if (mDsLogger.isErrorEnabled()) logError(t);
        } finally { if (!mRunning) return;
            pKey.interestOps(pKey.interestOps() & (~SelectionKey.OP_WRITE));
        }
    }

    /**
     * Called to shutdown the datagram server :-^
     * @throws ServiceException
     */
    public synchronized void shutdown() throws ServiceException {
        super.shutdown(); 
        if (!mRunning) return;
        try {
            if (mSelector != null) mSelector.close();
            if (mChannel != null) mChannel.close();
            if (mChannel != null) mChannel.disconnect();
        } catch (Throwable t) { logError(t); }
    }

    public final String getBindAddress() { return mBindAddress; }
    public final void setBindAddress(final String pBindAddress) { 
        mBindAddress = pBindAddress; 
    }
    
    public final int getPort() { return mPort; } 
    public final int getBufferSize() { return mBufferSize; }
        
    public final void run() {
        while (mRunning) {
            try {
                if (mSelector.select() == 0) continue;
                if (!mRunning) continue; 

                final Iterator<SelectionKey> iter 
                    = mSelector.selectedKeys().iterator();

                while (iter.hasNext()) {
                    final SelectionKey key = iter.next();
                    iter.remove();
                    if (!mRunning) continue;
                    processMessage(key);
                }
            } catch (Throwable t) { if(mDsLogger.isErrorEnabled()) logError(t);}
        }
    }

    private final void logError(Throwable t) {
        StringBuilder error = new StringBuilder("thread name: ");
        error.append(getName());
        error.append(" - bind address: ");
        error.append(mBindAddress);
        error.append(" - port: ");
        error.append(mPort);
        if (t.getMessage() != null) {
            error.append(" - message: ");
            error.append(t.getMessage());
        }
        mDsLogger.error(error.toString(), t);
    }
}

