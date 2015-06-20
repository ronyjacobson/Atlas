package il.ac.tau.cs.databases.atlas.utils;

import com.sun.javafx.binding.StringFormatter;

import java.util.Collection;

public class StringUtils {
    public static String concatWithCommas(Collection<String> words) {
        StringBuilder wordList = new StringBuilder();
        for (String word : words) {
            wordList.append(word + ",");
        }
        return new String(wordList.deleteCharAt(wordList.length() - 1));
    }

    public static String concatWithCommas(String str, int count) {
        StringBuilder wordList = new StringBuilder();
        for (int i = 0; i < count; i++) {
            wordList.append(str + ",");
        }
        return new String(wordList.deleteCharAt(wordList.length() - 1));
    }

    public static String concatValuesWithCommas(Collection<String> words) {
        StringBuilder wordList = new StringBuilder();
        for (String word : words) {
            wordList.append(word + "=VALUES(" + word + "),");
        }
        return new String(wordList.deleteCharAt(wordList.length() - 1));
    }
}
