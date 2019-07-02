package org.tnmk.robocode.robot.theunfolding.oneonone;

import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TestRounds;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import voidious.Diamond;

import java.util.Arrays;
import java.util.List;

public class AgainstVoidiusDiamondTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                Diamond.class.getCanonicalName()
        );

        /**
         * Run 1000 rounds with win percentage:
         * - OscillatorMoveController  for 1-on-1: 1.0% (OMG, it's was so so bad!!!)
         * - RandomMoveController for 1-on-1 (v2.4.0): 39.1%, 33.6%
         *      Run 2000 rounds: win 64.05%, 65.7%
         * - v2.4.2: Apply uTurn: 2000 rounds: 63.65%
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.6, TestRounds.NUM_ROUNDS_CERTAINTY);
        return testConfig;
    }
}
