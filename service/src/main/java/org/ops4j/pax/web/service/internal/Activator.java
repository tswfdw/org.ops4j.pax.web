/*  Copyright 2007 Niclas Hedhman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.web.service.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;

import java.util.Dictionary;
import java.util.Hashtable;

public class Activator
    implements BundleActivator
{
    private ServiceRegistration m_serviceRegistration;

    public void start( BundleContext bundleContext )
        throws Exception
    {
        Dictionary properties = new Hashtable();
        m_serviceRegistration = bundleContext.registerService(
                HttpService.class.getName(),
                new HttpServiceFactoryImpl(), 
                properties );
    }

    public void stop( BundleContext bundleContext )
        throws Exception
    {
        m_serviceRegistration.unregister();
    }
}
