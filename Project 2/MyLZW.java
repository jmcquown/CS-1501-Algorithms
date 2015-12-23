import java.lang.*;
import java.util.*;
//Files that change: all.tar, frosty.jpg, large.txt, texts.tar, wacky.bmp
//Monitor mode varies as well

public class MyLZW {
    private static final int R = 256; // number of input chars
    private static final int max = 65536;
    private static int[] W = new int[] { 9, 10, 11, 12, 13, 14, 15, 16 }; // Array
                                                                            // of
                                                                            // available
                                                                            // code
                                                                            // widths
    private static int wCount = 0; // Counter for the position in the array
    private static int L = 512; // number of codewords = 2^W
    private static boolean reset = false, doNothing = false, monitor = false, ratioFlag = false;
    private static int inputSize = 0, outputSize = 0; // Keeps track of the
                                                        // number of bits being
                                                        // input and output
    private static double compressionRatio = 0.0, oldRatio = 0.0;
    

    // private static

    public static void compress() {
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R + 1; // R is codeword for EOF

        if (doNothing) 
            BinaryStdOut.write('n');

        if (reset) 
            BinaryStdOut.write('r');

        if (monitor) 
            BinaryStdOut.write('m');

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input); // Find max prefix match s.
            inputSize += (8 * s.length()); // Calculate the number of bits in
                                            // the string prefix
            BinaryStdOut.write(st.get(s), W[wCount]); // Print s's encoding.
            outputSize += W[wCount]; // Calculate the total number of bits that
                                        // are being written to the file
            outputSize++; // Increment the number of bits that are being output
            int t = s.length();

            // If the total num of chars in the TST is greater than or equal to
            // the max number of bits
            if (code == L) {
                // If we are not at the end of the array
                if (wCount != 7) {
                    wCount++;
                    L = 2 * L;
                }
                // If reset mode
                if (reset) {
                    // If we are at the end of the array and the number of
                    // characters in the tree is = 2^16
                    if (code == max) {
                        // Create an empty TST
                        st = new TST<Integer>();
                        // Add all possible ASCII characters to the empty tree
                        for (int i = 0; i < R; i++)
                            st.put("" + (char) i, i);
                        code = R + 1;
                        wCount = 0;
                        L = 512;

                    }
                }
                // If monitor mode and the codebook is currently full at the
                // given bit width
                if (monitor) {

                    if (outputSize != 0)
                            compressionRatio = inputSize / outputSize;
                    if (wCount == 7) {
                        //If 
                        if (ratioFlag) {
                            //If ratio is greater than 1.1 reset TST
                            if ((oldRatio / compressionRatio) > 1.1) {
                                //Reset
                                // Create an empty TST
                                st = new TST<Integer>();
                                // Add all possible ASCII characters to the empty tree
                                for (int i = 0; i < R; i++)
                                    st.put("" + (char) i, i);
                                code = R + 1;

                                outputSize = 0;
                                inputSize = 0;
                                

                                ratioFlag = false;
                            }

                        } 
                        else {
                            oldRatio = compressionRatio;
                            ratioFlag = true;
                        }
                        
                    }

                }

            }

            

            if (t < input.length() && code < max)
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t); // Scan past s in input.
        }
        BinaryStdOut.write(R, W[wCount]);
        BinaryStdOut.close();
    }

    public static void expand() {
        char mode = BinaryStdIn.readChar();
        if (mode == 'n')
            doNothing = true;
        if (mode == 'r')
            reset = true;
        if (mode == 'm')
            monitor = true;

        String[] st = new String[max];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = ""; // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W[wCount]);
        if (codeword == R)
            return; // expanded message is empty string
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
            
            if (i == L) {
                if (wCount != 7) {
                    wCount++; // Increment the index in the array
                    L = 2 * L; // Recalculate L
                }
                //If reset mode
                if (reset) {
                    if (i == max) {
                        st = new String[max];
                        for (i = 0; i < R; i++)
                            st[i] = "" + (char) i;
                        st[i++] = "";
                        wCount = 0;
                        L = 512;
                        
                    }
                }
                //If monitor mode
                if (monitor) {
                    if (outputSize != 0) 
                        compressionRatio = inputSize / outputSize;
                    
                    if (wCount == 7) {
                        if (ratioFlag) {
                            if ((oldRatio / compressionRatio) > 1.1) {
                                st = new String[max];
                                for (i = 0; i < R; i++)
                                    st[i] = "" + (char) i;
                                st[i++] = "";

                                inputSize = 0;
                                outputSize = 0;
                                ratioFlag = false;
                            }
                        }
                        else {
                            oldRatio = compressionRatio;
                            ratioFlag = true;
                        }
                    }

                }

            }
            codeword = BinaryStdIn.readInt(W[wCount]);
            inputSize += W[wCount];

            if (codeword == R)
                break;
            String s = st[codeword];
            if (i == codeword)
                s = val + val.charAt(0); // special case hack
            if (i < L)
                st[i++] = val + s.charAt(0);
            val = s;
            outputSize += (8 * val.length());
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {

        if (args[0].equals("-")) {
            if (args[1].equals("n"))
                doNothing = true;
            else if (args[1].equals("r"))
                reset = true;
            else if (args[1].equals("m"))
                monitor = true;
            compress();
        } else if (args[0].equals("+"))
            expand();
        else
            throw new IllegalArgumentException("Illegal command line argument");
    }

}
