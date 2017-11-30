import java.io.*;
import java.util.Map;
import java.util.HashMap;


public class DataConverter {

    public static void main(String[] args) throws IOException, Exception {

        if (args == null || args.length < 3) {
            String title = "In DataConverter.main: main function needs 3 arguments:\n";
            String args0 = "args0: path_to_the_transition.txt\n";
            String args1 = "args1: path_to_the_final_prN.txt\n";
            String args2 = "args2: path_to_save_result.csv\n";
            throw new Exception(title + args0 + args1 + args2);
        }

        BufferedReader transitionReader = new BufferedReader(new FileReader(args[0]));
        BufferedReader pageRankReader = new BufferedReader(new FileReader(args[1]));
        FileWriter fileWriter = new FileWriter(args[2]);

        Map<String, String> prMap = new HashMap<>();

        String line = pageRankReader.readLine();
        while (line != null) {
            String[] pageRank = line.split("\t");
            prMap.put(pageRank[0], pageRank[1]);
            line = pageRankReader.readLine();
        }
        pageRankReader.close();

        fileWriter.write("source,target,value\n");

        line = transitionReader.readLine();
        while (line != null) {
            String[] fromTo = line.split("\t");
            if (fromTo.length < 2) {
                line = transitionReader.readLine();
                continue;
            }

            String[] toIds = fromTo[1].split(",");
            for (String toId : toIds) {
                String value = prMap.get(toId);
                fileWriter.write(String.format("%s,%s,%s\n", fromTo[0], toId, value));
            }

            line = transitionReader.readLine();
        }
        transitionReader.close();

        fileWriter.flush();
        fileWriter.close();
    }

}