import bwapi.TechType;
import bwapi.Unit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tomáš on 06.03.2016.
 */
public class TemplarManager implements Agent {

    public static Map<Integer, Integer> storms = new HashMap<>();

    public Unit templar;

    public int activateAfterFrame = 0;
    public int stormTime = 0;

    public TemplarManager(Unit templar) {
        this.templar = templar;
    }

    public void update() {
        if (B.b.game.getFrameCount() < activateAfterFrame) {
            return;
        }

        if (templar.getHitPoints() > 0) {
            if (canMerge()) {
                for (Agent agent: B.b.agents) {
                    if (agent != this && agent instanceof TemplarManager) {
                        if (((TemplarManager)agent).canMerge()) {
                            templar.useTech(TechType.Archon_Warp, ((TemplarManager) agent).templar);
                            activateAfterFrame = B.b.game.getFrameCount() + 50;
                        }
                    }
                }
            }

            tryAStrom();

            List<Unit> enemies = B.b.game.getUnitsInRadius(templar.getPosition(), 600);
            for (Unit enemy: enemies) {
                if (enemy.getPlayer().getID() != B.b.self.getID()
                        && B.b.game.getFrameCount() - activateAfterFrame > 5) {

                    activateAfterFrame = B.b.game.getFrameCount() + 20;
                    templar.move(B.b.allUnits.getMeanPosition());
                }
            }
        }
    }

    public boolean canMerge() {
        return templar.getHitPoints() > 0 && templar.getEnergy() < 65;
    }

    public void tryAStrom() {
        if (stormTime + 30 > B.b.game.getFrameCount()) {
            return;
        }

        List<Unit> enemies = B.b.game.getUnitsInRadius(templar.getPosition(), 250);

        Unit best = null;
        double count = 0;
        for (Unit enemy: enemies) {
            if (enemy.getPlayer().getID() != B.b.self.getID()
                    && !enemy.isUnderStorm()) {

                if (storms.containsKey(enemy.getID())) {
                    if (storms.get(enemy.getID()) + 40 < B.b.game.getFrameCount()) {
                        continue;
                    }
                }
                double s = shouldStorm(enemy);
                if (s > count) {
                    count = s;
                    best = enemy;
                }
            }
        }

        if (count > 2.5) {
            storms.put(best.getID(), B.b.game.getFrameCount());
            activateAfterFrame = B.b.game.getFrameCount() + 10;
            stormTime = B.b.game.getFrameCount();
            templar.useTech(TechType.Psionic_Storm, best.getPosition());
        }
    }

    public double shouldStorm(Unit enemy) {
        double count = 0;
        List<Unit> units = B.b.game.getUnitsInRadius(enemy.getPosition(), 70) ;

        for (Unit u: units) {
            if (u.getPlayer().getID() == B.b.self.getID() || u.isUnderStorm()) {
                count -= 1.1;
            } else {
                count++;
            }
        }

        return count;
    }
}
