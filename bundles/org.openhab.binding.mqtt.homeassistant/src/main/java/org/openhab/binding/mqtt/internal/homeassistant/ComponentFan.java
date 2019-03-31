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

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.mqtt.values.OnOffValue;
import org.openhab.binding.mqtt.values.TextValue;
import org.openhab.binding.mqtt.values.Value;

/**
 * A MQTT Fan component, following the https://www.home-assistant.io/components/fan.mqtt/ specification.
 *
 * Only ON/OFF is supported so far.
 *
 * @author David Graeff - Initial contribution
 * @author Jochen Klein
 */
@NonNullByDefault
public class ComponentFan extends AbstractComponent<ComponentFan.ChannelConfiguration> {
    public static final String fanChannelID = "fan"; // Randomly chosen channel "ID"
    public static final String oscChannelID = "osc"; // Randomly chosen channel "ID"
    public static final String speedChannelID = "speed"; // Randomly chosen channel "ID"

    /**
     * Configuration class for MQTT component
     */
    static class ChannelConfiguration extends BaseChannelConfiguration {
        ChannelConfiguration() {
            super("MQTT Fan");
        }

        protected boolean optimistic = false;

        protected @Nullable String state_value_template;

        protected @Nullable String state_topic;
        protected String payload_on = "ON";
        protected String payload_off = "OFF";

        protected String command_topic = "";

        protected @Nullable String oscillation_state_topic;
        protected @Nullable String oscillation_command_topic;
        protected String payload_oscillation_on = "oscillate_on";
        protected String payload_oscillation_off = "oscillate_off";
        protected @Nullable String oscillation_value_template;

        protected @Nullable String speed_state_topic;
        protected @Nullable String speed_command_topic;
        protected String payload_low_speed = "low";
        protected String payload_medium_speed = "medium";
        protected String payload_high_speed = "high";
        protected @Nullable String speed_value_template;

        protected @Nullable List<String> speeds;
    };

    public ComponentFan(CFactory.ComponentConfiguration componentConfiguration) {
        super(componentConfiguration, ChannelConfiguration.class);

        // command_topic is required
        Value value = new OnOffValue(channelConfiguration.payload_on, channelConfiguration.payload_off);
        buildChannel(fanChannelID, value, "Power").listener(componentConfiguration.getUpdateListener())//
                .stateTopic(channelConfiguration.state_topic, channelConfiguration.value_template)//
                .commandTopic(channelConfiguration.command_topic, channelConfiguration.retain)//
                .build();

        if (channelConfiguration.oscillation_state_topic != null
                || channelConfiguration.oscillation_command_topic != null) {
            value = new OnOffValue(channelConfiguration.payload_oscillation_on,
                    channelConfiguration.payload_oscillation_off);

            buildChannel(oscChannelID, value, "Oscillation").listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.oscillation_state_topic,
                            channelConfiguration.oscillation_value_template, channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.oscillation_command_topic, channelConfiguration.retain)//
                    .build();
        }
        if (channelConfiguration.speed_state_topic != null || channelConfiguration.speed_command_topic != null) {
            if (channelConfiguration.speeds != null) {
                value = new TextValue(channelConfiguration.speeds.toArray(new String[0]));
            } else {
                value = new TextValue(
                        new String[] { channelConfiguration.payload_off, channelConfiguration.payload_low_speed,
                                channelConfiguration.payload_medium_speed, channelConfiguration.payload_high_speed });
            }
            buildChannel(speedChannelID, value, "Speed").listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.speed_state_topic, channelConfiguration.speed_value_template,
                            channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.speed_command_topic, channelConfiguration.retain)//
                    .build();
        }
    }
}
