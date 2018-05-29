package translation.entities;

public class DataASTNode extends ASTNode
{
	public DataASTNode(String value)
	{
		nodeType = "data";
		leftmostSibling = this;
		this.value = value;
	}
}