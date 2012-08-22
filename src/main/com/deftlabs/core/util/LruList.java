/**
 * (C) Copyright 2007, Deft Labs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

