public class WaitNode extends Node {
    private int time;
    WaitNode(int time) {
        this.time = time;
    }


    @Override
    public void execute(Robot r) {
        for (int i=0; i<time; i++) {
            r.idleWait();
        }
    }

    @Override
    public String toString() {
        return "wait";
    }
}
