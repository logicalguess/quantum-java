package logicalguess.test.simulator;

import logicalguess.simulator.circuit.Circuit;
import logicalguess.simulator.gate.CNOT;
import logicalguess.simulator.gate.H;
import logicalguess.simulator.gate.X;
import logicalguess.simulator.gate.Z;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeleportationTest {

    private static final Logger LOG = LoggerFactory.getLogger(TeleportationTest.class);

    @Test
    public void runCircuit() {
        Circuit circuit = new Circuit(3);
        circuit.setStart();

        circuit.addGate(new H(new int[]{1}));
        circuit.addGate(new CNOT(new int[]{1, 2}));

        circuit.restOfSteps();

        circuit.addGate(new CNOT(new int[]{2, 1}));
        circuit.addGate(new H(new int[]{2}));

        circuit.restOfSteps();

        circuit.measure(2);
        circuit.measure(1);

        circuit.restOfSteps();

        if ((circuit.state.probQOne(1)) > 0) circuit.addGate(new X(new int[]{0}));
        if ((circuit.state.probQOne(2)) > 0) circuit.addGate(new Z(new int[]{0}));

        circuit.restOfSteps();

    }
}
