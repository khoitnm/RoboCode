package org.tnmk.robocode.common.robot.gft;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * This class is copied from http://old.robowiki.net/robowiki?GFTargetingBot.
 * I didn't change anything.
 * This class is used to illustrated how to apply GFTMovement, GFTUtils and GFTWave.
 * The {@link GFTAimGun} is used to encapsulate only aiming target logic which is used in this robot.
 */
public class GFTargetingBot extends AdvancedRobot {
	private static final double BULLET_POWER = 1.9;
	
	private static double lateralDirection;
	private static double lastEnemyVelocity;
	private static GFTMovement movement;

	public GFTargetingBot() {
		movement = new GFTMovement(this);	
	}
	
	public void run() {
		setColors(Color.BLUE, Color.BLACK, Color.YELLOW);
		lateralDirection = 1;
		lastEnemyVelocity = 0;
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		do {
			turnRadarRightRadians(Double.POSITIVE_INFINITY); 
		} while (true);
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		double enemyAbsoluteBearing = getHeadingRadians() + e.getBearingRadians();
		double enemyDistance = e.getDistance();
		double enemyVelocity = e.getVelocity();
		if (enemyVelocity != 0) {
			lateralDirection = GFTUtils.sign(enemyVelocity * Math.sin(e.getHeadingRadians() - enemyAbsoluteBearing));
		}
		GFTWave wave = new GFTWave(this);
		wave.gunLocation = new Point2D.Double(getX(), getY());
		GFTWave.targetLocation = GFTUtils.project(wave.gunLocation, enemyAbsoluteBearing, enemyDistance);
		wave.lateralDirection = lateralDirection;
		wave.bulletPower = BULLET_POWER;
		wave.setSegmentations(enemyDistance, enemyVelocity, lastEnemyVelocity);
		lastEnemyVelocity = enemyVelocity;
		wave.bearing = enemyAbsoluteBearing;
		setTurnGunRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - getGunHeadingRadians() + wave.mostVisitedBearingOffset()));
		setFire(wave.bulletPower);
		if (getEnergy() >= BULLET_POWER) {
			addCustomEvent(wave);
		}
		movement.onScannedRobot(e);
		setTurnRadarRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - getRadarHeadingRadians()) * 2);
	}
}