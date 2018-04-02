package logicalguess.simulator.circuit;

import org.apache.commons.math3.complex.Complex;

public class State {
    public final int bits;
    public final int size;
    public final Complex[] amplitudes;

    public State(int numBits) {
        bits = numBits;
        size = (int) Math.pow(2, bits);
        amplitudes = new Complex[size];
    }

    public Complex getC(int index) {
        return amplitudes[index];
    }

    public void set(int index, Complex value) {
        amplitudes[index] = value;
    }

    public boolean isPossible() {
        double prob = 0;
        for (int i = 0; i < size; i++) {
            prob += prob(getC(i));
        }
        if (Math.abs(1.0 - prob) < 0.00000001)
            return true;
        return false;
    }

    void normalize() {
        if (!isPossible()) {
            double coeff = 0;
            for (int i = 0; i < size; i++) {
                coeff += prob(getC(i));
            }
            coeff = Math.sqrt(coeff);
            for (int i = 0; i < amplitudes.length; i++) {
                amplitudes[i] = amplitudes[i].divide(coeff);
            }
        }
    }

    private double prob(Complex c) {
        return Math.pow(c.abs(), 2);
    }

    double probState(int i) {
        return prob(getC(i));
    }

    double probQOne(int j) //returns probability of a single qubit being one
    {
        double prob = 0;
        for (int i = 0; i < size; i++) {
            if (i / ((int) Math.pow(2, j)) % 2 == 1)
                prob += prob(getC(i));
        }
        return prob;
    }

    public double probQZero(int j) {

        return 1 - probQOne(j);
    }

    //returns formatted string for complex number
    public static String display(Complex c) {
        int sigFigs = 4; //can change at will

        double r = c.getReal();
        boolean rInt = isInt(r);
        double i = c.getImaginary();
        boolean iInt = isInt(i);
        boolean pureImaginary = (r == 0);
        boolean pureReal = (i == 0);
        String op = "+";
        if (i < 0)
            op = "";

        String left; //real
        String right; //imaginary

        if (pureImaginary) {
            left = "";
        } else if (rInt && !pureReal) {
            left = "" + Math.round(r) + op;
        } else if (rInt && pureReal) {
            left = "" + Math.round(r);
        } else if (pureReal) {
            left = "" + (Math.round(Math.pow(10, sigFigs) * r) / Math.pow(10, sigFigs));
        } else {
            left = "" + (Math.round(Math.pow(10, sigFigs) * r) / Math.pow(10, sigFigs)) + op;
        }

        if (pureReal) {
            right = "";
        } else if (iInt && Math.round(i) != 1 && Math.round(i) != -1) {
            right = "" + Math.round(i) + "i";
        } else if (iInt && Math.round(i) == 1) {
            right = "i";
        } else if (iInt && Math.round(i) == -1) {
            right = "-i";
        } else {
            right = "" + (Math.round(Math.pow(10, sigFigs) * i) / Math.pow(10, sigFigs)) + "i";
        }
        if (r == 0 && i == 0)
            return "0";
        return left + right;
    }

    //checks if double is sufficiently close to an integer
    private static boolean isInt(double d) {
        double error = Math.pow(10, -8);
        if (Math.abs(d - Math.round(d)) < error) {
            return true;
        }
        return false;
    }

    public static int bitsToRepresent(int i) {
        i += 1;
        i = (int) (Math.ceil(log2(i)));
        return i;
    }

    private static double logB(double d, double b) {
        return Math.log(d) / Math.log(b);
    }

    private static double log2(double d) {
        return logB(d, 2);
    }

    public static void transposeMatrix(Complex[][] m) {
        Complex[][] temp = new Complex[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[j][i] = m[i][j];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                m[i][j] = temp[i][j];
    }

}