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

// OEMware
import oemware.core.ServiceManager;
import oemware.core.CoreException;

// Jakarta Commons
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The system utils. 
 *
 * @author Ryan Nitz
 * @version $Id: SystemUtils.java 59 2008-09-16 02:45:18Z oemware $
 */
public final class SystemUtils {

    private volatile static DaemonThread sDaemonThread = null;
    private volatile static boolean sShutdownHookAdded = false;

    /**
     * Start the daemon thread.
     * @throws CoreException
     */
    public static synchronized void startDaemonThread() throws CoreException {
        if (sDaemonThread == null) return;
        sDaemonThread = new DaemonThread();
        sDaemonThread.start();
    }

    /**
     * Stop the daemon thread.
     * @throws CoreException
     */
    public static synchronized void stopDaemonThread() throws CoreException {
        if (sDaemonThread == null) return;
        try { sDaemonThread.interrupt();
        } catch (Throwable t) { throw new CoreException(t); }
    }

    /**
     * Add the shutdown hook thread.
     * @throws CoreException
     */
    public static synchronized void addShutdownHookThread() 
        throws CoreException
    {
        if (sShutdownHookAdded) return;
        Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
        sShutdownHookAdded = true;
    }

    /**
     * Returns the property or throws an exception. All of these are required properties.
     * @param pParamName The param name - e.g., oemware.instance.id.
     * @return True or false.
     * @throws CoreException.
     */
    public static final boolean getSystemPropertyBool(final String pParamName) 
        throws CoreException 
    {
        final String systemProperty = getSystemProperty(pParamName);
        if (StringUtils.equalsIgnoreCase(systemProperty, "true")) return true;
        return false;
    }


    /**
     * Returns the property or exception. All of these are required properties.
     * @param pParamName The param name - e.g., oemware.instance.id.
     * @return The value. 
     * @throws CoreException.
     */
    public static final int getSystemPropertyInt(final String pParamName) 
        throws CoreException 
    {
        String systemProperty = null;
        try { 
            systemProperty = getSystemProperty(pParamName);
            return Integer.parseInt(systemProperty);
        } catch (NumberFormatException nfe) {
            String errorMessage 
                = "-D" 
                + pParamName 
                + "=" 
                + systemProperty 
                + " is not valid int";

            throw new CoreException(errorMessage);
        }
    }

    /**
     * Returns the property or exception. All of these are required properties.
     * @param pParamName The param name - e.g., oemware.instance.id.
     * @return The value. 
     * @throws CoreException.
     */
    public static final short getSystemPropertyShort(final String pParamName) 
        throws CoreException 
    {
        String systemProperty = null;
        try { 
            systemProperty = getSystemProperty(pParamName);
            return Short.parseShort(systemProperty);
        } catch (NumberFormatException nfe) {
            String errorMessage 
                = "-D" 
                + pParamName 
                + "=" 
                + systemProperty 
                + " is not valid int";

            throw new CoreException(errorMessage);
        }
    }

    /**
     * Returns the property or dies. All of these are required properties.
     * @param pParamName The param name - e.g., oemware.instance.id.
     * @return The value. 
     * @throws CoreException
     */
    public static final String getSystemProperty(final String pParamName) 
        throws CoreException 
    {
        final String systemProperty 
            = StringUtils.trim(System.getProperty(pParamName));
        
        if (!StringUtils.isBlank(systemProperty)) return systemProperty;

        String errorMessage 
            = "-D" + pParamName + "=X is not set - pass this to the jvm.";

        throw new CoreException(errorMessage);
    }

    /**
     * The shutdown hook thread. This is registered with the java runtime.
     *
     * @author Ryan Nitz
     * @version $Id: SystemUtils.java 59 2008-09-16 02:45:18Z oemware $
     */
    private static class ShutdownHookThread extends Thread {
        private final Log mLog = LogFactory.getLog(SystemUtils.class);
        private ShutdownHookThread() {
            super("oemware-core-shutdown-hook-thread");
            setPriority(MAX_PRIORITY);
        }

        public void run() {
            try { ServiceManager.getInstance().shutdown();
            } catch (Throwable t) { mLog.error(t.getMessage(), t); }
        }
    }

    /**
     * The daemon thread. This keeps everything alive and kicking.
     */
    private static class DaemonThread extends Thread {
        private static final long SLEEP_TIME = 1000L;
        private final Log mLog = LogFactory.getLog(SystemUtils.class);

        private DaemonThread() throws CoreException {
            super("oemware-core-daemon-thread");
            setPriority(MIN_PRIORITY);
        }

        public void run() {
            mLog.debug("daemon thread starting");
            while (true) {
                try {
                    if (ServiceManager.getInstance().isRunning()) {
                        try { sleep(SLEEP_TIME);  } catch (Exception e) { }
                    } else {
                        mLog.debug("daemon thread stopping"); return;
                    }
                } catch (Throwable t) { mLog.error(t.getMessage(), t); }
            }
        }
    }
}

