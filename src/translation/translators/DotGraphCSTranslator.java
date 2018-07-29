package translation.translators;

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

public class DotGraphCSTranslator implements CompositeServiceTranslator
{
	private int colorIndex;
	private Map<String, List<String>> svcInfoTable;
	
	public DotGraphCSTranslator()
	{
		colorIndex = 0;
		svcInfoTable = new HashMap<String, List<String>>();
	}
	
	public String generateFormalLangCode(CSConfiguration compSvcConfig, LogUtil logger)
	{
		//Translating composite service to dot code for automated graph generation
		String csDotFileName = compServiceToDot(compSvcConfig, logger);
		if (csDotFileName == null)
		{
			return null;
		}
		
		//Generating graph PNG image from the dot code
		String csGraphFileName = csDotFileName.substring(0, csDotFileName.lastIndexOf(".")) + ".png";
		String procExecCmd = compSvcConfig.getDotExeName() 
								+ " -Tpng " 
								+ csDotFileName
								+ " -o "
								+ csGraphFileName;
		Runtime runTime = Runtime.getRuntime();
		Process process = runTime.exec(procExecCmd);
		
		return csGraphFileName;
	}
	
	private String compServiceToDot(CSConfiguration compSvcConfig, LogUtil logger)
	{
		Service compService = compSvcConfig.getCompositeService();
		ConstraintAwarePlan csPlan = ((LayeredCompositeService)compService).getCompositionPlan();
		if (!generateSvcInfoTable(csPlan, logger))
		{
			return null;
		}
		
		String graphProps = defGraphProps();
		
		Set<String> csOutputs = new HashSet<String>(compService.getOutput());
		String csSubgraph = defCSSubgraph(csOutputs, csPlan);
		
		Set<String> csInputs = new HashSet<String>(compService.getInput());
		String csInputDef = defCSInputs(csInputs, csPlan);
		
		String csOutputDef = defCSOutputs(csOutputs);
		
		String csName = compService.getName();
		String csGraphDef = "digraph " + csName
							+ "\n" + "{"
							+ "\n" + graphProps
							+ "\n\n" + csSubgraph
							+ "\n\n" + csInputDef
							+ "\n" + csOutputDef
							+ "\n" + "}";
		
		return csGraphDef;
	}
	
	private boolean generateSvcInfoTable(ConstraintAwarePlan csPlan, LogUtil logger)
	{
		int svcLayerCount = csPlan.getServiceLayerCount();
		for (int i = 0; i < svcLayerCount; i++)
		{
			List<ServiceNode> currSvcLayer = csPlan.getServiceLayers().get(i);
			int layerSvcCount = currSvcLayer.size();
			for (int j = 0; j < layerSvcCount; j++)
			{
				String svcName = currSvcLayer.get(j).getService().getName();
				List<String> svcInfo = new ArrayList<String>();
				svcInfo.add(i + "");
				svcInfo.add(j + "");
				svcInfo.add(getNextColor());
				
				if (!svcInfoTable.containsKey(svcName))
				{
					svcInfoTable.put(svcName, svcInfo);
				}
				else
				{
					logger.log("Multiple occurrences of service " + svcName + " found in the composition plan."
								+ "\nAborting graph generation.\n");
					return false;
				}
			}
		}
		
		return true;
	}
	
	private String defGraphProps()
	{
		String graphProps = "\t" + "rankdir = LR;" 
							+ "\n\t" + "nodesep = 0.5;" 
							+ "\n\t" + "ranksep = 1;" 
							+ "\n\t" + "edge [constraint = false];"
							+ "\n\t" + "compound = true;";		
		
		return graphProps;
	}
	
	private String defCSSubgraph(Set<String> csOutputs, ConstraintAwarePlan csPlan)
	{
		int svcLayerCount = csPlan.getServiceLayerCount();
		String layerSubgraphs = "";
		for (int i = 0; i < svcLayerCount; i++)
		{
			String layerSubgraph = defLayerSubgraph(i + "", csPlan.getServiceLayers().get(i));
			layerSubgraphs += layerSubgraph + "\n\n";
		}
		
		String accumSubgraph = defAccumSubgraph(svcLayerCount);
		
		String layerOrderDef = defLayerOrder(svcLayerCount);
		
		String nodeConnDefs = defSvcToSvcEdges(csPlan);
		
		String accumConnDefs = defSvcToAccumEdges(csOutputs, csPlan);
		
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
	
	private String defCSInputs(Set<String> csInputs, ConstraintAwarePlan csPlan)
	{
		String inputDefs = "";
		for (String input : csInputs)
		{
			inputDefs += "\t\t" + input + " [shape = \"point\"];" + "\n";
		}
		inputDefs += "\t" + "{"
					+ "\n\t\t" + "rank = same;"
					+ "\n" + inputDefs
					+ "\t" + "}";
		
		String inputEdgeDefs = "";
		int svcLayerCount = csPlan.getServiceLayerCount();
		for (int i = 0; i < svcLayerCount; i++)
		{
			List<ServiceNode> currSvcLayer = csPlan.getServiceLayers().get(i);
			for (ServiceNode svcNode : currSvcLayer)
			{
				Service service = svcNode.getService();
				String svcName = service.getName();
				String svcIndex = svcInfoTable.get(svcName).get(0) + svcInfoTable.get(svcName).get(1);
				Set<String> svcInputs = new HashSet<String>(service.getInput());
				
				svcInputs.retainAll(csInputs);
				
				if (svcInputs.size() > 0)
				{
					Set<String> predOutputs = new HashSet<String>();
					for (ServiceNode predNode : svcNode.getPredecessors())
					{
						predOutputs.addAll(predNode.getService().getOutput());
					}
					
					for (String sharedInput : svcInputs)
					{
						if (!predOutputs.contains(sharedInput))
						{
							String inputEdge = "\t" + sharedInput + ":e -> c" + svcIndex + ":w"
												+ " [lhead = cluster" + svcIndex + ","
												+ " constraint = true,"
												+ " taillabel = \"" + sharedInput + "\"];" + "\n";
							inputEdgeDefs += inputEdge;
						}
					}
				}	
			}
		}
		
		String csInputDef = inputDefs + "\n" + inputEdgeDefs;
		
		return csInputDef;
	}
	
	private String defCSOutputs(Set<String> csOutputs)
	{
		String outputDef = "\t" + "csoutput [shape = point];";
		
		String edgeLabel = "";
		for (String output : csOutputs)
		{
			edgeLabel += output + ", ";
		}
		if (edgeLabel.lastIndexOf(",") >= 0)
		{
			edgeLabel = edgeLabel.substring(0, edgeLabel.lastIndexOf(","));
		}
		String outputEdgeDef = "\t" + "a -> csoutput [constraint = true, label = \"" + edgeLabel + "\"];";
		
		String csOutputDef = outputDef + "\n" + outputEdgeDef;
		
		return csOutputDef;
	}
	
	private String getNextColor()
	{
		String nextColor = DotGraphColor.values()[colorIndex++].getValue();
		
		if (colorIndex >= DotGraphColor.values().length)
		{
			colorIndex = 0;
		}
		
		return nextColor;
	}
	
	private String defLayerSubgraph(String layerIndex, List<ServiceNode> svcLayer)
	{
		String layerLabel = "label = \"Layer " + layerIndex + "\";";
		String layerColor = "color = dimgray;";
		
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
		
		if (layerSvcCount > 1)
		{
			layerEdgeDefs += defLayerNodeRanks(layerIndex, layerSvcCount) + "\n";
		}
		
		String layerSubgraph = "\t\t" + "subgraph cluster" + layerIndex
								+ "\n\t\t" + "{"
								+ "\n\t\t\t" + layerLabel
								+ "\n\t\t\t" + layerColor
								+ "\n\n\t\t\t" + svcNodeDefs
								+ layerEdgeDefs
								+"\t\t" + "}";
				
		return layerSubgraph;
	}
	
	private String defAccumSubgraph(int svcLayerCount)
	{
		String accumSubgraph = "\t\t" + "subgraph cluster" + (svcLayerCount + 1)
								+ "\n\t\t" + "{"
								+ "\n\t\t\t" + "label = \"Output\nAccumulator\";"
								+ "\n\t\t\t" + "color = dimgray;"
								+ "\n\t\t\t" + "a [label = \"A\", style = filled, color = gray, fillcolor = gray];"
								+ "\n\t\t" + "}";
		
		return accumSubgraph;
	}
	
	private String defLayerOrder(int svcLayerCount)
	{
		String layerOrderDef = "";
		for (int i = 0; i < svcLayerCount - 1; i++)
		{
			String tailIndex = i + "0";
			String headIndex = (i + 1) + "0";
			layerOrderDef += "\t\t" + "w" + tailIndex + " -> c" + headIndex
							+ " [ltail = cluster" + tailIndex + ","
							+ " [lhead = cluster" + headIndex + ","
							+ " constraint = true,"
							+ " style = invis];" + "\n";
		}
		
		String tailIndex = (svcLayerCount - 1) + "0";
		layerOrderDef += "\t\t" + "w" + tailIndex + " -> a"
						+ " [ltail = cluster" + tailIndex + ","
						+ " constraint = true, "
						+ " style = invis];";
		
		return layerOrderDef;
	}
	
	private String defSvcToSvcEdges(ConstraintAwarePlan csPlan)
	{
		String nodeConnDefs = "";
		int svcLayerCount = csPlan.getServiceLayerCount();
		for (int i = 0; i < svcLayerCount - 1; i++)
		{
			List<ServiceNode> currSvcLayer = csPlan.getServiceLayers().get(i);
			for (ServiceNode svcNode : currSvcLayer)
			{
				Service service = svcNode.getService();
				String svcName = service.getName();
				Set<String> svcOutputs = new HashSet<String>(service.getOutput());
				
				String tailIndex = svcInfoTable.get(svcName).get(0) + svcInfoTable.get(svcName).get(1);
				String edgeColor = svcInfoTable.get(svcName).get(2);
				
				for (ServiceNode succNode : svcNode.getSuccessors())
				{
					Service succSvc = succNode.getService();
					String succName = succSvc.getName();
					Set<String> succInputs = new HashSet<String>(succSvc.getInput());
					succInputs.retainAll(svcOutputs);
					
					String edgeLabel = "";
					for (String sharedParam : succInputs)
					{
						edgeLabel += sharedParam + ",\n";
					}
					if (edgeLabel.lastIndexOf(",") >= 0)
					{
						edgeLabel = edgeLabel.substring(0, edgeLabel.lastIndexOf(","));
					}
					
					String headIndex = svcInfoTable.get(succName).get(0) + svcInfoTable.get(succName).get(1);
					
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
	
	private String defSvcToAccumEdges(Set<String> csOutputs, ConstraintAwarePlan csPlan)
	{
		String nodeConnDefs = "";
		int svcLayerCount = csPlan.getServiceLayerCount();
		for (int i = 0; i < svcLayerCount - 1; i++)
		{
			List<ServiceNode> currSvcLayer = csPlan.getServiceLayers().get(i);
			for (ServiceNode svcNode : currSvcLayer)
			{
				Service service = svcNode.getService();
				String svcName = service.getName();
				Set<String> svcOutputs = new HashSet<String>(service.getOutput());
				svcOutputs.retainAll(csOutputs);				
				
				if (svcOutputs.isEmpty())
				{
					continue;
				}
				
				String edgeLabel = "";
				for (String sharedOutput : svcOutputs)
				{
					edgeLabel += sharedOutput + ",\n";
				}
				if (edgeLabel.lastIndexOf(",") >= 0)
				{
					edgeLabel = edgeLabel.substring(0, edgeLabel.lastIndexOf(","));
				}
				
				String tailIndex = svcInfoTable.get(svcName).get(0) + svcInfoTable.get(svcName).get(1);
				String edgeColor = svcInfoTable.get(svcName).get(2);
				
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
	
	private String defSvcNodeSubgraph(String svcIndex, String svcName, String svcColor)
	{
		String nodeLabel = "label = \"" + svcName + "\";";
		String nodeColor = "color = " + svcColor + ";";
		String cnstrDef = "c" + svcIndex 
							+ " [label = \"C\","
							+ " shape = diamond,"
							+ " style = filled,"
							+ " color = " + svcColor + ","
							+ " fillcolor = " + svcColor + "];";
		String svcDef = "w" + svcIndex 
						+ " [label = \"W\","
						+ " shape = circle,"
						+ " style = filled,"
						+ " color = " + svcColor + ","
						+ " fillcolor = " + svcColor + "];";
		
		String svcNodeSubgraph = "\t\t\t" + "subgraph cluster" + svcIndex
								+ "\n\t\t\t" + "{"
								+ "\n\t\t\t\t" + nodeLabel
								+ "\n\t\t\t\t" + nodeColor
								+ "\n\t\t\t\t" + cnstrDef
								+ "\n\t\t\t\t" + svcDef
								+ "\n\t\t\t" + "}";
		
		return svcNodeSubgraph;
	}
	
	private String defSvcNodeEdge(String svcIndex, String svcColor)
	{
		String cnstrSvcEdge = "\t\t\t" +  "c" + svcIndex + " -> w" + svcIndex
								+ "[constraint = true, color = " + svcColor +"];";
		
		return cnstrSvcEdge;
	}
	
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