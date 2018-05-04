/**
 * Copyright 2004-2007 Sven Abels
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
package com.paritus.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper methods.
 *
 * @author Sven Abels
 * @author Daniel Naber
 */
public class FileTools
{

    private FileTools() {
        // class has static methods only, no public constructor
    }

    /**
     * Load a file and return each line, lowercased, as an entry in a HashSet.
     */
    public static Set<String> loadFileToSet(InputStream is, String charset) throws IOException {
        InputStreamReader isr = null;
        BufferedReader br = null;
        final HashSet<String> words = new HashSet<String>();
        try {
            isr = new InputStreamReader(is, charset);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
        } finally {
            if (br != null) br.close();
            if (isr != null) isr.close();
        }
        return words;
    }

    public static String loadFile(InputStream inputStream, String charset) throws IOException {
        InputStreamReader isr = null;
        BufferedReader br = null;
        final StringBuilder sb = new StringBuilder();
        try {
            isr = new InputStreamReader(inputStream, charset);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } finally {
            if (br != null) br.close();
            if (isr != null) isr.close();
        }
        return sb.toString();
    }

}
