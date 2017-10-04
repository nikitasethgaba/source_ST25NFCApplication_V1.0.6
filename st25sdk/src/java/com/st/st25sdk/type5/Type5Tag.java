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
import com.st.st25sdk.CacheInterface;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.SysFileInterface;
import com.st.st25sdk.TagCache;
import com.st.st25sdk.command.Iso15693Command;
import com.st.st25sdk.command.Iso15693Protocol;
import com.st.st25sdk.command.NdefCommandInterface;
import com.st.st25sdk.command.NdefType5Command;
import com.st.st25sdk.command.Type5Command;
import com.st.st25sdk.command.VicinityCommand;
import com.st.st25sdk.ndef.NDEFMsg;


import java.util.Arrays;

import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.INVALID_CCFILE;
import static com.st.st25sdk.STException.STExceptionCode.NOT_SUPPORTED;

public class Type5Tag extends NFCTag implements CCFileInterface, SysFileInterface, CacheInterface, NdefCommandInterface {


    protected CCFileType5 mCCFile;
    protected Type5Command mType5Cmd;
    protected Iso15693Command mIso15693Cmd;
    protected NdefType5Command mNdefCmd;

    protected TagCache mCache;

    protected int mMaxReadMultipleBlocksReturned;

    public static final int DEFAULT_NBR_OF_BYTES_PER_BLOCK = 4;

    private int mBlockSizeInBytes = DEFAULT_NBR_OF_BYTES_PER_BLOCK;

    public Type5Tag(RFReaderInterface readerInterface, byte[] uid) {
        super(readerInterface);

        mName = "NFC Type 5 tag";
        mDescription = "NFC type V - ISO/IEC 15693";

        mUid = Arrays.copyOf(uid, uid.length);

        determineBlockSize(readerInterface, uid);

        mType5Cmd = new Type5Command(readerInterface, uid);
        mIso15693Cmd = new Iso15693Command(readerInterface, uid);

        mCCFile = new CCFileType5(mType5Cmd);
        // NDEF file located at offset 0 of memory
        mNdefCmd = new NdefType5Command(readerInterface, uid);

        setMaxReadMultipleBlocksReturned(Iso15693Protocol.DEFAULT_READ_MULTIPLE_MAX_NBR_OF_BLOCKS);

        mCache = new TagCache();
        mCache.add(mCCFile);
    }


    private void determineBlockSize(RFReaderInterface readerInterface, byte[] uid) {
        Iso15693Command iso15693Command = new Iso15693Command(readerInterface, uid);

        // In case of failure, mBlockSizeInBytes will keep its default value (DEFAULT_NBR_OF_BYTES_PER_BLOCK)
        try {
            byte[] response = iso15693Command.readSingleBlock((byte) 0);

            if ((response != null) && (response.length > 1)) {
                mBlockSizeInBytes = response.length - 1;
            } else {
                STLog.e("Failed to determine the number of bytes per block!");
            }
        } catch (STException e) {
            // Could be a Vicinity tag
            VicinityCommand vicinityCommand = new VicinityCommand(readerInterface, uid);
            byte[] response;
            try {
                response = vicinityCommand.readSingleBlock(new byte[]{0 ,0});

                if ((response != null) && (response.length > 1)) {
                    mBlockSizeInBytes = response.length - 1;
                } else {
                    STLog.e("Failed to determine the number of bytes per block!");
                }
            } catch (STException ex) {
                STLog.e("Failed to determine the number of bytes per block!");
            }
        }
    }

    ///////////////////////     NDEF methods         ////////////////////////////

    /**
     * Write a NDEF message.
     * @param msg   NDEF Message to write, must not be null
     * @throws STException
     */
    @Override
    public void writeNdefMessage(NDEFMsg msg) throws STException {
        writeNdefMessage(msg, mIso15693Cmd.getFlag());
    }

    /**
     * Write a NDEF message with specified flags.
     * @param msg   NDEF Message to write, must not be null
     * @param flag  Requested flags
     * @throws STException
     */
    public void writeNdefMessage(NDEFMsg msg, byte flag) throws STException {
        // Length of the bytes containing the "type" and "length" of the TLV block containing the NDEF
        int tlSize = 2;
        int terminatorTlvLength = 1;
        int ccfileLength;
        int ccMemorySize;
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

        if (ccfileLength != 0) {
            int ndefLength = 0;
            try {
                ndefLength = msg.getLength();
            } catch (Exception e) {
                e.printStackTrace();
                throw new STException(STException.STExceptionCode.INVALID_NDEF_DATA);
            }

            if ((ccfileLength + tlSize + ndefLength + terminatorTlvLength) <= ccMemorySize)
            {
                if (mNdefMsg != null)
                {
                    mCache.remove(mNdefMsg);
                }
                mNdefCmd.writeNdefMessage((byte) ccfileLength / getBlockSizeInBytes(), msg, flag, mUid);
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
     * @return NDEF Message read from the tag
     * @throws STException
     */
    @Override
    public NDEFMsg readNdefMessage() throws STException {
        return readNdefMessage(mIso15693Cmd.getFlag());
    }

    /**
     * Read the current NdefMessage on tag.
     * @param  flag Requested flags
     * @return NDEF Message read from the tag
     * @throws STException
     */
    public NDEFMsg readNdefMessage(byte flag) throws STException {
        if (mCache.isCacheActivated() && mCache.isCacheValid(mNdefMsg))
            return mNdefMsg.copy();

        int nbOfBlocks  = getCCFileLength() / getBlockSizeInBytes();

        mCache.remove(mNdefMsg);
        mNdefMsg = mNdefCmd.readNdefMessage(nbOfBlocks, flag, mUid);

        if (mNdefMsg != null) {
            mCache.add(mNdefMsg);
            return mNdefMsg;
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
        return readBytes(byteAddress, sizeInBytes, mType5Cmd.getFlag());
    }

    /**
     * Read data with Byte granularity.
     *
     * @param byteAddress
     * @param sizeInBytes
     * @param flag
     * @return
     * @throws STException
     */
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

        return mType5Cmd.readBytes(byteAddress, sizeInBytes, flag, getUid());
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
        writeBytes(byteAddress, data, mIso15693Cmd.getFlag());
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
    public void writeBytes(int byteAddress, byte[] data, byte flag) throws STException {
        if( (byteAddress < 0) || ((byteAddress + data.length) > getMemSizeInBytes()) ) {
            throw new STException(BAD_PARAMETER);
        }

        mType5Cmd.writeBytes(byteAddress, data, flag, getUid());
    }

    ///////////////////////     Raw Data methods (with Block granularity)      ////////////////////////////

    /**
     * Read data with Block granularity
     * @param firstBlockAddress
     * @param sizeInBlocks
     * @return
     * @throws STException
     */
    public ReadBlockResult readBlocks(int firstBlockAddress, int sizeInBlocks) throws STException {
        return mType5Cmd.readBlocks(firstBlockAddress, sizeInBlocks);
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
    public ReadBlockResult readBlocks(int firstBlockAddress, int sizeInBlocks, byte flag) throws STException {
        return mType5Cmd.readBlocks(firstBlockAddress, sizeInBlocks, flag, getUid());
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
    public void writeBlocks(int firstBlockAddress, byte[] data) throws STException {
        mType5Cmd.writeBlocks(firstBlockAddress, data);
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
    public void writeBlocks(int firstBlockAddress, byte[] data, byte flag) throws STException {
        mType5Cmd.writeBlocks(firstBlockAddress, data, flag, getUid());
    }

    ///////////////////////     SysFile methods         ////////////////////////////

    @Override
    public byte[] readSysFile() throws STException {
        return new byte[0];
    }

    ///////////////////////     CCFile methods         ////////////////////////////

    @Override
    public void selectCCFile() throws STException {}

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
    public void initEmptyCCFile() throws STException{
        int memSize = getMemSizeInBytes();
        mCCFile.initEmptyCCFile(memSize);
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

    //////////////////////////////////////////////////////////////////////////////////

    /**
     * Read one Block from tag's EEPROM memory
     * @param blockAddress
     * @return
     * @throws STException
     */
    public byte[] readSingleBlock(byte blockAddress) throws STException {
        return readSingleBlock(blockAddress, mType5Cmd.getFlag());
    }

    /**
     * Read one Block from tag's EEPROM memory
     * @param blockAddress
     * @param flag
     * @return
     * @throws STException
     */
    public byte[] readSingleBlock(byte blockAddress, byte flag) throws STException {
        return mType5Cmd.readSingleBlock(blockAddress, flag, getUid());
    }

    /**
     * Write one Block to tag's EEPROM memory
     * @param blockAddress
     * @param buffer
     * @return
     * @throws STException
     */
    public byte writeSingleBlock(byte blockAddress, byte[] buffer) throws STException {
        return writeSingleBlock(blockAddress, buffer, mType5Cmd.getFlag());
    }

    /**
     * Write one Block to tag's EEPROM memory
     * @param blockAddress
     * @param buffer
     * @param flag
     * @return
     * @throws STException
     */
    public byte writeSingleBlock(byte blockAddress, byte[] buffer, byte flag) throws STException {
        return mType5Cmd.writeSingleBlock(blockAddress, buffer, flag, getUid());
    }

    public byte lockSingleBlock(byte blockAddress) throws STException {
        return lockSingleBlock(blockAddress, mType5Cmd.getFlag());
    }
    public byte lockSingleBlock(byte blockAddress, byte flag) throws STException {
        return mType5Cmd.lockSingleBlock(blockAddress, flag, getUid());
    }

    /**
     * Read Multiple Block from tag's EEPROM memory
     * @param blockAddress Address of the first block to read
     * @param numberOfBlocks The number of blocks in the request is one less than the number of
     *                       blocks that the VICC shall return in its response.
     * @return array of bytes = 1 byte response flag + the content of the selected block (Little endian,
     *          or LSByte first)
     * @throws STException
     */
    public byte[] readMultipleBlock(byte blockAddress, byte numberOfBlocks) throws STException {
        return readMultipleBlock(blockAddress, numberOfBlocks, mType5Cmd.getFlag());
    }

    /**
     * Read Multiple Block from tag's EEPROM memory
     * @param blockAddress Address of the first block to read
     * @param numberOfBlocks The number of blocks in the request is one less than the number of
     *                       blocks that the VICC shall return in its response.
     * @param flag Request flag for the command
     * @return array of bytes = 1 byte response flag + the content of the selected block (Little endian,
     *          or LSByte first)
     * @throws STException
     */
    public byte[] readMultipleBlock(byte blockAddress, byte numberOfBlocks, byte flag) throws STException {
        return mType5Cmd.readMultipleBlock(blockAddress, numberOfBlocks, flag, getUid());
    }

    /**
     * Read one Block from tag's EEPROM memory.
     * This Extended version is able to access to memory addresses bigger than 255 blocks
     * @param blockAddress
     * @return
     * @throws STException
     */
    public byte[] extendedReadSingleBlock(byte[] blockAddress) throws STException {
        return extendedReadSingleBlock(blockAddress, mType5Cmd.getFlag());
    }

    /**
     * Read one Block from tag's EEPROM memory.
     * This Extended version is able to access to memory addresses bigger than 255 blocks
     * @param blockAddress
     * @param flag
     * @return
     * @throws STException
     */
    public byte[] extendedReadSingleBlock(byte[] blockAddress, byte flag) throws STException {
        return mType5Cmd.extendedReadSingleBlock(blockAddress, flag, getUid());
    }

    /**
     * Write one Block to tag's EEPROM memory
     * This Extended version is able to access to memory addresses bigger than 255 blocks
     * @param blockAddress
     * @param buffer
     * @return
     * @throws STException
     */
    public byte extendedWriteSingleBlock(byte[] blockAddress, byte[] buffer) throws STException {
        return extendedWriteSingleBlock(blockAddress, buffer, mType5Cmd.getFlag());
    }

    /**
     * Write one Block to tag's EEPROM memory
     * This Extended version is able to access to memory addresses bigger than 255 blocks
     * @param blockAddress
     * @param buffer
     * @param flag
     * @return
     * @throws STException
     */
    public byte extendedWriteSingleBlock(byte[] blockAddress, byte[] buffer, byte flag) throws STException {
        return mType5Cmd.extendedWriteSingleBlock(blockAddress, buffer, flag, getUid());
    }

    public byte extendedLockSingleBlock(byte[] blockAddress) throws STException {
        return extendedLockSingleBlock(blockAddress, mType5Cmd.getFlag());
    }
    public byte extendedLockSingleBlock(byte[] blockAddress, byte flag) throws STException {
        return mType5Cmd.extendedLockSingleBlock(blockAddress, flag, getUid());
    }

    /**
     * Read some Blocks from tag's EEPROM memory
     * This Extended version is able to access to memory addresses bigger than 255 blocks
     * @param blockAddress
     * @param numberOfBlocks
     * @return
     * @throws STException
     */
    public byte[] extendedReadMultipleBlock(byte[] blockAddress, byte[] numberOfBlocks)throws STException {
        return extendedReadMultipleBlock(blockAddress, numberOfBlocks, mType5Cmd.getFlag());
    }

    /**
     * Read some Blocks from tag's EEPROM memory
     * This Extended version is able to access to memory addresses bigger than 255 blocks
     * @param blockAddress
     * @param numberOfBlocks
     * @param flag
     * @return
     * @throws STException
     */
    public byte[] extendedReadMultipleBlock(byte[] blockAddress, byte[] numberOfBlocks, byte flag)throws STException {
        return mType5Cmd.extendedReadMultipleBlock(blockAddress, numberOfBlocks, flag, getUid());
    }

    public byte select() throws STException {
        return select(mType5Cmd.getFlag());
    }
    public byte select(byte flag) throws STException {
        return mType5Cmd.select(flag, getUid());
    }

    public byte stayQuiet() throws STException {
        return stayQuiet(mType5Cmd.getFlag());
    }
    public byte stayQuiet(byte flag) throws STException {
        return mType5Cmd.stayQuiet(flag, getUid());
    }

    @Override
    public int getSysFileLength() throws STException {
        return 0;
    }

    public byte getDSFID() throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    public byte getAFI() throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public byte getICRef() throws STException {
        return 0;
    }

    @Override
    public int getMemSizeInBytes() throws STException {
        return 0;
    }

    /**
     * Number of Bytes per block
     * @return
     */
    public int getBlockSizeInBytes() {
        return mBlockSizeInBytes;
    }

    /**
     * Number of blocks in tag's EEPROM
     * @return
     * @throws STException
     */
    public int getNumberOfBlocks() throws STException {
        // This information is only available for STType5Tag because it is taken from the SysFile.
        throw new STException(STException.STExceptionCode.NOT_SUPPORTED);
    }


    @Override
    public void selectSysFile() throws STException {

    }


    /**
     * @return the max value returned by RF command ReadMultipleBlocks
     */
    public int getMaxReadMultipleBlocksReturned() {
        return mMaxReadMultipleBlocksReturned;
    }

    /**
     * @param maxReadMultipleBlocksReturned the max value of ReadMultipleBlocks to set
     */
    public void setMaxReadMultipleBlocksReturned(int maxReadMultipleBlocksReturned) {
        mMaxReadMultipleBlocksReturned = maxReadMultipleBlocksReturned;
    }

    /**
     * Invalidate all cache objects member of the class
     */
    @Override
    public void invalidateCache() {
        mCache.invalidateCache();
    }

    @Override
    public void validateCache() {
        mCache.validateCache();
    }

    @Override
    public void activateCache() {
        mCache.activateCache();
    }

    @Override
    public void deactivateCache() {
        mCache.deactivateCache();
    }

    @Override
    public boolean isCacheValid() {
        return mCache.isCacheValid();
    }

    @Override
    public boolean isCacheActivated() {
        return mCache.isCacheActivated();
    }

    @Override
    public void updateCache() throws STException {mCache.updateCache(); }
}
