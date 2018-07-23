package translation.readers.csreaders;

import java.util.ArrayList;
import java.util.List;
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
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.entities.ServiceNode;
import utilities.LogUtil;
import utilities.ReadWriteUtil;

/**
 * Concrete reader for reading a composite service from an XML file.
 * @author Jyotsana Gupta
 */
public class XMLCSReader implements CompositeServiceReader
{
	/**
	 * Method for initiating parsing of an XML composite service repository file to extract a specific composite service.
	 * @param 	csRepoFileName	Complete name and path of the repository file
	 * @param	csName			Name of the target composite service
	 * @param 	logger			Logging utility object for logging error or status messages to a text file
	 * @return	Target composite service object, if successfully extracted
	 * 			Null, otherwise
	 */
	public Service readCompositeService(String csRepoFileName, String csName, LogUtil logger)
	{
		//Extracting the target service from the given repository
		Service service = readCSFromXMLFile(csRepoFileName, csName);
		
		//In case the target service is not found in the repository
		if (service == null)
		{
			logger.log("The given XML composite service repository does not contain the target service.\n"
						+ "Aborting translation process.\n");
		}
		
		return service;
	}
	
	/**
	 * Method for parsing an XML composite service repository file to extract a specific service.
	 * @param 	compSvcFileName		Complete name and path of the repository file
	 * @param 	targetCSName		Name of the target composite service
	 * @return	Target composite service object, if successfully extracted
	 * 			Null, otherwise
	 */
	public Service readCSFromXMLFile(String compSvcFileName, String targetCSName)
	{
		//Creating an XML document for the composite service repository file
		Document doc = ReadWriteUtil.getXmlDocument(compSvcFileName);
		
		//Fetching all the composite service XML nodes from the repository file
		NodeList csNodes = doc.getElementsByTagName("compositeservice");
		for (int i = 0; i < csNodes.getLength(); i++)
		{
			Node csNode = csNodes.item(i);
			if (csNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element csElem = (Element)csNode;			
				
				//Parsing the current composite service node completely if its name matches the target service name
				String currCSName = ReadWriteUtil.getXMLTagValue("csname", "value", null, csElem);
				if (currCSName.equals(targetCSName))
				{
					//Fetching inner constrained service details from the composite service node
					ArrayList<String> csInputs = (ArrayList<String>)parseSimpleInstanceElements(null, csElem, "csinputs");
					ArrayList<String> csOutputs = (ArrayList<String>)parseSimpleInstanceElements(null, csElem, "csoutputs");
					ArrayList<String> csEffects = (ArrayList<String>)parseSimpleInstanceElements(null, csElem, "cseffects");
					ArrayList<Constraint> csConstraints = (ArrayList<Constraint>)parseSvcConstraints(null, csElem, "csconstraints");
					
					//Parsing the component atomic services for the composite service node
					List<Service> atomicSvcs = parseCSAtomicServices(csElem);
					
					//Fetching the composition plan from the composite service node
					ConstraintAwarePlan cnstrAwrPlan = parseCSPlan(csElem, atomicSvcs);
					
					//Creating constrained service without the composition plan
					ConstrainedService cnstrdService = new ConstrainedService(new BasicService(currCSName, csInputs, csOutputs), csConstraints, csEffects);
					
					//Creating composite service from the constrained service and composition plan
					Service layeredCS = new LayeredCompositeService(cnstrdService, cnstrAwrPlan);
					
					return layeredCS;
				}				
			}
		}
		
		return null;
	}
	
	/**
	 * Method for fetching the value of "name" attribute of all "instance" elements under a document's or another element's sub-element.
	 * @param 	doc			Document under which the container sub-element exists. This should be passed as null if the sub-element exists under another element.
	 * @param 	elem		Element under which the container sub-element exists. This should be passed as null if the sub-element exists under a document.
	 * @param 	tagName		Name of the container sub-element
	 * @return	List of values fetched
	 */
	private static List<String> parseSimpleInstanceElements(Document doc, Element elem, String tagName)
	{
		List<String> csFeatures = new ArrayList<String>();
		
		//Fetching the container sub-element depending on whether it exists under a document or another element
		Node csFeaturesNode = null;
		if (doc != null)
		{
			csFeaturesNode = doc.getElementsByTagName(tagName).item(0);
		}
		else if (elem != null)
		{
			csFeaturesNode = elem.getElementsByTagName(tagName).item(0);
		}
			
		//Fetching all the "instance" sub-elements of the fetched element and extracting their "name" attribute values
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
	
	/**
	 * Method for parsing composite service constraints.
	 * @param 	doc			Document under which the container constraints node exists. This should be passed as null if the constraints node exists under another element.
	 * @param 	elem		Element under which the container constraints node exists. This should be passed as null if the constraints node exists under a document.
	 * @param 	tagName		Name of the container constraints node
	 * @return	List of constraints parsed
	 */
	private static List<Constraint> parseSvcConstraints(Document doc, Element elem, String tagName)
	{
		List<Constraint> csConstraints = new ArrayList<Constraint>();
		
		//Fetching the container constraints node depending on whether it exists under a document or another element
		Node csConstraintsNode = null;
		if (doc != null)
		{
			csConstraintsNode = doc.getElementsByTagName(tagName).item(0);
		}
		else if (elem != null)
		{
			csConstraintsNode = elem.getElementsByTagName(tagName).item(0);
		}
		
		//Parsing all the constraint instances under the container constraints node
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
	
	/**
	 * Method for parsing all the component atomic services of a given composite service.
	 * @param 	csElem	Composite service element whose component atomic services need to be parsed
	 * @return	List of atomic service objects
	 */
	private static List<Service> parseCSAtomicServices(Element csElem)
	{
		List<Service> atomicServices = new ArrayList<Service>();
		Node csAtomicSvcsNode = csElem.getElementsByTagName("csatomicservices").item(0);
		if (csAtomicSvcsNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element csAtomicSvcsElem = (Element)csAtomicSvcsNode;
			NodeList csAtomicSvcNodes = csAtomicSvcsElem.getElementsByTagName("service");
			atomicServices = parseAtomicServiceNodes(csAtomicSvcNodes);
		}
		
		return atomicServices;
	}
	
	/**
	 * Method for parsing a list of atomic service XML nodes to create atomic service objects.
	 * @param 	atomicSvcNodes		Given list of atomic service nodes
	 * @return	List of atomic service objects
	 */
	private static List<Service> parseAtomicServiceNodes(NodeList atomicSvcNodes)
	{
		List<Service> atomicServices = new ArrayList<Service>();
		for (int i = 0; i < atomicSvcNodes.getLength(); i++)
		{
			Node atomicSvcNode = atomicSvcNodes.item(i);
			if (atomicSvcNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element atomicSvcElem = (Element)atomicSvcNode;
				
				//Fetching service details from the service element
				String svcName = atomicSvcElem.getAttribute("name");
				ArrayList<String> svcInputs = (ArrayList<String>)parseSimpleInstanceElements(null, atomicSvcElem, "inputs");
				ArrayList<String> svcOutputs = (ArrayList<String>)parseSimpleInstanceElements(null, atomicSvcElem, "outputs");
				ArrayList<String> svcEffects = (ArrayList<String>)parseSimpleInstanceElements(null, atomicSvcElem, "effects");
				ArrayList<Constraint> svcConstraints = (ArrayList<Constraint>)parseSvcConstraints(null, atomicSvcElem, "constraints");
							
				//Creating a service object using the information fetched and adding it to the service list
				Service service = new ConstrainedService(new BasicService(svcName, svcInputs, svcOutputs), svcConstraints, svcEffects);
				atomicServices.add(service);
			}
		}
		
		return atomicServices;
	}
	
	/**
	 * Method for parsing the composition plan of a given composite service XML element and creating a constraint-aware plan object for it.
	 * @param 	csElem			Composite service element containing the composition plan
	 * @param 	atomicSvcs		Component atomic service objects required for plan object construction
	 * @return	Constraint-aware plan constructed
	 */
	private static ConstraintAwarePlan parseCSPlan(Element csElem, List<Service> atomicSvcs)
	{
		ConstraintAwarePlan cnstrAwrPlan = null;
		
		//Fetching the composition plan element from the composite service element
		Node csPlanNode = csElem.getElementsByTagName("csplan").item(0);
		if (csPlanNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element csPlanElem = (Element)csPlanNode;
			
			//Creating an empty constraint-aware plan with as many layers as there are service layer nodes in the plan element fetched above
			NodeList svcLayerNodes = csPlanElem.getElementsByTagName("servicelayer");
			int svcLayerCount = svcLayerNodes.getLength();
			cnstrAwrPlan = new ConstraintAwarePlan(svcLayerCount);
			
			//Fetching all the service layer elements from the composition plan element
			for (int i = 0; i < svcLayerCount; i++)
			{
				Node svcLayerNode = svcLayerNodes.item(i);
				if (svcLayerNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element svcLayerElem = (Element)svcLayerNode;
					
					//Fetching all the service node elements from the current service layer element
					NodeList xmlSvcNodes = svcLayerElem.getElementsByTagName("servicenode");
					for (int j = 0; j < xmlSvcNodes.getLength(); j++)
					{
						Node xmlSvcNode = xmlSvcNodes.item(j);
						if (xmlSvcNode.getNodeType() == Node.ELEMENT_NODE)
						{
							Element svcNodeElem = (Element)xmlSvcNode;
							
							//Translating the current service node element into a service node object
							ServiceNode serviceNode = parseServiceNode(svcNodeElem, atomicSvcs, i);
							
							//Adding the service node object to the constraint-aware plan
							cnstrAwrPlan.addServiceNode(serviceNode);
							
							//Adding predecessors and successors to the existing service nodes of the constraint-aware plan
							List<String[]> predDetails = parseSvcPredecessors(svcNodeElem);
							assignPredsSuccs(serviceNode, predDetails, cnstrAwrPlan);
						}
					}
				}
			}
		}
		
		return cnstrAwrPlan;
	}
	
	/**
	 * Method for parsing a given service node XML element and creating a service node object for it.
	 * @param 	svcNodeElem			Given service node XML element
	 * @param 	atomicSvcs			List of atomic services containing the service that would become a component of the service node to be created
	 * @param 	svcLayerIndex		Index of the composition plan service layer in which the service node to be created will be added eventually
	 * @return	Service node created
	 */
	private static ServiceNode parseServiceNode(Element svcNodeElem, List<Service> atomicSvcs, int svcLayerIndex)
	{
		Service service = null;
		
		//Fetching the name of the component service from the given service node element
		Node svcNameNode = svcNodeElem.getElementsByTagName("service").item(0);
		if (svcNameNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element svcNameElem = (Element)svcNameNode;
			String svcName = svcNameElem.getAttribute("name");	
			
			//Fetching the atomic service that matches the name parsed above from the given service list
			for (Service atomicSvc : atomicSvcs)
			{
				if (atomicSvc.getName().equals(svcName))
				{
					service = atomicSvc;
				}
			}
		}
		
		//Fetching the constraints of the given service node element
		List<Constraint> svcConstraints = parseSvcConstraints(null, svcNodeElem, "constraints");
		
		//Creating a service node object using the information fetched
		ServiceNode serviceNode = new ServiceNode(service, svcConstraints, svcLayerIndex);
		
		return serviceNode;
	}
	
	/**
	 * Method for fetching predecessor information for a given service node element.
	 * @param 	svcNodeElem		Target service node element
	 * @return	List of predecessor information records. Each record contains predecessor layer index and name.
	 */
	private static List<String[]> parseSvcPredecessors(Element svcNodeElem)
	{
		List<String[]> predDetails = new ArrayList<String[]>();
		
		//Fetching the predecessors element from the given service node element
		Node predecessorsNode = svcNodeElem.getElementsByTagName("predecessors").item(0);
		if (predecessorsNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element predecessorsElem = (Element)predecessorsNode;
			
			//Fetching all predecessor instance elements
			NodeList predInstances = predecessorsElem.getElementsByTagName("instance");
			for (int i = 0; i < predInstances.getLength(); i++)
			{
				Node predInstance = predInstances.item(i);
				if (predInstance.getNodeType() == Node.ELEMENT_NODE)
				{
					Element predElem = (Element)predInstance;
					
					//Fetching the service layer index and name for the current predecessor instance
					String predLayerIndex = predElem.getAttribute("layerindex");
					String predName = predElem.getAttribute("name");					
					
					//Creating a predecessor record and adding it to the list of records
					String[] predRecord = {predLayerIndex, predName};
					predDetails.add(predRecord);
				}
			}
		}
		
		return predDetails;
	}
	
	/**
	 * Method for assigning predecessors to a given service node in a composition plan.
	 * The given node is also assigned as a successor to all its predecessor nodes in the plan.
	 * @param 	serviceNode		Service node whose predecessors need to be assigned
	 * @param 	predDetails		Details of the predecessors to be assigned
	 * @param 	cnstrAwrPlan	Constraint-aware composition plan containing the target node and its predecessors
	 */
	private static void assignPredsSuccs(ServiceNode serviceNode, List<String[]> predDetails, ConstraintAwarePlan cnstrAwrPlan)
	{
		for (String[] predRecord : predDetails)
		{
			//Fetching the current predecessor node from the constraint-aware plan
			ServiceNode predServiceNode = cnstrAwrPlan.getServiceNode(Integer.parseInt(predRecord[0]), predRecord[1]);
			if (predServiceNode != null)
			{
				//Assigning the predecessor service node as a predecessor to the given service node
				serviceNode.addPredecessor(predServiceNode);
				
				//Assigning the given service node as a successor to the predecessor service node
				predServiceNode.addSuccessor(serviceNode);
			}
		}
	}
}