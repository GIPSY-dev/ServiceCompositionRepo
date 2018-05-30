package translation.entities;

import translation.translationprocesses.Visitor;

/**
 * Concrete class for parallel execution operator AST node.
 * All the operands of a parallel execution operator must be executed
 * The order of execution of operands is not important.
 * @author Jyotsana Gupta
 */
public class ParExecOpASTNode extends ASTNode
{
	/**
	 * Default constructor.
	 */
	public ParExecOpASTNode()
	{
		nodeType = "parExecOp";
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