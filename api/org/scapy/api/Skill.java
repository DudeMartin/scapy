package org.scapy.api;

import org.scapy.utils.Preconditions;

public enum Skill {

    ATTACK,
    DEFENCE,
    STRENGTH,
    HITPOINTS,
    RANGED,
    PRAYER,
    MAGIC,
    COOKING,
    WOODCUTTING,
    FLETCHING,
    FISHING,
    FIREMAKING,
    CRAFTING,
    SMITHING,
    MINING,
    HERBLORE,
    AGILITY,
    THIEVING,
    SLAYER,
    FARMING,
    RUNECRAFT,
    HUNTER,
    CONSTRUCTION;

    private static final int MAX_LEVEL = 99;

    public static Skill forName(String name) {
        Preconditions.checkNull(name);
        for (Skill skill : Skill.values()) {
            if (skill.name().equalsIgnoreCase(name)) {
                return skill;
            }
        }
        return null;
    }

    public static Skill forIndex(int index) {
        return Skill.values()[index];
    }

    public static int[] currentLevels() {
        return Game.clientAccessor().getCurrentStats();
    }

    public static int[] baseLevels() {
        return Game.clientAccessor().getBaseStats();
    }

    public static int[] experiences() {
        return Game.clientAccessor().getStatExperiences();
    }

    public static int currentLevel(Skill skill) {
        return currentLevels()[skill.ordinal()];
    }

    public static int baseLevel(Skill skill) {
        return baseLevels()[skill.ordinal()];
    }

    public static int experience(Skill skill) {
        return experiences()[skill.ordinal()];
    }

    public static int experienceForLevel(int level) {
        Preconditions.check(level > 0 && level <= MAX_LEVEL, "The level must be in the range [1, " + MAX_LEVEL + "].", IllegalArgumentException.class);
        int experience = 0;
        for (int i = 1; i < level; i++) {
            experience += i + 300 * Math.pow(2, i / 7D);
        }
        return (int) Math.floor(experience / 4D);
    }

    public static int experienceToLevel(Skill skill, int targetLevel) {
        return (baseLevel(skill) >= targetLevel) ? 0 : (experienceForLevel(targetLevel) - experience(skill));
    }

    public static int experienceToNextLevel(Skill skill) {
        return experienceToLevel(skill, baseLevel(skill) + 1);
    }

    public static double percentToLevel(Skill skill, int targetLevel) {
        return (targetLevel <= baseLevel(skill)) ? 0 : (100 * experience(skill) / (double) experienceForLevel(targetLevel));
    }

    public static double percentToNextLevel(Skill skill) {
        return percentToLevel(skill, baseLevel(skill) + 1);
    }

    @Override
    public String toString() {
        String name = name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}