package org.tnmk.robocode.common.radar;

import java.util.Collection;
import java.util.Optional;
import org.tnmk.robocode.common.model.enemy.Enemy;

public class RadarHelper {
    /**
     * @param scannedEnemies
     * @param currentTime
     * @param considerOutdatedPeriod if an enemy is not updated after this period of time, it's considered outdated.
     * @return
     */
    public static boolean isAllEnemiesHasNewData(Collection<Enemy> scannedEnemies, long actualTotalEnemies, long currentTime, long considerOutdatedPeriod) {
        if (scannedEnemies.size() < actualTotalEnemies) {
            return false;
        }
        Optional<Enemy> outdatedEnemyOptional = scannedEnemies.stream().filter(enemy -> currentTime - enemy.getTime() > considerOutdatedPeriod).findAny();
        if (outdatedEnemyOptional.isPresent()) {
            System.out.println("Has one outdated enemy: " + outdatedEnemyOptional.get().getName() + ", time: " + outdatedEnemyOptional.get().getTime());
        }
        return !outdatedEnemyOptional.isPresent();
    }
}
