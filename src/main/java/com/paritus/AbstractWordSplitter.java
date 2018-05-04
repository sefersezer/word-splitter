
package com.paritus;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public abstract class AbstractWordSplitter {

    protected abstract Set<String> getWordList(InputStream stream) throws IOException;
    protected abstract Set<String> getWordList(File file) throws IOException;
    protected abstract Set<String> getWordList() throws IOException;
    protected abstract int getDefaultMinimumWordLength();
    public abstract List<String> splitWord(String word);
    protected Set<String> words = null;

    protected ExceptionSplits exceptionSplits;
    protected boolean strictMode = true;
    protected int minimumWordLength = getDefaultMinimumWordLength();

    /**
     * Create a word splitter that uses the embedded dictionary.
     *
     * @throws java.io.IOException
     */
    public AbstractWordSplitter() throws IOException {
        words = getWordList();
    }

    /**
      * @param  plainTextDict a stream of a text file with one word per line, to be used instead of the embedded dictionary,
     *                       must be in UTF-8 format
     * @throws java.io.IOException
     */
    public AbstractWordSplitter( InputStream plainTextDict) throws IOException {
        words = getWordList(plainTextDict);
    }

    /**
     * @param  plainTextDict a stream of a text file with one word per line, to be used instead of the embedded dictionary,
     *                       must be in UTF-8 format
     * @throws java.io.IOException
     */
    public AbstractWordSplitter( File plainTextDict) throws IOException {
        words = getWordList(plainTextDict);
    }

    public void setMinimumWordLength(int minimumWordLength) {
        this.minimumWordLength = minimumWordLength;
    }

    /**
     * @param filename UTF-8 encoded file with exceptions in the classpath, one exception per line, using pipe as delimiter.
     *   Example: <tt>Pilot|sendung</tt>
     * @throws java.io.IOException
     */
    public void setExceptionFile(String filename) throws IOException {
        exceptionSplits = new ExceptionSplits(filename);
    }

    /**
     * @param completeWord the word for which an exception is to be defined (will be considered case-insensitive)
     * @param wordParts the parts in which the word is to be split (use a list with a single element if the word should not be split)
     */
    public void addException(String completeWord, List<String> wordParts) {
        exceptionSplits.addSplit(completeWord.toLowerCase(), wordParts);
    }

    /**
     * When set to true, words will only be split if all parts are words.
     * Otherwise the splitting result might contain parts that are not words.
     * The minimum length of word parts is correctly taken into account only if this is set to true.
     */
    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

}
