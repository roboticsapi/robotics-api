package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Block informs net of user-requested cancel; may only be used once per net
 */
public class Cancel extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Cancel";

	/** true, if user requested cancel of net execution */
	private final OutPort outCancel = new OutPort("outCancel");

	public Cancel() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outCancel);

		// Add all parameters
	}

	/**
	 * true, if user requested cancel of net execution
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutCancel() {
		return this.outCancel;
	}

}
