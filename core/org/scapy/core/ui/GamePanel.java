package org.scapy.core.ui;

import org.scapy.core.GameCanvas;

import javax.swing.*;
import java.awt.*;

class GamePanel extends JPanel {

    GamePanel() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(GameCanvas.DEFAULT_WIDTH, GameCanvas.DEFAULT_HEIGHT));
        setBackground(Color.BLACK);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (getComponentCount() == 0) {
            g.setColor(Color.WHITE);
            drawCenteredText(g, "Initializing...");
        }
    }

    private void drawCenteredText(Graphics g, String text) {
        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(text)) / 2;
        int y = (getHeight() - metrics.getHeight()) / 2  + metrics.getAscent();
        g.drawString(text, x, y);
    }
}