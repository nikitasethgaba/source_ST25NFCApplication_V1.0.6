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

package com.st.st25sdk.type4a;

import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.type5.STType5PasswordInterface.PasswordLength;

import static com.st.st25sdk.STException.STExceptionCode.NOT_SUPPORTED;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READABLE_AND_WRITABLE;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READABLE_AND_WRITE_PROTECTED_BY_PWD;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READ_AND_WRITE_PROTECTED_BY_PWD;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.WRITEABLE_AND_READ_PROTECTED_BY_PWD;

public class STType4MultiAreaTag extends STType4Tag implements MultiAreaInterface {

    public STType4MultiAreaTag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface, uid);
    }

    ///////////////////    Multi-Area wrapper   ///////////////////////
    // Each area is mapped to a File
    //
    // If the tag has N files:
    // Areas are in the range[1..N]
    // FileIds are in the range 0x0001 to 0x000N (max value for N is 8)

    @Override
    public int getMaxNumberOfAreas() {
        return 1;
    }

    @Override
    public int getNumberOfAreas() throws STException {
        return getNbrOfFiles();
    }

    @Override
    public void setNumberOfAreas(int nbOfAreas) throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public int getAreaSizeInBytes(int area) throws STException {
        return getMaxFileSize(area);
    }

    @Override
    public int getAreaOffsetInBytes(int area) throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public int getAreaOffsetInBlocks(int area) throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public int getAreaFromBlockAddress(int blockOffset) throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public PasswordLength getAreaPasswordLength(int area) throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public TagHelper.ReadWriteProtection getReadWriteProtection(int area) throws STException {
        TagHelper.ReadWriteProtection readWriteProtection;

        boolean isReadPasswordRequested = isReadPasswordRequested(area);
        boolean isWritePasswordRequested = isWritePasswordRequested(area);

        if(isReadPasswordRequested) {
            if(isWritePasswordRequested) {
                // File protected in read and in write
                readWriteProtection = READ_AND_WRITE_PROTECTED_BY_PWD;
            } else {
                // File protected in read but not protected in write
                readWriteProtection = WRITEABLE_AND_READ_PROTECTED_BY_PWD;
            }
        } else {
            if(isWritePasswordRequested) {
                // File protected in write but not protected in read
                readWriteProtection = READABLE_AND_WRITE_PROTECTED_BY_PWD;
            } else {
                // File not protected
                readWriteProtection = READABLE_AND_WRITABLE;
            }
        }

        return readWriteProtection;
    }

    @Override
    public void setReadWriteProtection(int area, TagHelper.ReadWriteProtection readWriteProtection) throws STException {
        // For STType4Tags, you should use setReadWriteProtection(int area, ReadWriteProtection protection, byte[] password)
        // in order to do the selection, the password presentation and the protection update in an atomic manner.
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public void setReadWriteProtection(int area, TagHelper.ReadWriteProtection protection, byte[] writePassword) throws  STException {

        selectFile(area);
        verifyWritePassword(writePassword);

        switch(protection) {
            case READABLE_AND_WRITABLE:
                unlockRead();
                unlockWrite();
                break;

            case READABLE_AND_WRITE_PROTECTED_BY_PWD:
                unlockRead();
                lockWrite();
                break;

            case READ_AND_WRITE_PROTECTED_BY_PWD:
                lockRead();
                lockWrite();
                break;

            case WRITEABLE_AND_READ_PROTECTED_BY_PWD:
                lockRead();
                unlockWrite();
                break;

            default:
                STLog.e("Error! ReadWriteProtection " + protection.toString() + " is not supported by STType4Tag!");
                break;
        }
    }

    @Override
    public NDEFMsg readNdefMessage() throws STException {
        int ndefFileId = mCCFile.getNdefFileId();
        return super.readNdefMessage(ndefFileId);
    }

    @Override
    public void writeNdefMessage(NDEFMsg msg) throws STException {
        int ndefFileId = mCCFile.getNdefFileId();
        super.writeNdefMessage(ndefFileId, msg);
    }

}
