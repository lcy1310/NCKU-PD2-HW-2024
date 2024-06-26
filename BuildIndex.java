import java.io.*;

public class BuildIndex {
    public static void main(String[] args) {
        String filePath = args[0];
        int index = 0;
        int dot=0;
        for(int i=filePath.length()-1; i>=0; i--){
            if(filePath.charAt(i)=='.'){
                dot=i;
            }
            if(filePath.charAt(i)=='/'){
                index=i+1;
                break;
            }
        }
        String outputFileName=filePath.substring(index,dot).toUpperCase()+".txt";
        int linesPerGroup = 5;
        //String outputFileName = filePath.substring(16 , filePath.lastIndexOf('.')) + ".ser";
        //String outputFileName = "corpus0.ser";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath)); BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName))) {
            String line;
            int lineNumber = 0;
            int groupNumber = 0;
            StringBuilder groupText = new StringBuilder();

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String content = line.substring(line.indexOf('\t') + 1);
                String processedLine = content.replaceAll("[^a-zA-Z]", " ").toLowerCase().trim();
                processedLine = processedLine.replaceAll("\\s+", " ");
                groupText.append(processedLine).append(" ");
                if (lineNumber % linesPerGroup == 0) {
                    bw.write(groupNumber + "\t" + groupText.toString().trim());
                    bw.newLine();
                    groupText.setLength(0);
                    groupNumber++;
                }
            }

            if (groupText.length() > 0) {
                bw.write(groupNumber + "\t" + groupText.toString().trim());
                bw.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}