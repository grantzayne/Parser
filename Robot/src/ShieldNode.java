public class ShieldNode extends Node{
    private boolean active;

    ShieldNode(boolean active){
        this.active = active;
    }

    @Override
    public void execute(Robot r) {
        r.setShield(active);
    }

    @Override
    public String toString() {
        if(!this.active){
            return "shieldOff";
        }
        return "shieldOn";
    }
}
