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

        // entangle pair
        circuit.addGate(new H(new int[]{1}));
        circuit.addGate(new CNOT(new int[]{1, 2}));

        // prepare state to be teleported
        //circuit.addGate(new X(new int[]{0}));


        // encode
        circuit.addGate(new CNOT(new int[]{0, 1}));
        circuit.addGate(new H(new int[]{0}));

        circuit.measure(0);
        circuit.measure(1);

        circuit.restOfSteps();

        int c0 = (int) Math.round(circuit.state.probQOne(0));
        int c1 = (int) Math.round(circuit.state.probQOne(1));

        LOG.info("measured state of qubit 0: " + c0);
        LOG.info("measured state of qubit 1: " + c1);

        // decode
        if (c0 == 1) circuit.addGate(new Z(new int[]{2}));
        if (c1 == 1) circuit.addGate(new X(new int[]{2}));

        LOG.info("probability of qubit 2 being 1: " + circuit.state.probQOne(2));

        circuit.measure(2);

        circuit.restOfSteps();

        int c2 = (int) Math.round(circuit.state.probQOne(2));

        LOG.info("measured state of qubit 2: " + c2);



    }
}
