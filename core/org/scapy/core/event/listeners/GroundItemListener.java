package org.scapy.core.event.listeners;

import org.scapy.core.event.impl.GroundItemEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving ground item events. Ground item events
 * are received when a ground item appears or disappears from the local player's
 * current region. The <code>GroundItemEvent</code> class does not provide any
 * information about the item itself; it only provides positional information,
 * and whether the item appeared (spawned) or disappeared (despawned).
 *
 * @author Martin Tuskevicius
 */
public interface GroundItemListener extends EventListener {

    void onGroundItem(GroundItemEvent event);
}