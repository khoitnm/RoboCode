package org.tnmk.robocode.robot;

import java.util.Arrays;
import java.util.List;
import mld.Moebius;
import org.tnmk.robocode.common.gun.blackpearl.BlackPearlGun;
import org.tnmk.robocode.common.gun.gft.oldalgorithm.GFTAimGun;
import org.tnmk.robocode.common.gun.pattern.PatternPredictionGun;
import pez.micro.BlackWidow;
import wiki.mako.MakoHT;
import wiki.mini.GouldingiHT;

public class TheUnfoldingRobotVsMoebiusAndOthersTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                Moebius.class.getCanonicalName()
                , BlackWidow.class.getCanonicalName()
                , MakoHT.class.getCanonicalName()
                , GouldingiHT.class.getCanonicalName()
        );

        /**
         * At 2019-06-16, The champion percentage when running 1000 rounds (matches):
         * - {@link PatternPredictionGun} and {@link GFTAimGun}: 66.1%
         * - {@link PatternPredictionGun} and {@link BlackPearlGun}: 50.3%
         * View more at {@link TheUnfoldingRobotVsBlackPearlAndOthersTest} to see interesting resuls.
         * Moebius' gun is good, but his gun alone cannot touch TheUnfoldingRobot!
         */
        TestConfig testConfig  = new TestConfig(TheUnfoldingRobot.class,enemyNames, 0.7, 20);
        return testConfig;
    }
}
