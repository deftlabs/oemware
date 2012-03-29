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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The thread base class. Extend this class for your threaded
 * service.
 */
public abstract class ThreadBase extends Thread {

    /**
     * The running flag. This is only to be used by this thread.
     */
    protected volatile boolean _running = false;

    private static final Logger BASE_LOG = Logger.getLogger(ThreadBase.class.getName());

    /**
     * Set this flag to join the thread before proceeding.
     */
    protected boolean _joinThread = false;

    protected long _joinTimeout = 0;

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
            BASE_LOG.log(Level.SEVERE, "thread name: " + getName() + " - " + t.getMessage(), t);
        }
    }

    /**
     * This must be implemented for the thread to work.
     */
    public abstract void run();

    protected void beforeStart() { }
    protected void afterStart() { }

    protected void beforeStop() { }
    protected void afterStop() { }

    public synchronized void startup() {
        executeLifecycleMethod(BEFORE_START);
        _running = true;
        executeLifecycleMethod(START);
        executeLifecycleMethod(AFTER_START);
    }

    public synchronized void shutdown() {
        if (!_running) return;
        _running = false;
        executeLifecycleMethod(BEFORE_STOP);

        if (isAlive() && !interrupted()) {
            try {
                if (_joinThread) {
                    try { join(_joinTimeout);
                    } catch (InterruptedException ie) {
                        BASE_LOG.log(Level.WARNING, "failed to join thread: " + getName() + " in " + _joinTimeout + " (ms)");
                    }
                } else { interrupt(); }
            } catch (Throwable t) {
                BASE_LOG.log(Level.SEVERE, "thread name: " + getName() + " - " + t.getMessage(), t);
            }
        }

        executeLifecycleMethod(AFTER_STOP);
    }

    public final void setJoinThread(final boolean pV) { _joinThread = pV; }
    public final boolean getJoinThread() { return _joinThread; }

    public final void setJoinTimeout(final long pV) { _joinTimeout = pV; }
    public final long getJoinTimeout() { return _joinTimeout; }

    public final boolean isRunning() { return _running; }
}

