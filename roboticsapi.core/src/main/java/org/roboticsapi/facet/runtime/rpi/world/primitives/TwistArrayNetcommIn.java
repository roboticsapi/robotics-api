package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Communication Module to propagate value into net
 */
public class TwistArrayNetcommIn extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::TwistArrayNetcommIn";

	/**  */
	private final OutPort outLastUpdated = new OutPort("outLastUpdated");

	/**  */
	private final OutPort outValue = new OutPort("outValue");

	/** Key (unique name) of the property */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> paramKey = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring>(
			"Key", new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(""));

	/** Initial value to write to net */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray> paramValue = new Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray>(
			"Value", new org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray("[]"));

	public TwistArrayNetcommIn() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outLastUpdated);
		add(outValue);

		// Add all parameters
		add(paramKey);
		add(paramValue);
	}

	/**
	 * Creates communication Module to propagate value into net
	 *
	 * @param key   Key (unique name) of the property
	 * @param value Initial value to write to net
	 */
	public TwistArrayNetcommIn(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring paramKey,
			org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray paramValue) {
		this();

		// Set the parameters
		setKey(paramKey);
		setValue(paramValue);
	}

	/**
	 * Creates communication Module to propagate value into net
	 *
	 * @param key   Key (unique name) of the property
	 * @param value Initial value to write to net
	 */
	public TwistArrayNetcommIn(String paramKey, String paramValue) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(paramKey),
				new org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray(paramValue));
	}

	/**
	 * 
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutLastUpdated() {
		return this.outLastUpdated;
	}

	/**
	 * 
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Key (unique name) of the property
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> getKey() {
		return this.paramKey;
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the property
	 * 
	 * @param value new value of the parameter
	 */
	public final void setKey(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring value) {
		this.paramKey.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the property
	 * 
	 * @param value new value of the parameter
	 */
	public final void setKey(String value) {
		this.setKey(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(value));
	}

	/**
	 * Initial value to write to net
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Initial value to write to net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Initial value to write to net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(String value) {
		this.setValue(new org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray(value));
	}

}
