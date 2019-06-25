package org.tnmk.robocode.common.movement.wallsmooth;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Optional;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.helper.RobotStateConverter;
import org.tnmk.robocode.common.helper.WallSmoothHelper;
import org.tnmk.robocode.common.model.Area;
import org.tnmk.robocode.common.model.BaseRobotState;
import org.tnmk.robocode.common.model.BattleField;
import org.tnmk.robocode.common.movement.MoveStrategy;
import org.tnmk.robocode.common.movement.MovementContext;
import robocode.AdvancedRobot;
import robocode.Condition;

public class ShouldAvoidWallCondition extends Condition {
    private final AdvancedRobot robot;
    private final MovementContext movementContext;


    private final BattleField battleField;

    private final Rectangle2D safeAreaRect;


    private Optional<Double> turnRightRadianToAvoidWall;

    public ShouldAvoidWallCondition(AdvancedRobot robot, MovementContext movementContext, BattleField battleField) {
        this.robot = robot;
        this.movementContext = movementContext;
        this.battleField = battleField;

        Area safeArea = battleField.getSafeArea();
        this.safeAreaRect = new Rectangle2D.Double(safeArea.getLeft(), safeArea.getBottom(), safeArea.getRight(), safeArea.getTop());

    }

    @Override
    public boolean test() {
        BaseRobotState baseRobotState = RobotStateConverter.toRobotState(robot);
        Double turnRightRadianToAvoidWall = WallSmoothHelper.shouldAvoidWall(battleField.getSafeArea(), baseRobotState);
        this.turnRightRadianToAvoidWall = Optional.ofNullable(turnRightRadianToAvoidWall);
        boolean shouldAvoidWall = this.turnRightRadianToAvoidWall.isPresent();
        restMoveStrategyToNoneIfFinishWallSmooth(shouldAvoidWall);
        return shouldAvoidWall;
    }

    /**
     * WallSmooth Strategy will be set in {@link WallSmoothMoveController}.<br/>
     * But this class will handle resetting movementStrategy to {@link MovementContext#setNone()}
     * <p/>
     * TODO I know the code design is not good. Will improve later.
     *      One way to handle that is continously updating needAvoidWall value into MovementContext, and let {@link WallSmoothMoveController} handle it.
     * <p/>
     * @param needAvoidWall the result of calculating should avoid wall smooth.
     */
    private void restMoveStrategyToNoneIfFinishWallSmooth(boolean needAvoidWall) {
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());
        if (movementContext.is(MoveStrategy.WALL_SMOOTH)) {//If moving with other strategy, don't stop it.
            if (GeoMathUtils.checkInsideRectangle(robotPosition, safeAreaRect) && !needAvoidWall) {
                movementContext.setNone();
            }
        }
    }

    /**
     * If no value, it means don't need to change direction to avoid wall.
     *
     * @return
     */
    public Optional<Double> getTurnRightRadianToAvoidWall() {
        return turnRightRadianToAvoidWall;
    }

    public BattleField getBattleField() {
        return battleField;
    }

    public Rectangle2D getSafeAreaRect() {
        return safeAreaRect;
    }
}
