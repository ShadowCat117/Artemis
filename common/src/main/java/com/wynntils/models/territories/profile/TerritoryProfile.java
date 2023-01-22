/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.models.territories.profile;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.wynntils.core.WynntilsMod;
import com.wynntils.utils.colors.CustomColor;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TerritoryProfile {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

    private final String name;
    private final String friendlyName;
    private final int startX;
    private final int startZ;
    private final int endX;
    private final int endZ;

    private final String guild;
    private final String guildPrefix;
    private final CustomColor guildColor;
    private final String attacker;
    private final Date acquired;

    private final int level;

    public TerritoryProfile(
            String name,
            String friendlyName,
            String guildPrefix,
            CustomColor guildColor,
            int level,
            int startX,
            int startZ,
            int endX,
            int endZ,
            String guild,
            String attacker,
            Date acquired) {
        this.name = name;
        this.friendlyName = friendlyName;

        this.level = level;

        this.guildPrefix = guildPrefix;
        this.guildColor = guildColor;
        this.guild = guild;
        this.attacker = attacker;

        this.acquired = acquired;

        if (endX < startX) {
            this.startX = endX;
            this.endX = startX;
        } else {
            this.startX = startX;
            this.endX = endX;
        }

        if (endZ < startZ) {
            this.startZ = endZ;
            this.endZ = startZ;
        } else {
            this.startZ = startZ;
            this.endZ = endZ;
        }
    }

    public String getName() {
        return name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public CustomColor getGuildColor() {
        return guildColor;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartZ() {
        return startZ;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndZ() {
        return endZ;
    }

    public String getGuild() {
        return guild;
    }

    public String getGuildPrefix() {
        return guildPrefix;
    }

    public int getLevel() {
        return level;
    }

    public String getAttacker() {
        return attacker;
    }

    public Date getAcquired() {
        return acquired;
    }

    public boolean insideArea(int playerX, int playerZ) {
        return startX <= playerX && endX >= playerX && startZ <= playerZ && endZ >= playerZ;
    }

    public static class TerritoryDeserializer implements JsonDeserializer<TerritoryProfile> {
        @Override
        public TerritoryProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject territory = json.getAsJsonObject();
            int startX = Integer.MAX_VALUE - 1;
            int startZ = Integer.MAX_VALUE - 1;
            int endX = Integer.MAX_VALUE;
            int endZ = Integer.MAX_VALUE;
            if (territory.has("location")) {
                JsonObject location = territory.getAsJsonObject("location");
                startX = location.get("startX").getAsInt();
                startZ = location.get("startZ").getAsInt();
                endX = location.get("endX").getAsInt();
                endZ = location.get("endZ").getAsInt();
            }
            String territoryName = territory.get("territory").getAsString();
            String friendlyName = territoryName.replace('’', '\'');

            String guild;
            if (territory.get("guild").isJsonNull()) {
                guild = "Unknown";
            } else {
                guild = territory.get("guild").getAsString();
            }

            Date acquired = null;
            try {
                acquired = DATE_FORMAT.parse(territory.get("acquired").getAsString());
            } catch (ParseException e) {
                WynntilsMod.error("Error when trying to parse territory profile data.", e);
            }
            String attacker = null;
            if (!territory.get("attacker").isJsonNull()) {
                attacker = territory.get("attacker").getAsString();
            }

            String guildPrefix;
            if (territory.get("guildPrefix").isJsonNull()) {
                guildPrefix = "UNKNOWN";
            } else {
                guildPrefix = territory.get("guildPrefix").getAsString();
            }

            int level = territory.get("level").getAsInt();

            CustomColor guildColor;
            if (territory.get("guildColor").getAsString().isEmpty()) {
                guildColor = CustomColor.colorForStringHash(guild);
            } else {
                guildColor =
                        CustomColor.fromHexString(territory.get("guildColor").getAsString());
            }

            return new TerritoryProfile(
                    territoryName,
                    friendlyName,
                    guildPrefix,
                    guildColor,
                    level,
                    startX,
                    startZ,
                    endX,
                    endZ,
                    guild,
                    attacker,
                    acquired);
        }
    }
}