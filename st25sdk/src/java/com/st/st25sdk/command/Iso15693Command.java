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

import com.st.st25sdk.Helper;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.type5.Type5Tag;

public class Iso15693Command extends Iso15693Protocol implements Iso15693CommandInterface {

    public Iso15693Command(RFReaderInterface reader, byte[] uid) {
        this(reader, uid, DEFAULT_FLAG, Type5Tag.DEFAULT_NBR_OF_BYTES_PER_BLOCK);
    }

    public Iso15693Command(RFReaderInterface reader, byte[] uid, byte flag) {
        this(reader, uid, flag, Type5Tag.DEFAULT_NBR_OF_BYTES_PER_BLOCK);
    }

    public Iso15693Command(RFReaderInterface reader, byte[] uid, int nbrOfBytesPerBlock) {
        this(reader, uid, DEFAULT_FLAG, nbrOfBytesPerBlock);
    }

    public Iso15693Command(RFReaderInterface reader, byte[] uid, byte flag, int nbrOfBytesPerBlock) {
        super(reader, uid, flag, nbrOfBytesPerBlock);
    }

    public static final byte ISO15693_CMD_INVENTORY = (byte) 0x01;
    public static final byte ISO15693_CMD_STAY_QUIET = (byte) 0x02;
    public static final byte ISO15693_CMD_READ_SINGLE_BLOCK = (byte) 0x20;
    public static final byte ISO15693_CMD_WRITE_SINGLE_BLOCK = (byte) 0x21;
    public static final byte ISO15693_CMD_LOCK_BLOCK = (byte) 0x22;
    public static final byte ISO15693_CMD_READ_MULTIPLE_BLOCK = (byte) 0x23;
    public static final byte ISO15693_CMD_WRITE_MULTIPLE_BLOCK = (byte) 0x24;
    public static final byte ISO15693_CMD_SELECT = (byte) 0x25;
    public static final byte ISO15693_CMD_RESET_TO_READY = (byte) 0x26;
    public static final byte ISO15693_CMD_WRITE_AFI = (byte) 0x27;
    public static final byte ISO15693_CMD_LOCK_AFI = (byte) 0x28;
    public static final byte ISO15693_CMD_WRITE_DSFID = (byte) 0x29;
    public static final byte ISO15693_CMD_LOCK_DSFID = (byte) 0x2A;
    public static final byte ISO15693_CMD_GET_SYSTEM_INFO = (byte) 0x2B;
    public static final byte ISO15693_CMD_GET_MULTIPLE_BLOCK_SEC_STATUS = (byte) 0x2C;
    public static final byte ISO15693_CMD_EXTENDED_READ_SINGLE_BLOCK = (byte) 0x30;
    public static final byte ISO15693_CMD_EXTENDED_WRITE_SINGLE_BLOCK = (byte) 0x31;
    public static final byte ISO15693_CMD_EXTENDED_LOCK_SINGLE_BLOCK = (byte) 0x32;
    public static final byte ISO15693_CMD_EXTENDED_READ_MULTIPLE_BLOCK = (byte) 0x33;
    public static final byte ISO15693_CMD_EXTENDED_WRITE_MULTIPLE_BLOCK = (byte) 0x34;
    public static final byte ISO15693_CMD_EXTENDED_GET_SYSTEM_INFO = (byte) 0x3B;
    public static final byte ISO15693_CMD_EXTENDED_GET_MULTIPLE_BLOCK_SEC_STATUS = (byte) 0x3C;

    /********************* Implementation of ISO15693 commands *********************/

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] inventory(byte flag) throws STException {
        /* Default inventory configuration is:
         *    - Mask length of zero (so mask value doesn't matter)
         *    - No AFI field
         */
        return inventory(flag, (byte) 0x00, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] inventory(byte flag, byte maskLength, byte[] maskValue) throws STException {
        return buildInventoryFrame(flag, maskLength, maskValue, false, (byte) 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] inventory(byte flag, byte maskLength, byte[] maskValue, byte afiField) throws STException {
        return buildInventoryFrame(flag, maskLength, maskValue, true, afiField);
    }

    private byte[] buildInventoryFrame(byte flag, byte maskLength, byte[] maskValue, boolean isAFISet, byte afiField) throws STException {
        byte[] frame;
        int frameOptionSize = 0;
        int index = 0;

        if (maskLength != 0 && maskValue != null) {
            // Compute frame size based on the maskValue length passed a parameter
            frameOptionSize += maskValue.length;
        }

        if (isAFISet) {
            // Optional field
            frameOptionSize++;
        }

        // Frame size = header (flag + cmd) + maskLength + afi (optional) + mask (if maskLength != 0)
        frame = new byte[ISO15693_HEADER_SIZE + 1 + frameOptionSize];

        frame[index++] = flag;
        frame[index++] = ISO15693_CMD_INVENTORY;
        if (isAFISet) {
            frame[index++] = afiField;
        }
        frame[index++] = maskLength;

        if (maskLength != 0 && maskValue != null) {
            // Value is Little Endian
            maskValue = Helper.reverseByteArray(maskValue);
            System.arraycopy(maskValue, 0, frame, index, maskValue.length);
        }

        return transceive("inventory", frame);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte stayQuiet() throws STException {
        return stayQuiet(mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte stayQuiet(byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] frame;
        int headerSize;

        headerSize = ISO15693_HEADER_SIZE_UID;
        frame = new byte[headerSize];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_STAY_QUIET;

        // UID is mandatory in stayQuiet command
        addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        response = transceive("stayQuiet", frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public  byte[] readSingleBlock(byte blockAddress) throws STException {
        return readSingleBlock(blockAddress, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public  byte[] readSingleBlock(byte blockAddress, byte flag, byte[] uid) throws STException
    {
        byte[] frame;
        int headerSize;

        headerSize = getIso15693HeaderSize(flag);

        frame = new byte[headerSize + 1];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_READ_SINGLE_BLOCK;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        frame[headerSize] = blockAddress;

        return transceive("readSingleBlock", frame);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeSingleBlock(byte blockAddress, byte[] buffer) throws STException {
        return writeSingleBlock(blockAddress, buffer, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeSingleBlock(byte blockAddress, byte[] buffer, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] frame;
        int headerSize;

        if (buffer == null) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize + 1 + buffer.length];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_WRITE_SINGLE_BLOCK;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);


        frame[headerSize] = blockAddress;

        System.arraycopy(buffer, 0, frame, headerSize + 1, buffer.length);

        response = transceive("writeSingleBlock", frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte lockBlock(byte blockAddress) throws STException {
        return lockBlock(blockAddress, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte lockBlock(byte blockAddress, byte flag, byte[] uid)  throws STException {
        byte[] response;
        byte[] frame;
        int headerSize;

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize + 1];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_LOCK_BLOCK;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        frame[headerSize] = blockAddress;

        response = transceive("lockBlock", frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readMultipleBlock(byte blockAddress, byte nbrOfBlocks) throws STException {
        return readMultipleBlock(blockAddress, nbrOfBlocks, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readMultipleBlock(byte blockAddress, byte nbrOfBlocks, byte flag, byte[] uid) throws STException {
        byte[] frame;
        int headerSize;

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize + 1 + 1];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_READ_MULTIPLE_BLOCK;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        frame[headerSize] = blockAddress;
        frame[headerSize + 1] = nbrOfBlocks;

        return transceive("readMultipleBlock", frame);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeMultipleBlock(byte blockAddress, byte nbrOfBlocks, byte[] buffer) throws STException {
        return writeMultipleBlock(blockAddress, nbrOfBlocks, buffer, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeMultipleBlock(byte blockAddress, byte nbrOfBlocks, byte[] buffer, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] frame;
        int headerSize;

        if (buffer == null) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize + 2 + buffer.length];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_WRITE_MULTIPLE_BLOCK;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        frame[headerSize] = blockAddress;
        frame[headerSize + 1] = nbrOfBlocks;

        System.arraycopy(buffer, 0, frame, headerSize + 2, buffer.length);

        response = transceive("writeMultipleBlock", frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte select() throws STException {
        return select(mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte select(byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] frame;
        int headerSize;

        headerSize = ISO15693_HEADER_SIZE_UID;
        frame = new byte[headerSize];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_SELECT;

        // UID is mandatory in select command
        addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        response = transceive("select", frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte resetToReady() throws STException {
        return resetToReady(mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte resetToReady(byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] frame;
        int headerSize;

        headerSize = getIso15693HeaderSize(flag);

        frame = new byte[headerSize];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_RESET_TO_READY;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        response = transceive("resetToReady", frame);
        return response[0];
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeAFI(byte value) throws STException {
        return writeAFI(value, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeAFI(byte value, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] frame;
        int headerSize;

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize + 1];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_WRITE_AFI;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        frame[headerSize] = value;

        response = transceive("writeAFI", frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte lockAFI() throws STException {
        return lockAFI(mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte lockAFI(byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] frame;
        int headerSize;

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_LOCK_AFI;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        response = transceive("lockAFI", frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeDSFID(byte value) throws STException {
        return writeDSFID(value, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeDSFID(byte value, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] frame;
        int headerSize;

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize + 1];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_WRITE_DSFID;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        frame[headerSize] = value;

        response = transceive("writeDSFID", frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte lockDSFID() throws STException {
        return lockDSFID(mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte lockDSFID(byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] frame;
        int headerSize;

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_LOCK_DSFID;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        response = transceive("lockDSFID", frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getSystemInfo() throws STException {
        return getSystemInfo(mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getSystemInfo(byte flag, byte[] uid) throws STException {
        byte[] frame;
        int headerSize;

        headerSize = getIso15693HeaderSize(flag);

        frame = new byte[headerSize];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_GET_SYSTEM_INFO;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        return transceive("getSystemInfo", frame);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMultipleBlockSecStatus(byte firstBlock, byte nbOfBlocks) throws STException {
        return getMultipleBlockSecStatus(firstBlock, nbOfBlocks, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMultipleBlockSecStatus(byte firstBlock, byte nbOfBlocks, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        headerSize = getIso15693HeaderSize(flag);

        /* first block 1 byte
         * nbOfBlocks  1 byte
         */
        request = new byte[headerSize + 1 + 1];

        request[0] = flag;
        request[1] = ISO15693_CMD_GET_MULTIPLE_BLOCK_SEC_STATUS;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_UID_OFFSET, uid);

        request[headerSize] = firstBlock;
        request[headerSize + 1] = nbOfBlocks;

        return transceive("getMultipleBlockSecStatus", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] extendedReadSingleBlock(byte[] blockAddress) throws STException {
        return extendedReadSingleBlock(blockAddress, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] extendedReadSingleBlock(byte[] blockAddress, byte flag, byte[] uid)  throws STException {
        byte[] frame;
        int headerSize;

        if ((blockAddress == null) || (blockAddress.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize + 2];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_EXTENDED_READ_SINGLE_BLOCK;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        frame[headerSize] = blockAddress[1];        // LSByte
        frame[headerSize + 1] = blockAddress[0];    // MSByte

        return transceive("extendedReadSingleBlock", frame);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte extendedLockSingleBlock(byte[] blockAddress) throws STException {
        return extendedLockSingleBlock(blockAddress, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte extendedLockSingleBlock(byte[] blockAddress, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] frame;
        int headerSize;

        if ((blockAddress == null) || (blockAddress.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize + 2];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_EXTENDED_LOCK_SINGLE_BLOCK;
        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        frame[headerSize] = blockAddress[1];        // LSByte
        frame[headerSize + 1] = blockAddress[0];    // MSByte

        response = transceive("extendedLockSingleBlock", frame);
        return response[0];
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte extendedWriteSingleBlock(byte[] blockAddress, byte[] buffer) throws STException {
        return extendedWriteSingleBlock(blockAddress, buffer, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte extendedWriteSingleBlock(byte[] blockAddress, byte[] buffer, byte flag, byte[] uid) throws STException{
        byte[] response;
        byte[] frame;
        int headerSize;

        if ((buffer == null) || (blockAddress == null) || (blockAddress.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize + 2 + buffer.length];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_EXTENDED_WRITE_SINGLE_BLOCK;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        frame[headerSize] = blockAddress[1];        // LSByte
        frame[headerSize + 1] = blockAddress[0];    // MSByte

        System.arraycopy(buffer, 0, frame, headerSize + 2, buffer.length);

        response = transceive("extendedWriteSingleBlock", frame);
        return response[0];
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] extendedReadMultipleBlock(byte[] blockAddress, byte[] nbrOfBlocks) throws STException {
        return extendedReadMultipleBlock(blockAddress, nbrOfBlocks, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] extendedReadMultipleBlock(byte[] blockAddress, byte[] nbrOfBlocks, byte flag, byte[] uid) throws STException {
        byte[] frame;
        int headerSize;

        if ((blockAddress == null) || (blockAddress.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        if ((nbrOfBlocks == null) || (nbrOfBlocks.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize + blockAddress.length + nbrOfBlocks.length];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_EXTENDED_READ_MULTIPLE_BLOCK;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        frame[headerSize] = blockAddress[1];        // LSByte
        frame[headerSize + 1] = blockAddress[0];    // MSByte
        frame[headerSize + 2] = nbrOfBlocks[1];     // LSByte
        frame[headerSize + 3] = nbrOfBlocks[0];     // MSByte

        return transceive("extendedReadMultipleBlock", frame);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte extendedWriteMultipleBlock(byte[] blockAddress, byte[] nbrOfBlocks, byte[] buffer) throws STException {
        return extendedWriteMultipleBlock(blockAddress, nbrOfBlocks, buffer, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte extendedWriteMultipleBlock(byte[] blockAddress, byte[] nbrOfBlocks, byte[] buffer, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] frame;
        int headerSize;

        if ((blockAddress == null) || (blockAddress.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        if ((nbrOfBlocks == null) || (nbrOfBlocks.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize + blockAddress.length + nbrOfBlocks.length + buffer.length];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_EXTENDED_WRITE_MULTIPLE_BLOCK;

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET, uid);

        frame[headerSize] = blockAddress[1];        // LSByte
        frame[headerSize + 1] = blockAddress[0];    // MSByte
        frame[headerSize + 2] = nbrOfBlocks[1];     // LSByte
        frame[headerSize + 3] = nbrOfBlocks[0];     // MSByte

        System.arraycopy(buffer, 0, frame, headerSize + 4, buffer.length);

        response = transceive("extendedWriteMultipleBlock", frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] extendedGetSystemInfo() throws STException {
        // Hard-coded request of all parameters by default
        return this.extendedGetSystemInfo((byte) 0x7F);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] extendedGetSystemInfo(byte parameters) throws STException {
        return this.extendedGetSystemInfo(parameters, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] extendedGetSystemInfo(byte parameters, byte flag, byte[] uid) throws STException {
        byte[] frame;
        int headerSize;

        headerSize = getIso15693HeaderSize(flag);
        frame = new byte[headerSize + 1];

        frame[0] = flag;
        frame[1] = ISO15693_CMD_EXTENDED_GET_SYSTEM_INFO;
        frame[2] = parameters; // Parameter request field

        if (uidNeeded(flag))
            addUidToFrame(frame, ISO15693_UID_OFFSET + 1, uid);

        return transceive("extendedGetSystemInfo", frame);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] extendedGetMultipleBlockSecStatus(byte[] firstBlock, byte[] nbOfBlocks) throws STException {
        return extendedGetMultipleBlockSecStatus(firstBlock, nbOfBlocks, mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] extendedGetMultipleBlockSecStatus(byte[] firstBlock, byte[] nbrOfBlocks, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        if ((firstBlock == null) || (firstBlock.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        if ((nbrOfBlocks == null) || (nbrOfBlocks.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693HeaderSize(flag);

        /* first block 1 byte
         * nbOfBlocks  1 byte
         */
        request = new byte[headerSize + 2 + 2];

        request[0] = flag;
        request[1] = ISO15693_CMD_EXTENDED_GET_MULTIPLE_BLOCK_SEC_STATUS;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_UID_OFFSET, uid);

        request[headerSize]     = firstBlock[1];
        request[headerSize + 1] = firstBlock[0];
        request[headerSize + 2] = nbrOfBlocks[1];
        request[headerSize + 3] = nbrOfBlocks[0];

        return transceive("extendedGetMultipleBlockSecStatus", request);
    }

}
