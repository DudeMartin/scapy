package org.scapy.core.accessors;

import java.awt.*;

public interface IClient {

    int[] getRasterizer2DPixels();

    int getRasterizer2DWidth();

    int getRasterizer2DHeight();

    int getRasterizer2DTopX();

    int getRasterizer2DTopY();

    int getRasterizer2DBottomX();

    int getRasterizer2DBottomY();

    ILinkedList[][][] getGroundItems();

    void displayChatMessage(int channel, String sender, String message);

    IInteractableObjectDefinition getInteractableObjectDefinition(int id);

    IItemDefinition getItemDefinition(int id);

    INpcDefinition getNpcDefinition(int id);

    void worldToScreen(int x, int y, int z);

    int getTileHeight(int x, int y, int plane);

    boolean isResizableMode();

    int getLastWorldToScreenX();

    int getLastWorldToScreenY();

    int[] getBaseStats();

    int getBaseX();

    int getBaseY();

    int getCameraPitch();

    int getCameraX();

    int getCameraY();

    int getCameraYaw();

    int getCameraZ();

    Canvas getCanvas();

    ICollisionMap[] getCollisionMaps();

    int getConnectionState();

    int[] getCurrentStats();

    int getDestinationX();

    int getDestinationY();

    int getEnergy();

    int[] getGameSettings();

    int[] getInterfaceComponentHeights();

    IHashTable getInterfaceComponentNodes();

    int[] getInterfaceComponentWidths();

    int[] getInterfaceComponentXPositions();

    int[] getInterfaceComponentYPositions();

    IInterfaceComponent[][] getInterfaceComponents();

    IPlayer getLocalPlayer();

    IPacketBuffer getLoginBuffer();

    int getLoginIndex();

    int getMapAngle();

    int getMapOffset();

    int getMapScale();

    String[] getMenuActions();

    int getMenuHeight();

    int[] getMenuOpcodes();

    String[] getMenuOptions();

    int[] getMenuParams0();

    int[] getMenuParams1();

    int[] getMenuParams2();

    int getMenuSize();

    int getMenuWidth();

    int getMenuX();

    int getMenuY();

    INpc[] getNpcs();

    String getPassword();

    int getPlane();

    IPlayer[] getPlayers();

    IRegion getRegion();

    IPacketBuffer getSecureBuffer();

    int[] getStatExperiences();

    int[][][] getTileHeights();

    byte[][][] getTileSettings();

    String getUsername();

    int getWeight();

    boolean isMenuOpen();
}