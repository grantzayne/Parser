public class TakeFuelNode extends Node {
    @Override
    public void execute(Robot r) {
        r.takeFuel();
    }

    @Override
    public String toString() {
        return "takeFuel";
    }
}
