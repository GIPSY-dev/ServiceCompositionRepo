package translation.entities;

/**
 * Concrete class for data AST node.
 * It represents those input parameters of a service whose values are provided by the service requester.
 * @author Jyotsana Gupta
 */
public class DataASTNode extends ASTNode
{
	/**
	 * Parameterized constructor.
	 * @param 	value	Value of the input parameter represented by this data AST node
	 */
	public DataASTNode(String value)
	{
		nodeType = "data";
		leftmostSibling = this;
		this.value = value;
	}
}