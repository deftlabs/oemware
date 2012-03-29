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

package com.deftlabs.core.util;

// Java
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * An lru linked hash map. Access to this map is thread-safe. This class also
 * supports an optional eviction handler. To handle old data that is removed
 * from the map.
 */
public class LruMap<K,V> implements Map<K, V> {

    private final LinkedHashMap<K,V> _map;
    private final LruMap.EvictionHandler<K, V> _handler;
    private final int _size;

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
        _size = pSize;
        _handler = pHandler;

        final int capacity = (int)Math.ceil(_size / LOAD_FACTOR) + 1;

        _map = new LinkedHashMap<K,V>(capacity, LOAD_FACTOR, true) {
            private static final long serialVersionUID = 1L;
            @Override protected boolean removeEldestEntry(Map.Entry<K,V> pEldest) {
                final boolean remove = (size() > LruMap.this._size);
                if (!remove) return remove;

                if (_handler == null) return remove;

                LruMap.this._handler.execute(pEldest);

                return remove;
            }
        };
    }

    public final synchronized void clear() { _map.clear(); }
    public final synchronized int size() { return _map.size(); }
    public final synchronized Collection<V> values() { return _map.values(); }
    public final synchronized int hashCode() { return _map.hashCode(); }

    public final synchronized V get (final Object pKey) {
        return _map.get(pKey);
    }

    public final synchronized boolean containsKey(final Object pKey) {
        return _map.containsKey(pKey);
    }

    public final synchronized boolean containsValue(final Object pValue) {
        return _map.containsValue(pValue);
    }

    public final synchronized Set<Map.Entry<K,V>> entrySet() {
        return _map.entrySet();
    }

    public final synchronized boolean isEmpty() { return _map.isEmpty(); }

    public final synchronized Set<K> keySet() { return _map.keySet(); }

    public final synchronized V put (K pKey, V pValue) {
        return _map.put(pKey, pValue);
    }

    public final synchronized V remove(final Object pKey) {
        return _map.remove(pKey);
    }

    public final synchronized void putAll(Map   <? extends K,
                                                ? extends V> pValues)
    { _map.putAll(pValues); }

    public final synchronized boolean equals(final Object pValue) {
         return _map.equals(pValue);
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

