package logicalguess.util;

import org.apache.commons.math3.complex.Complex;

import java.util.ArrayList;
import java.util.Collections;

public class MatrixUtil {

    public static Complex[][] identity(int size) {
        Complex[][] m = new Complex[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    m[i][j] = new Complex(1.0);
                } else {
                    m[i][j] = Complex.ZERO;
                }
            }
        }
        return m;
    }

    //turns a number into binary representation of a certain length
    public static String bin(Integer a, int len) {
        String temp = Integer.toBinaryString(a);
        int temp2 = len - temp.length();
        while (temp2 > 0) {
            temp = "0" + temp;
            temp2 = len - temp.length();
        }
        return temp;
    }

    public static Complex[][] expand(Complex[][] matrix, int[] inputs, int s) {
        Complex[][] m = new Complex[(int) Math.pow(2, s)][(int) Math.pow(2, s)];
        for (int r = 0; r < m.length; r++) {
            for (int c = 0; c < m.length; c++) {
                boolean place = false;
                String temp = "";
                String from = num(r, s);
                String from2 = from;
                String to = num(c, s);
                String to2 = to;
                for (int i : inputs) {
                    temp += from.substring(i, i + 1);
                }
                from = temp;
                temp = "";
                for (int i : inputs) {
                    temp += to.substring(i, i + 1);
                }
                to = temp;
                to = new StringBuilder(to).reverse().toString();
                from = new StringBuilder(from).reverse().toString();
                int c1 = Integer.parseInt(to, 2);
                int r1 = Integer.parseInt(from, 2);

                sortGtoL(inputs);

                for (int i : inputs) {
                    from2 = remove(from2, i);
                    to2 = remove(to2, i);
                }

                if (from2.equals(to2)) {
                    place = true;
                }
                if (place) {
                    m[r][c] = matrix[r1][c1];//.divide(Math.sqrt(Math.pow(2,s-1)));
                } else {
                    m[r][c] = Complex.ZERO;
                }
            }
        }
        return m;
    }

    public static Complex[][] multiply(Complex[][] one, Complex[][] two) {
        Complex[][] ret = new Complex[two.length][two[0].length];
        if (ret[0].length == 1) {
            for (int i = 0; i < ret.length; i++) {
                Complex sum = new Complex(0);
                for (int j = 0; j < ret.length; j++) {
                    sum = sum.add((one[i][j].multiply(two[j][0])));
                }
                ret[i][0] = sum;
            }
        } else {
            for (int i = 0; i < ret.length; i++) {
                for (int j = 0; j < ret.length; j++) {
                    Complex sum = new Complex(0);
                    for (int k = 0; k < ret.length; k++) {
                        sum.add((one[i][k].multiply(two[k][j])));
                    }
                    ret[i][j] = sum;
                }
            }
        }
        return ret;
    }

    public static Complex[][] colToMatrix(Complex[] col) {
        Complex[][] two = new Complex[col.length][1];
        for (int i = 0; i < col.length; i++) {
            two[i][0] = col[i];
        }
        return two;
    }

    public static String num(int num, int len) {
        String temp = Integer.toBinaryString(num);
        while (temp.length() < len) {
            temp = "0" + temp;
        }
        return temp;
    }

    public static String remove(String s, int i) {
        if (s.length() == 1)
            return "";
        if (i == 0)
            return s.substring(1);
        if (i == s.length() - 1)
            return s.substring(0, s.length() - 1);
        return s.substring(0, i) + s.substring(i + 1);
    }

    public static void sortGtoL(int[] nums) {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int i : nums) {
            temp.add(i);
        }
        Collections.sort(temp);
        Collections.reverse(temp);
        for (int i = 0; i < temp.size(); i++) {
            nums[i] = temp.get(i);
        }
    }
}