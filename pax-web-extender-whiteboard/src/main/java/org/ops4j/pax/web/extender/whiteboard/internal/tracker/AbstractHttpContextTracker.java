/*
 * Copyright 2007 Alin Dreghiciu.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.web.extender.whiteboard.internal.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.web.extender.whiteboard.HttpContextMapping;
import org.ops4j.pax.web.extender.whiteboard.internal.ExtenderContext;
import org.ops4j.pax.web.extender.whiteboard.internal.WebApplication;

/**
 * Tracks objects published as services via a Service Tracker.
 *
 * @author Alin Dreghiciu
 * @since 0.2.0, August 21, 2007
 */
abstract class AbstractHttpContextTracker<T>
    extends ServiceTracker
{

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger( AbstractTracker.class );
    /**
     * Extender context.
     */
    private final ExtenderContext m_extenderContext;

    /**
     * Constructor.
     *
     * @param extenderContext extender context; cannot be null
     * @param bundleContext   extender bundle context; cannot be null
     * @param classes         array of class of the tracked objects; cannot be null
     */
    AbstractHttpContextTracker( final ExtenderContext extenderContext,
                                final BundleContext bundleContext,
                                final Class<? extends T>... classes )
    {
        super( validateBundleContext( bundleContext ), createFilter( bundleContext, classes ), null );
        NullArgumentException.validateNotNull( extenderContext, "Extender context" );
        m_extenderContext = extenderContext;
    }

    /**
     * Creates an OSGi filter for the classes.
     *
     * @param bundleContext a bundle context
     * @param classes       array of tracked classes
     *
     * @return osgi filter
     */
    private static Filter createFilter( final BundleContext bundleContext,
                                        final Class... classes )
    {
        final StringBuilder filter = new StringBuilder();
        if( classes != null )
        {
            if( classes.length > 1 )
            {
                filter.append( "(|" );
            }
            for( Class clazz : classes )
            {
                filter.append( "(" )
                    .append( Constants.OBJECTCLASS )
                    .append( "=" )
                    .append( clazz.getName() )
                    .append( ")" );
            }
            if( classes.length > 1 )
            {
                filter.append( ")" );
            }
        }
        try
        {
            return bundleContext.createFilter( filter.toString() );
        }
        catch( InvalidSyntaxException e )
        {
            throw new IllegalArgumentException( "Unexpected InvalidSyntaxException: " + e.getMessage() );
        }
    }

    /**
     * Validates that the bundle context is not null.
     * If null will throw IllegalArgumentException.
     *
     * @param bundleContext a bundle context
     *
     * @return the bundle context if not null
     */
    private static BundleContext validateBundleContext( final BundleContext bundleContext )
    {
        NullArgumentException.validateNotNull( bundleContext, "Bundle context" );
        return bundleContext;
    }

    /**
     * @see ServiceTracker#addingService(ServiceReference)
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public Object addingService( final ServiceReference serviceReference )
    {
        LOGGER.debug( "Service available " + serviceReference );
        T registered = (T) super.addingService( serviceReference );
        HttpContextMapping mapping = createHttpContextMapping( serviceReference, registered );
        if( mapping != null )
        {
            final WebApplication webApplication = m_extenderContext.getWebApplication(
                serviceReference.getBundle(),
                mapping.getHttpContextId()
            );
            webApplication.setHttpContextMapping( mapping );
            return mapping;
        }
        else
        {
            // if no mapping was created release the service
            super.remove( serviceReference );
            return null;
        }
    }

    /**
     * @see ServiceTracker#removedService(ServiceReference,Object)
     */
    @Override
    public void removedService( final ServiceReference serviceReference, final Object unpublished )
    {
        LOGGER.debug( "Service removed " + serviceReference );
        super.removedService( serviceReference, unpublished );
        final HttpContextMapping mapping = (HttpContextMapping) unpublished;
        final WebApplication webApplication = m_extenderContext.getWebApplication(
            serviceReference.getBundle(),
            mapping.getHttpContextId()
        );
        webApplication.setHttpContextMapping( null );
    }

    /**
     * Factory method for http context mapping.
     *
     * @param serviceReference service reference for published service
     * @param published        the actual published service
     *
     * @return an Registration if could be created or applicable or null if not
     */
    abstract HttpContextMapping createHttpContextMapping( final ServiceReference serviceReference,
                                                          final T published );

}
