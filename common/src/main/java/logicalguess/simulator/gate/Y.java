package logicalguess.simulator.gate;

import org.apache.commons.math3.complex.Complex;

//Pauli-Y gate
public class Y extends Gate {
    private static Complex[][] t = {{new Complex(0), new Complex(0, -1)},
            {new Complex(0, 1), new Complex(0)}};

    public Y(int[] i) {
        super(1, t, i, "Y");
    }
}
