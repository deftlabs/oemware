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

// Java
import java.io.RandomAccessFile;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicLong;

// Jakarta Commons
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The named pipe class. Encapsulates all reads/writes
 * to a named pipe. The read and write records will block
 * while executing.
 * 
 * @author Ryan Nitz
 * @version $Id: NamedPipe.java 13 2008-06-15 19:43:04Z oemware $
 */
public class NamedPipe {

    private final String mPipeName;
    private RandomAccessFile mBidirectionalFile;
    private FileInputStream mReadFile;
    private FileOutputStream mWriteFile;
    private FileChannel mFileChannel;
    private final int mType;
    private volatile boolean mRunning;
    private final long mMaxOperationTime;
    private final ServiceManager mServiceManager; 
    protected final AtomicLong mOperationStartTime = new AtomicLong(0L);

    // The pipe types.
    public static final int BIDIRECTIONAL = 1;
    public static final int READ = 2;
    public static final int WRITE = 3;

    public static final byte START_TRANSMIT = (byte)0x01;
    public static final byte END_TRANSMIT = (byte)0x04;

    private final Log mLogger = LogFactory.getLog(NamedPipe.class);

    private final OperationTimeCheckThread mOperationTimeCheckThread 
        = new OperationTimeCheckThread();

    private String mPath;

    private static final String NAMED_PIPE_PATH = "named.pipe.path";

    /**
     * Create a new object. This creates a bidirectional named pipe using
     * a random access file.
     * throws ServiceException
     */
    public NamedPipe(final String pPipeName) throws ServiceException {
        this(pPipeName, BIDIRECTIONAL, 0, false);
    }

    /**
     * Create a new object. This creates a read or write named pipe based
     * on which flag is set in the read boolean.
     * @param pRead The read flag.
     * @throws ServiceException 
     */
    public NamedPipe(   final String pPipeName, 
                        final boolean pRead) 
        throws ServiceException 
    { this(pPipeName, (pRead ? READ : WRITE), 0, false); }

    /**
     * Create a new object. This creates a read or write named pipe based
     * on which flag is set in the read boolean.
     * @param pRead The read flag.
     * @throws ServiceException 
     */
    public NamedPipe(   final String pPipeName, 
                        final boolean pRead,
                        final int pMaxOperationTime) 
        throws ServiceException 
    { this(pPipeName, (pRead ? READ : WRITE), pMaxOperationTime, false); }

    /**
     * Create a new object. This creates a read or write named pipe based
     * on which flag is set in the read boolean. It also starts the operation
     * time monitor with the frequency specified.
     * @param pPipeName The pipe name.
     * @param pType the named pipe type.
     * @param pMaxOperationTime The max operation time. Set to zero for 
     * unlimited or use another constructor ;-)
     * @param pAppendInstanceId The flag to set the instance id on the name.
     * @throws ServiceException 
     */
    public NamedPipe(   final String pPipeName,
                        final int pType, 
                        final long pMaxOperationTime,
                        final boolean pAppendInstanceId) 
        throws ServiceException 
    {
        mServiceManager = ServiceManager.getInstance();

        if (pAppendInstanceId) {
            mPipeName 
                = mServiceManager.getBaseDir() 
                + pPipeName 
                + mServiceManager.getInstanceId();
        } else {
            mPipeName 
                = mServiceManager.getBaseDir() 
                + pPipeName;
        }

        mType = pType;
        mMaxOperationTime = pMaxOperationTime;
    }

    /**
     * Start the process.
     * @throws ServiceException
     */
    public final void startup() throws ServiceException {
        mOperationTimeCheckThread.setName(  ("oemware-core-np-check-thread-" 
                                            + mPipeName));

        mOperationTimeCheckThread.namedPipe = this;

        open();
        mRunning = true;
        mOperationTimeCheckThread.start();
    }

    /**
     * Stop the procss. Must be called when done using the object.
     * @throws ServiceException
     */
    public final void shutdown() throws ServiceException {
        mRunning = false;
        close();
        mOperationTimeCheckThread.interrupt();
    }

    /**
     * Open the named pipe.
     */
    private final void open() throws ServiceException {

        try {

            mOperationStartTime.set(0L);

            final File file = new File(mPipeName);

            if (!file.exists()) {
                throw new ServiceException( "named pipe does not exist: " 
                                            + mPipeName 
                                            + " - create the file (mkfifo) "
                                            + "or fix the location");
            }

            mBidirectionalFile 
                = new RandomAccessFile(file, "rws"); 
            mFileChannel = mBidirectionalFile.getChannel();

            /*
            This is a bug in OSX. It's blocking on the opening of 
            FileInputStream and FileOutputStream.

            Put the selection back in when it's working on OSX.

            switch (mType) {
                case BIDIRECTIONAL: 
                    System.out.println("before bidirectional");
                    mBidirectionalFile 
                        = new RandomAccessFile(mPipeName, "rwd"); 
                    mFileChannel = mBidirectionalFile.getChannel();
                    break;
                case READ: 
                    System.out.println("before read");
                    mReadFile = new FileInputStream(mPipeName);
                    mFileChannel = mReadFile.getChannel();
                    break;
                case WRITE: 
                    System.out.println("before write");
                    mWriteFile = new FileOutputStream(mPipeName);
                    System.out.println("after write");
                    mFileChannel = mWriteFile.getChannel();
                    break;
            }
            */
        } catch (FileNotFoundException fnfe) {
            throw new ServiceException(fnfe);
        }
    }

    /**
     * Returns the pipe name.
     * @return The pipe name.
     */
    public final String getPipeName() { return mPipeName; }

    /**
     * Returns true if this class is set to read.
     * @return True if readable.
     */
    public final boolean isRead() { return (mType == WRITE) ? false : true; }

    /**
     * Close the named pipe.
     */
    private final void close() throws ServiceException {
        try {
            //System.out.println("before file channel close");
            //mFileChannel.close();
            //System.out.println("after file channel close");
            if (mBidirectionalFile != null) mBidirectionalFile.close();
            if (mReadFile != null) mReadFile.close();
            if (mWriteFile != null) mWriteFile.close();
        } catch (IOException ioe) { throw new ServiceException(ioe); }
    }

    /**
     * Restart the named pipe. This closes and opens the channel.
     */
    private final void restart() throws ServiceException {
        logWarn("restart called");
        mRunning = false;
        close();
        open();
        mRunning = true;
    }

    /**
     * Handle writing to the np.
     * @param pBuffer The buffer to write. This assumes the
     * params are set on the byte buffer.
     * @return The number of bytes written.
     * @throws ServiceExcpetion
     */
    public final int write(final ByteBuffer pBuffer) 
        throws ServiceException 
    {
        if (mType == READ) {
            throw new IllegalStateException("read only pipe: " + mPipeName);
        }

        mOperationStartTime.set(System.currentTimeMillis());
        try { if (!mRunning) return 0; return mFileChannel.write(pBuffer);
        } catch (AsynchronousCloseException ace) { return 0;
        } catch (ClosedChannelException cce) { return 0;
        } catch (IOException ioe) {
            throw new ServiceException(("named pipe: " + mPipeName), ioe);
        } finally { mOperationStartTime.set(0L); }
    }

    /**
     * Write all the data to the buffer. See the description of readAll(buffer)
     * for more information on how this method works.
     * @param pBuffer The buffer to write. This assumes the
     * params are set on the byte buffer.
     * @return The bytes written.
     * @throws ServiceExcpetion
     */
    public final int writeAll(final ByteBuffer pBuffer) 
        throws ServiceException 
    {
        if (mType == READ) {
            throw new IllegalStateException("read only pipe: " + mPipeName);
        }

        mOperationStartTime.set(System.currentTimeMillis());
        try {
            int count = 0;
            final int size = pBuffer.limit();
            while (mRunning) {
                count += mFileChannel.write(pBuffer);
                if (count == size) break;
            }
            return count;
        } catch (AsynchronousCloseException ace) { return 0;
        } catch (ClosedChannelException cce) { return 0;
        } catch (IOException ioe) {
            throw new ServiceException(("named pipe: " + mPipeName), ioe);
        } finally { mOperationStartTime.set(0L); }
    }

    /**
     * Handle reading from the np.
     * @param pBuffer User configured byte buffer.
     * @return The number of bytes read.
     * @throws ServiceException
     */
    public final int read(final ByteBuffer pBuffer) throws ServiceException {
        
        if (mType == WRITE) {
            throw new IllegalStateException("write only pipe: " + mPipeName);
        }

        mOperationStartTime.set(System.currentTimeMillis());
        try {
            if (!mRunning) return 0;
            return mFileChannel.read(pBuffer);
        } catch (AsynchronousCloseException ace) { return 0;
        } catch (ClosedChannelException cce) { return 0;
        } catch (IOException ioe) {
            throw new ServiceException(("named pipe: " + mPipeName), ioe);
        } finally { mOperationStartTime.set(0L); }
    }

    /**
     * Fill the buffer.
     * @param pBuffer The buffer to write. This assumes the
     * params are set on the byte buffer. The receiver needs
     * to check and see the number of bytes actually written.
     * Zero is possible if the named pipe is being restarted.
     * @throws ServiceExcpetion
     */
    public final int readAll(final ByteBuffer pBuffer) 
        throws ServiceException 
    {
        if (mType == WRITE) {
            throw new IllegalStateException("write only pipe: " + mPipeName);
        }

        mOperationStartTime.set(System.currentTimeMillis());
        try {
            int count = 0;
            final int size = pBuffer.limit();
            while (mRunning) {
                count += mFileChannel.read(pBuffer);
                if (count == size) break;
            }

            return count;

        } catch (AsynchronousCloseException ace) { return 0;
        } catch (ClosedChannelException cce) { return 0;
        } catch (IOException ioe) {
            throw new ServiceException(("named pipe: " + mPipeName), ioe);
        } finally { mOperationStartTime.set(0L); }
    }

    /**
     * Log the named pipe warning. This is a convienance method.
     * @param pMessage The message.
     */
    private final void logWarn(final String pMessage) {
        mLogger.warn(  "pipe name: "
                        + mPipeName
                        + " - type: "
                        + mType
                        + " - message: "
                        + pMessage);
    }

    /**
     * Log the named pipe exception. 
     * @param pThrowable The exception.
     */
    private final void logWarn(final Throwable pThrowable) {
        mLogger.warn(  "pipe name: " 
                        + mPipeName
                        + " - type: "
                        + mType
                        + " - source message: "
                        + pThrowable.getMessage(), 
                        pThrowable);

    }

    /**
     * Log the named pipe trace. 
     * @param pMessage The message.
     */
    private final void logTrace(final String pMessage) {
        mLogger.trace(  "pipe name: "
                        + mPipeName
                        + " - type: "
                        + mType
                        + " - message: "
                        + pMessage);
    }


    /**
     * Log the named pipe info. 
     * @param pMessage The message.
     */
    private final void logInfo(final String pMessage) {
        mLogger.info(  "pipe name: "
                        + mPipeName
                        + " - type: "
                        + mType
                        + " - message: "
                        + pMessage);
    }

    /**
     * Log the named pipe exception. 
     * @param pMessage The message.
     */
    private final void logError(final String pMessage) {
        mLogger.error(  "pipe name: " 
                        + mPipeName
                        + " - type: "
                        + mType
                        + " - message: "
                        + pMessage); 
    }

    /**
     * Log the named pipe exception.
     * @param pThrowable The exception.
     */
    private final void logError(final Throwable pThrowable) {
        mLogger.error(  "pipe name: " 
                        + mPipeName
                        + " - type: "
                        + mType
                        + " - source message: "
                        + pThrowable.getMessage(), 
                        pThrowable);

    }

    /**
     * Log the named pipe excepiton.
     * @param pThrowable The exception.
     * @param pMessage The message.
     */
    private final void logError(final String pMessage, 
                                final Throwable pThrowable) 
    {
        mLogger.error(  "pipe name: " 
                        + mPipeName
                        + " - message: "
                        + pMessage
                        + " - source message: "
                        + pThrowable.getMessage(), 
                        pThrowable);

    }

    /**
     * The named pipe max operation time monitor thread. This checks to see 
     * if the pipe has been stuck in a mode too long. If it has it will
     * restart the process. Only start this process when requested by
     * the constructor arg. 
     */
    private class OperationTimeCheckThread extends Thread {

        private NamedPipe namedPipe;
        private static final long FREQUENCY = 2000;
        public void run() {

            // If there isn't a max operation time set then we're not going
            // to monitor this object.
            if (mMaxOperationTime == 0) return;

            long executionTime;
            long currentTime;
            while (true) {
                try {
                    
                    sleep(FREQUENCY);

                    if (!namedPipe.mRunning) continue;
                    if (namedPipe.mOperationStartTime.get() == 0L) continue;
    
                    currentTime = System.currentTimeMillis();
                    executionTime = (currentTime - namedPipe.mOperationStartTime.get());

                    if (executionTime == currentTime) continue;

                    if (executionTime == 0) continue;
                    if (namedPipe.mOperationStartTime.get() == 0L) continue;

                    // Check to see how long the operation has been in place.
                    if (executionTime >= mMaxOperationTime) {
                        if (!mRunning) continue;
                        if (namedPipe.mOperationStartTime.get() == 0L) continue;

                        namedPipe.logError( "operation running to long "
                                            + "- time (ms): "
                                            + executionTime);

                        if (!namedPipe.mRunning) continue;

                        if (namedPipe.mOperationStartTime.get() == 0L) continue;

                        namedPipe.restart();
                    }
                } catch (InterruptedException ie) { return; 
                } catch (Throwable t) { namedPipe.logError(t); } 
            }
        }
    }
}

