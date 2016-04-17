package org.scapy.core;

import org.scapy.Application;
import org.scapy.Settings;
import org.scapy.Settings.DefaultSettings;
import org.scapy.core.accessors.IClient;
import org.scapy.core.mod.Injector;
import org.scapy.core.ui.GameWindow;
import org.scapy.core.utils.DefinableClassLoader;
import org.scapy.core.utils.RevisionUtilities;
import org.scapy.utils.FilterUtilities;
import org.scapy.utils.RandomUtilities;
import org.scapy.utils.WebUtilities;
import org.scapy.utils.WorldUtilities;
import org.scapy.utils.WorldUtilities.WorldActivityFilter;
import org.scapy.utils.WorldUtilities.WorldType;
import org.scapy.utils.WorldUtilities.WorldTypeFilter;

import java.applet.Applet;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameInstance {

    private Gamepack gamepack;
    private GameStub stub;
    private Applet clientApplet;
    private IClient clientAccessor;

    public GameInstance(int initialWorld) throws Exception {
        initialize(initialWorld);
    }

    public GameInstance() throws Exception {
        this(Settings.getNumeric(DefaultSettings.INITIAL_WORLD, -1).intValue());
    }

    private void initialize(int initialWorld) throws Exception {
        if (!WorldUtilities.exists(initialWorld)) {
            initialWorld = RandomUtilities.randomElement(
                    FilterUtilities.filter(
                            WorldUtilities.getWorlds(),
                            FilterUtilities.join(
                                    new WorldTypeFilter(WorldType.MEMBERS),
                                    new WorldActivityFilter()))).number;
        }
        String pageAddress = WorldUtilities.getAddress(initialWorld);
        if (!Application.isVirtualMode()) {
            Path gamepackPath = Application.getApplicationPath("data", "gamepack.jar");
            if (Files.exists(gamepackPath)) {
                gamepack = Gamepack.create(gamepackPath);
                if (!RevisionUtilities.checkRevision(gamepack.getRevision())) {
                    gamepack = saveGamepack(gamepackPath, pageAddress);
                }
            } else {
                gamepack = saveGamepack(gamepackPath, pageAddress);
            }
        } else {
            gamepack = Gamepack.create(downloadGamepack(pageAddress));
        }
        Injector.inject(gamepack);
        stub = new GameStub(new URL(pageAddress), WebUtilities.parseParameters(WebUtilities.downloadPageSource(pageAddress)));
        start();
    }

    private void start() throws Exception {
        Object clientInstance = new DefinableClassLoader(gamepack.classes, true).loadClass("client").newInstance();
        clientApplet = (Applet) clientInstance;
        clientAccessor = (IClient) clientInstance;
        clientApplet.setStub(stub);
        clientApplet.init();
        stub.active = true;
        clientApplet.start();
        GameWindow.getWindow().addGameApplet(clientApplet);
    }

    public void stop() {
        if (stub.isActive()) {
            stub.active = false;
            clientApplet.stop();
            clientApplet.destroy();
        }
    }

    public boolean isLoggedIn() {
        return clientAccessor.getConnectionState() >= 30;
    }

    public GameCanvas getCanvas() {
        return (GameCanvas) clientAccessor.getCanvas();
    }

    public IClient getClientAccessor() {
        return clientAccessor;
    }

    private static byte[] downloadGamepack(String pageAddress) throws IOException {
        return WebUtilities.download(pageAddress + "gamepack.jar");
    }

    private static Gamepack saveGamepack(Path gamepackPath, String pageAddress) throws IOException {
        Files.write(gamepackPath, downloadGamepack(pageAddress));
        return Gamepack.create(gamepackPath);
    }
}