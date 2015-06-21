package org.tnmk.robocode.common.helper;

import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.predictor.self.PredictHelper.PredictedAimAndFireSuccess;

import robocode.Rules;

public class GunHelper {
	public static final int BULLET_POWER_01 = 1;
	public static final int BULLET_POWER_02 = 2;
	public static final int BULLET_POWER_03 = 3;
	private static final int AIMFIRE_TOO_LONG = 40;
	/**
	 * @param power
	 *            bullet power
	 * @return the damage which will cause by power
	 */
	public static int reckonDamage(double power) {
		power = Math.max(power, Rules.MAX_BULLET_POWER);
		return (int) (4 * power + 2 * Math.max(power - 1, 0));
	}
	public enum FireDistance{
		STEPS_VERY_SHORT(8), STEPS_SHORT(15), STEPS_MIDDLE(22), STEPS_LONG(30), STEPS_TOO_LONG(40),
		DISTANCE_VERY_SHORT(100), DISTANCE_SHORT(200), DISTANCE_MIDDLE(350), DISTANCE_LONG(500), DISTANCE_TOO_LONG(650);
		private int numValue;
		FireDistance(int numValue){
			this.numValue = numValue;
		}
		public int getNumValue() {
	        return numValue;
        }
		public void setNumValue(int numValue) {
	        this.numValue = numValue;
        }

		
	}
	public static boolean isTooFarFromTarget(PredictedAimAndFireSuccess predicted){
		int fireSteps = predicted.getAimAndFire().getTotalSteps();
		return (fireSteps >= FireDistance.STEPS_TOO_LONG.getNumValue());
	}
	/**
	 * We don't want to use redundant power to shoot a low energy robot.
	 * 
	 * Damage = 6*power - 2 Damage should less than (remainEnergey + 1) => Power
	 * should less than (remainEnergy + 2)/6
	 * 
	 * @param targetRemainEnergy
	 *            the remain energy of target robot
	 * @return the maximum power necessary to shoot a robot with remain energy
	 */
	public static int reckonMaxNecessaryPower(double targetRemainEnergy) {
		if (targetRemainEnergy <= 4) {
			return BULLET_POWER_01;
		} else if (targetRemainEnergy <= 10) {
			return BULLET_POWER_02;
		} else {
			return BULLET_POWER_03;
		}
	}

	/**
	 * @param degreeAngle
	 *            never more than 180
	 */
	public static int reckonGunTurningSteps(double degreeAngle) {
		return (int) Math.ceil(degreeAngle / Rules.GUN_TURN_RATE);
	}

	public static int reckonBulletSteps(double distance, int firePower) {
		return (int) Math.ceil((distance / (20 - (3 * firePower))));
	}
	/**
	 * @param firePower
	 * @param gunHeading
	 * @param gunX
	 * @param gunY
	 * @param targetX
	 * @param targetY
	 * @return steps to turnGunHeading and bullet fly
	 */
	public static AimAndFireResult reckonStepsToAimAndFire(int firePower, double gunHeading, double gunX, double gunY, double targetX, double targetY) {
		AimAndFireResult result = new AimAndFireResult();
		result.turnRightAngle = MathUtils.calculateTurnRightAngleToTarget(gunHeading, gunX, gunY, targetX, targetY);
		result.distance = MathUtils.distance(gunX, gunY, targetX, targetY);
		result.aimSteps = reckonGunTurningSteps(result.turnRightAngle);
		result.fireSteps  = reckonBulletSteps(result.distance, firePower);
		return result;
	}
	public static class AimAndFireResult{
		/**
		 * gun turn right angle
		 */
		private double turnRightAngle;
		/**
		 * distance from aimed position to fired position.
		 */
		private double distance;
		private int aimSteps;
		private int fireSteps;
		
		
		public int getAimSteps() {
			return aimSteps;
		}

		public void setAimSteps(int aimSteps) {
			this.aimSteps = aimSteps;
		}

		public int getFireSteps() {
			return fireSteps;
		}

		public void setFireSteps(int fireSteps) {
			this.fireSteps = fireSteps;
		}

		public int getTotalSteps() {
			return aimSteps + fireSteps;
		}
		
		public double getTurnRightAngle() {
			return turnRightAngle;
		}
		public void setTurnRightAngle(double turnRightAngle) {
			this.turnRightAngle = turnRightAngle;
		}
		public double getDistance() {
			return distance;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
	}
}
