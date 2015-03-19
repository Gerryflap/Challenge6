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
            MyIPv6Packet ipv6Data = new MyIPv6Packet();
            ipv6Data.destAddress = ipv6Data.convertAddress("2001:67c:2564:a170:a00:27ff:fe11:cecb");
            ipv6Data.sourceAddress = ipv6Data.convertAddress("2001:610:1908:f000:2ab2:bdff:fe4a:1af");



            MyTCPPacket tcpData = new MyTCPPacket();
            tcpData.destPort = 7711;
            tcpData.sourcePort = 7777;
            tcpData.sequenceNumber = 1864871;
            //ipv6Data.nextHeader = 6;
            tcpData.flags = Short.parseShort("00000010" , 2);
            tcpData.windowSize = 1024;
            //tcpData.data = new byte[100];

            this.sendData(ipv6Data.toByteArray(tcpData.toByteArray()));
            this.receiveData(1000);




		}
	}
    public class MyIPv6Packet {
        public byte version = 6;
        public byte trafficClass;
        public int flowLabel;
        public byte nextHeader = (byte) 253;
        public byte hopLimit = (byte) 200;
        public byte[] sourceAddress;
        public byte[] destAddress;


        public byte[] convertAddress(String address){
            byte[] out = new byte[16];
            int outInt;
            String[] result = address.split(":");
            for (int i = 0; i < result.length; i++) {
                outInt = Integer.parseInt(result[i], 16);
                out[2*i] = (byte) (outInt >> 8);
                out[2*i+1] = (byte) (outInt);
            }
            return out.clone();
        }

        public byte[] toByteArray(byte[] data){
            short payloadLength = (short) (data.length);
            byte[] output = new byte[40 + payloadLength];
            output[0] = (byte) ((version << 4) + (trafficClass >> 4));

            output[1] = (byte) ((trafficClass << 4) + (flowLabel >> 16));
            output[2] = (byte) (flowLabel >> 8);
            output[3] = (byte) flowLabel;

            output[4] = (byte) (payloadLength >> 8);
            output[5] = (byte) payloadLength;

            output[6] = nextHeader;
            output[7] = hopLimit;

            putInArray(output, sourceAddress, 8);
            putInArray(output, destAddress, 24);
            System.out.println(output[8]);

            putInArray(output, data, 40);
            System.out.println(output[0]);
            return output;
        }

    }
    public class MyTCPPacket {
        public short destPort;
        public short sourcePort;
        public int sequenceNumber;
        public int ackNumber;
        public byte dataOffset = 5;
        public byte reserved;
        public short flags;
        public short windowSize;
        public short checkSum;
        public byte[] data;

        public MyTCPPacket(){
            data = new byte[0];
        }

        public byte[] toByteArray(){
            byte[] output = new byte[20 + data.length];
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

            output[12] = (byte) ((dataOffset << 4) + (reserved << 1) + (flags >> 8));
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

    public static void putInArray(byte[] array1, byte[] array2, int atIndex){
        for (int i = 0; i + atIndex< array1.length && i < array2.length; i++) {
            array1[i + atIndex] = array2[i];
        }
    }
}