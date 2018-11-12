/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/
public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static int L = 512;       // number of codewords = 2^W
    private static int W = 9;         // codeword width
    private static String mode = "";
    private static double oldRatio=0, uncompressedData=0, compressedData=0;
    private static boolean monitorMode=false;


    public static void compress(String cmd) {
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();

        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        if(cmd.equalsIgnoreCase("r") || cmd.equalsIgnoreCase("m"))
        {
          BinaryStdOut.write(cmd, W);
        }

        while (input.length() > 0)
        {
          String s = st.longestPrefixOf(input);  // Find max prefix match s.
          BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
          int t = s.length();
          uncompressedData += t*8;
          compressedData += W;
          if (t < input.length() && code < L)    // Add s to symbol table.
          {
              st.put(input.substring(0, t + 1), code++);
          }
          input = input.substring(t);            // Scan past s in input.

          if(code==L)
          {
            if(W<16)
            {
              W++;
              L = (int)Math.pow(2, W);
            }

            else if(cmd.equalsIgnoreCase("r") && W==16)
            {
              st = new TST<Integer>();
              W=9;
              L=(int)Math.pow(2, W);
              for (int i = 0; i < R; i++)
              {
                st.put("" + (char) i, i);
              }
              code = R+1;
            }

            else if(cmd.equalsIgnoreCase("m") && W==16 && !monitorMode)
            {
              monitorMode = true;
              oldRatio=uncompressedData/compressedData;
            }

            if(monitorMode)
            {
              double ratio = uncompressedData/compressedData;
              double currRatio=oldRatio/ratio;
              if(currRatio>1.1)
              {
                st = new TST<Integer>();
                W=9;
                L=(int)Math.pow(2, W);
                for (int i = 0; i < R; i++)
                {
                  st.put("" + (char) i, i);
                }
                code = R+1;
                monitorMode=false;
              }
            }
          }

        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }


    public static void expand() {
        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];
        if(val.equalsIgnoreCase("r"))
        {
          mode = "reset";
          codeword = BinaryStdIn.readInt(W);
          if (codeword == R) return;           // expanded message is empty string
          val = st[codeword];
        }
        else if(val.equalsIgnoreCase("m"))
        {
          mode = "monitor";
          codeword = BinaryStdIn.readInt(W);
          if (codeword == R) return;           // expanded message is empty string
          val = st[codeword];
        }

        while (true)
        {
          compressedData += val.length()*8;
          uncompressedData += W;
          BinaryStdOut.write(val);
          codeword = BinaryStdIn.readInt(W);
          if (codeword == R) break;
          String s = st[codeword];
          if (i == codeword)
          {
            s = val + val.charAt(0);   // special case hack
          }
          if (i < L)
          {
            st[i++] = val + s.charAt(0);
          }
          val = s;

          if(i==L-1)
          {
            if(W<16)
            {
              W++;
              L=(int)Math.pow(2, W);
              String[] tempArray = new String[L];
              for(int x =0; x<st.length; x++)
              {
                tempArray[x] = st[x];
              }
              st=tempArray;
            }

            else if(mode.equalsIgnoreCase("reset") && W==16)
            {
              W=9;
              L=(int)Math.pow(2, W);
              st = new String[L];
              for (i=0; i < R; i++)
              {
                st[i] = "" + (char) i;
							}
              st[i++] = "";
              i = R+1;
              codeword = BinaryStdIn.readInt(W);
              if (codeword == R) return;
              val = st[codeword];
            }

            else if(mode.equalsIgnoreCase("monitor") && W==16 && !monitorMode)
            {
              monitorMode = true;
              oldRatio=uncompressedData/compressedData;
            }

            if(monitorMode)
            {
              double ratio = uncompressedData/compressedData;
              double currRatio=oldRatio/ratio;
              if(currRatio>1.1)
              {
                W=9;
                L=(int)Math.pow(2, W);
                st = new String[L];
                for (i=0; i < R; i++)
                {
                  st[i] = "" + (char) i;
                }
                st[i++] = "";
                i = R+1;
                monitorMode=false;
                codeword = BinaryStdIn.readInt(W);
                if (codeword == R) return;
                val = st[codeword];
              }
            }
          }
        }
      BinaryStdOut.close();
    }



    public static void main(String[] args) {
        if      (args[0].equals("-")) compress(args[1]);
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
