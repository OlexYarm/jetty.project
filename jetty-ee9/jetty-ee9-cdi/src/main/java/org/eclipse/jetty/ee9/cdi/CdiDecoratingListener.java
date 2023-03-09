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

package org.eclipse.jetty.ee9.cdi;

import org.eclipse.jetty.ee9.servlet.DecoratingListener;
import org.eclipse.jetty.ee9.servlet.ServletContextHandler;

/**
 * A DecoratingListener that listens for "org.eclipse.jetty.ee9.cdi.decorator"
 */
public class CdiDecoratingListener extends DecoratingListener
{
    public static final String MODE = "CdiDecoratingListener";
    public static final String ATTRIBUTE = "org.eclipse.jetty.ee9.cdi.decorator";

    public CdiDecoratingListener(ServletContextHandler contextHandler)
    {
        super(contextHandler, ATTRIBUTE);
        contextHandler.setAttribute(CdiServletContainerInitializer.CDI_INTEGRATION_ATTRIBUTE, MODE);
    }
}