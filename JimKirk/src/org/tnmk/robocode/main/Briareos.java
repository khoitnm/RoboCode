package org.tnmk.robocode.main;

import java.awt.Color;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.tnmk.robocode.common.constant.RobotStatus;
import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.RobotStateConverter;
import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.model.BaseRobotState;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.model.Target;
import org.tnmk.robocode.common.model.TargetSet;
import org.tnmk.robocode.common.predictor.self.LinearPredictStrategy;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;
import org.tnmk.robocode.common.predictor.self.model.RawEstimateAimResult;

import robocode.ScannedRobotEvent;

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

	boolean finishPrepared = false;

	public Briareos() {
		super();
	}

	private RobotStatus currentStatus = RobotStatus.STAND_STILL;
	// private boolean isFired = false;
	//
	// int turnDirection = 1;
	// int headDirection = 1;

	private TargetSet<Target> targets = new TargetSet<>();
	/**
	 * Note: this target may have bestFirePoint or not, but we always can aim to it.
	 */
	private Target aimingTarget = null;

	public void run() {
		super.init();

		// ahead(5);
		// preparePos(battleField.getWidth()/2, battleField.getHeight() - 100);
		//
		// turnLeft(70);
		// setAhead(100000);
		// execute();

		System.out.println("FINISH PREPARING");
		finishPrepared = true;
		while (true) {
			if (getTime() >= super.paintedTime && getTime() <= paintedTime) {
				System.out.println("Debug paint");
			}

			scanRadarAround();

			if (isStandStill()) {
				moveToNearestTarget();
			}
			if (isFinishAim()) {
				if (canFire()) {
					fireToAimingTargetAtRightTime();// Don't need to see target at this moment, just fire at predicted position.
				} else {
					scanOtherNearestRobot();
					// It scan based on old data, but it not found other robots yet, so it cannot aim them.
				}
				moveToNearestTarget();
			}
			avoidWallWhenNecessary(this.battleField.getSafeArea());
			execute();
		}
	}

	private void scanOtherNearestRobot() {
		// TODO Target target = findNearestTargetByDistance();
		this.scanRadarAround();
	}

	/**
	 * If we didn't found any best fire point, it won't fire. If this is not the right time to fire, it won't fire at all. And it will fire at next tick.
	 */
	protected void fireToAimingTargetAtRightTime() {
		if (aimingTarget == null || !aimingTarget.getPredicted().isFoundBestPoint()) {
			return;
		}
		PredictedAimAndFireResult predicted = aimingTarget.getPredicted();
		if (getTime() == predicted.getAimedTime() && !predicted.isWaitForBetterAim()) {
			String msg = String.format("%s - THIS FIRE(%s, %s)", getTime(), getX(), getY());
			System.out.println(msg);
			Color bulletColor = predicted.getPredictStrategy().getPredictBulletColor();
			setBulletColor(bulletColor);
			setFire(predicted.getBestFirePoint().getFirePower());

			currentStatus = RobotStatus.STARTED_FIRE;
		}
	}

	protected void moveToNearestTarget() {
		Target target = findNearestTargetByDistance();
		if (target != null) {
			moveCloseToTarget(target.getState().getPosition());
		} else {
			moveToOtherSideOfBattleField();
		}
	}


	protected Target findNearestTargetByDistance() {
		Target nearestTarget = null;
		double nearestDistance = Double.MAX_VALUE;
		List<Target> targets = this.targets.list();
		for (Target itarget : targets) {
			double idistance = MathUtils.distance(getState().getPosition(), itarget.getState().getPosition());
			if (nearestTarget == null || nearestDistance > idistance) {
				nearestTarget = itarget;
				nearestDistance = idistance;
			}
		}
		return nearestTarget;
	}

	private void scanRadarAround() {
		if (getRadarTurnRemaining() == 0) {
			setTurnRadarRight(360);
		}
	}

	// private void fireAsPredicted() {
	// if (aimingTarget.getPredicted() == null) {
	// return;
	// }
	// if (getTime() == aimingTarget.getPredicted().getAimedTime() && !aimingTarget.getPredicted().isWaitForBetterAim()) {
	// String msg = String.format("%s - THIS FIRE(%s, %s)", getTime(), getX(), getY());
	// System.out.println(msg);
	// Color bulletColor = aimingTarget.getPredicted().getPredictStrategy().getPredictBulletColor();
	// setBulletColor(bulletColor);
	// setFire(aimingTarget.getPredicted().getBestFirePoint().getFirePower());
	//
	// isFired = true;
	// }
	// }

	// private void runNewMoveIfFinishOldMove() {
	// // Turn
	// if (getTurnRemaining() == 0) {
	// System.out.println("\t CHANGE TURN");
	// turnDirection = -turnDirection;
	// setTurnLeft(turnDirection * TURN);
	// }
	//
	// // Ahead
	// if (getDistanceRemaining() == 0) {
	// System.out.println("\t CHANGE DEADING");
	// turnDirection = -turnDirection;
	// setTurnLeft(turnDirection * TURN);
	// headDirection = -headDirection;
	// setAhead(headDirection * MOVE_DISTANCE);
	// }
	// }
	/**
	 * shorter aim time
	 * 
	 * @param targetStatus
	 * @return
	 */
	protected boolean isBetterPredictThanAimingTarget(Target newTarget) {
		// If never aim before, or that aiming was old, use the new one.
		if (aimingTarget == null || aimingTarget.getPredicted().getAimedTime() < getTime()) {
			return true;
		}
		PredictedAimAndFireResult aimingTargetPredicted = aimingTarget.getPredicted();
		PredictedAimAndFireResult newTargetPredicted = newTarget.getPredicted();

		if (!aimingTargetPredicted.isFoundBestPoint()) {
			if (newTargetPredicted.isFoundBestPoint()) {
				return true;
			} else {
				return (newTargetPredicted.getAimedTime() < aimingTargetPredicted.getAimedTime());
			}
		} else {
			if (!newTargetPredicted.isFoundBestPoint()) {
				return false;
			} else {
				return (newTargetPredicted.getAimedTime() < aimingTargetPredicted.getAimedTime());
			}
		}
	}

	protected void updateAimingTarget(Target target) {
		this.setAimingTarget(target);
	}

	protected void aimTo(Target target) {
		double gunTurnRightDirection;
		if (target.getPredicted().isFoundBestPoint()) {
			gunTurnRightDirection = target.getPredicted().getAimResult().getGunTurnRightDirection();
		} else {
			gunTurnRightDirection = target.getPredicted().getFirstAimEstimation().getGunTurnRightDirection();
		}
		setTurnGunRight(gunTurnRightDirection);
		currentStatus = RobotStatus.STARTED_AIM;
	}

	/**
	 * @return aiming target
	 */
	protected boolean isAiming() {
		return currentStatus == RobotStatus.STARTED_AIM;
	}

	protected boolean isFinishAim() {
		if (getGunTurnRemaining() == 0) {
			currentStatus = RobotStatus.AIMED;
		}
		return currentStatus.getNumValue() >= RobotStatus.AIMED.getNumValue();
	}

	protected boolean isStartedFire() {
		return currentStatus.getNumValue() >= RobotStatus.STARTED_FIRE.getNumValue();
	}

	protected boolean isStandStill() {
		if (getDistanceRemaining() == 0 || getVelocity() == 0) {
			currentStatus = RobotStatus.STAND_STILL;
		}
		return currentStatus.getNumValue() >= RobotStatus.STAND_STILL.getNumValue();
	}

	protected Target updateStateToCorrespondingTarget(BaseRobotState targetState) {
		Target target = new Target(targetState);
		this.targets.set(target);
		return target;
	}

	public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
		if (!finishPrepared) {
			return;
		}

		BaseRobotState targetStatus = RobotStateConverter.toTargetState(this, scannedRobotEvent);
		Target target = this.updateStateToCorrespondingTarget(targetStatus);
		PredictedAimAndFireResult newPredicted = predictTarget(scannedRobotEvent);
		target.setPredicted(newPredicted);

		if (isBetterPredictThanAimingTarget(target)) {
			// Should move to target?
			// If move, we have to predict aiming again.
			updateAimingTarget(target);
			aimTo(target);
		}// else, let it continue aiming to current target.

		printNewPredict(newPredicted);

		// if (newPredicted.isFoundBestPoint()) {
		// long newFiredTime = newPredicted.getFiredTime();
		// Long currentFiredTime = aimingTarget.getPredicted().getFiredTime();
		// if (aimingTarget.getPredicted() == null || currentFiredTime == null) {
		// aimingTarget.getPredicted() = newPredicted;
		// double gunTurnDirection = newPredicted.getAimResult().getGunTurnRightDirection();
		// setTurnGunRight(gunTurnDirection);
		// } else {
		// if (newFiredTime < currentFiredTime || (newFiredTime == currentFiredTime && newPredicted.getAimedTime() < aimingTarget.getPredicted().getAimedTime())) {
		// aimingTarget.getPredicted() = newPredicted;
		// double gunTurnDirection = newPredicted.getAimResult().getGunTurnRightDirection();
		// setTurnGunRight(gunTurnDirection);
		// }
		// }
		// }
		// if (isFired || aimingTarget.getPredicted() == null || getTime() > aimingTarget.getPredicted().getAimedTime()) {
		// FullRobotState targetState = RobotStateConverter.toTargetState(this, scannedRobotEvent);
		// if (getConfig().isMoveCloseToTarget()) {
		// moveHelper.moveCloseToTarget(targetState.getPosition());
		// }
		// aimingTarget.getPredicted() = aimTarget(scannedRobotEvent);
		// if (aimingTarget.getPredicted().getBestFirePoint() == null || GunHelper.isTooFarFromTarget(aimingTarget.getPredicted())) {
		// aimingTarget.getPredicted().setWaitForBetterAim(true);
		// }
		// }
	}

	private void printNewPredict(PredictedAimAndFireResult newPredicted) {
		if (!newPredicted.getPredictStrategy().getClass().equals(LinearPredictStrategy.class)){
			System.out.println("Debug predict not linear");
		}
		String currentPredictText = "";
		if (aimingTarget != null && aimingTarget.getPredicted() != null) {
			Long currentFiredTime = aimingTarget.getPredicted().getFiredTime() != null ? aimingTarget.getPredicted().getFiredTime() : 0;
			currentPredictText = String.format("Current Predict: %s\t%s. AimedTime: %s, TotalTime: %s", getTime(), aimingTarget.getPredicted().getPredictStrategy().getClass().getSimpleName(), aimingTarget.getPredicted().getAimedTime(), currentFiredTime);
		}

		String newPredictText = "";
		if (newPredicted != null) {
			Long newFiredTime = newPredicted.getFiredTime() != null ? newPredicted.getFiredTime() : 0;
			newPredictText = String.format("New Predict: %s\t%s. AimedTime: %s, TotalTime: %s", getTime(), newPredicted.getPredictStrategy().getClass().getSimpleName(), newPredicted.getAimedTime(), newFiredTime);
		}
		paint(currentPredictText, newPredictText);
		paintPredict(newPredicted);
	}

	public PredictedAimAndFireResult predictTarget(ScannedRobotEvent targetEvent) {
		FullRobotState thisState = RobotStateConverter.toRobotState(this);
		BaseRobotState targetState = RobotStateConverter.toTargetState(this, targetEvent);
		int maxPower = GunHelper.reckonMaxNecessaryPower(targetEvent.getEnergy());
		return predictHelper.predictBestStepsToAimAndFire(getTime(), this.getGunCoolingRate(), getGunHeat(), maxPower, getGunHeading(), thisState, targetState);
	}

	// public PredictedAimAndFireResult aimTarget(ScannedRobotEvent targetEvent) {
	// PredictedAimAndFireResult predicted = predictTarget(targetEvent);
	// if (predicted.getBestFirePoint() != null) {
	// double turnRightAngle = predicted.getAimResult().getGunTurnRightDirection();
	// setTurnGunRight(turnRightAngle);
	// isFired = false;
	// }
	// return predicted;
	// }

	public Target getAimingTarget() {
		return aimingTarget;
	}

	public void setAimingTarget(Target aimingTarget) {
//		this.aimingTarget = SerializationUtils.clone(aimingTarget);
		this.aimingTarget = aimingTarget;
	}
}