import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;

/**
 * The parser and interpreter. The top level parse function, a main method for
 * testing, and several utility methods are provided. You need to implement
 * parseProgram and all the rest of the parser.
 */
public class Parser {

	/**
	 * Top level parse method, called by the World
	 */
	static RobotProgramNode parseFile(File code) {
		Scanner scan = null;
		try {
			scan = new Scanner(code);

			// the only time tokens can be next to each other is
			// when one of them is one of (){},;
			scan.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");

			RobotProgramNode n = parseProgram(scan); // You need to implement this!!!

			scan.close();
			return n;
		} catch (FileNotFoundException e) {
			System.out.println("Robot program source file not found");
		} catch (ParserFailureException e) {
			System.out.println("Parser error:");
			System.out.println(e.getMessage());
			scan.close();
		}
		return null;
	}

	/** For testing the parser without requiring the world */

	public static void main(String[] args) {
		if (args.length > 0) {
			for (String arg : args) {
				File f = new File(arg);
				if (f.exists()) {
					System.out.println("Parsing '" + f + "'");
					RobotProgramNode prog = parseFile(f);
					System.out.println("Parsing completed ");
					if (prog != null) {
						System.out.println("================\nProgram:");
						System.out.println(prog);
					}
					System.out.println("=================");
				} else {
					System.out.println("Can't find file '" + f + "'");
				}
			}
		} else {
			while (true) {
				JFileChooser chooser = new JFileChooser(".");// System.getProperty("user.dir"));
				int res = chooser.showOpenDialog(null);
				if (res != JFileChooser.APPROVE_OPTION) {
					break;
				}
				RobotProgramNode prog = parseFile(chooser.getSelectedFile());
				System.out.println("Parsing completed");
				if (prog != null) {
					System.out.println("Program: \n" + prog);
				}
				System.out.println("=================");
			}
		}
		System.out.println("Done");
	}

	// Useful Patterns

	static Pattern NUMPAT = Pattern.compile("-?\\d+"); // ("-?(0|[1-9][0-9]*)");
	static Pattern OPENPAREN = Pattern.compile("\\(");
	static Pattern CLOSEPAREN = Pattern.compile("\\)");
	static Pattern OPENBRACE = Pattern.compile("\\{");
	static Pattern CLOSEBRACE = Pattern.compile("\\}");

	/**
	 * PROG ::= STMT+
	 */
	private static RobotProgramNode parseProgram(Scanner s) {
		if (!s.hasNext()) {
			return null;
		}
		List<RobotProgramNode> statements = new ArrayList<>();
		while (s.hasNext()) {
			statements.add(parseMain(s));
		}
		return new BlockNode(statements);
	}

	private static RobotProgramNode parseMain(Scanner s) {
		String token = s.next();
		if (token.equals("loop")) {
			return parseLoop(s);
		}
		else if(token.equals("if")){
			return parseIf(s,null);
		}
		else if(token.equals("while")){
			return parseWhile(s);
		}
		return parseProcess(token, s);
	}

	private static RobotProgramNode parseWhile(Scanner s) {
		List<RobotProgramNode> block = new ArrayList<>();

		if (!checkFor(OPENPAREN, s)) {
			fail("Expected open parenthesis", s);
		}
		ConditionNode condition = parseCondition(s);
		if (!checkFor(CLOSEPAREN, s)) {
			fail("Expected closed parenthesis", s);
		}
		if (!checkFor(OPENBRACE, s)) {
			fail("Expected open brace", s);
		}
		while (!checkFor(CLOSEBRACE, s)) {
			block.add(parseMain(s));
		}
		if (block.size() <= 0) {
			fail("Requires at least one statement in block", s);
		}
		if (condition.p1 != null) {
			if (condition.p1.getName().equals(condition.p2.getName())) {
				fail("Infinite loop", s);
			}
		}
		return new WhileNode(new BlockNode(block), condition);
	}

	private static IfNode parseIf(Scanner s, IfNode parent) {
		List<RobotProgramNode> block = new ArrayList<>();
		ConditionNode condition;
        if (!checkFor(OPENPAREN, s)) {
            fail("Expected open parenthesis", s);
        }
        condition = parseCondition(s);
        if (!checkFor(CLOSEPAREN, s)) {
            fail("Expected closed parenthesis", s);
        }
        if (!checkFor(OPENBRACE, s)) {
			fail("Expected open brace", s);
		}
		while (!checkFor(CLOSEBRACE, s)) {
			block.add(parseMain(s));
		}
		if (block.size() <= 0) {
			fail("Requires at least one statement in block", s);
		}
        IfNode n = new IfNode(new BlockNode(block), condition);
        if (s.hasNext("else if")) {
            s.next();
            if (parent == null) {
                n.elses.add(parseIf(s, n));
            } else {
                parent.elses.add(parseIf(s, parent));
            }
        } else if (s.hasNext("else")) {
            n.elses.add(parseElse(s));
        }
        return n;
	}

	private static IfNode parseElse(Scanner s){
        List<RobotProgramNode> block = new ArrayList<>();
        s.next();

        if (!checkFor(OPENBRACE, s)) {
            fail("Expected open brace", s);
        }
        while (!checkFor(CLOSEBRACE, s)) {
            block.add(parseMain(s));
        }
        if (block.size() <= 0) {
            fail("Requires at least one statement in block", s);
        }
        return new IfNode(new BlockNode(block), null);
    }

	private static ConditionNode parseCondition(Scanner s) {
		String op = s.next();
		if (!checkFor(OPENPAREN, s)) {
			fail("Expected open parenthesis", s);
		}
        if (!"lt".equals(op) && !"gt".equals(op) && !"eq".equals(op)) {
            if ("not".equals(op)) {
                ConditionNode c = new ConditionNode(op, parseCondition(s), null);
                if (!checkFor(CLOSEPAREN, s)) {
                    fail("Expected closed parenthesis", s);
                }
                return c;
            } else {
                ConditionNode c1 = parseCondition(s);
                if (!checkFor(",", s)) {
                    fail("Expected comma", s);
                }
                ConditionNode c2 = parseCondition(s);
                if (!checkFor(CLOSEPAREN, s)) {
                    fail("Expected closed parenthesis", s);
                }
                return new ConditionNode(op, c1, c2);
            }
        }
        ParameterNode p1 = parseParameter(s.next(), s);
		if (!checkFor(",", s)) {
			fail("Expected comma", s);
		}
		ParameterNode p2 = parseParameter(s.next(), s);
		if (!checkFor(CLOSEPAREN, s)) {
			fail("Expected closed parenthesis", s);
		}
        if (p2 != null && p1 != null && p1.isRobotParameter() && p2.isRobotParameter()) {
            fail("Cannot compare two robot variables", s);
        }
        if (p1 != null && p2 != null && p1.toString().equals(p2.toString())) {
            fail("Condition is always true", s);
        }
        return new ConditionNode(op, p1, p2);
	}

	private static ParameterNode parseParameter(String str, Scanner s) {
        if ("fuelLeft".equals(str)) {
            return new ParameterNode("fuelLeft", 0, true);
        } else if ("oppLR".equals(str)) {
            return new ParameterNode("oppLR", 0, true);
        } else if ("oppFB".equals(str)) {
            return new ParameterNode("oppFB", 0, true);
        } else if ("barrelLR".equals(str)) {
            return new ParameterNode("barrelLR", 0, true);
        } else if ("barrelFB".equals(str)) {
            return new ParameterNode("barrelFB", 0, true);
        } else if ("numBarrels".equals(str)) {
            return new ParameterNode("numBarrels", 0, true);
        } else if ("wallDist".equals(str)) {
            return new ParameterNode("wallDist", 0, true);
        } else {
            if (isInteger(str)) {
                return new ParameterNode(str, Integer.parseInt(str), false);
            }
            if (str.equals("add") || str.equals("mul") || str.equals("div") || str.equals("sub")) {
                if (!checkFor(OPENPAREN, s)) {
                    fail("Requires arguments", s);
                }
                ParameterNode v1 = parseParameter(s.next(), s);
                if (!checkFor(",", s)) {
                    fail("Requires two arguments", s);
                }
                ParameterNode v2 = parseParameter(s.next(), s);

                ParameterNode v = new ParameterNode(str, v1, v2);
                if (!checkFor(CLOSEPAREN, s)) {
                    fail("Requires closing parenthesis", s);
                }
                return v;
            }
        }
		fail("Invalid variable", s);
		return null;
	}

	/**
	 * Check if String is an integer found this bit of code on stackoverflow
	 * https://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
	 *
	 */
	private static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch(NumberFormatException | NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	private static RobotProgramNode parseLoop(Scanner s) {
		List<RobotProgramNode> block = new ArrayList<>();
		s.next();
		while (!checkFor(CLOSEBRACE, s)) {
			block.add(parseMain(s));
		}
		if (block.size() <= 0) {
			fail("Requires at least one statement in block", s);
		}
		return new LoopNode(new BlockNode(block));
	}

	private static RobotProgramNode parseProcess(String token, Scanner s) {
		RobotProgramNode node = null;
		if (token.equals("turnL")) {
			node = new TurnLeft();
		}
		else if(token.equals("turnR")) {
			node = new TurnRight();
		}
		else if(token.equals("turnAround")) {
			node = new TurnAround();
		}
		else if(token.equals("move")){
			node = parseMove(s);
		}
		else if(token.equals("takeFuel")){
			node = new TakeFuelNode();
		}
		else if(token.equals("wait")){
			node = parseWait(s);
		}
		else if(token.equals("shieldOn")){
			node =  new ShieldNode(true);
		}
		else if(token.equals("shieldOff")){
			node = new ShieldNode(false);
		}
		else{
				fail("Unknown command: " + token, s);
		}
		require(";", "No semicolon", s);
		return node;
	}

	private static RobotProgramNode parseMove(Scanner s) {
		if (checkFor(OPENPAREN, s)) {
			int amount = (int) parseExpression(s).getValue();
			MoveNode m = new MoveNode(Math.max(amount, 1));
			if (!checkFor(CLOSEPAREN, s)) {
				fail("Requires close parenthesis in move", s);
			}
			return m;
		}
		return new MoveNode(1);
	}

	private static RobotProgramNode parseWait(Scanner s) {
		if (checkFor(OPENPAREN, s)) {
			int amount = (int) parseExpression(s).getValue();
			WaitNode w = new WaitNode(amount);
			if (!checkFor(CLOSEPAREN, s)) {
				fail("Requires close parenthesis in wait", s);
			}
			return w;
		}
		return new WaitNode(1);
	}

	private static ParameterNode parseExpression(Scanner s){
		String token = s.next();
        if (!"add".equals(token) && !"sub".equals(token) && !"mul".equals(token) && !"div".equals(token)) {
            return parseParameter(token, s);
        }
        if (!checkFor(OPENPAREN, s)) {
			fail("Expression has no arguments", s);
		}
		ParameterNode v1 = parseExpression(s);
		if (!checkFor(",", s)) {
			fail("Requires comma", s);
		}
		ParameterNode v2 = parseExpression(s);
		if (!checkFor(CLOSEPAREN, s)) {
			fail("Requires closed parenthesis", s);
		}
		return new ParameterNode(token, v1, v2);
	}


	// utility methods for the parser

	/**
	 * Report a failure in the parser.
	 */
	static void fail(String message, Scanner s) {
		String msg = message + "\n   @ ...";
		for (int i = 0; i < 5 && s.hasNext(); i++) {
			msg += " " + s.next();
		}
		throw new ParserFailureException(msg + "...");
	}

	/**
	 * Requires that the next token matches a pattern if it matches, it consumes
	 * and returns the token, if not, it throws an exception with an error
	 * message
	 */
	static String require(String p, String message, Scanner s) {
		if (s.hasNext(p)) {
			return s.next();
		}
		fail(message, s);
		return null;
	}

	static String require(Pattern p, String message, Scanner s) {
		if (s.hasNext(p)) {
			return s.next();
		}
		fail(message, s);
		return null;
	}

	/**
	 * Requires that the next token matches a pattern (which should only match a
	 * number) if it matches, it consumes and returns the token as an integer if
	 * not, it throws an exception with an error message
	 */
	static int requireInt(String p, String message, Scanner s) {
		if (s.hasNext(p) && s.hasNextInt()) {
			return s.nextInt();
		}
		fail(message, s);
		return -1;
	}

	static int requireInt(Pattern p, String message, Scanner s) {
		if (s.hasNext(p) && s.hasNextInt()) {
			return s.nextInt();
		}
		fail(message, s);
		return -1;
	}

	/**
	 * Checks whether the next token in the scanner matches the specified
	 * pattern, if so, consumes the token and return true. Otherwise returns
	 * false without consuming anything.
	 */
	static boolean checkFor(String p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

	static boolean checkFor(Pattern p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

}

// You could add the node classes here, as long as they are not declared public (or private)
