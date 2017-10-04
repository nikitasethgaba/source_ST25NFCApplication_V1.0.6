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

package com.st.st25sdk.command;

import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.type5.ReadBlockResult;
import com.st.st25sdk.type5.Type5Tag;

public class Type5Command extends Iso15693Protocol implements Type5CommandInterface {

    protected Iso15693Command mIso15693Cmd;
    private Type5MemoryCommand mType5MemoryCommand;

    static final boolean DBG = true;

    public Type5Command(RFReaderInterface reader, byte[] uid) {
        this(reader, uid, Iso15693Protocol.DEFAULT_FLAG, Type5Tag.DEFAULT_NBR_OF_BYTES_PER_BLOCK);
    }

    public Type5Command(RFReaderInterface reader, byte[] uid, byte flag) {
        this(reader, uid, flag, Type5Tag.DEFAULT_NBR_OF_BYTES_PER_BLOCK);
    }

    public Type5Command(RFReaderInterface reader, byte[] uid, int nbrOfBytesPerBlock) {
        this(reader, uid, Iso15693Protocol.DEFAULT_FLAG, nbrOfBytesPerBlock);
    }

    public Type5Command(RFReaderInterface reader, byte[] uid, byte flag, int nbrOfBytesPerBlock) {
        super(reader, uid, flag, nbrOfBytesPerBlock);

        mIso15693Cmd = new Iso15693Command(reader, uid, flag, nbrOfBytesPerBlock);
        mType5MemoryCommand = new Type5MemoryCommand(reader, uid, flag, nbrOfBytesPerBlock);
    }

    /*
     * @param flag
     */
    @Override
    public void setFlag(byte flag) {
        mFlag = flag;
        mIso15693Cmd.setFlag(flag);
        mType5MemoryCommand.setFlag(flag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public  byte[] readSingleBlock(byte blockAddress, byte flag, byte[] uid) throws STException {
        return mIso15693Cmd.readSingleBlock(blockAddress, flag, uid);
    }

    /**
     * Read data with Block granularity
     *
     * WARNING: In case of read error, the command will return what has been read so ReadBlockResult
     *          may contain less bytes than requested.
     *
     * @param firstBlockAddress
     * @param sizeInBlocks
     * @return
     * @throws STException
     */
    public ReadBlockResult readBlocks(int firstBlockAddress, int sizeInBlocks) throws STException {
        return mType5MemoryCommand.readBlocks(firstBlockAddress, sizeInBlocks, mFlag, mUid);
    }

    /**
     * Read data with Block granularity
     *
     * WARNING: In case of read error, the command will return what has been read so ReadBlockResult
     *          may contain less bytes than requested.
     *
     * @param firstBlockAddress
     * @param sizeInBlocks
     * @param flag
     * @param uid
     * @return
     * @throws STException
     */
    public ReadBlockResult readBlocks(int firstBlockAddress, int sizeInBlocks, byte flag, byte[] uid) throws STException {
        return mType5MemoryCommand.readBlocks(firstBlockAddress, sizeInBlocks, flag, uid);
    }

    /**
     * Read data with Byte granularity
     *
     * WARNING: In case of read error, the command will return what has been read so the byte array
     *          may contain less bytes than requested.
     *
     * @param byteAddress
     * @param sizeInBytes
     * @return
     * @throws STException
     */
    public byte[] readBytes(int byteAddress, int sizeInBytes) throws STException {
        return mType5MemoryCommand.readBytes(byteAddress, sizeInBytes, mFlag, mUid);
    }

    /**
     * Read data with Byte granularity
     *
     * WARNING: In case of read error, the command will return what has been read so the byte array
     *          may contain less bytes than requested.
     *
     * @param byteAddress
     * @param sizeInBytes
     * @param flag
     * @param uid
     * @return
     * @throws STException
     */
    public byte[] readBytes(int byteAddress, int sizeInBytes, byte flag, byte[] uid) throws STException {
        return mType5MemoryCommand.readBytes(byteAddress, sizeInBytes, flag, uid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeSingleBlock(byte blockAddress, byte[] buffer, byte flag, byte[] uid) throws STException {
        return mIso15693Cmd.writeSingleBlock(blockAddress, buffer, flag, uid);
    }

    public byte writeSingleBlock(byte blockAddress, byte[] buffer) throws STException {
        return mIso15693Cmd.writeSingleBlock(blockAddress, buffer, mFlag, mUid);
    }

    /**
     * Write data with Block granularity
     * @param firstBlockAddress
     * @param buffer
     * @throws STException
     */
    public void writeBlocks(int firstBlockAddress, byte[] buffer) throws STException {
        mType5MemoryCommand.writeBlocks(firstBlockAddress, buffer, mFlag, mUid);
    }

    /**
     * Write data with Block granularity
     * @param firstBlockAddress
     * @param buffer
     * @param flag
     * @param uid
     * @throws STException
     */
    public void writeBlocks(int firstBlockAddress, byte[] buffer, byte flag, byte[] uid) throws STException {
        mType5MemoryCommand.writeBlocks(firstBlockAddress, buffer, flag, uid);
    }

    /**
     * Write data with Byte granularity
     * @param byteAddress
     * @param buffer
     * @throws STException
     */
    public void writeBytes(int byteAddress, byte[] buffer) throws STException {
        mType5MemoryCommand.writeBytes(byteAddress, buffer, mFlag, mUid);
    }

    /**
     * Write data with Byte granularity
     * @param byteAddress
     * @param buffer
     * @param flag
     * @param uid
     * @throws STException
     */
    public void writeBytes(int byteAddress, byte[] buffer, byte flag, byte[] uid) throws STException {
        mType5MemoryCommand.writeBytes(byteAddress, buffer, flag, uid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte lockSingleBlock(byte blockAddress, byte flag, byte[] uid)  throws STException {
        return mIso15693Cmd.lockBlock(blockAddress, flag, uid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readMultipleBlock(byte blockAddress, byte nbrOfBlocks, byte flag, byte[] uid) throws STException {
        return mIso15693Cmd.readMultipleBlock(blockAddress, nbrOfBlocks, flag, uid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] extendedReadSingleBlock(byte[] blockAddress, byte flag, byte[] uid)  throws STException {
        return mIso15693Cmd.extendedReadSingleBlock(blockAddress, flag, uid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte extendedWriteSingleBlock(byte[] blockAddress, byte[] buffer, byte flag, byte[] uid) throws STException{
        return mIso15693Cmd.extendedWriteSingleBlock(blockAddress, buffer, flag, uid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte extendedLockSingleBlock(byte[] blockAddress, byte flag, byte[] uid) throws STException {
        return mIso15693Cmd.extendedLockSingleBlock(blockAddress, flag, uid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] extendedReadMultipleBlock(byte[] blockAddress, byte[] nbrOfBlocks, byte flag, byte[] uid) throws STException {
        return mIso15693Cmd.extendedReadMultipleBlock(blockAddress, nbrOfBlocks, flag, uid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte select(byte flag, byte[] uid) throws STException {
        return mIso15693Cmd.select(flag, uid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte stayQuiet(byte flag, byte[] uid) throws STException {
        return mIso15693Cmd.stayQuiet(flag, uid);
    }

}
