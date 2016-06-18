package org.scapy.api;

import org.scapy.core.accessors.IClient;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class Rasterizer {

    private Rasterizer() {

    }

    public static int alphaBlend(int foreground, int background) {
        int alpha = (foreground >> 24) & 0xFF;
        int subAlpha = 0xFF - alpha;
        int blended = 0;
        for (int shift = 16; shift >= 0; shift -= 8) {
            blended |= (((((foreground >> shift) & 0xFF) * alpha) + (((background >> shift) & 0xFF)) * subAlpha) >> 8) << shift;
        }
        return blended;
    }

    public static void drawPixel(int x, int y, int argb) {
        IClient clientAccessor = Game.clientAccessor();
        if (x < clientAccessor.getRasterizer2DTopX()
                || x > clientAccessor.getRasterizer2DBottomX()
                || y < clientAccessor.getRasterizer2DTopY()
                || y > clientAccessor.getRasterizer2DBottomY()) {
            return;
        }
        drawPixelInternal(x, y, argb);
    }

    public static void drawLine(int startX, int startY, int endX, int endY, int argb) {
        int width = endX - startX;
        int height = endY - startY;
        int deltaStartX = 0, deltaStartY = 0, deltaEndX = 0, deltaEndY = 0;
        if (width < 0) {
            deltaStartX = deltaEndX = -1;
        } else if (width > 0) {
            deltaStartX = deltaEndX = 1;
        }
        if (height < 0) {
            deltaStartY = -1;
        } else if (height > 0) {
            deltaStartY = 1;
        }
        int longest = Math.abs(width);
        int shortest = Math.abs(height);
        if (longest <= shortest) {
            int swap = longest;
            longest = shortest;
            shortest = swap;
            if (height < 0) {
                deltaEndY = -1;
            } else if (height > 0) {
                deltaEndY = 1;
            }
            deltaEndX = 0;
        }
        int numerator = longest >> 1;
        for (int i = 0; i <= longest; i++) {
            drawPixel(startX, startY, argb);
            numerator += shortest;
            if (numerator >= longest) {
                numerator -= longest;
                startX += deltaStartX;
                startY += deltaStartY;
            } else {
                startX += deltaEndX;
                startY += deltaEndY;
            }
        }
    }

    public static void drawLine(Point a, Point b, int argb) {
        drawLine(a.x, a.y, b.x, b.y, argb);
    }

    public static void fillRectangle(int x, int y, int width, int height, int argb) {
        IClient clientAccessor = Game.clientAccessor();
        int topX = clientAccessor.getRasterizer2DTopX();
        int topY = clientAccessor.getRasterizer2DTopY();
        int bottomX = clientAccessor.getRasterizer2DBottomX();
        int bottomY = clientAccessor.getRasterizer2DBottomY();
        if (x < topX) {
            width -= topX - x;
            x = topX;
        }
        if (y < topY) {
            height -= topY - y;
            y = topY;
        }
        if (x + width > bottomX) {
            width = bottomX - x;
        }
        if (y + height > bottomY) {
            height = bottomY - y;
        }
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                drawPixelInternal(x + column, y + row, argb);
            }
        }
    }

    public static void fillRectangle(Rectangle rectangle, int argb) {
        fillRectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height, argb);
    }

    public static void drawPolygon(Polygon polygon, int argb) {
        for (int i = 0; i < polygon.npoints; i++) {
            if (i == polygon.npoints - 1) {
                drawLine(polygon.xpoints[i], polygon.ypoints[i], polygon.xpoints[0], polygon.ypoints[0], argb);
            } else {
                drawLine(polygon.xpoints[i], polygon.ypoints[i], polygon.xpoints[i + 1], polygon.ypoints[i + 1], argb);
            }
        }
    }

    public static void drawImage(BufferedImage image, int x, int y) {
        IClient clientAccessor = Game.clientAccessor();
        int topX = clientAccessor.getRasterizer2DTopX();
        int topY = clientAccessor.getRasterizer2DTopY();
        int imageX = 0, imageY = 0, imageYOffset = 0;
        if (x < topX) {
            imageX = topX - x;
            x = topX;
        }
        if (y < topY) {
            imageY = imageYOffset = topY - y;
            y = topY;
        }
        for (int rasterX = x; rasterX < clientAccessor.getRasterizer2DBottomX() && imageX < image.getWidth(); rasterX++, imageX++) {
            for (int rasterY = y; rasterY < clientAccessor.getRasterizer2DBottomY() && imageY < image.getHeight(); rasterY++, imageY++) {
                drawPixelInternal(rasterX, rasterY, image.getRGB(imageX, imageY));
            }
            imageY = imageYOffset;
        }
    }

    public static void drawString(String string, Font font, Color color, int x, int y) {
        FontMetrics metrics = Game.clientAccessor().getCanvas().getGraphics().getFontMetrics(font);
        int stringWidth = metrics.stringWidth(string);
        int stringHeight = metrics.getHeight();
        BufferedImage stringImage = new BufferedImage(stringWidth, stringHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics imageGraphics = stringImage.createGraphics();
        imageGraphics.setFont(font);
        imageGraphics.setColor(color);
        imageGraphics.drawString(string, 0, metrics.getAscent());
        imageGraphics.dispose();
        drawImage(stringImage, x, y);
        stringImage.flush();
    }

    private static void drawPixelInternal(int x, int y, int argb) {
        IClient clientAccessor = Game.clientAccessor();
        int offset = x + y * clientAccessor.getRasterizer2DWidth();
        int[] pixels = clientAccessor.getRasterizer2DPixels();
        int alpha = (argb >> 24) & 0xFF;
        if (alpha == 0xFF) {
            pixels[offset] = argb & 0xFFFFFF;
            return;
        }
        pixels[offset] = alphaBlend(argb, pixels[offset]);
    }
}