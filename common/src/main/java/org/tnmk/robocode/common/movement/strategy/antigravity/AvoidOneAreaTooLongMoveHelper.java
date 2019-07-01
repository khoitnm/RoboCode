package org.tnmk.robocode.common.movement.strategy.antigravity;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;
import org.tnmk.common.collection.ListUtils;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.helper.BattleFieldUtils;
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
     * @param tooLongMoveArea this area has diagonal less than {@link #ONE_SMALL_AREA_DIAGONAL}
     * @param enemies         latest information about our enemies
     * @return
     */
    private static Point2D findDestinationOutsideArea(Rectangle2D battleField, Rectangle2D tooLongMoveArea, Collection<Enemy> enemies) {
        List<Rectangle2D> battleFieldParts = splitToParts(battleField, 2);
        List<Rectangle2D> leastRiskyParts = RiskAreaAnalyst.findLeastRiskyArea(battleFieldParts);
        List<Rectangle2D> newParts = excludeTooLongMoveArea(battleFieldParts, tooLongMoveArea);
        Point2D destination = findBestDestinationInParts(newParts);
        return destination;
    }

    private static List<Rectangle2D> splitToParts(Rectangle2D rectangle2D, int dividend){
        Rectangle2D[][] parts = BattleFieldUtils.splitToParts(rectangle2D, 2, 2);
        return ListUtils.toList(parts);
    }
}
