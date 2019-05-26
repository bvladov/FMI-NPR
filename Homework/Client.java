import java.io.*;
import java.net.*;

public class Client implements Runnable {
	private MulticastSocket socket;
	private InetAddress group;
	private int port;

	public Client(MulticastSocket socket, InetAddress group, int port){
		this.socket = socket;
		this.group = group;
		this.port = port;
	}

	public void receiveText() throws IOException{
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);
		socket.receive(packet);
		System.out.println(new String(packet.getData(), 0, packet.getLength()));
	}

	public void receiveFile(String type) throws IOException{
		String outputfile = "receivedFile" + String.valueOf(System.currentTimeMillis());
		if(type.equals("2")) outputfile += ".jpg";
		else outputfile += ".mp4";

		File f = new File(outputfile);
		FileOutputStream out = new FileOutputStream(f);
		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
		int bytesReceived = 0;
		do {
			socket.receive(packet);
			out.write(packet.getData());
			bytesReceived = packet.getLength();
		} while (packet.getData() != null && bytesReceived > 0 && bytesReceived <= 1024);
		out.close();
	}
	@Override
	public void run(){
		while(true){
			try {
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);
				socket.receive(packet);
				String type = new String(packet.getData(), 0, packet.getLength());
				if(type.equals("1")) receiveText();
				else receiveFile(type);
			}catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}