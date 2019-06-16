package org.tnmk.robocode.robot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import mld.Moebius;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import pez.micro.BlackWidow;
import robocode.BattleResults;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.testing.RobotTestBed;
import wiki.mako.MakoHT;
import wiki.mini.GouldingiHT;

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
            MY_ROBOT_NAME
            , Moebius.class.getCanonicalName()
            , BlackWidow.class.getCanonicalName()
            , MakoHT.class.getCanonicalName()
            , GouldingiHT.class.getCanonicalName()
    );
    /**
     *
     */
    private static final double EXPECT_WIN_RATIO = 0.7;

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
     * @inhertie
     */
    @Override
    public int getNumRounds() {
        return 20;
    }

    /**
     * @inhertie
     */
    public boolean isDeterministic() {
        return false;
    }

    @Override
    protected void runSetup() {
    }

    /**
     * Tests to see if our robot won all rounds.
     *
     * @param event Holds information about the battle has been completed.
     */
    @Override
    public void onBattleCompleted(BattleCompletedEvent event) {
        BattleResults[] battleResultsArray = event.getIndexedResults();

        BattleResults battleResultsOfMyRobot = battleResultsArray[MY_ROBOT_INDEX];
        int numWinRounds = battleResultsOfMyRobot.getFirsts();
        double winRate = (double) numWinRounds / (double) getNumRounds();
        Assert.assertTrue("Check my robot winner at least " + (EXPECT_WIN_RATIO * 100) + "% of rounds: numWinRounds: " + numWinRounds + ", winPercentage: " + (winRate * 100), winRate > EXPECT_WIN_RATIO);
    }
}