package org.tnmk.robocode.common.radar;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.tnmk.robocode.common.gun.pattern.EnemyMovePatternIdentifyHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyPredictionHistory;
import org.tnmk.robocode.common.model.enemy.EnemyStatisticContext;
import robocode.AdvancedRobot;
import robocode.Robot;

/**
 * This context data will be shared by implementations of Radar, MoveController and Gun.
 * However, data inside this should be changed by Radar only. It shouldn't be changed by MoveController or Gun.
 */
public class AllEnemiesObservationContext {
    /**
     * If an enemy is not updated after this period of time, it's considered outdated.
     */
    private static final long CONSIDER_OUTDATED_PERIOD = 16 * 5;//5 times of full scan.
    private final AdvancedRobot robot;

    /**
     * Store the latest information about enemy map by name
     */
    private final Map<String, Enemy> enemiesMapByName = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, EnemyStatisticContext> enemiesPatternPredictionsMapByName = Collections.synchronizedMap(new HashMap<>());

    public AllEnemiesObservationContext(AdvancedRobot robot) {
        this.robot = robot;
    }

    /**
     * @see RadarHelper#isAllEnemiesHasNewData(Collection, long, long, long)
     * @return
     */
    public boolean isAllEnemiesHasNewData() {
        Collection<Enemy> enemies = enemiesMapByName.values();
        long totalActualEnemies = robot.getOthers();
        return RadarHelper.isAllEnemiesHasNewData(enemies, totalActualEnemies, robot.getTime(), CONSIDER_OUTDATED_PERIOD);
    }

    public Collection<Enemy> getEnemies() {
        return enemiesMapByName.values();
    }

    public Map<String, Enemy> getEnemiesMapByName() {
        return enemiesMapByName;
    }

    public void addEnemy(Enemy enemy) {
        this.enemiesMapByName.put(enemy.getName(), enemy);
        EnemyStatisticContext enemyStatisticContext = this.enemiesPatternPredictionsMapByName.get(enemy.getName());
        if (enemyStatisticContext == null) {
            EnemyHistory enemyHistory = new EnemyHistory(enemy.getName(), enemy);
            EnemyPredictionHistory enemyPredictionHistory = new EnemyPredictionHistory(enemy.getName());
            enemyStatisticContext = new EnemyStatisticContext(robot, enemy.getName(), enemyHistory, enemyPredictionHistory);
            enemiesPatternPredictionsMapByName.put(enemy.getName(), enemyStatisticContext);
        } else {
            enemyStatisticContext.getEnemyHistory().addToHistory(enemy);
        }
        long currentTime = enemy.getTime();
        EnemyMovePatternIdentifyHelper.identifyEnemyPatternIfNecessary(currentTime, enemyStatisticContext);

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
        return this.enemiesPatternPredictionsMapByName.get(enemyName).getEnemyHistory();
    }

    public void removeEnemy(String enemyName) {
        this.enemiesMapByName.remove(enemyName);
    }

    public EnemyStatisticContext getEnemyPatternPrediction(String enemyName) {
        return this.enemiesPatternPredictionsMapByName.get(enemyName);
    }
}
