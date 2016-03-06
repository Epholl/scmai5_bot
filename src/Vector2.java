import bwapi.Position;

/**
 * Created by Tomáš on 05.03.2016.
 */
public class Vector2 {

    public double x;
    public double y;

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Position position) {
        x = position.getX();
        y = position.getY();
    }

    public Vector2(Position start, Position end) {
        x = end.getX() - start.getX();
        y = end.getY() - start.getY();
    }

    public Vector2 normalize() {
        double length = getLength();
        x /= length;
        y /= length;

        return this;
    }

    public Vector2 multiply(double scalar) {
        x *= scalar;
        y *= scalar;

        return this;
    }

    public Vector2 getNormal() {
        return new Vector2(y, -x);
    }

    public Vector2 add(Vector2 added) {
        this.x += added.x;
        this.y += added.y;

        return this;
    }

    public Vector2 sub(Vector2 added) {
        this.x -= added.x;
        this.y -= added.y;

        return this;
    }

    public Vector2 avg(Vector2 vector) {
        return new Vector2((x + vector.x)/2, (y + vector.y)/2);
    }

    public Position toPosition() {
        return new Position((int) x, (int) y);
    }

    public double getDistance(Vector2 vector) {
        return Math.sqrt((x-vector.x)*(x-vector.x) + (y-vector.y)*(y-vector.y));
    }

    public double getLength() {
        return Math.sqrt(x*x + y*y);
    }

    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
