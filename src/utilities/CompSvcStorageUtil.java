package utilities;

import java.util.ArrayList;
import constraint.Constraint;
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
		ArrayList<Constraint> svcConstraints = null;		
		ArrayList<String> svcEffects = (ArrayList<String>) compRequest.getOutputs();
		
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
	public static Service readCSFromSerialFile(String csRepoFileName, String csName, LogUtil logger)
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
}