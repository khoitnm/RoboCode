package org.tnmk.robocode.common.helper;

import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.model.AimAndFireResult;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictedAimAndFireResult;

import robocode.Rules;

public class GunHelper {
	public static final int BULLET_POWER_01 = 1;
	public static final int BULLET_POWER_02 = 2;
	public static final int BULLET_POWER_03 = 3;
	/**
	 * @param power
	 *            bullet power
	 * @return the damage which will cause by power
	 */
	public static int reckonDamage(double power) {
		power = Math.max(power, Rules.MAX_BULLET_POWER);
		return (int) (4 * power + 2 * Math.max(power - 1, 0));
	}
	public static boolean isShouldFireBySteps(int firePower, int steps){
		if (firePower == BULLET_POWER_01){
			return steps <= FireDistance.STEPS_TOO_LONG.getNumValue();
		}else if (firePower == BULLET_POWER_02){
			return steps <= FireDistance.STEPS_MIDDLE.getNumValue();
		}else{
			return steps <= FireDistance.STEPS_VERY_SHORT.getNumValue();
		}
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
	public static boolean isTooFarFromTarget(PredictedAimAndFireResult predicted){
		Integer fireSteps = predicted.getTotalSteps();
		if (fireSteps == null) return true;
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
		result.setTurnRightAngle(GeoMathUtils.calculateTurnRightDirectionToTarget(gunHeading, gunX, gunY, targetX, targetY));
		result.setDistance(GeoMathUtils.distance(gunX, gunY, targetX, targetY));
		result.setAimSteps(reckonGunTurningSteps(result.getTurnRightAngle()));
		result.setFireSteps(reckonBulletSteps(result.getDistance(), firePower));
		return result;
	}
	public static int findFirePowerByDistance(double fireDistance) {
		if (fireDistance < GunHelper.FireDistance.DISTANCE_VERY_SHORT.getNumValue()){
			return GunHelper.BULLET_POWER_03;
		}else if (fireDistance < GunHelper.FireDistance.DISTANCE_MIDDLE.getNumValue()){
			return GunHelper.BULLET_POWER_02;
		}else{
			return GunHelper.BULLET_POWER_01;
		}
    }



}
