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

package com.st.st25sdk.tests.type5.st25dv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STException.STExceptionCode;
import com.st.st25sdk.STLog;
import com.st.st25sdk.type5.ST25DVTag;


public class ST25DVTestMultiArea {

    static int mNbrOfAreas;
    static int mArea1SizeInBytes, mArea2SizeInBytes, mArea3SizeInBytes, mArea4SizeInBytes;
    static int mArea1OffsetInBytes, mArea2OffsetInBytes, mArea3OffsetInBytes, mArea4OffsetInBytes;

    static final int INVALID_AREA = -1;

    static public void run(ST25DVTag st25DVTag) throws STException {
        int endOfArea1, endOfArea2, endOfArea3;
        int maxEndOfAreaValue = st25DVTag.getMaxEndOfAreaValue() & 0xFF;
        int tagMemSizeInBytes = st25DVTag.getMemSizeInBytes();

        // ST25DV should have a maximum of 4 areas
        assertEquals(4, st25DVTag.getMaxNumberOfAreas());

        //////////////////////////////////////////////////////////////////
        STLog.i("Test with 1 area");

        endOfArea1 = maxEndOfAreaValue;
        endOfArea2 = maxEndOfAreaValue;
        endOfArea3 = maxEndOfAreaValue;
        st25DVTag.setAreaEndValues((byte) endOfArea1, (byte) endOfArea2, (byte) endOfArea3);

        getAreasInfo(st25DVTag);
        assertEquals(1, mNbrOfAreas);

        assertEquals(tagMemSizeInBytes, mArea1SizeInBytes);
        assertEquals(0, mArea2SizeInBytes);
        assertEquals(0, mArea3SizeInBytes);
        assertEquals(0, mArea4SizeInBytes);

        assertEquals(0, mArea1OffsetInBytes);
        assertEquals(INVALID_AREA, mArea2OffsetInBytes);
        assertEquals(INVALID_AREA, mArea3OffsetInBytes);
        assertEquals(INVALID_AREA, mArea4OffsetInBytes);

        // Get area number from the last block available in the area
        assertEquals(1, st25DVTag.getAreaFromBlockAddress(8 * endOfArea1 + 7));

        //////////////////////////////////////////////////////////////////
        STLog.i("Test with 2 areas");

        endOfArea1 = maxEndOfAreaValue / 2;
        endOfArea2 = maxEndOfAreaValue;
        endOfArea3 = maxEndOfAreaValue;
        st25DVTag.setAreaEndValues((byte) endOfArea1, (byte) endOfArea2, (byte) endOfArea3);

        getAreasInfo(st25DVTag);
        assertEquals(2, mNbrOfAreas);

        assertEquals(tagMemSizeInBytes / 2, mArea1SizeInBytes);
        assertEquals(tagMemSizeInBytes / 2, mArea2SizeInBytes);
        assertEquals(0, mArea3SizeInBytes);
        assertEquals(0, mArea4SizeInBytes);

        assertEquals(0, mArea1OffsetInBytes);
        assertEquals(tagMemSizeInBytes / 2, mArea2OffsetInBytes);
        assertEquals(INVALID_AREA, mArea3OffsetInBytes);
        assertEquals(INVALID_AREA, mArea4OffsetInBytes);

        // Get area number from the last block available in the area
        assertEquals(1, st25DVTag.getAreaFromBlockAddress(8 * endOfArea1 + 7));
        assertEquals(2, st25DVTag.getAreaFromBlockAddress(8 * endOfArea2 + 7));

        //////////////////////////////////////////////////////////////////
        STLog.i("Test with 3 areas");

        endOfArea1 = 1 * maxEndOfAreaValue / 4;
        endOfArea2 = 2 * maxEndOfAreaValue / 4;
        endOfArea3 = maxEndOfAreaValue;
        st25DVTag.setAreaEndValues((byte) endOfArea1, (byte) endOfArea2, (byte) endOfArea3);

        getAreasInfo(st25DVTag);
        assertEquals(3, mNbrOfAreas);

        assertEquals(tagMemSizeInBytes / 4, mArea1SizeInBytes);
        assertEquals(tagMemSizeInBytes / 4, mArea2SizeInBytes);
        assertEquals(tagMemSizeInBytes / 2, mArea3SizeInBytes);
        assertEquals(0, mArea4SizeInBytes);

        assertEquals(0, mArea1OffsetInBytes);
        assertEquals(tagMemSizeInBytes / 4, mArea2OffsetInBytes);
        assertEquals(tagMemSizeInBytes / 2, mArea3OffsetInBytes);
        assertEquals(INVALID_AREA, mArea4OffsetInBytes);

        // Get area number from the last block available in the area
        assertEquals(1, st25DVTag.getAreaFromBlockAddress(8 * endOfArea1 + 7));
        assertEquals(2, st25DVTag.getAreaFromBlockAddress(8 * endOfArea2 + 7));
        assertEquals(3, st25DVTag.getAreaFromBlockAddress(8 * endOfArea3 + 7));

        //////////////////////////////////////////////////////////////////
        STLog.i("Test with 4 areas");

        endOfArea1 = 1 * maxEndOfAreaValue / 4;
        endOfArea2 = 2 * maxEndOfAreaValue / 4;
        endOfArea3 = 3 * maxEndOfAreaValue / 4;
        st25DVTag.setAreaEndValues((byte) endOfArea1, (byte) endOfArea2, (byte) endOfArea3);

        getAreasInfo(st25DVTag);
        assertEquals(4, mNbrOfAreas);

        assertEquals(tagMemSizeInBytes / 4, mArea1SizeInBytes);
        assertEquals(tagMemSizeInBytes / 4, mArea2SizeInBytes);
        assertEquals(tagMemSizeInBytes / 4, mArea3SizeInBytes);
        assertEquals(tagMemSizeInBytes / 4, mArea4SizeInBytes);

        assertEquals(0, mArea1OffsetInBytes);
        assertEquals(1 * tagMemSizeInBytes / 4, mArea2OffsetInBytes);
        assertEquals(2 * tagMemSizeInBytes / 4, mArea3OffsetInBytes);
        assertEquals(3 * tagMemSizeInBytes / 4, mArea4OffsetInBytes);

        // Get area number from the last block available in the area
        assertEquals(1, st25DVTag.getAreaFromBlockAddress(8 * endOfArea1 + 7));
        assertEquals(2, st25DVTag.getAreaFromBlockAddress(8 * endOfArea2 + 7));
        assertEquals(3, st25DVTag.getAreaFromBlockAddress(8 * endOfArea3 + 7));
        assertEquals(4, st25DVTag.getAreaFromBlockAddress(8 * endOfArea3 + 7 + 2 * st25DVTag.getBlockSizeInBytes()));

        //////////////////////////////////////////////////////////////////
        STLog.i("Test with wrong parameters");

        try {
            st25DVTag.getAreaOffsetInBlocks(0);
            fail("Expecting exception for area number 0 (areas are numbered 1 to 4)");
        } catch (STException e) {
            assertEquals(STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            st25DVTag.getAreaFromBlockAddress(-1);
            fail("Expecting exception for negative block address");
        } catch (STException e) {
            assertEquals(STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            st25DVTag.getRegisterEndArea(0);
            fail("Expecting exception for area number 0");
        } catch (STException e) {
            assertEquals(STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            st25DVTag.setNumberOfAreas(2);
            fail("Expecting exception for call to unimplemented function");
        } catch (STException e) {
            assertEquals(STExceptionCode.NOT_IMPLEMENTED, e.getError());
        }

        try {
            st25DVTag.getAreaSizeInBytes(-1);
            fail("Expecting exception for negative area number");
        } catch (STException e) {
            assertEquals(STExceptionCode.BAD_PARAMETER, e.getError());
        }


        ////////////////// Try to write endArea2 < endArea1
        endOfArea1 = maxEndOfAreaValue / 2;
        endOfArea2 = maxEndOfAreaValue / 2 - 1;
        endOfArea3 = maxEndOfAreaValue;

        resetAreas(st25DVTag, maxEndOfAreaValue);
        st25DVTag.getRegisterEndArea1().setRegisterValue(endOfArea1);
        try {
            st25DVTag.getRegisterEndArea2().setRegisterValue(endOfArea2);
            fail("Expecting exception");
        } catch (STException e) {
            if (e.getError() == STExceptionCode.CMD_FAILED) {
                byte[] expectedError = new byte[]{0x01, 0x0F};
                assertArrayEquals("Wrong ISO response", expectedError, e.getErrorData());
            } else {
                fail("Unexpected exception");
            }
        }

        resetAreas(st25DVTag, maxEndOfAreaValue);
        try {
            st25DVTag.setAreaEndValues((byte) endOfArea1, (byte) endOfArea2, (byte) endOfArea3);
            fail("Expecting exception");
        } catch (STException e) {
            assertEquals(STExceptionCode.BAD_PARAMETER, e.getError());
        }


        ////////////////// Try to write endArea2 = endArea1 and different from maxEndOfAreaValue
        endOfArea1 = maxEndOfAreaValue / 4;
        endOfArea2 = maxEndOfAreaValue / 4;
        endOfArea3 = maxEndOfAreaValue;

        resetAreas(st25DVTag, maxEndOfAreaValue);
        st25DVTag.getRegisterEndArea1().setRegisterValue(endOfArea1);
        try {
            st25DVTag.getRegisterEndArea2().setRegisterValue(endOfArea2);
            fail("Expecting exception");
        } catch (STException e) {
            if (e.getError() == STExceptionCode.CMD_FAILED) {
                byte[] expectedError = new byte[]{0x01, 0x0F};
                assertArrayEquals("Wrong ISO response", expectedError, e.getErrorData());
            } else {
                fail("Unexpected exception");
            }
        }

        resetAreas(st25DVTag, maxEndOfAreaValue);
        try {
            st25DVTag.setAreaEndValues((byte) endOfArea1, (byte) endOfArea2, (byte) endOfArea3);
            fail("Expecting exception");
        } catch (STException e) {
            assertEquals(STExceptionCode.BAD_PARAMETER, e.getError());
        }


        ////////////////// Try to write endArea3 < endArea2
        endOfArea1 = maxEndOfAreaValue / 4;
        endOfArea2 = maxEndOfAreaValue / 2;
        endOfArea3 = maxEndOfAreaValue / 2 - 1;

        resetAreas(st25DVTag, maxEndOfAreaValue);
        st25DVTag.getRegisterEndArea(ST25DVTag.AREA1).setRegisterValue(endOfArea1);
        st25DVTag.getRegisterEndArea(ST25DVTag.AREA2).setRegisterValue(endOfArea2);
        try {
            st25DVTag.getRegisterEndArea(ST25DVTag.AREA3).setRegisterValue(endOfArea3);
            fail("Expecting exception");
        } catch (STException e) {
            if (e.getError() == STExceptionCode.CMD_FAILED) {
                byte[] expectedError = new byte[]{0x01, 0x0F};
                assertArrayEquals("Wrong ISO response", expectedError, e.getErrorData());
            } else {
                fail("Unexpected exception");
            }
        }

        resetAreas(st25DVTag, maxEndOfAreaValue);
        try {
            st25DVTag.setAreaEndValues((byte) endOfArea1, (byte) endOfArea2, (byte) endOfArea3);
            fail("Expecting exception");
        } catch (STException e) {
            assertEquals(STExceptionCode.BAD_PARAMETER, e.getError());
        }


        ////////////////// Try to write endArea3 = endArea2 and different from maxEndOfAreaValue
        endOfArea1 = maxEndOfAreaValue / 4;
        endOfArea2 = maxEndOfAreaValue / 2;
        endOfArea3 = maxEndOfAreaValue / 2;

        resetAreas(st25DVTag, maxEndOfAreaValue);
        st25DVTag.getRegisterEndArea1().setRegisterValue(endOfArea1);
        st25DVTag.getRegisterEndArea2().setRegisterValue(endOfArea2);
        try {
            st25DVTag.getRegisterEndArea3().setRegisterValue(endOfArea3);
            fail("Expecting exception");
        } catch (STException e) {
            if (e.getError() == STExceptionCode.CMD_FAILED) {
                byte[] expectedError = new byte[]{0x01, 0x0F};
                assertArrayEquals("Wrong ISO response", expectedError, e.getErrorData());
            } else {
                fail("Unexpected exception");
            }
        }

        resetAreas(st25DVTag, maxEndOfAreaValue);
        try {
            st25DVTag.setAreaEndValues((byte) endOfArea1, (byte) endOfArea2, (byte) endOfArea3);
            fail("Expecting exception");
        } catch (STException e) {
            assertEquals(STExceptionCode.BAD_PARAMETER, e.getError());
        }

        //////////////////////////////////////////////////////////////////
        STLog.i("Test End");
    }

    private static void resetAreas(ST25DVTag st25DVTag, int maxEndOfAreaValue) throws STException {
        if ((st25DVTag.getRegisterEndArea3().getRegisterValue() & 0xFF) != maxEndOfAreaValue) {
            st25DVTag.getRegisterEndArea3().setRegisterValue(maxEndOfAreaValue);
        }

        if ((st25DVTag.getRegisterEndArea2().getRegisterValue() & 0xFF) != maxEndOfAreaValue) {
            st25DVTag.getRegisterEndArea2().setRegisterValue(maxEndOfAreaValue);
        }
    }

    /**
     * Get information about every areas
     */
    static void getAreasInfo(ST25DVTag st25DVTag) throws STException {
        mNbrOfAreas = st25DVTag.getNumberOfAreas();

        mArea1SizeInBytes = st25DVTag.getAreaSizeInBytes(MultiAreaInterface.AREA1);
        mArea2SizeInBytes = st25DVTag.getAreaSizeInBytes(MultiAreaInterface.AREA2);
        mArea3SizeInBytes = st25DVTag.getAreaSizeInBytes(MultiAreaInterface.AREA3);
        mArea4SizeInBytes = st25DVTag.getAreaSizeInBytes(MultiAreaInterface.AREA4);

        try {
            mArea1OffsetInBytes = st25DVTag.getAreaOffsetInBytes(MultiAreaInterface.AREA1);
        } catch (STException e) {
            switch (e.getError()) {
                case BAD_PARAMETER:
                    mArea1OffsetInBytes = INVALID_AREA;
                    break;

                default:
                    // Other exceptions are unchanged
                    throw (e);
            }
        }

        try {
            mArea2OffsetInBytes = st25DVTag.getAreaOffsetInBytes(MultiAreaInterface.AREA2);
        } catch (STException e) {
            switch (e.getError()) {
                case BAD_PARAMETER:
                    mArea2OffsetInBytes = INVALID_AREA;
                    break;

                default:
                    // Other exceptions are unchanged
                    throw (e);
            }
        }

        try {
            mArea3OffsetInBytes = st25DVTag.getAreaOffsetInBytes(MultiAreaInterface.AREA3);
        } catch (STException e) {
            switch (e.getError()) {
                case BAD_PARAMETER:
                    mArea3OffsetInBytes = INVALID_AREA;
                    break;

                default:
                    // Other exceptions are unchanged
                    throw (e);
            }
        }

        try {
            mArea4OffsetInBytes = st25DVTag.getAreaOffsetInBytes(MultiAreaInterface.AREA4);
        } catch (STException e) {
            switch (e.getError()) {
                case BAD_PARAMETER:
                    mArea4OffsetInBytes = INVALID_AREA;
                    break;

                default:
                    // Other exceptions are unchanged
                    throw (e);
            }
        }
    }

}
