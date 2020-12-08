public class TurnRight extends Node {
    TurnRight() { }

    @Override
    public void execute(Robot r) {
        r.turnRight();
    }

    @Override
    public String toString() {
        return "turnR";
    }
}
