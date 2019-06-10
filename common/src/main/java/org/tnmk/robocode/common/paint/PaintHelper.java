package org.tnmk.robocode.common.paint;

import com.sun.istack.internal.Nullable;
import robocode.Robot;

import java.awt.*;
import java.awt.geom.Point2D;

public class PaintHelper {
    /**
     * The height of a line of text (measure in pixel).
     */
    public static final int TEXT_LINE_HEIGHT = 20;

    /**
     * @param graphic   Usually get from {@link Robot#getGraphics()}
     * @param pointSize
     * @param color
     * @param point
     * @param printText the text next to the point. Could be null
     */
    public static void paintPoint(Graphics graphic, int pointSize, Color color, Point2D point, @Nullable String printText) {
        graphic.setColor(color);
        if (printText != null) {
            graphic.drawString(printText + point, (int) point.getX() + pointSize, (int) point.getY());
        }
        graphic.drawLine((int) point.getX() - pointSize, (int) point.getY(), (int) point.getX() + pointSize, (int) point.getY());
        graphic.drawLine((int) point.getX(), (int) point.getY() - pointSize, (int) point.getX(), (int) point.getY() + pointSize);
    }

    public static void paintLine(Graphics graphics, Point2D pointA, Point2D pointB, int width, @Nullable Color color) {
        if (color != null) {
            graphics.setColor(color);
        }
        if (width > 1) {
            for (double i = -width / 2; i < width / 2; i++) {
                graphics.drawLine((int) (pointA.getX() + i), (int) (pointA.getY() + i), (int) (pointB.getX() + i), (int) (pointB.getY() + i));
            }
        } else {
            paintLine(graphics, pointA, pointB, color);
        }
    }

    public static void paintLine(Graphics graphics, Point2D pointA, Point2D pointB, @Nullable Color color) {
        paintLine(graphics, pointA, pointB, 1, color);
    }

    /**
     * @param graphic Usually get from {@link Robot#getGraphics()}
     * @param string
     * @param x
     * @param line    the index of a line, each line is {@link #TEXT_LINE_HEIGHT} pixels.
     */
    public static void paintText(Graphics graphic, String string, int x, int line) {
        graphic.setColor(Color.WHITE);
        graphic.drawString(string, x, (line + 1) * TEXT_LINE_HEIGHT);
    }
}
