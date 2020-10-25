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
package net.java.sip.communicator.impl.netaddr;

import net.java.sip.communicator.service.netaddr.*;
import net.java.sip.communicator.util.*;

import net.java.sip.communicator.util.osgi.ServiceUtils;
import org.jitsi.service.configuration.*;
import org.jitsi.service.packetlogging.*;
import org.osgi.framework.*;

/**
 * The activator manage the the bundles between OSGi framework and the
 * Network address manager
 *
 * @author Emil Ivov
 */
public class NetaddrActivator
    extends AbstractServiceDependentActivator<ConfigurationService>
{
    /**
     * The logger for this class.
     */
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NetworkAddressManagerServiceImpl.class);

    /**
     * The OSGi bundle context.
     */
    private static BundleContext        bundleContext         = null;

    /**
     * The network address manager implementation.
     */
    private NetworkAddressManagerServiceImpl networkAMS = null;

    /**
     * The configuration service.
     */
    private static ConfigurationService configurationService = null;

    /**
     * The OSGi <tt>PacketLoggingService</tt> in
     * {@link #bundleContext} and used for debugging.
     */
    private static PacketLoggingService packetLoggingService  = null;

    @Override
    public void start(ConfigurationService dependentService) throws Exception
    {
        configurationService = dependentService;
        //in here we load static properties that should be else where
        //System.setProperty("java.net.preferIPv4Stack", "false");
        //System.setProperty("java.net.preferIPv6Addresses", "true");
        //end ugly property set

        //Create and start the network address manager.
        networkAMS =
            new NetworkAddressManagerServiceImpl();

        // give references to the NetworkAddressManager implementation
        networkAMS.start();

        if (logger.isInfoEnabled())
            logger.info("Network Address Manager         ...[  STARTED ]");

        bundleContext.registerService(
            NetworkAddressManagerService.class.getName(), networkAMS, null);

        logger.info("Network Address Manager Service ...[REGISTERED]");
    }

    @Override
    public Class<ConfigurationService> getDependentServiceClass()
    {
        return ConfigurationService.class;
    }

    @Override
    public void setBundleContext(BundleContext context)
    {
        bundleContext = context;
    }

    /**
     * Returns a reference to a ConfigurationService implementation currently
     * registered in the bundle context or null if no such implementation was
     * found.
     *
     * @return a currently valid implementation of the ConfigurationService.
     */
    public static ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    /**
     * Returns a reference to the <tt>PacketLoggingService</tt> implementation
     * currently registered in the bundle context or null if no such
     * implementation was found.
     *
     * @return a reference to a <tt>PacketLoggingService</tt> implementation
     * currently registered in the bundle context or null if no such
     * implementation was found.
     */
    public static PacketLoggingService getPacketLogging()
    {
        if (packetLoggingService == null)
        {
            packetLoggingService
                = ServiceUtils.getService(
                        bundleContext,
                        PacketLoggingService.class);
        }
        return packetLoggingService;
    }

    /**
     * Stops the Network Address Manager bundle
     *
     * @param bundleContext  the OSGI bundle context
     *
     */
    public void stop(BundleContext bundleContext)
    {
        if(networkAMS != null)
            networkAMS.stop();
        if (logger.isInfoEnabled())
            logger.info("Network Address Manager Service ...[STOPPED]");

        configurationService = null;
        packetLoggingService = null;
    }

    /**
     * Returns a reference to the bundle context that we were started with.
     *
     * @return a reference to the BundleContext instance that we were started
     * with.
     */
    static BundleContext getBundleContext()
    {
        return bundleContext;
    }
}
