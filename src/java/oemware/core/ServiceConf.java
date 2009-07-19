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
 * The service config object. This wraps both the instance and the 
 * node conf together. The instance conf is checked first and if 
 * nothing is found, the node conf is checked.
 *
 * @author Ryan Nitz
 * @version $Id: ServiceConf.java 41 2008-07-20 21:06:18Z oemware $
 */
public final class ServiceConf {

    private final InstanceConf mNodeConf;
    private final InstanceConf mModuleConf;
    private final InstanceConf mInstanceConf;
    private final ServiceManager mServiceManager;

    /**
     * Construct a new instance. 
     * @param pNodeConf The global node conf.
     * @param pModuleConf The module conf.
     * @param pInstanceConf The instance conf.
     * @throws ServiceException
     */
    public ServiceConf( final InstanceConf pNodeConf,
                        final InstanceConf pModuleConf, 
                        final InstanceConf pInstanceConf) 
        throws ServiceException
    {
        mNodeConf = pNodeConf;
        mModuleConf = pModuleConf;
        mInstanceConf = pInstanceConf;
        mServiceManager = ServiceManager.getInstance();
    }

    public final InstanceConf getNodeConf() { return mModuleConf; }
    public final InstanceConf getInstanceConf() { return mInstanceConf; }

    /**
     * Returns the property
     * @param pName The property name (required).
     * @return The value. An exception is thrown if not available.
     * @throws ServiceException
     */
    public final String getProperty(final String pName) 
        throws ServiceException 
    {
        try { return mInstanceConf.getProperty(pName);
        } catch (ServiceException ise) {  
            try { return mModuleConf.getProperty(pName);
            } catch (ServiceException nse) {
                return mNodeConf.getProperty(pName);
            }
        }
    }

    /**
     * Returns the property
     * @param pName The property name (required).
     * @param pDefault Is returned if not found in config files.
     * @return The value. An exception is thrown if not available.
     * @throws ServiceException
     */
    public final String getProperty(final String pName, final String pDefault) 
        throws ServiceException 
    {
        try { return mInstanceConf.getProperty(pName);
        } catch (ServiceException ise) { 
            try { return mModuleConf.getProperty(pName); 
            } catch (ServiceException nse) {
                try { return mNodeConf.getProperty(pName);
                } catch (ServiceException ngse) { return pDefault; }
            }
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
        try { return mInstanceConf.getIntProperty(pName);
        } catch (ServiceException ise) { 
            try { return mModuleConf.getIntProperty(pName); 
            } catch (ServiceException nse) {
                return mNodeConf.getIntProperty(pName);
            }
        }
    }

    /**
     * Returns an integer property value (works same as string version).
     * @param pName The property name (required).
     * @param pDefault The default value to pass if not found in config file.
     * @return The value. An exception is thrown if not available.
     * @throws ServiceException
     */
    public final int getIntProperty(final String pName, final int pDefault) 
        throws ServiceException 
    {
        try { return mInstanceConf.getIntProperty(pName);
        } catch (ServiceException ise) { 
            try { return mModuleConf.getIntProperty(pName); 
            } catch (ServiceException nse) {
                try { return mNodeConf.getIntProperty(pName);
                } catch (ServiceException ngse) { return pDefault; }
            }
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
        try { return mInstanceConf.getShortProperty(pName);
        } catch (ServiceException ise) { 
            try { return mModuleConf.getShortProperty(pName);     
            } catch (ServiceException nse) {
                return mNodeConf.getShortProperty(pName);
            }
        }
    }

    /**
     * Returns a short property value (works same as string version).
     * @param pName The property name (required).
     * @param pDefault Is returned if not in config file.
     * @return The value. An exception is thrown if not available.
     * @throws ServiceException
     */
    public final short getShortProperty(final String pName, 
                                        final short pDefault)
        throws ServiceException 
    {
        try { return mInstanceConf.getShortProperty(pName);
        } catch (ServiceException ise) { 
            try { return mModuleConf.getShortProperty(pName); 
            } catch (ServiceException nse) {
                try { return mNodeConf.getShortProperty(pName);
                } catch (ServiceException ngse) { return pDefault; }
            }
        }
    }

    /**
     * Returns a long property value (works same as string version).
     * @param pName The property name (required).
     * @param pDefault Is returned if not in config file.
     * @return The value. An exception is thrown if not available.
     * @throws ServiceException
     */
    public final long getLongProperty(  final String pName, 
                                        final long pDefault)
        throws ServiceException 
    {
        try { return mInstanceConf.getLongProperty(pName);
        } catch (ServiceException ise) { 
            try { return mModuleConf.getLongProperty(pName); 
            } catch (ServiceException nse) {
                try { return mNodeConf.getLongProperty(pName);
                } catch (ServiceException ngse) { return pDefault; }
            }
        }
    }

    public final String getServiceName() {
        return mServiceManager.getServiceName();
    }
    public final String getEnvName() { return mServiceManager.getEnvName(); }
    public final short getNodeId() { return mServiceManager.getNodeId(); }
    public final String getBaseDir() { return mServiceManager.getBaseDir(); }
    public final String getDataDir() { return mServiceManager.getDataDir(); }
    public final short getInstanceId(){return mServiceManager.getInstanceId();}
}

