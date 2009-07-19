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

package oemware.core.web;

// OEMware
import oemware.core.ServiceConf;
import oemware.core.ServiceException;

// Tomcat
import org.apache.catalina.startup.Embedded;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;

// Spring
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

// Jakarta Commons
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Java
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The embedded tomcat service. This is a servlet engine/web server
 * in a component.
 * 
 * @author Ryan Nitz
 * @version $Id: TomcatService.java 86 2008-09-27 17:22:45Z oemware $
 */
public final class TomcatService implements ApplicationListener {

    private String mHostname;
    private final String mEngineName;
    private final String mJvmRouteId;
    private final String mCatalinaHome;
    private final String mAppName;
    private final String mEmbeddedName;
    private final String mWebContextPath;
    private final int mHttpPort;

    private Embedded mTomcat;
    private Connector mHttpConnector;
    private Context mTomcatContext;
    private Host mTomcatHost;
    private Engine mTomcatEngine;

    private static final String TOMCAT_PROTOCOL 
        = "org.apache.coyote.http11.Http11NioProtocol";

    private final Log mLog = LogFactory.getLog(TomcatService.class);

    private final ServiceConf mServiceConf;  

    /**
     * Create a new tomcat service object.
     * @param pServiceConf The service conf object.
     * @param pServiceName The service name.
     * @param pHostname The hostname.
     * @param pEngineName The engine name.
     * @param pAppName The application name.
     * @param pEmbeddedName The name of the embedded tomcat service.
     * @param pWebContextPath The location on the url e.g. /foo.
     */
    public TomcatService(   final ServiceConf pServiceConf, 
                            final String pServiceName,
                            final String pHostname, 
                            final String pEngineName,
                            final String pAppName,
                            final String pEmbeddedName,
                            final String pWebContextPath)
     
        throws ServiceException 
    {
        mServiceConf = pServiceConf;
        mHostname = pHostname;
        mEngineName = pEngineName;
        mAppName = pAppName;
        mEmbeddedName = pEmbeddedName;
        mWebContextPath = pWebContextPath;
        
        mHttpPort = mServiceConf.getIntProperty("port_tomcat_http");
        
        // Load the jvm route id. This must be unique for each instance
        // of tomcat running in a cluster. 
        mJvmRouteId = StringUtils.strip(mServiceConf.getProperty("jvm_route_id", "instance1"));

        if (StringUtils.isEmpty(mHostname)) {
            mHostname 
            = StringUtils.strip(mServiceConf.getProperty("tomcat_host_name", ""));
        }

        // This location must be unique for each instance.
        String home = mServiceConf.getProperty("catalina_home", "");
        if (StringUtils.isEmpty(home)) {
            home = mServiceConf.getBaseDir() + "/" + pServiceName;
        }

        if (!home.endsWith(File.separator)) {
            home += File.separator;
        }
        mCatalinaHome = home;
        home = null;

        mLog.debug("jvm route id: '" + mJvmRouteId + "'");
        mLog.debug("catalina home: '" + mCatalinaHome + "'");
        mLog.debug("tomcat http port: '" + mHttpPort + "'");
    }

    /**
     * The stpring application event listener interface.
     * @param pEvent The context event.
     */
    public void onApplicationEvent(final ApplicationEvent pEvent) {
        if (pEvent == null) return;
        if (!(pEvent instanceof ContextRefreshedEvent)) return;
        ContextRefreshedEvent event = (ContextRefreshedEvent)pEvent;

        try { startTomcat();
        } catch (ServiceException se) {
            mLog.error(se.getMessage(), se);
        // We don't know how spring handles exceptions. Check the spring source
        // :-)
        } catch (Throwable t) { mLog.error(t.getMessage(), t); }
    }

    /**
     * Start tomcat.
     * @throws ServiceException
     */
    public synchronized void startup() throws ServiceException {

        // Initialize and start tomcat.
        mTomcat = new Embedded();

        mTomcat.setName(mEmbeddedName); 

        mTomcat.setCatalinaHome(mCatalinaHome);

        // The init order can't change.
        initHost();
        initContext();
        initEngine(); 
        initHttpConnector();

        // Add the engine to the embedded variable.
        mTomcat.addEngine(mTomcatEngine);

        mTomcat.addConnector(mHttpConnector);
    }

    private final void initHttpConnector() throws ServiceException {
        mHttpConnector 
        = mTomcat.createConnector((InetAddress)null, mHttpPort, TOMCAT_PROTOCOL);

        mHttpConnector.setEnableLookups(false);
        mHttpConnector.setSecure(false);
        mHttpConnector.setXpoweredBy(false);
        mHttpConnector.setScheme("http");

        mHttpConnector.setAttribute("minSpareThreads", "5");
        mHttpConnector.setAttribute("maxSpareThreads", "75");
        mHttpConnector.setAttribute("disableUploadTimeout", "true");
        mHttpConnector.setAttribute("acceptCount", "100");
        mHttpConnector.setAttribute("maxThreads", "200");
    }

    private final void initEngine() {
        mTomcatEngine = mTomcat.createEngine();
        mTomcatEngine.setName(mEngineName);
        mTomcatEngine.setJvmRoute(mJvmRouteId);
        mTomcatEngine.addChild(mTomcatHost);
        mTomcatEngine.setDefaultHost(mTomcatHost.getName());
    }

    private final void initContext() {
        
        final String contextPath 
            = (mCatalinaHome + mAppName + mWebContextPath);
        
        mTomcatContext 
            = mTomcat.createContext(mWebContextPath, contextPath); 

        mTomcatContext.setReloadable(false);
        mTomcatContext.setXmlValidation(false);
        mTomcatContext.setTldValidation(false);
        mTomcatContext.setPrivileged(false);

        // Add context to host and the host to the engine.
        mTomcatHost.addChild(mTomcatContext);
    }

    /**
     * Initialize the host.
     * @throws ServiceException
     */
    private final void initHost() throws ServiceException {

        try {
            final String hostPath = (mCatalinaHome + mAppName);
            if (StringUtils.isBlank(mHostname)) {
                mHostname = InetAddress.getLocalHost().getHostAddress();
            }
            mTomcatHost = mTomcat.createHost(mHostname, hostPath);
        } catch (UnknownHostException uhe) { throw new ServiceException(uhe); }
    }

    /**
     * Start the tomcat engine.
     * @throws ServiceException
     */
    private synchronized void startTomcat() throws ServiceException {
        try {
            if (mHttpConnector != null) mHttpConnector.start();
            if (mTomcat != null) mTomcat.start();
        } catch (LifecycleException le) { throw new ServiceException(le); }
    }

    /**
     * Stop tomcat.
     * @throws ServiceException
     */
    public synchronized void shutdown() throws ServiceException {

        final long stopTime = System.currentTimeMillis();
        mLog.debug("----- stopping: tomcat -----");

        try {

            // Stop  the http connector.
            if (mHttpConnector != null) { 
                mHttpConnector.stop();
            }

            if (mTomcat != null) {
                mTomcat.stop();
                mTomcat.destroy();
                mTomcat = null;
            }

        } catch (LifecycleException le) { throw new ServiceException(le); }

        final long shutdownTime = (System.currentTimeMillis() - stopTime);
        mLog.debug("----- stopped: tomcat - " + shutdownTime + " (ms) -----");
    }
}

