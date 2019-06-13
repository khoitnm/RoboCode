package wiki.mako;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import robocode.*;

// MakoHT - Prey sighted - prey eaten.
// Targeting by PEZ.
// Movement, HT (Hit This!) by Kawigi using SandboxFlattener movement.
//
// http://robowiki.dyndns.org/MakoHT 
//
// This code is open source. Released under GPL-ish terms. Meaning you can use the
// code in your own robots. Meaning you should feel obliged to share any improvements.
// Share it back on MakoHT's RoboWiki home.

// $Id: MakoHT.java,v 1.1 2003/04/15 05:16:45 peter Exp $ (there are newer version 1.2.2 - Ranks #53 on EternalRumble April 13 2003)

public class MakoHT extends AdvancedRobot {
    private static Point2D location = new Point2D.Double();
    private static Point2D oldLocation = new Point2D.Double();
    private static Point2D enemyLocation = new Point2D.Double();
    private static Point2D oldEnemyLocation = new Point2D.Double();
    private static Point2D impactLocation = new Point2D.Double();
    private static Rectangle2D fieldRectangle;
    private static boolean haveEnemy;
    private static double enemyDistance;
    private static double absoluteBearing;
    private static double deltaBearing;
    private static double meanAngularFactorLeft = 9.0;
    private static double meanAngularFactorStraight = 1.0;
    private static double meanAngularFactorRight = 9.0;
    private static double meanOffsetFactorLeft = 0.0;
    private static double meanOffsetFactorStraight = 0.0;
    private static double meanOffsetFactorRight = 0.0;
    private static double[] gunFactorLeft = new double[2];
    private static double[] gunFactorStraight = new double[2];
    private static double[] gunFactorRight = new double[2];
    private static int wins;

    //variables for flattener movement:
    private static double[] gainedenergy, lostenergy, hisgainedenergy, hislostenergy, hits, shots, hishits, hisshots;
    private double currentDirection=1, currentEnergy=100, currentVBound = 0;
    private long nextTime = 0;

    public void run() {
	fieldRectangle = new Rectangle2D.Double(0, 0 , getBattleFieldWidth(), getBattleFieldHeight());
	setColors(Color.gray, Color.gray, Color.yellow);
	setAdjustGunForRobotTurn(true);
	setAdjustRadarForGunTurn(true);
	addCustomEvent(new GunAimedCondition());

	//movement initialization:
	final double maxX = getBattleFieldWidth()-18;
	final double maxY = getBattleFieldHeight()-18;
	if (gainedenergy == null)
	{
	    double maxDistance = Point2D.distance(18, 18, maxX, maxY);
	    gainedenergy = new double[(int)(maxDistance/50)+2];
	    lostenergy = new double[(int)(maxDistance/50)+2];
	    hisgainedenergy = new double[(int)(maxDistance/50)+2];
	    hislostenergy = new double[(int)(maxDistance/50)+2];
	    hits = new double[(int)(maxDistance/50)+2];
	    shots = new double[(int)(maxDistance/50)+2];
	    hishits = new double[(int)(maxDistance/50)+2];
	    hisshots = new double[(int)(maxDistance/50)+2];
	}
	currentEnergy = 100;

	while (true) {
	    //set basic movement:
	    setMaxVelocity(currentVBound);
	    double futureX = currentDirection*currentVBound*5*Math.sin(getHeadingRadians())+getX();
	    double futureY = currentDirection*currentVBound*5*Math.cos(getHeadingRadians())+getY();
	    if (futureX < 18D || futureY < 18D || futureX > maxX || futureY > maxY)
		currentDirection = -currentDirection;
	    setAhead(currentDirection*40);

	    if (!haveEnemy) {
		setTurnRadarLeft(22.5);
	    }
	    haveEnemy = false;
	    execute();
	}
    }

    //radians version of normalRelativeAngle:
    private double normalize(double angle)
    {
	return Math.atan2(Math.sin(angle), Math.cos(angle));
    }

    public void onScannedRobot(ScannedRobotEvent e) {
	double radarTurn;
	oldLocation.setLocation(location);
	location.setLocation(getX(), getY());
	oldEnemyLocation.setLocation(enemyLocation);
	absoluteBearing = getHeading() + e.getBearing();
	enemyDistance = e.getDistance();
	toLocation(absoluteBearing, enemyDistance, location, enemyLocation);
	deltaBearing = rollingAvg(deltaBearing, absoluteBearing(oldLocation, enemyLocation) - absoluteBearing(oldLocation, oldEnemyLocation), 5, 1);
	haveEnemy = true;
	radarTurn = normalRelativeAngle(getHeading() + e.getBearing() - getRadarHeading()) * 1.6;
	setTurnRadarRight(radarTurn);

	//data collection for movement - detecting enemy bullet fire:
	double denergy = currentEnergy-e.getEnergy();
	if (denergy >= .095 && denergy <= 3)
	{
	    hislostenergy[(int)(e.getDistance()/50)] += denergy;
	    hisshots[(int)(e.getDistance()/50)] ++;
	    if (getTime() >= nextTime)
	    {
		double rand = Math.random()*40-20;
		currentDirection = (rand < 0)?-1:1;
		currentVBound = Math.abs(rand);
		nextTime = getTime()+(int)(e.getDistance()/(20-3*denergy)/2);
	    }
	}
	else if (denergy != 0)
	    System.out.println(denergy);
	currentEnergy -= denergy;
	double mindist = findDistanceBracket(200, 550);
	double rel;
	if (e.getDistance() < mindist+100 && e.getDistance() > mindist-50 && distanceFromCorner() > 200)
	    rel = Math.PI/2;
	else if ((e.getDistance() > mindist+100 || distanceFromCorner() < 200) == currentDirection > 0)
	    rel = Math.PI/3;
	else
	    rel = 2*Math.PI/3;
	setTurnRightRadians(normalize(e.getBearingRadians()-rel));

	aimGun();
	if (currentEnergy == 0.0 && getOthers() == 1 && getTime() > nextTime) {
	    goTo(enemyLocation);
	}
    }

    public void onCustomEvent(CustomEvent e) {
	Condition condition = e.getCondition();
	if (condition instanceof GunAimedCondition) {
	    if (currentEnergy > 0.0) {
		shots[(int)(enemyDistance/50)] ++;
		lostenergy[(int)(enemyDistance/50)] += bulletPower(currentEnergy);
		Bullet bullet = setFireBullet(bulletPower(currentEnergy));
		if (bullet != null) {
		    addCustomEvent(new CheckVirtualGunsCondition(bullet));
		}
	    }
	}
    }

    //print stats about distance benefits:
    public void onEnd()
    {
	System.out.println("dist | My gained | My lost | His gained | His lost | gained on him | hit rate | his hit rate");
	java.text.DecimalFormat fmt = new java.text.DecimalFormat("0.000");
	for (int i=0; i<gainedenergy.length; i++)
	{
	    System.out.println(printFormatted(Integer.toString(i*50), 4) + " | " 
		    + printFormatted(fmt.format(gainedenergy[i]), 9) + " | "
		    + printFormatted(fmt.format(lostenergy[i]), 7) + " | "
		    + printFormatted(fmt.format(hisgainedenergy[i]), 10) + " | "
		    + printFormatted(fmt.format(hislostenergy[i]), 8) + " | "
		    + printFormatted(fmt.format(findBenefit(i)), 13) + " | "
		    + printFormatted(fmt.format(hits[i]/shots[i]), 8) + " | "
		    + printFormatted(fmt.format(hishits[i]/hisshots[i]), 12));
	}
    }

    private String printFormatted(String string, int length)
    {
	if (string.length() > length)
	    string = string.substring(0, length-1)+".";
	else
	    while (string.length() < length)
		string += " ";
	return string;
    }

    public void onWin(WinEvent e) {
	System.out.println("Wins: " + ++wins);
	onEnd();
    }

    public void onDeath(DeathEvent e) {
	System.out.println("Wins: " + wins);
	onEnd();
    }

    //update opponent energy and benefit tables
    public void onHitByBullet(HitByBulletEvent e)
    {
	currentEnergy += e.getPower()*3;
	hisgainedenergy[(int)(enemyLocation.distance(getX(), getY())/50)] += e.getPower()*3;
	double damage = Math.max(4*e.getPower(), 4*e.getPower()+2*(e.getPower()-1));
	lostenergy[(int)(enemyLocation.distance(getX(), getY())/50)] += damage;
	hishits[(int)(enemyLocation.distance(getX(), getY())/50)] ++;
    }

    //update opponent energy and benefit tables
    public void onBulletHit(BulletHitEvent e)
    {
	double power = e.getBullet().getPower();
	double damage = Math.max(4*power, 4*power+2*(power-1));
	gainedenergy[(int)(enemyLocation.distance(getX(), getY())/50)] += power*3;
	hislostenergy[(int)(enemyLocation.distance(getX(), getY())/50)] += damage;
	hits[(int)(enemyLocation.distance(getX(), getY())/50)] ++;
	currentEnergy -= damage;
    }

    //distance-finding methods:
    public double findDistanceBracket(double min, double max)
    {
	int bestindex = (int)(min/50+.5);
	for (int i=(int)(min/50+1.5); i <= (int)(max/50+.5); i++)
	{
	    if (2*findBenefit(i)+findBenefit(i+1)+findBenefit(i-1) > 2*findBenefit(bestindex)+findBenefit(bestindex+1)+findBenefit(bestindex-1))
		bestindex = i;
	}
	return bestindex*50;
    }

    public double findBenefit(int index)
    {
	return (gainedenergy[index]-lostenergy[index]+hislostenergy[index]-hisgainedenergy[index])/(gainedenergy[index]+lostenergy[index]+hislostenergy[index]+hisgainedenergy[index]);
    }

    private double distanceFromCorner()
    {
	return Math.min(Math.min(Point2D.distance(getX(), getY(), 0, 0), Point2D.distance(getBattleFieldWidth(), getY(), getX(), 0)), Math.min(Point2D.distance(getX(), getBattleFieldHeight(), 0, getY()), Point2D.distance(getBattleFieldWidth(), getBattleFieldHeight(), getX(), getY())));
    }

    private double bulletPower(double enemyEnergy) {
	double power = Math.min(enemyEnergy / 4, 3);
	power = Math.min(power, getEnergy()/5);
	power = Math.min(power, 1200/enemyDistance);
	return power;
    }

    private static double guessedBearingAngular(double bearing, double delta, double diffFactor) {
	return bearing + delta * diffFactor;
    }

    private static double guessedBearingOffset(double bearing, double offsetFactor) {
	return bearing + offsetFactor;
    }

    private void aimGun() {
	double absoluteBearing = absoluteBearing(location, enemyLocation);
	double guessedDistance = location.distance(enemyLocation);
	double meanAngularFactor = meanAngularFactorStraight;
	double meanOffsetFactor = meanOffsetFactorStraight;
	double[] gunFactor = gunFactorStraight;
	if (deltaBearing < -0.3) {
	    meanAngularFactor = meanAngularFactorLeft;
	    meanOffsetFactor = meanOffsetFactorLeft;
	    gunFactor = gunFactorLeft;
	}
	else if (deltaBearing > 0.3) {
	    meanAngularFactor = meanAngularFactorRight;
	    meanOffsetFactor = meanOffsetFactorRight;
	    gunFactor = gunFactorRight;
	}
	double guessedBearing;
	if (gunFactor[0] < gunFactor[1]) {
	    guessedBearing = guessedBearingAngular(absoluteBearing, deltaBearing, meanAngularFactor);
	}
	else {
	    guessedBearing = guessedBearingOffset(absoluteBearing, meanOffsetFactor);
	}
	toLocation(guessedBearing, guessedDistance, location, impactLocation);
	translateInsideField(impactLocation, 1);
	guessedBearing = absoluteBearing(location, impactLocation);
	setTurnGunRight(normalRelativeAngle(guessedBearing - getGunHeading()));
    }

    private void goTo(Point2D point) {
	double distance = location.distance(point);
	double angle = normalRelativeAngle(absoluteBearing(location, point) - getHeading());
	if (Math.abs(angle) > 90) {
	    distance *= -1;
	    if (angle > 0) {
		angle -= 180;
	    }
	    else {
		angle += 180;
	    }
	}
	setTurnRight(angle);
	setAhead(distance);
    }

    private static double bulletVelocity(double power) {
	return 20 - 3 * power;
    }

    private static long travelTime(double distance, double velocity) {
	return (int)Math.round(distance / velocity);
    }
    private void translateInsideField(Point2D point, double margin) {
	point.setLocation(Math.max(margin, Math.min(fieldRectangle.getWidth() - margin, point.getX())),
		Math.max(margin, Math.min(fieldRectangle.getHeight() - margin, point.getY())));
    }

    private void toLocation(double angle, double length, Point2D sourceLocation, Point2D targetLocation) {
	targetLocation.setLocation(sourceLocation.getX() + Math.sin(Math.toRadians(angle)) * length,
		sourceLocation.getY() + Math.cos(Math.toRadians(angle)) * length);
    }

    private double absoluteBearing(Point2D source, Point2D target) {
	return Math.toDegrees(Math.atan2(target.getX() - source.getX(), target.getY() - source.getY()));
    }

    private static double normalRelativeAngle(double angle) {
	double relativeAngle = angle % 360;
	if (relativeAngle <= -180 )
	    return 180 + (relativeAngle % 180);
	else if ( relativeAngle > 180 )
	    return -180 + (relativeAngle % 180);
	else
	    return relativeAngle;
    }

    public static double rollingAvg(double value, double newEntry, double n, double weighting ) {
	return (value*n + newEntry*weighting)/(n + weighting);
    } 

    class CheckVirtualGunsCondition extends Condition {
	private long time;
	private double bulletVelocity;
	private double bulletPower;
	private double bearingDelta;
	private Point2D oldRLocation = new Point2D.Double();
	private Point2D oldELocation = new Point2D.Double();
	private double oldBearing;

	public CheckVirtualGunsCondition(Bullet bullet) {
	    this.time = getTime();
	    this.bulletVelocity = bullet.getVelocity();
	    this.bulletPower = bullet.getPower();
	    this.bearingDelta = deltaBearing;
	    this.oldRLocation.setLocation(location);
	    this.oldELocation.setLocation(enemyLocation);
	    this.oldBearing = absoluteBearing(oldRLocation, oldELocation);
	}

	public boolean test() {
	    if (getOthers() == 0) {
		return false;
	    }
	    double bulletDistance = bulletVelocity * (getTime() - time);
	    if (bulletDistance > location.distance(enemyLocation) - 10) {
		if (bearingDelta > 0.05) {
		    double impactBearing = absoluteBearing(oldRLocation, enemyLocation);
		    double bearingDiff = normalRelativeAngle(impactBearing - oldBearing);
		    double factor = bearingDiff / bearingDelta;
		    double miss;
		    if (bearingDelta < -0.3) {
			meanAngularFactorLeft = rollingAvg(meanAngularFactorLeft, factor, 50, bulletPower);
			meanOffsetFactorLeft = rollingAvg(meanOffsetFactorLeft, bearingDiff, 50, bulletPower);
			miss = Math.abs(normalRelativeAngle(impactBearing - guessedBearingAngular(oldBearing, bearingDelta, meanAngularFactorLeft)));
			gunFactorLeft[0] = rollingAvg(gunFactorLeft[0], miss, 50, bulletPower);
			miss = Math.abs(normalRelativeAngle(impactBearing - guessedBearingOffset(oldBearing, meanOffsetFactorLeft)));
			gunFactorLeft[1] = rollingAvg(gunFactorLeft[1], miss, 50, bulletPower);
		    }
		    else if (bearingDelta > 0.3) {
			meanAngularFactorRight = rollingAvg(meanAngularFactorRight, factor, 50, bulletPower);
			meanOffsetFactorRight = rollingAvg(meanOffsetFactorRight, bearingDiff, 50, bulletPower);
			miss = Math.abs(normalRelativeAngle(impactBearing - guessedBearingAngular(oldBearing, bearingDelta, meanAngularFactorRight)));
			gunFactorRight[0] = rollingAvg(gunFactorRight[0], miss, 50, bulletPower);
			miss = Math.abs(normalRelativeAngle(impactBearing - guessedBearingOffset(oldBearing, meanOffsetFactorRight)));
			gunFactorRight[1] = rollingAvg(gunFactorRight[1], miss, 50, bulletPower);
		    }
		    else {
			meanAngularFactorStraight = rollingAvg(meanAngularFactorStraight, factor, 50, bulletPower);
			meanOffsetFactorStraight = rollingAvg(meanOffsetFactorStraight, bearingDiff, 50, bulletPower);
			miss = Math.abs(normalRelativeAngle(impactBearing - guessedBearingAngular(oldBearing, bearingDelta, meanAngularFactorStraight)));
			gunFactorStraight[0] = rollingAvg(gunFactorStraight[0], miss, 50, bulletPower);
			miss = Math.abs(normalRelativeAngle(impactBearing - guessedBearingOffset(oldBearing, meanOffsetFactorStraight)));
			gunFactorStraight[1] = rollingAvg(gunFactorStraight[1], miss, 50, bulletPower);
		    }
		}
		removeCustomEvent(this);
	    }
	    return false;
	}
    }

    class GunAimedCondition extends Condition {
	public boolean test() {
	    if (getOthers() == 0) {
		removeCustomEvent(this);
	    }
	    return (getGunHeat() == 0.0 && getGunTurnRemaining() == 0.0);
	}
    }
}