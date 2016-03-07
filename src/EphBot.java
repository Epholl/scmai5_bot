import bwapi.*;
import bwapi.Flag.Enum;

import java.util.*;

public class EphBot extends DefaultBWListener {

    private Mirror mirror = new Mirror();

    public Game game;

    public Player self;

    public UnitGroup allUnits;

    public UnitGroup zealots;

    public UnitGroup dragoons;

    public UnitGroup templars;

    public List<Unit> enemyUnits = new ArrayList<>();

    public ArmyMoveManager moveManager;
    public LocationManager locationManager;
    public FightManager fightManager;

    public List<UnitGroup> allGroups = new ArrayList<>();
    public List<Agent> agents = new ArrayList<>();

    Random r = new Random();

    public void run() {
        B.b = this;
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }

    @Override
    public void onUnitCreate(Unit unit) {
        System.out.println("New unit discovered " + unit.getType());
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        self = game.self();
        game.enableFlag(Enum.UserInput.getValue());

        for (Player pl : game.getPlayers()) {
            if (pl.equals(self)) {
                continue;
            }
        }

        allUnits = new UnitGroup(self.getUnits());
        zealots = new UnitGroup(new ArrayList<Unit>());
        dragoons = new UnitGroup(new ArrayList<Unit>());
        templars = new UnitGroup(new ArrayList<Unit>());
        moveManager = new ArmyMoveManager();
        locationManager = new LocationManager();
        fightManager = new FightManager();
    }

    @Override
    public void onFrame() {
        //debugDraw();

        if (fightManager.fighting()) {

            fightManager.update();

        } else {
            fightManager.state = FightManager.NOTHING;

            if (enemyLosses > 1400){
                for (Unit u: allUnits.units) {
                    if (u.isIdle()) {
                        u.attack(moveManager.getRandomPosition());
                    }
                }
            } else {
                moveManager.update(game.getFrameCount());
            }
        }
    }

    public boolean isEnemyVisible() {
        for (Unit u: enemyUnits) {
            if (u.isVisible()) {
                return true;
            }
        }

        return false;
    }

    public Unit getEnemyUnit() {
        for (Unit u: enemyUnits) {
            if (u.isVisible()) {
                return u;
            }
        }
        return null;
    }

    public int myLosses = 0;
    public int enemyLosses = 0;
    public List<Position> calcPositions = new ArrayList<>();
    private void debugDraw() {
        game.drawTextScreen(50, 25, "FC: " + game.getFrameCount());

        game.drawCircleMap(allUnits.getMeanPosition(), 10, Color.White, false);
        game.drawTextMap(allUnits.getMeanPosition(), "" + allUnits.getDispersion());

        for (Unit u: self.getUnits()) {
            game.drawTextMap(u.getPosition(), u.getPosition().toString());

            if (u.isAttackFrame()) {
                game.drawCircleMap(u.getPosition().getX(), u.getPosition().getY(), 3, Color.Cyan, true);
            }
            if (u.isMoving()) {
                game.drawCircleMap(u.getPosition().getX(), u.getPosition().getY()+5, 3, Color.Red, true);
            }
            if (u.isAttacking()) {
                game.drawCircleMap(u.getPosition().getX(), u.getPosition().getY()+10, 3, Color.Yellow, true);
            }
            if (u.isStartingAttack()) {
                game.drawCircleMap(u.getPosition().getX(), u.getPosition().getY()+15, 3, Color.Blue, true);
            }
            if (u.isUnderAttack()) {
                game.drawCircleMap(u.getPosition().getX(), u.getPosition().getY()+20, 3, Color.Green, true);
            }
        }

        game.drawLineMap(moveManager.getNextPosition(), allUnits.getMeanPosition(), Color.Blue);
    }

    @Override
    public void onUnitDestroy(Unit unit) {
        super.onUnitDestroy(unit);

        if (unit.getPlayer().equals(self)) {
            myLosses += getUnitPrice(unit);
            for (UnitGroup g: allGroups) {
                for (Iterator<Unit> iterator = g.units.iterator(); iterator.hasNext();) {
                    Unit u = iterator.next();
                    if (u.getID() == unit.getID()) {
                        iterator.remove();
                    }
                }
            }
        }

        else {
            for (Iterator<Unit> iterator = enemyUnits.iterator(); iterator.hasNext();) {
                Unit u = iterator.next();
                if (u.getID() == unit.getID()) {
                    iterator.remove();
                }
            }
            enemyLosses += getUnitPrice(unit);
        }
    }

    @Override
    public void onUnitDiscover(Unit unit) {
        super.onUnitDiscover(unit);
        if (unit.getPlayer().equals(self)) {
            allUnits.units.add(unit);
            if (unit.getType() == UnitType.Protoss_Zealot) {
                zealots.units.add(unit);
            } else if (unit.getType() == UnitType.Protoss_Dragoon) {
                dragoons.units.add(unit);
            } else if (unit.getType() == UnitType.Protoss_High_Templar) {
                templars.units.add(unit);
                agents.add(new TemplarManager(unit));
            } else if (unit.getType() == UnitType.Protoss_Archon) {
                zealots.units.add(unit);
            }
        } else {
            enemyUnits.add(unit);
        }
    }

    public static void main(String[] args) {
        new EphBot().run();
    }

    private int getUnitPrice(Unit u) {
        return u.getType().mineralPrice() + u.getType().gasPrice();
    }
}