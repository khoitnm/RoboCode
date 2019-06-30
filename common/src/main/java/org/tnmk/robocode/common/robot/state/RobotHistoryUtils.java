package org.tnmk.robocode.common.robot.state;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Optional;
import org.tnmk.robocode.common.model.enemy.History;

public class RobotHistoryUtils {

    /**
     * @param robotHistory
     * @param currentTime
     * @param ticksPeriod
     * @param <R>
     * @return empty if there's no item in the history
     */
    public static <R extends AdvanceRobotState> Optional<Rectangle2D> reckonMoveAreaInRecentPeriod(History<R> robotHistory, long currentTime, int ticksPeriod) {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        long oldestTimeForCalculation = Math.max(currentTime - ticksPeriod, 0);
        for (R stateItem : robotHistory.getAllHistoryItemsIterable()) {
            if (stateItem.getTime() < oldestTimeForCalculation) {
                break;
            }
            Point2D position = stateItem.getPosition();
            if (position.getX() > maxX) {
                maxX = position.getX();
            }
            if (position.getX() < minX) {
                minX = position.getX();
            }
            if (position.getY() > maxY) {
                maxY = position.getY();
            }
            if (position.getY() < minY) {
                minY = position.getY();
            }
        }
        if (robotHistory.countHistoryItems() > 0) {
            return Optional.of(new Rectangle2D.Double(minX, minY, maxX, maxX));
        } else {
            return Optional.empty();
        }
    }
}
