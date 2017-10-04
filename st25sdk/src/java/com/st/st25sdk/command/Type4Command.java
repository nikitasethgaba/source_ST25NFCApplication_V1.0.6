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
import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.st.st25sdk.Helper;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;

public class Type4Command {

    private RFReaderInterface mReaderInterface;

    static final boolean DBG = true;

    public static final byte TYPE4_CMD_SELECT = (byte) 0xA4;
    public static final byte TYPE4_CMD_READ_BINARY = (byte) 0xB0;
    public static final byte TYPE4_CMD_UPDATE_BINARY = (byte) 0xD6;

    public static final byte TYPE4_CMD_SELECT_BY_FILE_ID = (byte) 0x00;
    public static final byte TYPE4_CMD_SELECT_BY_NAME    = (byte) 0x04;

    public static final byte TYPE4_CMD_FIRST_OR_ONLY_OCCURENCE = (byte) 0x0C;

    public static final Lock mLock = new ReentrantLock();
    protected int mMaxReadSizeInBytes;
    protected int mMaxWriteSizeInBytes;

    /**
     * Size of frame containing class (1byte) instruction command (1byte) p1,p2 (2 bytes)
     */
    public static final int TYPE4_HEADER_SIZE = 4;


    public Type4Command(RFReaderInterface reader, int maxRApduSize, int maxCApduSize) {
        mReaderInterface = reader;
        mMaxReadSizeInBytes = maxRApduSize;
        mMaxWriteSizeInBytes = maxCApduSize;
    }

    /**
     * This function sets the max data size that can be read from the Type 4 Tag
     * We will take the most restrictive value between:
     * - the reader's constraint (obtained from getMaxTransceiveLength())
     * - the tag's constraint
     * @param sizeInBytes
     */
    public void setMaxRApduDataSize(int sizeInBytes) {
        mMaxReadSizeInBytes = Math.min(sizeInBytes, mMaxReadSizeInBytes);
    }

    /**
     * This function sets the max data size that can be sent to the Type 4 Tag
     * We will take the most restrictive value between:
     * - the reader's constraint (obtained from getMaxTransceiveLength())
     * - the tag's constraint
     * @param sizeInBytes
     */
    public void setMaxCApduDataSize(int sizeInBytes) {
        mMaxWriteSizeInBytes = Math.min(sizeInBytes, mMaxWriteSizeInBytes);
    }

    /**
     * This function returns the max data size that can be read from the Type 4 Tag
     * in bytes
     * @return
     */
    public int getMaxRApduDataSize() {
        return mMaxReadSizeInBytes;
    }

    /**
     * This function returns the max data size that can be sent to the Type 4 Tag
     * in bytes
     * @return
     */
    public int getMaxCApduDataSize() {
        return mMaxWriteSizeInBytes;
    }

    /**
     *
     * @param p1   param as defined by Type 4
     * @param p2   param as defined by Type 4
     * @param data File identifier
     * @return
     * @throws STException
     */
    public byte[] select(byte p1, byte p2, byte[] data) throws STException {
        byte[] response;
        byte[] frame = new byte[data.length + TYPE4_HEADER_SIZE + 1];

        frame[0] = (byte) 0x00;
        frame[1] = TYPE4_CMD_SELECT;
        frame[2] = p1;
        frame[3] = p2;
        frame[4] = (byte) data.length;

        System.arraycopy(data, 0, frame, 5, data.length);
        try {
            response = transceive("select", frame);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }

    }

    /**
     *
     * @return
     * @throws STException
     */
    public byte[] selectNdefTagApplication() throws STException {
        byte[] response;
        byte[] frame = new byte[7 + TYPE4_HEADER_SIZE + 2];

        frame[0] = 0x00;
        frame[1] = TYPE4_CMD_SELECT;
        frame[2] = TYPE4_CMD_SELECT_BY_NAME;
        frame[3] = 0x00;
        frame[4] = (byte) 0x07;//length
        frame[5] = (byte) 0xD2;
        frame[6] = (byte) 0x76;
        frame[7] = (byte) 0x00;
        frame[8] = (byte) 0x00;
        frame[9] = (byte) 0x85;
        frame[10]= (byte) 0x01;
        frame[11]= (byte) 0x01;
        frame[12]= (byte) 0x00;

        try {
            response = transceive("Activate application", frame);

        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }
        return response;
    }

    /**
     * Select a file by fileId
     * @param fileId File identifier
     * @return
     * @throws STException
     */
    public byte[] selectFile(int fileId) throws STException {
        byte[] response;
        byte[] data = Helper.convertIntTo2BytesHexaFormat(fileId);
        byte[] frame = new byte[data.length + TYPE4_HEADER_SIZE + 1];

        frame[0] = (byte) 0x00;
        frame[1] = TYPE4_CMD_SELECT;
        frame[2] = TYPE4_CMD_SELECT_BY_FILE_ID;
        frame[3] = TYPE4_CMD_FIRST_OR_ONLY_OCCURENCE;
        frame[4] = (byte) data.length;

        System.arraycopy(data, 0, frame, 5, data.length);

        try {
            response = transceive("SelectFile 0x" + Helper.convertIntToHexFormatString(fileId), frame);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }

    }

    /**
     *
     * @param p1   param as defined by Type 4: File offset where starting to write data
     * @param p2   param as defined by Type 4: File offset where starting to write data
     * @param data Data to be written
     * @return
     * @throws STException
     */
    public byte[] updateBinary(byte p1, byte p2, byte[] data) throws STException {
        if(data.length > mMaxWriteSizeInBytes) {
            throw new STException(BAD_PARAMETER);
        }

        byte[] response;
        byte[] frame = new byte[data.length + TYPE4_HEADER_SIZE + 1];

        frame[0] = 0x00;
        frame[1] = TYPE4_CMD_UPDATE_BINARY;
        frame[2] = p1;
        frame[3] = p2;
        frame[4] = (byte) data.length;

        System.arraycopy(data, 0, frame, 5, data.length);

        try {
            response = transceive("updateBinary", frame);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }
    }

    /**
     *
     * @param p1     param as defined by Type 4: File offset to start reading
     * @param p2     param as defined by Type 4: File offset to start reading
     * @param length number of bytes to be read
     * @return
     * @throws STException
     */
    public byte[] readBinary(byte p1, byte p2, byte length) throws STException {
        if(length > mMaxReadSizeInBytes) {
            throw new STException(BAD_PARAMETER);
        }

        byte[] response;
        byte[] frame = new byte[1 + TYPE4_HEADER_SIZE];

        frame[0] = 0x00;
        frame[1] = TYPE4_CMD_READ_BINARY;
        frame[2] = p1;
        frame[3] = p2;
        frame[4] = length;

        try {
            response = transceive("readBinary", frame);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }
    }

    /**
     * Read the data in currently selected file (at specified fileOffset)
     * @param fileOffset : Offset in currently selected file
     * @param sizeInBytes :  Amount of data to read
     * @return
     * @throws STException
     */
    public byte[] readData(int fileOffset, int sizeInBytes) throws STException {
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
            buffer = readBinary(p1, p2, length);

            if (response == null)
                response = new byte[sizeInBytes];

            System.arraycopy(buffer, 0, response, (currentFileOffset - fileOffset), buffer.length - 2);

            currentFileOffset += dataChunktoRead;
            remainingDataToRead -= dataChunktoRead;
        }

        return response;
    }

    /**
     * Write some data to the currently selected file. Data are written at the specified offset.
     * @param fileOffset : Offset in currently selected file
     * @param data :  data to write
     * @throws STException
     */
    public void writeData(int fileOffset, byte[] data) throws STException {
        int currentFileOffset = fileOffset;
        int remainingDataToWrite = data.length;
        int dataChunktoWrite;
        int offsetInSrcDataFile = 0;

        while (remainingDataToWrite > 0) {
            dataChunktoWrite = (remainingDataToWrite > mMaxWriteSizeInBytes) ? mMaxWriteSizeInBytes : remainingDataToWrite;
            remainingDataToWrite -= dataChunktoWrite;

            byte[] buffer = new byte[dataChunktoWrite];
            System.arraycopy(data, offsetInSrcDataFile, buffer, 0, buffer.length);

            // p1 and p2 contain the offset in the selected file
            byte p1 = (byte) ((currentFileOffset & 0xFF00) >> 8);
            byte p2 = (byte) (currentFileOffset & 0xFF);

            updateBinary(p1, p2, buffer);

            // Move offset in source data file
            offsetInSrcDataFile += dataChunktoWrite;
            // Move offset in destination file
            currentFileOffset += dataChunktoWrite;
        }
    }

    public byte[] transceive(String commandName, byte[] data) throws STException {
        byte[] response = mReaderInterface.transceive(this.getClass().getSimpleName(), commandName, data);
        Iso7816Type4RApduStatus.checkError(response);
        return response;
    }
}
