package logicalguess.test.simulator;

import logicalguess.simulator.circuit.Circuit;
import logicalguess.simulator.circuit.SuperdenseCoding;
import logicalguess.simulator.gate.H;
import org.apache.commons.math3.complex.Complex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class SimulatorTest {

    private static final Logger LOG = LoggerFactory.getLogger(SimulatorTest.class);

    @Test
    public void testHadamard() {
        Circuit c = new Circuit(4);
        c.setStart();
        c.set(0, Complex.ONE);

        c.addGate(new H(new int[]{2}));
        c.restOfSteps();

        c.addGate(new H(new int[]{0}));
        c.restOfSteps();

        c.addGate(new H(new int[]{1}));
        c.restOfSteps();
    }
}
