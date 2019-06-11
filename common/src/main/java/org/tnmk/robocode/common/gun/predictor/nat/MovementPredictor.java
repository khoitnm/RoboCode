package org.tnmk.robocode.common.gun.predictor.nat;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import robocode.Rules;
import robocode.util.Utils;

/**
 * http://robowiki.net/wiki/User:Nat/Free_code#Movement_Predictor Movement
 * Predictor, also known as Precise Predictor
 */
public class MovementPredictor {
	public static class PredictionStatus extends Point2D.Double {
		private static final long serialVersionUID = 4116202515905711057L;
		public final double heading, velocity;
		public final long time;

		public PredictionStatus(double x, double y, double h, double v, long t) {
			super(x, y);
			heading = h;
			velocity = v;
			time = t;
		}
	}

	/**
	 * Calculate next tick prediction status. This always simulate accelerate to
	 * max velocity.
	 * 
	 * @param status
	 *            beginning status
	 * @param goAngle
	 *            angle to move, in radians, absolute
	 * @param maxVelocity
	 *            max allowed velocity of robot
	 * @return predicted state next tick
	 */
	public static PredictionStatus predict(PredictionStatus status, double goAngle) {
		return predict(status, goAngle, 8d);
	}

	/**
	 * Calculate next tick prediction status. This always simulate accelerate to
	 * max velocity.
	 * 
	 * @param status
	 *            beginning status
	 * @param goAngle
	 *            angle to move, in radians, absolute
	 * @param maxVelocity
	 *            max allowed velocity of robot
	 * @return predicted state next tick
	 */
	public static PredictionStatus predict(PredictionStatus status, double goAngle, double maxVelocity) {

		int moveDir = 1;

		if (Math.cos(goAngle - status.heading) < 0) {
			moveDir = -1;
		}

		return _predict(status, goAngle, maxVelocity, Double.POSITIVE_INFINITY * moveDir);
	}

	/**
	 * Calculate predicted status for every ticks before it reach its
	 * destination.
	 * 
	 * @param status
	 *            beginning status
	 * @param goAngle
	 *            angle to move, in radians, absolute
	 * @param maxVelocity
	 *            max allowed velocity of robot
	 * @param distanceRemaining
	 *            remain distance before stop
	 * @return list of predicted status
	 */
	public static List<PredictionStatus> predict(PredictionStatus status, double goAngle, double maxVelocity, double distanceRemaining) {
		List<PredictionStatus> predicted = new ArrayList<PredictionStatus>(20);
		predicted.add(status);

		while (distanceRemaining > 0) {
			status = _predict(status, goAngle, maxVelocity, distanceRemaining);
			predicted.add(status);

			// Deduct the distance remaining by the velocity
			distanceRemaining -= status.velocity;
		}

		return predicted;
	}

	/**
	 * Calculate predicted status for every ticks until timer run out.
	 * 
	 * @param status
	 *            beginning status
	 * @param tick
	 *            time available to move
	 * @param goAngle
	 *            angle to move, in radians, absolute
	 * @param maxVelocity
	 *            max allowed velocity of robot
	 * @return list of predicted status
	 */
	public static List<PredictionStatus> predict(PredictionStatus status, int tick, double goAngle, double maxVelocity) {
		List<PredictionStatus> predicted = new ArrayList<PredictionStatus>(tick + 2);
		predicted.add(status);

		while (tick-- > 0) {
			status = predict(status, goAngle, maxVelocity);
			predicted.add(status);
		}

		return predicted;
	}

	/**
	 * Calculate predicted status for every ticks before it reach its
	 * destination, or until timer run out.
	 * 
	 * @param status
	 *            beginning status
	 * @param tick
	 *            time available to move
	 * @param goAngle
	 *            angle to move, in radians, absolute
	 * @param maxVelocity
	 *            max allowed velocity of robot
	 * @param distanceRemaining
	 *            remain distance before stop
	 * @return list of predicted status
	 */
	public static List<PredictionStatus> predict(PredictionStatus status, int tick, double goAngle, double maxVelocity, double distanceRemaining) {
		List<PredictionStatus> predicted = new ArrayList<PredictionStatus>(tick + 2);
		predicted.add(status);

		while (distanceRemaining > 0 && tick-- > 0) {
			status = _predict(status, goAngle, maxVelocity, distanceRemaining);
			predicted.add(status);

			// Deduct the distance remaining by the velocity
			distanceRemaining -= status.velocity;
		}

		return predicted;
	}

	/**
	 * Calculate next tick prediction status. This always simulate accelerate to
	 * max velocity.
	 * 
	 * @param status
	 *            beginning status
	 * @param goAngle
	 *            angle to move, in radians, absolute
	 * @param maxVelocity
	 *            max allowed velocity of robot
	 * @param distanceRemaining
	 *            the remaining distance
	 * @return predicted state next tick
	 */
	private static PredictionStatus _predict(PredictionStatus status, double goAngle, double maxVelocity, double distanceRemaining) {
		double x = status.x;
		double y = status.y;
		double heading = status.heading;
		double velocity = status.velocity;

		// goAngle here is absolute, change to relative bearing
		goAngle -= heading;

		// If angle is at back, consider change direction
		if (Math.cos(goAngle) < 0) {
			goAngle += Math.PI;
		}

		// Normalize angle
		goAngle = Utils.normalRelativeAngle(goAngle);

		// Max turning rate, taken from Rules class
		double maxTurning = Math.toRadians(10d - 0.75 * velocity);
		heading += limit(-maxTurning, goAngle, maxTurning);

		// Get next velocity
		velocity = getVelocity(velocity, maxVelocity, distanceRemaining);

		// Calculate new location
		x += Math.sin(heading) * velocity;
		y += Math.cos(heading) * velocity;

		// return the prediction status
		return new PredictionStatus(x, y, heading, velocity, status.time + 1l);
	}

	/**
	 * This function return the new velocity base on the maximum velocity and
	 * distance remaining. This is copied from internal bug-fixed Robocode
	 * engine.
	 * 
	 * @param currentVelocity
	 *            current velocity of the robot
	 * @param maxVelocity
	 *            maximum allowed velocity of the robot
	 * @param distanceRemaining
	 *            the remaining distance to move
	 * @return velocity for current tick
	 */
	public static double getVelocity(double currentVelocity, double maxVelocity, double distanceRemaining) {
		if (distanceRemaining < 0) {
			return -getVelocity(-currentVelocity, maxVelocity, -distanceRemaining);
		}

		double newVelocity = currentVelocity;

		final double maxSpeed = Math.abs(maxVelocity);
		final double currentSpeed = Math.abs(currentVelocity);

		// Check if we are decelerating, i.e. if the velocity is negative.
		// Note that if the speed is too high due to a new max. velocity, we
		// must also decelerate.
		if (currentVelocity < 0 || currentSpeed > maxSpeed) {
			// If the velocity is negative, we are decelerating
			newVelocity = currentSpeed - Rules.DECELERATION;

			// Check if we are going from deceleration into acceleration
			if (newVelocity < 0) {
				// If we have decelerated to velocity = 0, then the remaining
				// time must be used for acceleration
				double decelTime = currentSpeed / Rules.DECELERATION;
				double accelTime = (1 - decelTime);

				// New velocity (v) = d / t, where time = 1 (i.e. 1 turn).
				// Hence, v = d / 1 => v = d
				// However, the new velocity must be limited by the max.
				// velocity
				newVelocity = Math.min(maxSpeed, Math.min(Rules.DECELERATION * decelTime * decelTime + Rules.ACCELERATION * accelTime * accelTime, distanceRemaining));

				// Note: We change the sign here due to the sign check later
				// when returning the result
				currentVelocity *= -1;
			}
		} else {
			// Else, we are not decelerating, but might need to start doing so
			// due to the remaining distance

			// Deceleration time (t) is calculated by: v = a * t => t = v / a
			final double decelTime = currentSpeed / Rules.DECELERATION;

			// Deceleration time (d) is calculated by: d = 1/2 a * t^2 + v0 * t
			// + t
			// Adding the extra 't' (in the end) is special for Robocode, and v0
			// is the starting velocity = 0
			final double decelDist = 0.5 * Rules.DECELERATION * decelTime * decelTime + decelTime;

			// Check if we should start decelerating
			if (distanceRemaining <= decelDist) {
				// If the distance < max. deceleration distance, we must
				// decelerate so we hit a distance = 0

				// Calculate time left for deceleration to distance = 0
				double time = distanceRemaining / (decelTime + 1); // 1 is added
				// here due
				// to the extra 't'
				// for Robocode

				// New velocity (v) = a * t, i.e. deceleration * time, but not
				// greater than the current speed

				if (time <= 1) {
					// When there is only one turn left (t <= 1), we set the
					// speed to match the remaining distance
					newVelocity = Math.max(currentSpeed - Rules.DECELERATION, distanceRemaining);
				} else {
					// New velocity (v) = a * t, i.e. deceleration * time
					newVelocity = time * Rules.DECELERATION;

					if (currentSpeed < newVelocity) {
						// If the speed is less that the new velocity we just
						// calculated, then use the old speed instead
						newVelocity = currentSpeed;
					} else if (currentSpeed - newVelocity > Rules.DECELERATION) {
						// The deceleration must not exceed the max.
						// deceleration.
						// Hence, we limit the velocity to the speed minus the
						// max. deceleration.
						newVelocity = currentSpeed - Rules.DECELERATION;
					}
				}
			} else {
				// Else, we need to accelerate, but only to max. velocity
				newVelocity = Math.min(currentSpeed + Rules.ACCELERATION, maxSpeed);
			}
		}

		// Return the new velocity with the correct sign. We have been working
		// with the speed, which is always positive
		return (currentVelocity < 0) ? -newVelocity : newVelocity;
	}

	public static final double limit(double a, double b, double c) {
		return Math.max(a, Math.min(b, c));
	}
}