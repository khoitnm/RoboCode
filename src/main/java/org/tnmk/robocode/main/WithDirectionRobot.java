package org.tnmk.robocode.main;

import java.io.Serializable;

import robocode.AdvancedRobot;
import robocode.util.Utils;

/**
 * @author Khoi With AdvancedRobot, the shooting time is different from basic Robot class.
 * 
 *         Term: + Bearing: the angle (degree) from pointA to pointB (or vectorA to vectorB). It can be an absolute bearing (compare to North axis) or relative bearing (compare to vectorA)
 */
public abstract class WithDirectionRobot extends AdvancedRobot implements Serializable {
    private static final long serialVersionUID = -6172244175878937185L;
    
	public static final int DIRECTION_AHEAD = 1;
	public static final int DIRECTION_BACK = -1;
	/**
	 * We cannot change move direction by changing velocity, but we can change movement direction by changing this value (using {@link #reverseDirection()}). This property doesn't have setter method. Never get use this property directly even inside this class. Only use getter or
	 * reverseDirection()
	 */
	private int moveDirection = DIRECTION_AHEAD;

	public WithDirectionRobot() {
		super();
	}

	@Override
	public void setAhead(double distance) {
		String msg = String.format("%s - \tsetAhead(%.1f)", getTime(), distance);
		System.out.println(msg);
		super.setAhead(getMoveDirection() * distance);
	}

	@Override
	public void setBack(double distance) {
		this.setAhead(-distance);
	}

	public void reverseDirection() {
		double velocity = getVelocity();
		if (!Utils.isNear(velocity, 0)) {
			String msg = String.format("Cannot change direction when velocity %.2f is different from 0.", velocity);
			throw new RuntimeException(msg);
		}
		this.moveDirection = -this.moveDirection;
	}

	/**
	 * This field is special, no setter method. This value is bounded to velocity.
	 * 
	 * @return
	 */
	public int getMoveDirection() {
		updateDirectionBaseOnVelocity();
		return moveDirection;
	}
	
	@Override
	public double getVelocity() {
		return updateDirectionBaseOnVelocity();
	}

	/**
	 * If direction is not mapped with current velocity, update it.
	 * @return current velocity;
	 */
	private double updateDirectionBaseOnVelocity() {
		// IMPORTANT: must use super.getVelocity here, never use this.getVelocity()!!!!
		double velocity = super.getVelocity();
		if (!Utils.isNear(velocity, 0)) {
			if (velocity > 0) {
				moveDirection = DIRECTION_AHEAD;
			} else {
				moveDirection = DIRECTION_BACK;
			}
		}
		return velocity;
	}
}