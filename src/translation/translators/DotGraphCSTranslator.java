package translation.translators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import service.Service;
import service.composite.layeredcompsvc.LayeredCompositeService;
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.entities.ServiceNode;
import translation.readers.csconfigreaders.CSConfiguration;
import utilities.LogUtil;
import utilities.ReadWriteUtil;

/**
 * Concrete translator for generating a Dot graph representation of a layered composite service.
 * @author Jyotsana Gupta
 */
public class DotGraphCSTranslator implements CompositeServiceTranslator
{
	private int colorIndex;
	private Map<String, List<String>> svcInfoTable;
	
	/**
	 * Default constructor.
	 */
	public DotGraphCSTranslator()
	{
		colorIndex = 0;
		svcInfoTable = new HashMap<String, List<String>>();
	}
	
	/**
	 * Method for translating a given layered composite service into a Dot graph representation.
	 * @param 	compSvcConfig	Composite service configuration containing information required for the translation
	 * @param 	logger			Logging utility object for logging error or status messages to a text file
	 * @return	Complete name and path of the Dot file generated by this translator
	 */
	public String generateFormalLangCode(CSConfiguration compSvcConfig, LogUtil logger)
	{
		//Translating composite service to Dot code for automated graph generation
		String csDotFileName = compServiceToDot(compSvcConfig, logger);
		if (csDotFileName == null)
		{
			return null;
		}
		
		//Generating graph PNG image from the Dot code
		String csImageFileName = csDotFileName.substring(0, csDotFileName.lastIndexOf(".")) + ".png";
		String procExecCmd = compSvcConfig.getDotExeName() 
								+ " -Tpng " 
								+ csDotFileName
								+ " -o "
								+ csImageFileName;
		Runtime runTime = Runtime.getRuntime();
		try 
		{
			Process process = runTime.exec(procExecCmd);
			process.waitFor();								//Waiting for the process execution to finish 
		}
		catch (IOException ioe) 
		{
			logger.log("Exception occurred while generating PNG image of the dot graph: " + ioe.getMessage());
		}
		catch (InterruptedException ie) 
		{
			logger.log("Exception occurred while generating PNG image of the dot graph: " + ie.getMessage());
		}
		
		return csDotFileName;
	}
	
	/**
	 * Method for sequentially triggering the phases involved in generation of a Dot graph for a composite service.
	 * @param 	compSvcConfig	Composite service configuration received from the user
	 * @param 	logger			Logging utility object for logging error or status messages to a text file
	 * @return	Complete name and path of the Dot file generated
	 */
	private String compServiceToDot(CSConfiguration compSvcConfig, LogUtil logger)
	{
		//Generating a map/table with service information required by various phases 
		Service compService = compSvcConfig.getCompositeService();
		ConstraintAwarePlan csPlan = ((LayeredCompositeService)compService).getCompositionPlan();
		if (!generateSvcInfoTable(csPlan, logger))
		{
			return null;
		}
		
		//Defining composite service graph's global properties
		String graphProps = defGraphProps();
		
		//Defining composite service graph
		Set<String> csOutputs = new HashSet<String>(compService.getOutput());
		String csSubgraph = defCSSubgraph(csOutputs, csPlan);
		
		//Defining user inputs fed to composite service graph
		Set<String> csInputs = new HashSet<String>(compService.getInput());
		String csInputDef = defCSInputs(csInputs, csPlan);
		
		//Defining outputs generated by composite service graph
		String csOutputDef = defCSOutputs(csOutputs);
		
		//Combining all the elements generated
		String csName = compService.getName();
		String csGraphDef = "digraph " + csName
							+ "\n" + "{"
							+ "\n" + graphProps
							+ "\n\n" + csSubgraph
							+ "\n\n" + csInputDef
							+ "\n" + csOutputDef
							+ "\n" + "}";
		
		//Writing Dot code to a file
		String csDotFileName = compSvcConfig.getDestinationFolder() + "CSDot_" + csName + ".dot";
		ReadWriteUtil.writeToTextFile(csDotFileName, csGraphDef);
				
		return csDotFileName;
	}
	
	/**
	 * Method for generating a map/table with service information required by various phases.
	 * @param 	csPlan		Constraint-aware plan of the given composite service
	 * @param 	logger		Logging utility object for logging error or status messages to a text file
	 * @return	true, if the table can be successfully generated
	 * 			false, otherwise
	 */
	private boolean generateSvcInfoTable(ConstraintAwarePlan csPlan, LogUtil logger)
	{
		//Looping through each service node in the given plan
		int svcLayerCount = csPlan.getServiceLayerCount();
		for (int i = 0; i < svcLayerCount; i++)
		{
			List<ServiceNode> currSvcLayer = csPlan.getServiceLayers().get(i);
			int layerSvcCount = currSvcLayer.size();
			for (int j = 0; j < layerSvcCount; j++)
			{
				//Forming a list containing the current service's layer index, service index and color
				String svcName = currSvcLayer.get(j).getService().getName();
				List<String> svcInfo = new ArrayList<String>();
				svcInfo.add(i + "");
				svcInfo.add(j + "");
				svcInfo.add(getNextColor());
				
				//Checking that service names are not repeated in the given plan
				if (!svcInfoTable.containsKey(svcName))
				{
					//Adding the information list to the map as value against the current service's name as key
					svcInfoTable.put(svcName, svcInfo);
				}
				else
				{
					//Aborting the translation process if duplicate service names are found in the plan
					logger.log("Multiple occurrences of service " + svcName + " found in the composition plan."
								+ "\nAborting graph generation.\n");
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Method for defining the global properties for a composite service graph.
	 * @return	Definition of composition graph's global properties
	 */
	private String defGraphProps()
	{
		String graphProps = "\t" + "rankdir = LR;" 
							+ "\n\t" + "nodesep = 0.5;" 
							+ "\n\t" + "ranksep = 1;" 
							+ "\n\t" + "edge [constraint = false];"
							+ "\n\t" + "compound = true;";		
		
		return graphProps;
	}
	
	/**
	 * Method for defining the subgraph containing the component services of the given composite service 
	 * and the connections between them.
	 * @param 	csOutputs	Outputs generated by the given composite service as a unit
	 * @param 	csPlan		Constraint-aware plan of the given composite service
	 * @return	Definition of the composite service subgraph
	 */
	private String defCSSubgraph(Set<String> csOutputs, ConstraintAwarePlan csPlan)
	{
		//Defining service layers
		int svcLayerCount = csPlan.getServiceLayerCount();
		String layerSubgraphs = "";
		for (int i = 0; i < svcLayerCount; i++)
		{
			String layerSubgraph = defLayerSubgraph(i + "", csPlan.getServiceLayers().get(i));
			layerSubgraphs += layerSubgraph + "\n\n";
		}
		
		//Defining output accumulator subgraph
		String accumSubgraph = defAccumSubgraph(svcLayerCount);
		
		//Defining service layer order and arrangement
		String layerOrderDef = defLayerOrder(svcLayerCount);
		
		//Defining connections between service nodes
		String nodeConnDefs = defSvcToSvcEdges(csPlan);
		
		//Defining connections between service nodes and output accumulator node
		String accumConnDefs = defSvcToAccumEdges(csOutputs, csPlan);
		
		//Combining all the elements generated to form a composite service subgraph
		String csSubgraph = "\t" + "subgraph clusterCS"
							+ "\n\t" + "{"
							+ "\n" + layerSubgraphs
							+ accumSubgraph
							+ "\n\n" + layerOrderDef
							+ "\n\n" + nodeConnDefs
							+ accumConnDefs
							+ "\t" + "}";
		
		return csSubgraph;
	}
	
	/**
	 * Method for defining the user inputs fed to the given composite service and 
	 * their connections to the component services.
	 * @param 	csInputs	User inputs for the given composite service
	 * @param 	csPlan		Constraint-aware plan of the given composite service
	 * @return	Definition of composite service inputs and their connections
	 */
	private String defCSInputs(Set<String> csInputs, ConstraintAwarePlan csPlan)
	{
		//Defining composite service inputs and their positions
		String inputDefs = "";
		List<String> csInputList = new ArrayList<String>(csInputs);
		for (String input : csInputList)
		{
			inputDefs += "\t\t" + "input" + csInputList.indexOf(input) 
						+ " [shape = rectangle,"
						+ " label = \"" + input + "\"];" + "\n";
		}
		inputDefs = "\t" + "{"
					+ "\n\t\t" + "rank = same;"
					+ "\n" + inputDefs
					+ "\t" + "}";
		
		//Connecting composite service inputs to component service nodes
		String inputEdgeDefs = "";
		int svcLayerCount = csPlan.getServiceLayerCount();
		for (int i = 0; i < svcLayerCount; i++)
		{
			List<ServiceNode> currSvcLayer = csPlan.getServiceLayers().get(i);
			for (ServiceNode svcNode : currSvcLayer)
			{
				//Creating a set of inputs of the current service
				Service service = svcNode.getService();
				String svcName = service.getName();
				String svcIndex = svcInfoTable.get(svcName).get(0) + svcInfoTable.get(svcName).get(1);
				Set<String> svcInputs = new HashSet<String>(service.getInput());
				
				//Checking if any service inputs are same as user inputs
				svcInputs.retainAll(csInputs);
				
				if (svcInputs.size() > 0)
				{
					//Creating a set of those inputs of the current service that are received from predecessors
					Set<String> predOutputs = new HashSet<String>();
					for (ServiceNode predNode : svcNode.getPredecessors())
					{
						predOutputs.addAll(predNode.getService().getOutput());
					}
					
					for (String sharedInput : svcInputs)
					{
						//Checking if a user input accepted by the current service is not generated by a predecessor
						if (!predOutputs.contains(sharedInput))
						{
							//Creating an edge from the user input to the current service for all such inputs
							String inputEdge = "\t" + "input" + csInputList.indexOf(sharedInput) + ":e -> c" + svcIndex + ":w"
												+ " [lhead = cluster" + svcIndex + ","
												+ " constraint = true];" + "\n";
							inputEdgeDefs += inputEdge;
						}
					}
				}	
			}
		}
		
		//Combining user inputs and their connection definitions
		String csInputDef = inputDefs + "\n" + inputEdgeDefs;
		
		return csInputDef;
	}
	
	/**
	 * Method for defining the outputs generated by the given composite service and 
	 * their connection to the output accumulator.
	 * @param 	csOutputs	Outputs generated by the composite service as a unit
	 * @return	Definition of composite service outputs and their connections
	 */
	private String defCSOutputs(Set<String> csOutputs)
	{
		//Creating a label with all output names
		String edgeLabel = "";
		for (String output : csOutputs)
		{
			edgeLabel += output + ",\\n";
		}
		if (edgeLabel.lastIndexOf(",") >= 0)
		{
			edgeLabel = edgeLabel.substring(0, edgeLabel.lastIndexOf(","));
		}
		
		//Defining composite service outputs
		String outputDef = "\t" + "csoutput [shape = rectangle, label = \"" + edgeLabel + "\"];";
				
		//Connecting the outputs to the output accumulator node
		String outputEdgeDef = "\t" + "a -> csoutput [constraint = true];";
		
		//Combining the outputs and their connection definitions
		String csOutputDef = outputDef + "\n" + outputEdgeDef;
		
		return csOutputDef;
	}
	
	/**
	 * Method for fetching the next color from the color enumeration.
	 * If all the colors from the enumeration have already been fetched for a composite service, 
	 * the color index is reset to the first color in the enumeration.
	 * @return	Name of the next color from the color enumeration
	 */
	private String getNextColor()
	{
		String nextColor = DotGraphColor.values()[colorIndex++].getValue();
		
		if (colorIndex >= DotGraphColor.values().length)
		{
			colorIndex = 0;
		}
		
		return nextColor;
	}
	
	/**
	 * Method for defining the subgraph representing a service layer.
	 * @param 	layerIndex	Index of the service layer to be represented
	 * @param 	svcLayer	Service layer (i.e., list of service nodes) to be represented
	 * @return	Service layer subgraph definition
	 */
	private String defLayerSubgraph(String layerIndex, List<ServiceNode> svcLayer)
	{
		String layerLabel = "label = \"Layer " + layerIndex + "\";";
		String layerColor = "color = dimgray;";
		
		//Defining each service node in the given layer and the connections within those nodes
		String svcNodeDefs = "";
		String layerEdgeDefs = "";
		int layerSvcCount = svcLayer.size();
		for (int i = 0; i < layerSvcCount; i++)
		{
			String svcName = svcLayer.get(i).getService().getName();
			String svcIndex = layerIndex + svcInfoTable.get(svcName).get(1);
			String svcColor = svcInfoTable.get(svcName).get(2);
			String svcNodeDef = defSvcNodeSubgraph(svcIndex, svcName, svcColor);
			String svcNodeEdgeDef = defSvcNodeEdge(svcIndex, svcColor);
			svcNodeDefs += svcNodeDef + "\n\n";
			layerEdgeDefs += svcNodeEdgeDef + "\n";
		}
		
		//In case of multiple nodes in a layer, defining the vertical arrangement between them
		if (layerSvcCount > 1)
		{
			layerEdgeDefs += defLayerNodeRanks(layerIndex, layerSvcCount) + "\n";
		}
		
		//Combining all the definitions generated to form a layer definition
		String layerSubgraph = "\t\t" + "subgraph cluster" + layerIndex
								+ "\n\t\t" + "{"
								+ "\n\t\t\t" + layerLabel
								+ "\n\t\t\t" + layerColor
								+ "\n\n" + svcNodeDefs
								+ layerEdgeDefs
								+"\t\t" + "}";
				
		return layerSubgraph;
	}
	
	/**
	 * Method for defining the output accumulator subgraph.
	 * @param 	svcLayerCount	Number of service layers in the given composite service's plan
	 * @return	Output accumulator subgraph definition
	 */
	private String defAccumSubgraph(int svcLayerCount)
	{
		String accumSubgraph = "\t\t" + "subgraph cluster" + (svcLayerCount)
								+ "\n\t\t" + "{"
								+ "\n\t\t\t" + "label = \"Output\\nAccumulator\";"
								+ "\n\t\t\t" + "color = dimgray;"
								+ "\n\t\t\t" + "a [label = \"A\", style = filled, color = gray, fillcolor = gray];"
								+ "\n\t\t" + "}";
		
		return accumSubgraph;
	}
	
	/**
	 * Method for defining the horizontal arrangement of service layers within the composite service graph.
	 * @param 	svcLayerCount	Number of service layers in the given composite service's plan
	 * @return	Definition of service layer arrangement
	 */
	private String defLayerOrder(int svcLayerCount)
	{
		//Defining relative arrangement of service layers
		String layerOrderDef = "";
		for (int i = 0; i < svcLayerCount - 1; i++)
		{
			String tailIndex = i + "0";
			String headIndex = (i + 1) + "0";
			layerOrderDef += "\t\t" + "w" + tailIndex + " -> c" + headIndex
							+ " [ltail = cluster" + tailIndex + ","
							+ " lhead = cluster" + headIndex + ","
							+ " constraint = true,"
							+ " style = invis];" + "\n";
		}
		
		//Defining relative arrangement between the last service layer and the output accumulator subgraph
		String tailIndex = (svcLayerCount - 1) + "0";
		layerOrderDef += "\t\t" + "w" + tailIndex + " -> a"
						+ " [ltail = cluster" + tailIndex + ","
						+ " constraint = true, "
						+ " style = invis];";
		
		return layerOrderDef;
	}
	
	/**
	 * Method for defining the connections between the component service nodes within the composite service graph.
	 * @param 	csPlan	Constraint-aware plan of the given composite service
	 * @return	Definition of connections between service nodes
	 */
	private String defSvcToSvcEdges(ConstraintAwarePlan csPlan)
	{
		//Looping through the service nodes of each layer except the last one
		String nodeConnDefs = "";
		int svcLayerCount = csPlan.getServiceLayerCount();
		for (int i = 0; i < svcLayerCount - 1; i++)
		{
			List<ServiceNode> currSvcLayer = csPlan.getServiceLayers().get(i);
			for (ServiceNode svcNode : currSvcLayer)
			{
				//Creating a set of outputs of the current service
				Service service = svcNode.getService();
				String svcName = service.getName();
				Set<String> svcOutputs = new HashSet<String>(service.getOutput());
				
				String tailIndex = svcInfoTable.get(svcName).get(0) + svcInfoTable.get(svcName).get(1);
				String edgeColor = svcInfoTable.get(svcName).get(2);
				
				for (ServiceNode succNode : svcNode.getSuccessors())
				{
					//Creating a set of outputs of the current service node that are inputs to its current successor
					Service succSvc = succNode.getService();
					String succName = succSvc.getName();
					Set<String> succInputs = new HashSet<String>(succSvc.getInput());
					succInputs.retainAll(svcOutputs);
					
					//Creating an edge label with the names of all the inputs from the above created set
					String edgeLabel = "";
					for (String sharedParam : succInputs)
					{
						edgeLabel += sharedParam + ",\\n";
					}
					if (edgeLabel.lastIndexOf(",") >= 0)
					{
						edgeLabel = edgeLabel.substring(0, edgeLabel.lastIndexOf(","));
					}
					
					String headIndex = svcInfoTable.get(succName).get(0) + svcInfoTable.get(succName).get(1);
					
					//Combining all the definitions generated to create a connection between service nodes
					String edgeDef = "\t\t" + "w" + tailIndex + ":e -> c" + headIndex + ":w"
									+ " [ltail = cluster" + tailIndex + ","
									+ " lhead = cluster" + headIndex + ","
									+ " label = \"" + edgeLabel + "\","
									+ " color = " + edgeColor + ","
									+ " fontcolor = " + edgeColor + "];" + "\n";
					
					nodeConnDefs += edgeDef;
				}
			}
		}
		
		return nodeConnDefs;
	}
	
	/**
	 * Method for defining the connections between component service nodes and output accumulator node. 
	 * @param 	csOutputs	Outputs generated by the composite service 
	 * @param 	csPlan		Constraint-aware plan of the given composite service
	 * @return	Definition of connections between service nodes and output accumulator node
	 */
	private String defSvcToAccumEdges(Set<String> csOutputs, ConstraintAwarePlan csPlan)
	{
		//Looping through each component service node
		String nodeConnDefs = "";
		int svcLayerCount = csPlan.getServiceLayerCount();
		for (int i = 0; i < svcLayerCount; i++)
		{
			List<ServiceNode> currSvcLayer = csPlan.getServiceLayers().get(i);
			for (ServiceNode svcNode : currSvcLayer)
			{
				//Creating a set of outputs of the current service
				Service service = svcNode.getService();
				String svcName = service.getName();
				Set<String> svcOutputs = new HashSet<String>(service.getOutput());
				
				//Checking if the current service produces any composite service outputs
				svcOutputs.retainAll(csOutputs);				
				
				//If not, no further processing is required for the current node
				if (svcOutputs.isEmpty())
				{
					continue;
				}
				
				//If yes, create an edge label with the names of all the composite service outputs 
				//produced by the current service node
				String edgeLabel = "";
				for (String sharedOutput : svcOutputs)
				{
					edgeLabel += sharedOutput + ",\\n";
				}
				if (edgeLabel.lastIndexOf(",") >= 0)
				{
					edgeLabel = edgeLabel.substring(0, edgeLabel.lastIndexOf(","));
				}
				
				String tailIndex = svcInfoTable.get(svcName).get(0) + svcInfoTable.get(svcName).get(1);
				String edgeColor = svcInfoTable.get(svcName).get(2);
				
				//Combine all the definitions generated to create a connection between
				//the current service node and the accumulator node
				String edgeDef = "\t\t" + "w" + tailIndex + ":e -> a:w"
								+ " [ltail = cluster" + tailIndex + ","
								+ " label = \"" + edgeLabel + "\","
								+ " color = " + edgeColor + ","
								+ " fontcolor = " + edgeColor + "];" + "\n";
					
				nodeConnDefs += edgeDef;
			}
		}
		
		return nodeConnDefs;
	}
	
	/**
	 * Method for defining a service node.
	 * @param 	svcIndex	Index of the service node within its service layer
	 * @param 	svcName		Name of the service of the service node
	 * @param 	svcColor	Color assigned to the service node
	 * @return	Definition of the service node
	 */
	private String defSvcNodeSubgraph(String svcIndex, String svcName, String svcColor)
	{
		//Defining the service node label and color 
		String nodeLabel = "label = \"" + svcName + "\";";
		String nodeColor = "color = " + svcColor + ";";
		
		//Defining the service of the service node
		String cnstrDef = "c" + svcIndex 
							+ " [label = \"C\","
							+ " shape = diamond,"
							+ " style = filled,"
							+ " color = " + svcColor + ","
							+ " fillcolor = " + svcColor + "];";
		
		//Defining the constraint section of the service node
		String svcDef = "w" + svcIndex 
						+ " [label = \"W\","
						+ " shape = circle,"
						+ " style = filled,"
						+ " color = " + svcColor + ","
						+ " fillcolor = " + svcColor + "];";
		
		//Combining the definitions generated for creating a service node definition
		String svcNodeSubgraph = "\t\t\t" + "subgraph cluster" + svcIndex
								+ "\n\t\t\t" + "{"
								+ "\n\t\t\t\t" + nodeLabel
								+ "\n\t\t\t\t" + nodeColor
								+ "\n\t\t\t\t" + cnstrDef
								+ "\n\t\t\t\t" + svcDef
								+ "\n\t\t\t" + "}";
		
		return svcNodeSubgraph;
	}
	
	/**
	 * Method for defining the connection between the constraint and the service within a service node.
	 * @param 	svcIndex	Index of the service node within its service layer
	 * @param 	svcColor	Color assigned to the service node
	 * @return	Definition of the constraint-service edge
	 */
	private String defSvcNodeEdge(String svcIndex, String svcColor)
	{
		String cnstrSvcEdge = "\t\t\t" +  "c" + svcIndex + " -> w" + svcIndex
								+ " [constraint = true, color = " + svcColor +"];";
		
		return cnstrSvcEdge;
	}
	
	/**
	 * Method for defining the relative vertical arrangement of service nodes within a service layer.
	 * @param 	layerIndex		Index of the service layer
	 * @param 	layerSvcCount	Number of service nodes in the service layer
	 * @return	Definition of the service node arrangement
	 */
	private String defLayerNodeRanks(String layerIndex, int layerSvcCount)
	{
		String cnstrRankDef = "";
		String svcRankDef = "";
		for (int i = layerSvcCount - 1; i >= 0; i--)
		{
			cnstrRankDef += "c" + layerIndex + i;
			svcRankDef += "w" + layerIndex + i;
			if (i > 0)
			{
				cnstrRankDef += " -> ";
				svcRankDef += " -> ";
			}
		}
		cnstrRankDef += " [constraint = false, style = invis];";
		svcRankDef += " [constraint = false, style = invis];";
		
		String nodeRankDef = "\t\t\t" + cnstrRankDef
							+ "\n\t\t\t" + svcRankDef;
		
		return nodeRankDef;
	}
}