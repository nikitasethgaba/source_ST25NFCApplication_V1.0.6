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

package com.st.st25sdk.tests.type5;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.SectorInterface;
import com.st.st25sdk.type5.STType5PasswordInterface;
import com.st.st25sdk.type5.Type5Tag;
import com.st.st25sdk.type5.STType5PasswordInterface.PasswordLength;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

public class TestSTSectors {

    static NFCTag mTag = null;

    static boolean mFastCommandsAvailable = false;

    static public void run(NFCTag tag, String readerName) throws STException, InterruptedException {

        mTag = tag;
        if (readerName.equals("CR95HF") || readerName.equals("ST25R3911B-DISCO")) {
            // as for now, there is no built-in mechanism to now if a feature is supported by a reader
            // so just rely on the test application to provide basic reader information.
            mFastCommandsAvailable = true;
        }

        //////////////////////////////////////////////////////////////////
        // !!! Assumption is default pwd = {0x00, 0x00, 0x00, 0x00 }
        STLog.i("Test Password commands");
        testPasswordCommands();

        //////////////////////////////////////////////////////////////////
        // !!! Assumption is default pwd = {0x00, 0x00, 0x00, 0x00 }
        STLog.i("Test Password commands");
        testSectorAccess();


        //////////////////////////////////////////////////////////////////
        STLog.i("Test End");

    }

    /**
     * @throws STException
     */
    static private void testPasswordCommands() throws STException {
        byte[] initialPassword = new byte[]{0x00, 0x00, 0x00, 0x00};
        //byte[] testPassword = new byte[]{(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef};
        byte[] testPassword = new byte[]{0x00, 0x00, 0x00, 0x01};

        for (int i = 1; i < 4; i++) {
            STType5PasswordInterface.PasswordLength length = ((STType5PasswordInterface)mTag).getPasswordLength((byte) i);
            if (length != STType5PasswordInterface.PasswordLength.PWD_ON_32_BITS)
                fail(String.format("Wrong password length"));
            ((STType5PasswordInterface)mTag).presentPassword((byte) i, initialPassword);
        }

        for (int i = 1; i < 4; i++) {
            ((STType5PasswordInterface)mTag).presentPassword((byte) i, initialPassword);
            ((STType5PasswordInterface)mTag).writePassword((byte) i, testPassword);
        }

        for (int i = 1; i < 4; i++) {
            try {

                ((STType5PasswordInterface)mTag).presentPassword((byte) i, initialPassword);
                fail(String.format("Failed to change password %d", i));
            }
            catch (STException e) {
                assertEquals(STException.STExceptionCode.CMD_FAILED, e.getError());
            }
        }

        // Read Sectors password
        for (int i = 1; i < 4; i++) {
            int pwdNumber = ((STType5PasswordInterface)mTag).getPasswordNumber(i - 1) & 0xFF;
            assertEquals(0, pwdNumber);
        }

        // Read Sectors password
        for (int i = 1; i < 4; i++) {
            int pwdNumber = ((STType5PasswordInterface)mTag).getPasswordNumber(i - 1) & 0xFF;
            assertEquals(0, pwdNumber);
        }

        //restore passwords
        for (int i = 1; i < 4; i++) {
            ((STType5PasswordInterface)mTag).presentPassword((byte) i, testPassword);
            ((STType5PasswordInterface)mTag).writePassword((byte) i, initialPassword);
        }
    }

    /**
     * @throws STException
     */
    static private void testSectorAccess() throws STException {
        byte[] initialPassword = new byte[]{0x00, 0x00, 0x00, 0x00};
        //byte[] testPassword = new byte[]{(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef};
        byte[] testPassword = new byte[]{0x00, 0x00, 0x00, 0x01};


        ///////////////////////////////////////////
        // Step 1: For each sector read/write data
        ///////////////////////////////////////////

        byte[] dataToWrite = new byte[] {0x01, 0x02, 0x03, 0x04};
        byte[] dataRead;
        int numberOfBlocksPerSector = ((SectorInterface)mTag).getNumberOfBlocksPerSector();
        int numberOfBytesPerBlock = ((Type5Tag) mTag).getBlockSizeInBytes();
        int numberOfSectors = ((SectorInterface)mTag).getNumberOfSectors();

        for (int i = 0; i < numberOfSectors; i++) {
            int byteAddress = numberOfBlocksPerSector * numberOfBytesPerBlock * i;
            mTag.writeBytes(byteAddress , dataToWrite);
            // Check that the data read is the same as what was written
            dataRead = mTag.readBytes(byteAddress, dataToWrite.length);
            assertArrayEquals(dataToWrite, dataRead);
        }

        ////////////////////////////////////////////
        //Step 2: Change password 2
        //
        ///////////////////////////////////////////

        ((STType5PasswordInterface)mTag).presentPassword((byte) 2, initialPassword);
        ((STType5PasswordInterface)mTag).writePassword((byte) 2, testPassword);


        ///////////////////////////////////////////
        //Step 3 Change password for sector 0
        // This set by default the sector 0 protected in W by password
        //
        //////////////////////////////////////////
        ((STType5PasswordInterface)mTag).setPasswordNumber(0, (byte) 2);
        byte securityStatus = ((SectorInterface)mTag).getSecurityStatus(0);
        if (securityStatus != (byte) 0x11)
            fail("Wrong security status");

        ((STType5PasswordInterface)mTag).presentPassword((byte) 1, initialPassword);

        try {
            mTag.writeBytes(0 , dataToWrite);
            fail(("Should fail because protected by pwd"));
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.ISO15693_BLOCK_IS_LOCKED, e.getError());
        }



        ///////////////////////////////////////////
        // Step 4: For each sector (starting from 1) status set to protected by pwd number 2 in write (9)
        // Check that write leads to an exception for every sectors
        ///////////////////////////////////////////
        for (int i = 1; i < numberOfSectors; i++) {
            //Protected in write with password number 2
            ((SectorInterface)mTag).setSecurityStatus(i, (byte) 0x11);
            int byteAddress = numberOfBlocksPerSector * numberOfBytesPerBlock * i;
            try {
                mTag.writeBytes(byteAddress , dataToWrite);
                fail(("Should fail because protected by pwd"));
            } catch (STException e) {
                assertEquals(STException.STExceptionCode.ISO15693_BLOCK_IS_LOCKED, e.getError());
            }
        }

        ///////////////////////////////////////////
        // Step 3: Password is presented check that read/write OK
        ///////////////////////////////////////////
        ((STType5PasswordInterface)mTag).presentPassword((byte) 2, testPassword);

        for (int i = 0; i < numberOfSectors; i++) {
            int byteAddress = numberOfBlocksPerSector * numberOfBytesPerBlock * i;
            mTag.writeBytes(byteAddress , dataToWrite);
            // Check that the data read is the same as what was written
            dataRead = mTag.readBytes(byteAddress, dataToWrite.length);
            assertArrayEquals(dataToWrite, dataRead);
            //Possible to revert with I2C
            //((SectorInterface)mTag).setSecurityStatus(i, (byte) 0x00);
            //Revert password number 2

        }
        ((STType5PasswordInterface)mTag).presentPassword((byte) 2, testPassword);
        ((STType5PasswordInterface)mTag).writePassword((byte) 2, initialPassword);
    }

}
