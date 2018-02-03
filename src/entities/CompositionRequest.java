package entities;

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
	private List<QualityOfService> qos;
	private List<Constraint> constraints;
	
	/**
	 * Default constructor.
	 */
	public CompositionRequest()
	{
		inputs = new ArrayList<String>();
		outputs = new ArrayList<String>();
		qos = new ArrayList<QualityOfService>();
		constraints = new ArrayList<Constraint>();
	}
	
	/**
	 * Constructor with all data member values accepted as arguments.
	 * @param	inputs			List of inputs provided to the composite service by the user
	 * @param	outputs			List of outputs expected from the composite service by the user
	 * @param	qos				List of quality of service features and values expected from the composite service by the user
	 * @param	constraints		List of constraints to be imposed on the composite service by the user
	 */
	public CompositionRequest(List<String> inputs, List<String> outputs, List<QualityOfService> qos, List<Constraint> constraints)
	{
		//Lists are deep-copied so as to avoid unwanted alteration from outside the class
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
		
		this.qos = new ArrayList<QualityOfService>();
		for (QualityOfService qosElem : qos)
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
	 * Accessor method for the list of inputs provided to the composite service by the user.
	 * @return	List of composition request inputs
	 */
	public List<String> getInputs()
	{
		//Lists are deep-copied so as to avoid unwanted alteration from outside the class
		List<String> retInputs = new ArrayList<String>();
		for (String input : inputs)
		{
			retInputs.add(input);
		}
		
		return retInputs;
	}
	
	/**
	 * Accessor method for the list of outputs expected from the composite service by the user.
	 * @return	List of composition request outputs
	 */
	public List<String> getOutputs()
	{
		//Lists are deep-copied so as to avoid unwanted alteration from outside the class
		List<String> retOutputs = new ArrayList<String>();
		for (String output : outputs)
		{
			retOutputs.add(output);
		}
		
		return retOutputs;
	}
	
	/**
	 * Accessor method for the list of quality of service features and values expected from the composite service by the user.
	 * @return	List of composition request quality of service features and values
	 */
	public List<QualityOfService> getQos()
	{
		//Lists are deep-copied so as to avoid unwanted alteration from outside the class
		List<QualityOfService> retQos = new ArrayList<QualityOfService>();
		for (QualityOfService qosElem : qos)
		{
			retQos.add(qosElem);
		}
		
		return retQos;
	}
	
	/**
	 * Accessor method for the list of constraints to be imposed on the composite service by the user.
	 * @return	List of composition request constraints
	 */
	public List<Constraint> getConstraints()
	{
		//Lists are deep-copied so as to avoid unwanted alteration from outside the class
		List<Constraint> retConstraints = new ArrayList<Constraint>();
		for (Constraint constraint : constraints)
		{
			retConstraints.add(constraint);
		}
		
		return retConstraints;
	}
	
	/**
	 * Mutator method for the list of inputs provided to the composite service by the user.
	 * @param	inputs	List of inputs to be assigned to this composition request
	 */
	public void setInputs(List<String> inputs)
	{
		//Lists are deep-copied so as to avoid unwanted alteration from outside the class
		this.inputs = new ArrayList<String>();
		for (String input : inputs)
		{
			this.inputs.add(input);
		}
	}
	
	/**
	 * Mutator method for the list of outputs expected from the composite service by the user.
	 * @param	outputs	List of outputs to be assigned to this composition request
	 */
	public void setOutputs(List<String> outputs)
	{
		//Lists are deep-copied so as to avoid unwanted alteration from outside the class
		this.outputs = new ArrayList<String>();
		for (String output : outputs)
		{
			this.outputs.add(output);
		}
	}
	
	/**
	 * Mutator method for the list of quality of service features and values expected from the composite service by the user.
	 * @param	qos	List of qos features and values to be assigned to this composition request
	 */
	public void setQos(List<QualityOfService> qos)
	{
		//Lists are deep-copied so as to avoid unwanted alteration from outside the class
		this.qos = new ArrayList<QualityOfService>();
		for (QualityOfService qosElem : qos)
		{
			this.qos.add(qosElem);
		}
	}
	
	/**
	 * Mutator method for the list of constraints to be imposed on the composite service by the user.
	 * @param	constraints	List of constraints to be assigned to this composition request
	 */
	public void setConstraints(List<Constraint> constraints)
	{
		//Lists are deep-copied so as to avoid unwanted alteration from outside the class
		this.constraints = new ArrayList<Constraint>();
		for (Constraint constraint : constraints)
		{
			this.constraints.add(constraint);
		}
	}
}