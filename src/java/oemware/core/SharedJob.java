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

/**
 * The shared job interface. Implemented by all shared jobs.
 *
 * @author Ryan Nitz
 * @version $Id: SharedJob.java 13 2008-06-15 19:43:04Z oemware $
 */
public interface SharedJob {

    /**
     * Called when its time/turn is available.
     */
    public void runJob(); 

    /**
     * Get the job name.
     * @return The job name.
     */
    public String getJobName();

    /**
     * Get the frequency (in ms).
     * @return The frequency.
     */
    public long getJobFrequency();

    /**
     * Set the last run time.
     * @param pLastRunTime The last time the job was run.
     */
    public void setLastRunTime(final long pLastRunTime);

    /**
     * Returns the last time the job was run.
     * @return The last time the job was run.
     */
    public long getLastRunTime();
}

