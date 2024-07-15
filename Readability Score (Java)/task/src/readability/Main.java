package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("path: " + args[0]);

        String inputText = new String(Files.readAllBytes(Paths.get(args[0])));

        System.out.println("The text is:");
        System.out.println(inputText);

        String[] sentencesList = inputText.split("[?.!]");

        for (int i = 0; i < sentencesList.length; i++) {
            sentencesList[i] = sentencesList[i].trim();
        }

        int sentences = sentencesList.length;
        System.out.println("sentences: " + sentences);

        List<String> wordList = new ArrayList<>();

        for (int i = 0; i < sentencesList.length; i++) {
            String sentenceRow = sentencesList[i];
            wordList.addAll(List.of(sentenceRow.split("\s")));
        }

        int words = wordList.size();
        System.out.println("Words: " + words);

        int syllables = countSyllables(inputText);
        int polySyllables = countPolySyllables(inputText);

        System.out.println("Syllables: " + syllables);
        System.out.println("PolySyllables: " + polySyllables);

        int characters = 0;
        String[] characterList = inputText.split("\s");

        for (int i = 0; i < characterList.length; i++) {
            characters += characterList[i].length();
        }

        System.out.println("Characters: " + characters);

        Map<Integer, String> scores = new HashMap<>();
        scores.put(1, "5-6");
        scores.put(2, "6-7");
        scores.put(3, "7-8");
        scores.put(4, "8-9");
        scores.put(5, "9-10");
        scores.put(6, "10-11");
        scores.put(7, "11-12");
        scores.put(8, "12-13");
        scores.put(9, "13-14");
        scores.put(10, "14-15");
        scores.put(11, "15-16");
        scores.put(12, "16-17");
        scores.put(13, "17-18");
        scores.put(14, "18-22");

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");
        String input = sc.nextLine();

        switch (input) {
            case "ARI":
                ARI(characters, words, sentences, scores);
                break;
            case "FK":
                FK(words, sentences, syllables, scores);
                break;
            case "SMOG":
                SMOG(polySyllables, sentences, scores);
                break;
            case "CL":
                CL(inputText, scores);
                break;
            case "all":
                ARI(characters, words, sentences, scores);
                FK(words, sentences, syllables, scores);
                SMOG(polySyllables, sentences, scores);
                CL(inputText, scores);
                break;
            default:
                break;
        }
    }

    private static void SMOG(int polySyllables, int sentences, Map<Integer, String> scores) {
        double score = 1.043 * Math.sqrt(polySyllables * (30 / (double) sentences)) + 3.1291;

        DecimalFormat df = new DecimalFormat("#.00");

        System.out.println("Simple Measure of Gobbledygook: " + df.format(score) + " (" + scores.get((int) Math.ceil(score)) + " year-olds).");
    }

    private static void FK(int words, int sentences, int syllables, Map<Integer, String> scores) {

        double score = 0.39 * ((double) words / sentences) + 11.8 * ((double) syllables / words) - 15.59;

        DecimalFormat df = new DecimalFormat("#.00");

        System.out.println("Flesch–Kincaid readability tests: " + df.format(score) + " (" + scores.get((int) Math.ceil(score)) + " year-olds).");
    }

    private static void ARI(double characters, int words, int sentences, Map<Integer, String> scores) {
        double score = 4.71 * (characters / words) + 0.5 * ((double) words / sentences) - 21.43;

        DecimalFormat df = new DecimalFormat("#.00");

        System.out.println("Automated Readability Index: " + df.format(score) + " (" + scores.get((int) Math.ceil(score)) + " year-olds).");
    }

    public static int countSyllables(String text) {
        String[] words = text.split("\\s+");
        int totalSyllables = 0;
        for (String word : words) {
            totalSyllables += countSyllablesInWord(word);

        }

        return totalSyllables;
    }

    public static int countPolySyllables(String text) {
        String[] words = text.split("\\s+");
        int polySyllables = 0;

        for (String word : words) {
            int syllables = countSyllablesInWord(word);

            if (syllables > 2) {
                polySyllables++;
            }
        }

        return polySyllables;
    }

    public static int countSyllablesInWord(String word) {
        word = word.toLowerCase();
        int syllableCount = 0;
        boolean isPrevVowel = false;
        String vowels = "aeiouy";

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (vowels.indexOf(c) != -1) {
                // Check if current character is a vowel
                if (!isPrevVowel) {
                    syllableCount++;
                    isPrevVowel = true;
                }
            } else {
                isPrevVowel = false;
            }
        }

        // Remove 'e' at the end of a word unless the word is only one letter or ends with "le"
        if (word.endsWith("e") && !word.endsWith("le") && word.length() > 1) {
            syllableCount--;
        }

        // Ensure each word has at least one syllable
        if (syllableCount == 0) {
            syllableCount = 1;
        }

        return syllableCount;
    }

    public static void CL(String text, Map<Integer, String> scores) {

        int letterCount = countLetters(text);
        int wordCount = countWords(text);
        int sentenceCount = countSentences(text);

        double L = (double) letterCount / wordCount * 100;
        double S = (double) sentenceCount / wordCount * 100;

        double score = 0.0588 * L - 0.296 * S - 15.8;

        DecimalFormat df = new DecimalFormat("#.00");

        System.out.println("Coleman-Liau index: " + df.format(score) + " (" + scores.get((int) Math.ceil(score)) + " year-olds).");

    }

    public static int countLetters(String text) {
        int letters = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                letters++;
            }
        }
        return letters;
    }

    public static int countWords(String text) {
        String[] words = text.split("\\s+");
        return words.length;
    }

    public static int countSentences(String text) {
        String[] sentences = text.split("[.!?]");
        return sentences.length;
    }
}
