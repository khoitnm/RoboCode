package org.tnmk.robocode.common.paint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.tnmk.robocode.common.helper.Move2DUtils;
import robocode.Robot;

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
    public static void paintPoint(Graphics2D graphic, int pointSize, Color color, Point2D point, String printText) {
        graphic.setColor(color);
        if (printText != null) {
            graphic.drawString(printText + point, (int) point.getX() + pointSize, (int) point.getY());
        }
        Ellipse2D.Double circle = new Ellipse2D.Double(point.getX(), point.getY(), pointSize, pointSize);
        graphic.fill(circle);
//        graphic.drawLine((int) point.getX() - pointSize, (int) point.getY(), (int) point.getX() + pointSize, (int) point.getY());
//        graphic.drawLine((int) point.getX(), (int) point.getY() - pointSize, (int) point.getX(), (int) point.getY() + pointSize);
    }

    public static void paintLine(Graphics2D graphics, Point2D pointA, Point2D pointB, int width, Color color) {
        if (color != null) {
            graphics.setColor(color);
        }
        if (width > 1) {
            graphics.setStroke(new BasicStroke(width));
        } else {
            graphics.setStroke(new BasicStroke(1));
        }
        graphics.drawLine((int) pointA.getX(), (int) pointA.getY(), (int) pointB.getX(), (int) pointB.getY());
    }

    public static void paintLine(Graphics2D graphics, Point2D pointA, Point2D pointB, Color color) {
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

    public static void paintTextAtPoint(Graphics graphic, String string, int x, int y) {
        graphic.setColor(Color.WHITE);
        graphic.drawString(string, x, y);
    }

    public static void paintAngleRadian(Graphics2D graphics, Point2D startingPosition, double normAngleRadian, double normDistance, int lineWeight, Color color) {
        Point2D destination = Move2DUtils.reckonDestination(startingPosition, normAngleRadian, normDistance);
        PaintHelper.paintLine(graphics, startingPosition, destination, lineWeight, color);

    }

    public static void paintRectangle(Graphics2D graphics, Rectangle2D rectangle2D, Color color) {
        graphics.setColor(color);
        graphics.fillRect((int) rectangle2D.getX(), (int) rectangle2D.getY(), (int) rectangle2D.getWidth(), (int) rectangle2D.getHeight());
    }

    public static void paintRectangle(Graphics2D graphics, Rectangle2D rectangle2D, Color color, String text) {
        paintRectangle(graphics, rectangle2D, color);
        paintTextAtPoint(graphics, text, (int) rectangle2D.getX() + 20, (int) rectangle2D.getY() - TEXT_LINE_HEIGHT);
    }

    public static void drawRectangle(Graphics2D graphics, Rectangle2D rectangle2D, Color color) {
        graphics.setColor(color);
        graphics.drawRect((int) rectangle2D.getX(), (int) rectangle2D.getY(), (int) rectangle2D.getWidth(), (int) rectangle2D.getHeight());
    }
}
