package translation.entities;

/**
 * Abstract parent class for every concrete AST node class.
 * @author Jyotsana Gupta
 */
public abstract class ASTNode 
{
	protected ASTNode parent;
	protected ASTNode leftmostSibling;
	protected ASTNode rightSibling;
	protected ASTNode leftmostChild;
	protected String nodeType;
	protected Object value;
	
	/**
	 * Default constructor.
	 */
	public ASTNode()
	{
		parent = null;
		leftmostSibling = null;
		rightSibling = null;
		leftmostChild = null;
		nodeType = null;
		value = null;
	}
	
	/**
	 * Method for fetching the parent node to this AST node.
	 * @return	Reference to the parent node
	 */
	public ASTNode getParent()
	{
		return parent;
	}
	
	/**
	 * Method for fetching the leftmost sibling node to this AST node.
	 * @return	Reference to the leftmost sibling node
	 */
	public ASTNode getLeftmostSibling()
	{
		return leftmostSibling;
	}
	
	/**
	 * Method for fetching the immediate right node to this AST node.
	 * @return	Reference to the immediate right node
	 */
	public ASTNode getRightSibling()
	{
		return rightSibling;
	}
	
	/**
	 * Method for fetching the leftmost child node to this AST node.
	 * @return	Reference to the leftmost child node
	 */
	public ASTNode getLeftmostChild()
	{
		return leftmostChild;
	}
	
	/**
	 * Method for fetching the type of this AST node.
	 * @return	Type of AST node
	 */
	public String getNodeType()
	{
		return nodeType;
	};
	
	/**
	 * Method for fetching the value stored by this AST node (if any).
	 * @return	Value stored by AST node (if any)
	 * 			null, otherwise
	 */
	public Object getValue()
	{
		return value;
	}
	
	/**
	 * Method for assigning the parent node to this AST node.
	 * @param	parent	Reference to the parent node
	 */
	public void setParent(ASTNode parent)
	{
		this.parent = parent;
	}
	
	/**
	 * Method for assigning the leftmost sibling node to this AST node.
	 * @param	parent	Reference to the leftmost sibling node
	 */
	public void setLeftmostSibling(ASTNode leftmostSibling)
	{
		this.leftmostSibling = leftmostSibling;
	}
	
	/**
	 * Method for assigning the immediate right node to this AST node.
	 * @param	parent	Reference to the immediate right node
	 */
	public void setRightSibling(ASTNode rightSibling)
	{
		this.rightSibling = rightSibling;
	}
	
	/**
	 * Method for assigning the leftmost child node to this AST node.
	 * @param	parent	Reference to the leftmost child node
	 */
	public void setLeftmostChild(ASTNode leftmostChild)
	{
		this.leftmostChild = leftmostChild;
	}
	
	/**
	 * Method for attaching a list of siblings to the right of the sibling-list of which this AST node is a part.
	 * @param	sibling		Reference to the leftmost member of the sibling list to be attached to the right
	 */
	public void makeSiblings(ASTNode sibling)
	{
		//Finding the rightmost node in the left list
		ASTNode xSibs = this;		
		while (xSibs.getRightSibling() != null)
		{
			xSibs = xSibs.getRightSibling();
		}
		
		//Joining the lists
		ASTNode ySibs = sibling.getLeftmostSibling();
		xSibs.setRightSibling(ySibs);
		
		//Setting the pointers for the new combined list
		//If the left list has a parent but the right one doesn't, assign the left parent to all the siblings
		if ((xSibs.getParent() != null) && (ySibs.getParent() == null))
		{
			ySibs.setLeftmostSibling(xSibs.getLeftmostSibling());
			ySibs.setParent(xSibs.getParent());
			
			while (ySibs.getRightSibling() != null)
			{
				ySibs = ySibs.getRightSibling();
				ySibs.setLeftmostSibling(xSibs.getLeftmostSibling());
				ySibs.setParent(xSibs.getParent());
			}			
		}
		//If the right list has a parent but the left one doesn't, assign the right parent to all the siblings
		else if ((xSibs.getParent() == null) && (ySibs.getParent() != null))
		{
			ySibs.setLeftmostSibling(xSibs.getLeftmostSibling());			
			while (ySibs.getRightSibling() != null)
			{
				ySibs = ySibs.getRightSibling();
				ySibs.setLeftmostSibling(xSibs.getLeftmostSibling());
			}
			
			xSibs = xSibs.getLeftmostSibling();
			xSibs.setParent(ySibs.getParent());
			while (xSibs.getRightSibling().getParent() == null)
			{
				xSibs = xSibs.getRightSibling();
				xSibs.setParent(ySibs.getParent());
			}
			xSibs.getParent().setLeftmostChild(xSibs.getLeftmostSibling());
		}
		//If neither of the lists has a parent, leave the parent reference as null for all the siblings
		else if ((xSibs.getParent() == null) && (ySibs.getParent() == null))
		{
			ySibs.setLeftmostSibling(xSibs.getLeftmostSibling());			
			while (ySibs.getRightSibling() != null)
			{
				ySibs = ySibs.getRightSibling();
				ySibs.setLeftmostSibling(xSibs.getLeftmostSibling());
			}
		}
	}
	
	/**
	 * Method for attaching child nodes to this AST node.
	 * @param	child	Reference to the leftmost member of the sibling-list of child nodes
	 */
	public void adoptChildren(ASTNode child)
	{
		//If this AST node already has some children
		if (this.getLeftmostChild() != null)
		{
			this.getLeftmostChild().makeSiblings(child);			
		}
		//If this AST node does not have any children currently
		else
		{
			ASTNode ySibs = child.getLeftmostSibling();
			this.setLeftmostChild(ySibs);
			
			while (ySibs != null)
			{
				ySibs.setParent(this);
				ySibs = ySibs.getRightSibling();
			}
		}
	}
}