public class LoopNode extends Node {
    private BlockNode blockNode;
    LoopNode(BlockNode blockNode) {
        this.blockNode = blockNode;
    }

    @Override
    public void execute(Robot r) {
        this.blockNode.execute(r);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("loop {\n");
        for (RobotProgramNode n : this.blockNode.getComponents()) {
            s.append("	").append(n.toString()).append("\n");
        }
        s.append("}\n");
        return s.toString();
    }
}
