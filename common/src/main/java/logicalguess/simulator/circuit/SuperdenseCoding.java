package logicalguess.simulator.circuit;

import logicalguess.simulator.gate.CNOT;
import logicalguess.simulator.gate.H;
import logicalguess.simulator.gate.X;
import logicalguess.simulator.gate.Z;
import logicalguess.util.MatrixUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperdenseCoding {

    private static final Logger LOG = LoggerFactory.getLogger(SuperdenseCoding.class);

    public static Circuit encode(int input) {

        int bits; //so number can be represented in base 2.
        int in = input;
        bits = 2 * (int) Math.ceil(Math.log(in + 1) / Math.log(4));

        Circuit circuit = new Circuit(bits);
        circuit.turnOffDisplay();
        circuit.setStart();

        LOG.info("Encoding Number: " + input);

        LOG.info("Start State: |" + MatrixUtil.bin(0, circuit.startState.bits) + ">\n |\n v");
        LOG.info("Progress -- " + "0/" + (bits / 2));

        //puts all bits into equal superposition
        for (int i = 0; i < bits; i += 2) {
            circuit.addGate(new H(new int[]{i}));
            circuit.addGate(new CNOT(new int[]{i, i + 1}));
        }

        circuit.restOfSteps();
        //break here to represent possible time break in actual implementation

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
        for (int i = bits - 2; i >= 0; i -= 2) {
            circuit.addGate(new CNOT(new int[]{i, i + 1})); //takes bits out of superposition
            circuit.addGate(new H(new int[]{i}));
            circuit.restOfSteps();
            LOG.info("Progress -- " + (bits / 2 - i / 2) + "/" + (bits / 2));
        }
        return circuit;
    }
}
