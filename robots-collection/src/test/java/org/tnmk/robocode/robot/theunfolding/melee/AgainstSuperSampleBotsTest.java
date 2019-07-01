package org.tnmk.robocode.robot.theunfolding.melee;

import java.util.Arrays;
import java.util.List;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import supersample.SuperMercutio;
import supersample.SuperRamFire;
import supersample.SuperSpinBot;
import supersample.SuperWalls;

public class AgainstSuperSampleBotsTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                SuperWalls.class.getCanonicalName()
                , SuperRamFire.class.getCanonicalName()
                , SuperSpinBot.class.getCanonicalName()
                , SuperMercutio.class.getCanonicalName()
        );

        /**
         * - v2.4.1: No uTurn: 2000 rounds: 58.7%
         * - v2.4.2: Apply uTurn: 2000 rounds: 42.85%
         */
        TestConfig testConfig  = new TestConfig(TheUnfoldingRobot.class,enemyNames, 0.5, 2000);
        return testConfig;
    }
}
