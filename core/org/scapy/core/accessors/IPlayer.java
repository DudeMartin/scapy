package org.scapy.core.accessors;

public interface IPlayer extends ICharacter {

    IPlayerDefinition getDefinition();

    IModel getModel();

    String getName();

    int getCombatLevel();
}