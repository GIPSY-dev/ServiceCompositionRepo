package translation.utilities;

import java.util.ArrayList;
import java.util.List;
import servicecomposition.entities.ServiceNode;
import translation.entities.ASTNode;

/**
 * Utility class for abstract syntax tree generation.
 * It contains methods that provide additional services, such as, generating String representation of an AST.
 * @author Jyotsana Gupta
 */
public class ASTUtil 
{
	/**
	 * Method for generating a String representation of an abstract syntax tree.
	 * @param 	root	Root node of the abstract syntax tree to be represented
	 * @return	String representation of the source abstract syntax tree
	 */
	public static String getASTString(ASTNode root)
	{
		String astOutput = "";
		String indentation ="\t";
		List<List<String>> astNodeLists = new ArrayList<List<String>>();
		
		//Depth-first traversing of the AST
		depthFirstTraverse(root, astNodeLists, 0);
		
		//Preparing the String output from the information obtained from the traversal
		for (List<String> astNodeList : astNodeLists)
		{
			int indentLevel = Integer.parseInt(astNodeList.get(0));
			for (int i = 0; i < indentLevel; i++)
			{
				astOutput += indentation; 
			}
			
			astOutput += astNodeList.get(1);
			astOutput += "\n";
		}
		
		return astOutput;
	}
	
	/**
	 * Method for traversing the AST in depth-first sequence.
	 * @param	root			Root node of the abstract syntax tree to be traversed
	 * @param	astNodeLists	Lists to store the AST node details in the required output sequence
	 * @param	nodeLevel		Level of an AST node with respect to its parent - required for adding proper indentation
	 */
	private static void depthFirstTraverse(ASTNode root, List<List<String>> astNodeLists, int nodeLevel)
	{
		List<String> astNodeList = new ArrayList<String>();		
		astNodeList.add(nodeLevel + "");
		astNodeList.add(root.getNodeType() + getNodeValue(root));
		astNodeLists.add(astNodeList);
		
		ASTNode currChild = root.getLeftmostChild();
		nodeLevel++;
		while (currChild != null)
		{
			astNodeList.add(currChild.getNodeType() + getNodeValue(currChild));
			depthFirstTraverse(currChild, astNodeLists, nodeLevel);
			currChild = currChild.getRightSibling();
		}
	}
	
	/**
	 * Method for getting the value of a given node to be added to the AST String.
	 * @param 	astNode		Node whose value needs to be extracted
	 * @return	Value of the given node to be added to the AST String
	 */
	private static String getNodeValue(ASTNode astNode)
	{
		String nodeValue = "";
		
		if (astNode.getNodeType().equals("data"))
		{
			nodeValue = "(" + astNode.getValue().toString() + ")";
		}
		else if (astNode.getNodeType().equals("service"))
		{
			ServiceNode serviceNode = (ServiceNode)astNode.getValue();
			nodeValue = "(" + serviceNode.getService().getName() + ")";
		}
		
		return nodeValue;
	}
}