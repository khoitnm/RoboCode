package org.tnmk.robocode.common.predictor.ibm;

import org.tnmk.robocode.common.math.Point;
/**
 * http://www.ibm.com/developerworks/library/j-pred-targeting/
 * @author Khoi
 */
public class Intercept {
	public static final double ROBOT_RADIUS = Double.MIN_NORMAL;//I still don't know what this number is
	
	public Point impactPoint = new Point(0, 0);
	public double bulletHeading_deg;

	protected Point bulletStartingPoint = new Point();
	protected Point targetStartingPoint = new Point();
	public double targetHeading;
	public double targetVelocity;
	public double bulletPower;
	public double angleThreshold;
	public double distance;

	protected double impactTime;
	protected double angularVelocity_rad_per_sec;

	public void calculate(

	// Initial bullet position x coordinate
	        double xb,
	        // Initial bullet position y coordinate
	        double yb,
	        // Initial target position x coordinate
	        double xt,
	        // Initial target position y coordinate
	        double yt,
	        // Target heading
	        double tHeading,
	        // Target velocity
	        double vt,
	        // Power of the bullet that we will be firing
	        double bPower,
	        // Angular velocity of the target
	        double angularVelocity_deg_per_sec) {
		angularVelocity_rad_per_sec = Math.toRadians(angularVelocity_deg_per_sec);

		bulletStartingPoint.set(xb, yb);
		targetStartingPoint.set(xt, yt);

		targetHeading = tHeading;
		targetVelocity = vt;
		bulletPower = bPower;
		double vb = 20 - 3 * bulletPower;

		double dX, dY;

		// Start with initial guesses at 10 and 20 ticks
		impactTime = getImpactTime(10, 20, 0.01);
		impactPoint = getEstimatedPosition(impactTime);

		dX = (impactPoint.x - bulletStartingPoint.x);
		dY = (impactPoint.y - bulletStartingPoint.y);

		distance = Math.sqrt(dX * dX + dY * dY);

		bulletHeading_deg = Math.toDegrees(Math.atan2(dX, dY));
		angleThreshold = Math.toDegrees(Math.atan(ROBOT_RADIUS / distance));
	}

	protected Point getEstimatedPosition(double time) {

		double x = targetStartingPoint.x + targetVelocity * time * Math.sin(Math.toRadians(targetHeading));
		double y = targetStartingPoint.y + targetVelocity * time * Math.cos(Math.toRadians(targetHeading));
		return new Point(x, y);
	}

	private double f(double time) {

		double vb = 20 - 3 * bulletPower;

		Point targetPosition = getEstimatedPosition(time);
		double dX = (targetPosition.x - bulletStartingPoint.x);
		double dY = (targetPosition.y - bulletStartingPoint.y);

		return Math.sqrt(dX * dX + dY * dY) - vb * time;
	}

	private double getImpactTime(double t0, double t1, double accuracy) {

		double X = t1;
		double lastX = t0;
		int iterationCount = 0;
		double lastfX = f(lastX);

		while ((Math.abs(X - lastX) >= accuracy) && (iterationCount < 15)) {

			iterationCount++;
			double fX = f(X);

			if ((fX - lastfX) == 0.0)
				break;

			double nextX = X - fX * (X - lastX) / (fX - lastfX);
			lastX = X;
			X = nextX;
			lastfX = fX;
		}

		return X;
	}
}
