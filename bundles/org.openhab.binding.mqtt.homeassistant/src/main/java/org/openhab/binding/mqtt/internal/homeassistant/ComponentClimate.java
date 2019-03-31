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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.mqtt.values.NumberValue;
import org.openhab.binding.mqtt.values.OnOffValue;
import org.openhab.binding.mqtt.values.TextValue;
import org.openhab.binding.mqtt.values.Value;

/**
 * A MQTT climate component, following the https://www.home-assistant.io/components/climate.mqtt/ specification.
 *
 * At the moment this only notifies the user that this feature is not yet supported.
 *
 * @author David Graeff - Initial contribution
 * @author Jochen Klein
 */
@NonNullByDefault
public class ComponentClimate extends AbstractComponent<ComponentClimate.ChannelConfiguration> {
    public static final String currentChannelID = "current"; // Randomly chosen channel "ID"
    public static final String powerChannelID = "power"; // Randomly chosen channel "ID"
    public static final String modeChannelID = "mode"; // Randomly chosen channel "ID"
    public static final String temperatueChannelID = "tempertature"; // Randomly chosen channel "ID"
    public static final String fanChannelID = "fan"; // Randomly chosen channel "ID"
    public static final String swingChannelID = "swing"; // Randomly chosen channel "ID"
    public static final String awayChannelID = "away"; // Randomly chosen channel "ID"
    public static final String holdChannelID = "hold"; // Randomly chosen channel "ID"
    public static final String auxChannelID = "aux"; // Randomly chosen channel "ID"

    /**
     * Configuration class for MQTT component
     */
    static class ChannelConfiguration extends BaseChannelConfiguration {
        ChannelConfiguration() {
            super("MQTT HVAC");
        }

        protected boolean send_if_off = true;

        protected BigDecimal initial = BigDecimal.valueOf(21);

        protected @Nullable String current_temperature_topic;
        protected @Nullable String current_temperature_template;

        protected @Nullable String power_command_topic;
        protected String payload_on = "ON";
        protected String payload_off = "OFF";

        protected @Nullable String mode_command_topic;
        protected @Nullable String mode_state_topic;
        protected @Nullable String mode_state_template;

        protected List<String> modes = Stream.of("auto", "off", "cool", "heat", "dry", "fan_only")
                .collect(Collectors.toList());

        protected @Nullable String temperature_command_topic;
        protected @Nullable String temperature_state_topic;
        protected @Nullable String temperature_state_template;
        protected @Nullable BigDecimal min_temp;
        protected @Nullable BigDecimal max_temp;
        protected BigDecimal temp_step = BigDecimal.ONE;

        protected @Nullable String fan_mode_command_topic;
        protected @Nullable String fan_mode_state_topic;
        protected @Nullable String fan_mode_state_template;

        protected List<String> fan_modes = Stream.of("auto", "low", "medium", "high").collect(Collectors.toList());

        protected @Nullable String swing_mode_command_topic;
        protected @Nullable String swing_mode_state_topic;
        protected @Nullable String swing_mode_state_template;

        protected List<String> swing_modes = Stream.of("on", "off").collect(Collectors.toList());

        protected @Nullable String away_mode_command_topic;
        protected @Nullable String away_mode_state_topic;
        protected @Nullable String away_mode_state_template;

        protected @Nullable String hold_command_topic;
        protected @Nullable String hold_state_topic;
        protected @Nullable String hold_state_template;

        protected @Nullable String aux_command_topic;
        protected @Nullable String aux_state_topic;
        protected @Nullable String aux_state_template;
    }

    public ComponentClimate(CFactory.ComponentConfiguration componentConfiguration) {
        super(componentConfiguration, ChannelConfiguration.class);

        if (channelConfiguration.current_temperature_topic != null) {
            Value value = new NumberValue(null, null, null);

            // There is no "current temperature unit" defined
            buildChannel(currentChannelID, value, "Current Temperature")//
                    .listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.current_temperature_topic,
                            channelConfiguration.current_temperature_template, channelConfiguration.value_template)//
                    .build();
        }

        if (channelConfiguration.power_command_topic != null) {
            Value value = new OnOffValue(channelConfiguration.payload_on, channelConfiguration.payload_off);

            buildChannel(powerChannelID, value, "Power")//
                    .listener(componentConfiguration.getUpdateListener())//
                    .commandTopic(channelConfiguration.power_command_topic, channelConfiguration.retain)//
                    .build();
        }

        if (channelConfiguration.mode_command_topic != null || channelConfiguration.mode_state_topic != null) {
            Value value = new TextValue(
                    channelConfiguration.modes.toArray(new String[channelConfiguration.modes.size()]));

            buildChannel(modeChannelID, value, "Mode")//
                    .listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.mode_state_topic, channelConfiguration.mode_state_template,
                            channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.mode_command_topic, channelConfiguration.retain)//
                    .build();
        }

        if (channelConfiguration.temperature_command_topic != null
                || channelConfiguration.temperature_state_topic != null) {
            Value value = new NumberValue(channelConfiguration.min_temp, channelConfiguration.max_temp,
                    channelConfiguration.temp_step);

            // There is no "temperature unit" defined
            buildChannel(temperatueChannelID, value, "Temperature")//
                    .listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.temperature_state_topic,
                            channelConfiguration.temperature_state_template, channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.temperature_command_topic, channelConfiguration.retain)//
                    .build();
        }

        if (channelConfiguration.fan_mode_command_topic != null || channelConfiguration.fan_mode_state_topic != null) {
            Value value = new TextValue(
                    channelConfiguration.fan_modes.toArray(new String[channelConfiguration.fan_modes.size()]));

            buildChannel(fanChannelID, value, "Fan mode")//
                    .listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.fan_mode_state_topic, channelConfiguration.mode_state_template,
                            channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.fan_mode_command_topic, channelConfiguration.retain)//
                    .build();
        }

        if (channelConfiguration.swing_mode_command_topic != null
                || channelConfiguration.swing_mode_state_topic != null) {
            Value value = new TextValue(
                    channelConfiguration.swing_modes.toArray(new String[channelConfiguration.swing_modes.size()]));

            buildChannel(fanChannelID, value, "Swing mode")//
                    .listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.swing_mode_state_topic, channelConfiguration.mode_state_template,
                            channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.swing_mode_command_topic, channelConfiguration.retain)//
                    .build();
        }

        if (channelConfiguration.away_mode_command_topic != null
                || channelConfiguration.away_mode_state_topic != null) {
            Value value = new OnOffValue(channelConfiguration.payload_on, channelConfiguration.payload_off);

            buildChannel(awayChannelID, value, "Away mode")//
                    .listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.away_mode_state_topic,
                            channelConfiguration.away_mode_state_template, channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.away_mode_command_topic, channelConfiguration.retain)//
                    .build();
        }

        if (channelConfiguration.hold_command_topic != null || channelConfiguration.hold_state_topic != null) {
            Value value = new OnOffValue(channelConfiguration.payload_on, channelConfiguration.payload_off);

            buildChannel(holdChannelID, value, "Away mode")//
                    .listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.hold_state_topic, channelConfiguration.hold_state_template,
                            channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.hold_command_topic, channelConfiguration.retain)//
                    .build();
        }

        if (channelConfiguration.aux_command_topic != null || channelConfiguration.aux_state_topic != null) {
            Value value = new OnOffValue(channelConfiguration.payload_on, channelConfiguration.payload_off);

            buildChannel(auxChannelID, value, "Aux mode")//
                    .listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.aux_state_topic, channelConfiguration.aux_state_template,
                            channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.aux_command_topic, channelConfiguration.retain)//
                    .build();
        }
    }
}
