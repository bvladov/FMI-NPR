import java.net.*;
import java.io.*;

public class Client {
	private final int BUFFER_SIZE = 4096;
	private DatagramSocket client;
	private final int port = 8888;
	
	public Client() throws SocketException {
		client = new DatagramSocket(8889);
	}
	
	public void send(String minraresup, String minrare, String filename) throws IOException, InterruptedException {
		InetAddress to = InetAddress.getByName("localhost");

		DatagramPacket mrs = new DatagramPacket(minraresup.getBytes(), minraresup.length(), to, port);
		DatagramPacket ms = new DatagramPacket(minrare.getBytes(), minrare.length(), to, port);
		client.send(mrs);
		client.send(ms);

		File f = new File(filename);
		FileInputStream in = new FileInputStream(f);
		int bytesRead = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		
		while((bytesRead = in.read(buffer, 0, BUFFER_SIZE)) > 0) {
			DatagramPacket packet = new DatagramPacket(new byte[bytesRead], bytesRead, to, port);
			packet.setData(buffer, 0, bytesRead);
			client.send(packet);
			Thread.sleep(1);
		}

	    DatagramPacket empty = new DatagramPacket(new byte[1], 1, to, port);
		empty.setData("".getBytes());
		client.send(empty);
		in.close();
	}

	public void receive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
		int bytesReceived = 0;
		do{
			packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
			client.receive(packet);
			System.out.println(new String(packet.getData(), 0, packet.getLength()));
			bytesReceived = packet.getLength();
		} while(packet.getData() != null && bytesReceived > 0 && bytesReceived <= BUFFER_SIZE);
	}
	
	public static void main(String[] args) throws SocketException, IOException, InterruptedException {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter minraresup:");
		String minraresup = input.readLine();

		System.out.println("Enter minsup (should be higher than minraresup):");
		String minsup = input.readLine();

		System.out.println("Enter filename (file should be in the same folder) or leave blank for 'logs_BCS37_20181103_UTF8.txt':");
		String filename = input.readLine();
		if(filename.equals("")) filename = "logs_BCS37_20181103_UTF8.txt";

		Client client = new Client();
		client.send(minraresup, minsup, filename);
		client.receive();
	}
}
