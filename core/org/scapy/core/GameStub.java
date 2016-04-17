package org.scapy.core;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;
import java.util.Map;

class GameStub implements AppletStub {

    boolean active;
    private final URL base;
    private final Map<String, String> parameters;

    GameStub(URL base, Map<String, String> parameters) {
        this.base = base;
        this.parameters = parameters;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public URL getDocumentBase() {
        return base;
    }

    @Override
    public URL getCodeBase() {
        return base;
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name);
    }

    @Override
    public AppletContext getAppletContext() {
        return null;
    }

    @Override
    public void appletResize(int width, int height) {

    }
}