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

