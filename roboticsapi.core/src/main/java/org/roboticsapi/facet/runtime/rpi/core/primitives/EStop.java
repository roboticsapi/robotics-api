package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * 
 */
public class EStop extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "EStop";

	/** this port signals an emergency stop for all running devices */
	private final InPort inEStop = new InPort("inEStop");

	public EStop() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inEStop);

		// Add all parameters
	}

	/**
	 * this port signals an emergency stop for all running devices
	 *
	 * @return the input port of the block
	 */
	public final InPort getInEStop() {
		return this.inEStop;
	}

}
