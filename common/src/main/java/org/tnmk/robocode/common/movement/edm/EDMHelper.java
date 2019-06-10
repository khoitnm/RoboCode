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
     * How far the potential destination away from the robot's current position.
     */
    private static final int DESTINATION_DISTANCE = 100;//50
    private static final int DANGEROUS_DISTANCE = DESTINATION_DISTANCE * 5;
    private static final boolean IGNORE_ENEMIES_OUTSIDE_DANGEROUS_AREA = false;

    /**
     * The number of potential destination calculation???
     */
    private static final int POTENTIAL_DESTINATIONS_COUNT = 9;

    // PAINT CONSTANTS
    private static final Color DESTINATION_PAINT_COLOR = Color.GREEN;
    private static final int DESTINATION_PAINT_SIZE = 3;//pixels
    private static final Color DANGEROUS_PAINT_COLOR = Color.RED;

    private final Robot robot;
    /**
     * The area this robot will actually move inside.
     * Usually, it won't move into sentryBorder's area because those border areas are protected by sentry bots.
     */
    private Rectangle activityArea;

    public EDMHelper(Robot robot) {
        this.robot = robot;
    }

    public void runInit(){
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
     * @return furthest point from enemies. Note: if {@link #IGNORE_ENEMIES_OUTSIDE_DANGEROUS_AREA}, the result could be null because it cannot found any enemy.
     * Note: one flaw of this algorithm is that when the destination calculation could return a result which is blocked by an enemy. (you can test with a RamFire enemy and Crazy enemy)
     */
    public DestinationCalculation getDestination(Collection<Point2D> enemies) {
        final Collection<EDMPoint> potentialDestinations = getPotentialDestinations(DESTINATION_DISTANCE, enemies);

        double maxAvgDistance = 0;
        EDMPoint destination = null;

        for (EDMPoint potentialDestination : potentialDestinations) {
            double avgDistance = calculateAvgDistance(potentialDestination, enemies);
            if (avgDistance > maxAvgDistance) {
                maxAvgDistance = avgDistance;
                destination = potentialDestination;
            }
        }

        return new DestinationCalculation(destination, potentialDestinations);
    }

    public static class DestinationCalculation{
        private final EDMPoint destination;
        private final Collection<EDMPoint> enemies;

        private DestinationCalculation(EDMPoint destination, Collection<EDMPoint> enemies) {
            this.destination = destination;
            this.enemies = enemies;
        }

        public EDMPoint getDestination() {
            return destination;
        }

        public Collection<EDMPoint> getEnemies() {
            return enemies;
        }
    }
    /**
     * Returns the collection of points, which are located on circle with radius = <code>dist</code> and with center
     * in [<code>robot.getX()</code>, <code>robot.getY()</code>]
     *
     * @param dist    distance to probably destination points from robot
     * @param enemies enemies positions
     * @return Returns the collection of points
     */
    private Collection<EDMPoint> getPotentialDestinations(double dist, Collection<Point2D> enemies) {
        final Collection<EDMPoint> points = new LinkedList<EDMPoint>();
        //FIXME there's a bug if myPos is outside of activityArea (but it still inside the battlefield).
        final Point2D myPos = new Point2D.Double(robot.getX(), robot.getY());
        for (double angle = 0; angle < PI * 2; angle += PI / POTENTIAL_DESTINATIONS_COUNT) {
            final EDMPoint p = new EDMPoint(myPos.getX() + sin(angle) * dist, myPos.getY() + cos(angle) * dist);

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
     * @param potentialDestination   point to calculate averenge distance
     * @param enemies enemies positions
     * @return average distance
     * Note, if we ignore enemies outside dangerous area, the result could be 0.
     */
    private double calculateAvgDistance(Point2D potentialDestination, Collection<Point2D> enemies) {
        double distanceSum = 0;
        int closeEnemyCount = 0;
        for (Point2D enemy : enemies) {
            final double distance = enemy.distance(potentialDestination);
            if (IGNORE_ENEMIES_OUTSIDE_DANGEROUS_AREA && enemy.distance(robot.getX(), robot.getY()) > DANGEROUS_DISTANCE) {
                continue;
            }

            distanceSum += distance;
            closeEnemyCount++;
        }

        return distanceSum / (double) (closeEnemyCount > 0 ? closeEnemyCount : 1);
    }

    public void paintEnemiesAndDestination(AdvancedRobot robot, Collection<EDMPoint> enemies, Point2D destination) {
        Graphics2D graphics2D = robot.getGraphics();
        paintEnemies(graphics2D, enemies);
        paintDestination(graphics2D, destination);
    }

    private void paintDestination(Graphics2D graphics2D, Point2D destination){
        PaintHelper.paintPoint(graphics2D, DESTINATION_PAINT_SIZE, DESTINATION_PAINT_COLOR, destination, null);
    }
    /**
     * Paints a EDM's model with color from Red (dangerous destinations) to White (safe destinations)
     *
     * @param g       graphics to decorate. Usually {@link Robot#getGraphics()}
     * @param enemies enemies positions
     */
    private void paintEnemies(Graphics2D g, Collection<EDMPoint> enemies) {
        g.setColor(Color.WHITE);
        double maxAvgDist = 0;
        double minAvgDist = Double.MAX_VALUE;
        for (EDMPoint p : enemies) {
            if (p.avgDistance < minAvgDist) {
                minAvgDist = p.avgDistance;
            }
            if (p.avgDistance > maxAvgDist) {
                maxAvgDist = p.avgDistance;
            }
        }

        for (EDMPoint rp : enemies) {

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

        painDangerousArea(g);
    }

    private void painDangerousArea(Graphics2D g){
        g.setColor(DANGEROUS_PAINT_COLOR);
        final int fieldOfVisionRadius = DANGEROUS_DISTANCE;
        g.drawOval((int) robot.getX() - fieldOfVisionRadius / 2, (int) robot.getY() - fieldOfVisionRadius / 2,
                fieldOfVisionRadius, fieldOfVisionRadius);
    }
}