/**
 * Copyright 2012 Daniel Naber (www.danielnaber.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paritus;

import com.google.common.io.Resources;
import com.paritus.tools.FastObjectSaver;
import com.paritus.tools.FileTools;
import zemberek.core.io.SimpleTextReader;
import zemberek.morphology.apps.TurkishMorphParser;
import zemberek.tokenizer.ZemberekLexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Split Turkish compound words. Based on an embedded dictionary, or on an
 * external plain text dictionary.
 */
public class TurkishWordSplitter extends AbstractWordSplitter {

    private static final String SERIALIZED_DICT = "/com/paritus/wordsTurkish.ser";   // dict inside the JAR
    private static final String EXCEPTION_DICT = "/com/paritus/exceptionTurkish.txt";   // dict inside the JAR
    private static final String MASTER_DICT="tr/master-dictionary.dict";
    private static final String NON_TDK_DICT="tr/non-tdk.dict";
    private static final String PROPER_DICT="tr/proper.dict";
    private static final String SECONDARY_DICT="tr/secondary-dictionary.dict";

    private TurkishMorphParser turkishMorphParser;

    private static final Set<String> IGNORED_PARTS = new HashSet<String>();
    /*
    static {
        IGNORED_PARTS.add(".");
    }
    */
    private static final Set<String> ADDED_PARTS = new HashSet<String>();

    static {
        ADDED_PARTS.add("istanbul");
        ADDED_PARTS.add("sok");
        ADDED_PARTS.add("dilligil");
        ADDED_PARTS.add("avni");
        ADDED_PARTS.add("izmir");
    }


    public TurkishWordSplitter() throws IOException {
        super();
        turkishMorphParser = TurkishMorphParser.createWithDefaults();
        init();
    }

    public TurkishWordSplitter( InputStream plainTextDict) throws IOException {
        super(plainTextDict);
        init();
    }

    public TurkishWordSplitter(File plainTextDict) throws IOException {
        super(plainTextDict);
        init();
    }

    private void init() throws IOException {
        setExceptionFile(EXCEPTION_DICT);
    }

    @Override
    protected Set<String> getWordList(File file) throws IOException {
        final FileInputStream fis = new FileInputStream(file);
        try {
            return getWordList(fis);
        } finally {
            fis.close();
        }
    }

    @Override
    protected Set<String> getWordList(InputStream stream) throws IOException {
        return FileTools.loadFileToSet(stream, "utf-8");
    }

    @Override
    protected Set<String> getWordList() throws IOException {
        if (words == null) {
            Locale lcl = new Locale("tr-TR");
            words = new HashSet<String>();
            List<String> master = SimpleTextReader.trimmingUTF8Reader(new File(Resources.getResource(MASTER_DICT).getFile())).asStringList();
            for(String str: master){
                if(!str.startsWith("#") && str.length()!=0)
                    words.add(str.toLowerCase(lcl).split(" ")[0]);
            }
            List<String> nontdk = SimpleTextReader.trimmingUTF8Reader(new File(Resources.getResource(NON_TDK_DICT).getFile())).asStringList();
            for(String str: nontdk){
                if(!str.startsWith("#") && str.length()!=0)
                    words.add(str.toLowerCase(lcl).split(" ")[0]);
            }
            List<String> proper = SimpleTextReader.trimmingUTF8Reader(new File(Resources.getResource(PROPER_DICT).getFile())).asStringList();
            for(String str: proper){
                if(!str.startsWith("#") && str.length()!=0)
                    words.add(str.toLowerCase(lcl).split(" ")[0]);
            }
            List<String> secondary_dictionary = SimpleTextReader.trimmingUTF8Reader(new File(Resources.getResource(SECONDARY_DICT).getFile())).asStringList();
            for(String str: secondary_dictionary){
                if(!str.startsWith("#") && str.length()!=0)
                    words.add(str.toLowerCase(lcl).split(" ")[0]);
            }
            words.addAll((HashSet<String>)FastObjectSaver.load(SERIALIZED_DICT));
        }
        words.addAll(ADDED_PARTS);
        words.removeAll(IGNORED_PARTS);
        return words;
    }

    @Override
    protected int getDefaultMinimumWordLength() {
        return 4;
    }



    @Override
    public List<String> splitWord(String word) {
        if (word == null) {
            return Collections.emptyList();
        }
        final String trimmedWord = word.trim();
        final List<String> exceptionSplit = exceptionSplits.getExceptionSplitOrNull(trimmedWord);
        if (exceptionSplit != null) {
            return exceptionSplit;
        }
        final List<String> parts = split(trimmedWord.toLowerCase());
        if (parts == null) {
            return Collections.singletonList(trimmedWord);
        }
        return getExceptionSplitOrNull(parts);
    }


    private List<String> split(String lcWord) {
        List<String> parts;
        if (isSimpleWord(lcWord)) {
            parts = Collections.singletonList(lcWord);
        } else {
            parts = splitFromRight(lcWord);
            if (parts == null ) {
                parts = splitFromRight(lcWord);
            }
        }
        return parts;
    }

    private List<String> splitFromRight(String word) {
        List<String> parts = null;
        for (int i = word.length() - minimumWordLength; i >= minimumWordLength; i--) {
            final String leftPart = word.substring(0, i);
            final String rightPart = word.substring(i);
            //System.out.println(word  + " -> " + leftPart + " + " + rightPart);
           /*
            if (!strictMode) {
                final List<String> exceptionSplit = getExceptionSplitOrNull(rightPart, leftPart);
                if (exceptionSplit != null) {
                    return exceptionSplit;
                }
            }
            */
            if (isSimpleWord(rightPart)) {
                final List<String> leftPartParts = split(leftPart);
                final boolean isLeftPartAWord = leftPartParts != null;
                if (isLeftPartAWord) {
                    parts = new ArrayList<String>(leftPartParts);
                    parts.add(rightPart);
                } else if (!strictMode) {
                    parts = Arrays.asList(leftPart, rightPart);
                }
            }
        }
        return parts;
    }

    private List<String> getExceptionSplitOrNull(List<String> parts) {
        List<String> newparts = new ArrayList<String>();
        for(String part : parts){
            final List<String> exceptionSplit = exceptionSplits.getExceptionSplitOrNull(part);
            if (exceptionSplit != null) {
                newparts.addAll(exceptionSplit);
            }else
                newparts.add(part);
        }
        return newparts;
    }


    private boolean isSimpleWord(String part) {

        if(part.length() >= minimumWordLength){
            if(words.contains(part)) return true;
            else if(turkishMorphParser.parse(part).size()>0) return true;
            else return false;
        }else
            return false;
    }


/*
    private boolean isSimpleWord(String part) {
        return part.length() >= minimumWordLength && turkishMorphParser.parse(part).size()>0;
    }
   */
    /*
    private boolean isSimpleWord(String part) {
        return part.length() >= minimumWordLength && words.contains(part);
    }
    */
}
