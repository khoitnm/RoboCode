package org.tnmk.robocode.common.movement.strategy.antigravity;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.helper.Move2DUtils;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.movement.MoveAreaHelper;
import org.tnmk.robocode.common.movement.MovementContext;
import robocode.AdvancedRobot;
import robocode.Rules;

public class AvoidOneAreaTooLongMoveHelper {
    private static final double ONE_CIRCLE_PERIOD = Move2DUtils.reckFinishCircleTime(Rules.MAX_VELOCITY);
    private static final int ONE_CIRCLE_PERIOD_TICKS = (int) Math.round(ONE_CIRCLE_PERIOD);

    /**
     * Battlefield 1200x1200, 424 is the diagonal of each part when splitting width & height to 16 parts (4 parts each side)
     */
    private static final double ONE_SMALL_AREA_DIAGONAL = 400;

    public static Point2D avoidMovingInOneAreaForTooLong(AdvancedRobot robot, Rectangle2D battleField, MovementContext movementContext, Collection<Enemy> enemies, Point2D destination) {
        if (movementContext.getRobotHistory().isEmpty()) {
            return destination;
        }
        MoveAreaHelper.MoveAreaTooLongResult moveAreaTooLongResult = MoveAreaHelper.isMoveAreaHistoryLarger(movementContext.getRobotHistory(), robot.getTime(), ONE_CIRCLE_PERIOD_TICKS, ONE_SMALL_AREA_DIAGONAL).get();
        if (moveAreaTooLongResult.isTooLong()) {
            Rectangle2D tooLongMoveArea = moveAreaTooLongResult.getMoveArea();
            if (GeoMathUtils.checkInsideRectangle(destination, tooLongMoveArea)) {
                return findDestinationOutsideArea(battleField, tooLongMoveArea, enemies);
            } else {
                return destination;
            }
        } else {
            return destination;
        }
    }

    /**
     * @param battleField
     * @param tooLongMoveArea the area which our robot has been moving inside for too long (and we want to move to another area). This area has diagonal less than {@link #ONE_SMALL_AREA_DIAGONAL}
     * @param enemies         latest information about our enemies
     * @return
     */
    private static Point2D findDestinationOutsideArea(Rectangle2D battleField, Rectangle2D tooLongMoveArea, Collection<Enemy> enemies) {
        List<RiskArea> analyzedRiskAreas = RiskAreaAnalyst.analyzeRiskAreas(battleField, enemies);
        List<RiskArea> excludedOldAreas = RiskAreaHelper.excludeAreas(analyzedRiskAreas, tooLongMoveArea);
        List<RiskArea> leastRiskAreas = RiskAreaAnalyst.findLeastRiskyArea(excludedOldAreas);
        Point2D destination = findBestDestinationInParts(leastRiskAreas);
        return destination;
    }

    private static Point2D findBestDestinationInParts(List<RiskArea> leastRiskAreas) {
        List<Rectangle2D> areas = leastRiskAreas.stream().map(RiskArea::getArea).collect(Collectors.toList());
        return DestinationRandomHelper.randomPointInAreas(areas);
    }


}
