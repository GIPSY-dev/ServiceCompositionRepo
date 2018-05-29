package translation.entities;

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
}