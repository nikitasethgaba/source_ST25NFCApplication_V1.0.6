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
package com.st.st25sdk.iso14443sr;


import com.st.st25sdk.NFCTag;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso14443SRCustomCommand;
import com.st.st25sdk.ndef.NDEFMsg;

import java.util.Arrays;

import static com.st.st25sdk.STException.STExceptionCode.NOT_IMPLEMENTED;

public class STIso14443SRTag extends NFCTag {

    protected Iso14443SRCustomCommand mIso14443BSTCmd;

    public static final int DEFAULT_NBR_OF_BYTES_PER_BLOCK = 4;

    protected byte mChipID;

    public STIso14443SRTag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface);

        mName = "ISO 14443-2B tag";
        mDescription = "ST Tag based on ISO/IEC 14443-B";

        mUid = Arrays.copyOf(uid, uid.length);

        mIso14443BSTCmd = new Iso14443SRCustomCommand(readerInterface);
    }


    /**
     * @return the tag's random 8-bit chipID
     */
    public byte getChipID() {
        return mChipID;
    }

    public byte selectTag() throws STException {
        mIso14443BSTCmd.resetToInventory();
        byte chipID = mIso14443BSTCmd.initiate();
        mChipID = chipID;
        return mIso14443BSTCmd.select(chipID);
    }

    /**
     * Tag only responds if already in SELECTED state
     *
     * @param firstBlockAddress
     * @param sizeInBlocks
     * @return
     * @throws STException
     */
    public byte[] readBlocks(byte firstBlockAddress, byte sizeInBlocks) throws STException {
        return mIso14443BSTCmd.readBlocks(firstBlockAddress, sizeInBlocks);
    }

    /**
     * Tag only responds if already in SELECTED state
     *
     * @param firstBlockAddress
     * @param data
     * @throws STException
     */
    public void writeBlocks(byte firstBlockAddress, byte[] data) throws STException {
        mIso14443BSTCmd.writeBlocks(firstBlockAddress, data);
    }

    /**
     * Sets tag in the SELECTED state
     * @return
     * @throws STException
     */
    public byte select() throws STException {
        return mIso14443BSTCmd.select(mChipID);
    }

    /**
     * Tag enters deactivated state if already in SELECTED state
     * No answer from the tag
     * @throws STException
     */
    public void completion() throws STException {
        mIso14443BSTCmd.completion();
    }

    /**
     * Tag only responds if already in SELECTED state
     *
     * @return
     * @throws STException
     */
    public byte[] readUid() throws STException {
        return mIso14443BSTCmd.getUid();
    }

    @Override
    public int getCCFileLength() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public byte getCCMagicNumber() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public byte getCCMappingVersion() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public byte getCCReadAccess() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public byte getCCWriteAccess() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public int getCCMemorySize() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public void writeNdefMessage(NDEFMsg msg) throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public NDEFMsg readNdefMessage() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public byte[] readCCFile() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public void writeCCFile() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public void selectCCFile() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public void initEmptyCCFile() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public int getSysFileLength() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public int getMemSizeInBytes() throws STException {
        return mMemSize;
    }

    public int getBlockSizeInBytes() throws STException {
        return 4;   // 1 block = 4 bytes
    }

    @Override
    public byte[] readBytes(int byteAddress, int sizeInBytes) throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public void writeBytes(int byteAddress, byte[] data) throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }


    @Override
    public byte[] readSysFile() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }
}

