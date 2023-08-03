//
// ========================================================================
// Copyright (c) 1995 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License v. 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jetty.util.component.Dumpable;

/**
 * @deprecated use {@link Attributes.Mapped}
 */
@Deprecated
public class AttributesMap implements Attributes, Dumpable
{
    private final AtomicReference<ConcurrentMap<String, Object>> _map = new AtomicReference<>();

    public AttributesMap()
    {
    }

    public AttributesMap(AttributesMap attributes)
    {
        ConcurrentMap<String, Object> map = attributes.map();
        if (map != null)
            _map.set(new ConcurrentHashMap<>(map));
    }

    private ConcurrentMap<String, Object> map()
    {
        return _map.get();
    }

    private ConcurrentMap<String, Object> ensureMap()
    {
        while (true)
        {
            ConcurrentMap<String, Object> map = map();
            if (map != null)
                return map;
            map = new ConcurrentHashMap<>();
            if (_map.compareAndSet(null, map))
                return map;
        }
    }

    @Override
    public Object removeAttribute(String name)
    {
        Map<String, Object> map = map();
        return map == null ? null : map.remove(name);
    }

    @Override
    public Object setAttribute(String name, Object attribute)
    {
        if (attribute == null)
            return removeAttribute(name);
        return ensureMap().put(name, attribute);
    }

    @Override
    public Object getAttribute(String name)
    {
        Map<String, Object> map = map();
        return map == null ? null : map.get(name);
    }

    @Override
    public Set<String> getAttributeNameSet()
    {
        return Collections.unmodifiableSet(keySet());
    }

    public Set<Map.Entry<String, Object>> getAttributeEntrySet()
    {
        Map<String, Object> map = map();
        return map == null ? Collections.<Map.Entry<String, Object>>emptySet() : map.entrySet();
    }

    public static Enumeration<String> getAttributeNamesCopy(Attributes attrs)
    {
        if (attrs instanceof AttributesMap)
            return Collections.enumeration(((AttributesMap)attrs).keySet());

        List<String> names = new ArrayList<>(attrs.getAttributeNameSet());
        return Collections.enumeration(names);
    }

    public static Set<String> getAttributeNameSetCopy(Attributes attrs)
    {
        if (attrs instanceof AttributesMap)
            return ((AttributesMap)attrs).keySet();

        List<String> names = new ArrayList<>(attrs.getAttributeNameSet());
        return new HashSet<>(names);
    }

    @Override
    public void clearAttributes()
    {
        Map<String, Object> map = map();
        if (map != null)
            map.clear();
    }

    public int size()
    {
        Map<String, Object> map = map();
        return map == null ? 0 : map.size();
    }

    @Override
    public String toString()
    {
        Map<String, Object> map = map();
        return map == null ? "{}" : map.toString();
    }

    private Set<String> keySet()
    {
        Map<String, Object> map = map();
        return map == null ? Collections.emptySet() : map.keySet();
    }

    public void addAll(Attributes attributes)
    {
        for (String name : attributes.getAttributeNameSet())
            setAttribute(name, attributes.getAttribute(name));
    }

    @Override
    public String dump()
    {
        return Dumpable.dump(this);
    }

    @Override
    public void dump(Appendable out, String indent) throws IOException
    {
        Dumpable.dumpObjects(out, indent, String.format("%s@%x", this.getClass().getSimpleName(), hashCode()), map());
    }
}