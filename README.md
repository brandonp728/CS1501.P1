# CS1501.P2
An implementation of an LZW compression Algorithm. Has three modes: Do nothing, Reset, Monitor. Do nothing lets the code book fill without replacement, reset resets the codebook, monitor tracks the compression ratio and resets the codebook if the ratio of uncompressed data to compressed data is too large.
run with 
java MyLZW + <-n|-r|-m> <file> to compress
java MyLZW - <-n|-r|-m> <file> t decompress
