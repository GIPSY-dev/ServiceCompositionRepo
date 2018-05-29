package translation.entities;

/**
 * Concrete class for output accumulator AST node. It serves as the root node of the AST.
 * It represents the processing node that accumulates the final (requested) output parameters into a single data structure.
 * @author Jyotsana Gupta
 */
public class OutputAccumASTNode extends ASTNode
{
	/**
	 * Default constructor.
	 */
	public OutputAccumASTNode()
	{
		nodeType = "outputAccumulator";
		leftmostSibling = this;
	}
}