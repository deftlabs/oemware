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

// Java 
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * The time utils.  
 *
 * @author Ryan Nitz
 * @version $Id: TimeUtils.java 13 2008-06-15 19:43:04Z oemware $
 */
public final class TimeUtils {

    public static final int RECORD_INTERVAL = 300; 

    /**
     * Returns the epoch id for the start/stop time. This will
     * take the epoch in which this spent the most time. This
     * is a little fuzzy but it works well over time.
     * @param pStartTime The start time.
     * @param pStopTime The stop time.
     * @return The epoch id.
     */
    public static final int epochId(final int pStartTime, 
                                    final int pStopTime) 
    {
        final int startRemainder = (pStartTime % RECORD_INTERVAL);
        final int stopRemainder = (pStopTime % RECORD_INTERVAL);

        if (startRemainder == 0) return epochId(pStartTime);
        if (stopRemainder == 0) return epochId(pStartTime);

        // If the remainder is the same the tie always goes to the 
        // lower :-D
        if (startRemainder == stopRemainder) {
            return epochId((pStartTime - startRemainder));
        }

        // Get the epoch ids.
        final int startEpochId = epochId((pStartTime - startRemainder));
        final int stopEpochId = epochId((pStopTime - stopRemainder));
        
        if (startEpochId == stopEpochId) return startEpochId;

        // We're now dealing with different epochs.
        if (stopEpochId < startEpochId) {
            throw new IllegalStateException("stop time is less than start.");
        }

        // Check the remainder.
        if (stopRemainder > startRemainder) return startEpochId;
        return stopEpochId;
    }

    /**
     * Returns the current start time (based on epoch).
     * @return The currenht start time (based on epoch).
     */
    public static final int currentEpochTime() {
        final int currentTime = currentTimeSecs();
        return (currentTime - (currentTime % RECORD_INTERVAL));
    }

    /**
     * Returns the current epoch id.
     * @return The current epoch id.
     */
    public static final int currentEpochId() {
        return epochId(currentEpochTime());
    }

    /**
     * Returns the time for the epoch id passed. Assumes a valid 300 second 
     * epoch.
     * @param pEpochId The epoch id.
     * @return The time.
     */
    public static final int epochTime(final int pEpochId) {
        if (pEpochId < 1) {
            throw new IllegalArgumentException("bad epoch: " + pEpochId);
        }
        return (pEpochId * RECORD_INTERVAL);    
    }

    /**
     * Returns the time period id based on the start and stop time.
     * @param pTime The time the event started (in seconds).
     * @return The period id. The period id devides the day into
     * five minute intervals that are unique since THE epoch. 
     * time in secs / 300 = X (epoch: January 1, 1970 00:00:00.000 GMT).
     */
    public static final int epochId(final int pTime) {
        if (pTime < 1) throw new IllegalArgumentException("bad time: " + pTime);
        return (pTime / RECORD_INTERVAL);    
    }

    /**
     * Returns the current time in seconds.
     * @return The time in seconds.
     */
    public static final int currentTimeSecs() {
        return currentTimeSecs(System.currentTimeMillis());
    }

    /**
     * Returns the time in seconds for the value passed.
     * @param pTimeMillis The millis time.
     * @return The time in seconds.
     */
    public static final int currentTimeSecs(final long pTimeMillis) {
        return (int)(pTimeMillis / 1000L);
    }

    /**
     * Returns the time in ms - converted from seconds.
     * @param pTime The time in seconds.
     * @return The time in ms.
     */
    public static final long secsToMillis(final int pTime) {
        if (pTime < 1) throw new IllegalArgumentException("bad time: " + pTime);
        return (((long)pTime) * 1000L);
    }

    /**
     * Convert the passed date to milliseconds. This method creates a new 
     * date format object for each parse. This method shouldn't be used in 
     * performance critical sections.<br/><br/>
     * Formats Supported: <br/>
     * Thu, 03 Jan 2008 14:29:00 GMT<br/>
     * Sunday, 06-Nov-94 08:49:37 GMT<br/>
     * Sun Nov 6 08:49:37 1994<br/>
     * @param pDate The raw RFC 822/1123 time.
     * @return The time or -1 if the format is invalid.
     */
    public static final long convertHttpDate(final String pDate) {
        if (pDate == null) return -1L;
        // Thu, 03 Jan 2008 14:29:00 GMT
        DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        try {
            return (formatter.parse(pDate)).getTime();
        } catch (ParseException pe) { 
            formatter = new SimpleDateFormat("EEEE, dd-MM-yy HH:mm:ss zzz");
            try { return (formatter.parse(pDate)).getTime();
            } catch (ParseException pe1) { 
                formatter = new SimpleDateFormat("EEE MM d HH:mm:ss yyyy");
                try { return (formatter.parse(pDate)).getTime();
                } catch (ParseException pe2) {  return -1L; }
            }
        }
    }
}

