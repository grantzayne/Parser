public abstract class Node implements RobotProgramNode {
    /**
     * I did want to put the Node and other Node classes in their own file
     * but the code didn't work properly. Just wasn't able to find the
     * RobotProgramNode so had to put them all in the same place. Sorry.
     */
    Node() {}
    public abstract void execute(Robot r);
    public abstract String toString();
}
