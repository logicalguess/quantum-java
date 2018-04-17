package logicalguess.simulator.circuit;

import logicalguess.simulator.gate.Gate;
import logicalguess.simulator.gate.Measure;
import logicalguess.util.MatrixUtil;
import org.apache.commons.math3.complex.Complex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Circuit {

    private static final Logger LOG = LoggerFactory.getLogger(Circuit.class);

    public final int qubits;
    public final State state;
    private ArrayList<Gate> gates;
    private Complex[][] matrix;
    private int step;

    private boolean display = true;
    private boolean displayFinal = true;

    public Circuit(int bits) {
        qubits = bits;
        step = 0;
        state = new State(qubits);
        int count = 0;
        for (Complex c : state.amplitudes) {
            state.amplitudes[count] = c;
            count++;
        }
        gates = new ArrayList<>();
        matrix = MatrixUtil.identity(bits);
    }

    public Circuit(Complex[] starter, int bits) {
        int count = 0;
        state = new State(bits);
        for (Complex c : state.amplitudes) {
            state.amplitudes[count] = c;
            count++;
        }
        qubits = bits;
        gates = new ArrayList<>();
        matrix = MatrixUtil.identity(qubits);
        step = 0;

        printState();
    }

    //to set all qubits to 0
    public void setEmpty() {
        for (int r = 0; r < state.amplitudes.length; r++) {
            state.amplitudes[r] = Complex.ZERO;
        }
    }

    public void setStart() {
        state.amplitudes[0] = Complex.ONE;
        for (int r = 1; r < state.amplitudes.length; r++) {
            state.amplitudes[r] = Complex.ZERO;
        }
        printState();
    }

    public void measure(int i) {
        this.addGate(new Measure(i));
    }

    public int measureAll() {
        int finalState = -1;
        for (int i = 0; i < qubits; i++) {
            this.measure(i);
        }
        //this.restOfSteps();

        for (int i = 0; i < state.amplitudes.length; i++) {
            if (!state.amplitudes[i].equals(Complex.ZERO)) {
                state.amplitudes[i] = Complex.ONE;
            }
        }

        if (displayFinal) {
            LOG.info("\n |\n v");
        }
        for (int i = 0; i < state.amplitudes.length; i++) {
            if (state.amplitudes[i].equals(Complex.ONE)) {
                finalState = i;
                if (displayFinal) {
                    LOG.info("Final State: |" + MatrixUtil.bin(i, state.bits) + ">");
                }
            }
        }
        return finalState;
    }

    public void setStartState(Complex[] starter) {
        if (starter == null) //same as no parameter
        {
            for (int r = 0; r < state.amplitudes.length; r++) {
                state.amplitudes[r] = Complex.ZERO;
            }
        } else {
            int count = 0;
            for (Complex c : state.amplitudes) {
                state.amplitudes[count] = c;
                count++;
            }
        }
        printState();
    }

    public void printState() {
        if (display) {
            for (int i = 0; i < state.amplitudes.length; i++) {
                if (state.amplitudes[i].equals(Complex.ZERO)) continue;
                LOG.info("|" + MatrixUtil.bin(i, state.bits) + ">: " + State.display((state.amplitudes[i])));
            }
        }
    }

    public void set(int index, Complex value) {
        state.set(index, value);
        state.normalize();
        printState();
    }

    public void turnOffDisplay() {
        display = false;
    }

    public void turnOnDisplay() {
        display = true;
    }

    public void turnOffDisplayFinal() {
        displayFinal = false;
    }

    public void turnOnDisplayFinal() {
        displayFinal = true;
    }

    public void addGate(Gate g) {
        gates.add(g);
    }

    public void setGates(ArrayList<Gate> g) {
        gates = g;
    }

    public void step() {
        Gate gate = gates.get(step);
        //measures individual qubits
        if (gate.type.equals("Measure")) {
            matrix = MatrixUtil.identity(state.size);
            int which = ((Measure) gate).qubit;
            Complex out;
            if (Math.random() < state.probQOne(which)) {
                out = Complex.ONE;
            } else {
                out = Complex.ZERO;
            }
            char bit;
            if (Complex.equals(out, Complex.ZERO)) {
                bit = '0';
            } else if (Complex.equals(out, Complex.ONE)) {
                bit = '1';
            } else {
                bit = 'u';
            }
            Complex[] change = new Complex[state.size];
            double div = 0;
            for (int i = 0; i < state.size; i++) {
                String temp = MatrixUtil.bin(i, state.bits);
                if (temp.charAt(state.bits - 1 - which) != bit) {
                    change[i] = Complex.ZERO;
                } else if (temp.charAt(state.bits - 1 - which) == bit) {
                    div += Math.pow(state.amplitudes[i].abs(), 2);
                }

            }
            for (int i = 0; i < state.size; i++) {
                String temp = MatrixUtil.bin(i, state.bits);
                if (temp.charAt(state.bits - 1 - which) != bit) {
                    change[i] = Complex.ZERO;
                } else if (temp.charAt(state.bits - 1 - which) == bit) {
                    Complex a = state.amplitudes[i];
                    change[i] = a.divide(Math.sqrt(div));
                }
            }

            for (int i = 0; i < change.length; i++) {
                state.amplitudes[i] = change[i];
            }
        } else {
            matrix = MatrixUtil.expand(gate.matrix, gate.inputs, qubits);
            Complex[][] change = MatrixUtil.multiply(matrix, MatrixUtil.colToMatrix(state.amplitudes));
            for (int i = 0; i < change.length; i++) {
                state.amplitudes[i] = change[i][0];
            }
        }
        step++;

        if (display) {
            LOG.info("\n |\n v");
            for (int i = 0; i < state.amplitudes.length; i++) {
                if (state.amplitudes[i].equals(Complex.ZERO)) continue;
                LOG.info("|" + MatrixUtil.bin(i, state.bits) +
                        ">: " + State.display((state.amplitudes[i])) +
                " -> " + state.probState(i) + "%");
            }
        }
    }

    //runs rest of amplitudes
    public void restOfSteps() {
        for (int i = step; i < gates.size(); i++) {
            step();
        }
    }
}