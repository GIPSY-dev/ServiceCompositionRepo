package utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class SvcRepoStorageUtil 
{
	/**
	 * Method for creating a composite service.
	 * @param 	compRequest		Service composition request for which composition plan was created
	 * @param 	cnstrAwrPlan	Constraint-aware plan created for the composition request
	 * @return	Layered composite service created
	 */
	public static LayeredCompositeService createCompositeService(CompositionRequest compRequest, ConstraintAwarePlan cnstrAwrPlan)
	{
		//Getting current timestamp for naming the composite service uniquely
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		String timestamp = dateFormat.format(new Date()).toString();
		
		//Getting composite service elements from the composition request
		String svcName = "CompSvc." + timestamp;
		ArrayList<String> svcInputs = (ArrayList<String>) compRequest.getInputs();
		ArrayList<String> svcOutputs = (ArrayList<String>) compRequest.getOutputs();
		ArrayList<Constraint> svcConstraints = null;		
		ArrayList<String> svcEffects = (ArrayList<String>) compRequest.getOutputs();
		
		//Creating constrained service without the composition plan
		ConstrainedService cnstrdService = new ConstrainedService(new BasicService(svcName, svcInputs, svcOutputs), svcConstraints, svcEffects);
		
		//Creating composite service from the constrained service and composition plan
		LayeredCompositeService layeredCS = new LayeredCompositeService(cnstrdService, cnstrAwrPlan);
		
		return layeredCS;
	}
	
	/**
	 * Method for appending a list of layered composite services to a repository of serialized service objects.
	 * @param 	compSvcs		List of layered composite services to be appended 
	 * @param 	repoFileName	Complete name and path of the destination repository
	 */
	public static void writeCSToSvcRepo(ArrayList<Service> compSvcs, String repoFileName)
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
}