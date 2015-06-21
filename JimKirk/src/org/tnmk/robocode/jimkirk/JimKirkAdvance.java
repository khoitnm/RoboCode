package org.tnmk.robocode.jimkirk;

import org.tnmk.robocode.common.constant.AimStatus;
import org.tnmk.robocode.common.helper.BattleField;
import org.tnmk.robocode.common.helper.FireByDistance;
import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.MoveHelper;
import org.tnmk.robocode.common.helper.MoveHelper.BattlePosition;
import org.tnmk.robocode.common.helper.RobotStateConverter;
import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.predictor.self.PredictHelper;
import org.tnmk.robocode.common.predictor.self.PredictHelper.PredictedAimAndFireSuccess;
import org.tnmk.robocode.common.predictor.self.RobotState;

import robocode.AdvancedRobot;
import robocode.Event;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

/**
 * @author Khoi With AdvancedRobot, the shooting time is different from basic
 *         Robot class.
 * 
 *         Term: + Bearing: the angle (degree) from pointA to pointB (or vectorA
 *         to vectorB). It can be an absolute bearing (compare to North axis) or
 *         relative bearing (compare to vectorA)
 */
public class JimKirkAdvance extends JimKirkBase {
	public static double MOVE_DISTANCE = 30200.54;
	public static double TURN = 0;
	public static int DISTANCE_LOOP = 5;
	public static int FIRE_COUNT = 3;

	private boolean isFired = false;
	boolean finishPrepared = false;

	public JimKirkAdvance() {
		super();
	}

	protected long getPeriod(long previousTime) {
		return getTime() - previousTime;
	}


	int turnDirection = 1;
	int headDirection = 1;

	private AimStatus aimStatus = AimStatus.NOT;

	private boolean isAiming() {
		return (aimStatus == AimStatus.AIMING);
	}

	public void run() {
		super.init();

		ahead(5);
		moveHelper.moveToSafeCorner(BattlePosition.TOP_RIGHT);
		turnLeft(this.getHeading() + 90);
		System.out.println("FINISH PREPARING");

		setAhead(headDirection * MOVE_DISTANCE);
		setTurnLeft(turnDirection * TURN);
		execute();
		finishPrepared = true;
		while (true) {
			// if has new aim, than we can change direction.
			// if (movingForAimCount != aimCount){
			runNewMoveIfFinishOldMove();
			// }

			// Radar
			if (getRadarTurnRemaining() == 0) {
				setTurnRadarRight(360);
			}

			// Our robot already predicted target long time ago, so it just shot
			// by predicted. It doesn't need to see target to shot anymore.
			if (canFire() && getGunTurnRemaining() == 0) {
				fireAsPredicted();
			}
			execute();
		}
	}
	private void fireAsPredicted(){
		if (predicted == null){
			return;
		}
		if (getTime() == predicted.getFinishAimTime() && !predicted.isWaitForBetterAim()) {
			String msg = String.format("%s - THIS FIRE(%s, %s)", getTime(), getX(), getY());
			System.out.println(msg);
			setFire(predicted.getBestPredictPoint().getFirePower());
			
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
			// setTurnLeft(90.73);//override current turn left
			turnDirection = -turnDirection;
			setTurnLeft(turnDirection * TURN);
			headDirection = -headDirection;
			setAhead(headDirection * MOVE_DISTANCE);
		}
	}

	public void onStatus(StatusEvent e) {
		long time = getTime();
		if (predicted != null && predicted.getFinishAimTime() == time) {
			RobotState robotState = RobotStateConverter.toRobotState(this);
			String msg = String.format("%s - THIS(%s, %s)", time, robotState.getX(), robotState.getY());
			System.out.println(msg);
		}
	}

	public void onScannedRobot(ScannedRobotEvent targetEvent) {
		if (!finishPrepared) {
			return;
		}		
		if (isFired || predicted == null || getTime() > predicted.getFinishAimTime()) {
			predicted = aimTarget(targetEvent);
			if (predicted.getBestPredictPoint() == null || GunHelper.isTooFarFromTarget(predicted)){
				predicted.setWaitForBetterAim(true);
				moveHelper.moveCloseToTarget(predicted.getCurrentTarget().getPosition());
			}
		}
	}

	public PredictedAimAndFireSuccess aimTarget(ScannedRobotEvent targetEvent) {
		RobotState thisState = RobotStateConverter.toRobotState(this);
		RobotState targetState = RobotStateConverter.toRobotState(this, targetEvent);
		double maxPower = GunHelper.reckonMaxNecessaryPower(targetEvent.getEnergy());
		PredictedAimAndFireSuccess predicted = predictHelper.predictBestStepsToAimAndFire(this.getGunCoolingRate(), getGunHeat(), maxPower, getGunHeading(), thisState, targetState);
		predicted.setCurrentSource(thisState);
		predicted.setCurrentTarget(targetState);
		predicted.setTime(getTime());
		String msg = String.format("PREDICTION:\n" + "\tNOW:\t %s - THIS(%s,%s) & TARGET(%s,%s)\n" + "\tPREDICT:\t %s - THIS FIRE(%s,%s)\n" + "\tPREDICT:\t %s - TARGET(%s,%s)", getTime(), getX(), getY(), targetState.getX(), targetState.getY(), getTime()
		        + predicted.getAimAndFire().getAimSteps(), predicted.getPredictedAimedSource().getX(), predicted.getPredictedAimedSource().getY(), getTime() + predicted.getAimAndFire().getTotalSteps(), predicted.getPredictedFiredTarget().getX(), predicted
		        .getPredictedFiredTarget().getY());
		System.out.println(msg);
		if (predicted.getBestPredictPoint() != null){
			double turnRightAngle = predicted.getAimAndFire().getTurnRightAngle();
			setTurnGunRight(turnRightAngle);
			isFired = false;
		}		
		return predicted;
	}
}