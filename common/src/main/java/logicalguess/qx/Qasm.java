package logicalguess.qx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Qasm {

    private transient Logger logger = LoggerFactory.getLogger(Qasm.class);

    private String qasmCodeFilePath;
    private File qasmCodeFile;
    private String[] qasmCode;

    public Qasm(String qasmCodeFilePath) {
        setQasmCodeFilePath(qasmCodeFilePath);
        setQasmCodeFile(qasmCodeFilePath);
        if (!this.getQasmCodeFilePath().equals("")) {
            try {
                setQasmCode(extractQasmCode());
            } catch (Exception err) {
                logger.error(err.getMessage());
            }
        }
    }

    /**
     * These are the getter functions for Qasm
     */
    public String getQasmCodeFilePath() {
        return qasmCodeFilePath;
    }

    public File getQasmCodeFile() {
        return qasmCodeFile;
    }

    public String[] getQasmCode() {

        return qasmCode;

    }


    /**
     * These are the setter functions for Qasm
     */
    public void setQasmCodeFilePath(String qasmCodeFilePath) {
        this.qasmCodeFilePath = qasmCodeFilePath;
    }

    public void setQasmCodeFile(String qasmCodeFilePath) {
        this.qasmCodeFile = new File(qasmCodeFilePath);
    }

    public void setQasmCode(String[] qasmCode) {
        this.qasmCode = qasmCode;
    }

    // This function extracts the qasm code from a file
    public String[] extractQasmCode() throws FileNotFoundException, IllegalArgumentException {

        if (getQasmCodeFile() == null) {
            throw new IllegalArgumentException("No Qasm code file given/set");
        }

        Scanner sc = new Scanner(this.qasmCodeFile);
        List<String> tempCodeArray = new ArrayList<String>();

        while (sc.hasNextLine()) {

            tempCodeArray.add(sc.nextLine());

        }

        String[] qasmCodeFromFile = tempCodeArray.toArray(new String[0]);

        return qasmCodeFromFile;

    }

    public String[] extractQasmCode(File qasmCodeFile) throws FileNotFoundException {

        Scanner sc = new Scanner(qasmCodeFile);
        List<String> tempCodeArray = new ArrayList<String>();

        while (sc.hasNextLine()) {

            tempCodeArray.add(sc.nextLine());

        }

        String[] qasmCodeFromFile = tempCodeArray.toArray(new String[0]);

        return qasmCodeFromFile;

    }


    // This replaces a substring in the code to a new string
    public String[] replace(String oldLineOfCode, String newLineOfCode) throws IllegalArgumentException {

        int index = 0;

        if (getQasmCode() == null)
            throw new IllegalArgumentException("No Qasm code provided.");

        for (String lineOfCode : getQasmCode()) {

            if (lineOfCode.equals(oldLineOfCode)) {

                this.qasmCode[index] = newLineOfCode;
                return this.qasmCode;

            } else {

                index++;

            }

        }

        return this.qasmCode;

    }

}
