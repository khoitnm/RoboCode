package org.tnmk.robocode.robot;

import java.awt.Color;
import java.util.List;

import org.tnmk.robocode.common.constant.FireStatus;
import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.MoveHelper;
import org.tnmk.robocode.common.helper.RobotStateConverter;
import org.tnmk.common.math.MathUtils;
import org.tnmk.robocode.common.model.BaseRobotState;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.model.PredictedTarget;
import org.tnmk.robocode.common.model.TargetSet;
import org.tnmk.robocode.common.predictor.self.LinearPredictStrategy;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;

import org.tnmk.robocode.common.robot.ModernRobot;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

/**
 * TODO error because of there are some tasks running in many steps, so the code doesn't run into run() method in those steps, it won't can avoidWall in those steps.
 * 
 * Briareos Hecatonchires: an character in Appleseed anime movie. He's very similar to Outlander (from Outlands soundtrack in Tron Legacy movie)
 * 
 * @author Khoi With AdvancedRobot, the shooting time is different from basic Robot class.
 * 
 *         Term: + Bearing: the angle (degree) from pointA to pointB (or vectorA to vectorB). It can be an absolute bearing (compare to North axis) or relative bearing (compare to vectorA)
 */
public class Briareos extends ModernRobot {
	private static final long serialVersionUID = -3795784962177096082L;

	public static double MOVE_DISTANCE = 30200.54;
	public static double TURN = 0;
	public static int DISTANCE_LOOP = 5;
	public static int FIRE_COUNT = 3;

	private boolean finishPrepared = false;
	private HitRobotEvent hitRobotEvent = null;
	public Briareos() {
		super();
	}

	private FireStatus currentFireStatus = FireStatus.STAND_STILL;

	// private boolean isFired = false;
	//
	// int turnDirection = 1;
	// int headDirection = 1;

	private TargetSet<PredictedTarget> aliveTargets = new TargetSet<>();
	/**
	 * Note: this target may have bestFirePoint or not, but we always can aim to it.
	 */
	private PredictedTarget aimingTarget = null;

	public void run() {
		super.init();

		// preparePos(500, 500);
		// preparePos(200, 200);
		// Standstill (0, 200), (200, 200)
		// ahead(5);
		// preparePos(battleField.getWidth()/2, battleField.getHeight() - 100);
		//
		// turnLeft(70);
		// setAhead(100000);
		// execute();

		System.out.println("FINISH PREPARING");
		finishPrepared = true;
		while (true) {
			long begin = System.currentTimeMillis();
			System.out.println(getTime() + "------------------------------------");
			printStatus("Norm");
			
			if (isStandStill() && (getX() > battleField.getWidth() - 20 || getX() < 20 || getY() < 20 || getY() > battleField.getHeight() - 20)) {
				String s = "Debug hit wall";
			}
			if (getTime() >= super.paintedTime && getTime() <= paintedTime) {
				// String msg = "Debug paint";
			}
			// begin = printRunTime("Normal Begin", begin);
			if (isFinishScanRobot()) {
				// System.out.println("Scaned Targets "+ aliveTargets.size());
				resetTargetsAfterFinishScan();
				scanRadarAround();
			}
			
			if (isHitRobot()){
				printStatus("HIT:b");
				if (getConfig().isChangeDirectionWhenRobotHit()) {
					printStatus("MoveAwayAfterHitRobot "+hitRobotEvent.getName());
					setMoveAwayAfterHitRobot(hitRobotEvent);
				}
				printStatus("HIT:e");
				this.hitRobotEvent = null;
			}else if (isStandStill()) {
				setMoveToNearestTarget();
			}
			if (isFinishAim()) {
				if (canFire()) {
					fireToAimingTargetAtRightTime();// Don't need to see target at this moment, just fire at predicted position.
				} else {
					scanOtherNearestRobot();
					// It scan based on old data, but it not found other robots yet, so it cannot aim them.
				}
				setMoveToNearestTarget();
			}
			avoidWallWhenNecessary(this.battleField.getSafeArea());
			
			execute();// execute hitRobot of nextTime
		}
	}

	private boolean isHitRobot() {
	    return this.hitRobotEvent != null;
    }

	public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
		long begin = System.currentTimeMillis();
		// System.out.println("OnScannedRobot Begin: "+scannedRobotEvent.getName()+" - "+getTime());
		if (!finishPrepared) {
			return;
		}

		BaseRobotState targetStatus = RobotStateConverter.toTargetState(this, scannedRobotEvent);
		PredictedTarget target = this.updateStateToCorrespondingTarget(targetStatus);
		PredictedAimAndFireResult newPredicted = predictTarget(scannedRobotEvent);
		// begin = printRunTime("\t Predicted Target", begin);
		target.setPredicted(newPredicted);

		if (isBetterPredictThanAimingTarget(target)) {
			// Should move to target?
			// If move, we have to predict aiming again.
			updateAimingTarget(target);
			setAimTo(target);
			// begin = printRunTime("\t Stared Aiming Target", begin);
		}// else, let it continue aiming to current target.

		printNewPredict(newPredicted);
		// begin = printRunTime("OnScannedRobot End: "+scannedRobotEvent.getName(), begin);
	}

	/**
	 * TODO must distinguise between hit to robot (move away from target) and standstill (move close to target).
	 * When we do that, in the same step: at first, it move away from target. But then in run(), it misunderstand that robot is standstill -> move close to target.
	 */
	@Override
	public void onHitRobot(HitRobotEvent hitRobotEvent) {
		this.hitRobotEvent = hitRobotEvent;
	}
	protected void printStatus(String title) {
		String targetStr = "null";
		if (aimingTarget != null){
			targetStr = String.format("%s %s", aimingTarget.getState().getName(),  aimingTarget.getState().getPosition());
		}
		String msg = String.format("%s %s\t Pos: %s\tVelo: %.2f\tHeading: %.2f\tAngle: %.2f\tDist: %4.2f\tTarget: %s\tDirect: %s", getTime(), title, getState().getPosition(), getVelocity(), getHeading(), getState().getMoveAngle(), getDistanceRemaining(), targetStr, getMoveDirection());
		System.out.println(msg);
	}
	private long printRunTime(String desc, long beginTime) {
		long end = System.currentTimeMillis();
		System.out.println(desc + " - " + getTime() + " - runtime: " + (end - beginTime) + " ms");
		return end;
	}

	protected void resetTargetsAfterFinishScan() {
		// didn't found aimingTarget in the list of alive robots.
		if (aimingTarget != null && !aliveTargets.containsRobot(aimingTarget.getState().getName())) {
			if (!isStartedAim() || isStartedFire()) {
				aimingTarget = null;
			}
		}
		aliveTargets = new TargetSet<>();
	}

	protected boolean isStartedAim() {
		return currentFireStatus.getNumValue() >= FireStatus.STARTED_AIM.getNumValue();
	}

	private boolean isFinishScanRobot() {
		return getRadarTurnRemaining() == 0;
	}

	private void setMoveAwayAfterHitRobot(HitRobotEvent hitRobot) {
		reverseDirection();
		setTurnLeft(30);				
		setAhead(MoveHelper.DEFAULT_DISTANCE);
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
			Color bulletColor = predicted.getPredictStrategy().getPredictBulletColor();
			// TODO set bullet color make robot slow a step (tick)???
			setBulletColor(bulletColor);
			setFire(predicted.getBestFirePoint().getFirePower());

			currentFireStatus = FireStatus.STARTED_FIRE;
		}
	}

	protected void setMoveToNearestTarget() {
		PredictedTarget target = findNearestAliveTargetByDistance();
		if (target != null) {
			setMoveCloseToTarget(target.getState().getPosition());
			printStatus("MoveCloseToTarget "+target.getState().getName()+""+target.getState().getPosition());
		} else {
			setMoveToOtherSideOfBattleField();
			printStatus("MoveToOtherSideOfBattleField");
		}
	}

	protected PredictedTarget findNearestAliveTargetByDistance() {
		PredictedTarget nearestTarget = null;
		double nearestDistance = Double.MAX_VALUE;
		List<PredictedTarget> targets = this.aliveTargets.list();
		for (PredictedTarget itarget : targets) {
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

	/**
	 * shorter aim time
	 * 
	 * @param targetStatus
	 * @return
	 */
	protected boolean isBetterPredictThanAimingTarget(PredictedTarget newTarget) {
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

	protected void updateAimingTarget(PredictedTarget target) {
		this.setAimingTarget(target);
	}

	protected void setAimTo(PredictedTarget target) {
		double gunTurnRightDirection;
		if (target.getPredicted().isFoundBestPoint()) {
			gunTurnRightDirection = target.getPredicted().getAimResult().getGunTurnRightDirection();
		} else {
			gunTurnRightDirection = target.getPredicted().getFirstAimEstimation().getGunTurnRightDirection();
		}
		setTurnGunRight(gunTurnRightDirection);
		currentFireStatus = FireStatus.STARTED_AIM;
	}

	/**
	 * @return aiming target
	 */
	protected boolean isAiming() {
		return currentFireStatus == FireStatus.STARTED_AIM;
	}

	protected boolean isFinishAim() {
		if (getGunTurnRemaining() == 0) {
			currentFireStatus = FireStatus.AIMED;
		}
		return currentFireStatus.getNumValue() >= FireStatus.AIMED.getNumValue();
	}

	protected boolean isStartedFire() {
		return currentFireStatus.getNumValue() >= FireStatus.STARTED_FIRE.getNumValue();
	}

	protected boolean isStandStill() {
		//TODO we may mistunderstand that it stands still with hit robot or hit wall.
		return (getDistanceRemaining() == 0 || getVelocity() == 0);
	}

	protected PredictedTarget updateStateToCorrespondingTarget(BaseRobotState targetState) {
		PredictedTarget target = new PredictedTarget(targetState);
		this.aliveTargets.set(target);
		return target;
	}

	protected void printNewPredict(PredictedAimAndFireResult newPredicted) {
		if (!newPredicted.getPredictStrategy().getClass().equals(LinearPredictStrategy.class)) {
			String msg = "Debug predict not linear";
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

	public PredictedTarget getAimingTarget() {
		return aimingTarget;
	}

	public void setAimingTarget(PredictedTarget aimingTarget) {
		// this.aimingTarget = SerializationUtils.clone(aimingTarget);
		// TODO copy properties.
		this.aimingTarget = new PredictedTarget(aimingTarget.getState());
		this.aimingTarget.setPredicted(aimingTarget.getPredicted());
	}
}