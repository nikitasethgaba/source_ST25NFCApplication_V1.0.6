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

package com.st.st25sdk.tests.type5.st25dv02kw;

import static com.st.st25sdk.TagHelper.ReadWriteProtection.READABLE_AND_WRITABLE;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READ_AND_WRITE_PROTECTED_BY_PWD;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.tests.type5.Type5Tests;
import com.st.st25sdk.type5.ST25DV02KWTag;


// This class is for ST25DV02KW specific tests. For Type5 generic tests, use Type5Tests
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ST25DV02KWTests extends Type5Tests {

    private static final String TAG = "ST25DV02KWTests";
    static private ST25DV02KWTag mST25DV02KWTag;


    @BeforeClass
    static public void setUp() {
        // Function called once before all tests
    }

    static public void setTag(ST25DV02KWTag st25DV02KWTag) {
        mST25DV02KWTag = st25DV02KWTag;
        Type5Tests.setTag(st25DV02KWTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readRawData() and writeRawData()
     */
    @Test
    @Override
    public void testReadWriteRawData() throws STException {

        NFCTagUtils.printTestName("testReadWriteRawData");

        // Pre-requisite for ST25DV02KW:
        // Configure the ST25DV02KW tag with one single area without protection
        setSingleAreaWithoutProtections();

        // We can now proceed with the test
        super.testReadWriteRawData();
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readSingleBlock() and writeSingleBlock()
     */
    @Test
    @Override
    public void testReadWriteSingleBlock() throws STException {

        NFCTagUtils.printTestName("testReadWriteSingleBlock");

        // Pre-requisite for ST25DV02KW:
        // Configure the ST25DV02KW tag with one single area without protection
        setSingleAreaWithoutProtections();

        // We can now proceed with the test
        super.testReadWriteSingleBlock();
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBlocks() and writeBlocks()
     */
    @Test
    @Override
    public void testReadWriteBlocks() throws STException {

        NFCTagUtils.printTestName("testReadWriteBlocks");

        // Pre-requisite for ST25DV02KW:
        // Configure the ST25DV02KW tag with one single area without protection
        setSingleAreaWithoutProtections();

        // We can now proceed with the test
        super.testReadWriteBlocks();
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing some NDEF records.
     * It will test that we can:
     * - add a record
     * - update an existing record
     * - and delete a record
     */
    @Test
    @Override
    public void testAddUpdateDeleteNdefRecords() throws STException {

        NFCTagUtils.printTestName("testAddUpdateDeleteNdefRecords");

        // Pre-requisite for ST25DV02KW:
        // Configure the ST25DV02KW tag with one single area without protection
        setSingleAreaWithoutProtections();

        // We can now proceed with the test
        super.testAddUpdateDeleteNdefRecords();
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing NDEF record in all the areas of the tags
     */
    @Test
    @Override
    public void testNdefInMultipleAreas() throws STException {

        NFCTagUtils.printTestName("testNdefInMultipleAreas");

        // Pre-requisite for ST25DV02KW:
        // Configure the ST25DV02KW tag with two areas without protection
        setDualAreaWithoutProtections();

        // We can now proceed with the test
        super.testNdefInMultipleAreas();
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of the tag when writting too much data in an area.
     * It should not corrupt the data in the following area.
     */
    @Override
    @Test
    public void testAreaBoundaries() throws STException {
        NFCTagUtils.printTestName("testMultiAreas");

        // Pre-requisite for ST25DV02KW:
        // Configure the ST25DV02KW tag with two areas without protection
        setDualAreaWithoutProtections();

        // We can now proceed with the test
        super.testAreaBoundaries();
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBytes() and readBlocks() when there are less bytes available
     * than requested. We should return what we have been able to read and not raise an exception.
     *
     * This test checks the behavior at end of memory (when the read asks more bytes than available
     * in memory)
     */
    @Override
    @Test
    public void testIncompleteReadAtEndOfMemory() throws STException {
        NFCTagUtils.printTestName("testIncompleteReadAtEndOfMemory");

        // Pre-requisite for ST25DV02KW:
        // Configure the ST25DV02KW tag with one single area without protection
        setSingleAreaWithoutProtections();

        // We can now proceed with the test
        super.testIncompleteReadAtEndOfMemory();
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBytes() and readBlocks() when there are less bytes available
     * than requested. We should return what we have been able to read and not raise an exception.
     *
     * This test checks the behavior when the following area is not readable. It is executed only for tags
     * supporting MultiAreaInterface.
     */
    @Override
    @Test
    public void testIncompleteReadAtAreaBoundary() throws STException {
        NFCTagUtils.printTestName("testIncompleteReadAtAreaBoundary");

        // Pre-requisite for ST25DV02KW:
        // Configure the ST25DV02KW tag with two areas with AREA1 not protected and AREA2 protected by PWD
        setMultipleAreasWithArea2Protected();

        // We can now proceed with the test
        super.testIncompleteReadAtAreaBoundary();
    }

    /***********************************************************************************/

    public void setSingleAreaWithoutProtections() throws STException {
        STLog.i("Configure the ST25DV02KW tag in single area without protections");

        // Write a Config to put the tag in single area, without permissions for Read and Write

        // Config password is needed when we want to change the Config

        byte[] password = new byte[] {0x00, 0x00, 0x00, 0x00};

        // Enter Config password
        byte passwordNumber = ST25DV02KWTag.ST25DV02KW_CONFIGURATION_PASSWORD_ID;
        mST25DV02KWTag.presentPassword(passwordNumber, password);

        // We can now set the tag in single area mode
        mST25DV02KWTag.setNumberOfAreas(1);

        // without pwd protection
        mST25DV02KWTag.setReadWriteProtection(1, READABLE_AND_WRITABLE);

    }

    public void setDualAreaWithoutProtections() throws STException {
        STLog.i("Configure the ST25DV02KW tag in dual areas without protections");

        // Write a Config to put the tag in dual area, without permissions for Read and Write

        // Config password is needed when we want to change the Config

        byte[] password = new byte[] {0x00, 0x00, 0x00, 0x00};

        // Enter Config password
        byte passwordNumber = ST25DV02KWTag.ST25DV02KW_CONFIGURATION_PASSWORD_ID;

        mST25DV02KWTag.presentPassword(passwordNumber, password);

        // We can now set the tag in dual area mode
        mST25DV02KWTag.setNumberOfAreas(2);

        // without pwd protection for both areas
        mST25DV02KWTag.setReadWriteProtection(1, READABLE_AND_WRITABLE);
        mST25DV02KWTag.setReadWriteProtection(2, READABLE_AND_WRITABLE);

    }

    public void setMultipleAreasWithArea2Protected() throws STException {
        STLog.i("Configure the ST25DV02KW tag in dual areas with AREA1 not protected and AREA2 protected by PWD.");

        // Write a Config to put the tag in dual area, without permissions for Read and Write

        // Config password is needed when we want to change the Config

        byte[] password = new byte[] {0x00, 0x00, 0x00, 0x00};

        // Enter Config password
        byte passwordNumber = ST25DV02KWTag.ST25DV02KW_CONFIGURATION_PASSWORD_ID;
        mST25DV02KWTag.presentPassword(passwordNumber, password);

        // We can now set the tag in dual area mode
        mST25DV02KWTag.setNumberOfAreas(2);

        // without pwd protection for both areas
        mST25DV02KWTag.setReadWriteProtection(1, READABLE_AND_WRITABLE);
        mST25DV02KWTag.setReadWriteProtection(2, READ_AND_WRITE_PROTECTED_BY_PWD);

    }

    @Test
    public void testPwm() throws STException {
        NFCTagUtils.printTestName("testMultiAreas");
        // We can now proceed with the test
        ST25DV02KWTestPwm.run(mST25DV02KWTag);
    }
}
