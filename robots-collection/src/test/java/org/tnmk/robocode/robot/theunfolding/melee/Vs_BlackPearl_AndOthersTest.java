package org.tnmk.robocode.robot.theunfolding.melee;

import java.util.Arrays;
import java.util.List;
import jekl.mini.BlackPearl;
import org.tnmk.robocode.common.gun.blackpearl.BlackPearlGun;
import org.tnmk.robocode.common.gun.gft.oldalgorithm.GFTAimGun;
import org.tnmk.robocode.common.gun.pattern.PatternPredictionGun;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TestRounds;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import pez.micro.BlackWidow;
import wiki.mako.MakoHT;
import wiki.mini.GouldingiHT;

/**
 * To win this, your robot should run smoothly. It shouldn't use stop-and-run strategy because it will make your robot slow, which cannot escape from enemies's bullets.
 * <p/>
 * The movement against these enemies are quite opposite with strategy {@link Vs_SuperSampleBots01Test}.
 */
public class Vs_BlackPearl_AndOthersTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                BlackPearl.class.getCanonicalName()
                , GouldingiHT.class.getCanonicalName()
                , BlackWidow.class.getCanonicalName()
                , MakoHT.class.getCanonicalName()
        );

        /**
         * At 2019-06-16, The champion percentage when running 1000 rounds (matches):
         * - {@link PatternPredictionGun} and {@link GFTAimGun}: 34.69% (0.3469).
         *   After fixing bug in PatternPredictionGun (v.2.3.4): 40.7%
         *   After using random movement for 1-on-1 (v2.4.0):  37.9%, 37.8%
         *      Run 2000 rounds: win 46.3%, 42.4%
         * - v2.4.2: Apply uTurn: 2000 rounds: 41.0%
         *
         * - {@link PatternPredictionGun} and {@link BlackPearlGun}: 31.4%
         * The toughest opponents are BlackPearl.
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.45, TestRounds.NUM_ROUNDS_ABSOLUTE_CERTAINTY);
        return testConfig;
    }
}
