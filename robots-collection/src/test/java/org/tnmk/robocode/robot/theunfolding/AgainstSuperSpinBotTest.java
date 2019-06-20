package org.tnmk.robocode.robot.theunfolding;

import java.util.Arrays;
import java.util.List;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import supersample.SuperSpinBot;

public class AgainstSuperSpinBotTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                SuperSpinBot.class.getCanonicalName()
        );

        /**
         * Run 1000 rounds with win percentage:
         * - OscillatorMovement  for 1-on-1: 31.5%
         * - (v2.4.0)RandomMovement for 1-on-1: 56.0%
         *      Run 2000 rounds: win 75%
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.7, 20);
        return testConfig;
    }
}
