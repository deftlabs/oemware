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
import oemware.core.util.ClasspathUtils;
import oemware.core.util.FileUtils;
import oemware.core.CoreException;

// Jakarta Commons 
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Java
import java.io.File;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The instance configuration. This component can be registered with
 * a shared job runner if you want it to refresh. If a user attempts
 * get a property that doesn't exist, an exception is thrown. This 
 * component pulls the data from a file.
 * <br/><br/>
 *
 * This component is to be used for instance configuration and should 
 * not be used for storing rapidly changing application states. 
 * <br/><br/>
 *
 * @author Ryan Nitz
 * @version $Id: InstanceConf.java 13 2008-06-15 19:43:04Z oemware $
 */
public final class InstanceConf extends BaseSharedJob {

    /**
     * The system property name/key is passed into the  JVM
     * at startup. E.g. -Doemware.instance.conf=/home/oemware/conf/instance1.xml
     * The file that this key points to is used to populate the attributes
     * here.
     */
    private String mSystemPropertyName;
    private String mFileName;

    private final Properties mInstanceProperties = new Properties();
    private final ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();
    private final AtomicLong mFileLastModified = new AtomicLong();
    private final Log mLog = LogFactory.getLog(InstanceConf.class);

    public static final String NODE_ID_KEY = "oemware.node.id";
    public static final String INSTANCE_ID_KEY = "oemware.instance.id";
    public static final String SERVICE_NAME_KEY = "oemware.service.name";
    public static final String ENV_NAME_KEY = "oemware.env.name";
    public static final String DIR_DATA_KEY = "oemware.dir.data";

    /**
     * Construct a new instance. This is mainly used for unit tests.
     */
    public InstanceConf() { }

    /**
     * Construct a new instance. You must pass the system property in the
     * constructor.
     * @param pSystemPropertyName The system property name (-Dfoo=value).
     */
    public InstanceConf(final String pSystemPropertyName) {
        mSystemPropertyName = pSystemPropertyName;

        if (StringUtils.isBlank(pSystemPropertyName)) {
            mLog.warn("system property name isn't set");
            mFileName = null;
            return;
        }

        // Get the file name from the system property.
        mFileName = System.getProperty(mSystemPropertyName);
        if (StringUtils.isBlank(mFileName)) {
            mLog.error("file name not set: " + mSystemPropertyName);
            return;
        }

        readProperties();
    }

    /**
     * This is the method called by the shared job runner.
     */
    public final void runJob() { readProperties(); }

    /**
     * Returns the property
     * @param pName The property name (required).
     * @return The value. An exception is thrown if not available.
     * @throws ServiceException
     */
    public final String getProperty(final String pName) 
        throws ServiceException 
    {
        if (StringUtils.isBlank(pName)) {
            throw new ServiceException("property name not set: " + pName);
        }

        mLock.readLock().lock();
        try {
            final String value = mInstanceProperties.getProperty(pName);
            if (StringUtils.isBlank(value)) {
                throw new ServiceException("property not set: " + pName);
            }

            return value;
        } finally { mLock.readLock().unlock(); }
    }

    /**
     * Returns a long property value (works same as string version).
     * @param pName The property name (required).
     * @return The value. An exception is thrown if not available.
     * @throws ServiceException
     */
    public final long getLongProperty(final String pName) 
        throws ServiceException 
    {
        final String value = getProperty(pName);
        try { return Long.parseLong(StringUtils.trim(value));
        } catch (NumberFormatException nfe) {
            throw new ServiceException( "key: " 
                                        + pName 
                                        + " value: '" 
                                        + value + "'",
                                        nfe);
        }
    }

    /**
     * Returns an integer property value (works same as string version).
     * @param pName The property name (required).
     * @return The value. An exception is thrown if not available.
     * @throws ServiceException
     */
    public final int getIntProperty(final String pName) 
        throws ServiceException 
    {
        final String value = getProperty(pName);
        try { return Integer.parseInt(StringUtils.trim(value));
        } catch (NumberFormatException nfe) {
            throw new ServiceException( "key: " 
                                        + pName 
                                        + " value: '" 
                                        + value + "'",
                                        nfe);
        }
    }

    /**
     * Returns a short property value (works same as string version).
     * @param pName The property name (required).
     * @return The value. An exception is thrown if not available.
     * @throws ServiceException
     */
    public final short getShortProperty(final String pName) 
        throws ServiceException 
    {
        final String value = getProperty(pName);
        try { return Short.parseShort(StringUtils.trim(value));
        } catch (NumberFormatException nfe) {
            throw new ServiceException( "key: " 
                                        + pName 
                                        + " value: '" 
                                        + value + "'",
                                        nfe);
        }
    }

    /**
     * Set the property.
     * @param pName The property name.
     * @param pValue The property value.
     */
    public final void setProperty(final String pName, final String pValue) 
        throws ServiceException
    {
        if (StringUtils.isBlank(pName)) {
            throw new ServiceException("property name not set - " + pValue);
        } else if (StringUtils.isBlank(pValue)) {
            throw new ServiceException("property value not set: " + pName);
        }

        mLock.writeLock().lock();
        try {
            mInstanceProperties.setProperty(pName, pValue);
            
            // Store to xml.
            storeProperties();

        } finally { mLock.writeLock().unlock(); }
    }

    /**
     * Read the properties.
     */
    private final void readProperties() {
        if (StringUtils.isBlank(mFileName)) return;
        File file = null;
        mLock.writeLock().lock();
        try {
            file = new File(mFileName);

            // Check the last modified time to see if there
            // is a need to read the file.
            final long lastModifiedTime = FileUtils.lastModifiedTime(file);
           
            if (mFileLastModified.get() == lastModifiedTime) {
                if (mLog.isDebugEnabled()) {
                    mLog.debug(  "file unchanged - not reading: " + mFileName);
                }
                return;
            }

            if (mLog.isDebugEnabled()) {
                mLog.debug(  "file chnaged - reading: " + mFileName);
            }

            mFileLastModified.set(lastModifiedTime);

            // Load the xml properties.
            FileUtils.loadProperties(file, mInstanceProperties);

        } catch (CoreException ce) { mLog.error(ce.getMessage(), ce); 
        } catch (Throwable t) { mLog.error(t.getMessage(), t);
        } finally { mLock.writeLock().unlock(); file = null; }
    }

    /**
     * Store the properties. This should be in a write lock.
     */
    private final void storeProperties() {
        if (StringUtils.isBlank(mFileName)) return;

        try {
            FileUtils.storeXmlProperties(mFileName, mInstanceProperties);
        } catch (CoreException ce) { mLog.error(ce.getMessage(), ce);
        } catch (Throwable t) { mLog.error(t.getMessage(), t); }
    }
}

