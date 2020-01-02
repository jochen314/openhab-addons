/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.binding.mqtt.homeassistant.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.mqtt.generic.values.OnOffValue;
import org.openhab.binding.mqtt.generic.values.TextValue;

/**
 * A MQTT Fan component, following the https://www.home-assistant.io/components/fan.mqtt/ specification.
 *
 * @author David Graeff - Initial contribution
 */
@NonNullByDefault
public class ComponentFan extends AbstractComponent<ComponentFan.ChannelConfiguration> {
    public static final String stateChannelID = "state"; // Randomly chosen channel "ID"
    public static final String oscillationChannelID = "oscillation"; // Randomly chosen channel "ID"
    public static final String speedChannelID = "speed"; // Randomly chosen channel "ID"

    /**
     * Configuration class for MQTT component
     */
    static class ChannelConfiguration extends BaseChannelConfiguration {
        ChannelConfiguration() {
            super("MQTT Fan");
        }

        protected @Nullable Boolean optimistic;

        protected @Nullable String state_topic;
        protected String command_topic = "";
        protected String payload_on = "ON";
        protected String payload_off = "OFF";

        protected @Nullable String oscillation_state_topic;
        protected @Nullable String oscillation_command_topic;
        protected @Nullable String oscillation_value_template;
        protected String payload_oscillation_on = "oscillate_on";
        protected String payload_oscillation_off = "oscillate_off";

        protected @Nullable String speed_state_topic;
        protected @Nullable String speed_command_topic;
        protected @Nullable String speed_value_template;
        protected @Nullable List<String> speeds;
        protected String payload_off_speed = "off";
        protected String payload_low_speed = "low";
        protected String payload_medium_speed = "medium";
        protected String payload_high_speed = "high";

        protected @Nullable String json_attributes_topic;
        protected @Nullable String json_attributes_template;
    }

    public ComponentFan(CFactory.ComponentConfiguration componentConfiguration) {
        super(componentConfiguration, ChannelConfiguration.class);

        {
            // state
            OnOffValue value = new OnOffValue(channelConfiguration.payload_on, channelConfiguration.payload_off);
            buildChannel(stateChannelID, value, "state", componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.state_topic, channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.command_topic, channelConfiguration.retain,
                            channelConfiguration.qos)//
                    .build();
        }

        if (channelConfiguration.oscillation_state_topic != null
                || channelConfiguration.oscillation_command_topic != null) {
            // oscillation
            OnOffValue value = new OnOffValue(channelConfiguration.payload_oscillation_on,
                    channelConfiguration.payload_oscillation_off);
            buildChannel(oscillationChannelID, value, "oscillation", componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.oscillation_state_topic,
                            channelConfiguration.oscillation_value_template)//
                    .commandTopic(channelConfiguration.oscillation_command_topic, channelConfiguration.retain,
                            channelConfiguration.qos)//
                    .build();
        }

        Collection<String> configSpeeds = channelConfiguration.speeds;

        if ((channelConfiguration.speed_state_topic != null || channelConfiguration.speed_command_topic != null)
                && (configSpeeds != null && !configSpeeds.isEmpty())) {
            // speed

            Collection<String> speeds = new ArrayList<>();
            if (configSpeeds.contains("off")) {
                speeds.add(channelConfiguration.payload_off_speed);
            }
            if (configSpeeds.contains("low")) {
                speeds.add(channelConfiguration.payload_low_speed);
            }
            if (configSpeeds.contains("medium")) {
                speeds.add(channelConfiguration.payload_medium_speed);
            }
            if (configSpeeds.contains("high")) {
                speeds.add(channelConfiguration.payload_high_speed);
            }

            TextValue value = new TextValue(speeds.toArray(new String[speeds.size()]));

            buildChannel(speedChannelID, value, "speed", componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.speed_state_topic, channelConfiguration.speed_value_template)//
                    .commandTopic(channelConfiguration.speed_command_topic, channelConfiguration.retain,
                            channelConfiguration.qos)//
                    .build();
        }
    }
}
