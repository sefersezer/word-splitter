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

import com.paritus.tools.FileTools;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Exceptions for splitting loaded from a file.
 */
class ExceptionSplits {

    private static final String COMMENT_CHAR = "#";
    private static final String DELIMITER_CHAR = "|";

    private final Map<String,List<String>> exceptionMap = new HashMap<String, List<String>>();

    ExceptionSplits(String filename) throws IOException {
        final InputStream is = AbstractWordSplitter.class.getResourceAsStream(filename);
        try {
            if (is == null) {
                throw new IOException("Cannot locate exception list in class path: " + filename);
            }
            final String exceptions = FileTools.loadFile(is, "UTF-8");
            final Scanner scanner = new Scanner(exceptions);
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith(COMMENT_CHAR)) {
                    final String[] parts = line.split("\\|");
                    final String completeWord = line.replace(DELIMITER_CHAR, "");
                    final List<String> list = new ArrayList<String>(Arrays.asList(parts));
                    exceptionMap.put(completeWord.toLowerCase(), list);
                }
            }
            scanner.close();
        } finally {
            if (is != null) is.close();
        }
    }

    List<String> getExceptionSplitOrNull(String word) {
        String lcWord = word.toLowerCase();
        List<String> result = exceptionMap.get(lcWord);
        if (result != null) {
            // The following code will only get executed if an exception split is encountered
            String check = join(result, "");
            if (lcWord.equals(check.toLowerCase())) {
                // The recombined, lowercased split-word is equal to the lowercase original word
                // Generate the pieces by splitting the original word with the same string lengths
                // as the splitted word. This will preserve the case of the original word
                result = splitEqually(result, word);
            }
        }
        return result;
    }

    protected List<String> splitEqually(List<String> splitted, String original) {
        List<String> list = new ArrayList<String>();
        Iterator<String> iter = splitted.iterator();
        int offset = 0;

        while (iter.hasNext()) {
            int length = iter.next().length();
            list.add(original.substring(offset, offset+length));
            offset += length;
        }
        return list;
    }

    protected String join(List<String> elements, String separator) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = elements.iterator();

        if (iter.hasNext()) {
            builder.append(iter.next());
            while (iter.hasNext()) {
                builder.append(separator).append(iter.next());
            }
        }

        return builder.toString();
    }

    void addSplit(String word, List<String> wordParts) {
        exceptionMap.put(word.toLowerCase(), wordParts);
    }
}
