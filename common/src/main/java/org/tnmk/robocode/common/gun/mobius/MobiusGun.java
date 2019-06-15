package org.tnmk.robocode.common.gun.mobius;

import java.awt.Color;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * This code was blindly copied from `mld.Moebius`, but I removed radar and movement setting.
 */
public class MobiusGun implements OnScannedRobotControl {
    private static final Color BULLET_COLOR = new Color(255, 142, 141);

    private final AdvancedRobot robot;

    // Constants
    static final int SEARCH_DEPTH = 30;    // Increasing this slows down game execution - beware!
    static final int MOVEMENT_LENGTH = 150;    // Larger helps on no-aim and nanoLauLectrik - smaller on linear-lead bots
    static final int BULLET_SPEED = 11;    // 3 power bullets travel at this speed.
    static final int MAX_RANGE = 800;    // Range where we're guarenteed to get a look-ahead lock
    // 1200 would be another good value as this is the max radar distance.
    // Yet too large makes it take longer to hit new movement patterns (Lemon)...
    static final int SEARCH_END_BUFFER = SEARCH_DEPTH + MAX_RANGE / BULLET_SPEED;    // How much room to leave for leading

    // Globals
    static double arcLength[] = new double[100000];
    static StringBuffer patternMatcher = new StringBuffer("\0\3\6\1\4\7\2\5\b" + (char) (-1) + (char) (-4) + (char) (-7) + (char) (-2) +
            (char) (-5) + (char) (-8) + (char) (-3) + (char) (-6) + "This space filler for end buffer." +
            "The numbers up top assure a 1 length match every time.  This string must be " +
            "longer than SEARCH_END_BUFFER. - Mike Dorgan");

    public MobiusGun(AdvancedRobot robot) {
        this.robot = robot;
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        doMoebius(0, patternMatcher.length(), e, SEARCH_DEPTH, e.getBearingRadians() + robot.getHeadingRadians());
    }

    private void doMoebius(int matchIndex, int historyIndex, ScannedRobotEvent e, int searchDepth, double targetBearing) {
        robot.setBulletColor(BULLET_COLOR);
        // Assign ArcMovement here to save a byte with the targetBearing assign.
        double arcMovement = e.getVelocity() * Math.sin(e.getHeadingRadians() - targetBearing);

        // Move in a SHM oscollator pattern with a bit of random thrown in for good measure.
//        robot.setAhead(Math.cos(historyIndex>>4) * MOVEMENT_LENGTH * Math.random());

        // Try to stay equa-distance to the target -  a slight movement towards
        // target would help with corner death, but no room.
//        robot.setTurnRightRadians(e.getBearingRadians() + Math.PI / 2);

        // Assume small aim increment so we can always fire.  Too much cost for gun turn check
        // Add simple power management code.  This keeps us alive a bit longer against bots we
        // have trouble locking on to.  Helps in melee as well.  It basically gives us 9 more shots.
        // -2 is better, but costs 1 more byte
        robot.setFire(robot.getEnergy() - 1);

        // Cummulative radial velocity relative to us. This is the ArcLength that the enemy traces relative to us.
        // ArcLength S = Angle (radians) * Radius of circle.
        arcLength[historyIndex + 1] = arcLength[historyIndex] + arcMovement;

        // Add ArcMovement to lookup buffer.  Typecast to char so it takes 1 entry.
        patternMatcher.append((char) (arcMovement));

        // Do adjustable buffer pattern match.  Use above buffer to save all out of bounds checks... ;)
        do {
            matchIndex = patternMatcher.lastIndexOf(
                    patternMatcher.substring(historyIndex - --searchDepth),
                    historyIndex - SEARCH_END_BUFFER);
        }
        while (matchIndex < 0);

        // Update index to end of search
        matchIndex += searchDepth;

        // Aim at target (asin() in front of sin would be better, but no room at 3 byte cost.)
        robot.setTurnGunRightRadians(Math.sin(
                (arcLength[matchIndex + ((int) (e.getDistance() / BULLET_SPEED))] - arcLength[matchIndex]) / e.getDistance() +
                        targetBearing - robot.getGunHeadingRadians()));

        // Lock Radar Infinite style.  About a 99% lock rate - plus good melee coverage.
//        setTurnRadarLeftRadians(getRadarTurnRemaining());

        // I'd love to drop a clearAllEvents() here for melee radar locking help, but no space.
    }
}
