package wiki.mini;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import robocode.*;

// GouldingiHT - Small, hard to catch and with sharp teeth.
// Targeting, from Gouldingi, by PEZ
// Movement, from FloodMicro, by Kawigi
//
// GouldingiHT is open source under GPL-like conditions. Meaning you can use
// the code. Meaning you should feel obliged to share any improvements you do
// to the code. Meaning you must release your bots code if you directly use this
// code.
//
// Home page of this bot is: http://robowiki.dyndns.org/?GouldingiHT
// The code should be available there and it is also the place for you to share any
// code improvements.
//
// $Id: GouldingiHT.java,v 1.2 2003/04/21 19:54:06 peter Exp $

public class GouldingiHT extends AdvancedRobot {
    private static Point2D location = new Point2D.Double();
    private static Point2D oldLocation = new Point2D.Double();
    private static Point2D enemyLocation = new Point2D.Double();
    private static Point2D oldEnemyLocation = new Point2D.Double();
    private static Rectangle2D fieldRectangle;
    private static double enemyDistance;
    private static double enemyEnergy;
    private static double deltaBearing;
    private static double meanOffsetFactor;
    private static double meanAimFactorLeft;
    private static double meanAimFactorStraight;
    private static double meanAimFactorRight;
    private static double currentDirection=40;
    private static double currentVBound;
    private static int distanceindex;
    private long nextTime;
    //I'd never need 100 things in these arrays except on an extremely large battlefield.
    //Of course I can't see stuff 5000 away anyways, I suppose a little more than 20 would do...
    private static double[] benefit = new double[100], penalty = new double[100];

    public void run() {
        fieldRectangle = new Rectangle2D.Double(0, 0 , getBattleFieldWidth(), getBattleFieldHeight());
        //setColors(Color.gray, Color.yellow, Color.black);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        enemyEnergy = 100;
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY); 

        do {
            initMovement();
            if (getEnergy() > .1 && enemyEnergy > 0) {
                double power = Math.min(getEnergy() / 3, Math.min(enemyEnergy / 4, 3));
                Bullet bullet = setFireBullet(power);
                if (bullet != null) {
                    //penalty for firing because I lose energy:
                    penalty[distanceindex] += power;
                    addCustomEvent(new CheckUpdateFactors(bullet));
                }
            }
            setAhead(currentDirection);
            execute();
        } while (true);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        oldLocation.setLocation(location);
        location.setLocation(getX(), getY());
        oldEnemyLocation.setLocation(enemyLocation);
        enemyDistance = e.getDistance();
        toLocation(getHeadingRadians() + e.getBearingRadians(), enemyDistance, location, enemyLocation);
        double denergy = enemyEnergy-e.getEnergy();
        if (denergy >= .09 && denergy <= 3) {
            benefit[distanceindex] += denergy;	//my benefit, because he lost energy to fire
            if (getTime() >= nextTime) {
                if (Math.random() < .5)
                    currentDirection = -currentDirection;
                currentVBound = Math.random()*12;
                nextTime = getTime()+(int)(enemyDistance/(30-4.5*denergy));
            }
        }
        enemyEnergy = e.getEnergy();
        move(e);
        aim();
        // Radar from Moebius - http://robowiki.dyndns.org/?Moebius
        setTurnRadarLeftRadians(getRadarTurnRemaining()); 
    }

    //Event handlers to 'keep score'.  Some calculations combined to save space:
    public void onHitByBullet(HitByBulletEvent e) {
        double power = e.getPower();
        penalty[distanceindex] += Math.max(power*7, power*9-2);
    }

    public void onBulletHit(BulletHitEvent e) {
        double power = e.getBullet().getPower();
        double damage = Math.max(4*power, 6*power-2);
        benefit[distanceindex] += damage+power*3;
    }

    private void initMovement() {
        //wall avoidance
        final double maxX = getBattleFieldWidth()-18D;
        final double maxY = getBattleFieldHeight()-18D;

        setMaxVelocity(currentVBound);
        double futureX = currentDirection*Math.sin(getHeadingRadians())+getX();
        double futureY = currentDirection*Math.cos(getHeadingRadians())+getY();
        if (futureX < 18D || futureY < 18D || futureX > maxX || futureY > maxY) {
            currentDirection = -currentDirection;
        }
    }

    //find distance to closest corner - this is trimmed down in codesize quite a bit, and uses some interesting bitwise operations.
    private double distanceFromCorner() {
        double min = Double.POSITIVE_INFINITY;
        for (int i=0; i<4; i++)
            min = Math.min(min, Point2D.distance(getX(), getY(), (i&1)*getBattleFieldWidth(), (i>>1)*getBattleFieldHeight()));
        return min;
    }

    private void move(ScannedRobotEvent e) {
        //index for benefit stats:
        distanceindex = (int)(enemyDistance/50);
        Point2D backwall = new Point2D.Double();
        //backwall is the point 30 behind me relative to the enemy- this 
        //is to detect if I've backed up against a wall.
        toLocation(absoluteBearing(enemyLocation, location), 30, location, backwall);
        double mindist = findDistanceBracket()+85;
        double rel;		//offset angle - move laterally, toward, or away?
        //addition - if backwall is outside the field, stay lateral.  This could have some side effects
        //if I wanted to go toward him, but I figure I'll move away from the wall a little soon and then
        //I can move toward him.
        if ((e.getDistance() < mindist && enemyDistance > mindist-120 && distanceFromCorner()>200 && distanceFromCorner() > enemyDistance) |! fieldRectangle.contains(backwall))
            rel = Math.PI/2;
        //the == in boolean expressions is essentially a logical xnor.
        else if ((e.getDistance() > mindist || distanceFromCorner() < 200) == currentDirection > 0)
            rel = Math.PI/3;
        else
            rel = 2*Math.PI/3;
        setTurnRightRadians(normalRelativeAngle(e.getBearingRadians()-rel));
    }

    //methods to find optimal search bracket.  Trimmed down a bit from how MakoHT does it, to save space:
    private double findDistanceBracket() {
        int bestindex = 4;
        for (int i=5; i <= 14; i++)
            if (findBenefit(i) > findBenefit(bestindex))
                bestindex = i;
        if (enemyEnergy == 0) {
            bestindex = -1;
        }
        return bestindex*50;
    }

    private double findBenefit(int index) {
        return (benefit[index]-penalty[index])/(benefit[index]+penalty[index]);
    }

    private void aim() {
        double meanAimFactor = meanAimFactorStraight;
        deltaBearing = normalRelativeAngle(absoluteBearing(oldLocation, enemyLocation) -
            absoluteBearing(oldLocation, oldEnemyLocation));
        if (deltaBearing < -0.005) {
            meanAimFactor = meanAimFactorLeft;
        }
        else if (deltaBearing > 0.005) {
            meanAimFactor = meanAimFactorRight;
        }
        double absoluteBearing = absoluteBearing(location, enemyLocation);
        double guessedHeading = absoluteBearing + meanOffsetFactor;
        if (Math.abs(deltaBearing) > 0.001) {
            guessedHeading = absoluteBearing + deltaBearing * meanAimFactor;
        }
        Point2D impactLocation = new Point2D.Double();
        toLocation(guessedHeading, enemyDistance, location, impactLocation);
        guessedHeading = absoluteBearing(location, impactLocation);
        setTurnGunRightRadians(normalRelativeAngle(guessedHeading - getGunHeadingRadians()));
    }

    private void toLocation(double angle, double length, Point2D sourceLocation, Point2D targetLocation) {
        targetLocation.setLocation(sourceLocation.getX() + Math.sin(angle) * length,
            sourceLocation.getY() + Math.cos(angle) * length);
    }

    private double absoluteBearing(Point2D source, Point2D target) {
        return Math.atan2(target.getX() - source.getX(), target.getY() - source.getY());
    }

    private double normalRelativeAngle(double angle) {
        return Math.atan2(Math.sin(angle), Math.cos(angle));
    }

    public double rollingAvg(double value, double newEntry, double n, double weighting ) {
        return (value * n + newEntry * weighting) / (n + weighting);
    } 

    class CheckUpdateFactors extends Condition {
        private long time;
        private double bulletVelocity;
        private double bulletPower;
        private double bearingDelta;
        private Point2D oldRLocation = new Point2D.Double();
        private Point2D oldELocation = new Point2D.Double();

        public CheckUpdateFactors(Bullet bullet) {
            this.time = getTime();
            this.bulletVelocity = bullet.getVelocity();
            this.bulletPower = bullet.getPower();
            this.bearingDelta = deltaBearing;
            this.oldRLocation.setLocation(location);
            this.oldELocation.setLocation(enemyLocation);
        }

        public boolean test() {
            double bulletDistance = bulletVelocity * (getTime() - time);
            if (getOthers() > 0 && bulletDistance > oldRLocation.distance(enemyLocation)) {
                double bearingDiff = normalRelativeAngle(absoluteBearing(oldRLocation, enemyLocation) -
                    absoluteBearing(oldRLocation, oldELocation));
                meanOffsetFactor = rollingAvg(meanOffsetFactor, bearingDiff, 20, bulletPower);
                if (Math.abs(bearingDelta) > 0.001) {
                    double factor = bearingDiff / bearingDelta;
                    if (bearingDelta < -0.05) {
                        meanAimFactorLeft = rollingAvg(meanAimFactorLeft, factor, 75, bulletPower);
                    }
                    else if (bearingDelta > 0.05) {
                        meanAimFactorRight = rollingAvg(meanAimFactorRight, factor, 75, bulletPower);
                    }
                    else {
                        meanAimFactorStraight = rollingAvg(meanAimFactorStraight, factor, 75, bulletPower);
                    }
                }
                removeCustomEvent(this);
            }
            return false;
        }
    }
}