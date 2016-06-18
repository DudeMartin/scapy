package org.scapy.api.entities;

import org.scapy.api.Game;
import org.scapy.api.utils.filter.FilterUtilities;
import org.scapy.api.wrapper.Npc;
import org.scapy.core.accessors.INpc;
import org.scapy.utils.Filter;

import java.util.Arrays;

/**
 * A facility for retrieving local NPCs.
 *
 * @author Martin Tuskevicius
 */
public final class Npcs {

    /**
     * Prevents external initialization.
     */
    private Npcs() {

    }

    /**
     * Returns an array of local NPCs.
     *
     * @return the local NPCs, or <code>null</code>.
     */
    public static Npc[] npcs() {
        INpc[] accessors = Game.clientAccessor().getNpcs();
        if (accessors != null) {
            Npc[] wrappers = new Npc[accessors.length];
            int validCount = 0;
            for (INpc accessor : accessors) {
                if (accessor != null) {
                    wrappers[validCount++] = new Npc(accessor);
                }
            }
            return Arrays.copyOf(wrappers, validCount);
        }
        return null;
    }

    /**
     * Returns a filtered array of local NPCs.
     *
     * @param filter the NPC filter.
     * @return the filtered local NPCs, or <code>null</code>.
     * @throws NullPointerException if <code>filter</code> is <code>null</code>.
     */
    public static Npc[] npcs(Filter<Npc> filter) {
        Npc[] wrappers = npcs();
        return (wrappers == null) ? null : FilterUtilities.filter(wrappers, filter);
    }
}