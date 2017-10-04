/*
 * @author STMicroelectronics MMY Application team
 *
 ******************************************************************************
 * @attention
 *
 * <h2><center>&copy; COPYRIGHT 2017 STMicroelectronics</center></h2>
 *
 * Licensed under ST MIX_MYLIBERTY SOFTWARE LICENSE AGREEMENT (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *        http://www.st.com/Mix_MyLiberty
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied,
 * AND SPECIFICALLY DISCLAIMING THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************
 */

package com.st.st25sdk.tests.generic;

import static com.st.st25sdk.Helper.printHexByteArray;
import static com.st.st25sdk.STException.STExceptionCode.INVALID_DATA;
import static org.junit.Assert.assertEquals;

import java.util.Random;

import com.st.st25sdk.Helper;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.type4a.Type4Tag;
import com.st.st25sdk.type5.LRiTag;
import com.st.st25sdk.type5.Type5Tag;


public class NFCTagUtils {
    static String mLastTestName;

    /**
     * Fill the first 32 bytes of the tag with random data in order to be sure
     * that there is no valid NDEF.
     * @param tag
     */
    static public void eraseBeginningOfTag(NFCTag tag) throws STException {
        byte[] randomData = new byte[32];
        new Random().nextBytes(randomData);
        tag.writeBytes(0, randomData);

        // Flush the cache otherwise the tag thinks that it still has a valid CC File
        invalidateCache(tag);
    }

    /**
     * Allocates the specified number of random data bytes
     * @param sizeInBytes
     * @return
     * @throws STException
     */
    static public byte[] allocateRandomData(int sizeInBytes) throws STException {
        byte[] randomData;

        STLog.i("allocateRandomData");

        // Allocate memory
        randomData = new byte[sizeInBytes];

        // And fill it with random numbers
        new Random().nextBytes(randomData);

        if (NFCTagTests.debug) {
            printHexByteArray("randomData", randomData);
        }

        return randomData;
    }

    /**
     *
     * @param tag
     */
    static public void invalidateCache(NFCTag tag) {

        STLog.i("Flush the cache");

        // Case of a Type5 tag
        try {
            Type5Tag type5Tag = (Type5Tag) tag;
            type5Tag.invalidateCache();

        } catch (ClassCastException e) {
            // This is not a Type5 tag
        }

        // Case of a Type4 tag
        try {
            Type4Tag Type4Tag = (Type4Tag) tag;
            Type4Tag.invalidateCache();

        } catch (ClassCastException e) {
            // This is not a Type4 tag
        }
    }

    static public void printTestName(String testName) {

        // Some tests use heritage. They do some pre-requisite initializations and call the parent method.
        // In such case, the test name is displayed twice.
        // Here we use a simple trick to display the test name only if it has changed
        if(!testName.equals(mLastTestName)) {
            // This is a new test
            mLastTestName = testName;

            STLog.i(" ");
            STLog.i(" ");
            STLog.i("*** Starting test: " + testName);
        }
    }

    static public void checkType5CcFileContent(Type5Tag type5Tag) throws STException {
        byte[] ccFileData = type5Tag.readCCFile();

        int ccFileLength = type5Tag.getCCFileLength();
        if(ccFileLength == 4) {
            assertEquals((byte) 0xE1, ccFileData[0]);
            assertEquals((byte) 0x40, ccFileData[1]);
            // Byte 2 depends of tag size so we don't test it
            assertEquals((byte) 0x05, ccFileData[3]);

        } else if(ccFileLength == 8) {
            assertEquals((byte) 0xE2, ccFileData[0]);
            assertEquals((byte) 0x40, ccFileData[1]);
            assertEquals((byte) 0x00, ccFileData[2]);
            assertEquals((byte) 0x05, ccFileData[3]);
            // TODO: Add test of other Bytes?

        } else {
            throw new STException(INVALID_DATA);
        }
    }

    /**
     * This function is used to facilitate the creation of a byte array containing the data
     * expected during a test. These data can be seen as a "reference" that we expect to get
     * every times we run this test. By this way, we have a "byte accurate" test.
     *
     * For example, a byte array containing 0x00, 0x01, 0x02 will be converted into a String
     * containing: "byte[] expectedData = new byte[] {(byte) 0x00, (byte) 0x01, (byte) 0x02};"
     *
     * @param data
     */
    static public String generateByteArray(byte[] data) {
        String txt = "byte[] expectedData = new byte[] {";

        for(int i=0; i<data.length; i++) {

            txt += "(byte) 0x" + Helper.convertByteToHexString(data[i]);

            // Add a ',' excepted for the last byte of the array
            if(i!=data.length-1) {
                txt += ", ";
            }

            // Add a carriage return every 8 bytes
            if(i%8==7) {
                txt += "\n";
            }

        }

        txt += "};";

        return txt;
    }

    /**
     * Indicates if this tag is looping back to the beginning of the memory when reading
     * beyond the last byte of memory.
     * This is the case of LRi tags.
     *
     * @param tag
     * @return
     */
    static public boolean isReadLooping(NFCTag tag) {
        boolean isReadLooping = false;

        if (tag instanceof LRiTag) {
            isReadLooping = true;
        }

        return isReadLooping;
    }

}
