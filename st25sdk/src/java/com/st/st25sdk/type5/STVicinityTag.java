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

import com.st.st25sdk.CCFileInterface;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.SectorInterface;
import com.st.st25sdk.command.NdefCommandInterface;
import com.st.st25sdk.command.NdefVicinityCommand;
import com.st.st25sdk.command.VicinityCommand;
import com.st.st25sdk.command.VicinityMemoryCommand;
import com.st.st25sdk.ndef.NDEFMsg;

import java.util.Arrays;

import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;
import static com.st.st25sdk.STException.STExceptionCode.INVALID_CCFILE;
import static com.st.st25sdk.STException.STExceptionCode.NOT_IMPLEMENTED;
import static com.st.st25sdk.STException.STExceptionCode.NOT_SUPPORTED;
import static com.st.st25sdk.type5.STType5PasswordInterface.PasswordLength.PWD_ON_32_BITS;


public class STVicinityTag extends STType5Tag implements CCFileInterface, SectorInterface, NdefCommandInterface, STType5PasswordInterface {

    protected CCFileVicinity mCCFile;
    protected VicinityCommand mCmd;
    protected NdefVicinityCommand mNdefVicinityCmd;
    protected STType5Sector mSectorSec;
    protected VicinityMemoryCommand mVicinityMemoryCommand;

    protected int mNbOfSectors;
    protected int mNbOfBlocksPerSector;

    protected STVicinityTag(RFReaderInterface readerInterface, byte[] uid, int nbOfSectors, int nbOfBlocksPerSector) throws STException {
        super(readerInterface, uid);
        mAddressingMode = TagAddressingMode.NON_ADDRESSED;


        mCmd = new VicinityCommand(readerInterface, uid);
        mNdefVicinityCmd = new NdefVicinityCommand(readerInterface, uid);
        mVicinityMemoryCommand = new VicinityMemoryCommand(readerInterface, uid);

        mCCFile = new CCFileVicinity(mVicinityMemoryCommand);
        mSysFile = new SysFileVicinity(mCmd);

        mUid = Arrays.copyOf(uid, uid.length);

        mDescription = "ISO/IEC 15693";

        mCache.add(mCCFile);
        mCache.add(mSysFile);

        mNbOfSectors = nbOfSectors;
        mNbOfBlocksPerSector = nbOfBlocksPerSector;
        try {
            mSectorSec = new STType5Sector(mCmd, nbOfSectors, nbOfBlocksPerSector);
            mCache.add(mSectorSec);
        } catch (STException e) {
            STLog.e("No support for sector interface");
        }


    }

    /**
     * Write a NDEF message.
     *  Function updates cache if successful.
     * @param msg   NDEF Message to write, must not be null
     * @throws STException
     */
    @Override
    public void writeNdefMessage(NDEFMsg msg) throws STException {
        writeNdefMessage(msg, mCmd.getFlag());
    }

    /**
     * Write a NDEF message with specified flags.
     *  Function updates cache if successful.
     * @param msg   NDEF Message to write, must not be null
     * @param flag  Requested flags
     * @throws STException
     */
    @Override
    public void writeNdefMessage(NDEFMsg msg, byte flag) throws STException {
        // Length of the bytes containing the "type" and "length" of the TLV block containing the NDEF
        int tlSize = 2;
        int terminatorTlvLength = 1;
        int ccfileLength;
        int ccMemorySize;

        int ndefLength;
        try {
            ndefLength = msg.getLength();
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(STException.STExceptionCode.INVALID_NDEF_DATA);
        }

        try {
            ccfileLength = getCCFileLength();
            ccMemorySize = getCCMemorySize();
        } catch (STException e) {

            if (e.getError().equals(INVALID_CCFILE)) {
                initEmptyCCFile();
                if (mNdefMsg != null)
                {
                    mCache.remove(mNdefMsg);
                }
                writeCCFile();
                ccfileLength = getCCFileLength();
                ccMemorySize = getCCMemorySize();
            } else
                throw e;
        }

        if (ccfileLength > 0) {
            if ((ccMemorySize - ccfileLength - tlSize - terminatorTlvLength) >= ndefLength)
            {
                if (mNdefMsg != null)
                {
                    mCache.remove(mNdefMsg);
                }
                mNdefVicinityCmd.writeNdefMessage((byte) ccfileLength / getBlockSizeInBytes(), msg, flag, mUid);
            } else {
                throw new STException(STException.STExceptionCode.NDEF_MESSAGE_TOO_BIG);
            }
        } else {
            throw new STException(STException.STExceptionCode.INVALID_CCFILE);
        }

        mNdefMsg = msg.copy();
        mCache.add(mNdefMsg);
    }


    /**
     * Read the current NdefMessage on tag.
     * Uses the cache to check if a RF read activity on tag should be done
     * @return NDEF Message read from the tag or from the cache.
     * @throws STException
     */
    @Override
    public NDEFMsg readNdefMessage() throws STException {
        return readNdefMessage(mCmd.getFlag());
    }

    /**
     * Read the current NdefMessage on tag.
     * Uses the cache to check if a RF read activity on tag should be done
     * @param  flag
     * @return NDEF Message read from the tag or from the cache.
     * @throws STException
     */
    @Override
    public NDEFMsg readNdefMessage(byte flag) throws STException {
        if (mCache.isCacheActivated() && mCache.isCacheValid(mNdefMsg))
            return mNdefMsg.copy();

        int offsetInBlocks = getCCFileLength() / getBlockSizeInBytes();

        mCache.remove(mNdefMsg);
        mNdefMsg = mNdefVicinityCmd.readNdefMessage(offsetInBlocks, flag, getUid());

        if (mNdefMsg != null ) {
            mCache.add(mNdefMsg);
            return mNdefMsg.copy();
        }

        return null;
    }

    ///////////////////////     Raw Data methods (with Byte granularity)      ////////////////////////////

    /**
     * Read data with Byte granularity
     * @param byteAddress
     * @param sizeInBytes
     * @return
     * @throws STException
     */
    @Override
    public byte[] readBytes(int byteAddress, int sizeInBytes) throws STException {
        return readBytes(byteAddress, sizeInBytes, mCmd.getFlag());
    }

    /**
     * Read data with Byte granularity.
     *
     * maxReadResponseSize can be specified. It should be the minimum value between
     * the tag's capability and the reader's max possible response length.
     *
     * @param byteAddress
     * @param sizeInBytes
     * @param flag
     * @return
     * @throws STException
     */
    @Override
    public byte[] readBytes(int byteAddress, int sizeInBytes, byte flag) throws STException {
        int memSizeInBytes = getMemSizeInBytes();

        if((byteAddress < 0) || (byteAddress >= memSizeInBytes)) {
            throw new STException(BAD_PARAMETER);
        }

        if((byteAddress + sizeInBytes) > memSizeInBytes) {
            // This read will be incomplete because the end of the memory is reached.
            // Return what can be read.
            sizeInBytes = memSizeInBytes - byteAddress;
        }

        return mVicinityMemoryCommand.readBytes(byteAddress, sizeInBytes, flag, getUid());
    }

    /**
     * Write data with Byte granularity
     *
     * WARNING: After this write, the cache may not be aligned anymore with tag's memory content so
     *          it is recommended to invalidate the cache.
     *
     * @param byteAddress
     * @param data
     * @throws STException
     */
    @Override
    public void writeBytes(int byteAddress, byte[] data) throws STException {
        writeBytes(byteAddress, data, mCmd.getFlag());
    }

    /**
     * Write data with Byte granularity
     *
     * WARNING: After this write, the cache may not be aligned anymore with tag's memory content so
     *          it is recommended to invalidate the cache.
     *
     * @param byteAddress
     * @param data
     * @param flag
     * @throws STException
     */
    @Override
    public void writeBytes(int byteAddress, byte[] data, byte flag) throws STException {
        if( (byteAddress < 0) || ((byteAddress + data.length) > getMemSizeInBytes()) ) {
            throw new STException(BAD_PARAMETER);
        }

        mVicinityMemoryCommand.writeBytes(byteAddress, data, flag, getUid());
    }

    ///////////////////////     Raw Data methods (with Block granularity)      ////////////////////////////

    /**
     * Read data with Block granularity
     * @param firstBlockAddress
     * @param sizeInBlocks
     * @return
     * @throws STException
     */
    @Override
    public ReadBlockResult readBlocks(int firstBlockAddress, int sizeInBlocks) throws STException {
        return mVicinityMemoryCommand.readBlocks(firstBlockAddress, sizeInBlocks);
    }

    /**
     * Read data with Block granularity.
     *
     * @param firstBlockAddress
     * @param sizeInBlocks
     * @param flag
     * @return
     * @throws STException
     */
    @Override
    public ReadBlockResult readBlocks(int firstBlockAddress, int sizeInBlocks, byte flag) throws STException {
        return mVicinityMemoryCommand.readBlocks(firstBlockAddress, sizeInBlocks, flag, getUid());
    }

    /**
     * Write data with Block granularity
     *
     * WARNING: After this write, the cache may not be aligned anymore with tag's memory content so
     *          it is recommended to invalidate the cache.
     *
     * @param firstBlockAddress
     * @param data
     * @throws STException
     */
    @Override
    public void writeBlocks(int firstBlockAddress, byte[] data) throws STException {
        mVicinityMemoryCommand.writeBlocks(firstBlockAddress, data);
    }

    /**
     * Write data with Block granularity
     *
     * WARNING: After this write, the cache may not be aligned anymore with tag's memory content so
     *          it is recommended to invalidate the cache.
     *
     * @param firstBlockAddress
     * @param data
     * @param flag
     * @throws STException
     */
    @Override
    public void writeBlocks(int firstBlockAddress, byte[] data, byte flag) throws STException {
        mVicinityMemoryCommand.writeBlocks(firstBlockAddress, data, flag, getUid());
    }

    @Override
    public void initEmptyCCFile() throws STException {
        int memSize = getMemSizeInBytes();
        mCCFile.initEmptyCCFile(memSize);
    }

    @Override
    public void selectCCFile() throws STException {
    }

    @Override
    public int getCCFileLength() throws STException {
        return mCCFile.getCCLength();
    }

    @Override
    public byte[] readCCFile() throws STException {
        return mCCFile.read();
    }

    @Override
    public void writeCCFile() throws STException {
        mCCFile.write();
    }

    @Override
    public byte getCCMagicNumber() throws STException {
        return mCCFile.getMagicNumber();
    }

    @Override
    public byte getCCMappingVersion() throws STException {
        return mCCFile.getCCMappingVersion();
    }

    @Override
    public byte getCCReadAccess() throws STException {
        return mCCFile.getCCReadAccess();
    }

    @Override
    public byte getCCWriteAccess() throws STException {
        return mCCFile.getCCWriteAccess();
    }

    @Override
    public int getCCMemorySize() throws STException {
        return mCCFile.getDataAreaSize();
    }

    /////////////////////////////////////////////////////////////////////

    @Override
    public byte[] readSingleBlock(byte pos) throws STException {
        // For vicinity tag, use the method taking a position in a byte[]
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public byte[] readSingleBlock(byte pos, byte flag) throws STException {
        // For vicinity tag, use the method taking a position in a byte[]
        throw new STException(NOT_SUPPORTED);
    }

    public byte[] readSingleBlock(byte[] pos) throws STException {
        return readSingleBlock(pos, mCmd.getFlag());
    }

    public byte[] readSingleBlock(byte[] pos, byte flag) throws STException {
        return mCmd.readSingleBlock(pos, flag, mUid);
    }

    /////////////////////////////////////////////////////////////////////

    @Override
    public byte writeSingleBlock(byte pos, byte[] buffer) throws STException {
        // For vicinity tag, use the method taking a position in a byte[]
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public byte writeSingleBlock(byte pos, byte[] buffer, byte flag) throws STException {
        // For vicinity tag, use the method taking a position in a byte[]
        throw new STException(NOT_SUPPORTED);
    }

    public byte writeSingleBlock(byte[] pos, byte[] buffer) throws STException {
        return writeSingleBlock(pos, buffer, mCmd.getFlag());
    }

    public byte writeSingleBlock(byte[] pos, byte[] buffer, byte flag) throws STException {
        return mCmd.writeSingleBlock(pos, buffer, flag, mUid);
    }

    /////////////////////////////////////////////////////////////////////

    @Override
    public byte[] readMultipleBlock(byte pos, byte block) throws STException {
        // For vicinity tag, use the method taking a position in a byte[]
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public byte[] readMultipleBlock(byte pos, byte block, byte flag) throws STException {
        // For vicinity tag, use the method taking a position in a byte[]
        throw new STException(NOT_SUPPORTED);
    }

    public byte[] readMultipleBlock(byte[] pos, byte block) throws STException {
        return readMultipleBlock(pos, block, mCmd.getFlag());
    }

    public byte[] readMultipleBlock(byte[] pos, byte block, byte flag) throws STException {
        return mCmd.readMultipleBlock(pos, block, flag, mUid);
    }

    /**
     * Reads back a single block of data from a tag.
     * The data rate of the response is twice as fast as a readSingleBlock command.
     *
     * @param blockAddress Address of the block to read
     * @return
     * @throws STException
     */
    public byte[] fastReadSingleBlock(byte[] blockAddress) throws STException {
        return fastReadSingleBlock(blockAddress, mCmd.getFlag());
    }

    /**
     * Reads back a single block of data from a tag.
     * The data rate of the response is twice as fast as a readSingleBlock command.
     *
     * @param blockAddress Address of the block to read
     * @param flag When the Option bit is set, the response includes the Block Security Status
     * @return
     * @throws STException
     */
    public byte[] fastReadSingleBlock(byte[] blockAddress, byte flag) throws STException {
        return mCmd.fastReadSingleBlock(blockAddress, flag, mUid);
    }

    /**
     * Reads back a multiple blocks of data from a tag. The number of blocks read is numberOfBlocks + 1.
     * The data rate of the response is twice as fast as a readMultipleBlock command.
     *
     * @param blockAddress Address of the first block to read
     * @param numberOfBlocks Set to target blocks to read minus 1. The tag will actually return numberOfBlocks + 1 blocks.
     * @return
     * @throws STException
     */
    public byte[] fastReadMultipleBlock(byte[] blockAddress, byte numberOfBlocks) throws STException {
        return fastReadMultipleBlock(blockAddress, numberOfBlocks, mCmd.getFlag());
    }

    /**
     * Reads back a multiple blocks of data from a tag. The number of blocks read is numberOfBlocks + 1.
     * The data rate of the response is twice as fast as a readMultipleBlock command.
     *
     * @param blockAddress Address of the first block to read
     * @param numberOfBlocks Set to target blocks to read minus 1. The tag will actually return numberOfBlocks + 1 blocks.
     * @param flag When the Option bit is set, the response includes the Block Security Status
     * @return
     * @throws STException
     */
    public byte[] fastReadMultipleBlock(byte[] blockAddress, byte numberOfBlocks, byte flag) throws STException {
        return mCmd.fastReadMultipleBlock(blockAddress, numberOfBlocks, flag, mUid);
    }

    /////////////////////////////////////////////////////////////////////

    @Override
    public byte[] readSysFile() throws STException {
        return mCmd.getSystemInfo();
    }

    @Override
    public int getMemSizeInBytes() throws STException {
        return getNumberOfBlocks() * getBlockSizeInBytes();
    }


    @Override
    public int getNumberOfSectors() {
        return mNbOfSectors;
    }

    @Override
    public int getNumberOfBlocksPerSector() {
        return mNbOfBlocksPerSector;
    }

    @Override
    public byte getSecurityStatus(int sector) throws STException {
        if (mSectorSec != null)
            return mSectorSec.getSecurityStatus(sector);
        return (byte) 0xFF;
    }

    @Override
    public byte[] getSecurityStatus() throws STException {
        if (mSectorSec != null)
            return mSectorSec.getSecurityStatus();
        return null;
    }

    @Override
    public void setSecurityStatus(int sector, byte value) throws STException {
        if (mSectorSec != null)
            mSectorSec.setSecurityStatus(sector, value);
    }

    @Override
    public void presentPassword(byte passwordNumber, byte[] password) throws STException {
        mIso15693CustomCommand.presentPwd(passwordNumber, password);
    }

    @Override
    public void writePassword(byte passwordNumber, byte[] newPassword) throws STException {
        mIso15693CustomCommand.writePwd(passwordNumber, newPassword);
    }

    @Override
    public PasswordLength getPasswordLength(byte passwordNumber) throws STException {
        return PWD_ON_32_BITS;
    }

    @Override
    public byte getConfigurationPasswordNumber() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }

    @Override
    public byte getPasswordNumber(int sector) throws STException {
        if (mSectorSec != null)
            return (byte) ((mSectorSec.getSecurityStatus(sector) & 0x0C) >> 2);
        else
            throw new STException(CMD_FAILED);
    }

    @Override
    public void setPasswordNumber(int sector, byte passwordNumber) throws STException {
        if (mSectorSec != null)
            mSectorSec.setSecurityStatus(sector, (byte) (passwordNumber << 3));
        else
            throw new STException(CMD_FAILED);
    }
}
