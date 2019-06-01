package org.tnmk.robocode.common.predictor.albert;

public class MoveSimStat {
	public double x;
	public double y;
	public double velocity;
	public double heading;
	/**
	 * The different of predicted heading compare to heading of previous step. 
	 */
	public double differentHeading;
 
 
	public MoveSimStat(double x, double y, double v, double h, double w) {
		this.x = x; this.y = y; this.velocity = v; this.heading = h; this.differentHeading = w; 
	}
 
 
 
}