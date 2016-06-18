package org.scapy.api;

public final class GameSettings {

    public static final class CommonSettings {

        public static final int RUN = 173;
        public static final int COMBAT_STYLE = 43;
        public static final int BRIGHTNESS = 166;
        public static final int AUTO_RETALIATE = 172;
        public static final int ACTIVE_PRAYERS = 83;
        public static final int QUICK_PRAYERS = 84;
        public static final int QUICK_PRAYERS_ACTIVE = 375;
        public static final int ACCEPT_AID = 427;
        public static final int MUSIC_VOLUME = 168;
        public static final int SOUND_EFFECT_VOLUME = 169;
        public static final int AREA_SOUND_EFFECT_VOLUME = 872;
        public static final int CHAT_EFFECTS = 171;
        public static final int SPLIT_PRIVATE_CHAT = 287;
        public static final int PROFANITY_FILTER = 1074;
        public static final int MOUSE_BUTTONS = 170;
        public static final int PRAYER_ATTACK_OPTIONS = 1107;
        public static final int NPC_ATTACK_OPTIONS = 1306;
        public static final int MUSIC_MODE = 18;
        public static final int MUSIC_LOOP = 19;

        private CommonSettings() {

        }
    }

    public static final int SETTINGS_ARRAY_LENGTH = 2000;

    private GameSettings() {

    }

    public static int[] getSettings() {
        return Game.clientAccessor().getGameSettings();
    }

    public static int getSetting(int index) {
        return getSettings()[index];
    }
}