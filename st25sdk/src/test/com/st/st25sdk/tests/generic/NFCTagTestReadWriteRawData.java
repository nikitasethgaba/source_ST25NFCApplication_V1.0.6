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

import com.st.st25sdk.Helper;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static com.st.st25sdk.Helper.printHexByteArray;
import static com.st.st25sdk.STException.STExceptionCode.INVALID_DATA;

public class NFCTagTestReadWriteRawData {
    /**
     * Write some raw data in a tag by using writeRawData() function
     *
     * @param srcStream : ByteArrayInputStream containing the source data
     * @param lengthInBytes: Length of the data to write into the tag
     * @param destTag : Destination tag
     */
    static public void writeDataByRawDataFunction(ByteArrayInputStream srcStream, int lengthInBytes, NFCTag destTag, int destByteAddress) throws STException {

        // Get the data that we want to write into the tag
        byte[] data = Helper.readByteArrayInputStream(srcStream, lengthInBytes);

        if(NFCTagTests.debug) {
            printHexByteArray("writeDataByRawDataFunction", data);
        }

        // Write the data into the tag
        destTag.writeBytes(destByteAddress, data);
    }

    /**
     * Read data from the tag thanks to readRawData() function
     * @param srcTag
     * @param offsetInBytes
     * @param lengthInBytes
     * @param dstOutputStream
     * @throws STException
     */
    static public void readDataByRawDataFunction(NFCTag srcTag, int offsetInBytes, int lengthInBytes, ByteArrayOutputStream dstOutputStream) throws STException {
        byte[] dataRead = srcTag.readBytes(offsetInBytes, lengthInBytes);

        // Check that the expected number of bytes was read
        if(dataRead == null || dataRead.length != lengthInBytes){
            throw new STException(INVALID_DATA);
        }

        dstOutputStream.write(dataRead, 0, dataRead.length);
    }
}
