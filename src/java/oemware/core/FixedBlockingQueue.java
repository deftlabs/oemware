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

// Java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The blocking fixed size queue (i.e., has a max size).
 *
 * @author Ryan Nitz
 * @version $Id$
 */
public final class FixedBlockingQueue {

    private volatile BlockingQueue<Object> mQueue; 

    /**
     * Construct a new queue.
     * @param pCapacity The max capacity.
     */
    public FixedBlockingQueue(final int pCapacity) { 
        mQueue = new LinkedBlockingQueue<Object>(pCapacity); 
    }

    /**
     * Take the object. Blocks if no object.
     * @return The new object.
     * @throws InterruptedException
     */
    public final Object take() throws InterruptedException
    { return mQueue.take(); }

    /**
     * Add an element.
     * @throws InterruptedException
     */
    public final void put(final Object pObj) throws InterruptedException 
    { mQueue.put(pObj); }

    /**
     * Returns the remaining capacity.
     * @return The remaining capacity.
     */
    public final int remainingCapacity() { return mQueue.remainingCapacity(); }
        
    /**
     * Stop the queue.
     */
    public final void shutdown() { }
    public final void startup() { }
}

