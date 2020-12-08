import java.util.List;

public class BlockNode extends Node {

    private List<RobotProgramNode> components;

    BlockNode(List<RobotProgramNode> statements) {
        this.components = statements;
        for (int i=0; i<this.components.size(); i++) {
            if (this.components.get(i) == null) {
                this.components.remove(i);
            }
        }
    }

    @Override
    public void execute(Robot r) {
        for (RobotProgramNode n : this.components) {
            n.execute(r);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (RobotProgramNode n : this.components) {
            s.append(n.toString()).append("\n");
        }
        return s.toString();
    }

    List<RobotProgramNode> getComponents() {
        return this.components;
    }
}
