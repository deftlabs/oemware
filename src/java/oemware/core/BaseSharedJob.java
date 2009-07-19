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
 * The base shared job. This implements the base get/set methods.
 *
 * @author Ryan Nitz
 * @version $Id: BaseSharedJob.java 13 2008-06-15 19:43:04Z oemware $
 */
public abstract class BaseSharedJob implements SharedJob {

    private String mJobName;
    private long mJobFrequency = 10000L;
    private long mLastRunTime;

    /**
     * You must implement this method in your class.
     */
    public abstract void runJob(); 

    public final String getJobName() { return mJobName; }
    public final void setJobName(final String pJobName) {
        mJobName = pJobName;
    }

    public final long getJobFrequency() { return mJobFrequency; }
    public final void setJobFrequency(final long pJobFrequency) {
        mJobFrequency = pJobFrequency;
    }

    public long getLastRunTime() { return mLastRunTime; }
    public void setLastRunTime(final long pLastRunTime) {
        mLastRunTime = pLastRunTime;
    }
}

