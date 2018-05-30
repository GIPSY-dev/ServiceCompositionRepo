package translation.translationprocesses;

import translation.entities.ASTNode;
import translation.entities.DataASTNode;
import translation.entities.OutputAccumASTNode;
import translation.entities.ParExecOpASTNode;
import translation.entities.SeqExecOpASTNode;
import translation.entities.ServiceASTNode;

/**
 * Abstract parent class for every concrete Visitor class.
 * @author Jyotsana Gupta
 */
public abstract class Visitor 
{
	//Default visit methods for AST nodes.
	//To be invoked in case no semantic actions need to be performed for those nodes.
	public void visit(ASTNode node)					{};
	public void visit(DataASTNode node)				{};
	public void visit(OutputAccumASTNode node)		{};
	public void visit(ParExecOpASTNode node)		{};
	public void visit(SeqExecOpASTNode node)		{};
	public void visit(ServiceASTNode node)			{};
}