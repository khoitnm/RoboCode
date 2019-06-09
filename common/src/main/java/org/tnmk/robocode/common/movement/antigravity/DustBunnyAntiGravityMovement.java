package org.tnmk.robocode.common.movement.antigravity;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

/**
 * Copied from DustBunny http://robowiki.net/wiki/DustBunny#Background_Information
 * Not so effective.
 */
public class DustBunnyAntiGravityMovement {
    private final AdvancedRobot robot;

    private double xForce;
    private double yForce;

    public DustBunnyAntiGravityMovement(AdvancedRobot robot) {
        this.robot = robot;
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        // Get Abs bearing for aiming routines (and A-Grav)
        // and distance for just about everything else :)
        double absoluteBearing = scannedRobotEvent.getBearingRadians() + robot.getHeadingRadians();
        double distance = scannedRobotEvent.getDistance();

        // Use a very simple running average system.  /2 is as cheap as I can get this
        xForce = xForce * .9 - Math.sin(absoluteBearing) / distance;
        yForce = yForce * .9 - Math.cos(absoluteBearing) / distance;

        // Get our turn angle - factor in distance from each wall every time so we get
        // pushed towards the center when close to the walls.  This took a long time to come up with.
        robot.setTurnRightRadians(Utils.normalRelativeAngle(
                Math.atan2(xForce + 1 / robot.getX() - 1 / (robot.getBattleFieldWidth() - robot.getX()),
                        yForce + 1 / robot.getY() - 1 / (robot.getBattleFieldHeight() - robot.getY()))
                        - robot.getHeadingRadians()));

        // Move ahead depending on how much turn is needed.
        robot.setAhead(120 - Math.abs(robot.getTurnRemaining()));
    }
}
