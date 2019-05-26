import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import rpgrowth.AlgoRPGrowth;

public class RPMiner{
    private double minraresup, minsup;
    private final String inputFile = "input.txt";
    private final String resultFile = "resultFile.txt";

    private HashMap<String, Integer> encode;
    private ArrayList<String> decode;

    public RPMiner(double minraresup, double minsup){
        this.minraresup = minraresup;
        this.minsup = minsup;
        encode = new HashMap<>();
        decode = new ArrayList<>();
    }

    public String processFileLine(String line){
        if(line.split(",").length != 8 ) return "";
        String description = line.split(",")[5];
        String ip = line.split(",")[7];
        String toReturn = "";
        int key;

        if(!encode.containsKey(description)){
            key = decode.size();
            encode.put(description, decode.size());
            decode.add(description);
        }
        else
        {
            key = encode.get(description);
        }

        toReturn += Integer.toString(key) + " ";

        if(!encode.containsKey(ip)){
            key = decode.size();
            encode.put(ip, key);
            decode.add(ip);
        }
        else
        {
            key = encode.get(ip);
        }

        toReturn += encode.get(ip);

        return toReturn;
    }

    public void processFile(String filename) throws IOException{

        BufferedReader reader;
        FileWriter fw = new FileWriter(this.inputFile);
        String newLine = System.getProperty("line.separator");

        reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        line = reader.readLine();
        while(line != null){
            line = processFileLine(line);
            fw.write(line + newLine);
            line = reader.readLine();
        }
        fw.close();
        reader.close();
    }

    public void runAlg() throws IOException {
        AlgoRPGrowth algo = new AlgoRPGrowth(this.decode);
        algo.runAlgorithm(this.inputFile, this.resultFile, this.minsup, this.minraresup);
    }
}