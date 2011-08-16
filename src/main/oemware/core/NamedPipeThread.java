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

package oemware.core;

// OEMware
import oemware.core.pool.ObjectPool;

// Java
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.NoSuchElementException;

// Jakarta Commons
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The named pipe thread class. This operates on a named
 * pipe object. Data is pulled/added to a queue (one queue
 * per object. The named pipe thread doesn't do anything around
 * data validation or verification. It simply reads and writes
 * the buffer size.
 * 
 * @author Ryan Nitz
 * @version $Id: NamedPipeThread.java 13 2008-06-15 19:43:04Z oemware $
 */
public final class NamedPipeThread extends SingleThread {

    private final NamedPipe mNamedPipe;
    private final boolean mRead;
    private final int mBufferSize;

    /**
     * Data is received or passed to the queue based on 
     * the mRead flag. If the mode is write then the 
     * object will be returned to the pool. This return
     * only happens if the pool is not null. Make sure this is
     * an array based queue.
     */
    private BlockingQueue <ByteBuffer>mQueue;

    /**
     * If you use the buffer pool you must return the object in your code :-)
     */
    private ObjectPool mBufferPool;

    private final Log mLogger = LogFactory.getLog(NamedPipeThread.class);

    /**
     * Create a new object.
     * @param pNamedPipe The named pipe.
     * @param pQueue The read/write queue (based on pRead flag).
     * @param pBufferSize The size of the buffer. This is only
     * used if the buffer pool isn't passed in.
     */
    public NamedPipeThread( final NamedPipe pNamedPipe, 
                            final BlockingQueue <ByteBuffer>pQueue,
                            final int pBufferSize)
    { this(pNamedPipe, null, pQueue, pBufferSize); }

    /**
     * Create a new object.
     * @param pNamedPipe The named pipe.
     * @param pBufferPool This is an optional param. May be null.
     * @param pQueue The read/write queue (based on pRead flag).
     * @param pBufferSize The size of the buffer. This is only
     * used if the buffer pool isn't passed in.
     */
    public NamedPipeThread( final NamedPipe pNamedPipe, 
                            final ObjectPool pBufferPool,
                            final BlockingQueue <ByteBuffer>pQueue,
                            final int pBufferSize)
    {
        super();
        mNamedPipe = pNamedPipe;
        mRead = mNamedPipe.isRead();
        mBufferPool = pBufferPool;
        mQueue = pQueue;
        mBufferSize = pBufferSize;
    }

    /**
     * Handle the read/write to the named pipe.
     */
    public final void execute() {
        try { if (mRead) read(); else write();
        } catch (Throwable t) { logNamedPipeError(t); }
    }

    /**
     * Handle writing to the np. 
     */
    private final void write() {
        // Get the data from the queue.    
        ByteBuffer buffer = null;
        try {
            buffer = mQueue.take();
            if (buffer == null) return;

            // IF we didn't write all of the data, get out of here.
            // Leave the head element in the queue.
            if (mNamedPipe.writeAll(buffer) != mBufferSize) {
                logNamedPipeError("did not write complete buffer");
            }

        } catch (InterruptedException ie) { return;
        } catch (ServiceException se) { logNamedPipeError(se);
        } catch (Throwable t) { logNamedPipeError(t);
        } finally { returnBuffer(buffer); }
    }

    /**
     * Handle reading from the np.
     */
    private final void read() {
        boolean error = false;
        ByteBuffer buffer = null;
        try {
            // Check and make sure you can get buffer.
            buffer = getCleanBuffer();
            if (buffer == null) return;

            if (mNamedPipe.readAll(buffer) != mBufferSize)  {
                logNamedPipeError(  ("data read from named pipe does not "
                                    + "equal buffer size: " 
                                    + mBufferSize));
                error = true;
                return;
            }

            // Pass the message to the queue.
            if (!mQueue.offer(buffer)) { 
                logNamedPipeError(  "queue is full - message discarded - "
                                    + " tune the named pipe components"); 

                error = true;
                return;
            }

        } catch (ServiceException se) {
            error = true;
            logNamedPipeError(se);
        } catch (Throwable t) {
            error = true;
            logNamedPipeError(t);
        } finally {
            // We have to make sure we get the buffer 
            // back in the pool because we're not going
            // to pass anything to the named pipe.
            if (error) returnBuffer(buffer);
        }
    }

    public final NamedPipe getNamedPipe() {
        return mNamedPipe;
    }

    private void returnBuffer(final ByteBuffer pBuffer) {
        if (mBufferPool == null) return;
        if (pBuffer == null) return;
        try { mBufferPool.returnObject(pBuffer);
        } catch (Exception e) { logNamedPipeError(e); }
    }

    /**
     * Returns a buffer from the pool or new if no pool
     * is available.
     * @return The buffer or null if there was a problem.
     */
    private final ByteBuffer getCleanBuffer() {
        // If we don't have a buffer pool, create a new buffer.
        if (mBufferPool == null) return ByteBuffer.allocate(mBufferSize);

        // We're going to get the object from the pool.
        try {
            return (ByteBuffer)mBufferPool.borrowObject(); 
        } catch (NoSuchElementException nsee) {
            // This exception is thrown when the object pool
            // is empty and blocking is not set. See
            // setWhenExhaustedAction(byte) for more info.
            logNamedPipeError(  "buffer pool is maxed - max active: " 
                                + mBufferPool.getMaxActive());
            
            // This message is lost because we continue and the
            // first byte is read again. Tune the buffer pool to
            // the queue size.

        } catch (Exception e) { logNamedPipeError(e); }
        return null;
    }

    /**
     * Log the named pipe excepiton. This is a convienance method.
     * @param pThrowable The exception.
     */
    private final void logNamedPipeError(final Throwable pThrowable) {
        mLogger.error(  "thread: " 
                        + getName()
                        + " - "
                        + pThrowable.getMessage(), 
                        pThrowable);

    }

    private final void logNamedPipeError(final String pMessage) {
        mLogger.error(  "thread: " 
                        + getName()
                        + " - "
                        + pMessage);
    }
}

