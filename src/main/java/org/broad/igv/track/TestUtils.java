/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2007-2015 Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.broad.igv.track;

import org.broad.igv.feature.Chromosome;
import org.broad.igv.feature.genome.ChromAliasDefaults;
import org.broad.igv.feature.genome.Genome;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jrobinso
 */
public class TestUtils {

    public static void main(String[] args) throws IOException {
        createFeatureTestFile();
    }

    public static void createFeatureTestFile() throws IOException {
        String chr = "chr1";
        long chrLength = 560000;
        long featureLength = 1;

        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("data/featureTest.txt")));

        long featureStart = 0;
        long featureEnd = featureStart + featureLength;
        int n = 0;
        while (featureEnd <= chrLength) {
            pw.println(chr + "\t" + featureStart + "\t" + featureEnd + "\t" + ("feature_" + n));
            featureStart += 2 * featureLength;
            featureEnd = featureStart + featureLength;
        }

        pw.close();
    }

}
