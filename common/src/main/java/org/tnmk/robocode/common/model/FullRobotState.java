package org.tnmk.robocode.common.model;


import org.tnmk.robocode.common.robot.ModernRobot;
import robocode.Rules;
import robocode.util.Utils;


public class FullRobotState extends BaseRobotState{
    private static final long serialVersionUID = -9122595279962389587L;
    
	public static final double DISTANCE_REMAINING_UNKNOWN = Double.MAX_VALUE;
	public static final double TURN_REMAINING_UNKOWN = Double.MAX_VALUE;
	public static final double MAX_VELOCITY_UNKOWN = 0;
	public static final double MAX_TURNRATE_UNKOWN = 0;
	
	private double maxVelocity = MAX_VELOCITY_UNKOWN;
	private double distanceRemaining = DISTANCE_REMAINING_UNKNOWN;
	private double maxTurnRate = MAX_TURNRATE_UNKOWN;
	private double turnRemaining = TURN_REMAINING_UNKOWN;
	
	private double moveDirection = ModernRobot.DIRECTION_AHEAD;//We should put direction into this status to trace real direction when velocity is 0.
	
	public double getMoveAngle() {
		if (Utils.isNear(velocity, 0)){
			if (moveDirection > 0) {
				return heading;
			} else {
				double rs = heading - 180;
				return (rs + 360) % 360;// to ensure that 0 <= angle <= 360
			}
		}else{
			return super.getMoveAngle();
		}
	}
	
	/**
	 * If don't know remain distance, assume that it running with the same speed for the rest
	 * @param velocity
	 */
	public void setVelocity(double velocity) {
		this.velocity = velocity;
		if (this.distanceRemaining != DISTANCE_REMAINING_UNKNOWN){
			if (this.maxVelocity == MAX_VELOCITY_UNKOWN){
				this.maxVelocity = Rules.MAX_VELOCITY;
			}
		}else{
			this.maxVelocity = this.velocity;
		}
	}
	/**
	 * If don't know remain turning, assume that it won't turn.
	 * Else: it can turn, maxTurnRate must be not 0
	 * @param turnRemaining
	 */
	public void setTurnRemaining(double turnRemaining) {
		this.turnRemaining = turnRemaining;
		if (this.turnRemaining != TURN_REMAINING_UNKOWN){//then it can turn
			if (this.maxTurnRate == MAX_TURNRATE_UNKOWN){
				this.maxTurnRate = Rules.MAX_TURN_RATE;
			}
		}else{
			this.maxTurnRate = MAX_TURNRATE_UNKOWN;
		}
	}


	public double getDistanceRemaining() {
		return distanceRemaining;
	}
	public void setDistanceRemaining(double distanceRemaining) {
		this.distanceRemaining = distanceRemaining;
	}
	
	public double getTurnRemaining() {
		return turnRemaining;
	}

	public double getMaxVelocity() {
	    return maxVelocity;
    }
	public void setMaxVelocity(double maxVelocity) {
	    this.maxVelocity = maxVelocity;
    }
	public double getMaxTurnRate() {
	    return maxTurnRate;
    }
	public void setMaxTurnRate(double maxTurnRate) {
	    this.maxTurnRate = maxTurnRate;
    }
	public double getMoveDirection() {
	    return moveDirection;
    }
	public void setMoveDirection(double direction) {
	    this.moveDirection = direction;
    }

}
