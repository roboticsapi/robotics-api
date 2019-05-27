package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * signals another net which is scheduled for execution right after this net is
 * finished; block may only be used once per net
 */
public class Takeover extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Takeover";

	/** signals if other net is scheduled for execution */
	private final OutPort outTakeover = new OutPort("outTakeover");

	public Takeover() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outTakeover);

		// Add all parameters
	}

	/**
	 * signals if other net is scheduled for execution
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutTakeover() {
		return this.outTakeover;
	}

}
