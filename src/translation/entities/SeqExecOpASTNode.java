package translation.entities;

import translation.translationprocesses.Visitor;

/**
 * Concrete class for sequential execution operator AST node.
 * All the operands of a sequential execution operator must be executed.
 * The order of execution of operands (child nodes) is from left to right.
 * @author Jyotsana Gupta
 */
public class SeqExecOpASTNode extends ASTNode
{
	/**
	 * Default constructor.
	 */
	public SeqExecOpASTNode()
	{
		nodeType = "seqExecOp";
		leftmostSibling = this;
	}
	
	/**
	 * Accept method for this node as part of the Visitor pattern.
	 */
	public void accept(Visitor visitor) 
	{
		visitor.visit(this);
	}
}