package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.helper.BattleFieldUtils;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.movement.tactic.uturn.UTurnMoveController;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;

/**
 * The robot for testing the {@link org.tnmk.robocode.common.movement.tactic.uturn.UTurnMoveController}
 */
public class UTurnRobot extends AdvancedRobot {
    private static final double MOVE_DISTANCE = 200;

    private final UTurnMoveController uTurnMoveController;
    private final MovementContext movementContext;

    public UTurnRobot() {
        this.movementContext = new MovementContext(this);
        this.uTurnMoveController = new UTurnMoveController(this, movementContext);
    }

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);


        while (true) {
            uTurnMoveController.runLoop();
            if (uTurnMoveController.isStopped()) {
                Point2D destination = calculateNewDestination(BattleFieldUtils.constructRobotPosition(this));
                uTurnMoveController.setMoveToDestination(this, destination);
                LogHelper.logRobotMovement(this, "New destination: " + LogHelper.toString(destination));
            } else {
                LogHelper.logRobotMovement(this, "Move");
            }
            execute();
        }
    }

    private Point2D calculateNewDestination(Point2D currentPosition) {
        Point2D centralBattle = new Point2D.Double(this.getBattleFieldWidth() / 2d, this.getBattleFieldHeight() / 2d);
        double deltaX;
        double deltaY;
        if (currentPosition.getX() < centralBattle.getX()) {
            deltaX = MOVE_DISTANCE;
        } else {
            deltaX = -MOVE_DISTANCE;
        }
        if (currentPosition.getY() < centralBattle.getY()) {
            deltaY = MOVE_DISTANCE;
        } else {
            deltaY = -MOVE_DISTANCE;
        }
        return new Point2D.Double(currentPosition.getX() + deltaX, currentPosition.getY() + deltaY);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        setFire(0.1);
    }


}
