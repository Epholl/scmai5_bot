import bwapi.Position;
import bwapi.Unit;

import java.util.List;

/**
 * Created by Tomáš on 05.03.2016.
 */
public class UnitGroup {

    public List<Unit> units;

    public UnitGroup(List<Unit> units) {
        this.units = units;
        B.b.allGroups.add(this);
    }

    public boolean hasUnits() {
        return !units.isEmpty();
    }

    public Position getMeanPosition() {
        if (units.isEmpty()) {
            return Position.None;
        }

        int sumX = 0;
        int sumY = 0;

        for (Unit u: units) {
            sumX += u.getX();
            sumY += u.getY();
        }

        int count = units.size();
        return new Position(sumX / count, sumY / count);
    }

    public int getDispersion() {
        Position mean = getMeanPosition();

        double totalDistance = 0.0;

        for (Unit u: units) {
            double distance = u.getPosition().getDistance(mean.getX(), mean.getY());
            totalDistance += distance*distance;
        }

        return (int) Math.sqrt(totalDistance / units.size());
    }

    public void lineFormation(Position point, Position target, double offset, double spread) {
        Vector2 direction = new Vector2(point, target);
        direction.normalize();
        Vector2 normal = direction.getNormal();

        double unitIndex = - (units.size() - (((double) units.size()) / 2));

        for (Unit u : units) {
            Position unitPos = new Position(
                        (int)(point.getX() + normal.x * unitIndex * spread + direction.x * offset),
                        (int) (point.getY() + normal.y * unitIndex * spread + direction.y * offset));
            u.move(unitPos);
            unitIndex += 1;
        }
    }



    public void moveByOffset(Position point, Position target, double distance) {
        Vector2 v = new Vector2(point, target);
        v.normalize().multiply(distance);

        for (Unit u: units) {
            u.move(new Position(u.getX() + (int)v.x, u.getY() + (int)v.y));
        }
    }

    public double getIdleIndex() {

        double sum = 0.0;
        for (Unit u: units) {
            if (!u.isMoving()) {
                sum++;
            }
        }
        return sum / units.size();
    }
}
