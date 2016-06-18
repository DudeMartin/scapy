package org.scapy.api;

import org.scapy.api.wrapper.Positionable;
import org.scapy.core.accessors.IClient;

import java.awt.Point;
import java.awt.Polygon;

public final class Calculations {

    private static final int TILE_HALF_WIDTH = 64;

    private Calculations() {

    }

    public static Point worldToScreen(int preciseX, int preciseY, int height) {
        IClient clientAccessor = Game.clientAccessor();
        clientAccessor.worldToScreen(preciseX, preciseY, height);
        return new Point(clientAccessor.getLastWorldToScreenX(), clientAccessor.getLastWorldToScreenY());
    }

    public static Polygon tileBounds(Positionable positionable) {
        int preciseX = positionable.getPreciseLocalX();
        int preciseY = positionable.getPreciseLocalY();
        IClient clientAccessor = Game.clientAccessor();
        Polygon polygon = new Polygon();
        clientAccessor.worldToScreen(preciseX - TILE_HALF_WIDTH, preciseY + TILE_HALF_WIDTH, 0);
        polygon.addPoint(clientAccessor.getLastWorldToScreenX(), clientAccessor.getLastWorldToScreenY());
        clientAccessor.worldToScreen(preciseX + TILE_HALF_WIDTH, preciseY + TILE_HALF_WIDTH, 0);
        polygon.addPoint(clientAccessor.getLastWorldToScreenX(), clientAccessor.getLastWorldToScreenY());
        clientAccessor.worldToScreen(preciseX + TILE_HALF_WIDTH, preciseY - TILE_HALF_WIDTH, 0);
        polygon.addPoint(clientAccessor.getLastWorldToScreenX(), clientAccessor.getLastWorldToScreenY());
        clientAccessor.worldToScreen(preciseX - TILE_HALF_WIDTH, preciseY - TILE_HALF_WIDTH, 0);
        polygon.addPoint(clientAccessor.getLastWorldToScreenX(), clientAccessor.getLastWorldToScreenY());
        return polygon;
    }

    public static int tileDistance(Positionable a, Positionable b) {
        return (int) Math.hypot(a.getWorldX() - b.getWorldX(), a.getWorldY() - b.getWorldY());
    }
}