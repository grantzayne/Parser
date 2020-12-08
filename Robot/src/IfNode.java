import java.util.ArrayList;
import java.util.List;

public class IfNode extends Node{
    List<IfNode> elses;
    private BlockNode block;
    private ConditionNode condition;

    IfNode(BlockNode block, ConditionNode con) {
        this.block = block;
        this.condition = con;
        this.elses = new ArrayList<>();
    }

    @Override
    public void execute(Robot r) {
        if (this.condition != null) {
            this.condition.evaluate(r);
            if (this.condition.holds()) {
                for (RobotProgramNode n : this.block.getComponents()) {
                    n.execute(r);
                }
            } else {
                for (IfNode n : elses) {
                    for (RobotProgramNode n2 : n.block.getComponents()) {
                        n2.execute(r);
                    }
                }
            }
        } else {
            for (RobotProgramNode n : this.block.getComponents()) {
                n.execute(r);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder s;
        if (this.condition == null) {
            s = new StringBuilder("else {\n");
        } else {
            s = new StringBuilder("if (" + this.condition + ") {\n");
        }

        for (RobotProgramNode n : this.block.getComponents()) {
            s.append(n.toString()).append("\n");
        }

        if (this.elses.isEmpty()) {
            s.append("}\n");
        } else {
            s.append("} ");
            for (IfNode els : this.elses) {
                s.append(els.toString());
            }
        }
        return s.toString();
    }
}
