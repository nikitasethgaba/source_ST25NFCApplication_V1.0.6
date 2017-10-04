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

import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;

import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.type5.Type5Tag;


public class VicinityCommand extends Iso15693Protocol implements VicinityCommandInterface {

    public VicinityCommand(RFReaderInterface reader, byte[] uid) {
        this(reader, uid, Iso15693Protocol.DEFAULT_VICINITY_FLAG, Type5Tag.DEFAULT_NBR_OF_BYTES_PER_BLOCK);
    }

    public VicinityCommand(RFReaderInterface reader, byte[] uid, byte flag) {
        this(reader, uid, flag, Type5Tag.DEFAULT_NBR_OF_BYTES_PER_BLOCK);
    }

    public VicinityCommand(RFReaderInterface reader, byte[] uid, int nbrOfBytesPerBlock) {
        this(reader, uid, Iso15693Protocol.DEFAULT_VICINITY_FLAG, nbrOfBytesPerBlock);
    }

    public VicinityCommand(RFReaderInterface reader, byte[] uid, byte flag, int nbrOfBytesPerBlock) {
        super(reader, uid, flag, nbrOfBytesPerBlock);

        if((flag & PROTOCOL_FORMAT_EXTENSION) == 0) {
            STLog.e("Error! Flag PROTOCOL_FORMAT_EXTENSION is mandatory for this class");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readSingleBlock(byte[] blockAddress) throws STException {
        return readSingleBlock(blockAddress, getFlag(), getUid());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readSingleBlock(byte[] blockAddress, byte flag, byte[] uid) throws STException
    {
        byte[] request;
        int header_size;

        if ((blockAddress == null) || (blockAddress.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        header_size = getIso15693HeaderSize(flag);

        //header size + 2 bytes addr
        request = new byte[header_size + 2];

        request[0] = flag;
        request[1] = Iso15693Command.ISO15693_CMD_READ_SINGLE_BLOCK;
        request[header_size] = blockAddress[1];
        request[header_size + 1] = blockAddress[0];

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_UID_OFFSET, uid);

        return transceive("readSingleBlockVicinity", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeSingleBlock(byte[] blockAddress, byte[] buffer) throws STException {
        return writeSingleBlock(blockAddress, buffer, getFlag(), getUid());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeSingleBlock(byte[] blockAddress, byte[] buffer, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int header_size;

        if ((buffer == null) || (blockAddress == null) || (blockAddress.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        header_size = getIso15693HeaderSize(flag);

        //header size + 2 bytes addr + buffer length
        request = new byte[header_size + 2 + buffer.length];

        request[0] = flag;
        request[1] = Iso15693Command.ISO15693_CMD_WRITE_SINGLE_BLOCK;
        request[header_size] = blockAddress[1];
        request[header_size + 1] = blockAddress[0];

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_UID_OFFSET, uid);

        System.arraycopy(buffer, 0, request, header_size + 2, buffer.length);

        response = transceive("writeSingleBlockVicinity", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readMultipleBlock(byte[] blockAddress, byte nbrOfBlocks) throws STException {
        return readMultipleBlock(blockAddress, nbrOfBlocks, getFlag(), getUid());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readMultipleBlock(byte[] blockAddress, byte nbrOfBlocks, byte flag, byte[] uid) throws STException {
        byte[] request;
        int header_size;

        if ((blockAddress == null) || (blockAddress.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        header_size = getIso15693HeaderSize(flag);

        //header size + 2 bytes addr + 1 byte pos
        request = new byte[header_size + 2 + 1];

        request[0] = flag;
        request[1] = Iso15693Command.ISO15693_CMD_READ_MULTIPLE_BLOCK;
        request[header_size]     = blockAddress[1];
        request[header_size + 1] = blockAddress[0];
        request[header_size + 2] = nbrOfBlocks;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_UID_OFFSET, uid);

        return transceive("readMultipleBlockVicinity", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getSystemInfo() throws STException {
        return getSystemInfo(getFlag(), getUid());
    }

    public byte[] getSystemInfo(byte flag, byte[] uid) throws STException {
        byte[] request;
        int header_size;

        header_size = getIso15693HeaderSize(flag);

        request = new byte[header_size];

        request[0] = flag;
        request[1] = Iso15693Command.ISO15693_CMD_GET_SYSTEM_INFO;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_UID_OFFSET, uid);

        return transceive("getSystemInfoVicinity", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte lockSector(byte[] blockAddress, byte securityStatus) throws STException {
        return lockSector(blockAddress, securityStatus, getFlag(), getUid());
    }

    public byte lockSector(byte[] blockAddress, byte securityStatus, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int header_size;

        if ((blockAddress == null) || (blockAddress.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        header_size = getIso15693CustomHeaderSize(flag);

        /* sector number 2 bytes
         * security status 1 byte
         */
        request = new byte[header_size + 2 + 1];

        request[0] = flag;
        request[1] = Iso15693CustomCommand.ISO15693_CUSTOM_ST_CMD_LOCK_SECTOR;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_UID_OFFSET, uid);

        request[header_size] = blockAddress[1];
        request[header_size + 1] = blockAddress[0];
        request[header_size + 2] = securityStatus;

        response = transceive("lockSector", request);
        return response[0];

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMultipleBlockSecStatus(byte[] firstBlock, byte[] nbOfBlocks) throws STException {
        return getMultipleBlockSecStatus(firstBlock, nbOfBlocks, getFlag(), getUid());
    }

    public byte[] getMultipleBlockSecStatus(byte[] firstBlock, byte[] nbrOfBlocks, byte flag, byte[] uid) throws STException {
        byte[] request;
        int header_size;

        if ((firstBlock == null) || (firstBlock.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        if ((nbrOfBlocks == null) || (nbrOfBlocks.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        header_size = getIso15693HeaderSize(flag);

        /* first block 2 bytes
         * nbOfBlocks  2 bytes
         */
        request = new byte[header_size + 2 + 2];
        request[0] = flag;
        request[1] =  Iso15693Command.ISO15693_CMD_GET_MULTIPLE_BLOCK_SEC_STATUS;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_UID_OFFSET, uid);

        request[header_size]     = firstBlock[1];
        request[header_size + 1] = firstBlock[0];
        request[header_size + 2] = nbrOfBlocks[1];
        request[header_size + 3] = nbrOfBlocks[0];

        return transceive("getMultipleBlockSecStatus", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastReadSingleBlock(byte blockOffset) throws STException {
        byte[] blockAddress = new byte[2];
        blockAddress[0] = blockOffset;
        blockAddress[1] = 0x00;
        return fastReadSingleBlock(blockAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastReadSingleBlock(byte[] blockAddress) throws STException {
        return fastReadSingleBlock(blockAddress, getFlag(), getUid());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastReadSingleBlock(byte[] blockAddress, byte flag, byte[] uid) throws STException {
        byte[] request;
        int header_size;

        if ((blockAddress == null) || (blockAddress.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        header_size = getIso15693CustomHeaderSize(flag);

        //header size + 2 bytes addr
        request = new byte[header_size + 2];

        request[0] = flag;
        request[1] = Iso15693CustomCommand.ISO15693_CUSTOM_ST_CMD_FAST_READ_SINGLE_BLOCK;
        request[2] = STM_MANUFACTURER_CODE;
        request[header_size] = blockAddress[1];
        request[header_size + 1] = blockAddress[0];

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_UID_OFFSET, uid);

        return transceive("fastReadSingleBlockVicinity", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastReadMultipleBlock(byte blockOffset, byte nbrOfBlocks) throws STException {
        byte[] blockAddress = new byte[2];
        blockAddress[0] = blockOffset;
        blockAddress[1] = 0x00;
        return fastReadMultipleBlock(blockAddress, nbrOfBlocks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastReadMultipleBlock(byte[] blockAddress, byte nbrOfBlocks) throws STException {
        return fastReadMultipleBlock(blockAddress, nbrOfBlocks, getFlag(), getUid());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastReadMultipleBlock(byte[] blockAddress, byte nbrOfBlocks, byte flag, byte[] uid) throws STException {
        byte[] request;
        int header_size;

        if ((blockAddress == null) || (blockAddress.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        header_size = getIso15693CustomHeaderSize(flag);

        //header size + 2 bytes addr + 1 byte pos
        request = new byte[header_size + 2 + 1];

        request[0] = flag;
        request[1] = Iso15693CustomCommand.ISO15693_CUSTOM_ST_CMD_FAST_READ_MULTIPLE_BLOCK;
        request[2] = STM_MANUFACTURER_CODE;
        request[header_size]     = blockAddress[1];
        request[header_size + 1] = blockAddress[0];
        request[header_size + 2] = nbrOfBlocks;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_UID_OFFSET, uid);

        return transceive("fastReadMultipleBlockVicinity", request);
    }

}
