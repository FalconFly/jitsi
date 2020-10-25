/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Copyright @ 2015 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.java.sip.communicator.impl.contactlist;

import net.java.sip.communicator.service.contactlist.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.*;

import net.java.sip.communicator.util.osgi.ServiceUtils;
import org.jitsi.service.fileaccess.*;
import org.jitsi.service.resources.*;
import org.osgi.framework.*;

/**
 * @author Emil Ivov
 */
public class ContactlistActivator
    extends AbstractServiceDependentActivator<ResourceManagementService>
{
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ContactlistActivator.class);

    private MetaContactListServiceImpl mclServiceImpl  = null;

    private static AccountManager accountManager;

    private static BundleContext bundleContext;

    @Override
    public void start(ResourceManagementService res)
    {
        if (logger.isDebugEnabled())
            logger.debug("Service Impl: " + getClass().getName() + " [  STARTED ]");

        mclServiceImpl = new MetaContactListServiceImpl(res);

        //reg the icq account man.
        bundleContext.registerService(MetaContactListService.class.getName(),
            mclServiceImpl, null);

        mclServiceImpl.start(bundleContext);

        if (logger.isDebugEnabled())
            logger.debug("Service Impl: " + getClass().getName() + " [REGISTERED]");
    }

    @Override
    public Class<ResourceManagementService> getDependentServiceClass()
    {
        return ResourceManagementService.class;
    }

    @Override
    public void setBundleContext(BundleContext context)
    {
        bundleContext = context;
    }

    /**
     * Called when this bundle is stopped so the Framework can perform the
     * bundle-specific activities necessary to stop the bundle.
     *
     * @param context The execution context of the bundle being stopped.
     * @throws Exception If this method throws an exception, the bundle is
     *   still marked as stopped, and the Framework will remove the bundle's
     *   listeners, unregister all services registered by the bundle, and
     *   release all services used by the bundle.
     */
    public void stop(BundleContext context) throws Exception
    {
        if (logger.isTraceEnabled())
            logger.trace("Stopping the contact list.");
        if(mclServiceImpl != null)
            mclServiceImpl.stop(context);
    }

    /**
     * Returns the <tt>AccountManager</tt> obtained from the bundle context.
     * @return the <tt>AccountManager</tt> obtained from the bundle context
     */
    public static AccountManager getAccountManager()
    {
        if(accountManager == null)
        {
            accountManager
                = ServiceUtils.getService(bundleContext, AccountManager.class);
        }
        return accountManager;
    }
}
