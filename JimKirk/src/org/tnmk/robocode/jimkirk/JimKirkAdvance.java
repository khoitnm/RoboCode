package org.tnmk.robocode.jimkirk;

import org.tnmk.robocode.common.constant.AimStatus;
import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.WallSmoothHelper;
import org.tnmk.robocode.common.helper.MoveHelper.BattlePosition;
import org.tnmk.robocode.common.helper.RobotStateConverter;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedFirePoint;

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
	public static boolean CONFIG_FIRE = true;
	public static boolean CONFIG_MOVE_CLOSE_TO_TARGET = true;
	public static boolean CONFIG_CHANGE_DIRECTION = true;
	
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
	private void praparePos(double x, double y){
		moveHelper.moveTo(x, y);
		turnLeft(this.getHeading());//moveAngle = 0
	}
	public void run() {
		super.init();

		ahead(5);
		praparePos(500, battleField.getHeight() - 500);
		
		turnLeft(70);
		setAhead(100000);
		execute();
		
		
//		moveHelper.moveToSafeCorner(BattlePosition.TOP_RIGHT);
//		turnLeft(this.getHeading() + 90);
		System.out.println("FINISH PREPARING");

//		setAhead(headDirection * MOVE_DISTANCE);
//		setTurnLeft(Math.round(360) * turnDirection * TURN % 360);
//		execute();
		finishPrepared = true;
		while (true) {
			// if has new aim, than we can change direction.
			// if (movingForAimCount != aimCount){
			if (CONFIG_CHANGE_DIRECTION){
				runNewMoveIfFinishOldMove();
			}
			
			// }

			// Radar
			if (getRadarTurnRemaining() == 0) {
				setTurnRadarRight(360);
			}

			// Our robot already predicted target long time ago, so it just shot
			// by predicted. It doesn't need to see target to shot anymore.
			if (CONFIG_FIRE && canFire() && getGunTurnRemaining() == 0) {
				fireAsPredicted();
			}
			avoidWallWhenNecessary();
			execute();
		}
	}
	private void fireAsPredicted(){
		if (predicted == null){
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
			// setTurnLeft(90.73);//override current turn left
			turnDirection = -turnDirection;
			setTurnLeft(turnDirection * TURN);
			headDirection = -headDirection;
			setAhead(headDirection * MOVE_DISTANCE);
		}
	}

	public void onStatus(StatusEvent e) {
		long time = getTime();
		if (predicted != null && predicted.getAimedTime() == time) {
			FullRobotState robotState = RobotStateConverter.toRobotState(this);
			String msg = String.format("%s - THIS(%s, %s)", time, robotState.getX(), robotState.getY());
			System.out.println(msg);
		}
		super.onStatus(e);
	}

	public void onScannedRobot(ScannedRobotEvent targetEvent) {
		if (!finishPrepared) {
			return;
		}		
		if (isFired || predicted == null || getTime() > predicted.getAimedTime()) {
			predicted = aimTarget(targetEvent);
			if (predicted.getBestFirePoint() == null || GunHelper.isTooFarFromTarget(predicted)){
				predicted.setWaitForBetterAim(true);
				if (CONFIG_MOVE_CLOSE_TO_TARGET){
					moveHelper.moveCloseToTarget(predicted.getBeginTarget().getPosition());
				}
			}
		}
	}

	public PredictedAimAndFireResult aimTarget(ScannedRobotEvent targetEvent) {
		FullRobotState thisState = RobotStateConverter.toRobotState(this);
		FullRobotState targetState = RobotStateConverter.toRobotState(this, targetEvent);
		int maxPower = GunHelper.reckonMaxNecessaryPower(targetEvent.getEnergy());
		PredictedAimAndFireResult predicted = predictHelper.predictBestStepsToAimAndFire(getTime(), this.getGunCoolingRate(), getGunHeat(), maxPower, getGunHeading(), thisState, targetState);
		
		PredictedAimResult predictedAiming = predicted.getAimResult();
		PredictedFirePoint predictedFirePoint = predicted.getBestFirePoint();
		if (predictedFirePoint != null){
			String msg = String.format("PREDICTION:\n" + 
					"\tNOW:\t %s - THIS(%.2f, %.2f) & TARGET %s\n" + 
					"\tPREDICT (Aimed):\t %s - THIS FIRE %s\n" + 
					"\tPREDICT (Aimed):\t %s - TARGET %s\n" +
					"\tPREDICT (Fired):\t %s - TARGET %s", 
					getTime(), getX(), getY(), targetState.getPosition(), 
					predicted.getAimedTime(), predictedAiming.getSource().getPosition(),
					predicted.getAimedTime(), predictedAiming.getFiredTarget(),
					predicted.getTotalTime(), predictedFirePoint);
			System.out.println(msg);
			
			double turnRightAngle = predicted.getAimResult().getGunTurnRightDirection();
			setTurnGunRight(turnRightAngle);
			isFired = false;
		}		
		return predicted;
	}
}