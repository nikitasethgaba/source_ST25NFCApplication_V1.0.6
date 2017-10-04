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

import static org.junit.Assert.assertEquals;

import com.st.st25sdk.Helper;
import com.st.st25sdk.STException;

public class HelperFunctionTests {

    static public void run() throws STException {
        byte byteResult;
        int intResult;
        String stringResult;
        byte[] byteArrayResult;

        ////////////////////////////////////////////
        // Tests of convertHexStringToByte()
        byteResult = Helper.convertHexStringToByte("0");
        assertEquals((byte) 0x00, byteResult);

        byteResult = Helper.convertHexStringToByte("4");
        assertEquals((byte) 0x04, byteResult);

        byteResult = Helper.convertHexStringToByte("80");
        assertEquals((byte) 0x80, byteResult);

        byteResult = Helper.convertHexStringToByte("FF");
        assertEquals((byte) 0xFF, byteResult);

        ////////////////////////////////////////////
        // Tests of convertHexStringToInt()
        intResult = Helper.convertHexStringToInt("000");
        assertEquals(0x0, intResult);

        intResult = Helper.convertHexStringToInt("FFFF");
        assertEquals(0xFFFF, intResult);

        ////////////////////////////////////////////
        // Tests of convertStringToInt()
        intResult = Helper.convertStringToInt("123456");
        assertEquals(123456, intResult);

        ////////////////////////////////////////////
        // Tests of convertByteToHexString()
        stringResult = Helper.convertByteToHexString((byte) 0x0);
        assertEquals("00", stringResult);

        stringResult = Helper.convertByteToHexString((byte) 0x80);
        assertEquals("80", stringResult);

        stringResult = Helper.convertByteToHexString((byte) 0xFF);
        assertEquals("FF", stringResult);

        ////////////////////////////////////////////
        // Tests of convertIntTo2BytesHexaFormat()
        byteArrayResult = Helper.convertIntTo2BytesHexaFormat(0xA1F2);
        assertEquals((byte) 0xA1, byteArrayResult[0]);
        assertEquals((byte) 0xF2, byteArrayResult[1]);

        ////////////////////////////////////////////
        // Tests of convert2BytesHexaFormatToInt()
        intResult = Helper.convert2BytesHexaFormatToInt(new byte[]{0x07, 0x54});
        assertEquals(1876, intResult);

        intResult = Helper.convert2BytesHexaFormatToInt(null);
        assertEquals(0, intResult);

        intResult = Helper.convert2BytesHexaFormatToInt(new byte[]{0x00, 0x00});
        assertEquals(0, intResult);

        intResult = Helper.convert2BytesHexaFormatToInt(new byte[]{(byte) 0xFF, (byte) 0x28});
        assertEquals(65320, intResult);

        ////////////////////////////////////////////
        // Tests of convertIntToHexFormatString()
        stringResult = Helper.convertIntToHexFormatString(0xA1F2);
        assertEquals("A1F2", stringResult);

        ////////////////////////////////////////////
        // Tests of convertHexStringToByteArray
        String stringWithEvenNbrOfItems = "08FF5A380089";
        byteArrayResult = Helper.convertHexStringToByteArray(stringWithEvenNbrOfItems);
        assertEquals((byte) 0x08, byteArrayResult[0]);
        assertEquals((byte) 0xFF, byteArrayResult[1]);
        assertEquals((byte) 0x5A, byteArrayResult[2]);
        assertEquals((byte) 0x38, byteArrayResult[3]);
        assertEquals((byte) 0x00, byteArrayResult[4]);
        assertEquals((byte) 0x89, byteArrayResult[5]);

        String stringWithOddNbrOfItems = "07AF303CC3";
        byteArrayResult = Helper.convertHexStringToByteArray(stringWithOddNbrOfItems);
        assertEquals((byte) 0x07, byteArrayResult[0]);
        assertEquals((byte) 0xAF, byteArrayResult[1]);
        assertEquals((byte) 0x30, byteArrayResult[2]);
        assertEquals((byte) 0x3C, byteArrayResult[3]);
        assertEquals((byte) 0xC3, byteArrayResult[4]);
    }

}
