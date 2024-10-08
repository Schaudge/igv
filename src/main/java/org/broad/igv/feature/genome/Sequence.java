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

package org.broad.igv.feature.genome;

import org.broad.igv.feature.Chromosome;
import org.broad.igv.ui.panel.ReferenceFrame;

import java.util.List;

/**
 * @author jrobinso
 * @date 8/8/11
 *
 * Represents a reference sequence.
 */
public interface Sequence {

    /**
     * Return the sequence for the given range.  If sequence named "seq" does not exist returns null.
     * @param seq
     * @param start
     * @param end
     * @return  The sequence in bytes, or null if no sequence exists
     */
    byte[] getSequence(String seq, int start, int end) ;

    byte getBase(String seq, int position);

    List<String> getChromosomeNames();

    /**
     * Return the given sequence length.  If no sequence exists with name "seq" return -1.
     *
     * @param seq
     * @return
     */
    int getChromosomeLength(String seq);

    List<Chromosome> getChromosomes();

    default boolean hasChromosomes() {
        return true;
    }

}
