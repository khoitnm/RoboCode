package org.tnmk.robocode.main;

import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.RobotStateConverter;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;

import robocode.ScannedRobotEvent;
import robocode.control.snapshot.RobotState;

/**
 * Briareos Hecatonchires: an character in Appleseed anime movie. He's very similar to Outlander (from Outlands soundtrack in Tron Legacy movie)
 * 
 * @author Khoi With AdvancedRobot, the shooting time is different from basic Robot class.
 * 
 *         Term: + Bearing: the angle (degree) from pointA to pointB (or vectorA to vectorB). It can be an absolute bearing (compare to North axis) or relative bearing (compare to vectorA)
 */
public class Briareos extends OutlanderBase {

	public static double MOVE_DISTANCE = 30200.54;
	public static double TURN = 0;
	public static int DISTANCE_LOOP = 5;
	public static int FIRE_COUNT = 3;

	private boolean isFired = false;
	boolean finishPrepared = false;

	public Briareos() {
		super();
	}

	int turnDirection = 1;
	int headDirection = 1;

	public void run() {
		super.init();
		
		// ahead(5);
//		 preparePos(battleField.getWidth()/2, battleField.getHeight() - 100);
		//
		// turnLeft(70);
		// setAhead(100000);
		// execute();

		System.out.println("FINISH PREPARING");
		finishPrepared = true;
		while (true) {
			if (getConfig().isChangeDirectionWhenFinishMove()) {
				runNewMoveIfFinishOldMove();
			}

			if (getRadarTurnRemaining() == 0) {
				setTurnRadarRight(360);
			}

			// Our robot already predicted target long time ago, so it just shot by predicted. It doesn't need to see target to shot anymore.
			if (getConfig().isFire() && canFire() && getGunTurnRemaining() == 0) {
				fireAsPredicted();
			}
			avoidWallWhenNecessary(this.battleField.getSafeArea());
			execute();
		}
	}

	private void fireAsPredicted() {
		if (predicted == null) {
			return;
		}
		if (getTime() == predicted.getAimedTime() && !predicted.isWaitForBetterAim()) {
			String msg = String.format("%s - THIS FIRE(%s, %s)", getTime(), getX(), getY());
			System.out.println(msg);
			setFire(predicted.getBestFirePoint().getFirePower());

			isFired = true;
		}
	}

	private void runNewMoveIfFinishOldMove() {
		// Turn
		if (getTurnRemaining() == 0) {
			System.out.println("\t CHANGE TURN");
			turnDirection = -turnDirection;
			setTurnLeft(turnDirection * TURN);
		}

		// Ahead
		if (getDistanceRemaining() == 0) {
			System.out.println("\t CHANGE DEADING");
			turnDirection = -turnDirection;
			setTurnLeft(turnDirection * TURN);
			headDirection = -headDirection;
			setAhead(headDirection * MOVE_DISTANCE);
		}
	}

	public void onScannedRobot(ScannedRobotEvent targetEvent) {
		if (!finishPrepared) {
			return;
		}
		if (isFired || predicted == null || getTime() > predicted.getAimedTime()) {
			FullRobotState target = RobotStateConverter.toRobotState(this, targetEvent);
			if (getConfig().isMoveCloseToTarget()) {
				moveHelper.moveCloseToTarget(target.getPosition());
			}
			predicted = aimTarget(targetEvent);
			if (predicted.getBestFirePoint() == null || GunHelper.isTooFarFromTarget(predicted)) {
				predicted.setWaitForBetterAim(true);
			}
		}
	}

	public PredictedAimAndFireResult aimTarget(ScannedRobotEvent targetEvent) {
		FullRobotState thisState = RobotStateConverter.toRobotState(this);
		FullRobotState targetState = RobotStateConverter.toRobotState(this, targetEvent);
		int maxPower = GunHelper.reckonMaxNecessaryPower(targetEvent.getEnergy());
		PredictedAimAndFireResult predicted = predictHelper.predictBestStepsToAimAndFire(getTime(), this.getGunCoolingRate(), getGunHeat(), maxPower, getGunHeading(), thisState, targetState);
		if (predicted.getBestFirePoint() != null) {
			double turnRightAngle = predicted.getAimResult().getGunTurnRightDirection();
			setTurnGunRight(turnRightAngle);
			isFired = false;
		}
		return predicted;
	}
}