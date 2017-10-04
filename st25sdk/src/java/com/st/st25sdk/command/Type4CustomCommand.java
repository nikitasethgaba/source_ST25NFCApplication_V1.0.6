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

import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;

public class Type4CustomCommand extends Type4Command implements Type4CustomCommandInterface {

    public static final byte TYPE4_CUSTOM_CMD_ENABLE_PERMANENT_STATE = (byte) 0x28;
    public static final byte TYPE4_CUSTOM_CMD_DISABLE_PERMANENT_STATE = (byte) 0x26;
    public static final byte TYPE4_CUSTOM_CMD_UPDATE_FILE_TYPE = (byte) 0xD6;
    public static final byte TYPE4_CUSTOM_CMD_SEND_INTERRUPT = (byte) 0xD6;
    public static final byte TYPE4_CUSTOM_CMD_STATE_CONTROL = (byte) 0xD6;
    public static final byte TYPE4_CUSTOM_CMD_EXTENDED_READ = (byte) 0xB0;

    public static final byte TYPE4_CUSTOM_CMD_CLA_ST = (byte) 0xA2;

    /**
     * Size of frame containing class (1byte) instruction command (1byte) p1,p2 (2 bytes)
     */
    public static final int TYPE4_CUSTOM_HEADER_SIZE = 4;

    public Type4CustomCommand(RFReaderInterface reader, int maxRApduSize, int maxCApduSize) {
        super(reader, maxRApduSize, maxCApduSize);
    }

    public byte[] writeBinary(byte cla, byte p1, byte p2, byte[] data) throws STException {

        if (data == null) throw  new STException(STException.STExceptionCode.BAD_PARAMETER);

        byte[] response;
        byte[] frame = new byte[data.length + Iso7816Command.ISO7816_HEADER_SIZE];

        frame[0] = TYPE4_CUSTOM_CMD_CLA_ST;
        frame[1] = Type4Command.TYPE4_CMD_UPDATE_BINARY;
        frame[2] = p1;
        frame[3] = p2;

        System.arraycopy(data, 0, frame, 4, data.length);

        response = transceive("Type4CustomCommand writeBinary", frame);

        return response;
    }

    @Override
    public byte[] enablePermanentState(byte p1, byte p2) throws STException {

        byte[] frame = new byte[TYPE4_CUSTOM_HEADER_SIZE];

        frame[0] = TYPE4_CUSTOM_CMD_CLA_ST;
        frame[1] = TYPE4_CUSTOM_CMD_ENABLE_PERMANENT_STATE;
        frame[2] = p1;
        frame[3] = p2;

        try {
            return transceive("enablePermanentState", frame);
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }

    }

    @Override
    public byte[] disablePermanentState(byte p1, byte p2) throws STException {
        byte[] frame = new byte[TYPE4_CUSTOM_HEADER_SIZE];

        frame[0] = TYPE4_CUSTOM_CMD_CLA_ST;
        frame[1] = TYPE4_CUSTOM_CMD_DISABLE_PERMANENT_STATE;
        frame[2] = p1;
        frame[3] = p2;


        try {
            return transceive("disablePermanentState", frame);
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }
    }

    /**
     * Read the data in currently selected file (at specified fileOffset)
     * This is the same command as readData() but:
     * - readData() relies on readBinary command
     * - extendedReadData() relies on extendedReadBinary command
     *
     * @param fileOffset : Offset in currently selected file
     * @param sizeInBytes :  Amount of data to read
     * @return
     * @throws STException
     */
    public byte[] extendedReadData(int fileOffset, int sizeInBytes) throws STException {
        byte[] response = null;
        byte[] buffer;
        int currentFileOffset = fileOffset;
        int remainingDataToRead = sizeInBytes;
        int dataChunktoRead;

        while (remainingDataToRead > 0) {
            dataChunktoRead = (remainingDataToRead > mMaxReadSizeInBytes) ? mMaxReadSizeInBytes : remainingDataToRead;

            // p1 and p2 contain the offset in the selected file
            byte p1 = (byte) ((currentFileOffset & 0xFF00) >> 8);
            byte p2 = (byte) (currentFileOffset & 0xFF);
            byte length = (byte) (dataChunktoRead & 0xFF);

            // Buffer contains the data read + 2 status bytes at the end
            // It is possible that the response is less than the requested chunk size
            // In that case, adjust the array copy size but pretend that we got all the data
            // as it is unlikely that more will follow
            buffer = extendedReadBinary(p1, p2, length);

            if (response == null)
                response = new byte[sizeInBytes];

            System.arraycopy(buffer, 0, response, (currentFileOffset - fileOffset), buffer.length - 2);

            currentFileOffset += dataChunktoRead;
            remainingDataToRead -= dataChunktoRead;
        }

        return response;
    }

    @Override
    public byte[] extendedReadBinary(byte p1, byte p2, byte length) throws STException {
        byte[] response;
        byte[] frame = new byte[TYPE4_CUSTOM_HEADER_SIZE + 1];

        frame[0] = TYPE4_CUSTOM_CMD_CLA_ST;
        frame[1] = TYPE4_CUSTOM_CMD_EXTENDED_READ;
        frame[2] = p1;
        frame[3] = p2;
        frame[4] = length;

        try {
            response = transceive("extendedReadBinary", frame);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }
    }

    @Override
    public byte[]  updateFileType(byte data) throws STException {
        byte[] frame = new byte[TYPE4_CUSTOM_HEADER_SIZE + 1 + 1];

        frame[0] = TYPE4_CUSTOM_CMD_CLA_ST;
        frame[1] = TYPE4_CUSTOM_CMD_UPDATE_FILE_TYPE;
        frame[2] = 0x00;
        frame[3] = 0x00;
        frame[4] = 0x01;
        frame[5] = data;

        try {
            return transceive("updateFileType", frame);
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }

    }

    @Override
    public byte[] sendInterrupt() throws STException {
        byte[] frame = new byte[TYPE4_CUSTOM_HEADER_SIZE + 1];

        frame[0] = TYPE4_CUSTOM_CMD_CLA_ST;
        frame[1] = TYPE4_CUSTOM_CMD_SEND_INTERRUPT;
        frame[2] = 0x00;
        frame[3] = 0x1E;
        frame[4] = (byte) 0x00;

        try {
            return transceive("sendInterrupt", frame);
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }
    }

    @Override
    public byte[] setStateControl(byte data) throws STException {
        byte[] frame = new byte[TYPE4_CUSTOM_HEADER_SIZE + 1 + 1];

        frame[0] = TYPE4_CUSTOM_CMD_CLA_ST;
        frame[1] = TYPE4_CUSTOM_CMD_STATE_CONTROL;
        frame[2] = 0x00;
        frame[3] = 0x1F;
        frame[4] = (byte) 0x01;
        frame[5] = data;

        try {
            return transceive("setStateControl", frame);
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }

    }

    @Override
    public byte[] setConfigCounter(byte counterConfigurationValue) throws STException {
        byte[] frame = new byte[TYPE4_CUSTOM_HEADER_SIZE + 1 + 1];

        frame[0] = 0x00;
        frame[1] = Type4Command.TYPE4_CMD_UPDATE_BINARY;
        frame[2] = 0x00;
        frame[3] = 0x03;
        frame[4] = (byte) 0x01;
        frame[5] = counterConfigurationValue;

        try {
            return transceive("setConfigCounter", frame);
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }
    }


    /**
     * Set the GPO byte value using ISODEP APDU command
     * @param data GPO value
     * @return response code
     * @throws STException
     */
    public byte[] setGpo(byte data) throws STException {
        byte[] response;
        byte[] frame = new byte[TYPE4_CUSTOM_HEADER_SIZE + 1 + 1];

        frame[0] = 0x00;
        frame[1] = Type4Command.TYPE4_CMD_UPDATE_BINARY;
        frame[2] = 0x00;
        frame[3] = 0x02;
        frame[4] = (byte) 0x01;
        frame[5] = data;

        try {
            response = transceive("setGpo", frame);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }

    }

}
