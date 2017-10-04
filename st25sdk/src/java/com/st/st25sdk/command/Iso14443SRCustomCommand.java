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

import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STException.STExceptionCode;
import com.st.st25sdk.iso14443sr.STIso14443SRTag;


public class Iso14443SRCustomCommand implements Iso14443SRCustomCommandInterface {

    protected RFReaderInterface mReaderInterface;

    protected static final int ISO14443SR_CUSTOM_CMD_INITIATE = 0x0600;
    protected static final int ISO14443SR_CUSTOM_CMD_PCALL16 = 0x0604;
    protected static final int ISO14443SR_CUSTOM_CMD_SLOT_MARKER = 0x06;
    protected static final int ISO14443SR_CUSTOM_CMD_SELECT = 0x0E;
    protected static final int ISO14443SR_CUSTOM_CMD_COMPLETION = 0x0F;
    protected static final int ISO14443SR_CUSTOM_CMD_RESET_TO_INVENTORY = 0x0C;

    protected static final int ISO14443SR_CUSTOM_CMD_READ_BLOCK = 0x08;
    protected static final int ISO14443SR_CUSTOM_CMD_WRITE_BLOCK = 0x09;
    protected static final int ISO14443SR_CUSTOM_CMD_GET_UID = 0x0B;

    private int mNbrOfBytesPerBlock;


    public Iso14443SRCustomCommand(RFReaderInterface reader) {
        this(reader, STIso14443SRTag.DEFAULT_NBR_OF_BYTES_PER_BLOCK);
    }

    public Iso14443SRCustomCommand(RFReaderInterface reader, int nbrOfBytesPerBlock) {
        mReaderInterface = reader;
        mNbrOfBytesPerBlock = nbrOfBytesPerBlock;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte initiate() throws STException {
        byte[] frame;
        byte[] response;
        frame = new byte[2];

        frame[0] = (ISO14443SR_CUSTOM_CMD_INITIATE & 0xFF00) >> 8;
        frame[1] = ISO14443SR_CUSTOM_CMD_INITIATE & 0x00FF;

        response = transceive("sr_Initiate", frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte pCall16() throws STException {
        byte[] frame;
        byte[] response;
        frame = new byte[2];

        frame[0] = (ISO14443SR_CUSTOM_CMD_PCALL16 & 0xFF00) >> 8;
        frame[1] = ISO14443SR_CUSTOM_CMD_PCALL16 & 0x00FF;

        response = transceive("sr_Pcall16", frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte slotMarker(byte slotNumber) throws STException {
        byte[] frame;
        byte[] response;
        frame = new byte[1];
        byte slotNumberValue = (byte) (slotNumber & 0x0F);

        // slotNumber must appear in the 4 most significant bits of the command
        frame[0] = (byte) (ISO14443SR_CUSTOM_CMD_SLOT_MARKER | slotNumberValue << 4);

        response = transceive("sr_slotMarker" + Integer.toString(slotNumberValue), frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte select(byte chipID) throws STException {
        byte[] frame;
        byte[] response;
        frame = new byte[2];

        frame[0] = ISO14443SR_CUSTOM_CMD_SELECT;
        frame[1] = chipID;

        response = transceive("sr_Select", frame);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completion() throws STException {
        byte[] frame;
        frame = new byte[1];

        frame[0] = ISO14443SR_CUSTOM_CMD_COMPLETION;

        transceive("sr_Completion", frame);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetToInventory() throws STException {
        byte[] frame;
        frame = new byte[1];

        frame[0] = ISO14443SR_CUSTOM_CMD_RESET_TO_INVENTORY;

        transceive("sr_Reset_to_inventory", frame);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getUid() throws STException {
        byte[] frame;
        frame = new byte[1];

        frame[0] = ISO14443SR_CUSTOM_CMD_GET_UID;

        return transceive("sr_getUID", frame);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readBlock(byte blockAddress) throws STException {
        byte[] frame;
        frame = new byte[2];

        frame[0] = ISO14443SR_CUSTOM_CMD_READ_BLOCK;
        frame[1] = blockAddress;
        return transceive("sr_Read_block", frame);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeBlock(byte blockAddress, byte[] buffer) throws STException {
        byte[] frame;
        frame = new byte[2 + buffer.length];

        frame[0] = ISO14443SR_CUSTOM_CMD_WRITE_BLOCK;
        frame[1] = blockAddress;
        System.arraycopy(buffer, 0, frame, 2, buffer.length);

        transceive("sr_Write_block", frame);
    }


    /** Other commands **/

    /**
     * Read blocks
     * @param firstBlockAddress first block address
     * @param sizeInBlocks number of blocks
     * @return
     * @throws STException {@link}STException
     */
    public byte[] readBlocks(byte firstBlockAddress, byte sizeInBlocks) throws STException {
        ByteArrayOutputStream responseByteArray = new ByteArrayOutputStream();

        try {
            for (byte blockIndex = firstBlockAddress; blockIndex < firstBlockAddress + sizeInBlocks; blockIndex++) {
                byte[] tmp = readBlock(blockIndex);
                responseByteArray.write(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }

        return responseByteArray.toByteArray();
    }

    /**
     * Write blocks
     * @param firstBlockAddress first block address
     * @param data bytes array
     * @throws STException {@link}STException
     */
    public void writeBlocks(byte firstBlockAddress, byte[] data) throws STException {
        // Check that data is a round number of blocks
        if (data.length % mNbrOfBytesPerBlock != 0) {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        }

        for (byte writtenDataIndex = 0, blockIndex = firstBlockAddress;
                blockIndex < firstBlockAddress + data.length / mNbrOfBytesPerBlock;
                blockIndex++) {
            byte[] tmp = Arrays.copyOfRange(data, writtenDataIndex, writtenDataIndex + mNbrOfBytesPerBlock);
            writeBlock(blockIndex, tmp);
            writtenDataIndex += mNbrOfBytesPerBlock;
        }
    }


    /**
     *
     * @param commandName : Name of command passed to the transceive implementation
     * @param data
     * @return
     * @throws STException {@link}STException
     */
    public byte[] transceive (String commandName, byte[] data) throws STException {
        try {
            return mReaderInterface.transceive(this.getClass().getSimpleName(), commandName, data);
        } catch(Exception e) {
            // Catch all Java exceptions
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }
    }

}
