package translation.entities;

import servicecomposition.entities.ServiceNode;

public class ServiceASTNode extends ASTNode
{
	public ServiceASTNode(ServiceNode value)
	{
		nodeType = "service";
		leftmostSibling = this;
		this.value = value;
	}
}