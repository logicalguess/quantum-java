package logicalguess.simulator.gate;

import org.apache.commons.math3.complex.Complex;

//NOT gate
public class X extends Gate {
    private static Complex[][] t = {{new Complex(0), new Complex(1)},
            {new Complex(1), new Complex(0)}};

    public X(int[] i) {
        super(1, t, i, "X");
    }
}
