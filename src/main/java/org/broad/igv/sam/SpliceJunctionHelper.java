/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2007-2015 Fred Hutchinson Cancer Research Center and Broad Institute
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


package org.broad.igv.sam;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.broad.igv.logging.*;
import org.broad.igv.feature.FeatureUtils;
import org.broad.igv.feature.SpliceJunctionFeature;
import org.broad.igv.feature.Strand;
import org.broad.igv.prefs.Constants;
import org.broad.igv.prefs.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class for computing splice junctions from alignments.
 * Junctions are filtered based on minimum flanking width on loading, so data
 * needs to be
 *
 * @author dhmay, jrobinso
 * @date Jul 3, 2011
 */
public class SpliceJunctionHelper {

    static Logger log = LogManager.getLogger(SpliceJunctionHelper.class);

    List<SpliceJunctionFeature> allSpliceJunctionFeatures = new ArrayList<SpliceJunctionFeature>();
    //  List<SpliceJunctionFeature> filteredSpliceJunctionFeatures = null;
    List<SpliceJunctionFeature> filteredCombinedFeatures = null;

    Table<Integer, Integer, SpliceJunctionFeature> posStartEndJunctionsMap = HashBasedTable.create();
    Table<Integer, Integer, SpliceJunctionFeature> negStartEndJunctionsMap = HashBasedTable.create();


    public SpliceJunctionHelper() {
    }

    public List<SpliceJunctionFeature> getFilteredJunctions(SpliceJunctionTrack.StrandOption strandOption, int minJunctionCoverage) {

        List<SpliceJunctionFeature> junctions;

        switch (strandOption) {
            case FORWARD:
                junctions = new ArrayList<>(posStartEndJunctionsMap.values());
                break;
            case REVERSE:
                junctions = new ArrayList<>(negStartEndJunctionsMap.values());
                break;
            case BOTH:
                junctions = new ArrayList<>(posStartEndJunctionsMap.values());
                junctions.addAll(negStartEndJunctionsMap.values());
                break;
            default:
                junctions = combineStrandJunctionsMaps();
        }

        List<SpliceJunctionFeature> unfiltered = junctions;
        List<SpliceJunctionFeature> filteredJunctions;

        if (minJunctionCoverage > 1) {
            List<SpliceJunctionFeature> coveredFeatures = new ArrayList<SpliceJunctionFeature>(unfiltered.size());
            for (SpliceJunctionFeature feature : unfiltered) {
                if (feature.getJunctionDepth() >= minJunctionCoverage) {
                    coveredFeatures.add(feature);
                }
            }
            filteredJunctions = coveredFeatures;
        } else {
            filteredJunctions = unfiltered;
        }

        FeatureUtils.sortFeatureList(filteredJunctions);

        return filteredJunctions;

    }

    public void addAlignment(Alignment alignment) {

        AlignmentBlock[] blocks = alignment.getAlignmentBlocks();
        if (blocks == null || blocks.length < 2) {
            return;
        }

        // Determine strand.  First check for explicit attribute.
        boolean isNegativeStrand;
        Object strandAttr = alignment.getAttribute("TS");
        if(strandAttr == null) {
            strandAttr = alignment.getAttribute("XS");   // Older convention
        }

        if (strandAttr != null) {
            isNegativeStrand = strandAttr.toString().charAt(0) == '-';
        } else {
            if (alignment.isPaired()) {
                isNegativeStrand = alignment.getFirstOfPairStrand() == Strand.NEGATIVE;
            } else {
                isNegativeStrand = alignment.isNegativeStrand(); // <= TODO -- this isn't correct for all libraries.
            }
        }
        Table<Integer, Integer, SpliceJunctionFeature> startEndJunctionsTableThisStrand =
                isNegativeStrand ? negStartEndJunctionsMap : posStartEndJunctionsMap;


        // For each gap marked "skip" (cigar N), create or add evidence to a splice junction
        List<Gap> gaps = alignment.getGaps();
        int minReadFlankingWidth = PreferencesManager.getPreferences(Constants.RNA).getAsInt(Constants.SAM_JUNCTION_MIN_FLANKING_WIDTH);
        if (gaps != null) {
            for (Gap gap : gaps) {

                if (gap instanceof SpliceGap) {

                    SpliceGap spliceGap = (SpliceGap) gap;
                    //only proceed if the flanking regions are both bigger than the minimum
                    if (minReadFlankingWidth == 0 ||
                            (spliceGap.getFlankingLeft() >= minReadFlankingWidth &&
                                    spliceGap.getFlankingRight() >= minReadFlankingWidth)) {

                        int junctionStart = spliceGap.getStart();
                        int junctionEnd = junctionStart + spliceGap.getnBases();
                        int flankingStart = junctionStart - spliceGap.getFlankingLeft();
                        int flankingEnd = junctionEnd + spliceGap.getFlankingRight();

                        SpliceJunctionFeature junction = startEndJunctionsTableThisStrand.get(junctionStart, junctionEnd);

                        if (junction == null) {
                            junction = new SpliceJunctionFeature(alignment.getChr(), junctionStart, junctionEnd,
                                    isNegativeStrand ? Strand.NEGATIVE : Strand.POSITIVE);
                            startEndJunctionsTableThisStrand.put(junctionStart, junctionEnd, junction);
                            allSpliceJunctionFeatures.add(junction);
                        }
                        junction.addRead(flankingStart, flankingEnd);
                    }
                }
            }
        }
    }


    /**
     * Combine junctions from both strands.  Used for Sashimi plot.
     * Note: Flanking depth arrays are not combined.
     */
    private List<SpliceJunctionFeature> combineStrandJunctionsMaps() {

        // Start with all + junctions
        Table<Integer, Integer, SpliceJunctionFeature> combinedMap = HashBasedTable.create();

        for (SpliceJunctionFeature posFeature : posStartEndJunctionsMap.values()) {
            final int junctionStart = posFeature.getJunctionStart();
            final int junctionEnd = posFeature.getJunctionEnd();

            SpliceJunctionFeature combinedFeature = new SpliceJunctionFeature(posFeature.getChr(), junctionStart, junctionEnd);
            combinedFeature.setJunctionDepth(posFeature.getJunctionDepth());
            combinedMap.put(junctionStart, junctionEnd, combinedFeature);
        }


        // Merge in - junctions
        for (SpliceJunctionFeature negFeature : negStartEndJunctionsMap.values()) {

            int junctionStart = negFeature.getJunctionStart();
            int junctionEnd = negFeature.getJunctionEnd();

            SpliceJunctionFeature junction = combinedMap.get(junctionStart, junctionEnd);

            if (junction == null) {
                // No existing (+) junction here, just add the (-) one\
                SpliceJunctionFeature combinedFeature = new SpliceJunctionFeature(negFeature.getChr(), junctionStart, junctionEnd);
                combinedFeature.setJunctionDepth(negFeature.getJunctionDepth());
                combinedMap.put(junctionStart, junctionEnd, negFeature);
            } else {
                int newJunctionDepth = junction.getJunctionDepth() + negFeature.getJunctionDepth();
                junction.setJunctionDepth(newJunctionDepth);
            }
        }

        return new ArrayList<>(combinedMap.values());
    }


}
