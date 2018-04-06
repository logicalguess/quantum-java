package logicalguess.simulator.circuit;

import logicalguess.simulator.gate.CNOT;
import logicalguess.simulator.gate.H;
import logicalguess.simulator.gate.X;
import logicalguess.simulator.gate.Z;
import logicalguess.util.MatrixUtil;
import org.apache.commons.math3.complex.Complex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperdenseCoding {

    private static final Logger LOG = LoggerFactory.getLogger(SuperdenseCoding.class);

    public static int getBitsNeeded(int input) {
        return 2 * (int) Math.ceil(Math.log(input + 1) / Math.log(4));
    }

    public static Circuit entangle(int bits) {

        Circuit circuit = new Circuit(bits);
        circuit.turnOffDisplay();
        circuit.setStart();
        circuit.state.set(0, Complex.ONE);

        //puts all bits into equal superposition
        for (int i = 0; i < bits; i += 2) {
            circuit.addGate(new H(new int[]{i}));
            circuit.addGate(new CNOT(new int[]{i, i + 1}));
        }

        circuit.restOfSteps();
        //break here to represent possible time break in actual implementation

        return circuit;
    }

    public static Circuit encode(int in, Circuit circuit) {

        LOG.info("Encoding Number: " + in);

        int bits = circuit.qubits;
        LOG.info("Start State: |" + MatrixUtil.bin(0, circuit.state.bits) + ">\n |\n v");

        for (int i = bits - 2; i >= 0; i -= 2) {
            LOG.info("IN: " + in);
            switch (in % 4) {
                case 0:
                    break;
                case 1:
                    circuit.addGate(new X(new int[]{i}));
                    break;
                case 2:
                    circuit.addGate(new Z(new int[]{i}));
                    break;
                case 3:
                    circuit.addGate(new Z(new int[]{i}));
                    circuit.addGate(new X(new int[]{i}));
                    break;
            }
            in = in / 4;
        }
        return circuit;
    }

    public static Circuit decode(Circuit circuit) {
        int bits = circuit.qubits;
        LOG.info("Progress -- " + "0/" + (bits / 2));

        for (int i = bits - 2; i >= 0; i -= 2) {
            circuit.addGate(new CNOT(new int[]{i, i + 1})); //takes bits out of superposition
            circuit.addGate(new H(new int[]{i}));
            circuit.restOfSteps();
            LOG.info("Progress -- " + (bits / 2 - i / 2) + "/" + (bits / 2));
        }
        return circuit;
    }
}
