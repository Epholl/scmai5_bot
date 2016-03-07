import bwapi.Color;
import bwapi.Position;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tomáš on 05.03.2016.
 */
public class LocationManager {

    public LocationManager() {
        B.lm = this;
        if (isArmyPositionRight()) {
            movementPositions = movementPositionRight;
        } else {
            movementPositions = movementPositionsLeft;
        }
    }

    public static final Position[] movementPositionsLeft = {
            new Position(900, 500),
            new Position(1500, 600),
            new Position(2600, 600),
            new Position(3200, 500),
            new Position(2250, 1580),
            new Position(1830, 1580),
            new Position(720, 500),
            new Position(3220, 460)

    };

    public static final Position[] movementPositionRight = {
            new Position(3200, 500),
            new Position(2600, 650),
            new Position(1500, 650),
            new Position(900, 500),
            new Position(1830, 1580),
            new Position(2250, 1580),
            new Position(3420, 500),
            new Position(820, 460)
    };

    public int movementIndex = 0;
    public Position[] movementPositions;

    public Position getNextPosition() {
        return movementPositions[movementIndex++ % movementPositions.length];
    }

    public boolean isArmyPositionRight() {
        return B.b.allUnits.getMeanPosition().getX() > 2000;
    }












    public void generateMovementPositions(Position start, Position target, List<Position> list) {
        B.b.calcPositions.clear();
        Position first = getNextPosition(start, target, 200.0);
        Position second = getNextPosition(first, target, 200.0);

        list.add(first);
        list.add(second);

        int firstTargetDistance = first.getApproxDistance(target);
        int secondTargetDistance = second.getApproxDistance(target);



        Vector2 next;
        while (firstTargetDistance - secondTargetDistance > 20 ) {
            next = new Vector2(first, second);
            first = second;
            firstTargetDistance = secondTargetDistance;

            second = getNextPosition(first, next.toPosition(), 200.0);
            list.add(second);

            secondTargetDistance = second.getApproxDistance(target);
        }
    }

    public Position getNextPosition(Position startP, Position endP, double distance) {
        Vector2 direction = new Vector2(startP, endP).normalize();
        Vector2 normal = direction.getNormal().normalize();

        Vector2 realEnd = rayCast(new Vector2(startP), direction, distance*3);
        Vector2 right = rayCast(new Vector2(startP), normal.multiply(-1.0), distance);
        Vector2 left = rayCast(new Vector2(startP), normal.multiply(-1.0), distance);

        List<Position> positions = B.b.calcPositions;
        positions.add(startP);
        positions.add(realEnd.toPosition());
        positions.add(right.toPosition());
        positions.add(left.toPosition());

        Vector2 finish = realEnd.avg(right).avg(left);
        return finish.toPosition();
    }

    public Vector2 rayCast(Vector2 start, Vector2 direction, double maxDistance) {
        Vector2 heading = direction.normalize().multiply(4.0);

        double distance = 0.0;

        while (isWalkable(start) && distance < maxDistance) {
            start.add(heading);
            distance += 4.0;
        }

        return start;
    }

    public boolean isWalkable(Vector2 position) {
        return (B.b.game.isWalkable((int)position.x/8, (int) position.y/8));
    }
}
