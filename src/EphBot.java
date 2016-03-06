import bwapi.*;
import bwapi.Flag.Enum;
import bwta.BWTA;
import bwta.BaseLocation;

import java.util.*;

public class EphBot extends DefaultBWListener {

    private Mirror mirror = new Mirror();

    public Game game;

    public Player self;

    public UnitGroup allUnits;

    public UnitGroup zealots;

    public UnitGroup dragoons;

    public UnitGroup templars;

    public ArmyMoveManager moveManager;
    public LocationManager locationManager;

    public List<UnitGroup> allGroups = new ArrayList<>();

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

        allUnits = new UnitGroup(self.getUnits());
        zealots = new UnitGroup(new ArrayList<Unit>());
        dragoons = new UnitGroup(new ArrayList<Unit>());
        templars = new UnitGroup(new ArrayList<Unit>());
        moveManager = new ArmyMoveManager();
        locationManager = new LocationManager();
    }

    @Override
    public void onFrame() {
        debugDraw();

        moveManager.update(game.getFrameCount());

    }

    public List<Position> calcPositions = new ArrayList<>();
    private void debugDraw() {
        game.drawTextScreen(50, 25, "Movemanager updated: " + moveManager.lastCommandFrame);

        game.drawCircleMap(allUnits.getMeanPosition(), 10, Color.White, false);
        game.drawTextMap(allUnits.getMeanPosition(), "" + allUnits.getDispersion());
        game.drawCircleMap(zealots.getMeanPosition(), 10, Color.White, false);
        game.drawTextMap(zealots.getMeanPosition(), "" + allUnits.getDispersion());
        game.drawCircleMap(dragoons.getMeanPosition(), 10, Color.White, false);
        game.drawTextMap(dragoons.getMeanPosition(), "" + allUnits.getDispersion());
        game.drawCircleMap(templars.getMeanPosition(), 10, Color.White, false);
        game.drawTextMap(templars.getMeanPosition(), "" + allUnits.getDispersion());

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

        for (Position p: moveManager.destinations) {

            game.drawCircleMap(p, 8, Color.Red);
        }

        for (int i = 0; i < calcPositions.size(); i+= 4) {
            Position start = calcPositions.get(i);
            Position first = calcPositions.get(i+1);
            Position second = calcPositions.get(i+2);
            Position third = calcPositions.get(i+3);

            game.drawCircleMap(start, 8, Color.Green, true);
            game.drawLineMap(first, second, Color.Green);
            game.drawLineMap(first, third, Color.Green);
            game.drawLineMap(third, second, Color.Cyan);
        }
    }

    @Override
    public void onUnitDestroy(Unit unit) {
        super.onUnitDestroy(unit);
        for (UnitGroup g: allGroups) {
            for (Iterator<Unit> iterator = g.units.iterator(); iterator.hasNext();) {
                Unit u = iterator.next();
                if (u.getID() == unit.getID()) {
                    iterator.remove();
                }
            }
            if (g.units.size() <= 0) {
                allGroups.remove(g);
            }
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
            }
        }
    }

    public static void main(String[] args) {
        new EphBot().run();
    }
}