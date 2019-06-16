package org.tnmk.robocode.common.gun.pattern;

import java.util.Random;
import robocode.Rules;

/**
 * Bullet power calculation.
 */
public class BulletPowerHelper {
    private static final double MAX_DISTANCE_TO_REACH_MIN_POWER = 700;
    private static final double MIN_DISTANCE_TO_REACH_MAX_POWER = 300;

    /**
     * This value is different from {@link Rules#MIN_BULLET_POWER}.
     * It represents the minimum power you want to fire, not the allowed minimum power defined by the game.
     */
    private static final double MIN_EXPECT_BULLET_POWER = 1.0d;

    private static final double DELTA_DISTANCE = MAX_DISTANCE_TO_REACH_MIN_POWER - MIN_DISTANCE_TO_REACH_MAX_POWER;
    /**
     * Delta is negative because the the further distance is, the smaller bullet power is.
     */
    private static final double DELTA_BULLET_POWER = MIN_EXPECT_BULLET_POWER - Rules.MAX_BULLET_POWER;
    private static final double BULLET_POWER_DISTRIBUTION_OVER_DISTANCE = DELTA_BULLET_POWER / DELTA_DISTANCE;

    //I usually play with 4-5 opponents and battle field 1200x1200, increasing to 0.2 is too risky in that case.
    private static final double INCREASE_POWER_FOR_EACH_REMAIN_ENEMIES = 0.15;

    private static final double LOW_ENERGY = 10;
    private static final double BULLET_POWER_COEFFICIENT_FOR_LOW_ENERGY = 1 / 10;

    /**
     * This method is mostly applicable only for {@link PatternPredictionGun} because it requires movePatternCertainty argument.
     *
     * @param fireDistance
     * @param movePatternCertainty
     * @param enemiesCount         the number of current enemies on the battle field.
     * @return
     * @see #reckonRawBulletPower(double, double)
     */
    public static double reckonBulletPower(double fireDistance, double movePatternCertainty, double enemiesCount, double remainEnergy) {
        double bulletPower = reckonRawBulletPower(fireDistance, movePatternCertainty);
        bulletPower = increaseBulletPowerBasedOnEnemiesCount(bulletPower, enemiesCount);
        bulletPower = reckonBulletPowerWithinLimit(bulletPower);
        bulletPower = reduceBulletPowerBasedOnRemainEnergy(bulletPower, remainEnergy);
        return bulletPower;
    }

    public static double reckonBulletPower(double fireDistance, double enemiesCount, double remainEnergy) {
        double bulletPower = reckonRawBulletPower(fireDistance, 1);
        bulletPower = increaseBulletPowerBasedOnEnemiesCount(bulletPower, enemiesCount);
        bulletPower = reckonBulletPowerWithinLimit(bulletPower);
        bulletPower = reduceBulletPowerBasedOnRemainEnergy(bulletPower, remainEnergy);
        return bulletPower;
    }

    /**
     * If energy is too low, don't fire.
     *
     * @param originalBulletPower
     * @param remainEnergy
     * @return
     */
    private static double reduceBulletPowerBasedOnRemainEnergy(double originalBulletPower, double remainEnergy) {
        double bulletPower = originalBulletPower;
        if (remainEnergy < LOW_ENERGY) {
            bulletPower = bulletPower * (remainEnergy * BULLET_POWER_COEFFICIENT_FOR_LOW_ENERGY);
        }
        if (remainEnergy < bulletPower) {
            bulletPower = 0;
        }
        return bulletPower;
    }

    private static double increaseBulletPowerBasedOnEnemiesCount(double originalBulletPower, double enemiesCount) {
        double bulletPower = originalBulletPower + (enemiesCount * INCREASE_POWER_FOR_EACH_REMAIN_ENEMIES);
        return bulletPower;
    }

    /**
     * @param fireDistance         the distance to target.
     * @param movePatternCertainty from 0.0 to 1.0. The lower number is, the lower certainty is. It means the bullet power will reduce to avoid too much risk.
     * @return bullet power: this is just a raw number, so it can be lower than {@link #MIN_EXPECT_BULLET_POWER} or bigger than {@link Rules#MAX_BULLET_POWER}
     * If it's zero, shouldn't fire bullet.
     */
    private static double reckonRawBulletPower(double fireDistance, double movePatternCertainty) {
        if (movePatternCertainty <= 0) {
            movePatternCertainty = 0.5;
        }

        double bulletPower = Rules.MAX_BULLET_POWER + (fireDistance - MIN_DISTANCE_TO_REACH_MAX_POWER) * BULLET_POWER_DISTRIBUTION_OVER_DISTANCE * movePatternCertainty;
        return bulletPower;
    }

    /**
     * @param bulletPower
     * @return if bulletPower greater than limit or lower than the limit, adjust it to be within the limit.
     * If the result is zero, it means you shouldn't fire bullet.
     */
    private static double reckonBulletPowerWithinLimit(double bulletPower) {
        double bulletPowerWithinLimit = Math.min(Rules.MAX_BULLET_POWER, bulletPower);
        if (bulletPower > Rules.MAX_BULLET_POWER) {
            bulletPowerWithinLimit = Rules.MAX_BULLET_POWER;
        } else if (bulletPower < MIN_EXPECT_BULLET_POWER) {
            bulletPowerWithinLimit = randomlyShouldFireBullet(bulletPower);
        }
        return bulletPowerWithinLimit;
    }

    /**
     * @param bulletPower bulletPower less than {@link #MIN_EXPECT_BULLET_POWER}.
     * @return randomly return 0 or {@link #MIN_EXPECT_BULLET_POWER}.<br/>
     * If value is 0, it means we shouldn't fire bullet at all.<br/>
     * Otherwise, just fire bullet with minimum power.<br/>
     * The lower bulletPower is, the more chance the result will be zero (shouldn't fire bullet).
     */
    private static double randomlyShouldFireBullet(double bulletPower) {
        //For example: if bulletPower is 0.8, 1 - bulletPower = 0.2 (20%). If we use that number for risk, it's maybe a little bit too high.
        //Don't need to be that conservative, I decide to divide that risk by 4,
        double riskCertainty = Math.max(0, MIN_EXPECT_BULLET_POWER - bulletPower) / 4d;
        double riskPercentage = riskCertainty * 100d;
        Random random = new Random();
        int randomInt = random.nextInt(100) + 1;
        if (randomInt < riskPercentage) {
            return 0;
        } else {
            return MIN_EXPECT_BULLET_POWER;
        }
    }
}
