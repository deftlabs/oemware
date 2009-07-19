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

// Jakarta Commons
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Java
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The thread base class. Extend this class for your threaded
 * service.
 *
 * @author Ryan Nitz
 * @version $Id: ThreadBase.java 13 2008-06-15 19:43:04Z oemware $
 */
public abstract class ThreadBase extends Thread {

    /**
     * The running flag. This is only to be used by this thread.
     */
    protected volatile boolean mRunning = false;
    private final Log mThreadLogger = LogFactory.getLog(ThreadBase.class);

    /**
     * Set this flag to join the thread before proceeding.
     */
    protected boolean mJoinThread = false;

    protected long mJoinTimeout = 0;

    // The lifecycle states.
    private static final int BEFORE_START = 1;
    private static final int AFTER_START = 2;
    private static final int BEFORE_STOP = 3;
    private static final int AFTER_STOP = 4;
    private static final int START = 5;

    /**
     * Execute the proper lifecycle method.
     * @param pState The state.
     */
    private final void executeLifecycleMethod(final int pState) {
        try {
            switch (pState) {
                case START: start(); break;
                case BEFORE_START: beforeStart(); break; 
                case AFTER_START: afterStart(); break; 
                case BEFORE_STOP: beforeStop(); break; 
                case AFTER_STOP: afterStop(); break; 
            }
        } catch (Throwable t) {
            mThreadLogger.error(    "thread name: " 
                                    + getName() 
                                    + " - " 
                                    + t.getMessage(), 
                                    t);
 
        }
    }

    /**
     * This must be implemented for the thread to work.
     */
    public abstract void run(); 

    protected void beforeStart() throws ServiceException { }
    protected void afterStart() throws ServiceException { }

    protected void beforeStop() throws ServiceException { }
    protected void afterStop() throws ServiceException { }

    public synchronized void startup() throws ServiceException {
        executeLifecycleMethod(BEFORE_START);
        mRunning = true;
        executeLifecycleMethod(START);
        executeLifecycleMethod(AFTER_START);
    }

    public synchronized void shutdown() throws ServiceException {
        if (!mRunning) return;
        mRunning = false;
        executeLifecycleMethod(BEFORE_STOP);

        if (isAlive() && !interrupted()) {
            try {
                if (mJoinThread) {
                    try { join(mJoinTimeout);
                    } catch (InterruptedException ie) {
                        mThreadLogger.warn( "failed to join thread: " 
                                            + getName() 
                                            + " in " 
                                            + mJoinTimeout 
                                            + " (ms)"); 
                    }
                } else { interrupt(); }
            } catch (Throwable t) {
                mThreadLogger.error(    "thread name: " 
                                        + getName() 
                                        + " - " 
                                        + t.getMessage(), 
                                        t);
            }
        }

        executeLifecycleMethod(AFTER_STOP);
    }

    public final void setJoinThread(final boolean pJoinThread) {
        mJoinThread = pJoinThread;
    }
    
    public final boolean getJoinThread() { return mJoinThread; }
    public final void setJoinTimeout(final long pJoinTimeout) {
        mJoinTimeout = pJoinTimeout;
    }

    public final long getJoinTimeout() { return mJoinTimeout; }
    public final boolean isRunning() { return mRunning; }
}

