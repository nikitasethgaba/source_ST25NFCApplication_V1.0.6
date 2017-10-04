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

package com.st.st25sdk.type5;

import com.st.st25sdk.CacheInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso15693Protocol;
import com.st.st25sdk.command.NdefType5Command;
import com.st.st25sdk.ndef.NDEFMsg;

public class STAreaContent implements CacheInterface {

    private STType5MultiAreaTag mTag;


    // Offset of the area containing data
    private int mMemoryOffsetInBytes;
    private int mAreaSizeInBytes;

    //private CCFileType5Custom mCCFile;
    private  NDEFMsg mNdefMsg = null;



    // Offset (in number of blocks) at which the CC File + the NDEF file are located in memory.
    // Warning: The length of the CC File can vary!
    private int mOffset;

    private boolean mCacheActivated;
    private boolean mCacheInvalidated;

    private int mCcfileLength;


    /**
     *
     * @param tag: tag interface
     * @param memoryOffsetInBytes : Offset of the area containing this NDEF
     * @param areaSizeInBytes : Size of this area
     */
    public STAreaContent(STType5MultiAreaTag tag, int memoryOffsetInBytes, int areaSizeInBytes) {
        super();
        mTag = tag;
        mMemoryOffsetInBytes = memoryOffsetInBytes;
        mAreaSizeInBytes = areaSizeInBytes;

        mCacheActivated = true;
        mCacheInvalidated = true;
    }

    /**
     * Write a NDEF message with specified offset in block.
     * @param   ccOffsetInBlocks offset in block where to write NDEF message
     * @param   msg NDEF Message to write, must not be null
     * @throws  STException
     */
    public void writeNdefMessage(int ccOffsetInBlocks, NDEFMsg msg) throws STException {
        writeNdefMessage(ccOffsetInBlocks, msg, Iso15693Protocol.HIGH_DATA_RATE_MODE, mTag.getUid());
    }

    public void writeNdefMessage(NDEFMsg msg) throws STException {
        // Check if this area already has a valid CCFile

        // Length of the bytes containing the "type" and "length" of the TLV block containing the NDEF
        int tlSize = 2;
        int terminatorTlvLength = 1;
        int areaSizeInBytes = mAreaSizeInBytes;

        int expectedCCFileLength = CCFile.getExpectedCCFileLength(areaSizeInBytes);

        int ndefMsgLength;
        try {
            ndefMsgLength = msg.getLength();
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(STException.STExceptionCode.INVALID_NDEF_DATA);
        }

        // Check that the area size is enough for the NDEF message
        if (ndefMsgLength > (areaSizeInBytes - expectedCCFileLength - tlSize - terminatorTlvLength))
        {
            throw new STException(STException.STExceptionCode.NDEF_MESSAGE_TOO_BIG);
        }

        if (mCcfileLength != expectedCCFileLength) {
            // Write a CCFile with the appropriate size
            int ccfileLengthInBytes;
            byte[] buffer;
            if (areaSizeInBytes >= 2048) {
                buffer = new byte[8];
                buffer[0] = CCFile.CCFILE_LONG_IDENTIFIER;
                buffer[2] = 0x00;
                buffer[3] = (byte) 0x05; //Read multiple blocks mask (0x01) & high density mask (0x04)
                buffer[4] = 0x00;//RFU
                buffer[5] = 0x00;//RFU
                buffer[6] = (byte) (((areaSizeInBytes / CCFile.CCFILE_DATA_AREA_SIZE_MULTIPLIER) & 0xFF00) >> 8);
                buffer[7] = (byte) ((areaSizeInBytes / CCFile.CCFILE_DATA_AREA_SIZE_MULTIPLIER) & 0xFF);
                ccfileLengthInBytes = 8;

            }
            else {
                buffer = new byte[4];
                buffer[0] = CCFile.CCFILE_SHORT_IDENTIFIER;
                buffer[2] = (byte) (areaSizeInBytes / CCFile.CCFILE_DATA_AREA_SIZE_MULTIPLIER);
                buffer[3] = (byte) 0x01; //Read multiple blocks mask (0x01)
                ccfileLengthInBytes = 4;
            }

            buffer[1] = (byte) 0x40;
            mTag.writeBytes(mMemoryOffsetInBytes, buffer);

            // CC File successfully written. Save its size
            mCcfileLength = ccfileLengthInBytes;
        }

        // Write NDEF
        writeNdefMessage((byte) (mCcfileLength & 0xFF) / mTag.getBlockSizeInBytes(), msg);
    }


    /**
     * Write a NDEF message with specified offset in block and flags.
     *  Message is written at ccOffsetInBlocks + offset of the area containing this NDEF
     * @param ccOffsetInBlocks offset in block where to write NDEF message
     * @param msg NDEF Message to write, must not be null
     * @param flag Requested flags
     * @param uid TAG uid
     * @throws STException
     */
    public void writeNdefMessage(int ccOffsetInBlocks, NDEFMsg msg, byte flag, byte[] uid) throws STException {

        int offsetInBlocks = mMemoryOffsetInBytes / mTag.getBlockSizeInBytes() + ccOffsetInBlocks;

        NdefType5Command cmd = new NdefType5Command(mTag.getReaderInterface(), mTag.getUid());
        cmd.writeNdefMessage(offsetInBlocks, msg,flag, uid);

        // NDEF write was successful so we can save the NDEF in the cache

        mNdefMsg = msg.copy();
        mCacheInvalidated = false;
    }

    /**
     * Read the current NdefMessage on tag.
     * @see NDEFMsg
     * @return NDEF Message read.
     * @throws STException
     */
    public NDEFMsg readNdefMessage() throws STException {
        return readNdefMessage(Iso15693Protocol.HIGH_DATA_RATE_MODE, mTag.getUid());
    }

    /**
     * Read the current NdefMessage on tag.
     * @param flag Requested flags
     * @param uid TAG uid
     * @see NDEFMsg
     * @return NDEF Message read.
     * @throws STException
     */
    public NDEFMsg readNdefMessage(byte flag, byte[] uid) throws STException {

        if (!isCacheActivated() || !isCacheValid()) {
            // Cache not valid. NDEF should be read from the tag

            byte[] ccBlock = mTag.readBytes(mMemoryOffsetInBytes, 8);

            if (ccBlock.length >= 8) {
                if (ccBlock[2] == 0)
                    mCcfileLength = 8;
                else
                    mCcfileLength = 4;
            }
            else throw  new STException(STException.STExceptionCode.CMD_FAILED);

            int nbOfBlocksInCCFile  = mCcfileLength / mTag.getBlockSizeInBytes();

            // Read successful. Save the NDEF in cache
            // NB: ndefmsg can be null if the data doesn't contain a NDEF
            mNdefMsg = readNdefMessage(nbOfBlocksInCCFile, flag, uid);
            mCacheInvalidated = false;
        }
        // NDEF data already present in cache
        if(mNdefMsg != null) {
            return mNdefMsg.copy();
        } else {
            return null;
        }
    }

    /**
     * Read the NdefMessage on tag at specified block
     * @param nbOfBlocksInCCFile block number where to read.
     * @param flag Requested flags
     * @param uid TAG uid
     * @see NDEFMsg
     * @return NDEF Message read.
     * @throws STException
     */
    public NDEFMsg readNdefMessage(int nbOfBlocksInCCFile, byte flag, byte[] uid) throws STException {
        if (isCacheActivated() && isCacheValid()) {
            // NDEF data already present in cache
            if(mNdefMsg != null) {
                return mNdefMsg.copy();
            } else {
                return null;
            }
        } else {
            NdefType5Command cmd = new NdefType5Command(mTag.getReaderInterface(), mTag.getUid());
            // Read the first block following the CC File. This is the beginning of the Data Area containing a TLV block
            mNdefMsg = cmd.readNdefMessage(mMemoryOffsetInBytes/mTag.getBlockSizeInBytes() + nbOfBlocksInCCFile, flag, uid);
            mCacheInvalidated = false;

            if(mNdefMsg != null) {
                return mNdefMsg.copy();
            } else {
                return null;
            }
        }
    }


    @Override
    public void invalidateCache() {
        mCacheInvalidated = true;
    }

    @Override
    public void validateCache() {
        mCacheInvalidated = false;
    }

    @Override
    public void activateCache() {
        mCacheActivated = true;
    }

    @Override
    public void deactivateCache() {
        mCacheActivated = false;
    }

    @Override
    public void updateCache() throws STException {
        mCacheInvalidated = true;
    }

    @Override
    public boolean isCacheValid(){
        return !mCacheInvalidated;
    }

    @Override
    public boolean isCacheActivated(){
        return mCacheActivated;
    }

}
