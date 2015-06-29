package org.tnmk.robocode.main;

import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;

import org.tnmk.robocode.common.constant.AimStatus;
import org.tnmk.robocode.common.helper.GunHelper;
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
public class OutlanderStandAim extends OutlanderBase {
	public static double MOVE_DISTANCE = 30200.54;
	public static double TURN = 0;
	public static int DISTANCE_LOOP = 5;
	public static int FIRE_COUNT = 3;


	private boolean isFired = false;
	boolean finishPrepared = false;

	public OutlanderStandAim() {
		super();
	}

	protected long getPeriod(long previousTime) {
		return getTime() - previousTime;
	}

	String action = "";
	private long aimCount = 0;
	private long movingForAimCount = 0;

	int turnDirection = 1;
	int headDirection = 1;

	private AimStatus aimStatus = AimStatus.NOT;

	private boolean isAiming() {
		return (aimStatus == AimStatus.AIMING);
	}

	public void run() {
		super.init();

		super.preparePos(this.battleField.getWidth() - 500, 100);
		finishPrepared = true;
		while (true) {
			// Radar
			if (paintedTime >= getTime()-1){
				System.out.println("Debug Painted");
			}
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
		if (currentPredicted == null){
			return;
		}
		if (getTime() == currentPredicted.getAimedTime() && !currentPredicted.isWaitForBetterAim()) {
			String msg = String.format("%s - THIS FIRE(%.2f, %.2f)", getTime(), getX(), getY());
			System.out.println(msg);
			setFire(currentPredicted.getBestFirePoint().getFirePower());
			
			isFired = true;
		}
	}

	public void onStatus(StatusEvent e) {
		long time = getTime();
		if (currentPredicted != null && currentPredicted.getAimedTime() == time) {
			FullRobotState robotState = RobotStateConverter.toRobotState(this);
			String msg = String.format("%s - THIS(%.2f, %.2f)", time, robotState.getX(), robotState.getY());
			System.out.println(msg);
		}
	}
	List<Long> predictTagetHitTimes = new LinkedList<>();
	List<Long> predictTagetAimedTimes = new LinkedList<>();
	public void onScannedRobot(ScannedRobotEvent targetEvent) {
		if (!finishPrepared) {
			return;
		}
		FullRobotState target = RobotStateConverter.toTargetState(this, targetEvent);
		String s = String.format("\t %s Target:%s", getTime(), target.getPosition());
		System.out.println(s);
		if (predictTagetHitTimes.contains(getTime()) || predictTagetAimedTimes.contains(getTime())){
			FullRobotState targetState = RobotStateConverter.toTargetState(this, targetEvent);
			String msg = String.format("%s - TARGET(%s, %s)", getTime(), targetState.getX(), targetState.getY());
			System.out.println(msg);
		}
		if (isFired || currentPredicted == null || getTime() > currentPredicted.getAimedTime()) {
			currentPredicted = aimTarget(targetEvent);
			this.paintPredict(currentPredicted);
			if (currentPredicted.getBestFirePoint() != null){
				predictTagetHitTimes.add(currentPredicted.getFiredTime());
			}else{
				currentPredicted.setWaitForBetterAim(true);
			}
			predictTagetAimedTimes.add(currentPredicted.getAimedTime());
			if (currentPredicted.getBestFirePoint() == null || GunHelper.isTooFarFromTarget(currentPredicted)){
//				moveHelper.moveCloseToTarget(predicted.getCurrentTarget().getPoint());
			}
		}
		setTurnRadarToTarget(targetEvent.getBearing());
	}

	public PredictedAimAndFireResult aimTarget(ScannedRobotEvent targetEvent) {
		FullRobotState thisState = RobotStateConverter.toRobotState(this);
		FullRobotState targetState = RobotStateConverter.toTargetState(this, targetEvent);
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
					predicted.getFiredTime(), predictedFirePoint);
			System.out.println(msg);
		
			double turnRightAngle = predicted.getAimResult().getGunTurnRightDirection();
			setTurnGunRight(turnRightAngle);
			isFired = false;
		}
		return predicted;
	}
}