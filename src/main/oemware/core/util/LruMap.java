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

// Java
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * An lru linked hash map. Access to this map is thread-safe. This class also
 * supports an optional eviction handler. To handle old data that is removed 
 * from the map. 
 *
 * @author Ryan Nitz
 * @version $Id: LruMap.java 13 2008-06-15 19:43:04Z oemware $
 */
public class LruMap<K,V> implements Map<K, V> {

    private final LinkedHashMap<K,V> mMap;
    private final LruMap.EvictionHandler<K, V> mHandler;
    private final int mSize;
    
    private static final float LOAD_FACTOR = 0.75f;

    /**
     * Create a new lru map.
     * @param pSize The max size of the map.
     */
    public LruMap(final int pSize) { this(pSize, null); }

    /**
     * Create a new lru linked hash map.
     * @param pSize The max size of the map.
     * @param pHandler The optional eviction handler.
     */
    public LruMap(  final int pSize, 
                    final LruMap.EvictionHandler<K, V> pHandler) 
    {
        mSize = pSize;
        mHandler = pHandler;

        final int capacity = (int)Math.ceil(mSize / LOAD_FACTOR) + 1;

        mMap = new LinkedHashMap<K,V>(capacity, LOAD_FACTOR, true) {
            private static final long serialVersionUID = 1L;
            @Override protected boolean removeEldestEntry(Map.Entry<K,V> pEldest) {
                final boolean remove = (size() > LruMap.this.mSize); 
                if (!remove) return remove;

                if (mHandler == null) return remove;
                
                LruMap.this.mHandler.execute(pEldest);

                return remove;
            }
        }; 
    }
    
    public final synchronized void clear() { mMap.clear(); }
    public final synchronized int size() { return mMap.size(); }
    public final synchronized Collection<V> values() { return mMap.values(); }
    public final synchronized int hashCode() { return mMap.hashCode(); }

    public final synchronized V get (final Object pKey) { 
        return mMap.get(pKey); 
    }

    public final synchronized boolean containsKey(final Object pKey) { 
        return mMap.containsKey(pKey);
    }

    public final synchronized boolean containsValue(final Object pValue) { 
        return mMap.containsValue(pValue);
    }

    public final synchronized Set<Map.Entry<K,V>> entrySet() {
        return mMap.entrySet();
    }

    public final synchronized boolean isEmpty() { return mMap.isEmpty(); }

    public final synchronized Set<K> keySet() { return mMap.keySet(); }

    public final synchronized V put (K pKey, V pValue) { 
        return mMap.put(pKey, pValue); 
    }

    public final synchronized V remove(final Object pKey) {
        return mMap.remove(pKey);
    }

    public final synchronized void putAll(Map   <? extends K, 
                                                ? extends V> pValues) 
    { mMap.putAll(pValues); }
    
    public final synchronized boolean equals(final Object pValue) { 
         return mMap.equals(pValue); 
    }

    /**
     * The eviction handler interface. Implement this interface to work with
     * the removed entry.
     */
    public static interface EvictionHandler<K, V> {
        /**
         * The eldest entry removed from the lru.
         * @param pEldest The entry being removed.
         */
        public void execute(Map.Entry<K,V> pEldest); 
    }
}

