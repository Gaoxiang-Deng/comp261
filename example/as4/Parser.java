import java.io.File;import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.*;
import javax.swing.JFileChooser;

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
	static PROGNode programNode = new PROGNode();
	/**
	 * PROG ::= STMT+
	 */
	static RobotProgramNode parseProgram(Scanner s) {
		// TODO: 1. Add basic action class implements RobotProgramNode
		// TODO: 2. Implement basic action
		if (!s.hasNext()) {
			return programNode;
		}

		// TODO: Return PROGNode  - rootNode

		RobotProgramNode parsedNode = parseSTMT(s);
		if(parsedNode != null){
			programNode.addSTMTNode(parsedNode);
		} else {
			return programNode;
		}

		return parseProgram(s);
		// THE PARSER GOES HERE
	}

	// utility methods for the parser
	static RobotProgramNode parseLoop(Scanner s) {
		if(!s.hasNext()) {
			return null;
		}
		String token = s.next();
		if(token.equals("{")) {
			RobotProgramNode blockNode = parseBlock(s);
			LoopNode loopNode = new LoopNode(blockNode);
			return loopNode;
		}

		return	null;
	}

	static RobotProgramNode parseBlock(Scanner s){
		BlockNode blockNode = new BlockNode();
		if(!s.hasNext()) {
			return null;
		}
		while (true) {
			RobotProgramNode parsedNode = parseSTMT(s);

			if(parsedNode == null) {
				break;
			} else {
				blockNode.addSTMTNode(parsedNode);
			}
		}
		return blockNode;
	}

	static RobotProgramNode parseSTMT(Scanner s) {
		if(!s.hasNext()) {
			return null;
		}
		String token = s.next();
		List<Action> list = Arrays.asList(Action.values());
		System.out.println(list);

		RobotProgramNode actionNode = parseACT(s);
		if(actionNode != null) {
			return actionNode;
		}
		if(token.equals("loop")) {
			return parseLoop(s);
		} else if(token.equals("if")){
			// TODO parse IF
			return parseIf(s);
		} else if(token.equals("elif")){
			return parseELIf(s);
		} else if(token.equals("while")){
			// TODO While
			return parseWhile(s);
		} else if(token.equals(";")) {
			return parseSTMT(s);
		}
		return null;
	}
	static RobotProgramNode parseACT(Scanner s) {
		if(!s.hasNext()) {
			return null;
		}
		String token = s.next();
		Action act = Action.getAction(token);
		ActionNode actionNode = null;


		if(Arrays.asList(Action.values()).contains(act)) {
			// If the action is not move or wait , directly return
			actionNode =  new ActionNode(act);
		}
		if(actionNode == null) {
			return null;
		}
		Scanner tmpS = s;

		RobotProgramNode expNode = parseExp(s);
		if(expNode != null) {
			actionNode.setExpNode(expNode);
			return actionNode;
		}
		token = require("(", "not has (", tmpS);

		if(token!= null) {
			// TODO parse expression
			expNode = parseExp(s);
			if(expNode == null) {
				return null;
			}
			actionNode.setExpNode(expNode);
		}

		token = require(")", "not has )", s);
		if(token != null) {
			// TODO return the actionNode
			return actionNode;
		}

		return null;
	}

	static RobotProgramNode parseExp (Scanner s) {
		if(!s.hasNext()) {
			return null;
		}
//		Scanner s1 = s;
//		Scanner s2 = s;

		// TODO Maybe here is some question for scanner next
		RobotProgramNode programNode = parseNUMNode(s, "-?[1-9][0-9]*|0");
		if(programNode != null) {
			return programNode;
		}
		programNode = parseSEN(s);
		if(programNode != null) {
			return programNode;
		}

		programNode = parseOP(s);
		if(programNode != null) {
			return programNode;
		}

		return null;
	}
	static RobotProgramNode parseOP(Scanner s) {
		if(!s.hasNext()) {
			return null;
		}

		String token = s.next();
		Op op =  Op.getOp(token);
		OPNode opNode;

		if(Arrays.asList(Op.values()).contains(op)) {
			opNode = new OPNode();
			token = require("(", "", s);

			if(token != null) {
				RobotProgramNode exp1 = parseExp(s);
				if(exp1 != null) {
					opNode.setExp1(exp1);
				}
			}
			token = require(",", "", s);
			if(token != null) {
				RobotProgramNode exp2 = parseExp(s);
				if(exp2 != null) {
					opNode.setExp1(exp2);
				}
			}
			token = require(")", "", s);
			if(token != null) {
				return opNode;
			}
		}

		return null;
	}

	static RobotProgramNode parseIf(Scanner s) {
		if (!s.hasNext()) {
			return null;
		}
		String token = s.next();
		RobotProgramNode condNode = null;
		RobotProgramNode blockNode = null;
		RobotProgramNode elseBlock = null;
		if(token.equals("(")) {
			// Note Parse COND node
			condNode = parseCOND(s);
		}
		token = s.next();
		if(token.equals(")")) {
			token = s.next();
			if(token.equals("{")){
				blockNode = parseBlock(s);
			}
		}
		if(condNode != null && blockNode != null) {
			return new IFNode(condNode, blockNode);
		}

		return null;
	}
	static RobotProgramNode parseELIf(Scanner s) {
		if (!s.hasNext()) {
			return null;
		}
		String token = s.next();
		RobotProgramNode condNode = null;
		RobotProgramNode blockNode = null;
		if(token.equals("(")) {
			// Note Parse COND node
			condNode = parseCOND(s);
		}
		token = s.next();
		if(token.equals(")")) {
			token = s.next();
			if(token.equals("{")){
				blockNode = parseBlock(s);
			}
		}
		if(condNode != null && blockNode != null) {
			return new IFNode(condNode, blockNode);
		}

		return null;
	}
	static RobotProgramNode parseWhile(Scanner s) {
		if (!s.hasNext()) {
			return null;
		}
		String token = s.next();
		RobotProgramNode condNode = null;
		RobotProgramNode blockNode = null;
		if(token.equals("(")) {
			// Note Parse COND node
			condNode = parseCOND(s);
		}
		token = s.next();
		if(token.equals(")")) {
			token = s.next();
			if(token.equals("{")) {
				blockNode = parseBlock(s);
			}
		}
		if(condNode != null && blockNode != null) {
			return new WHILENode(condNode, blockNode);
		}

		return null;
	}

	static RobotProgramNode parseCOND(Scanner s) {
		if (!s.hasNext()) {
			return null;
		}
		CONDNode condNode = new CONDNode();
		RobotProgramNode relopNode = parseRELOP(s);
		if(relopNode == null) {
			return null;
		}
		condNode.setRelopNode(relopNode);

		String token = s.next();
		if(token.equals("(")) {
			RobotProgramNode senNode = parseSEN(s);
			if(senNode == null) {
				return null;
			}
			condNode.setSenNode(senNode);
		}
		token = s.next();
		if(token.equals(",")){
			int num = parseNUM(s, "-?[0-9]+");
			condNode.setNum(num);
		}
		token = s.next();
		if (token.equals(")")){
			return condNode;
		}

		return null;
	}
	static RobotProgramNode parseRELOP(Scanner s) {
		if(!s.hasNext()) {
			return null;
		}
		String token = s.next();
		Relop relop = Relop.getRelop(token);
		if(Arrays.asList(Relop.values()).contains(relop)) {
			return new RELOPNode(relop);
		}
		return null;
	}

	static RobotProgramNode parseSEN(Scanner s) {
		if (!s.hasNext()) {
			return null;
		}
		String token = s.next();
		Sen sen = Sen.getSen(token);
		if(Arrays.asList(Sen.values()).contains(sen)) {
			return new SENNode(sen);
		}

		return null;
	}
	static int parseNUM(Scanner s, String matchStr) {
		if (!s.hasNext()) {
			return 0;
		}
		String token = s.next();
		if(token.matches(matchStr )) {
			return Integer.parseInt(token);
		}

		return 0;
	}
	static NUMNode parseNUMNode(Scanner s, String matchStr) {
		if (!s.hasNext()) {
			return null;
		}
		String token = s.next();
		if(token.matches(matchStr )) {
			return new NUMNode(Integer.parseInt(token));
		}

		return null;
	}


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

enum Action  {
	MOVE("move"),
	TURN_LEFT("turnL"),
	TURN_RIGHT("turnR"),
	TAKE_FUEL("takeFuel"),
	TURN_AROUND("turnAround"),
	SHIELD_ON("shieldOn"),
	SHIELD_OFF("shieldOff"),
	WAIT("wait");

	private String name;

	Action(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static Action getAction(String name) {
		for (Action ac : Action.values()) {
			if (ac.getName().equals(name)) {
				return ac;
			}
		}
		return null;
	}
}

enum Relop  {
	LT("lt"),
	GT("gt"),
	EQ("eq");

	private String name;

	Relop(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static Relop getRelop(String name) {
		for (Relop ac : Relop.values()) {
			if (ac.getName().equals(name)) {
				return ac;
			}
		}
		return null;
	}
}

enum Sen  {
	FUEL_LEFT("fuelLeft"),
	OPP_LR("oppLR"),
	OPP_FB("oppFB"),
	NUM_BARRELS("numBarrels"),
	BARREL_LR("barrelLR"),
	BARREL_FB("barrelFB"),
	WALL_DIST("wallDist");


	private String name;

	Sen(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static Sen getSen(String name) {
		for (Sen ac : Sen.values()) {
			if (ac.getName().equals(name)) {
				return ac;
			}
		}
		return null;
	}
}
class SENNode  implements RobotProgramNode{
	private Sen sen;
	private int value;
	SENNode(Sen sen) {
		this.sen = sen;
	}

	int getValue() {
		return value;
	}

	@Override
	public void execute(Robot robot) {
		switch (sen)	{
			case FUEL_LEFT:
				value = robot.getFuel();
				break;
			case OPP_LR:
				value = robot.getOpponentLR();
				break;
			case OPP_FB:
				value = robot.getOpponentFB();
				break;
			case BARREL_FB:
				value = robot.getClosestBarrelFB();
				break;
			case BARREL_LR:
				value = robot.getClosestBarrelLR();
				break;
			case WALL_DIST:
				value = robot.getDistanceToWall();
				break;
			case NUM_BARRELS:
				value = robot.numBarrels();
				break;
			default:
				value = 0;

		}
	}
}

class RELOPNode implements RobotProgramNode{
	private Relop relop;

	RELOPNode(Relop relop) {
		this.relop = relop;
	}

	public Relop getRelop() {
		return relop;
	}

	@Override
	public void execute(Robot robot) {
		// TODO Exec relop
	}
}


// You could add the node classes here, as long as they are not declared public (or private)

class ActionNode implements RobotProgramNode {
	private Action action;
	private RobotProgramNode expNode;

	void setExpNode(RobotProgramNode expNode) {
		this.expNode = expNode;
	}

	ActionNode (Action action) {
		this.action = action;
	}

	ActionNode (Action action, RobotProgramNode expNode) {
		String actionName = action.getName();
		if(!actionName.equals(Action.MOVE.getName()) || !actionName.equals(Action.WAIT.getName()) ) {
			throw new ParserFailureException("Only Move action and Wait action can has expression.");
		}
		this.action = action;
		this.expNode = expNode;
	}

	@Override
	public void execute(Robot robot) {
		switch (this.action)	{
			case TURN_LEFT:
				robot.turnLeft();
				break;
			case TURN_RIGHT:
				robot.turnRight();
				break;
			case MOVE:
				robot.move();
				break;
			case WAIT:
				try {
					robot.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case TAKE_FUEL:
				robot.takeFuel();
				break;
			case TURN_AROUND:
				robot.turnAround();
				break;
			case SHIELD_ON:
				robot.setShield(true);
				break;
			case SHIELD_OFF:
				robot.setShield(false);
				break;
		}
	}
	// TODO: Implements toString
}

class BlockNode implements RobotProgramNode {
	private Queue<RobotProgramNode> stmtNodes;

	BlockNode() {
		stmtNodes = new ArrayDeque<>();
	}

	public void addSTMTNode(RobotProgramNode stmtNode) {
		this.stmtNodes.add(stmtNode);
	}

	BlockNode( ArrayDeque<RobotProgramNode> stmtNodes)  {
		if(stmtNodes.isEmpty()) {
			throw new RobotInterruptedException();
		}
		this.stmtNodes = stmtNodes;
	}

	@Override
	public void execute(Robot robot) {
		// TODO Block Node
		while (!stmtNodes.isEmpty()) {
			stmtNodes.poll().execute(robot);
		}
	}
	// TODO: Add toString
}
class IFNode implements RobotProgramNode {
	private RobotProgramNode condNode;
	private RobotProgramNode blockNode;
	IFNode(RobotProgramNode condNode, RobotProgramNode blockNode) {
		this.condNode = condNode;
		this.blockNode = blockNode;
	}

	@Override
	public void execute(Robot robot) {
		CONDNode condNode = (CONDNode) this.condNode;
		condNode.execute(robot);
		if(condNode.isResult()) {
			this.blockNode.execute(robot);
		}
	}
}
class ELIFNode implements RobotProgramNode {
	private RobotProgramNode condNode;
	private RobotProgramNode blockNode;
	ELIFNode(RobotProgramNode condNode, RobotProgramNode blockNode) {
		this.condNode = condNode;
		this.blockNode = blockNode;
	}

	@Override
	public void execute(Robot robot) {
		CONDNode condNode = (CONDNode) this.condNode;
		condNode.execute(robot);
		if(condNode.isResult()) {
			this.blockNode.execute(robot);
		}
	}
}
class WHILENode implements RobotProgramNode {
	private RobotProgramNode condNode;
	private RobotProgramNode blockNode;
	WHILENode(RobotProgramNode condNode, RobotProgramNode blockNode) {
		this.condNode = condNode;
		this.blockNode = blockNode;
	}

	@Override
	public void execute(Robot robot) {
		CONDNode condNode = (CONDNode) this.condNode;
		condNode.execute(robot);
		while (condNode.isResult()) {
			this.blockNode.execute(robot);
		}
	}
}


class PROGNode implements RobotProgramNode {
	private Queue<RobotProgramNode> stmtNodes;
	PROGNode () {
		stmtNodes = new ArrayDeque<>();
	}
	public void addSTMTNode(RobotProgramNode stmtNode){
		this.stmtNodes.add(stmtNode);
	}
	PROGNode (ArrayDeque<RobotProgramNode> stmtNodes) {
		this.stmtNodes = stmtNodes;
	}

	@Override
	public void execute(Robot robot) {
		// TODO Block Node
		while (!stmtNodes.isEmpty()) {
			stmtNodes.poll().execute(robot);
		}
	}
}

class LoopNode implements RobotProgramNode {
	private RobotProgramNode block;

	LoopNode(RobotProgramNode block){
		this.block = block;
	}

	@Override
	public void execute(Robot robot) {
		// TODO Add while loop
		System.out.println("loop exec");
		block.execute(robot);
	}

	@Override
	public String toString() {
		return "loop" + this.block;
	}
}

class CONDNode implements RobotProgramNode {
	private int num;
	private String logicOp; // 'and', 'or', 'not'
	private RobotProgramNode relopNode;
	private RobotProgramNode senNode;
	private boolean result;

	public boolean isResult() {
		return result;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void setRelopNode(RobotProgramNode relopNode) {
		this.relopNode = relopNode;
	}

	public void setSenNode(RobotProgramNode senNode) {
		this.senNode = senNode;
	}
	private boolean calcRes(Robot robot){
		RELOPNode relopNode = (RELOPNode)this.relopNode;
		SENNode senNode = (SENNode) this.senNode;
		senNode.execute(robot);
		int value = senNode.getValue();
		switch (relopNode.getRelop()) {
			case LT:
				return value < num;
			case GT:
				return value > num;
			case EQ:
				return value == num;
		}
		return  false;

	}

	@Override
	public void execute(Robot robot) {
		this.result = calcRes(robot);

	}
}

class STMTNode implements RobotProgramNode {
	private RobotProgramNode programNode;

	STMTNode (RobotProgramNode programNode) {
		this.programNode = programNode;
	}



	@Override
	public void execute(Robot robot) {
		programNode.execute(robot);
	}

	@Override
	public String toString() {
		return "STMTNode{" +
				"programNode=" + programNode +
				'}';
	}
}

enum Op  {
	ADD("add"),
	SUB("sub"),
	MUL("mul"),
	DIV("dib");

	private String name;

	Op(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static Op getOp(String name) {
		for (Op op : Op.values()) {
			if (op.getName().equals(name)) {
				return op;
			}
		}
		return null;
	}
}

class OPNode implements RobotProgramNode {
	private RobotProgramNode exp1;
	private RobotProgramNode exp2;

	OPNode() {
		super();
	}

	OPNode(RobotProgramNode exp1, RobotProgramNode exp2) {
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	public void setExp1(RobotProgramNode exp1) {
		this.exp1 = exp1;
	}

	public void setExp2(RobotProgramNode exp2) {
		this.exp2 = exp2;
	}

	@Override
	public void execute(Robot robot) {

	}
}

class EXPNode implements RobotProgramNode{
	private RobotProgramNode programNode; // NUM | SEN | OP

	EXPNode(RobotProgramNode programNode) {
		this.programNode = programNode;
	}

	@Override
	public void execute(Robot robot) {

	}
}

class NUMNode implements RobotProgramNode {
	int value ;

	NUMNode(int value){
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public void execute(Robot robot) {

	}
}