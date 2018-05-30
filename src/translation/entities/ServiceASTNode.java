package translation.entities;

import servicecomposition.entities.ServiceNode;
import translation.translationprocesses.Visitor;

/**
 * Concrete class for service AST node.
 * It represents a service node from the source constraint-aware plan.
 * @author Jyotsana Gupta
 */
public class ServiceASTNode extends ASTNode
{
	/**
	 * Parameterized constructor.
	 * @param 	value	Service node from the source constraint-aware plan represented by this AST node
	 */
	public ServiceASTNode(ServiceNode value)
	{
		nodeType = "service";
		leftmostSibling = this;
		this.value = value;
	}
	
	/**
	 * Accept method for this node as part of the Visitor pattern.
	 */
	public void accept(Visitor visitor) 
	{
		visitor.visit(this);
	}
}