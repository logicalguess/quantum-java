package logicalguess.simulator.gate;

import org.apache.commons.math3.complex.Complex;

//switch NOT gate
public class CNOT extends Gate {
    private static Complex[][] t = {{new Complex(1), new Complex(0), new Complex(0), new Complex(0)},
            {new Complex(0), new Complex(1), new Complex(0), new Complex(0)},
            {new Complex(0), new Complex(0), new Complex(0), new Complex(1)},
            {new Complex(0), new Complex(0), new Complex(1), new Complex(0)}};

    public CNOT(int[] i) {
        super(2, t, i, "CNOT");
    }
}
