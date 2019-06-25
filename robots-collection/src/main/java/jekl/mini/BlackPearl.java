/*
 * Created on Aug 4, 2003
 * http://old.robowiki.net/robowiki?BlackPearl/Code
 */
package jekl.mini;
import java.awt.geom.*;
import java.io.*;
import java.util.zip.*;
import robocode.*;
import robocode.util.Utils;

public class BlackPearl extends AdvancedRobot {
	private static final double 	MAX_STAND_OFF_DISTANCE 	= 600D;
	private static final double 	MIN_STAND_OFF_DISTANCE 	= 500D;
	private static final double 	GUESS_FACTORS 		= 23D;
	private static final double	MIDDLE_FACTOR		= (GUESS_FACTORS -1) / 2;
	private static final double	WALL_CURVE_DISTANCE	= 180D;					//pixels
	private static final int	CLOSE_IN		= -1;
	private static final int	BACK_OFF		= 1;
	private static Rectangle2D.Double field;
	private static int statBuffer[][][][][][];
	private static String targetName;
	private static int hits;
	private int direction = 1;
	private int eDirection;
	private double eEnergy = 100D;
	private double eX, eY, eDelta, eAbsBearing, eVelocity, eDistance, eHeading;
	private boolean flatten = (hits > 2);
	private double eLastShot = 3;
	private static int ticksSinceReverse = 0;
	int lastLatVelIndex = 0;

	public void run() {
		//setColors(java.awt.Color.MAGENTA, java.awt.Color.LIGHT_GRAY, java.awt.Color.RED);
		field = new Rectangle2D.Double(18, 18, getBattleFieldWidth() - 36, getBattleFieldHeight() - 36);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		do {
			turnRadarRightRadians(Double.POSITIVE_INFINITY);
		} while(true);
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		//Gather target information
		targetName = e.getName();
		
		if (Math.abs(eVelocity) > Math.abs(eVelocity = e.getVelocity())) {
			ticksSinceReverse = 0;
		}
		double latVel = (eVelocity) * Math.sin(eHeading = e.getHeadingRadians() - (eAbsBearing = Utils.normalRelativeAngle(getHeadingRadians() + (e.getBearingRadians()))));
		eX = getX() + Math.sin(eAbsBearing) * (eDistance = e.getDistance());
		eY = getY() + Math.cos(eAbsBearing) * eDistance;

		//Keep the sign of the direction last used if latVel is zero
		if (latVel != 0) {
			eDirection = (int)(Math.abs(latVel) / latVel);
		}

		//Set the indicies for shooting
		int distanceIndex = (int)Math.min(4, eDistance / 180D);
		
		//Reverse chance
		if ((eDelta = eEnergy - (eEnergy = e.getEnergy())) > 0 && eDelta <= 3) {
			eLastShot = eDelta;
		}
		
		//Set the firePower
		double firePower = (distanceIndex <=1 ? 3.0 : 1.9);
		//double firePower = (getEnergy() > 0 ? 3.0 : 0);

		//MoveController
		//This is an adaptation from Raiko. If you can't beat 'em, join 'em		
		double distDelta = 0.02 + Math.PI/2 + (e.getDistance() > 400 ? -.1 : .5);
		double theta = 0.5952*bulletV(eLastShot)/eDistance;
		if ( (Math.random() > Math.pow(theta, theta)  && flatten)|| distDelta < Math.PI/4 || (distDelta < Math.PI/3 && eDistance < 400)){
		//double theta = .64 * bulletV(eLastShot)/eDistance;
		//if ((Math.random() > Math.pow(theta, theta) && flatten)) {
			direction = -direction;
		}

		//Finally move
		goTo(getDestination(new Point2D.Double(getX(), getY()),direction));

		//Radar
		setTurnRadarRightRadians(Math.sin(eAbsBearing - getRadarHeadingRadians()));

		//Gun 
		//Initialize the statbuffer
		if (statBuffer == null) {
			statBuffer = (int[][][][][][]) readObject(targetName);
		}

		//Retrieve the current stat segment to use
		int stats[] = statBuffer[getOutIndex()][lastLatVelIndex][(lastLatVelIndex = ((int)Math.abs(latVel) / 2))][Math.min(4, ++ticksSinceReverse/10)][distanceIndex];

		//Get the current best Guess Factor to shoot at
		int bestIndex = (int)MIDDLE_FACTOR;
		for (int i = 0; i < stats.length ; i++) {
			if (stats[i] > stats[bestIndex])
				bestIndex = i;
		}
		

		//Aim the gun
		//Original
		setTurnGunRightRadians(Utils.normalRelativeAngle(eAbsBearing - getGunHeadingRadians() + (getGuessAngle(bestIndex, firePower))));

		if (firePower > 0) {
			setFire(firePower);
			Wave w = new Wave();
			w.guessFactors = stats;
			w.wDirection = eDirection;
			w.bulletVel=bulletV(firePower);
			w.dist+=w.bulletVel;
			w.shotOrigin = new Point2D.Double(getX(), getY());
			w.startingAbsTargetBearing = eAbsBearing;
			w.maxAnglePossible = Math.asin(8.0 / w.bulletVel);
			addCustomEvent(w);
		}
		//lastLatVelIndex = latVelIndex;
	}
	
	// Check if we are hit with full lead aim
	// Adapted from Axe's Musashi: http://robowiki.net/?Musashi
	public void onHitByBullet(HitByBulletEvent e) {
		//if (ticksSinceReverse > (eDistance / e.getVelocity()) && !flatten) {
		if (!flatten) {
			flatten = (++hits > 0);
		}
	}

	// Save the data if I win the round
	public void onWin(WinEvent e) {
		try {
			ObjectOutputStream  oos = new ObjectOutputStream(new GZIPOutputStream(new RobocodeFileOutputStream(getDataFile(targetName))));
			oos.writeObject(statBuffer);
			oos.close();
		} catch (IOException ex) {
			//Should do something here but alas it is too big
			//e.printStackTrace();
		}
	}

	// Figures the Guess Angle Radians to offset the gun by
	private double getGuessAngle(int guessIndex, double firePower) {
		if (eEnergy == 0.0) //If the enemy bot is disabled, fire directly at it.
			return 0.0;
		return (guessIndex - MIDDLE_FACTOR) / MIDDLE_FACTOR * Math.asin(8.0 / bulletV(firePower)) * eDirection;
	}

	// Method to read data from disc. Taken from SandboxMini
	Object readObject(String fileName) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(getDataFile(fileName))));
			Object o = ois.readObject();
			ois.close();
			return o;
		} catch (Exception e) {
			//int stats[] = statBuffer[getOutIndex()][lastLatVelIndex][latVelIndex][Math.min(4, ++ticksSinceReverse/5)][distanceIndex];
			return new int[5][5][5][5][5][(int)GUESS_FACTORS];
			//Should do something here but alas it is too big
			//e.printStackTrace();
		}
	}
	
	//Retrieve the bullets velocity
	private double bulletV (double shotPower) {
		return (20 - (3 * shotPower));
	}
	
	//Gets the point we are going to aim for in the next tick
	private Point2D.Double getDestination(Point2D.Double startFrom, int direct) {
		double fudge = 0;	//Adjustment to rotate the heading by
		Point2D.Double dest = new Point2D.Double();
		//If we are  in the "sweet spot" no need to move in or out
		double dir = eAbsBearing + (Math.PI / 2) + (.2618 * direct * (eDistance > MAX_STAND_OFF_DISTANCE ? CLOSE_IN : (eDistance < MIN_STAND_OFF_DISTANCE ? BACK_OFF : 0)));
		int counter = 0;
		//Rotate the position we are heading for until it is in the field
		do {
			dest.setLocation(startFrom.x + Math.sin(dir + (fudge * direct)) * WALL_CURVE_DISTANCE * direct, 
				startFrom.y + Math.cos(dir + (fudge * direct)) * (WALL_CURVE_DISTANCE * direct));
			fudge-=.0085;	//.5 degrees radians
		} while (!field.contains(dest) && ++counter < 180);
		if (counter >= 55 && flatten) {
			direction = -direction;
		}
		return dest;
	}

	//This moves me about the field
	private void goTo(Point2D destination) {
		ticksSinceReverse++;
		double angle = Utils.normalRelativeAngle(Math.atan2((destination.getX() - getX()), (destination.getY()- getY())) - getHeadingRadians());
		double turnAngle = Math.atan(Math.tan(angle));
		setTurnRightRadians(turnAngle);
		setAhead(destination.distance(getX(), getY()) * (angle == turnAngle ? 1 : -1) * Math.cos(turnAngle));
	}
		
	//Return the wall index
	private int getOutIndex() {
		int i = 0;
		do {
			
		} while (++i < 4 && field.contains((eX + (Math.sin(eHeading)* (45.0 * i) * eDirection)), (eY + (Math.cos(eHeading) * (45.0 * i) * eDirection))));
		return (i);
	}
	
	// Jekyl's implementation of a Wave (mostly)
	public class Wave extends Condition {
		public Point2D.Double shotOrigin;
		public double bulletVel;
		public double dist;
		public double startingAbsTargetBearing;
		public double maxAnglePossible;
		public int[] guessFactors;
		public int wDirection;

		public boolean test() {
			if ((dist+=bulletVel) >= shotOrigin.distance(eX,eY) - 18) {
				double currentAbsBearingFromShotOrigin = Math.atan2(eX - shotOrigin.getX(), eY - shotOrigin.getY());
				guessFactors[(int)Math.max(0, Math.min((GUESS_FACTORS - 1),(int) Math.round(((((Utils.normalRelativeAngle(currentAbsBearingFromShotOrigin - startingAbsTargetBearing) * wDirection) / maxAnglePossible) * MIDDLE_FACTOR) + MIDDLE_FACTOR))))]++;
				removeCustomEvent(this);
			}
			return false;
		}
	}  //End Wave
}