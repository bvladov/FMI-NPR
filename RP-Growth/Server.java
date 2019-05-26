import java.net.*;
import java.io.*;

public class Server {
	private DatagramSocket server;
	private final int BUFFER_SIZE = 4096;
	private int port = 8888;
	
	public Server() throws SocketException {
		server = new DatagramSocket(this.port);
	}

	public void startRPGrowth(double minraresup, double minrare, String receivedFilename) throws IOException {
		RPMiner miner = new RPMiner(minraresup, minrare);
		miner.processFile(receivedFilename);
		miner.runAlg();
	}

	public void sendResults() throws IOException {
		InetAddress to = InetAddress.getByName("localhost");
		int port = 8889;
		BufferedReader reader = new BufferedReader(new FileReader("resultFile.txt"));
		String line = reader.readLine();
		while(line != null){
			if(!line.isEmpty()){
				DatagramPacket packet = new DatagramPacket(new byte[line.length()], line.length(), to, port);
				packet.setData(line.getBytes());
				server.send(packet);
			}
			line = reader.readLine();
		}
		line = "";
		DatagramPacket empty = new DatagramPacket(new byte[1], 1, to, 8889);
		empty.setData(line.getBytes());
		server.send(empty);
		reader.close();
		System.out.println("Results sent.");
	}
	
	public void listen() throws IOException {
		
		System.out.println("Server listening...");
		while(true){
		try {
			DatagramPacket input = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE-1);
			server.receive(input);
			Double minraresup = Double.parseDouble(new String(input.getData(), 0, input.getLength()));
			server.receive(input);
			Double minsup = Double.parseDouble(new String(input.getData(), 0, input.getLength()));
			server.receive(input);
			
			String receivedFilename = "receivedFile";
			File f = new File(receivedFilename);

			FileOutputStream out = new FileOutputStream(f);

			int bytesReceived = 0;
			DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE-1);
			do { 
				server.receive(packet);
				out.write(packet.getData());
				bytesReceived = packet.getLength();
			} while (packet.getData() != null && bytesReceived > 0 && bytesReceived <= BUFFER_SIZE);

			out.close();
			System.out.println("Received file from client.");
			startRPGrowth(minraresup, minsup, receivedFilename);
			sendResults();
			System.out.println("Server still listening (press Ctrl+C to stop)...");
			} catch(IOException e) { e.printStackTrace(); } 
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Server().listen();
	}
}