package org.tnmk.robocode.common.movement.strategy.wallsmooth;


import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * http://robowiki.net/wiki/Wall_Smoothing/Implementations
 * Fast Exact Wall Smoothing (by Cb)<br/>
 * This simple algorithm is based on the Pythagorean theorem. It usually requires less than two iterations, and it returns a exact wall-smoothed destination point.<br/>
 * @deprecated Still not able to find a good way to apply it! The current project is still using the old {@link org.tnmk.robocode.common.helper.WallSmoothHelper} (with old verbose data structure), but it works.
 */
@Deprecated
public class WallSmoothUtils {
    /**
     * Fast Exact Wall Smoothing.
     * @param location the robot's current location point
     * @param destination the destination point you want to go to
     * @param circleDirection 1 or -1, for clock-wise or counter-clockwise wall smoothing
     * @param wallStick the length of the wall stick
     * @return the new wall smoothed destination point
     */
    public static Point2D wallSmoothing(Point2D location, Point2D destination, int circleDirection, double wallStick, double battleFieldWidth, double battleFieldHeight) {
        Rectangle2D.Double battleField = new Rectangle2D.Double(18, 18, battleFieldWidth - 36, battleFieldHeight - 36);
        Point2D.Double p = new Point2D.Double(destination.getX(), destination.getY());
        for (int i = 0; !battleField.contains(p) && i < 4; i++) {
            if (p.x < 18) {
                p.x = 18;
                double a = location.getX() - 18;
                p.y = location.getY() + circleDirection * Math.sqrt(wallStick * wallStick - a * a);
            } else if (p.y > battleFieldHeight - 18) {
                p.y = battleFieldHeight - 18;
                double a = battleFieldHeight - 18 - location.getY();
                p.x = location.getX() + circleDirection * Math.sqrt(wallStick * wallStick - a * a);
            } else if (p.x > battleFieldWidth - 18) {
                p.x = battleFieldWidth - 18;
                double a = battleFieldWidth - 18 - location.getX();
                p.y = location.getY() - circleDirection * Math.sqrt(wallStick * wallStick - a * a);
            } else if (p.y < 18) {
                p.y = 18;
                double a = location.getY() - 18;
                p.x = location.getX() - circleDirection * Math.sqrt(wallStick * wallStick - a * a);
            }
        }
        return p;
    }
}
