package logicalguess.simulator.gate;

import org.apache.commons.math3.complex.Complex;

//Hadamard gate
public class H extends Gate {
    private static Complex[][] t = {{new Complex(1 / Math.sqrt(2)), new Complex(1 / Math.sqrt(2))},
            {new Complex(1 / Math.sqrt(2)), new Complex(-1 / Math.sqrt(2))}};

    public H(int[] i) {
        super(1, t, i, "H");
    }
}
