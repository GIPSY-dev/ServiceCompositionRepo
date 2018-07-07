package utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import constraint.Constraint;
import constraint.ConstraintXMLParser;
import service.BasicService;
import service.ConstrainedService;
import service.Service;
import service.composite.layeredcompsvc.LayeredCompositeService;
import service.parser.BasicServiceParser;
import service.parser.ServiceFileParserDecorator;
import service.parser.ServiceSerializedParser;
import service.writer.BasicServiceWriter;
import service.writer.ServiceFileWriterDecorator;
import service.writer.ServiceSerializedWriter;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.entities.ServiceNode;

/**
 * Utility class for creating composite services and storing them in service repository.
 * @author Jyotsana Gupta
 */
public class CompSvcStorageUtil 
{
	/**
	 * Method for creating a composite service.
	 * @param 	compRequest		Service composition request for which composition plan was created
	 * @param 	cnstrAwrPlan	Constraint-aware plan created for the composition request
	 * @return	Layered composite service created
	 */
	public static Service createCompositeService(CompositionRequest compRequest, ConstraintAwarePlan cnstrAwrPlan)
	{
		//Gathering composite service elements from the composition request
		String svcName = "CompSvc_" + System.nanoTime();
		ArrayList<String> svcInputs = (ArrayList<String>) compRequest.getInputs();
		ArrayList<String> svcOutputs = (ArrayList<String>) compRequest.getOutputs();
		
		Set<Constraint> compSvcCnstrSet = new HashSet<Constraint>();
		Set<String> compSvcEffectSet = new HashSet<String>();
		for (List<ServiceNode> serviceLayer : cnstrAwrPlan.getServiceLayers())
		{
			for (ServiceNode serviceNode : serviceLayer)
			{
				compSvcCnstrSet.addAll(serviceNode.getConstraints());
				compSvcEffectSet.addAll(((ConstrainedService)serviceNode.getService()).getEffects());
			}
		}
		ArrayList<Constraint> svcConstraints = new ArrayList<Constraint>(compSvcCnstrSet);	
		ArrayList<String> svcEffects = new ArrayList<String>(compSvcEffectSet);
		
		//Creating constrained service without the composition plan
		ConstrainedService cnstrdService = new ConstrainedService(new BasicService(svcName, svcInputs, svcOutputs), svcConstraints, svcEffects);
		
		//Creating composite service from the constrained service and composition plan
		Service layeredCS = new LayeredCompositeService(cnstrdService, cnstrAwrPlan);
		
		return layeredCS;
	}
	
	/**
	 * Method for appending a list of layered composite services to a repository of serialized service objects.
	 * @param 	compSvcs		List of layered composite services to be appended 
	 * @param 	repoFileName	Complete name and path of the destination repository
	 */
	public static void writeCSToSerialSvcRepo(ArrayList<Service> compSvcs, String repoFileName)
	{
		ServiceFileParserDecorator svcParser = new ServiceSerializedParser(new BasicServiceParser());
		svcParser.setLocation(repoFileName);
		ArrayList<Service> existingSvcs = svcParser.parse();
		
		if (existingSvcs == null)
		{
			existingSvcs = new ArrayList<Service>();
		}
		existingSvcs.addAll(compSvcs);
		
		ServiceFileWriterDecorator svcWriter = new ServiceSerializedWriter(new BasicServiceWriter());
		svcWriter.setLocation(repoFileName);
		svcWriter.write(existingSvcs);
	}
	
	/**
	 * Method for parsing a serialized composite service repository file to extract a composite service.
	 * @param 	csRepoFileName	Complete name and path of the serialized composite service repository file
	 * @param	csName			Name of the target composite service
	 * @param 	logger			Logging utility object for logging error or status messages to a text file
	 * @return	Composite service object, if it can be extracted
	 * 			Null, otherwise
	 */
	public static Service readCSFromSerialSvcRepo(String csRepoFileName, String csName, LogUtil logger)
	{
		ServiceFileParserDecorator svcParser = new ServiceSerializedParser(new BasicServiceParser());
		svcParser.setLocation(csRepoFileName);
		ArrayList<Service> services = svcParser.parse();
		
		if ((services == null) || (services.size() == 0))
		{
			logger.log("No services exist in the serialized composite service source file.\n"
						+ "Aborting translation process.\n");
		}
		else
		{
			for (Service service : services)
			{
				if (service.getName().equals(csName))
				{
					return service;
				}
			}
		}
		
		return null;
	}
	
	public static void writeCSToXMLFile(LayeredCompositeService compSvc, String compSvcFileName, LogUtil logger)
	{
		//TODO
	}
	
	public static Service readCSFromXMLFile(String compSvcFileName, LogUtil logger)
	{
		//Creating an XML document for the composite service file
		Document doc = ReadWriteUtil.getXmlDocument(compSvcFileName);
		
		//Fetching inner constrained service details from the file
		String csName = ReadWriteUtil.getXMLTagValue("csname", "value", doc, null);
		ArrayList<String> csInputs = (ArrayList<String>)parseSvcStringDetails(doc, null, "csinputs");
		ArrayList<String> csOutputs = (ArrayList<String>)parseSvcStringDetails(doc, null, "csoutputs");
		ArrayList<String> csEffects = (ArrayList<String>)parseSvcStringDetails(doc, null, "cseffects");
		ArrayList<Constraint> csConstraints = (ArrayList<Constraint>)parseSvcConstraints(doc, null, "csconstraints");
		
		//Parsing the component atomic services
		List<Service> atomicSvcs = parseCSAtomicServices(doc);
		
		//Fetching the composition plan from the file
		ConstraintAwarePlan cnstrAwrPlan = parseCSPlan(doc, atomicSvcs);
		
		//Creating constrained service without the composition plan
		ConstrainedService cnstrdService = new ConstrainedService(new BasicService(csName, csInputs, csOutputs), csConstraints, csEffects);
		
		//Creating composite service from the constrained service and composition plan
		Service layeredCS = new LayeredCompositeService(cnstrdService, cnstrAwrPlan);
		
		return layeredCS;
	}
	
	private static List<String> parseSvcStringDetails(Document doc, Element elem, String tagName)
	{
		List<String> csFeatures = new ArrayList<String>();
		
		Node csFeaturesNode = null;
		if (doc != null)
		{
			csFeaturesNode = doc.getElementsByTagName(tagName).item(0);
		}
		else if (elem != null)
		{
			csFeaturesNode = elem.getElementsByTagName(tagName).item(0);
		}
			
		if (csFeaturesNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element csFeaturesElem = (Element)csFeaturesNode;
			NodeList csFeatureInstances = csFeaturesElem.getElementsByTagName("instance");
			for (int i = 0; i < csFeatureInstances.getLength(); i++)
			{
				Node csFeatureInstance = csFeatureInstances.item(i);
				if (csFeatureInstance.getNodeType() == Node.ELEMENT_NODE)
				{
					Element csFeatureElem = (Element)csFeatureInstance;
					String csFeature = csFeatureElem.getAttribute("name");
					csFeatures.add(csFeature);
				}
			}
		}
		
		return csFeatures;
	}
	
	private static List<Constraint> parseSvcConstraints(Document doc, Element elem, String tagName)
	{
		List<Constraint> csConstraints = new ArrayList<Constraint>();
		
		Node csConstraintsNode = null;
		if (doc != null)
		{
			csConstraintsNode = doc.getElementsByTagName(tagName).item(0);
		}
		else if (elem != null)
		{
			csConstraintsNode = elem.getElementsByTagName(tagName).item(0);
		}
		
		if (csConstraintsNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element csConstraintsElem = (Element)csConstraintsNode;
			NodeList csConstraintsInstances = csConstraintsElem.getElementsByTagName("instance");
			for (int i = 0; i < csConstraintsInstances.getLength(); i++)
			{
				Node csConstraintInstance = csConstraintsInstances.item(i);
				ConstraintXMLParser cnstrParser = new ConstraintXMLParser();
				Constraint csConstraint = cnstrParser.parseConstraintNode(csConstraintInstance);
				csConstraints.add(csConstraint);
			}
		}
		
		return csConstraints;
	}
	
	private static List<Service> parseCSAtomicServices(Document doc)
	{
		List<Service> atomicServices = new ArrayList<Service>();
		Node csAtomicSvcsNode = doc.getElementsByTagName("csatomicservices").item(0);
		if (csAtomicSvcsNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element csAtomicSvcsElem = (Element)csAtomicSvcsNode;
			NodeList csAtomicSvcNodes = csAtomicSvcsElem.getElementsByTagName("service");
			atomicServices = parseAtomicServiceNodes(csAtomicSvcNodes);
		}
		
		return atomicServices;
	}
	
	private static List<Service> parseAtomicServiceNodes(NodeList atomicSvcNodes)
	{
		List<Service> atomicServices = new ArrayList<Service>();
		
		//Creating Service objects from the nodes and adding them to a list
		for (int i = 0; i < atomicSvcNodes.getLength(); i++)
		{
			Node atomicSvcNode = atomicSvcNodes.item(i);
			if (atomicSvcNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element atomicSvcElem = (Element)atomicSvcNode;
				
				//Fetching service details from the service element
				String svcName = atomicSvcElem.getAttribute("name");
				ArrayList<String> svcInputs = (ArrayList<String>)parseSvcStringDetails(null, atomicSvcElem, "inputs");
				ArrayList<String> svcOutputs = (ArrayList<String>)parseSvcStringDetails(null, atomicSvcElem, "outputs");
				ArrayList<String> svcEffects = (ArrayList<String>)parseSvcStringDetails(null, atomicSvcElem, "effects");
				ArrayList<Constraint> svcConstraints = (ArrayList<Constraint>)parseSvcConstraints(null, atomicSvcElem, "constraints");
							
				Service service = new ConstrainedService(new BasicService(svcName, svcInputs, svcOutputs), svcConstraints, svcEffects);
				atomicServices.add(service);
			}
		}
		
		return atomicServices;
	}
	
	private static ConstraintAwarePlan parseCSPlan(Document doc, List<Service> atomicSvcs)
	{
		ConstraintAwarePlan cnstrAwrPlan = null;
		Node csPlanNode = doc.getElementsByTagName("csplan").item(0);
		if (csPlanNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element csPlanElem = (Element)csPlanNode;
			NodeList svcLayerNodes = csPlanElem.getElementsByTagName("servicelayer");
			int svcLayerCount = svcLayerNodes.getLength();
			cnstrAwrPlan = new ConstraintAwarePlan(svcLayerCount);
			
			for (int i = 0; i < svcLayerCount; i++)
			{
				Node svcLayerNode = svcLayerNodes.item(i);
				if (svcLayerNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element svcLayerElem = (Element)svcLayerNode;
					NodeList xmlSvcNodes = svcLayerElem.getElementsByTagName("servicenode");
					for (int j = 0; j < xmlSvcNodes.getLength(); j++)
					{
						Node xmlSvcNode = xmlSvcNodes.item(j);
						if (xmlSvcNode.getNodeType() == Node.ELEMENT_NODE)
						{
							Element svcNodeElem = (Element)xmlSvcNode;
							ServiceNode serviceNode = parseServiceNode(svcNodeElem, atomicSvcs, i);
							
							cnstrAwrPlan.addServiceNode(serviceNode);
							
							List<String[]> predDetails = parseSvcPredecessors(svcNodeElem);
							assignPredsSuccs(serviceNode, predDetails, cnstrAwrPlan);
						}
					}
				}
			}
		}
		
		return cnstrAwrPlan;
	}
	
	private static ServiceNode parseServiceNode(Element svcNodeElem, List<Service> atomicSvcs, int svcLayerIndex)
	{
		Service service = null;
		Node svcNameNode = svcNodeElem.getElementsByTagName("service").item(0);
		if (svcNameNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element svcNameElem = (Element)svcNameNode;
			String svcName = svcNameElem.getAttribute("name");							
			for (Service atomicSvc : atomicSvcs)
			{
				if (atomicSvc.getName().equals(svcName))
				{
					service = atomicSvc;
				}
			}
		}
		
		List<Constraint> svcConstraints = parseSvcConstraints(null, svcNodeElem, "constraints");
		
		ServiceNode serviceNode = new ServiceNode(service, svcConstraints, svcLayerIndex);
		
		return serviceNode;
	}
	
	private static List<String[]> parseSvcPredecessors(Element svcNodeElem)
	{
		List<String[]> predDetails = new ArrayList<String[]>();
		
		Node predecessorsNode = svcNodeElem.getElementsByTagName("predecessors").item(0);
		if (predecessorsNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element predecessorsElem = (Element)predecessorsNode;
			NodeList predInstances = predecessorsElem.getElementsByTagName("instance");
			for (int i = 0; i < predInstances.getLength(); i++)
			{
				Node predInstance = predInstances.item(i);
				if (predInstance.getNodeType() == Node.ELEMENT_NODE)
				{
					Element predElem = (Element)predInstance;
					String predLayerIndex = predElem.getAttribute("layerindex");
					String predName = predElem.getAttribute("name");					
					
					String[] predRecord = {predLayerIndex, predName};
					predDetails.add(predRecord);
				}
			}
		}
		
		return predDetails;
	}
	
	private static void assignPredsSuccs(ServiceNode serviceNode, List<String[]> predDetails, ConstraintAwarePlan cnstrAwrPlan)
	{
		for (String[] predRecord : predDetails)
		{
			//Fetching the current predecessor node from the constraint-aware plan
			ServiceNode predServiceNode = cnstrAwrPlan.getServiceNode(Integer.parseInt(predRecord[0]), predRecord[1]);
			if (predServiceNode != null)
			{
				//Assign the predecessor service node as a predecessor to the given service node
				serviceNode.addPredecessor(predServiceNode);
				
				//Assign the given service node as a successor to the predecessor service node
				predServiceNode.addSuccessor(serviceNode);
			}
		}
	}
}