package org.tnmk.robocode.common.radar;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import robocode.AdvancedRobot;
import robocode.Robot;

/**
 * This context data will be shared by implementations of Radar, Movement and Gun.
 * However, data inside this should be changed by Radar only. It shouldn't be changed by Movement or Gun.
 */
public class AllEnemiesObservationContext {
    private final AdvancedRobot robot;

    private final Map<String, Enemy> enemiesMapByName = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, EnemyHistory> enemiesHistoriesMapByName = Collections.synchronizedMap(new HashMap<>());


    public AllEnemiesObservationContext(AdvancedRobot robot) {
        this.robot = robot;
    }

    public Collection<Enemy> getEnemies() {
        return enemiesMapByName.values();
    }

    public Map<String, Enemy> getEnemiesMapByName() {
        return enemiesMapByName;
    }

    public void addEnemy(Enemy enemy) {
        this.enemiesMapByName.put(enemy.getName(), enemy);
        EnemyHistory enemyHistory = this.enemiesHistoriesMapByName.get(enemy.getName());
        if (enemyHistory == null) {
            enemyHistory = new EnemyHistory(enemy.getName(), enemy);
            enemiesHistoriesMapByName.put(enemy.getName(), enemyHistory);
        }
    }

    /**
     * @return This method just count scanned enemies.<br/>
     * To count all actual existing enemies, use {@link Robot#getOthers()}.
     */
    public int countEnemies() {
        return this.enemiesMapByName.size();
    }

    public Enemy getEnemy(String enemyName) {
        return this.enemiesMapByName.get(enemyName);
    }

    public EnemyHistory getEnemyHistory(String enemyName) {
        return this.enemiesHistoriesMapByName.get(enemyName);
    }

    public void removeEnemy(String enemyName) {
        this.enemiesMapByName.remove(enemyName);
    }
}
