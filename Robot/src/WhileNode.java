public class WhileNode extends Node implements  RobotProgramNode{
    private BlockNode block;
    private ConditionNode condition;

    WhileNode(BlockNode block, ConditionNode con) {
        this.block = block;
        this.condition = con;
    }

    @Override
    public void execute(Robot r) {
        this.condition.evaluate(r);
        while (this.condition.holds()) {
            this.condition.evaluate(r);
            for (RobotProgramNode n : this.block.getComponents()) {
                n.execute(r);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("while (" + this.condition + ") {\n");
        for (RobotProgramNode n : this.block.getComponents()) {
            s.append(n.toString()).append("\n");
        }
        s.append("}\n");
        return s.toString();
    }
}
