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
 *  The single thread base clase. Handles the thread loop. If the sleep time
 * is set above zero, the thread sleeps after each execution.
 *
 * @author Ryan Nitz
 * @version $Id: SingleThread.java 13 2008-06-15 19:43:04Z oemware $
 */
public abstract class SingleThread extends ThreadBase {

    private long mSleepTime = 0L;
    private boolean mDeductExecuteTime = false;
    private final Log mSingleThreadLogger=LogFactory.getLog(SingleThread.class);

    /**
     * Continuously call the "execute" method. All that is throwable is caught
     * and logged but the interrupted exception breaks the loop.
     */
    public void run() {

        long startTime = 0L;
        long executeTime = 0L;

        while (mRunning) {
            try {
                // Execute the job.
                try { 

                    if (mDeductExecuteTime) {
                        startTime = System.currentTimeMillis();                    
                    }
                    
                    execute();

                } catch (Throwable t) {
                    mSingleThreadLogger.error(  "thread name: " 
                                                + getName() 
                                                + " - " 
                                                + t.getMessage(), 
                                                t);
                }

                if (mSleepTime > 0L) {
                    if (!mDeductExecuteTime) { sleep(mSleepTime);
                    } else {
                        executeTime = System.currentTimeMillis() - startTime;
                        if (executeTime < mSleepTime) {
                            sleep(mSleepTime - executeTime);
                        }
                    }
                }

            } catch (InterruptedException ie) { break;
            } catch (Throwable t) {
                mSingleThreadLogger.error(  "thread name: " 
                                            + getName() 
                                            + " - " 
                                            + t.getMessage(), 
                                            t);
            }
        }
    }

    /**
     * Implement this method.
     */
    public abstract void execute();

    public final void setSleepTime(final long pV) { mSleepTime = pV; }
    public final long getSleepTime() { return mSleepTime; }

    public final void setDeductExecuteTime(final boolean pV) { mDeductExecuteTime = pV; }
    public final boolean getDeductExecuteTime() { return mDeductExecuteTime; }
}

