package org.tnmk.robocode.robot.theunfolding.melee;

import java.util.Arrays;
import java.util.List;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TestRounds;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import supersample.SuperCrazy;
import supersample.SuperMercutio;
import supersample.SuperSpinBot;

/**
 * To win against this, your robot should not run with the same velocity (which is opposite with {@link Vs_BlackPearl_AndOthersTest}.
 * <p/>
 * Hence, to find the balance, use {@link org.tnmk.robocode.common.movement.tactic.uturn.NonStopUTurnMoveController} which change the velocity when moving, but it won't stop. So it won't move too slow.
 */
public class Vs_SuperSampleBots01Test extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                SuperCrazy.class.getCanonicalName()
                , SuperSpinBot.class.getCanonicalName()
                , SuperMercutio.class.getCanonicalName()
        );

        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.63, TestRounds.NUM_ROUNDS_ABSOLUTE_CERTAINTY);
        return testConfig;
    }
}
