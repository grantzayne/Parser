public class TurnAround extends Node{
    @Override
    public void execute(Robot r) {
        r.turnAround();
    }

    @Override
    public String toString() {
        return "turnAround";
    }
}
