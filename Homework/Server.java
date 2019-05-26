import java.net.*; 
import java.io.*; 
public class Server 
{
    private int port = 8080;
    private String name;
    private MulticastSocket socket;
    private InetAddress group;


    public Server(String group, int port) throws IOException{
        this.group = InetAddress.getByName(group);
        this.port = port;
        socket = new MulticastSocket(this.port);
        socket.joinGroup(this.group);
    }

    public void sendText() throws IOException, InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String message;
        System.out.print('>');
        message = reader.readLine();
        message = this.name + ": " + message; 
        byte[] buffer = message.getBytes(); 
        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port); 
        this.socket.send(datagram); 
        Thread.sleep(1);
    }

    public void sendFile() throws IOException, InterruptedException {
        System.out.print(">Enter file name: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String filename = reader.readLine();
        File f = new File(filename);
        FileInputStream in = new FileInputStream(f);
        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = in.read(buffer, 0, 1024)) > 0) {
            DatagramPacket packet = new DatagramPacket(new byte[1024], bytesRead, this.group, this.port);
            packet.setData(buffer, 0, bytesRead);
            this.socket.send(packet);
            Thread.sleep(1);
        }
        DatagramPacket empty = new DatagramPacket(new byte[1], 1, this.group, this.port);
		empty.setData("".getBytes());
		this.socket.send(empty);
		in.close();
    }

    public void Start() throws IOException, InterruptedException{
        Thread t = new Thread(new Client(socket,group,port)); 
        t.start();  

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(">Enter your name: ");      
        this.name = reader.readLine();       
        while(true) 
        {
            System.out.println(">Enter message type (TEXT=1, IMAGE=2, VIDEO=3): ");
            System.out.print('>');
            String type = reader.readLine();
            DatagramPacket packet = new DatagramPacket(new byte[16], 16 ,this.group, this.port); 
            packet.setData(type.getBytes());
            this.socket.send(packet);

            if (type.equals("1")) sendText();
            else                  sendFile();         
        }  
    }
    public static void main(String[] args) throws IOException, InterruptedException { 
        new Server("230.0.0.1", 8080).Start();
    } 
} 