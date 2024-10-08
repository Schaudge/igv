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

package org.broad.igv.ui.color;

import org.junit.Test;

import java.awt.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author jrobinso
 *         Date: 11/28/12
 *         Time: 11:49 AM
 */
public class ColorUtilitiesTest {


    @Test
    public void testStringToColor() throws Exception {
        // Test parsing strings with quotes (common problem with Excel exports)
        String quotedString = "\"0,0,255,\"";
        Color b = ColorUtilities.stringToColor(quotedString);
        assertEquals(Color.blue, b);
    }

    @Test
    public void testHexColorString() throws Exception {
        Color grey = new Color(170,170, 170);
        Color c = ColorUtilities.stringToColor("#AAAAAA");
        assertEquals(grey, c);
    }

    @Test
    public void testRGBToColor() throws Exception {
        // Test parsing javascript style rgb string
        String rgbString = "rgb(0,0,255)";
        Color b = ColorUtilities.stringToColor(rgbString);
        assertEquals(Color.blue, b);
    }
}
