package org.scapy.api;

import org.scapy.api.GameSettings.CommonSettings;

import java.util.EnumSet;

public enum Prayer {

    THICK_SKIN(1),
    BURST_OF_STRENGTH(1 << 1),
    CLARITY_OF_THOUGHT(1 << 2),
    SHARP_EYE(1 << 18),
    MYSTIC_WILL(1 << 19),
    ROCK_SKIN(1 << 3),
    SUPERHUMAN_STRENGTH(1 << 4),
    IMPROVED_REFLEXES(1 << 5),
    RAPID_RESTORE(1 << 6),
    RAPID_HEAL(1 << 7),
    PROTECT_ITEM(1 << 8),
    HAWK_EYE(1 << 20),
    MYSTIC_LORE(1 << 21),
    STEEL_SKIN(1 << 9),
    ULTIMATE_STRENGTH(1 << 10),
    INCREDIBLE_REFLEXES(1 << 11),
    PROTECT_FROM_MAGIC(1 << 12),
    PROTECT_FROM_MISSILES(1 << 13),
    PROTECT_FROM_MELEE(1 << 14),
    EAGLE_EYE(1 << 22),
    MYSTIC_MIGHT(1 << 23),
    RETRIBUTION(1 << 15),
    REDEMPTION(1 << 16),
    SMITE(1 << 17),
    CHIVALRY(1 << 24),
    PIETY(1 << 25);

    public final int flag;

    Prayer(int flag) {
        this.flag = flag;
    }

    public static boolean isActive() {
        return activeFlags() != 0;
    }

    public static boolean isActive(Prayer prayer) {
        return (activeFlags() & prayer.flag) != 0;
    }

    public static EnumSet<Prayer> activePrayers() {
        return prayers(activeFlags());
    }

    public static EnumSet<Prayer> quickPrayers() {
        return prayers(GameSettings.getSetting(CommonSettings.QUICK_PRAYERS));
    }

    private static int activeFlags() {
        return GameSettings.getSetting(CommonSettings.ACTIVE_PRAYERS);
    }

    private static EnumSet<Prayer> prayers(int flags) {
        EnumSet<Prayer> flagged = EnumSet.noneOf(Prayer.class);
        for (Prayer prayer : Prayer.values()) {
            if ((flags & prayer.flag) != 0) {
                flagged.add(prayer);
            }
        }
        return flagged;
    }
}