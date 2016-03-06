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

    public void update(int frameCount) {

        if (allUnits.getIdleIndex() > 0.1) {
            lastCommandFrame = frameCount;
            Position newPos = getNextPosition();
            Position curPos = allUnits.getMeanPosition();
            if (allUnits.getDispersion() > 100) {

                zealots.lineFormation(curPos, newPos, 30, 10);
                dragoons.lineFormation(curPos, newPos, 0, 20);
                templars.lineFormation(curPos, newPos, -40, 100);
            } else {

                allUnits.moveByOffset(curPos, newPos, 200);
            }
        }

        if (allUnits.getMeanPosition().getApproxDistance(getNextPosition()) < 200) {
            destinations.removeFirst();
        }
    }

    public Position getNextPosition() {
        if (destinations.isEmpty()) {
            Position destination = getRandomPosition();
            B.lm.generateMovementPositions(allUnits.getMeanPosition(), destination, destinations);
        }
        return destinations.getFirst();
    }

    private Position getRandomPosition() {
        return new Position(random.nextInt(4000), random.nextInt(1000));
    }
}
