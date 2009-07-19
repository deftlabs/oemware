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
import org.apache.commons.logging.LogFactory;

/**
 * The log utils.  
 *
 * @author Ryan Nitz
 * @version $Id: LogUtils.java 13 2008-06-15 19:43:04Z oemware $
 */
public final class LogUtils {

    /**
     * Returns the log for the object.
     * @param pObject The value.
     * @return The log.
     */
    public static final Log getLog(final Object pObject) { 
        return LogFactory.getLog(pObject.getClass());
    }

    /**
     * Log the warning. This is a convienance method.
     * @param pLogger The log.
     * @param pComponentId The component id.
     * @param pMessage The message.
     */
    public static final void logWarn(   final Log pLogger,
                                        final String pComponentId, 
                                        final String pMessage) 
    {
        pLogger.warn(  "component: " 
                        + pComponentId 
                        + " - message: "
                        + pMessage); 
    }

    /**
     * Log the pipe excepiton. This is a convienance method.
     * @param pLogger The log.
     * @param pComponentId The component id.
     * @param pMessage The message.
     */
    public static final void logError(  final Log pLogger,
                                        final String pComponentId, 
                                        final String pMessage) 
    {
        pLogger.error(  "component: " 
                        + pComponentId 
                        + " - message: "
                        + pMessage); 
    }

    /**
     * Log the named pipe excepiton. This is a convienance method.
     * @param pLogger The log.
     * @param pComponentId The component id.
     * @param pThrowable The exception.
     */
    public static final void logError(  final Log pLogger,
                                        final String pComponentId, 
                                        final Throwable pThrowable) 
    {
        pLogger.error(  "component: " 
                        + pComponentId 
                        + " - source message: "
                        + pThrowable.getMessage(), 
                        pThrowable);
    }

    /**
     * Log the named pipe excepiton. This is a convienance method.
     * @param pLogger The log.
     * @param pComponentId The component id.
     * @param pThrowable The exception.
     * @param pMessage The message.
     */
    public static final void logError(  final Log pLogger,
                                        final String pComponentId,
                                        final String pMessage, 
                                        final Throwable pThrowable) 
    {
        pLogger.error(  "component: " 
                        + pComponentId 
                        + " - message: "
                        + pMessage
                        + " - source message: "
                        + pThrowable.getMessage(), 
                        pThrowable);
    }
}

