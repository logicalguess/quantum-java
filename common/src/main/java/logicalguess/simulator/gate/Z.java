package logicalguess.simulator.gate;

import org.apache.commons.math3.complex.Complex;

//Pauli-Z gate
public class Z extends Gate {
    private static Complex[][] t = {{new Complex(1), new Complex(0)},
            {new Complex(0), new Complex(-1)}};

    public Z(int[] i) {
        super(1, t, i, "Z");
    }
}
