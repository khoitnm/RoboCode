package org.tnmk.robocode.jimkirk;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.tnmk.robocode.common.constant.AimStatus;
import org.tnmk.robocode.common.helper.BattleField;
import org.tnmk.robocode.common.helper.FireByDistance;
import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.MoveHelper;
import org.tnmk.robocode.common.helper.RobotStateConverter;
import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.math.Point;
import org.tnmk.robocode.common.predictor.self.PredictHelper;
import org.tnmk.robocode.common.predictor.self.PredictHelper.PredictedAimAndFireSuccess;
import org.tnmk.robocode.common.predictor.self.PredictHelper.PredictedFiredPoint;
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
public class JimKirkStandAim extends JimKirkBase {
	public static double MOVE_DISTANCE = 30200.54;
	public static double TURN = 0;
	public static int DISTANCE_LOOP = 5;
	public static int FIRE_COUNT = 3;


	private boolean isFired = false;
	boolean finishPrepared = false;

	public JimKirkStandAim() {
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

		finishPrepared = true;
		while (true) {
			// Radar
			if (painted == getTime()-1){
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
		if (predicted == null){
			return;
		}
		if (getTime() == predicted.getFinishAimTime() && !predicted.isWaitForBetterAim()) {
			String msg = String.format("%s - THIS FIRE(%.2f, %.2f)", getTime(), getX(), getY());
			System.out.println(msg);
			setFire(predicted.getBestPredictPoint().getFirePower());
			
			isFired = true;
		}
	}

	public void onStatus(StatusEvent e) {
		long time = getTime();
		if (predicted != null && predicted.getFinishAimTime() == time) {
			RobotState robotState = RobotStateConverter.toRobotState(this);
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
		RobotState target = RobotStateConverter.toRobotState(this, targetEvent);
		String s = String.format("\t %s Target:%s", getTime(), target.getPosition());
		System.out.println(s);
		if (predictTagetHitTimes.contains(getTime()) || predictTagetAimedTimes.contains(getTime())){
			RobotState targetState = RobotStateConverter.toRobotState(this, targetEvent);
			String msg = String.format("%s - TARGET(%s, %s)", getTime(), targetState.getX(), targetState.getY());
			System.out.println(msg);
		}
		if (isFired || predicted == null || getTime() > predicted.getFinishAimTime()) {
			predicted = aimTarget(targetEvent);
			this.paintPredict();
			if (predicted.getBestPredictPoint() != null){
				predictTagetHitTimes.add(predicted.getFinishAllTime());
			}else{
				predicted.setWaitForBetterAim(true);
			}
			predictTagetAimedTimes.add(predicted.getFinishAimTime());
			if (GunHelper.isTooFarFromTarget(predicted)){
//				moveHelper.moveCloseToTarget(predicted.getCurrentTarget().getPoint());
			}
		}
		setTurnRadarToTarget(targetEvent.getBearing());
	}

	public PredictedAimAndFireSuccess aimTarget(ScannedRobotEvent targetEvent) {
		RobotState thisState = RobotStateConverter.toRobotState(this);
		RobotState targetState = RobotStateConverter.toRobotState(this, targetEvent);
		double maxPower = GunHelper.reckonMaxNecessaryPower(targetEvent.getEnergy());
		PredictedAimAndFireSuccess predicted = predictHelper.predictBestStepsToAimAndFire(this.getGunCoolingRate(), getGunHeat(), maxPower, getGunHeading(), thisState, targetState);
		predicted.setCurrentSource(thisState);
		predicted.setCurrentTarget(targetState);
		predicted.setTime(getTime());
		String msg = String.format("PREDICTION:\n" + 
				"\tNOW:\t %s - THIS(%.2f, %.2f) & TARGET(%.2f, %.2f)\n" + 
				"\tPREDICT (Aimed):\t %s - THIS FIRE(%.2f, %.2f)\n" + 
				"\tPREDICT (Aimed):\t %s - TARGET(%.2f, %.2f)\n" +
				"\tPREDICT (Fired):\t %s - TARGET(%.2f, %.2f)", 
				getTime(), getX(), getY(), targetState.getX(), targetState.getY(), 
				predicted.getFinishAimTime(), predicted.getPredictedAimedSource().getX(), predicted.getPredictedAimedSource().getY(),
				predicted.getFinishAimTime(), predicted.getPredictedAimedTarget().getX(), predicted.getPredictedAimedTarget().getY(),
				predicted.getFinishAllTime(), predicted.getPredictedFiredTarget().getX(), predicted.getPredictedFiredTarget().getY());
		System.out.println(msg);
		if (predicted.getBestPredictPoint() != null){
			double turnRightAngle = predicted.getAimAndFire().getTurnRightAngle();
			setTurnGunRight(turnRightAngle);
			isFired = false;
		}
		return predicted;
	}
}