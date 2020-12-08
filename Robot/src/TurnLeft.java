
public class TurnLeft extends Node {

    TurnLeft() {}

    @Override
    public void execute(Robot r) {
        r.turnLeft();
    }

    @Override
    public String toString() {
        return "turnL";
    }
}
