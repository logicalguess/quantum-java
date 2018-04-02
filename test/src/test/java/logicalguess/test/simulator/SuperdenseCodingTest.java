package logicalguess.test.simulator;

import logicalguess.simulator.circuit.Circuit;
import logicalguess.simulator.circuit.SuperdenseCoding;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class SuperdenseCodingTest {

    private static final Logger LOG = LoggerFactory.getLogger(SuperdenseCodingTest.class);

    @Test
    public void runCircuit() {
        int input = 125;

        int bits = SuperdenseCoding.getBitsNeeded(input);
        Circuit circuit = SuperdenseCoding.entangle(bits);
        circuit = SuperdenseCoding.encode(input, circuit);
        int result = SuperdenseCoding.decode(circuit).measureAll();

        LOG.info("Decoded number: " + result);
        assertEquals(input, result);
    }
}
