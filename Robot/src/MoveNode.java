public class MoveNode extends Node {
    private int movement;
    MoveNode(int movement) {
        this.movement = movement;
    }

    @Override
    public void execute(Robot r) {
        for (int i=0; i<movement; i++) {
            r.move();
        }
    }

    @Override
    public String toString() {
        return "move";
    }
}
