import bwapi.Position;
import bwapi.Unit;

/**
 * Created by Tomáš on 06.03.2016.
 */
public class FightManager {

    public static final int FIGHTING = 3;
    public static final int RUNNING = 5;
    public static final int NOTHING = 1;

    public int state = 1;

    public void update() {

        for (Agent a: B.b.agents) {
            a.update();
        }

        Unit u = B.b.getEnemyUnit();



        if (state == RUNNING) {
            if (isStormed()) {
                return;
            } else {
                state = NOTHING;
            }
        } else { // FIGHTING, NOTHING
            if (isStormed()) {
                state = RUNNING;
                Position runAwayFrom = u.getPosition();

                B.mm.moveFormation(runAwayFrom, -500, 5.0);
            }
        }

        if (state == NOTHING) {
            for (Unit myUnit: B.b.allUnits.units) {
                myUnit.attack(u.getPosition());
            }
            state = FIGHTING;
            return;
        }

        if (state == FIGHTING) {
            for (Unit myUnit: B.b.allUnits.units) {
                if (myUnit.isIdle()) {
                    myUnit.attack(u.getPosition());
                }
            }
        }
    }

    public boolean fighting() {
        return B.b.isEnemyVisible();
    }

    private int lastStormFrame = 0;
    public boolean isStormed() {
        for (Unit u: B.b.allUnits.units) {
            if (u.isUnderStorm()) {
                lastStormFrame = B.b.game.getFrameCount();
                return true;
            }
        }
        if (lastStormFrame + 22 > B.b.game.getFrameCount()) {
            return true;
        }

        return false;
    }
}
