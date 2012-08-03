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
import java.util.concurrent.locks.ReentrantLock;

/**
 * An lru linked hash map. Access to this map is thread-safe. This class also
 * supports an optional eviction handler. To handle old data that is removed
 * from the map.
 */
public class LruMap<K,V> implements Map<K, V> {

    private final LinkedHashMap<K,V> _map;
    private final LruMap.EvictionHandler<K, V> _handler;
    private final int _size;
    private final ReentrantLock _lock = new ReentrantLock(true);

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

    @Override public void clear() {
        try {
            _lock.lock();
            _map.clear();
        } finally { _lock.unlock(); }
    }

    @Override public int size() {
        try {
            _lock.lock();
            return _map.size();
        } finally { _lock.unlock(); }
    }

    @Override public Collection<V> values() {
        try {
            _lock.lock();
            return _map.values();
        } finally { _lock.unlock(); }
    }

    @Override public int hashCode() {
        try {
            _lock.lock();
            return _map.hashCode();
        } finally { _lock.unlock(); }
    }

    @Override public V get (final Object pKey) {
        try {
            _lock.lock();
            return _map.get(pKey);
        } finally { _lock.unlock(); }
    }

    @Override public boolean containsKey(final Object pKey) {
        try {
            _lock.lock();
            return _map.containsKey(pKey);
        } finally { _lock.unlock(); }
    }

    @Override public boolean containsValue(final Object pValue) {
        try {
            _lock.lock();
            return _map.containsValue(pValue);
        } finally { _lock.unlock(); }
    }

    @Override public Set<Map.Entry<K,V>> entrySet() {
        try {
            _lock.lock();
            return _map.entrySet();
        } finally { _lock.unlock(); }
    }

    @Override public boolean isEmpty() {
        try {
            _lock.lock();
            return _map.isEmpty();
        } finally { _lock.unlock(); }
    }

    @Override public Set<K> keySet() {
        try {
            _lock.lock();
            return _map.keySet();
        } finally { _lock.unlock(); }
    }

    @Override public V put (K pKey, V pValue) {
        try {
            _lock.lock();
            return _map.put(pKey, pValue);
        } finally { _lock.unlock(); }
    }

    @Override public V remove(final Object pKey) {
        try {
            _lock.lock();
            return _map.remove(pKey);
        } finally { _lock.unlock(); }
    }

    @Override public void putAll(Map <? extends K, ? extends V> pValues) {
        try {
            _lock.lock();
            _map.putAll(pValues);
        } finally { _lock.unlock(); }
    }

    @Override public boolean equals(final Object pValue) {
        try {
            _lock.lock();
            return _map.equals(pValue);
        } finally { _lock.unlock(); }
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

