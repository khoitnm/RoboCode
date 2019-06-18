package org.tnmk.robocode.common.movement.wallsmooth;

import org.tnmk.robocode.common.helper.MoveHelper;
import org.tnmk.robocode.common.model.BattleField;
import org.tnmk.robocode.common.movement.MoveStrategy;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.OnCustomEventControl;
import robocode.AdvancedRobot;
import robocode.Condition;
import robocode.CustomEvent;

public class WallSmoothMovement implements OnCustomEventControl, InitiableRun {
    private final AdvancedRobot robot;
    private final MovementContext movementContext;
    private ShouldAvoidWallCondition shouldAvoidWallCondition = null;

    public WallSmoothMovement(AdvancedRobot robot, MovementContext movementContext) {
        this.robot = robot;
        this.movementContext = movementContext;
    }

    @Override
    public void onCustomEvent(CustomEvent customEvent) {
        Condition condition = customEvent.getCondition();
        if (condition instanceof ShouldAvoidWallCondition) {
            if (shouldApplyWallSmooth(movementContext)) {
                movementContext.setMoveStrategy(MoveStrategy.WALL_SMOOTH);//WallSmooth movement will be reset by ShouldAvoidWallCondition.restMoveStrategyToNoneIfFinishWallSmooth
                ShouldAvoidWallCondition shouldAvoidWallCondition = (ShouldAvoidWallCondition) condition;
                double turnRightRadianToAvoidWall = shouldAvoidWallCondition.getTurnRightRadianToAvoidWall().orElseThrow(() -> new IllegalStateException("Should have turnRightRadian to avoid wall"));
                robot.setTurnRightRadians(turnRightRadianToAvoidWall);
            }
        }
    }

    private boolean shouldApplyWallSmooth(MovementContext movementContext){
        return movementContext.hasLowerPriority(MoveStrategy.WALL_SMOOTH);
    }

    @Override
    public void runInit() {
        BattleField battleField = MoveHelper.createBattleField(robot);
        if (shouldAvoidWallCondition == null){
            ShouldAvoidWallCondition shouldAvoidWallCondition = new ShouldAvoidWallCondition(robot, movementContext, battleField);
            this.robot.addCustomEvent(shouldAvoidWallCondition);
        }
    }
}
