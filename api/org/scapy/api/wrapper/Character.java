package org.scapy.api.wrapper;

import org.scapy.api.Game;
import org.scapy.core.accessors.ICharacter;
import org.scapy.core.accessors.IClient;
import org.scapy.core.accessors.INpc;
import org.scapy.core.accessors.IPlayer;

public class Character extends Renderable implements Positionable {

    private static final int PLAYER_INDEX_OFFSET = 1 << 15;

    public Character(ICharacter accessor) {
        super(accessor);
    }

    public int getInteractingIndex() {
        return accessor().getInteractingIndex();
    }

    public Character getInteraction() {
        int index = getInteractingIndex();
        if (index != -1) {
            IClient clientAccessor = Game.clientAccessor();
            if (index < PLAYER_INDEX_OFFSET) {
                INpc accessor = clientAccessor.getNpcs()[index];
                if (accessor != null) {
                    return new Npc(accessor);
                }
            } else {
                IPlayer accessor = clientAccessor.getPlayers()[index - PLAYER_INDEX_OFFSET];
                if (accessor != null) {
                    return new Player(accessor);
                }
            }
        }
        return null;
    }

    public int getOrientation() {
        return accessor().getOrientation();
    }

    public String getOverheadText() {
        String text = accessor().getOverheadText();
        return text == null ? "" : text;
    }

    public int[] getQueueX() {
        return accessor().getQueueX();
    }

    public int[] getQueueY() {
        return accessor().getQueueY();
    }

    public int getCurrentHealth() {
        return accessor().getCurrentHealth();
    }

    public int getMaximumHealth() {
        return accessor().getMaxHealth();
    }

    public boolean isInCombat() {
        return Game.clientAccessor().getGameCycle() < accessor().getCombatCycle();
    }

    @Override
    public int getPreciseLocalX() {
        return accessor().getX();
    }

    @Override
    public int getPreciseLocalY() {
        return accessor().getY();
    }

    @Override
    public int getLocalX() {
        return getPreciseLocalX() >> 7;
    }

    @Override
    public int getLocalY() {
        return getPreciseLocalY() >> 7;
    }

    @Override
    public int getWorldX() {
        return Game.clientAccessor().getBaseX() + getLocalX();
    }

    @Override
    public int getWorldY() {
        return Game.clientAccessor().getBaseY() + getLocalY();
    }

    @Override
    public int getPlane() {
        return Game.clientAccessor().getPlane();
    }

    @Override
    public ICharacter accessor() {
        return (ICharacter) super.accessor();
    }
}