package org.tnmk.robocode.robot.theunfolding;

import java.util.Arrays;
import java.util.List;
import org.tnmk.robocode.common.gun.blackpearl.BlackPearlGun;
import org.tnmk.robocode.common.gun.gft.oldalgorithm.GFTAimGun;
import org.tnmk.robocode.common.gun.pattern.PatternPredictionGun;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import jekl.mini.BlackPearl;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import pez.micro.BlackWidow;
import wiki.mako.MakoHT;
import wiki.mini.GouldingiHT;

public class AgainstBlackPearlAndOthersTest extends AbstractWinRateTest {

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
         * - {@link PatternPredictionGun} and {@link BlackPearlGun}: 31.4%
         * The toughest opponents are BlackPearl.
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.37, 50);
        return testConfig;
    }
}
