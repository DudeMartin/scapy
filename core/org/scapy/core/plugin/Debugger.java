package org.scapy.core.plugin;

import org.scapy.core.event.impl.PaintEvent;
import org.scapy.core.event.listeners.PaintListener;
import org.scapy.core.ui.GameWindow;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

public abstract class Debugger extends Plugin implements PaintListener {

    protected final JCheckBoxMenuItem debugItem;
    protected volatile boolean selected;

    protected Debugger() {
        final JCheckBoxMenuItem[] temporaryItem = new JCheckBoxMenuItem[1];
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    temporaryItem[0] = new JCheckBoxMenuItem(name());
                    temporaryItem[0].setToolTipText(manifest().description());
                    temporaryItem[0].addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            selected = temporaryItem[0].isSelected();
                            onSelect(selected);
                        }
                    });
                }
            });
        } catch (InterruptedException | InvocationTargetException ignored) {}
        debugItem = temporaryItem[0];
        GameWindow.getWindow().addDebugItem(debugItem);
    }

    protected abstract void paint(Graphics g);

    protected void onSelect(boolean selected) {

    }

    @Override
    public final void onPaint(PaintEvent event) {
        if (selected) {
            paint(event.graphics);
        }
    }

    @Override
    protected final long execute() {
        return -1;
    }

    @Override
    public final JPanel getSettingsPanel() {
        return null;
    }

    @Override
    public final JMenu getMenu() {
        return null;
    }
}