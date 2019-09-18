package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;

/**
 * Returns result value of a previously executed net
 */
public class PreviousNetResult extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "PreviousNetResult";

	/** result value of previously executed net */
	private final OutPort outPreviousNetResult = new OutPort("outPreviousNetResult");

	/** Name of previous executed net for which result is requested */
	private final Parameter<RPIstring> paramPreviousNet = new Parameter<RPIstring>("previousNet", new RPIstring(""));

	public PreviousNetResult() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outPreviousNetResult);

		// Add all parameters
		add(paramPreviousNet);
	}

	/**
	 * Creates returns result value of a previously executed net
	 * 
	 * @param previousNet Name of previous executed net for which result is
	 *                    requested
	 */
	public PreviousNetResult(RPIstring paramPreviousNet) {
		this();

		// Set the parameters
		setpreviousNet(paramPreviousNet);
	}

	/**
	 * Creates returns result value of a previously executed net
	 * 
	 * @param previousNet Name of previous executed net for which result is
	 *                    requested
	 */
	public PreviousNetResult(String paramPreviousNet) {
		this(new RPIstring(paramPreviousNet));
	}

	/**
	 * result value of previously executed net
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutPreviousNetResult() {
		return this.outPreviousNetResult;
	}

	/**
	 * Name of previous executed net for which result is requested
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIstring> getpreviousNet() {
		return this.paramPreviousNet;
	}

	/**
	 * Sets a parameter of the block: Name of previous executed net for which result
	 * is requested
	 * 
	 * @param value new value of the parameter
	 */
	public final void setpreviousNet(RPIstring value) {
		this.paramPreviousNet.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Name of previous executed net for which result
	 * is requested
	 * 
	 * @param value new value of the parameter
	 */
	public final void setpreviousNet(String value) {
		this.setpreviousNet(new RPIstring(value));
	}

}
