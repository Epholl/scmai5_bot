import bwapi.Position;
import javafx.geometry.Pos;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Tomáš on 05.03.2016.
 */
public class ArmyMoveManager {

    Random random = new Random();
    private EphBot b = B.b;

    public int lastCommandFrame = Integer.MIN_VALUE;

    private UnitGroup allUnits = B.b.allUnits;
    private UnitGroup zealots = B.b.zealots;
    private UnitGroup dragoons = B.b.dragoons;
    private UnitGroup templars = B.b.templars;

    public LinkedList<Position> destinations = new LinkedList<>();

    public ArmyMoveManager() {
        B.mm = this;
    }

    public boolean waiting = false;
    public boolean enough = false;
    public void update(int frameCount) {

        if (frameCount > 820) {
            enough = true;
        }

        if (allUnits.getIdleIndex() > 0.75 || (allUnits.getDispersion() < 90 && B.b.myLosses == 0)) {
            lastCommandFrame = frameCount;
            Position newPos = getNextPosition();

            if (!enough && frameCount > 120) {
                if (!waiting) {
                    moveFormation(newPos, 180, 1.9);
                    waiting = true;
                } else {
                    return;
                }
            } else {
                moveFormation(newPos, 130);
            }



        }

        if (allUnits.getMeanPosition().getApproxDistance(getNextPosition()) < 200) {
            destinations.removeFirst();
        }
    }

    public void moveFormation(Position newPos, int distance) {
        moveFormation(newPos, distance, 1.0);
    }

    public void moveFormation(Position newPos, int distance, double spreadMultiplyer) {
        Position curPos = allUnits.getMeanPosition();

        if (zealots.hasUnits()) {
            zealots.lineFormation(curPos, newPos, 105 + distance, 30 * spreadMultiplyer);
        }

        if (dragoons.hasUnits()) {
            dragoons.lineFormation(curPos, newPos, 60 + distance, 30 * spreadMultiplyer);
        }

        if (templars.hasUnits()) {
            templars.lineFormation(curPos, newPos, distance, 100 * spreadMultiplyer);
        }
    }

    public Position getNextPosition() {
        if (destinations.isEmpty()) {
            Position destination = B.lm.getNextPosition();
            destinations.add(destination);//B.lm.generateMovementPositions(allUnits.getMeanPosition(), destination, destinations);
        }
        return destinations.getFirst();
    }

    public Position getRandomPosition() {
        return new Position(random.nextInt(4000), random.nextInt(2000));
    }
}
