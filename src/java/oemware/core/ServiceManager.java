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

// OEMware
import oemware.core.util.ChainUtils;
import oemware.core.util.SystemUtils;

// Jakarta Commons
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The service manager. This class is the base for all java applications. When 
 * a server/module is started/initialized, it is done so here. If a stop service
 * is registered, then it interacts with the service manager. There will only be
 * one instance of this singleton running. The service manager executes the 
 * start/stop chains registered.<br/><br/>
 *
 * The startup/shutdown is modular so you don't need to add application specific
 * initialization here.
 * 
 * The contract is to call the startup method when you begin and the shutdown 
 * method when you need to exit :-^
 *
 * @author Ryan Nitz
 * @version $Id: ServiceManager.java 60 2008-09-16 02:46:55Z oemware $
 */
public final class ServiceManager {

    private static ServiceManager sInstance = null;
    private final static Log sLog = LogFactory.getLog(ServiceManager.class);
    private AtomicLong mStartTime = new AtomicLong(0L);

    private static final String VERSION = "0.4";

    // The application run states.
    public static final int STATE_INITIALIZE = 1;
    public static final int STATE_STARTING = 2;
    public static final int STATE_RUNNING = 3;
    public static final int STATE_STOPPING = 4;
    public static final int STATE_STOPPED = 5;

    /**
     * The lifecycle config file location. This must point to a file with
     * two chains: 'startup' and 'shutdown'.
     */
    private static final String LIFECYCLE_FILE = "oemware-lifecycle.xml";

    /**
     * The context object that is used to hold items between startup
     * and shutdown.
     */
    private Context mServiceContext = null;

    /**
     * The run state. Let's others know where we are.
     */
    private static final AtomicInteger mRunState 
        = new AtomicInteger(STATE_STARTING);

    // The system properties passed in.
    private String mServiceName = null;
    private String mEnvName = null;
    private short mInstanceId = (short)0;
    private short mNodeId = 0;
    private String mBaseDir;
    private String mAppDir;
    private String mDataDir;
    private boolean mIsCordial = true;

    /**
     * The private constructor. This is a singleton. It reads
     * the system properties.
     * @throws ServiceException
     */
    private ServiceManager() throws ServiceException 
    { readSystemProperties(); if (mIsCordial) { Cordial.hey(); } }

    /**
     * The start hook. Calls the registered startup chain.
     * @throws ServiceException
     */
    public synchronized final void startup() throws ServiceException {
        // Get out of here if we're already running.
        if (isRunning()) return;
        
        mStartTime.set(System.currentTimeMillis());

        // Info.
        if (sLog.isInfoEnabled()) {
            sLog.info(  "----- starting service: "
                        + mServiceName
                        + " - env: "
                        + mEnvName
                        + " -----");
        }

        // Change the state to starting.
        mRunState.set(STATE_STARTING);

        // Init the manager.
        try {
            mServiceContext = createLifecycleContext();
            
            ChainUtils.executeChain(loadLifecycleCatalog(), 
                                    "startup", 
                                    mServiceContext);

            SystemUtils.startDaemonThread();
            SystemUtils.addShutdownHookThread();
        } catch (CoreException ce) { throw new ServiceException(ce); }

        // Set the run state to running.
        mRunState.set(STATE_RUNNING); 

        // Info.
        if (sLog.isInfoEnabled()) {
            sLog.info(  "----- started service:  "
                        + mServiceName
                        + " - env: "
                        + mEnvName
                        + " - start time: "
                        + (System.currentTimeMillis() - mStartTime.get())
                        + " (ms) -----"); 
        }
    }

    /**
     * Read the args needed to start.
     * @throws CoreException
     */
    private final void readSystemProperties() throws ServiceException {
        try {
            // Load the service name.
            mServiceName = SystemUtils.getSystemProperty("oemware.service.name");
            sLog.debug("oemware.service.name: " + mServiceName);

            // Load the environment name.
            mEnvName = SystemUtils.getSystemProperty("oemware.env.name");
            sLog.debug("oemware.env.name: " + mEnvName);

            // Load the base dir.
            mBaseDir = SystemUtils.getSystemProperty("oemware.dir");
            sLog.debug("oemware.dir: " + mBaseDir);

            // Load the app dir.
            mAppDir = SystemUtils.getSystemProperty("oemware.app.dir");
            sLog.debug("oemware.app.dir: " + mAppDir);

            // Load the node id.
            mNodeId = (short)SystemUtils.getSystemPropertyInt("oemware.node.id");
            sLog.debug("oemware.node.id: " + mNodeId);

            // Load the data dir.
            mDataDir = SystemUtils.getSystemProperty("oemware.data.dir");
            sLog.debug("oemware.data.dir: " + mDataDir);

            // Load the instance id.
            mInstanceId = SystemUtils.getSystemPropertyShort("oemware.instance.id");
            sLog.debug("oemware.instance.id: " + mInstanceId);

            // Check to see if the version announcement is enabled.
            try {
            if (SystemUtils.getSystemPropertyBool("oemware.cordial")) 
                mIsCordial = true;
            else mIsCordial = false;
            } catch (final Throwable t) { mIsCordial = true; }
            
        } catch (final Throwable t) { throw new ServiceException(t); }
    }

    /**
     * The stop hook. Calls the registered shutdown chain.
     * @throws ServiceException
     */
    public synchronized final void shutdown() throws ServiceException {
        if (!isRunning()) return;
        mRunState.set(STATE_STOPPING);
        final long shutdownStartTime = System.currentTimeMillis();

        // Info.
        if (sLog.isInfoEnabled()) {
            sLog.info(   "----- stopping service: "
                            + mServiceName
                            + " - env: "
                            + mEnvName
                            + " -----");
        }

        // Shutdown the service.
        try {
            if (mIsCordial) Cordial.seeYa();
            SystemUtils.stopDaemonThread();
            ChainUtils.executeChain(loadLifecycleCatalog(), 
                                    "shutdown", 
                                    mServiceContext);
        } catch (CoreException ce) { throw new ServiceException(ce); }
        mRunState.set(STATE_STOPPED);
        final long currentTime = System.currentTimeMillis();

        // Info.
        if (sLog.isInfoEnabled()) {
            sLog.info(  "----- stopped service:  "
                        + mServiceName
                        + " - env: "
                        + mEnvName
                        + " - stop time: "
                        + (currentTime - shutdownStartTime)
                        + " (ms) "
                        + " - service run time: "
                        + (currentTime - mStartTime.get())
                        + " (ms) -----");
        }
   }

    /**
     * The main method. Used to init from command line.
     * @param pArgs The command line arguments.
     */
    public static final void main(final String [] pArgs) {
        try { ServiceManager.getInstance().startup();
        } catch (Throwable t) { sLog.error(t.getMessage(), t); }
    }

    /**
     * Returns a context object for the start/stop chains.
     * @return A context for the start/stop chains.
     */
    @SuppressWarnings(value="unchecked")
    private final Context createLifecycleContext() {
        final Context context = ChainUtils.createContext();
        context.put("oemware.env.name", mEnvName);
        context.put("oemware.service.name", mServiceName);
        return context;
    }

    public final boolean isRunning() { 
        return (mRunState.get() == STATE_RUNNING);
    }

    public final boolean isStopping() {
        return (mRunState.get() == STATE_STOPPING);
    }

    public final String getServiceName() { return mServiceName; }
    public final String getEnvName() { return mEnvName; }
    public final short getNodeId() { return mNodeId; }
    public final String getAppDir() { return mAppDir; }
    public final String getBaseDir() { return mBaseDir; }
    public final String getDataDir() { return mDataDir; }
    public final short getInstanceId() { return mInstanceId; }
    public final int getRunState() { return mRunState.get(); }
    public final long getStartTime() { return mStartTime.get(); }

    public final long getUptime() {
        return System.currentTimeMillis() - mStartTime.get();
    }

    /**
     * Loads the startup/shutdown (lifecycle) catalog.
     * @return The lifecycle catalog. 
     * @throws ServiceException
     */
    private synchronized final Catalog loadLifecycleCatalog() 
        throws ServiceException 
    {
        try {
            return ChainUtils.loadCatalog(LIFECYCLE_FILE, "lifecycle");
        } catch (CoreException ce) { throw new ServiceException(ce); }
    }

    /**
     * Returns the (only) instance of this class. This methoed is syncronized so
     * cache the result.
     * @return The instance.
     * @throws ServiceException
     */
    public static final synchronized ServiceManager getInstance() 
        throws ServiceException
    {
        if (sInstance == null) sInstance = new ServiceManager();
        return sInstance;
    }

    /**
     * OEMware is interested in the adoption rate and versions 
     * in use to deal with maintenance of the code base. This is open source so
     * the maintenance is extremely optional but, we're human :-)
     * There is no relationship or tie to running this ping, nor is it required.
     */
    private static final class Cordial extends Thread {
        private volatile static Cordial mPulse = null;
        private synchronized final static void hey() {
            try {
                if (mPulse != null) return;
                mPulse = new Cordial();
                mPulse.setPriority(Thread.MIN_PRIORITY); 
                mPulse.start();
            } catch (final Throwable t) { }
        }
        private synchronized final static void seeYa() {
            if (mPulse == null) return;
            try{mPulse.interrupt(); mPulse = null;}catch(final Throwable t){}
        }
        public final void run() {
            long sleepTime = 60000;
            String id = null;
            while (true) {
                try {
                    mPulse.sleep(sleepTime);
                    if (id == null)
                    {id = java.net.URLEncoder.encode(java.util.UUID.randomUUID().toString(),"UTF-8");}
                    java.net.HttpURLConnection co = null;
                    try {
                        java.net.URLConnection c 
                        = (new java.net.URL("http://oemware.com:80/cordial/"
                        + java.net.URLEncoder.encode(VERSION, "UTF-8")
                        + "/" +id)).openConnection();
                        co = (java.net.HttpURLConnection)c;
                        co.setRequestMethod("GET");
                        co.getContentLength();
                        if (sleepTime < 3600000) sleepTime += 60000;
                    } finally { if (co != null) { co.disconnect(); } }
                } catch (final InterruptedException ie) { break;
                } catch (final Throwable t) 
                { try { mPulse.sleep(3600000); 
                } catch (final InterruptedException ie) { break;
                } catch (final Throwable ta) { } }
            }
        }
    }
}

