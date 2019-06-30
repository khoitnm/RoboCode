package org.tnmk.robocode.common.movement;

import java.awt.geom.Rectangle2D;
import java.util.Optional;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.model.enemy.History;
import org.tnmk.robocode.common.robot.state.AdvanceRobotState;
import org.tnmk.robocode.common.robot.state.RobotHistoryUtils;

public class MoveAreaHelper {
    /**
     * @param robotHistory
     * @param currentTime
     * @param timePeriod
     * @param expectDiagonal
     * @return empty if there's no item in the history
     */
    public static Optional<Boolean> isMoveAreaHistoryLarger(History<? extends AdvanceRobotState> robotHistory, long currentTime, int timePeriod, double expectDiagonal) {
        Optional<Rectangle2D> moveArea = RobotHistoryUtils.reckonMoveAreaInRecentPeriod(robotHistory, currentTime, timePeriod);
        if (moveArea.isPresent()) {
            double actualDiagonal = GeoMathUtils.calculateDiagonal(moveArea.get());
            return Optional.of(actualDiagonal > expectDiagonal);
        } else {
            return Optional.empty();
        }
    }
}
