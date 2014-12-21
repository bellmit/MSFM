package com.cboe.instrumentationService.impls;

/**
 * Describe class ResettableHwm here.
 *
 *
 * Created: Thu Sep 15 09:21:59 2005
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class ResettableHwm {

	private long hwm;

	/**
	 * Creates a new <code>ResettableHwm</code> instance.
	 *
	 */
	public ResettableHwm( long pHwm ) {
		hwm = pHwm;
	}

	public long value() {
		return hwm;
	}

	public void setValue( long newValue ) {
		hwm = newValue;
	}

	public void reset() {
		hwm = 0;
	}

}
