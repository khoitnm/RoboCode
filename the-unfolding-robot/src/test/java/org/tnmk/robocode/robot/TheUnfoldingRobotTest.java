package org.tnmk.robocode.robot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import robocode.BattleResults;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.testing.RobotTestBed;

/**
 * <pre>
 * Some example code:
 * https://hiraidekeone.wordpress.com/2013/02/26/robocode-quality-assurance-and-junit-testing/
 * https://bretkikehara.wordpress.com/2013/02/26/robocode-unit-testing-goodness/
 * https://github.com/robo-code/robocode/blob/master/plugins/testing/robocode.testing.samples/src/main/java/sample/TestWallBehavior.java
 *
 * Set up to run this test:
 * In IntelliJ menu > Run > Edit Configurations > In VM Options, add: " -Drobocode.home=D:\SourceCode\RoboCode\robocode"
 * </pre>
 */
@RunWith(JUnit4.class)
public class TheUnfoldingRobotTest extends RobotTestBed {
    /**
     * The order of my robot in the list {@link #getRobotNames()}.
     */
    private static final int MY_ROBOT_INDEX = 0;
    private static final String MY_ROBOT_NAME = TheUnfoldingRobot.class.getCanonicalName();

    private static final List<String> TEST_ROBOTS = Arrays.asList(
            MY_ROBOT_NAME,
            "sample.Tracker"
    );


    /**
     * Specifies the robots that will fight.
     *
     * @return The comma-delimited list of robots in this match.
     */
    @Override
    public String getRobotNames() {
        return TEST_ROBOTS.stream().collect(Collectors.joining(","));
    }

    /**
     * This test runs for 20 rounds.
     *
     * @return The number of rounds.
     */
    @Override
    public int getNumRounds() {
        return 20;
    }

    /**
     * Tests to see if our robot won all rounds.
     *
     * @param event Holds information about the battle has been completed.
     */
    @Override
    public void onBattleCompleted(BattleCompletedEvent event) {
        // Return the results in order of getRobotNames.
        BattleResults[] battleResultsArray = event.getIndexedResults();
        // Sanity check that results[0] is PewPew.
        BattleResults battleResultsOfMyRobot = battleResultsArray[MY_ROBOT_INDEX];
        String robotName = battleResultsOfMyRobot.getTeamLeaderName();
//        Assert.assertEquals("Check that the winner is my robot", MY_ROBOT_NAME, robotName);

        // Check to make sure my robot won at least won over half the rounds.
        int numWinRounds = battleResultsOfMyRobot.getFirsts();
        Assert.assertTrue("Check my robot winner at least 75% of rounds", numWinRounds > getNumRounds() * 0.75);
    }
}