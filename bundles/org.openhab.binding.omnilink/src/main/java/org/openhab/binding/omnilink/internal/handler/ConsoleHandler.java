/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.omnilink.internal.handler;

import static org.openhab.binding.omnilink.internal.OmnilinkBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ConsoleHandler} defines some methods that are used to
 * interface with an OmniLink Console. This by extension also defines the
 * Console thing that openHAB will be able to pick up and interface with.
 *
 * @author Craig Hamilton - Initial contribution
 * @author Ethan Dye - openHAB3 rewrite
 */
@NonNullByDefault
public class ConsoleHandler extends AbstractOmnilinkHandler {
    private final Logger logger = LoggerFactory.getLogger(ConsoleHandler.class);
    private final int thingID = getThingNumber();

    public ConsoleHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        if (getOmnilinkBridgeHandler() != null) {
            updateStatus(ThingStatus.ONLINE);
            updateChannels();
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR,
                    "Received null bridge while initializing Console!");
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("handleCommand called for channel: {}, command: {}", channelUID, command);

        if (command instanceof RefreshType) {
            updateChannels();
            return;
        }

        switch (channelUID.getId()) {
            case CHANNEL_CONSOLE_ENABLE_DISABLE_BEEPER:
                if (command instanceof StringType) {
                    sendOmnilinkCommand(OmniLinkCmd.CMD_CONSOLE_ENABLE_DISABLE_BEEPER.getNumber(),
                            ((StringType) command).equals(StringType.valueOf("OFF")) ? 0 : 1, thingID);
                } else {
                    logger.debug("Invalid command: {}, must be StringType", command);
                }
                break;
            case CHANNEL_CONSOLE_BEEP:
                if (command instanceof DecimalType) {
                    sendOmnilinkCommand(OmniLinkCmd.CMD_CONSOLE_BEEP.getNumber(), ((DecimalType) command).intValue(),
                            thingID);
                } else {
                    logger.debug("Invalid command: {}, must be DecimalType", command);
                }
                break;
            default:
                logger.warn("Unknown channel for Console thing: {}", channelUID);
        }
        updateChannels();
    }

    public void updateChannels() {
        updateState(CHANNEL_CONSOLE_ENABLE_DISABLE_BEEPER, UnDefType.UNDEF);
        updateState(CHANNEL_CONSOLE_BEEP, UnDefType.UNDEF);
    }
}