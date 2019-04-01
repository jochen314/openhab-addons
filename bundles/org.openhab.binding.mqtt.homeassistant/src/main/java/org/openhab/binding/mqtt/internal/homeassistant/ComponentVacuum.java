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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.mqtt.values.OnOffValue;
import org.openhab.binding.mqtt.values.PercentageValue;
import org.openhab.binding.mqtt.values.TextValue;
import org.openhab.binding.mqtt.values.Value;

/**
 * A MQTT vacuum, following the https://www.home-assistant.io/components/vacuum.mqtt/ specification.
 *
 *
 * @author Jochen Klein - Initial contribution
 */
@NonNullByDefault
public class ComponentVacuum extends AbstractComponent<ComponentVacuum.ChannelConfiguration> {
    public static final String commandChannelID = "command"; // Randomly chosen channel "ID"
    public static final String batteryChannelID = "battery"; // Randomly chosen channel "ID"
    public static final String chargingChannelID = "charging"; // Randomly chosen channel "ID"
    public static final String cleaningChannelID = "cleaning"; // Randomly chosen channel "ID"
    public static final String dockedChannelID = "docked"; // Randomly chosen channel "ID"
    public static final String errorChannelID = "error"; // Randomly chosen channel "ID"
    public static final String fanChannelID = "fan"; // Randomly chosen channel "ID"
    public static final String extraChannelID = "extra"; // Randomly chosen channel "ID"

    /**
     * Configuration class for MQTT component
     */
    static class ChannelConfiguration extends BaseChannelConfiguration {
        ChannelConfiguration() {
            super("MQTT Vacuum");
        }

        protected List<String> supported_features = Arrays.asList("turn_on", "turn_off", "stop", "return_home",
                "status", "battery", "clean_spot");

        protected @Nullable String command_topic;

        protected String payload_turn_on = "turn_on";
        protected String payload_turn_off = "turn_off";
        protected String payload_return_to_base = "return_to_base";
        protected String payload_stop = "stop";
        protected String payload_clean_spot = "clean_spot";
        protected String payload_locate = "locate";
        protected String payload_start_pause = "start_pause";

        protected @Nullable String battery_level_topic;
        protected @Nullable String battery_level_template;

        protected @Nullable String charging_topic;
        protected @Nullable String charging_template;

        protected @Nullable String cleaning_topic;
        protected @Nullable String cleaning_template;

        protected @Nullable String docked_topic;
        protected @Nullable String docked_template;

        protected @Nullable String error_topic;
        protected @Nullable String error_template;

        protected @Nullable String fan_speed_topic;
        protected @Nullable String fan_speed_template;

        protected @Nullable String set_fan_speed_topic;

        protected @Nullable List<String> fan_speed_list;

        protected @Nullable String send_command_topic;

        protected @Nullable String json_attributes_topic;
    };

    public ComponentVacuum(CFactory.ComponentConfiguration componentConfiguration) {
        super(componentConfiguration, ChannelConfiguration.class);

        if (channelConfiguration.command_topic != null) {
            List<String> commands = new ArrayList<>();

            for (String feature : channelConfiguration.supported_features) {
                switch (feature) {
                    case "turn_on":
                        commands.add(channelConfiguration.payload_turn_on);
                        break;
                    case "turn_off":
                        commands.add(channelConfiguration.payload_turn_off);
                        break;
                    case "pause":
                        commands.add(channelConfiguration.payload_start_pause);
                        break;
                    case "stop":
                        commands.add(channelConfiguration.payload_stop);
                        break;
                    case "return_home":
                        commands.add(channelConfiguration.payload_return_to_base);
                        break;
                    case "locate":
                        commands.add(channelConfiguration.payload_locate);
                        break;
                    case "clean_spot":
                        commands.add(channelConfiguration.payload_clean_spot);
                        break;
                }
            }
            Value value = new TextValue(commands.toArray(new String[commands.size()]));
            buildChannel(commandChannelID, value, "Command").listener(componentConfiguration.getUpdateListener())//
                    .commandTopic(channelConfiguration.command_topic, channelConfiguration.retain)//
                    .build();
        }
        if (channelConfiguration.battery_level_topic != null) {
            Value value = new PercentageValue(BigDecimal.valueOf(0), BigDecimal.valueOf(100), null, null, null);
            buildChannel(batteryChannelID, value, "Battery level").listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.battery_level_topic, channelConfiguration.battery_level_template,
                            channelConfiguration.value_template)//
                    .unit("%%").build();
        }
        if (channelConfiguration.charging_topic != null) {
            Value value = new OnOffValue();
            buildChannel(chargingChannelID, value, "Charging").listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.charging_topic, channelConfiguration.charging_template,
                            channelConfiguration.value_template)//
                    .build();
        }
        if (channelConfiguration.cleaning_topic != null) {
            Value value = new OnOffValue();
            buildChannel(cleaningChannelID, value, "Cleaning").listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.cleaning_topic, channelConfiguration.cleaning_template,
                            channelConfiguration.value_template)//
                    .build();
        }
        if (channelConfiguration.docked_topic != null) {
            Value value = new OnOffValue();
            buildChannel(dockedChannelID, value, "Docked").listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.docked_topic, channelConfiguration.docked_template,
                            channelConfiguration.value_template)//
                    .build();
        }
        if (channelConfiguration.error_topic != null) {
            Value value = new TextValue();
            buildChannel(errorChannelID, value, "Error").listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.error_topic, channelConfiguration.error_template,
                            channelConfiguration.value_template)//
                    .build();
        }
        if ((channelConfiguration.set_fan_speed_topic != null || channelConfiguration.fan_speed_topic != null)
                && channelConfiguration.fan_speed_list != null) {
            Value value = new TextValue(channelConfiguration.fan_speed_list.toArray(new String[0]));
            buildChannel(fanChannelID, value, "Fan speed").listener(componentConfiguration.getUpdateListener())//
                    .stateTopic(channelConfiguration.fan_speed_topic, channelConfiguration.fan_speed_template,
                            channelConfiguration.value_template)//
                    .commandTopic(channelConfiguration.set_fan_speed_topic, channelConfiguration.retain)//
                    .build();
        }
        if (channelConfiguration.send_command_topic != null) {
            Value value = new TextValue();
            buildChannel(extraChannelID, value, "Extra command").listener(componentConfiguration.getUpdateListener())//
                    .commandTopic(channelConfiguration.send_command_topic, channelConfiguration.retain)//
                    .build();
        }
    }
}
