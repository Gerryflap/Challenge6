package ns.tcphack;

class MyTcpHandler extends TcpHandler {
	public static void main(String[] args) {
		new MyTcpHandler();
	}

	public MyTcpHandler() {
		super();

		boolean done = false;
		while (!done) {
			// TODO: Implement your client for the server by combining:
			//        - Send packets, use this.sendData(byte[]).
			//           The data passed to sendData should contain raw
			//           IP/TCP/(optionally HTTP) headers and data.
			//        - Receive packets, you can retreive byte arrays using
			//           byte[] this.receiveData(long timeout).
			//           The timeout passed to this function will indicate the maximum amount of
			//           milliseconds for receiveData to complete. When the timeout expires before a
			//           packet is received, an empty array will be returned.
			//
			//           The data you'll receive and send will and should contain all packet 
			//           data from the network layer and up.
		}
	}

    public class MyTCPPacket {
        public short destPort;
        public short sourcePort;
        public int sequenceNumber;
        public int ackNumber;
        public byte dataOffset = 0;
        public byte reserved;
        public short flags;
        public short windowSize;
        public short checkSum;
        public byte[] data;

        public MyTCPPacket(){

        }

        public byte[] toByteArray(){
            byte[] output = new byte[16];
            output[0] = (byte) (sourcePort >> 8);
            output[1] = (byte) (sourcePort);

            output[2] = (byte) (destPort >> 8);
            output[3] = (byte) (destPort);

            output[4] = (byte) (sequenceNumber >> 24);
            output[5] = (byte) (sequenceNumber >> 16);
            output[6] = (byte) (sequenceNumber >> 8);
            output[7] = (byte) (sequenceNumber);

            output[8] = (byte) (ackNumber >> 24);
            output[9] = (byte) (ackNumber >> 16);
            output[10] = (byte) (ackNumber >> 8);
            output[11] = (byte) (ackNumber);

            output[12] = (byte) (dataOffset << 4 + reserved << 1 + flags >> 8);
            output[13] = (byte) flags;

            output[14] = (byte) (windowSize >> 8);
            output[15] = (byte) (windowSize);

            output[16] = (byte) (checkSum >> 8);
            output[17] = (byte) (checkSum);

            output[18] = (byte) (0);
            output[19] = (byte) (0);

            return output;
        }

    }
}
