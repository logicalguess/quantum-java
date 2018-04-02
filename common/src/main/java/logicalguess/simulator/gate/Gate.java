package logicalguess.simulator.gate;

import org.apache.commons.math3.complex.Complex;

public abstract class Gate {
    public final Complex[][] matrix;
    public final int qubits;
    public final int[] inputs;
    public final int size;
    public final String type;

    public Gate(int size, Complex[][] rep, int[] r, String t) {
        qubits = size;
        matrix = new Complex[rep.length][rep[0].length];
        copy(matrix, rep);
        inputs = r;
        type = t;
        this.size = (int) (Math.pow(2, qubits));
    }

    private void copy(Complex[][] a, Complex[][] b) {
        int i = 0;
        int j = 0;
        for (Complex[] r : b) {
            for (Complex c : r) {
                a[i][j] = c;
                j++;
            }
            j = 0;
            i++;
        }
    }
}