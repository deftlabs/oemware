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

package oemware.core.util;

// Jakarta Commons
import org.apache.commons.logging.Log;

// Java
import java.util.Map;
import java.util.HashMap;

/**
 * <p>The trace log utils. Used to determine exection time and log.</p>
 *
 * <p><b>note:</b> These utils create thread local objects so the class may
 * not be ideal in short/frequent traces.</p>
 *
 * <p>This object is a singleton.</p>
 *
 * <p><pre>
 * Usage:
 * TraceUtils.getInstance().startTrace(this.class+"test", mLog);
 *
 * ... do something ...
 *
 * TraceUtils.getInstance().stopTrace("test");
 * </p></pre>
 *
 * @author Ryan Nitz
 * @version $Id: TraceUtils.java 13 2008-06-15 19:43:04Z oemware $
 */
public final class TraceUtils {

    private static final int TRACE = 0, DEBUG = 1, INFO = 2;

    private final ThreadLocalTimer sTimers = new ThreadLocalTimer();
    
    private static TraceUtils sInstance = null;
    private static final Object sMutex = new Object();
    private TraceUtils() { }

    /**
     * Start the trace.
     * @param pTraceName The trace name.
     * @param pLog The log.
     * @param pWarnTime The warn time.
     * @param pErrorTime The time to write an error message.
     */
    public final long startTrace(   final String pTraceName, 
                                    final Log pLog,
                                    final long pWarnTime,
                                    final long pErrorTime)
    { return startTrace(pTraceName, pLog,TRACE, pWarnTime, pErrorTime); }

    /**
     * Start the trace.
     * @param pTraceName The trace name.
     * @param pLog The log.
     */
    public final long startTrace(   final String pTraceName, 
                                    final Log pLog)
    { return startTrace(pTraceName, pLog,TRACE, Long.MAX_VALUE,Long.MAX_VALUE);}

    /**
     * Start the debug trace.
     * @param pTraceName The trace name.
     * @param pLog The log.
     * @param pWarnTime The warn time.
     * @param pErrorTime The time to write an error message.
     */
    public final long startDebugTrace(  final String pTraceName, 
                                        final Log pLog,
                                        final long pWarnTime,
                                        final long pErrorTime)
    { return startTrace(pTraceName, pLog, DEBUG, pWarnTime, pErrorTime); }

    /**
     * Start the debug trace.
     * @param pTraceName The trace name.
     * @param pLog The log.
     */
    public final long startDebugTrace(  final String pTraceName, 
                                        final Log pLog)
    { return startTrace(pTraceName,pLog, DEBUG, Long.MAX_VALUE,Long.MAX_VALUE);}

    /**
     * Stop the debug trace.
     * @param pTraceName The trace name.
     */
    public final void stopDebugTrace(final String pTraceName) 
    { stopTrace(pTraceName); } 

    /**
     * Start the info trace.
     * @param pTraceName The trace name.
     * @param pLog The log.
     * @param pWarnTime The warn time.
     * @param pErrorTime The time to write an error message.
     */
    public final long startInfoTrace(   final String pTraceName, 
                                        final Log pLog,
                                        final long pWarnTime,
                                        final long pErrorTime)
    { return startTrace(pTraceName, pLog, INFO, pWarnTime, pErrorTime); }

    /**
     * Start the info trace.
     * @param pTraceName The trace name.
     * @param pLog The log.
     */
    public final long startInfoTrace(   final String pTraceName, 
                                        final Log pLog)
    { return startTrace(pTraceName, pLog, INFO, Long.MAX_VALUE, Long.MAX_VALUE);}

    /**
     * Stop the info trace.
    * @param pTraceName The trace name.
     */
    public final void stopInfoTrace(final String pTraceName) 
    { stopTrace(pTraceName); } 

    /**
     * Start the trace.
     * @param pTraceName The trace name.
     * @param pLog The log.
     * @param pLogLevel The log level (trace, debug or info).
     * @param pWarnTime The warn time.
     * @param pErrorTime The time to write an error message.
     */
    private long startTrace(   final String pTraceName, 
                                    final Log pLog,
                                    final int pLogLevel,
                                    final long pWarnTime,
                                    final long pErrorTime)
    {
        final Trace trace = sTimers.getTrace(pTraceName);
        trace.reset();
        trace.name = pTraceName;
        trace.log = pLog;
        trace.level = pLogLevel;
        trace.warnTime = pWarnTime;
        trace.errorTime = pErrorTime;
        trace.startTime = System.currentTimeMillis();
        return trace.startTime;
    }

    /**
     * Stop the trace.
     * @param pTraceName The trace name.
     */
    public final long stopTrace(final String pTraceName) {
        final Trace trace = sTimers.getTrace(pTraceName);
        final long executeTime = System.currentTimeMillis() - trace.startTime;

        final StringBuilder sb = new StringBuilder("[trace]: ");
        sb.append(trace.name);
        sb.append(" - time: ");
        sb.append(executeTime);
        sb.append(" (ms)");

        if (executeTime >= trace.errorTime) {
            if (trace.log.isErrorEnabled()) { trace.log.error(sb); }
        } else if (executeTime >= trace.warnTime) {
            if (trace.log.isWarnEnabled()) { trace.log.warn(sb); }
        } else if (trace.level == TRACE) {
            if (trace.log.isTraceEnabled()) { trace.log.trace(sb); }
        } else if (trace.level == DEBUG) {
            if (trace.log.isDebugEnabled()) { trace.log.debug(sb); }
        } else if (trace.level == INFO) {
            if (trace.log.isInfoEnabled()) { trace.log.info(sb); }
        }
        return executeTime;
    }
    public static final TraceUtils getInstance() {
        if (sInstance != null) return sInstance;
        synchronized(sMutex) {
            if (sInstance != null) return sInstance;
            sInstance = new TraceUtils();
            return sInstance;
        }
    }

    /**
     * The thread timer object.
     */
    private final class ThreadLocalTimer extends ThreadLocal {
        public final Object initialValue() { return new TraceContainer(); }
        public final Trace getTrace(final String pName) { 
            final TraceContainer container = (TraceContainer)super.get();
            return container.get(pName);
        }
    }

    private final class TraceContainer {
        private final Map<String, Trace> mItems = new HashMap<String, Trace>();
        private TraceContainer() { }

        private final Trace get(final String pName) { 
            Trace trace = mItems.get(pName); 
            if (trace == null) {
                trace = new Trace();
                mItems.put(pName, trace);
            }
            return trace; 
        }

        private final void put(final String pName, final Trace pTrace) 
        { mItems.put(pName, pTrace); }
    }

    /**
     * The trace object.
     */
    private final class Trace {
        private String name;
        private Log log;
        private int level;
        private long warnTime;
        private long errorTime;
        private long startTime;
        private Trace() { }
        private final void reset() 
        { name=null;log=null;level=0;warnTime=0L;errorTime=0L;startTime=0L; }
    }

}

