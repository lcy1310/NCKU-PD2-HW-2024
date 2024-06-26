import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

public class TFIDFCalculator {
    public static ArrayList<String> words = new ArrayList<>();
    public static ArrayList<Integer> numbers = new ArrayList<>();
    public static ArrayList<Double> answer = new ArrayList<>();
    public static ArrayList<Integer> dfList = new ArrayList<>();
    public static HashSet<String> uniqueWords = new HashSet<>();

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

    public static void main(String[] args) {
        String filePath = args[0];
        String testCaseFile = args[1];
        readTestCase(testCaseFile);
        int linesPerGroup = 5;
        String searchWord = "";
        ArrayList<Trie> trieList = new ArrayList<>();
        ArrayList<Integer> totalWordsCountList = new ArrayList<>();
        int textGroupsWithSearchWord = 0;
        int numberOfGroups = 0;
        for (int i = 0; i < uniqueWords.size(); i++) {
            dfList.add(0);
        }

        ArrayList<String> uniqueWordsList = new ArrayList<>(uniqueWords);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            Trie trie = new Trie();
            int totalWordsCount = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (lineNumber % linesPerGroup == 1) {
                    trie = new Trie();
                    totalWordsCount = 0;
                }
                String processedLine = line.replaceAll("[^a-zA-Z]", " ").toLowerCase();
                StringTokenizer tokenizer = new StringTokenizer(processedLine);
                while (tokenizer.hasMoreTokens()) {
                    String word = tokenizer.nextToken();
                    trie.insert(word);
                    totalWordsCount++;
                }
                if (lineNumber % linesPerGroup == 0) {
                    trieList.add(trie);
                    totalWordsCountList.add(totalWordsCount);
                    numberOfGroups++;
                    for (int i = 0; i < uniqueWordsList.size(); i++) {
                        if (trie.containsWord(uniqueWordsList.get(i))) {
                            dfList.set(i, dfList.get(i) + 1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int j = 0; j < words.size(); j++) {
            searchWord = words.get(j);
            int chosenText = numbers.get(j);
            int wordIndex = uniqueWordsList.indexOf(searchWord);
            Trie currentTrie = trieList.get(chosenText);
            int count = currentTrie.containsWord(searchWord) ? currentTrie.search(searchWord) : 0;
            if(count==0){
                double special=0.0;
                answer.add(special);
                continue;
            }
            int df = dfList.get(wordIndex);
            int totalWordsCount = totalWordsCountList.get(chosenText);
            double tfidf = ((double) count / (double) totalWordsCount) * (Math.log((double) numberOfGroups / (double) df));
            answer.add(tfidf);
        }

        writeArrayListToFile(answer);
    }

    public static void readTestCase(String testCaseFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(testCaseFile))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) {
                    String[] wordArray = line.toLowerCase().split("\\s+");
                    for (String word : wordArray) {
                        words.add(word);
                        uniqueWords.add(word);
                    }
                } else if (lineNumber == 2) {
                    String[] numberArray = line.split("\\s+");
                    for (String num : numberArray) {
                        numbers.add(Integer.parseInt(num));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeArrayListToFile(ArrayList<Double> tfidf) {
        try (FileWriter writer = new FileWriter("output.txt")) {
            for (int i = 0; i < tfidf.size(); i++) {
                Double tfIdf = tfidf.get(i);
                writer.write(String.format("%.5f", tfIdf));
                if (i < tfidf.size() - 1) {
                    writer.write(" ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
