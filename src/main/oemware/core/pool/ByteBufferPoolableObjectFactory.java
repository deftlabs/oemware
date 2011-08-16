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
import org.apache.commons.pool.PoolableObjectFactory;

// Java
import java.nio.ByteBuffer;

/**
 * The byte buffer object factory.
 *
 * @author Ryan Nitz
 * @version $Id: ByteBufferPoolableObjectFactory.java 13 2008-06-15 19:43:04Z oemware $
 */
public class ByteBufferPoolableObjectFactory implements PoolableObjectFactory {

    private final int mBufferSize;

    private final boolean mUseDirect;

    /**
     * Construct the object and set the size of the buffer.
     * @param pBufferSize The buffer size in bytes.
     */
    public ByteBufferPoolableObjectFactory(final int pBufferSize) { 
        this(pBufferSize, false); 
    }

    /**
     * Construct the object and set the size of the buffer.
     * @param pBufferSize The buffer size in bytes.
     * @param pUseDirect Set to true to use direct memory.
     */
    public ByteBufferPoolableObjectFactory( final int pBufferSize, 
                                            final boolean pUseDirect) 
    {
        mBufferSize = pBufferSize;
        mUseDirect = pUseDirect;
    }

    // The PoolableObjectFactory methods.
    public final Object makeObject() {
        if (mUseDirect) return ByteBuffer.allocateDirect(mBufferSize);
        return ByteBuffer.allocate(mBufferSize);
    }

    public final boolean validateObject(Object pObject) {
        ((ByteBuffer)pObject).clear();
        return true;
    }

    public final void activateObject(Object pObject) {
        ((ByteBuffer)pObject).clear();
    }

    public final void destroyObject(Object pObject) {
        pObject = null; 
    }

    public final void passivateObject(Object pObject) { }
}

