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

/**
 * The multiple thread object. This creates multiple threads
 * of the extended class, and runs them.
 *
 * @author Ryan Nitz
 * @version $Id: MultiThread.java 104 2009-01-14 18:49:04Z oemware $
 */
public abstract class MultiThread implements Runnable {

    private volatile boolean mRunning = false;
    private final boolean mThreadSleep;
    private final long mSleepTime;
    private int mThreadCount;
    private Thread[] mThreads;
    private String mThreadName;
    private final int mThreadPriority;
    private final boolean mJoinThread;

    private final static Log mThreadLogger 
        = LogFactory.getLog(MultiThread.class);

    /**
     * Create the new object.
     * @param pSleepTime The sleep time.
     * @param pThreadCount The thread count.
     * @param pThreadName The thread name.
     * @param pThreadPriority The thread priority.
     */
    public MultiThread( final long pSleepTime, 
                        final int pThreadCount, 
                        final String pThreadName,
                        final int pThreadPriority,
                        final boolean pJoinThread) 
    {
        mSleepTime = pSleepTime;
        mThreadCount = pThreadCount;
        mThreadName = pThreadName;
        mThreadPriority = pThreadPriority;
        mJoinThread = pJoinThread;
        if (mSleepTime > 0) mThreadSleep = true;
        else mThreadSleep = false;
    }

    /**
     * Create a new object with param(s). If you call this, you must set
     * the thread name (setName(str)).
     * @param pThreadCount The thread count.
     */
    public MultiThread(final int pThreadCount) {
        this(0l, pThreadCount, null, Thread.NORM_PRIORITY, true);
    }

    /**
     * Create a new object with params.
     * @param pThreadCount The thread count.
     * @param pThreadName The thread name.
     */
    public MultiThread( final int pThreadCount, 
                        final String pThreadName)
    { this(0l, pThreadCount, pThreadName, Thread.NORM_PRIORITY, true); }

    /**
     * Create a new object with params.
     * @param pThreadName The thread name.
     */
    public MultiThread( final String pThreadName)
    { this(0l, 0, pThreadName, Thread.NORM_PRIORITY, true); }

    /**
     * Create a new object with params. You need to set the thread count
     * or this will not run any threads.
     * @param pThreadName The thread name.
     * @param pSleepTime The sleep time.
     */
    public MultiThread( final String pThreadName, final long pSleepTime)
    { this(pSleepTime, 0, pThreadName, Thread.NORM_PRIORITY, true); }

    /**
     * Create a new object with params.
     * @param pThreadCount The thread count.
     * @param pThreadName The thread name.
     * @param pThreadPriority The thread priority.
     */
    public MultiThread( final int pThreadCount, 
                        final String pThreadName, 
                        final int pThreadPriority,
                        final boolean pJoinThread) 
    { this(0l, pThreadCount, pThreadName, pThreadPriority, pJoinThread); }

    /**
     * Start the component/threads.
     */
    public final synchronized void startup() {
        if (mRunning) return;
        mRunning = true;
        mThreads = new Thread[mThreadCount];
        for (int idx=0; idx < mThreadCount; idx++) {
            mThreads[idx] = new Thread(this, (mThreadName + "-" + idx));
            mThreads[idx].setPriority(mThreadPriority);
            mThreads[idx].start();
        }
    }

    /**
     * Returns true if the thread is running.
     * @return True if the thread is running.
     */
    public final boolean isRunning() { return mRunning; }

    /**
     * Set the running flag to false.
     */
    public final void stopRunning() { mRunning = false; }     

    /**
     * Shutdown the component.
     */
    public synchronized void shutdown() {
        if (!mRunning) return;
        mRunning = false;

        for (Thread thread : mThreads) {
            thread.interrupt();
            if (!mJoinThread) continue;
            try { thread.join();
            } catch (InterruptedException ie) { }
        }
    }

    /** 
     * The run method.
     */
    public final void run() {
        while (mRunning) {
            try {
                if (mThreadSleep) { // Sleep if configured to do so.
                    try { Thread.currentThread().sleep(mSleepTime);
                    } catch (InterruptedException ie) { continue; }
                }

                execute();
            } catch (Throwable t) { mThreadLogger.error(t.getMessage(), t); }
        }
    }

    /**
     * Set the thread name (call before startup).
     * @param pV The thread name.
     */
    public void setName(final String pV) { mThreadName = pV; }

    /**
     * This is implemented by the extending class. It's called in the thread's
     * run method in an infinite loop. It sleeps if configured to do so.
     */
    public abstract void execute();

    /**
     * Set the thread count. THis must be called before startup and does 
     * nothing after startup is called.
     * @param pCount The number of threads to spawn.
     */
    public final void setThreadCount(final int pCount) { mThreadCount = pCount; }

    /**
     * Returns the thread count.
     * @return The thread count.
     */
    public final int getThreadCount() { return mThreadCount; }
}

