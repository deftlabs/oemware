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
import java.util.List;

/**
 * The shared job runner is a single thread that is used amongst 
 * a group of low priority jobs. The jobs registered
 * should take less than a second to execute; additionally,
 * the jobs should be relatively infrequent (i.e., five seconds
 * or greater). The execution of the job is also not exact since
 * they're executed synchronously. The thread executes a check
 * once per mSleepTime (ms) to see if it should run any jobs.
 * <br/><br/>
 * All jobs are run on startup.
 *
 * Set the mMaxTimeBeforeError attribute to log an error for
 * long running jobs.
 *
 * <br/><br/>
 * The purpose of this component is to reduce the number of 
 * monitor threads running in the instance (consuming resources).
 * <br/><br/>
 * You can create multiple job runner components in an instance.
 * 
 * @author Ryan Nitz
 * @version $Id: SharedJobRunner.java 13 2008-06-15 19:43:04Z oemware $
 */
public final class SharedJobRunner extends SingleThread {

    private List<SharedJob> mJobs;
    private long mMaxTimeBeforeError = 0L;
    private final Log mLogger = LogFactory.getLog(SharedJobRunner.class);

    public final void execute() {
        if (mJobs == null) return;

        try {

            long startTime = 0;
            long executionTime = 0;
            long currentTime = 0;

            for (SharedJob sharedJob : mJobs) {
                startTime = System.currentTimeMillis();

                try {

                    // Check to see if we should run this job.
                    if ((startTime - sharedJob.getLastRunTime()) 
                        >= sharedJob.getJobFrequency()) 
                    {
                        // Debug.
                        if (mLogger.isDebugEnabled()) {
                            mLogger.debug(  "running job: " 
                                            + sharedJob.getJobName());
                        }

                        // Execute the job.
                        sharedJob.runJob();

                        currentTime = System.currentTimeMillis();
                        executionTime = currentTime - startTime;
                        
                        sharedJob.setLastRunTime(currentTime);
                       
                       // Log an error if the job took too long to run.
                        if ((mMaxTimeBeforeError > 0) 
                            && (executionTime > mMaxTimeBeforeError)) 
                        {
                            mLogger.error(  "job: " 
                                            + sharedJob.getJobName() 
                                            + " - ran too long: " 
                                            + executionTime 
                                            + " (ms) - max is: " 
                                            + mMaxTimeBeforeError 
                                            + " (ms)"); 
                        }

                        // Debug.
                        if (mLogger.isDebugEnabled()) {
                            mLogger.debug(  "ran job: " 
                                            + sharedJob.getJobName()
                                            + " - "
                                            + executionTime 
                                            + " (ms)");
                        }
                    }

                } catch (Throwable t) {
                    mLogger.error(  "error running job: " 
                                    + sharedJob.getJobName(), 
                                    t);
                }
            }

        } catch (Throwable t) { mLogger.error(t.getMessage(), t); }
    }

    public final void setJobs(List<SharedJob> pJobs) { mJobs = pJobs; }
    public final List<SharedJob> getJobs() { return mJobs; }
    public final void setMaxTimeBeforeError(final long  pMaxTimeBeforeError) {
        mMaxTimeBeforeError = pMaxTimeBeforeError;
    }
    public final long getMaxTimeBeforeError() { return mMaxTimeBeforeError; }
}

