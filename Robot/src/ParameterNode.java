public class ParameterNode {
    private String name;
    private String valueInt;
    private boolean robotParameter = false;

    private String operator;
    private ParameterNode p1;
    private ParameterNode p2;

    ParameterNode(String name, int value, boolean Par) {
        this.name = name;
        this.valueInt = value + "";
        this.robotParameter = Par;
    }

    ParameterNode(String op, ParameterNode p1, ParameterNode p2) {
        this.operator = op;
        this.p1 = p1;
        this.p2 = p2;
    }

    boolean isRobotParameter() {
        return this.robotParameter;
    }

    void setValue(int i) throws IllegalArgumentException {
        if (valueInt == null) {
            throw new IllegalArgumentException("Invalid variable type: expected String");
        }
        this.valueInt = i + "";
    }

    Object getValue() throws NullPointerException {
            return Integer.parseInt(this.valueInt);
    }
    String getName() {
        return this.name;
    }

    public String toString() {
        if (this.p1 != null) {
            if ("add".equals(this.operator)) {
                return "(" + p1.toString() + " + " + p2.toString() + ")";
            } else if ("sub".equals(this.operator)) {
                return "(" + p1.toString() + " - " + p2.toString() + ")";
            } else if ("mul".equals(this.operator)) {
                return "(" + p1.toString() + " * " + p2.toString() + ")";
            } else if ("div".equals(this.operator)) {
                return "(" + p1.toString() + " / " + p2.toString() + ")";
            }
        }
        return name;
    }
}
