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
import java.util.LinkedList;
import java.util.Iterator;

/**
 * A simple lru list. Access to this list is NOT thread-safe.
 */
public class LruList<E> {

    private final int _maxSize;
    private final LinkedList<E> _list = new LinkedList<E>();

    /**
     * Create a new lru linked hash map.
     * @param pMaxSize The max size of the list.
     */
    public LruList(final int pMaxSize) { _maxSize = pMaxSize; }

    /**
     * Use this method for the lru.
     * @param e The element.
     */
    public final void put(final E e) {
        if (_list.size() >= _maxSize) _list.removeFirst();
        _list.add(e);
    }

    public boolean remove(final Object o) { return _list.remove(o); }
    public boolean contains(final Object o) { return _list.contains(o); }
    public Iterator<E> entries() { return _list.iterator(); }
    public int size() { return _list.size(); }
    public void clear() { _list.clear(); }
}

