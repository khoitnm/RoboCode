package org.tnmk.robocode.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tnmk.robocode.common.log.LogHelper;
import robocode.BattleResults;
import robocode.Robot;
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
@Ignore
public abstract class AbstractWinRateTest extends RobotTestBed {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * The order of my robot in the list {@link #getRobotNames()}.
     */
    private static final int MY_ROBOT_INDEX = 0;
    private List<String> allRobotNames;
    private final TestConfig testConfig;

    public AbstractWinRateTest() {
        this.testConfig = constructTestConfig();
    }

    abstract public TestConfig constructTestConfig();

    /**
     * Specifies the robots that will fight.
     *
     * @return The comma-delimited list of robots in this match.
     */
    @Override
    public String getRobotNames() {
        allRobotNames = new ArrayList<>();
        allRobotNames.add(testConfig.myRobotClass.getCanonicalName());
        allRobotNames.addAll(testConfig.enemiesNamesList);
        return allRobotNames.stream().collect(Collectors.joining(","));
    }

    /**
     * @inhertie
     */
    @Override
    public int getNumRounds() {
        return testConfig.numRounds;
    }

    /**
     * @inhertie
     */
    public boolean isDeterministic() {
        return false;
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


        String message = "Actual Win: " + (winRate * 100) + "% (" + getNumRounds() + " rounds)." +
                "\n\t Expect Win: " + (testConfig.expectWinRatio * 100) + "%";
        logger.info(LogHelper.appendGitInfo(message));

        Assert.assertEquals("My robot should be the champion, but the actual rank is " + battleResultsOfMyRobot.getRank(), battleResultsOfMyRobot.getRank(), 1);
        Assert.assertTrue(message, winRate >= testConfig.expectWinRatio);
    }


    public static class TestConfig {
        private final Class<? extends Robot> myRobotClass;
        private final int numRounds;
        private final double expectWinRatio;
        /**
         * We don't use Robot class because we may want to test some robot which exist not in our source code.
         */
        private final List<String> enemiesNamesList;

        /**
         * @param myRobotClass
         * @param enemiesNamesList
         * @param expectWinRatio   represent how many percent (value is from 0.0 to 1.0) our robot should be the champion through out numRounds.
         * @param numRounds
         */
        public TestConfig(Class<? extends Robot> myRobotClass, List<String> enemiesNamesList, double expectWinRatio, int numRounds) {
            this.myRobotClass = myRobotClass;
            this.numRounds = numRounds;
            this.expectWinRatio = expectWinRatio;
            this.enemiesNamesList = enemiesNamesList;
        }
    }
}