package org.scapy.core.event.listeners;

import org.scapy.core.event.impl.RegionDrawEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving region draw events. Region draw events
 * are received when the client finishes drawing the region (technically, what
 * is visible by the camera). Implementations of this listener can be used to
 * draw on the game screen using the game's rasterizer before any interfaces
 * or menus are drawn. This can be used to achieve the effect where interfaces
 * are drawn <em>on top</em> of any custom painting, rather than the other way
 * around.
 *
 * @author Martin Tuskevicius
 */
public interface RegionDrawListener extends EventListener {

    void onRegionDraw(RegionDrawEvent event);
}