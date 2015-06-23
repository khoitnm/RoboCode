package org.tnmk.robocode.common.predictor.albert;

import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.util.Utils;

public class MoveSim {
	private double systemMaxTurnRate = Math.toRadians(10.0);
	private double systemMaxVelocity = Rules.MAX_VELOCITY;
	private double maxDeceleration = Rules.DECELERATION;//stop acceleration (speed -= 2)
	private double maxAcceleration = Rules.ACCELERATION;//start acceleration (speed +=1)

	public double defaultMaxTurnRate = Rules.MAX_TURN_RATE;
	public double defaultMaxVelocity = Rules.MAX_VELOCITY;

	public MoveSim() {
	};

	public MoveSimStat[] futurePos(int steps, AdvancedRobot b) {
		return futurePos(steps, b, defaultMaxVelocity, defaultMaxTurnRate);
	}

	public MoveSimStat[] futurePos(int steps, AdvancedRobot robotState, double maxVel, double maxTurnRate) {
		return futurePos(steps, robotState.getX(), robotState.getY(), robotState.getVelocity(), maxVel, robotState.getHeadingRadians(), robotState.getDistanceRemaining(), robotState.getTurnRemainingRadians(), maxTurnRate, robotState.getBattleFieldWidth(), robotState.getBattleFieldHeight());
	}
	public MoveSimStat[] futurePos(int steps, double x, double y, double velocity, double maxVelocity, double headingRadian, double distanceRemaining, double turnRemainingRadian, double maxTurnRate, double battleFieldW, double battleFieldH) {
		// maxTurnRate in degrees
		MoveSimStat[] pos = new MoveSimStat[steps];
		double acceleration = 0;
		boolean slowingDown = false;
		double moveDirection;

		maxTurnRate = Math.toRadians(maxTurnRate);
		if (distanceRemaining == 0)
			moveDirection = 0;
		else if (distanceRemaining < 0.0)
			moveDirection = -1;
		else
			moveDirection = 1;

		// heading, accel, velocity, distance
		for (int i = 0; i < steps; i++) {
			// heading
			double lastHeading = headingRadian;
			double turnRate = Math.min(maxTurnRate, ((0.4 + 0.6 * (1.0 - (Math.abs(velocity) / systemMaxVelocity))) * systemMaxTurnRate));
			if (turnRemainingRadian > 0.0) {
				if (turnRemainingRadian < turnRate) {
					headingRadian += turnRemainingRadian;
					turnRemainingRadian = 0.0;
				} else {
					headingRadian += turnRate;
					turnRemainingRadian -= turnRate;
				}
			} else if (turnRemainingRadian < 0.0) {
				if (turnRemainingRadian > -turnRate) {
					headingRadian += turnRemainingRadian;
					turnRemainingRadian = 0.0;
				} else {
					headingRadian -= turnRate;
					turnRemainingRadian += turnRate;
				}
			}
			headingRadian = Utils.normalAbsoluteAngle(headingRadian);
			// movement
			if (distanceRemaining != 0.0 || velocity != 0.0) {
				// lastX = x; lastY = y;
				if (!slowingDown && moveDirection == 0) {
					slowingDown = true;
					if (velocity > 0.0)
						moveDirection = 1;
					else if (velocity < 0.0)
						moveDirection = -1;
					else
						moveDirection = 0;
				}
				double desiredDistanceRemaining = distanceRemaining;
				if (slowingDown) {
					if (moveDirection == 1 && distanceRemaining < 0.0)
						desiredDistanceRemaining = 0.0;
					else if (moveDirection == -1 && distanceRemaining > 1.0)
						desiredDistanceRemaining = 0.0;
				}
				double slowDownVelocity = (double) (int) (maxDeceleration / 2.0 * ((Math.sqrt(4.0 * Math.abs(desiredDistanceRemaining) + 1.0)) - 1.0));
				if (moveDirection == -1)
					slowDownVelocity = -slowDownVelocity;
				if (!slowingDown) {
					if (moveDirection == 1) {
						if (velocity < 0.0)
							acceleration = maxDeceleration;
						else
							acceleration = maxAcceleration;
						if (velocity + acceleration > slowDownVelocity)
							slowingDown = true;
					} else if (moveDirection == -1) {
						if (velocity > 0.0)
							acceleration = -maxDeceleration;
						else
							acceleration = -maxAcceleration;
						if (velocity + acceleration < slowDownVelocity)
							slowingDown = true;
					}
				}
				if (slowingDown) {
					if (distanceRemaining != 0.0 && Math.abs(velocity) <= maxDeceleration && Math.abs(distanceRemaining) <= maxDeceleration)
						slowDownVelocity = distanceRemaining;
					double perfectAccel = slowDownVelocity - velocity;
					if (perfectAccel > maxDeceleration)
						perfectAccel = maxDeceleration;
					else if (perfectAccel < -maxDeceleration)
						perfectAccel = -maxDeceleration;
					acceleration = perfectAccel;
				}
				if (velocity > maxVelocity || velocity < -maxVelocity)
					acceleration = 0.0;
				velocity += acceleration;
				if (velocity > maxVelocity)
					velocity -= Math.min(maxDeceleration, velocity - maxVelocity);
				if (velocity < -maxVelocity)
					velocity += Math.min(maxDeceleration, -velocity - maxVelocity);
				double dx = velocity * Math.sin(headingRadian);
				double dy = velocity * Math.cos(headingRadian);
				x += dx;
				y += dy;
				// boolean updateBounds = false;
				// if (dx != 0.0 || dy != 0.0) updateBounds = true;
				if (slowingDown && velocity == 0.0) {
					distanceRemaining = 0.0;
					moveDirection = 0;
					slowingDown = false;
					acceleration = 0.0;
				}
				// if (updateBounds) updateBoundingBox();
				distanceRemaining -= velocity;
				if (x < 18 || y < 18 || x > battleFieldW - 18 || y > battleFieldH - 18) {
					distanceRemaining = 0;
					turnRemainingRadian = 0;
					velocity = 0;
					moveDirection = 0;
					x = Math.max(18, Math.min(battleFieldW - 18, x));
					y = Math.max(18, Math.min(battleFieldH - 18, y));
				}
			}
			// add position
			pos[i] = new MoveSimStat(x, y, velocity, headingRadian, Utils.normalRelativeAngle(headingRadian - lastHeading));
		}
		return pos;
	}

}
