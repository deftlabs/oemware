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

package oemware.core.pool;

// Jakarta Commons
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import static org.apache.commons.pool.impl.GenericObjectPool.WHEN_EXHAUSTED_BLOCK;

/**
 * The object pool manager. Extends commons generic
 * object pool.
 *
 * @author Ryan Nitz
 * @version $Id: ObjectPool.java 13 2008-06-15 19:43:04Z oemware $
 */
public class ObjectPool extends GenericObjectPool {

    private final String mName;

    /**
     * Create a new object and set some of the properties. This constructor 
     * blocks empty requests until max wait has been reached. It doesn't run
     * any tests.
     * @param pName The object pool name.
     * @param pObjectFactory The object factory.
     * @param pMaxActive The maximum number of items in the pool.
     * @param pMaxIdle The max number of idle objects.
     * @param pMaxWait The max wait in ms.
     * GenericObjectPool).
     */
    public ObjectPool(  final String pName, 
                        final PoolableObjectFactory pObjectFactory, 
                        final int pMaxActive, 
                        final int pMaxIdle, 
                        final long pMaxWait)
    {
        super(pObjectFactory, pMaxActive);

        // Configure the pool.
        setMaxIdle(pMaxIdle);
        setMaxWait(pMaxWait);
        setTestOnBorrow(false);
        setTestOnReturn(false);
        setTestWhileIdle(false);
        setWhenExhaustedAction(WHEN_EXHAUSTED_BLOCK);

        mName = pName;
    }

    /**
     * Create a new object and set some of the properties.
     * @param pName The object pool name.
     * @param pObjectFactory The object factory.
     * @param pMaxActive The maximum number of items in the pool.
     * @param pMaxIdle The max number of idle objects.
     * @param pMaxWait The max wait in ms.
     * @param pTestOnBorrow The test on borrow flag.
     * @param pTestOnReturn The test on return flag.
     * @param pTestWhileIdle The test while idle flag. 
     * @param pExhaustedAction The exhausted action (see commons
     * GenericObjectPool).
     */
    public ObjectPool(  final String pName, 
                        final PoolableObjectFactory pObjectFactory, 
                        final int pMaxActive, 
                        final int pMaxIdle, 
                        final long pMaxWait,
                        final boolean pTestOnBorrow,
                        final boolean pTestOnReturn,
                        final boolean pTestWhileIdle,
                        final byte pExhaustedAction) 
    {
        super(pObjectFactory, pMaxActive);

        // Configure the pool.
        setMaxIdle(pMaxIdle);
        setMaxWait(pMaxWait);
        setTestOnBorrow(pTestOnBorrow);
        setTestOnReturn(pTestOnReturn);
        setTestWhileIdle(pTestWhileIdle);
        setWhenExhaustedAction(pExhaustedAction);

        mName = pName;

    }
}

