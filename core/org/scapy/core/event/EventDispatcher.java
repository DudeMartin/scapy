package org.scapy.core.event;

import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class EventDispatcher {

    public static final EventDispatcher instance = new EventDispatcher();
    private final Set<EventListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<EventListener, Boolean>());

    private EventDispatcher() {

    }

    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(EventListener listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    public void dispatch(EventObject event) {
        for (EventListener listener : listeners) {
            if (event instanceof AWTEvent) {
                dispatchWindowEvent(listener, (AWTEvent) event);
            } else if (event instanceof GameEvent) {
                Listenable listenable = event.getClass().getAnnotation(Listenable.class);
                GameEvent gameEvent = (GameEvent) event;
                if (listenable == null || listenable.listener().isAssignableFrom(listener.getClass())) {
                    gameEvent.dispatch(listener);
                }
            } else {
                throw new UnsupportedOperationException("Unsupported event type.");
            }
        }
    }

    private static void dispatchWindowEvent(EventListener listener, AWTEvent event) {
        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            if (listener instanceof MouseListener) {
                MouseListener mouseListener = (MouseListener) listener;
                switch (event.getID()) {
                case MouseEvent.MOUSE_CLICKED:
                    mouseListener.mouseClicked(mouseEvent);
                    break;
                case MouseEvent.MOUSE_PRESSED:
                    mouseListener.mousePressed(mouseEvent);
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    mouseListener.mouseReleased(mouseEvent);
                    break;
                case MouseEvent.MOUSE_ENTERED:
                    mouseListener.mouseEntered(mouseEvent);
                    break;
                case MouseEvent.MOUSE_EXITED:
                    mouseListener.mouseExited(mouseEvent);
                    break;
                }
            }
            if (listener instanceof MouseWheelListener) {
                MouseWheelListener mouseWheelListener = (MouseWheelListener) listener;
                switch (event.getID()) {
                case MouseEvent.MOUSE_WHEEL:
                    mouseWheelListener.mouseWheelMoved((MouseWheelEvent) mouseEvent);
                    break;
                }
            }
            if (listener instanceof MouseMotionListener) {
                MouseMotionListener mouseMotionListener = (MouseMotionListener) listener;
                switch (event.getID()) {
                case MouseEvent.MOUSE_DRAGGED:
                    mouseMotionListener.mouseDragged(mouseEvent);
                    break;
                case MouseEvent.MOUSE_MOVED:
                    mouseMotionListener.mouseMoved(mouseEvent);
                    break;
                }
            }
        } else if (event instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) event;
            if (listener instanceof KeyListener) {
                KeyListener keyListener = (KeyListener) listener;
                switch (event.getID()) {
                case KeyEvent.KEY_TYPED:
                    keyListener.keyTyped(keyEvent);
                    break;
                case KeyEvent.KEY_PRESSED:
                    keyListener.keyPressed(keyEvent);
                    break;
                case KeyEvent.KEY_RELEASED:
                    keyListener.keyReleased(keyEvent);
                    break;
                }
            }
        } else if (event instanceof FocusEvent) {
            FocusEvent focusEvent = (FocusEvent) event;
            if (listener instanceof FocusListener) {
                FocusListener focusListener = (FocusListener) listener;
                switch (event.getID()) {
                case FocusEvent.FOCUS_GAINED:
                    focusListener.focusGained(focusEvent);
                    break;
                case FocusEvent.FOCUS_LOST:
                    focusListener.focusLost(focusEvent);
                    break;
                }
            }
        } else {
            throw new UnsupportedOperationException("Unsupported AWT event type.");
        }
    }
}