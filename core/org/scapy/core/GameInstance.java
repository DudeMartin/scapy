package org.scapy.core;

import org.scapy.Application;
import org.scapy.Settings;
import org.scapy.Settings.DefaultSettings;
import org.scapy.core.accessors.IClient;
import org.scapy.core.mod.Injector;
import org.scapy.core.ui.GameWindow;
import org.scapy.core.utils.DefinableClassLoader;
import org.scapy.core.utils.RevisionChecker;
import org.scapy.utils.WebUtilities;

import java.applet.Applet;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameInstance {

    private static final int DEFAULT_WORLD = 2;
    private GameStub stub;
    private Applet clientApplet;
    private IClient clientAccessor;

    public GameInstance(int initialWorld) throws Exception {
        initialize(initialWorld);
    }

    public GameInstance() throws Exception {
        this(Settings.getNumeric(DefaultSettings.INITIAL_WORLD, DEFAULT_WORLD).intValue());
    }

    private void initialize(int initialWorld) throws Exception {
        String pageAddress = "http://oldschool" + initialWorld + ".runescape.com/";
        Gamepack<?> gamepack;
        if (!Application.isVirtualMode()) {
            Path gamepackPath = Application.getPath("data", "gamepack.jar");
            if (Files.exists(gamepackPath)) {
                gamepack = Gamepack.create(gamepackPath);
                try {
                    if (!RevisionChecker.check(initialWorld, gamepack.getRevision())) {
                        gamepack = saveGamepack(gamepackPath, pageAddress);
                    }
                } catch (UnknownHostException e) {
                    System.err.println("Could not connect to world " + initialWorld + ". Attempting world " + DEFAULT_WORLD + "...");
                    Settings.set(DefaultSettings.INITIAL_WORLD, DEFAULT_WORLD);
                    initialize(DEFAULT_WORLD);
                    return;
                }
            } else {
                gamepack = saveGamepack(gamepackPath, pageAddress);
            }
        } else {
            gamepack = Gamepack.create(downloadGamepack(pageAddress));
        }
        Injector.inject(gamepack);
        stub = new GameStub(new URL(pageAddress), WebUtilities.parseParameters(WebUtilities.downloadPageSource(pageAddress)));
        start(gamepack);
    }

    private void start(Gamepack<?> gamepack) throws Exception {
        Object clientInstance = new DefinableClassLoader(gamepack.classes).loadClass("client").newInstance();
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

    private static Gamepack<?> saveGamepack(Path gamepackPath, String pageAddress) throws IOException {
        Files.write(gamepackPath, downloadGamepack(pageAddress));
        return Gamepack.create(gamepackPath);
    }
}