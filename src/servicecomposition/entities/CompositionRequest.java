package servicecomposition.entities;

import java.util.ArrayList;
import java.util.List;
import constraint.Constraint;

/**
 * Class for representing a service composition request from a user.
 * @author Jyotsana Gupta
 */
public class CompositionRequest 
{
	private List<String> inputs;
	private List<String> outputs;
	private List<String> qos;
	private List<Constraint> constraints;
	
	/**
	 * Default constructor.
	 */
	public CompositionRequest()
	{
		inputs = new ArrayList<String>();
		outputs = new ArrayList<String>();
		qos = new ArrayList<String>();
		constraints = new ArrayList<Constraint>();
	}
	
	/**
	 * Constructor with all data member values accepted as arguments.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @param	inputs			List of ontology types provided as inputs to the composite service by the user
	 * @param	outputs			List of ontology types expected as outputs from the composite service by the user
	 * @param	qos				List of quality of service features expected from the composite service by the user
	 * @param	constraints		List of constraints to be imposed on the inputs, outputs and QoS features of the composite service by the user
	 */
	public CompositionRequest(List<String> inputs, List<String> outputs, List<String> qos, List<Constraint> constraints)
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
		this.inputs = new ArrayList<String>();
		for (String input : inputs)
		{
			this.inputs.add(input);
		}
		
		this.outputs = new ArrayList<String>();
		for (String output : outputs)
		{
			this.outputs.add(output);
		}
		
		this.qos = new ArrayList<String>();
		for (String qosElem : qos)
		{
			this.qos.add(qosElem);
		}
		
		this.constraints = new ArrayList<Constraint>();
		for (Constraint constraint : constraints)
		{
			this.constraints.add(constraint);
		}
	}
	
	/**
	 * Accessor method for the list of input ontology types provided to the composite service by the user.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @return	List of composition request input ontology types
	 */
	public List<String> getInputs()
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
		List<String> retInputs = new ArrayList<String>();
		for (String input : inputs)
		{
			retInputs.add(input);
		}
		
		return retInputs;
	}
	
	/**
	 * Accessor method for the list of output ontology types expected from the composite service by the user.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @return	List of composition request output ontology types
	 */
	public List<String> getOutputs()
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
		List<String> retOutputs = new ArrayList<String>();
		for (String output : outputs)
		{
			retOutputs.add(output);
		}
		
		return retOutputs;
	}
	
	/**
	 * Accessor method for the list of quality of service features expected from the composite service by the user.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @return	List of composition request quality of service features
	 */
	public List<String> getQos()
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
		List<String> retQos = new ArrayList<String>();
		for (String qosElem : qos)
		{
			retQos.add(qosElem);
		}
		
		return retQos;
	}
	
	/**
	 * Accessor method for the list of constraints to be imposed on the composite service inputs, outputs and QoS features by the user.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @return	List of composition request constraints
	 */
	public List<Constraint> getConstraints()
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
		List<Constraint> retConstraints = new ArrayList<Constraint>();
		for (Constraint constraint : constraints)
		{
			retConstraints.add(constraint);
		}
		
		return retConstraints;
	}
	
	/**
	 * Mutator method for the list of input ontology types provided to the composite service by the user.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @param	inputs	List of input ontology types to be assigned to this composition request
	 */
	public void setInputs(List<String> inputs)
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
		this.inputs = new ArrayList<String>();
		for (String input : inputs)
		{
			this.inputs.add(input);
		}
	}
	
	/**
	 * Mutator method for the list of output ontology types expected from the composite service by the user.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @param	outputs	List of output ontology types to be assigned to this composition request
	 */
	public void setOutputs(List<String> outputs)
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
		this.outputs = new ArrayList<String>();
		for (String output : outputs)
		{
			this.outputs.add(output);
		}
	}
	
	/**
	 * Overridden toString method for Composition Request class.
	 * @return	String containing details of this composition request
	 */
	@Override
	public String toString()
	{
		String compReqString = "";
		
		//Gathering requested input details
		String inputString = "";
		for (String input : inputs)
		{
			inputString += input + ", ";
		}
		if (inputString.lastIndexOf(",") >= 0)
		{
			inputString = inputString.substring(0, inputString.lastIndexOf(","));
		}
		
		//Gathering requested output details
		String outputString = "";
		for (String output : outputs)
		{
			outputString += output + ", ";
		}
		if (outputString.lastIndexOf(",") >= 0)
		{
			outputString = outputString.substring(0, outputString.lastIndexOf(","));
		}
		
		//Gathering requested QoS feature details
		String qosString = "";
		for (String qosFeature : qos)
		{
			qosString += qosFeature + ", ";
		}
		if (qosString.lastIndexOf(",") >= 0)
		{
			qosString = qosString.substring(0, qosString.lastIndexOf(","));
		}
		
		//Gathering requested constraint details
		String constraintString = "";
		for (Constraint constraint : constraints)
		{
			String currCnstrStr = "";
			currCnstrStr += "(" + constraint.getServiceName() + ") "
							+ constraint.getType() + " "
							+ constraint.getOperator() + " "
							+ constraint.getLiteralValue();
			constraintString += currCnstrStr + ", ";
		}
		if (constraintString.lastIndexOf(",") >= 0)
		{
			constraintString = constraintString.substring(0, constraintString.lastIndexOf(","));
		}
		
		//Preparing a single string with all the required details for this request
		compReqString += "Inputs: " + inputString + "\n"
						+ "Outputs: " + outputString + "\n"
						+ "QoS: " + qosString + "\n"
						+ "Constraints: " + constraintString;
		compReqString = compReqString.trim();
		
		return compReqString;
	}
}