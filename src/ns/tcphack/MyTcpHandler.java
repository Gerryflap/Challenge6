package ns.tcphack;

class MyTcpHandler extends TcpHandler {
    public static final int OUR_PORT = 7777;
    public static final int SERVER_PORT = 7711;
    public static short windowSize = 4976;

	public static void main(String[] args) {
		new MyTcpHandler();
	}

    public String httpGet = "GET /?nr=1546856 HTTP/1.0\r\n";


	public MyTcpHandler() {
		super();

		boolean done = false;


        //Syn-packet:
        MyIPv6Packet ipv6Data = new MyIPv6Packet();
        ipv6Data.destAddress = ipv6Data.convertAddress("2001:67c:2564:a170:a00:27ff:fe11:cecb");
        ipv6Data.sourceAddress = ipv6Data.convertAddress("2001:610:1908:f000:f1e3:8ff4:2c07:cbf7");


        MyTCPPacket tcpData = new MyTCPPacket();
        tcpData.destPort = SERVER_PORT;
        tcpData.sourcePort = OUR_PORT;
        tcpData.sequenceNumber = 0;
        tcpData.windowSize = windowSize;

        tcpData.flags = Short.parseShort("000000010" , 2);
        this.sendData(ipv6Data.toByteArray(tcpData.toByteArray()));


		while (!done) {

            MyTCPPacket recieved = new MyTCPPacket();
            try {
                //Read the recieved packet:
                recieved.fill(this.receiveData(3000));


                if(recieved.windowSize != 0){
                    windowSize = recieved.windowSize;
                }

                byte[] getArray = this.httpGet.getBytes();
                MyTCPPacket ack = new MyTCPPacket();
                ack.ackNumber = recieved.sequenceNumber + 1;
                ack.sequenceNumber = recieved.ackNumber;
                ack.sourcePort = OUR_PORT;
                ack.destPort = SERVER_PORT;

                if ((recieved.flags & 4)!= 0){
                    //RST flag is set:
                    ack.flags = Short.parseShort("000000010" , 2);
                    ack.windowSize = windowSize;
                } else {
                    //Send ACK with GET Request:
                    ack.flags = Short.parseShort("000010000", 2);
                    ack.windowSize = windowSize;
                    ack.data = getArray;
                }
                this.sendData(ipv6Data.toByteArray(ack.toByteArray()));



                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            catch(ArrayIndexOutOfBoundsException e) {
                //If the connection is not open:

                this.sendData(ipv6Data.toByteArray(tcpData.toByteArray()));

            }


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

            putInArray(output, data, 40);
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

        public void fill(byte[] packet){
            sequenceNumber = ((fB(packet[40 + 4]) << 24) | (fB(packet[40 + 5]) << 16) | (fB(packet[40 + 6]) << 8) | (fB(packet[40 + 7])));
            ackNumber = (fB(packet[40 + 8]) << 24) | (fB(packet[40 + 9]) << 16) | (fB(packet[40 + 10]) << 8) | (fB(packet[40 + 11]));
            flags = (short) ((packet[40 + 12]  << 8) + packet[40 + 13]);
            windowSize = (short) ((packet[40+14] << 8) + packet[40+15]);
        }


    }

    public static int fB(byte data){
        //Function to fix signed stuff.
        long dataL = (long) data;
        int dataI = (int )dataL & 0xff;
        return dataI;
    }

    public static void putInArray(byte[] array1, byte[] array2, int atIndex){
        for (int i = 0; i + atIndex< array1.length && i < array2.length; i++) {
            array1[i + atIndex] = array2[i];
        }
    }
}