package logicalguess.simulator.circuit;

import logicalguess.simulator.gate.Gate;
import logicalguess.simulator.gate.Measure;
import logicalguess.util.MatrixUtil;
import org.apache.commons.math3.complex.Complex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Circuit {

    private static final Logger LOG = LoggerFactory.getLogger(SuperdenseCoding.class);

    public final State startState;
    public final int qubits;
    private ArrayList<Gate> gates;
    private State currentState;
    private Complex[][] currentMatrix;
    private int step;

    private boolean display = true;
    private boolean displayFinal = true;

    public Circuit(int size) {
        qubits = size;
        step = 0;
        startState = new State(qubits);
        currentState = new State(qubits);
        int count = 0;
        for (Complex c : startState.amplitudes) {
            currentState.amplitudes[count] = c;
            count++;
        }
        gates = new ArrayList<>();
        currentMatrix = MatrixUtil.identity(size);
    }

    public Circuit(Complex[] starter, int size) {
        startState = new State(size);
        int count = 0;
        for (Complex c : starter) {
            startState.amplitudes[count] = c;
            count++;
        }
        count = 0;
        for (Complex c : startState.amplitudes) {
            currentState.amplitudes[count] = c;
            count++;
        }
        qubits = size;
        gates = new ArrayList<>();
        currentMatrix = MatrixUtil.identity(qubits);
        step = 0;
    }

    //to set all qubits to 0
    public void setStart() {
        for (int r = 0; r < startState.amplitudes.length; r++) {
            if (r == 0) {
                startState.amplitudes[r] = Complex.ONE;
                currentState.amplitudes[r] = Complex.ONE;
            } else {
                startState.amplitudes[r] = Complex.ZERO;
                currentState.amplitudes[r] = Complex.ZERO;
            }
        }

        if (display) {
            for (int i = 0; i < startState.amplitudes.length; i++) {
                LOG.info("|" + MatrixUtil.bin(i, startState.bits) + ">: " + State.display((startState.amplitudes[i])));
            }
        }
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

        for (int i = 0; i < currentState.amplitudes.length; i++) {
            if (!currentState.amplitudes[i].equals(Complex.ZERO)) {
                currentState.amplitudes[i] = Complex.ONE;
            }
        }

        if (displayFinal) {
            LOG.info("\n |\n v");
        }
        for (int i = 0; i < currentState.amplitudes.length; i++) {
            if (currentState.amplitudes[i].equals(Complex.ONE)) {
                finalState = i;
                if (displayFinal) {
                    LOG.info("Final State: |" + MatrixUtil.bin(i, startState.bits) + ">");
                }
            }
        }
        return finalState;
    }

    public void setStartState(Complex[] starter) {
        if (starter == null) //same as no parameter
        {
            for (int r = 0; r < startState.amplitudes.length; r++) {
                if (r == 0) {
                    startState.amplitudes[r] = Complex.ONE;
                    currentState.amplitudes[r] = Complex.ONE;
                } else {
                    startState.amplitudes[r] = Complex.ZERO;
                    currentState.amplitudes[r] = Complex.ZERO;
                }
            }
        } else {
            int count = 0;
            for (Complex c : starter) {
                startState.amplitudes[count] = c;
                count++;
            }
            count = 0;
            for (Complex c : startState.amplitudes) {
                currentState.amplitudes[count] = c;
                count++;
            }
        }
        if (display) {
            for (int i = 0; i < startState.amplitudes.length; i++) {
                LOG.info("|" + MatrixUtil.bin(i, startState.bits) + ">: " + State.display((startState.amplitudes[i])));
            }
        }
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
        if (gate.type.equals("Measure")) //measures individual qubits
        {
            currentMatrix = MatrixUtil.identity(currentState.size);
            int which = ((Measure) gate).qubit;
            Complex out;
            if (Math.random() < currentState.probQOne(which)) {
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
            Complex[] change = new Complex[currentState.size];
            double div = 0;
            for (int i = 0; i < currentState.size; i++) {
                String temp = MatrixUtil.bin(i, currentState.bits);
                if (temp.charAt(currentState.bits - 1 - which) != bit) {
                    change[i] = Complex.ZERO;
                } else if (temp.charAt(currentState.bits - 1 - which) == bit) {
                    div += Math.pow(currentState.amplitudes[i].abs(), 2);
                }

            }
            for (int i = 0; i < currentState.size; i++) {
                String temp = MatrixUtil.bin(i, currentState.bits);
                if (temp.charAt(currentState.bits - 1 - which) != bit) {
                    change[i] = Complex.ZERO;
                } else if (temp.charAt(currentState.bits - 1 - which) == bit) {
                    Complex a = currentState.amplitudes[i];
                    change[i] = a.divide(Math.sqrt(div));
                }
            }

            for (int i = 0; i < change.length; i++) {
                currentState.amplitudes[i] = change[i];
            }
        } else {
            currentMatrix = MatrixUtil.convert(gate.matrix, gate.inputs, qubits);
            Complex[][] change = MatrixUtil.multiply(currentMatrix, MatrixUtil.colToMatrix(currentState.amplitudes));
            for (int i = 0; i < change.length; i++) {
                currentState.amplitudes[i] = change[i][0];
            }
        }
        step++;
        if (display) {
            LOG.info(" |\n v");
            for (int i = 0; i < currentState.amplitudes.length; i++) {
                LOG.info("|" + MatrixUtil.bin(i, startState.bits) + ">: " + State.display((currentState.amplitudes[i])));
            }
        }
    }

    //runs rest of amplitudes
    public void restOfSteps() {
        for (int i = step; i < gates.size(); i++) {
            step();
        }
    }

    public void resetToBeginning() {
        step = 0;
        setStartState(startState.amplitudes); //sets currentState to startState amplitudes
    }
}