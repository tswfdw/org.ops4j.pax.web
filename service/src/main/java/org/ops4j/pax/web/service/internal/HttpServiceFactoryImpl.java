/*  Copyright 2007 Alin Dreghiciu.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

import javax.servlet.ServletException;

public class HttpServiceFactoryImpl implements ServiceFactory
{

    private static final Log m_logger = LogFactory.getLog( HttpServiceFactoryImpl.class );

    public Object getService(Bundle bundle, ServiceRegistration serviceRegistration)
    {
        if( m_logger.isInfoEnabled() )
        {
            m_logger.info( "Binding a new HttpService to bundle " + bundle );
        }
        try {
            HttpServiceImpl httpService = new HttpServiceImpl( bundle );
            httpService.start();
            return httpService;
        } catch (Exception e) {
            // TODO what to do in this case, chack out the specs
            m_logger.error( "unxpected", e );
            return null;
        }
    }

    public void ungetService(Bundle bundle, ServiceRegistration serviceRegistration, Object object) {
        // TODO do something?
    }
}
