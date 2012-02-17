/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.BitArray;

import java.util.Hashtable;
import java.util.Vector;

/**
 * <p>A reader that can read all available UPC/EAN formats. If a caller wants to try to
 * read all such formats, it is most efficient to use this implementation rather than invoke
 * individual readers.</p>
 *
 * @author Sean Owen
 */
public final class MultiFormatUPCEANReader extends OneDReader {

  private final Vector readers;

  public MultiFormatUPCEANReader(Hashtable hints) {
    Vector possibleFormats = hints == null ? null :
        (Vector) hints.get(DecodeHintType.POSSIBLE_FORMATS);
    readers = new Vector();
    if (possibleFormats != null) {
      if (possibleFormats.contains(BarcodeFormat.EAN_13)) {
        readers.addElement(new EAN13Reader());
      }
    }
    if (readers.isEmpty()) {
      readers.addElement(new EAN13Reader());
    }
  }

  @Override
public Result decodeRow(int rowNumber, BitArray row, Hashtable hints) throws NotFoundException {
    // Compute this location once and reuse it on multiple implementations
    int[] startGuardPattern = UPCEANReader.findStartGuardPattern(row);
    int size = readers.size();
    for (int i = 0; i < size; i++) {
      UPCEANReader reader = (UPCEANReader) readers.elementAt(i);
      Result result;
      try {
        result = reader.decodeRow(rowNumber, row, startGuardPattern, hints);
      } catch (ReaderException re) {
        continue;
      }
      
      return result;
    }

    throw NotFoundException.getNotFoundInstance();
  }

  @Override
public void reset() {
    int size = readers.size();
    for (int i = 0; i < size; i++) {
      Reader reader = (Reader) readers.elementAt(i);
      reader.reset();
    }
  }

}
