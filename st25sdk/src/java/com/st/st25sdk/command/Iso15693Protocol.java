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

import com.st.st25sdk.Helper;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STException.STExceptionCode;
import com.st.st25sdk.STLog;
import com.st.st25sdk.type5.Type5Tag;

public class Iso15693Protocol  {

    public Iso15693Protocol(RFReaderInterface reader, byte[] uid) {
        this(reader, uid, DEFAULT_FLAG, Type5Tag.DEFAULT_NBR_OF_BYTES_PER_BLOCK);
    }

    public Iso15693Protocol(RFReaderInterface reader, byte[] uid, byte flag) {
        this(reader, uid, flag, Type5Tag.DEFAULT_NBR_OF_BYTES_PER_BLOCK);
    }

    public Iso15693Protocol(RFReaderInterface reader, byte[] uid, int nbrOfBytesPerBlock) {
        this(reader, uid, DEFAULT_FLAG, nbrOfBytesPerBlock);
    }

    public Iso15693Protocol(RFReaderInterface reader, byte[] uid, byte flag, int nbrOfBytesPerBlock) {
        mReaderInterface = reader;
        setUid(uid);
        mFlag = flag;
        mNbrOfBytesPerBlock = nbrOfBytesPerBlock;
        if(nbrOfBytesPerBlock == 0) {
            STLog.e("Error! Invalid nbrOfBytesPerBlock");
        }
    }

    // Request flag definition
    public static final byte STM_MANUFACTURER_CODE = (byte) 0x02;

    // Request flag definition
    public static final byte SUB_CARRIER_MODE = (byte) 0x01;
    public static final byte HIGH_DATA_RATE_MODE = (byte) 0x02;
    public static final byte INVENTORY_MODE = (byte) 0x04;
    public static final byte PROTOCOL_FORMAT_EXTENSION = (byte) 0x08;
    public static final byte SELECTOR_MODE = (byte) 0x10;
    public static final byte ADDRESSED_MODE = (byte) 0x20;
    public static final byte OPTION_FLAG = (byte) 0x40;

    // Inventory mode definition
    public static final byte INVENTORY_AFI_FIELD = (byte) 0x10;
    public static final byte INVENTORY_ONE_SLOT = (byte) 0x20;

    public static final int  DEFAULT_READ_MULTIPLE_MAX_NBR_OF_BLOCKS = 32;
    public static final int  DEFAULT_WRITE_MULTIPLE_MAX_NBR_OF_BLOCKS = 4;

    protected RFReaderInterface mReaderInterface;

    static final boolean DBG = true;

    static protected final byte DEFAULT_FLAG = HIGH_DATA_RATE_MODE | ADDRESSED_MODE;
    static protected final byte DEFAULT_VICINITY_FLAG = HIGH_DATA_RATE_MODE | ADDRESSED_MODE | PROTOCOL_FORMAT_EXTENSION;

    protected byte mFlag = HIGH_DATA_RATE_MODE | ADDRESSED_MODE;
    protected byte[] mUid;

    protected int mNbrOfBytesPerBlock;

    /**
     * Size of frame containing flag (1byte) command (1byte) uid (8 bytes)
     */
    protected static final int ISO15693_HEADER_SIZE_UID = 10;
    protected static final int ISO15693_UID_OFFSET = 2;
    protected static final int ISO15693_HEADER_SIZE = 2;
    /**
     * Size of frame containing flag (1byte) command (1byte) Mfg code (1 byte) uid (8 bytes) for ISO15693 Custom Commands
     */
    protected static final int  ISO15693_CUSTOM_ST_HEADER_SIZE_UID = 11;
    protected static final int ISO15693_CUSTOM_ST_UID_OFFSET = 3;
    protected static final int  ISO15693_CUSTOM_ST_HEADER_SIZE = 3;

    /********************* Helper functions *********************/


    protected int getIso15693HeaderSize(byte flag) {
        if (uidNeeded(flag)) {
            return ISO15693_HEADER_SIZE_UID;
        }
        else return ISO15693_HEADER_SIZE;
    }

    protected int getIso15693CustomHeaderSize(byte flag) {
        if (uidNeeded(flag)) {
            return ISO15693_CUSTOM_ST_HEADER_SIZE_UID;
        }
        else return ISO15693_CUSTOM_ST_HEADER_SIZE;
    }

    protected boolean uidNeeded(byte flag) {
        return ((flag & ADDRESSED_MODE) == ADDRESSED_MODE);
    }

    protected void addUidToFrame(byte[] frame, int offset) throws STException {
        addUidToFrame(frame, offset, mUid);
    }

    protected void addUidToFrame(byte[] frame, int offset, byte[] uid) throws STException {
        // Addressed Mode, add UID to frame
        byte[] reversedUID;

        if (uid != null && uid.length == 8) {
            // Reverse UID before sending command
            reversedUID = Helper.reverseByteArray(uid);
            System.arraycopy(reversedUID, 0, frame, offset, 8);
        } else {
            throw new STException(BAD_PARAMETER);
        }
    }

    /**
     * This function checks a command response and raises a STException in case of error
     * @param response
     * @throws STException
     */
    protected void checkIso15693Response(byte[] response) throws STException {
        if ((response != null) && (response[0] != 0x00)) {
            if (response.length >= 2) {
                generateCmdException(response);
            } else {
                throw new STException(CMD_FAILED, response);
            }
        }
    }

    protected void generateCmdException(byte[] response) throws STException {
        switch(response[1]) {
            case 0x01: // Command is not supported.
                throw new STException(STExceptionCode.ISO15693_CMD_NOT_SUPPORTED, response);
            case 0x02: //Command is not recognized (format error).
                throw new STException(STExceptionCode.ISO15693_CMD_NOT_RECOGNIZED, response);
            case 0x03: //The option is not supported.
                throw new STException(STExceptionCode.ISO15693_CMD_OPTION_NOT_SUPPORTED, response);
            case 0x0F: //Error with no information given.
                throw new STException(STExceptionCode.CMD_FAILED, response);
            case 0x10: //The specified block is not available.
                throw new STException(STExceptionCode.ISO15693_BLOCK_NOT_AVAILABLE, response);
            case 0x11: //The specified block is already locked and thus cannot be locked again.
                throw new STException(STExceptionCode.ISO15693_BLOCK_ALREADY_LOCKED, response);
            case 0x12: //The specified block is locked and its contents cannot be changed.
                throw new STException(STExceptionCode.ISO15693_BLOCK_IS_LOCKED, response);
            case 0x13: //The specified block was not successfully programmed.
                throw new STException(STExceptionCode.ISO15693_BLOCK_PROGRAMMING_FAILED, response);
            case 0x14: //The specified block was not successfully locked.
                throw new STException(STExceptionCode.ISO15693_BLOCK_LOCKING_FAILED, response);
            case 0x15: //The specified block is protected (in read or write).
                throw new STException(STExceptionCode.ISO15693_BLOCK_PROTECTED, response);
            default:
                throw new STException(STExceptionCode.INVALID_ERROR_CODE, response);
        }
    }

    /**
     *
     * @param commandName : Name of command passed to the transceive implementation
     * @param data
     * @return
     * @throws STException
     */
    public byte[] transceive (String commandName, byte[] data) throws STException {
        try {
            byte[] response = mReaderInterface.transceive(this.getClass().getSimpleName(), commandName, data);
            // Catch all ISO errors
            checkIso15693Response(response);
            return response;
        } catch(Exception e) {
            // Catch all Java exceptions
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }
    }

    /**
     *
     * @return
     */
    public byte[] getUid() {
        return mUid;}

    /*
     * @param flag
     */
    public void setFlag(byte flag) {
        mFlag = flag;
    }

    /**
     *
     * @return
     */
    public byte getFlag() {
        return mFlag;
    }

    /**
     *
     * @param uid
     */
    public void setUid(byte[] uid) {
        if (uid != null){
            mUid = uid.clone();
        }
    }

}
