package org.tnmk.robocode.robot.theunfolding.oneonone;

import java.util.Arrays;
import java.util.List;
import mld.Moebius;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TestRounds;
import org.tnmk.robocode.robot.TheUnfoldingRobot;

public class AgainstMoebiusTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                Moebius.class.getCanonicalName()
        );

        /**
         * 1000 rounds:
         * - (v2.4.0) RandomMoveController: 97.2% (What!!!!!) (Run 100 rounds, win 86.0%!!!)
         *      2000 rounds: 98.55%
         * - v2.4.2: Apply uTurn: 1000 rounds: 97.3%
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.9, TestRounds.NUM_ROUNDS_QUICK);
        return testConfig;
    }
}
