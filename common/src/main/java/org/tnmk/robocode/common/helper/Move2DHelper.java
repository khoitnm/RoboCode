package org.tnmk.robocode.common.helper;

import robocode.HitRobotEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class Move2DHelper implements Serializable{
	public static final double ROBOT_SIZE = 50;

	public static Point2D reckonTargetPosition(Robot thisRobot, HitRobotEvent targetRobotEvent) {
		return reckonTargetPosition(thisRobot, targetRobotEvent.getBearing(), ROBOT_SIZE);
	}

	/**
	 * This method is correct, no need to debug.
	 * @param thisRobot
	 * @param targetRobotEvent
	 * @return
	 */
	public static Point2D reckonTargetPosition(Robot thisRobot, ScannedRobotEvent targetRobotEvent) {
		return reckonTargetPosition(thisRobot, targetRobotEvent.getBearing(), targetRobotEvent.getDistance());
	}

	public static Point2D reckonTargetPosition(Robot thisRobot, double bearingToEnemy, double distanceToTarget) {
		double angle = Math.toRadians(thisRobot.getHeading() + bearingToEnemy);
		double x = (thisRobot.getX() + Math.sin(angle) * distanceToTarget);
		double y = (thisRobot.getY() + Math.cos(angle) * distanceToTarget);
		return new Point2D.Double(x, y);
	}

}
