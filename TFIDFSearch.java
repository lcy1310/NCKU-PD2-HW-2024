import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringTokenizer;

public class TFIDFSearch {
    public static int fileAmount=0;
    public static List<List<String>> lines = new ArrayList<>();
    public static List<String> searchWords = new ArrayList<>();
    private static Set<String> uniqueSearchWords = new HashSet<>();
    public static ArrayList<Integer> dfList = new ArrayList<>();
    public static ArrayList<Integer> totalWordsCountList = new ArrayList<>();
    public static ArrayList<Trie> trieList = new ArrayList<>();
    public static List<List<Integer>> answer = new ArrayList<>();
    public static List<HashMap<Integer, Double>> data = new ArrayList<>();

    static class TrieNode {
        TrieNode[] children;
        boolean isEndOfWord;
        int count;

        TrieNode() {
            children = new TrieNode[26];
            isEndOfWord = false;
            count = 0;
        }
    }

    static class Trie {
        private TrieNode root;

        Trie() {
            root = new TrieNode();
        }

        void insert(String word) {
            TrieNode current = root;
            for (char ch : word.toCharArray()) {
                int index = ch - 'a';
                if (current.children[index] == null) {
                    current.children[index] = new TrieNode();
                }
                current = current.children[index];
            }
            current.isEndOfWord = true;
            current.count++;
        }

        int search(String word) {
            TrieNode current = root;
            for (char ch : word.toCharArray()) {
                int index = ch - 'a';
                if (current.children[index] == null) {
                    return 0;
                }
                current = current.children[index];
            }
            return current.count;
        }

        boolean containsWord(String word) {
            TrieNode current = root;
            for (char ch : word.toCharArray()) {
                int index = ch - 'a';
                if (current.children[index] == null) {
                    return false;
                }
                current = current.children[index];
            }
            return current.isEndOfWord;
        }
    }

    public static void main(String[] args){
        String corpus = args[0].toUpperCase()+".txt";
        String testCaseFile = args[1];
        readTestCase(testCaseFile);
        for (int i = 0; i < searchWords.size(); i++) {
            dfList.add(0);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(corpus))) {
            String line;
            //int lineNumber = -1;
            Trie trie = new Trie();
            int totalWordsCount = 0;
            while ((line = br.readLine()) != null){
                //lineNumber++;
                trie = new Trie();
                totalWordsCount = 0;
                String[] parts = line.split("\t",2); // test in vscode should be revised as blank " " // remember to fix as \t //
                int number = Integer.parseInt(parts[0]);
                String processedLine = parts[1];
                StringTokenizer tokenizer = new StringTokenizer(processedLine);
                while (tokenizer.hasMoreTokens()) {
                    String word = tokenizer.nextToken();
                    trie.insert(word);
                    totalWordsCount++;
                }
                trieList.add(trie);
                totalWordsCountList.add(totalWordsCount);
                for (int i = 0; i < searchWords.size(); i++) {
                    if (trie.containsWord(searchWords.get(i))) {
                        dfList.set(i, dfList.get(i) + 1);
                    }
                }

            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        //newsearch.addAll(uniqueSearchWords);
        TFIDFCalculator();
        for (List<String> line : lines) {
            List<Integer> result = processLine(line, fileAmount);
            answer.add(result);
        }

        try {
            writeAnswerToFile("output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*System.out.println("lines:");
        System.out.println(lines);
        System.out.println("searchWords:");
        System.out.println(searchWords);
        System.out.println("dflist");
        System.out.println(dfList);
        System.out.println("uniqueSearchWords:");
        System.out.println(uniqueSearchWords);*/
        /*System.out.println("data:");
        System.out.println(data);
        System.out.println("answer:");
        System.out.println(answer);*/
    }

    public static void readTestCase(String testCaseFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(testCaseFile))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) {
                    fileAmount=Integer.parseInt(line);
                } else{
                    String[] parts = line.split(" ");
                    List<String> lineElements = new ArrayList<>();
                    Set<String> uniqueElements = new HashSet<>();
                    for (String part : parts) {
                        if (!uniqueElements.contains(part)) {
                            uniqueElements.add(part);
                            lineElements.add(part);
                            if (!uniqueSearchWords.contains(part)) {
                                uniqueSearchWords.add(part);
                                searchWords.add(part);
                            }
                        }
                    }
                    lines.add(lineElements);
                }
            }
            searchWords.remove("AND");
            searchWords.remove("OR");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void TFIDFCalculator(){
        for (int i=0 ; i<searchWords.size(); i++) {
            double tfidf = 0.0;
            int amount = trieList.size();
            String WORD=searchWords.get(i);
            HashMap<Integer, Double> map = new HashMap<>();
            if(dfList.get(i)==0){
                data.add(map);
                continue;
            }
            for(int j=0; j<trieList.size(); j++){
                Trie targetTrie = trieList.get(j);
                if(!targetTrie.containsWord(WORD)){   //!targetTrie.containsWord(WORD)
                    continue;
                }
                int tfBaby = targetTrie.search(WORD);
                int tfMother = totalWordsCountList.get(j);
                int df = dfList.get(i);
                double result = (((double)tfBaby/(double)tfMother)*(Math.log((double)amount/(double)df)));
                //System.out.println(result);
                map.put(j, result);
            }
            //System.out.println(map);
            data.add(map);
            //map.clear();
        }
        //System.out.println(data.get(1));
    }

    /*private static List<Integer> getTopKResults(HashMap<Integer, Double> intermediateResult, int K) {
        PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>((a, b) -> {
            if (a.getValue().equals(b.getValue())) {
                return a.getKey() - b.getKey();
            }
            return Double.compare(b.getValue(), a.getValue());
        });

        for (Map.Entry<Integer, Double> entry : intermediateResult.entrySet()) {
            pq.offer(entry);
            if (pq.size() > K) {
                pq.poll();
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(0, pq.poll().getKey());
        }

        return result;
    }*/

    public static List<Integer> processLine(List<String> line, int K) {
        List<Integer> result = new ArrayList<>();
        HashMap<Integer, Double> intermediateResult = new HashMap<>();
        boolean hasAND = false;
        boolean hasOR = false;

        for (String word : line) {
            if (word.equals("AND")) {
                hasAND = true;
            } else if (word.equals("OR")) {
                hasOR = true;
            }
        }

        if (hasAND && !hasOR) {
            // Process AND query
            HashSet<Integer> commonKeys = new HashSet<>();
            boolean firstWord = true;

            for (String word : line) {
                if (!word.equals("AND")) {
                    int index = searchWords.indexOf(word);
                    if (index != -1) {
                        HashMap<Integer, Double> wordData = data.get(index);
                        if (firstWord) {
                            commonKeys.addAll(wordData.keySet());
                            firstWord = false;
                        } else {
                            commonKeys.retainAll(wordData.keySet());
                        }
                    }
                }
            }

            if (!commonKeys.isEmpty()) {
                for (String word : line) {
                    if (!word.equals("AND")) {
                        int index = searchWords.indexOf(word);
                        if (index != -1) {
                            HashMap<Integer, Double> wordData = data.get(index);
                            for (Integer key : commonKeys) {
                                double value = wordData.get(key);
                                intermediateResult.put(key, intermediateResult.getOrDefault(key, 0.0) + value);
                            }
                        }
                    }
                }
            }
        } else if (!hasAND && hasOR) {
            // Process OR query
            for (String word : line) {
                if (!word.equals("OR")) {
                    int index = searchWords.indexOf(word);
                    if (index != -1) {
                        HashMap<Integer, Double> wordData = data.get(index);
                        for (Map.Entry<Integer, Double> entry : wordData.entrySet()) {
                            int key = entry.getKey();
                            double value = entry.getValue();
                            intermediateResult.put(key, intermediateResult.getOrDefault(key, 0.0) + value);
                            //intermediateResult.put(key, Math.max(intermediateResult.getOrDefault(key, 0.0), value));
                        }
                    }
                }
            }
        } else {
            // Process single word queries
            for (String word : line) {
                int index = searchWords.indexOf(word);
                if (index != -1) {
                    HashMap<Integer, Double> wordData = data.get(index);
                    for (Map.Entry<Integer, Double> entry : wordData.entrySet()) {
                        int key = entry.getKey();
                        double value = entry.getValue();
                        intermediateResult.put(key, intermediateResult.getOrDefault(key, 0.0) + value);
                    }
                }
            }
        }

        result = getTopKResults(intermediateResult,new ArrayList<>(intermediateResult.keySet()), K);

        while (result.size() < K) {
            result.add(-1);
        }

        return result;
    }

    private static List<Integer> getTopKResults(HashMap<Integer, Double> intermediateResult, List<Integer> docIndices, int K) {
    PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> {
        double diff = intermediateResult.get(a) - intermediateResult.get(b);
        if (diff == 0) {
            return Integer.compare(b, a); 
        }
        return Double.compare(diff, 0);
    });

    for (int docIndex : docIndices) {
        pq.offer(docIndex);
        if (pq.size() > K) {
            pq.poll();
        }
    }

    List<Integer> result = new ArrayList<>();
    while (!pq.isEmpty()) {
        result.add(pq.poll());
    }

    Collections.reverse(result); 
    return result;
    }

    public static void writeAnswerToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            StringBuilder sb = new StringBuilder();
            int numberOfLists = answer.size();
            for (int i = 0; i < numberOfLists; i++) {
                List<Integer> list = answer.get(i);
                for (int j = 0; j < list.size(); j++) {
                    if (j != 0) {
                        sb.append(" ");
                    }
                    sb.append(list.get(j));
                }
                if (i != numberOfLists - 1) {
                    sb.append("\n");
                }
            }
            writer.write(sb.toString());
        }
    }
}