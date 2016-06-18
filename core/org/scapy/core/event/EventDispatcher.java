package org.scapy.core.event;

import org.scapy.core.event.listeners.*;
import org.scapy.core.mod.Callbacks;

import java.awt.AWTEvent;
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
        if (listeners.add(listener)) {
            updateListenerCounters(listener, true);
        }
    }

    public void removeListener(EventListener listener) {
        if (listeners.remove(listener)) {
            updateListenerCounters(listener, false);
        }
    }

    public void clearListeners() {
        listeners.clear();
        clearListenerCounters();
    }

    public void dispatch(EventObject event) {
        for (EventListener listener : listeners) {
            if (event instanceof GameEvent) {
                Listenable listenable = event.getClass().getAnnotation(Listenable.class);
                GameEvent gameEvent = (GameEvent) event;
                if (listenable == null || listenable.listener().isAssignableFrom(listener.getClass())) {
                    gameEvent.dispatch(listener);
                }
            } else if (event instanceof AWTEvent) {
                dispatchWindowEvent(listener, (AWTEvent) event);
            } else {
                throw new UnsupportedOperationException("Unsupported event type.");
            }
        }
    }

    private void updateListenerCounters(EventListener listener, boolean added) {
        int delta = added ? 1 : -1;
        if (listener instanceof RegionDrawListener) {
            Callbacks.regionDrawListenerCount.addAndGet(delta);
        }
        if (listener instanceof ModelRenderListener) {
            Callbacks.modelRenderListenerCount.addAndGet(delta);
        }
        if (listener instanceof GameLoopListener) {
            Callbacks.gameLoopListenerCount.addAndGet(delta);
        }
        if (listener instanceof SettingListener) {
            Callbacks.settingListenerCount.addAndGet(delta);
        }
        if (listener instanceof SkillListener) {
            Callbacks.skillListenerCount.addAndGet(delta);
        }
    }

    private void clearListenerCounters() {
        Callbacks.regionDrawListenerCount.set(0);
        Callbacks.modelRenderListenerCount.set(0);
        Callbacks.gameLoopListenerCount.set(0);
        Callbacks.settingListenerCount.set(0);
        Callbacks.skillListenerCount.set(0);
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