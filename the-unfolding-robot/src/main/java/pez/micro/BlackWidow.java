package pez.micro;
import robocode.*;
import java.awt.geom.*;
import java.awt.Color;

// BlackWidow - Small, black, shiny and deadly - By PEZ
//
// BlackWidow is open source under GPL-like conditions. Meaning you can use
// the code. Meaning you should feel obliged to share any improvements you do
// to the code. Meaning you must release your bots code if you directly use this
// code.
//
// Home page of this bot is: http://robowiki.dyndns.org/?BlackWidow
// The code should be available there and it is also the place for you to share any
// code improvements.
//
// $Id: BlackWidow.java,v 1.4 2003/04/24 16:30:59 peter Exp $

public class BlackWidow extends AdvancedRobot {
    private static Point2D location = new Point2D.Double();
    private static Point2D oldLocation = new Point2D.Double();
    private static Point2D enemyLocation = new Point2D.Double();
    private static Point2D oldEnemyLocation = new Point2D.Double();
    private static Point2D bulletLocation = new Point2D.Double();
    private static Rectangle2D fieldRectangle;
    private static Bullet bullet;
    private static double enemyDistance;
    private static double enemyEnergy;
    private static double enemyBearing;
    private static double enemyBearingOffset;
    private static double velocity = 8;

    public void run() {
        fieldRectangle = new Rectangle2D.Double(0, 0 , getBattleFieldWidth(), getBattleFieldHeight());
        setColors(Color.black, Color.black, Color.red);
        setAdjustGunForRobotTurn(true);
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY); 

        do {
            location.setLocation(getX(), getY());
            try {
                if (oldLocation.distance(new Point2D.Double(bullet.getX(), bullet.getY())) >
                        oldLocation.distance(enemyLocation) || !bullet.isActive()) {
                            enemyBearingOffset = absoluteBearing(oldLocation, enemyLocation) - 
                                absoluteBearing(oldLocation, oldEnemyLocation);
                    bullet = null;
                }
                shoot();
            }
            catch (Exception e) {
                bullet = shoot();
                oldLocation.setLocation(location);
                oldEnemyLocation.setLocation(enemyLocation);
            }
            execute();
        } while (true);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        enemyDistance = e.getDistance();
        enemyBearing = getHeadingRadians() + e.getBearingRadians();
        enemyEnergy = e.getEnergy();
        setLocationRelative(location, enemyDistance, getHeadingRadians() + e.getBearingRadians(), enemyLocation);
        setTurnGunRightRadians(normalRelativeAngle(enemyBearing +
            enemyBearingOffset * (-0.5 + Math.random() * 1.5) - getGunHeadingRadians()));
        move();
    }

    private Bullet shoot() {
        if (getEnergy() > 0.2) {
            return setFireBullet(Math.min(getEnergy() / 3, enemyEnergy / 4));
        }
        return bullet;
    }

    private void move() {
        if (Math.random() < 0.05) {
            velocity = Math.min(8, Math.random() * 24);
        }
        setMaxVelocity(Math.abs(getTurnRemaining()) > 45 ? 0.1 : velocity);
        if (Math.abs(getDistanceRemaining()) < 40) {
            Point2D destination = new Point2D.Double();
            double distanceExtra = 3;
            if (enemyDistance > 475) {
                distanceExtra = -2;
            }
            double relativeAngle = Math.PI / -4 + Math.random() * Math.PI / 2;
            distanceExtra *= Math.abs(relativeAngle) * 40;
            setLocationRelative(enemyLocation, enemyDistance + distanceExtra, enemyBearing + Math.PI + relativeAngle, destination);
            if (!fieldRectangle.contains(destination)) {
                setLocationRelative(enemyLocation, enemyDistance + distanceExtra, enemyBearing + Math.PI - relativeAngle, destination);
            }
            translateInsideField(destination);
            double angle = normalRelativeAngle(absoluteBearing(location, destination) - getHeadingRadians());
            int direction = 1;
            if (Math.abs(angle) > Math.PI / 2) {
                angle += Math.acos(direction = -1);
            }
            setTurnRightRadians(normalRelativeAngle(angle));
            setAhead(location.distance(destination) * direction);
        }
    }

    private void translateInsideField(Point2D point) {
        point.setLocation(Math.max(45, Math.min(fieldRectangle.getWidth() - 45, point.getX())),
              Math.max(45, Math.min(fieldRectangle.getHeight() - 45, point.getY())));
    }

    private void setLocationRelative(Point2D sourceLocation, double length, double angle, Point2D targetLocation) {
        targetLocation.setLocation(sourceLocation.getX() + Math.sin(angle) * length,
            sourceLocation.getY() + Math.cos(angle) * length);
    }

    private double absoluteBearing(Point2D source, Point2D target) {
        return Math.atan2(target.getX() - source.getX(), target.getY() - source.getY());
    }

    private double normalRelativeAngle(double angle) {
        return Math.atan2(Math.sin(angle), Math.cos(angle));
    }
}