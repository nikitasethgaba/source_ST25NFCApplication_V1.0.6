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

package com.st.st25sdk.tests.type4;

import org.junit.Assume;
import org.junit.Test;

import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.tests.generic.NFCTagTests;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type4a.STType4Tag;
import com.st.st25sdk.type4a.Type4Tag;

public class Type4Tests extends NFCTagTests {

    static private Type4Tag mType4Tag;

    static public void setTag(Type4Tag type4Tag) {
        mType4Tag = type4Tag;
        NFCTagTests.setTag(type4Tag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readRawData() and writeRawData()
     */
    @Test
    public void testReadWriteRawData() throws STException {
        NFCTagUtils.printTestName("testReadWriteRawData");

        Type4TestReadWriteRawData.run(mType4Tag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing NDEF record in all the areas of the tags
     */
    @Test
    @Override
    public void testNdefInMultipleAreas() throws STException {

        NFCTagUtils.printTestName("testNdefInMultipleAreas");

        // This test can only be run on Type 4 tags supporting multiple areas
        try {
            MultiAreaInterface multiAreaInterface = (MultiAreaInterface) mType4Tag;

            // Pre-requisite for Type4 tags supporting multiple areas:
            // Configure the tag with 8 areas without protection
            STM24TAHighDensityUtils.setNbrOfAreasAndDisablePwdProtections(mType4Tag, multiAreaInterface, 8);

            // We can now proceed with the test
            super.testNdefInMultipleAreas();

        } catch (ClassCastException e) {
            // Skip this test because this tag doesn't support multiple area
            Assume.assumeTrue(false);
        }

    }


    /*
     * TEST DESCRIPTION:
     * Test reading the System File
     */
    @Test
    public void testSystemFile() throws STException {

        NFCTagUtils.printTestName("testSystemFile");

        // This test can only be run on STType4Tag
        try {
            STType4Tag stType4Tag = (STType4Tag) mType4Tag;
            Type4TestSystemFile.run(stType4Tag);

        } catch (ClassCastException e) {
            // Skip this test because this tag doesn't have a SysFile
            Assume.assumeTrue(false);
        }
    }

    @Test
    public void testReadWrite36Bytes() throws STException {
        Assume.assumeTrue(mType4Tag instanceof STType4Tag);
        Type4ATestReadWrite36Bytes.run((STType4Tag)mType4Tag);
    }

    @Test
    public void testReadWriteNDEF36Bytes() throws STException {
        Assume.assumeTrue(mType4Tag instanceof STType4Tag);
        Type4ATestNdefUriRecord36Bytes.run((STType4Tag)mType4Tag);
    }

}
