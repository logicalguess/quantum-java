package logicalguess.simulator.gate;

import org.apache.commons.math3.complex.Complex;

public class Measure extends Gate
{
	public final int qubit;

	public Measure(int q) {
		super(1, new Complex[2][2], new int[0], "Measure");
		qubit = q;
	}
}
