public class ConditionNode {

    private String operator;
    ParameterNode p1;
    ParameterNode p2;

    private ConditionNode c1;
    private ConditionNode c2;

    ConditionNode(String op, ParameterNode p1, ParameterNode p2) throws IllegalArgumentException {
        this.operator = op;
        if (!(op.equals("lt") || op.equals("gt") || op.equals("eq"))) {
            throw new IllegalArgumentException("Invalid operator");
        }
        this.p1 = p1;
        this.p2 = p2;
    }

    ConditionNode(String op, ConditionNode c1, ConditionNode c2) throws IllegalArgumentException {
        this.operator = op;
        if (c2 == null && !op.equals("not")) {
            throw new IllegalArgumentException("Cannot perform operation on a single condition");
        }
        if (c2 != null && op.equals("not")) {
            throw new IllegalArgumentException("Cannot negate two conditions");
        }

        this.c1 = c1;
        this.c2 = c2;
    }

    void evaluate(Robot r) {
        if (p1 != null && p2 != null) {
            if (p1.isRobotParameter()) {
                if ("fuelLeft".equals(p1.getName())) {
                    p1.setValue(r.getFuel());
                } else if ("oppLR".equals(p1.getName())) {
                    p1.setValue(r.getOpponentLR());
                } else if ("oppFB".equals(p1.getName())) {
                    p1.setValue(r.getOpponentFB());
                } else if ("barrelLR".equals(p1.getName())) {
                    p1.setValue(r.getClosestBarrelLR());
                } else if ("barrelFB".equals(p1.getName())) {
                    p1.setValue(r.getClosestBarrelFB());
                } else if ("numBarrels".equals(p1.getName())) {
                    p1.setValue(r.numBarrels());
                } else if ("wallDist".equals(p1.getName())) {
                    p1.setValue(r.getDistanceToWall());
                }
            }
            if (p2.isRobotParameter()) {
                if ("fuelLeft".equals(p2.getName())) {
                    p2.setValue(r.getFuel());
                } else if ("oppLR".equals(p2.getName())) {
                    p2.setValue(r.getOpponentLR());
                } else if ("oppFB".equals(p2.getName())) {
                    p2.setValue(r.getOpponentFB());
                } else if ("barrelLR".equals(p2.getName())) {
                    p2.setValue(r.getClosestBarrelLR());
                } else if ("barrelFB".equals(p2.getName())) {
                    p2.setValue(r.getClosestBarrelFB());
                } else if ("numBarrels".equals(p2.getName())) {
                    p2.setValue(r.numBarrels());
                } else if ("wallDist".equals(p2.getName())) {
                    p2.setValue(r.getDistanceToWall());
                }
            }
        } else {
            c1.evaluate(r);
            if (c2 != null) {
                c2.evaluate(r);
            }
        }
    }

    boolean holds() {
        if (p1 != null) {
            if (this.operator.equals("lt")) {
                return (int) (p1.getValue()) < (int) (p2.getValue());
            } else if (this.operator.equals("gt")) {
                return (int) (p1.getValue()) > (int) (p2.getValue());
            } else if (this.operator.equals("eq")) {
                return (int) (p1.getValue()) == (int) (p2.getValue());
            }
            return false;
        }
        if (c2 != null) {
            switch (operator) {
                case("and"):
                    return c1.holds() && c2.holds();
                case("or"):
                    return c1.holds() || c2.holds();
            }
        }
        return !c1.holds();
    }

    public String toString() {
        if (operator.equals("lt")) {
            return p1 + " < " + p2;
        } else if (operator.equals("gt")) {
            return p1 + " > " + p2;
        } else if (operator.equals("eq")) {
            return p1 + " == " + p2;
        } else if (operator.equals("or")) {
            return c1 + " || " + c2;
        } else if (operator.equals("and")) {
            return c1 + " && " + c2;
        } else if (operator.equals("not")) {
            return "!" + c1;
        }
        return null;
    }
}
