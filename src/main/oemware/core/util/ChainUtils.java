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
import oemware.core.CoreException;

// Jakarta Commons
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.config.ConfigParser;
import org.apache.commons.chain.impl.CatalogFactoryBase;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.chain.Chain;

// Java
import java.util.Iterator;

/**
 * The chain utils.
 *
 * @author Ryan Nitz
 * @version $Id: ChainUtils.java 13 2008-06-15 19:43:04Z oemware $
 */
public final class ChainUtils {

    /**
     * Returns the standard context object for the system.
     * @return A new context object.
     */
    public static final Context createContext() { return new ContextBase(); }

    /**
     * Execute a command name in a chain.
     * @param pCatalog The catalog.
     * @param pChainName The chain name.
     * @return The context object.
     * @throws CoreException
     */
    public static final Context executeChain(   final Catalog pCatalog, 
                                                final String pChainName)
        throws CoreException
    {
        final Context context = createContext();
        executeChain(pCatalog, pChainName, context);
        return context;
    }

    /**
     * Execute a command name in a chain.
     * @param pCatalog The catalog.
     * @param pChainName The chain name.
     * @param pContext The context object.
     * @throws CoreException
     */
    public static final void executeChain(  final Catalog pCatalog, 
                                            final String pChainName,
                                            final Context pContext)
        throws CoreException
    { executeChain(getChain(pCatalog, pChainName), pContext); }

    /**
     * Execute a chain with a passed context.
     * @param pChain The chain
     * @param pContext The context object.
     * @throws CoreException
     */
    public static final void executeChain(  final Chain pChain, 
                                            final Context pContext)
        throws CoreException
    {
        try { pChain.execute(pContext);
        } catch (Throwable t) { throw new CoreException(t); }
    }

    /**
     * Execute a chain with a new empty context object.
     * @param pChain The chain
     * @return The context object.
     * @throws CoreException
     */
    public static final Context executeChain(final Chain pChain)
                                    
        throws CoreException
    {
        try { 
            final Context context = createContext();
            pChain.execute(context);
            return context;
        } catch (Throwable t) { throw new CoreException(t); }
    }

    /**
     * Returns a chain from a catalog.
     * @param pCatalog The catalog.
     * @param pChainName The chain name.
     * @return The chain. Exception if not found.
     * @throws CoreException
     */
    public static final Chain getChain( final Catalog pCatalog, 
                                        final String pChainName) 
        throws CoreException 
    {
        final Chain chain 
            = (Chain)pCatalog.getCommand(pChainName);

        if (chain == null) {
            throw new CoreException("chain not found: " 
                                    + pChainName 
                                    + " - available chains: [" 
                                    + chainsAvailable(pCatalog)
                                    + "]");
        }

        return chain;
    }

    /**
     * Returns a string of available chains in a catalog. Comma delimited.
     * @param pCatalog The catalog
     * @return The string.
     */
    @SuppressWarnings(value="unchecked")
    public static final String chainsAvailable(final Catalog pCatalog) {
        final StringBuilder value = new StringBuilder();
        for (Iterator<String> i = pCatalog.getNames(); i.hasNext();) {
            value.append(i.next());
            if (i.hasNext()) value.append(", ");
        }
        return value.toString();
    }

    /**
     * Load a catalog.
     * @param pResourceName The catalog config file name (must be in the 
     * classpath e.g., /oemware-lifecycle.xml).
     * @param pCatalogName The name of the catalog.
     * @return The catalog. Throws an exception if it's not found.
     * @throws CoreException
     */
    public final static Catalog loadCatalog(    final String pResourceName, 
                                                final String pCatalogName ) 
        throws CoreException 
    {
        try {
            final ConfigParser parser = new ConfigParser();
            parser.parse(ClasspathUtils.loadResourceUrl(pResourceName));
            return CatalogFactoryBase.getInstance().getCatalog(pCatalogName);
        } catch (Throwable t) { throw new CoreException(t); }
    }
}

