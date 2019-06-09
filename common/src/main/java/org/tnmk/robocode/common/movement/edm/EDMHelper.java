package org.tnmk.robocode.common.movement.edm;

import org.tnmk.robocode.common.paint.PaintHelper;
import robocode.AdvancedRobot;
import robocode.Robot;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * This class is implement Enemy dodging movement method
 *
 * @author jdev
 */
public class EDMHelper {

    /**
     * How far it should see enemies.
     */
    private static final int FIELD_OF_VISION = 500;//50
    private static final int DANGER_DISTANCE = FIELD_OF_VISION * 3;
    /**
     * The number of potential destination calculation.
     */
    private static final int POTENTIAL_DESTINATIONS_COUNT = 18;

    // PAINT CONSTANTS
    private static final Color DESTINATION_PAINT_COLOR = Color.GREEN;
    private static final int DESTINATION_PAINT_SIZE = 3;//pixels

    private final Robot robot;
    /**
     * The area this robot will actually move inside.
     * Usually, it won't move into sentryBorder's area because those border areas are protected by sentry bots.
     */
    private final Rectangle activityArea;

    public EDMHelper(Robot robot) {
        this.robot = robot;

        int activity_margin = this.robot.getSentryBorderSize();
        activityArea = new Rectangle(activity_margin, activity_margin,
                (int) robot.getBattleFieldWidth() - activity_margin * 2,
                (int) robot.getBattleFieldHeight() - activity_margin * 2);

        Graphics2D graphics2D = this.robot.getGraphics();
        graphics2D.setColor(Color.GRAY);
        graphics2D.drawRect(activityArea.x, activityArea.y, activityArea.width, activityArea.height);
    }

    /**
     * Method to calculate furthest point from enemies
     *
     * @param enemies position of enemies
     * @return farest point from enemies
     */
    public Point2D.Double getDestination(Collection<Point2D.Double> enemies) {
        final Collection<EDMPoint> points = getPoints(FIELD_OF_VISION, enemies);

        double maxAvgDist = 0;
        EDMPoint destination = null;

        for (EDMPoint p : points) {
            double avgDist = calculateAvgDistance(p, enemies);
            if (avgDist > maxAvgDist) {
                maxAvgDist = avgDist;
                destination = p;
            }
        }

        return destination;
    }

    /**
     * Returns the collection of points, which are located on circle with radius = <code>dist</code> and with center
     * in [<code>robot.getX()</code>, <code>robot.getY()</code>]
     *
     * @param dist    distance to probably destination points from robot
     * @param enemies enemies positions
     * @return Returns the collection of points
     */
    private Collection<EDMPoint> getPoints(double dist, Collection<Point2D.Double> enemies) {
        final Collection<EDMPoint> points = new LinkedList<EDMPoint>();
        final Point2D.Double myPos = new Point2D.Double(robot.getX(), robot.getY());
        for (double angle = 0; angle < PI * 2; angle += PI / POTENTIAL_DESTINATIONS_COUNT) {
            final EDMPoint p = new EDMPoint(myPos.x + sin(angle) * dist, myPos.y + cos(angle) * dist);

            if (!activityArea.contains(p)) {
                continue;
            }
            p.avgDistance = calculateAvgDistance(p, enemies);
            points.add(p);
        }

        return points;
    }

    /**
     * Calculates avarenge distance from point <code>point</code> to enemies in <code>enemies</code>
     *
     * @param point   point to calculate averenge distance
     * @param enemies enemies positions
     * @return averenge distance
     */
    private double calculateAvgDistance(Point2D.Double point, Collection<Point2D.Double> enemies) {
        double distanceSum = 0;
        int closeEnemyCount = 0;
        for (Point2D.Double p : enemies) {
            final double distance = p.distance(point);
            if (p.distance(robot.getX(), robot.getY()) > DANGER_DISTANCE) {
                continue;
            }

            distanceSum += distance;
            closeEnemyCount++;
        }

        return distanceSum / (double) (closeEnemyCount > 0 ? closeEnemyCount : 1);
    }

    public void paintEnemiesAndDestination(AdvancedRobot robot, Collection<Point2D.Double> enemies, Point2D.Double destination) {
        Graphics2D graphics2D = robot.getGraphics();
        paintEnemies(graphics2D, enemies);
        paintDestination(graphics2D, destination);
    }

    private void paintDestination(Graphics2D graphics2D, Point2D.Double destination){
        PaintHelper.paintPoint(graphics2D, DESTINATION_PAINT_SIZE, DESTINATION_PAINT_COLOR, destination, null);
    }
    /**
     * Paints a EDM's model
     *
     * @param g       graphics to decorate. Usually {@link Robot#getGraphics()}
     * @param enemies enemies positions
     */
    private void paintEnemies(Graphics2D g, Collection<Point2D.Double> enemies) {
        g.setColor(Color.WHITE);
        final Collection<EDMPoint> points = getPoints(FIELD_OF_VISION, enemies);
        double maxAvgDist = 0;
        double minAvgDist = Double.MAX_VALUE;
        for (EDMPoint p : points) {
            if (p.avgDistance < minAvgDist) {
                minAvgDist = p.avgDistance;
            }
            if (p.avgDistance > maxAvgDist) {
                maxAvgDist = p.avgDistance;
            }
        }

        for (EDMPoint rp : points) {

            int radius = 4;
            int gb = (int) (255 * (rp.avgDistance - minAvgDist) / (maxAvgDist - minAvgDist));
            if (gb < 0) {
                gb = 0;
            } else if (gb > 255) {
                gb = 255;
            }
            g.setColor(new Color(255, gb, gb));
            g.fillOval((int) Math.round(rp.x - radius / 2), (int) Math.round(rp.y - radius / 2), radius, radius);
            if (rp.avgDistance == maxAvgDist) {
                radius = 6;
                g.drawOval((int) Math.round(rp.x - radius / 2), (int) Math.round(rp.y - radius / 2), radius, radius);
            }
        }

        g.setColor(Color.BLUE);
        final int fieldOfVisionRadius = DANGER_DISTANCE * 2;
        g.drawOval((int) robot.getX() - fieldOfVisionRadius / 2, (int) robot.getY() - fieldOfVisionRadius / 2,
                fieldOfVisionRadius, fieldOfVisionRadius);
    }

}