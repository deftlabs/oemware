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

package oemware.core.chain;

// OEMware
import oemware.core.util.ChainUtils;
import oemware.core.CoreException;
import oemware.core.ServiceException;

// Jakarta Commons
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Java
import java.util.Map;
import java.util.HashMap;

/**
 * The chain manager. This handles a group of chains and can execute them 
 * on demand.
 * 
 * @author Ryan Nitz
 * @version $Id: ChainManager.java 13 2008-06-15 19:43:04Z oemware $
 */
public final class ChainManager {

    private final String mName;
    private final Map<String, Catalog> mCatalogs 
        = new HashMap<String, Catalog>();
    private final static Log sLogger = LogFactory.getLog(ChainManager.class);

    /**
     * Execute a chain.
     * @param pCatalogName The catalog name.
     * @param pChainName The chain name.
     * @throws ServiceException
     */
    public final Context executeChain(  final String pCatalogName, 
                                        final String pChainName) 
        throws ServiceException
    {
        final Context context = ChainUtils.createContext();
        executeChain(pCatalogName, pChainName, context);
        return context;
    }

    /**
     * Execute a chain.
     * @param pCatalogName The catalog name.
     * @param pChainName The chain name.
     * @param pContext The context object.
     * @throws ServiceException
     */
    public final void executeChain( final String pCatalogName, 
                                    final String pChainName,
                                    final Context pContext) 
        throws ServiceException
    {
        try {
            final Catalog catalog = mCatalogs.get(pChainName);
            if (catalog == null) {
                throw new ServiceException( "catalog not found: " 
                                            + pCatalogName);
            } 

            ChainUtils.executeChain(catalog, pChainName, pContext);
        } catch (CoreException ce) { throw new ServiceException(ce); }
    }

    /**
     * Construct a new object with the catalogs passed.
     * @param pName The name of this chain.
     * @param pCatalogs The string mapping to the resource/file name.
     * @throws ServiceException
     */
    public ChainManager(final String pName, 
                        final Map<String, String> pCatalogs) 
        throws ServiceException
    {
        mName = pName;

        // Load the catalogs.
        for (String catalogName: pCatalogs.keySet()) {
            try {
                final String resourceName = pCatalogs.get(catalogName);
                final Catalog catalog 
                    = ChainUtils.loadCatalog(resourceName, catalogName);

                mCatalogs.put(catalogName, catalog);
            } catch (CoreException ce) { throw new ServiceException(ce); }
        }
    }
}

