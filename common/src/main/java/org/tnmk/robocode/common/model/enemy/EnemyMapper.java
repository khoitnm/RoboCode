package org.tnmk.robocode.common.model.enemy;

import java.awt.geom.Point2D;
import org.tnmk.robocode.common.helper.Move2DHelper;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class EnemyMapper {
    public static Enemy toEnemy(AdvancedRobot robot, ScannedRobotEvent scannedRobotEvent){
        Enemy enemy = new Enemy();
        enemy.setBearing(scannedRobotEvent.getBearing());
        enemy.setDistance(scannedRobotEvent.getDistance());
        enemy.setEnergy(scannedRobotEvent.getEnergy());
        enemy.setHeading(scannedRobotEvent.getHeading());
        enemy.setName(scannedRobotEvent.getName());
        enemy.setSentryRobot(scannedRobotEvent.isSentryRobot());
        enemy.setVelocity(scannedRobotEvent.getVelocity());
        Point2D targetPosition = Move2DHelper.reckonTargetPosition(robot, scannedRobotEvent);
        enemy.setPosition(targetPosition);
        enemy.setTime(robot.getTime());
        return enemy;
    }
}
