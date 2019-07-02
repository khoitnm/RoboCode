package org.tnmk.robocode.common.movement;

import java.awt.geom.Rectangle2D;
import java.util.Optional;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.log.DebugHelper;
import org.tnmk.robocode.common.model.enemy.History;
import org.tnmk.robocode.common.robot.state.AdvanceRobotState;
import org.tnmk.robocode.common.robot.state.RobotHistoryUtils;
import robocode.AdvancedRobot;

public class MoveAreaHelper {
    /**
     * @param robotHistory
     * @param currentTime
     * @param timePeriod
     * @param expectDiagonal
     * @return empty if there's no item in the history
     */
    public static Optional<MoveAreaTooLongResult> isMoveAreaHistoryLarger(AdvancedRobot robot, History<? extends AdvanceRobotState> robotHistory, long currentTime, int timePeriod, double expectDiagonal) {
        Optional<Rectangle2D> moveArea = RobotHistoryUtils.reckonMoveAreaInRecentPeriod(robotHistory, currentTime, timePeriod);
        if (moveArea.isPresent()) {
            double actualDiagonal = GeoMathUtils.calculateDiagonal(moveArea.get());
            DebugHelper.debugMoveArea(robot.getGraphics(), moveArea.get(), actualDiagonal);
            boolean isMoveInSmallArea = actualDiagonal <= expectDiagonal;
            MoveAreaTooLongResult result = new MoveAreaTooLongResult(moveArea.get(), isMoveInSmallArea);
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }

    public static class MoveAreaTooLongResult {
        private final Rectangle2D moveArea;
        private final boolean isTooLong;

        public MoveAreaTooLongResult(Rectangle2D moveArea, boolean isTooLong) {
            this.moveArea = moveArea;
            this.isTooLong = isTooLong;
        }

        public Rectangle2D getMoveArea() {
            return moveArea;
        }

        public boolean isTooLong() {
            return isTooLong;
        }
    }
}
