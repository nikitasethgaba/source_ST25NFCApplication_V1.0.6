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
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.type5.ReadBlockResult;
import com.st.st25sdk.type5.Type5Tag;

import static com.st.st25sdk.STException.STExceptionCode.INVALID_DATA;

public class NdefType5Command extends Type5Command {

    public NdefType5Command(RFReaderInterface reader, byte[] uid) {
        this(reader, uid, Iso15693Protocol.DEFAULT_FLAG, Type5Tag.DEFAULT_NBR_OF_BYTES_PER_BLOCK);
    }

    public NdefType5Command(RFReaderInterface reader, byte[] uid, byte flag) {
        this(reader, uid, flag, Type5Tag.DEFAULT_NBR_OF_BYTES_PER_BLOCK);
    }

    public NdefType5Command(RFReaderInterface reader, byte[] uid, int nbrOfBytesPerBlock) {
        this(reader, uid, Iso15693Protocol.DEFAULT_FLAG, nbrOfBytesPerBlock);
    }

    public NdefType5Command(RFReaderInterface reader, byte[] uid, byte flag, int nbrOfBytesPerBlock) {
        super(reader, uid, flag, nbrOfBytesPerBlock);
    }


    /**
     * Write a NDEF message with specified offset in block and flags.
     *  Message is written at offsetInBlocks
     * @param offsetInBlocks offset in block where to write NDEF message
     * @param msg NDEF Message to write, must not be null
     * @throws STException
     */
    public void writeNdefMessage(int offsetInBlocks, NDEFMsg msg) throws STException {
        writeNdefMessage(offsetInBlocks, msg, mFlag, mUid);
    }

    /**
     * Write a NDEF message with specified offset in block and flags, flags and uid.
     *  Message is written at offsetInBlocks
     * @param offsetInBlocks offset in block where to write NDEF message
     * @param msg NDEF Message to write, must not be null
     * @param flag Requested flags
     * @param uid TAG uid
     * @throws STException
     */
    public void writeNdefMessage(int offsetInBlocks, NDEFMsg msg, byte flag, byte[] uid) throws STException {

        byte[] formatedBuffer;
        try {
            formatedBuffer = msg.formatType5();
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(STException.STExceptionCode.INVALID_NDEF_DATA);
        }

        byte[] headerBuffer = new byte[mNbrOfBytesPerBlock];
        byte[] payloadBuffer;


        //Following NFC specifications to write an NDEF:
        // 1) write id + length = 0
        // 2) write the payload
        // 3) write id + correct length

        //Starting step 1)
        if (formatedBuffer.length < mNbrOfBytesPerBlock)
            throw new STException(STException.STExceptionCode.BAD_PARAMETER);

        System.arraycopy(formatedBuffer, 0, headerBuffer, 0, mNbrOfBytesPerBlock);
        headerBuffer[1] = (byte) 0x00;
        writeBlocks(offsetInBlocks, headerBuffer, flag, uid);
        //End of step 1)

        //Starting step 2)
        payloadBuffer = new byte[formatedBuffer.length - mNbrOfBytesPerBlock];
        System.arraycopy(formatedBuffer, mNbrOfBytesPerBlock, payloadBuffer, 0, formatedBuffer.length - mNbrOfBytesPerBlock );

        writeBlocks(
                offsetInBlocks + 1,
                payloadBuffer,
                flag,
                uid);
        //End of step 2)

        //Starting step 3)
        headerBuffer[1] = formatedBuffer[1];
        writeBlocks(offsetInBlocks, headerBuffer, flag, uid);
        //End of step 3)
    }

    /**
     * Read the current NdefMessage on tag at specified offset in block.
     * @param offsetInBlocks offset in block where to read
     * @return NDEF Message read from the tag
     * @throws STException
     */
    public NDEFMsg readNdefMessage(int offsetInBlocks) throws STException {
        return readNdefMessage(offsetInBlocks, mFlag, mUid);
    }

    /**
     * Read the current NdefMessage on tag at specified offset in block.
     * @param offsetInBlocks offset in block where to read
     * @param flag Requested flags
     * @param uid TAG uid
     * @return NDEF Message read from the tag
     * @throws STException
     */
    public NDEFMsg readNdefMessage(int offsetInBlocks, byte flag, byte[] uid) throws STException {
        byte[] dataArea;
        byte[] buffer;
        byte[] msg;
        int sizeInBytes;
        ReadBlockResult readResult;

        // Read the first block following the CC File. This is the beginning of the Data Area containing a TLV block
        readResult = readBlocks(offsetInBlocks, 1, flag, uid);
        dataArea = readResult.data;

        // Check that the expected number of bytes was read
        if(dataArea.length != mNbrOfBytesPerBlock) {
            throw new STException(INVALID_DATA);
        }

        // Check that this Data Area contains a NDEF
        if (dataArea[0] != NDEFMsg.NDEF_IDENTIFIER) {
            // This is NOT a NDEF
            return null;
        }

        if (dataArea[1] == (byte) 0xFF) {
            // Length field is on 3 Bytes (dataArea[1], dataArea[2], dataArea[3])
            sizeInBytes = ((dataArea[2] << 8) & 0xFF00) + (dataArea[3] &  0xFF);

            // Read all the Data Area. Add 4 Bytes for the Type (1 Byte) and Length fields (3 Bytes)
            buffer = readBytes(
                    offsetInBlocks * mNbrOfBytesPerBlock,
                    sizeInBytes + 4,
                    flag,
                    uid);

            // Skip the first 4 Bytes containing Type and Length fields
            msg = new byte[sizeInBytes];
            System.arraycopy(buffer, 4, msg,  0, sizeInBytes);
        }
        else {
            // Length field is on 1 Byte (dataArea[1])
            sizeInBytes = (dataArea[1]) & 0xFF;

            // Read all the Data Area. Add 2 Bytes for the Type (1 Byte) and Length fields (1 Byte)
            buffer = readBytes(
                    offsetInBlocks * mNbrOfBytesPerBlock,
                    sizeInBytes + 2,
                    flag,
                    uid);

            // Skip the first 2 Bytes containing Type and Length fields
            msg = new byte[sizeInBytes];
            System.arraycopy(buffer, 2, msg, 0, sizeInBytes);
        }

        NDEFMsg ndefmsg;
        try {
            ndefmsg = new NDEFMsg(msg);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new STException(STException.STExceptionCode.INVALID_NDEF_DATA);
        }

        return ndefmsg;
    }

}
