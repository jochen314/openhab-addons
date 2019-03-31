/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
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
package org.openhab.binding.mqtt.internal.homeassistant;

import java.math.BigDecimal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.mqtt.values.PercentageValue;
import org.openhab.binding.mqtt.values.RollershutterValue;
import org.openhab.binding.mqtt.values.Value;

/**
 * A MQTT Cover component, following the https://www.home-assistant.io/components/cover.mqtt/ specification.
 *
 * Only Open/Close/Stop works so far.
 *
 * @author David Graeff - Initial contribution
 * @author Jochen Klein
 */
@NonNullByDefault
public class ComponentCover extends AbstractComponent<ComponentCover.ChannelConfiguration> {
    public static final String coverChannelID = "cover"; // Randomly chosen channel "ID"
    public static final String posChannelID = "position"; // Randomly chosen channel "ID"
    public static final String tiltChannelID = "tilt"; // Randomly chosen channel "ID"

    /**
     * Configuration class for MQTT component
     */
    static class ChannelConfiguration extends BaseChannelConfiguration {
        ChannelConfiguration() {
            super("MQTT Cover");
        }

        protected boolean optimistic = false;

        protected @Nullable String state_topic;
        protected String payload_open = "OPEN";
        protected String payload_close = "CLOSE";
        protected String payload_stop = "STOP";

        protected @Nullable String command_topic;

        protected @Nullable String position_topic;
        protected int position_open = 100;
        protected int position_close = 0;

        protected @Nullable String set_position_topic;
        protected @Nullable String set_position_template;

        protected @Nullable String tilt_command_topic;
        protected @Nullable String tilt_status_topic;
        protected int tilt_min = 0;
        protected int tilt_max = 100;
        protected int tilt_closed_value = 0;
        protected int tilt_opened_value = 100;
        protected boolean tilt_status_optimistic = false;
        protected boolean tilt_invert_state = false;
    }

    public ComponentCover(CFactory.ComponentConfiguration componentConfiguration) {
        super(componentConfiguration, ChannelConfiguration.class);

        if (channelConfiguration.position_topic != null) {
            Value value = new PercentageValue(BigDecimal.valueOf(channelConfiguration.position_close),
                    BigDecimal.valueOf(channelConfiguration.position_open), null, null, null);

            // TODO: set_position_template not supported

            buildChannel(posChannelID, value, "Position").listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.position_topic, channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.set_position_topic, channelConfiguration.retain)//
                    .build();

        } else if (channelConfiguration.state_topic != null) {
            Value value = new RollershutterValue(channelConfiguration.payload_open, channelConfiguration.payload_close,
                    channelConfiguration.payload_stop);

            buildChannel(coverChannelID, value, "State").listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.state_topic, channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.command_topic, channelConfiguration.retain)//
                    .build();
        }

        if (channelConfiguration.tilt_command_topic != null || channelConfiguration.tilt_status_topic != null) {
            Value value;

            if (channelConfiguration.tilt_invert_state) {
                value = new PercentageValue(BigDecimal.valueOf(channelConfiguration.tilt_max),
                        BigDecimal.valueOf(channelConfiguration.tilt_min), null,
                        "" + channelConfiguration.tilt_opened_value, "" + channelConfiguration.tilt_closed_value);
            } else {
                value = new PercentageValue(BigDecimal.valueOf(channelConfiguration.tilt_min),
                        BigDecimal.valueOf(channelConfiguration.tilt_max), null,
                        "" + channelConfiguration.tilt_opened_value, "" + channelConfiguration.tilt_closed_value);
            }

            buildChannel(tiltChannelID, value, "Tilt").listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.tilt_status_topic, channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.tilt_command_topic, channelConfiguration.retain)//
                    .build();
        }
    }
}
