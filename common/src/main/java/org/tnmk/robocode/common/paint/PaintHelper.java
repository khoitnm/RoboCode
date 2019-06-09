package org.tnmk.robocode.common.paint;

import com.sun.istack.internal.Nullable;
import org.tnmk.common.math.Point;
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
    public static void paintPoint(Graphics graphic, int pointSize, Color color, Point2D point,@Nullable String printText) {
        graphic.setColor(color);
        if (printText != null) {
            graphic.drawString(printText + point, (int) point.getX() + pointSize, (int) point.getY());
        }
        graphic.drawLine((int) point.getX() - pointSize, (int) point.getY(), (int) point.getX() + pointSize, (int) point.getY());
        graphic.drawLine((int) point.getX(), (int) point.getY() - pointSize, (int) point.getX(), (int) point.getY() + pointSize);
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
