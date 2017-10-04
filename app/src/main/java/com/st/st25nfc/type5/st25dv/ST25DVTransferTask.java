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

package com.st.st25nfc.type5.st25dv;

import android.os.SystemClock;

import com.st.st25sdk.Crc;
import com.st.st25sdk.Helper;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.command.Iso15693Command;
import com.st.st25sdk.type5.ST25DVTag;

import java.util.Arrays;

import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;

public class ST25DVTransferTask implements Runnable {

    public interface OnTransferListener {
        public void transferOnProgress(double progressStatus);

        public void transferFinished(boolean success, long time, byte[] buffer);

        public byte[] getDataToWrite();
    }


    public enum ST25DVTransferEvent {
        START,
        STOP,
        PAUSE,
        RESUME
    }

    //C.F. protocol defined for demos
    public static final int FAST_BASIC_TRANSFER_FUNCTION  = 0x03;
    public static final int FAST_IMAGE_UPLOAD_FUNCTION    = 0x09;
    public static final int FAST_IMAGE_DOWNLOAD_FUNCTION  = 0x07;
    public static final int FAST_FIRMWARE_UPDATE_FUNCTION = 0x04;
    public static final int FAST_PRESENT_PWD_FUNCTION     = 0x08;
    public static final int FAST_RANDOM_TRANSFER_FUNCTION = 0x0A;
    public static final int FAST_CHRONO_DEMO_FUNCTION     = 0x0B;

    private enum State {
        INIT, TRANSFERING, CHECK_CRC, ACKNOWLEDGE, ENDING
    }

    private State mState;
    private byte[] mBuffer;

    //private ST25DVTransferAction mAction;
    private int mAction;
    private ST25DVTransferEvent mEvent;


    private int mOffset;
    private long mTimeStamp;

    // To Do compute exactly and dynamically
    private int mMaxPayloadSizeTx = 220;
    private int mMaxPayloadSizeRx = 32;

    private long mTimeTransfer;

    private ST25DVTransferTask.OnTransferListener mListener;

    private final int FAST_TRANSFER_COMMAND = 0x00;
    private final int FAST_TRANSFER_ANSWER  = 0x01;
    private final int FAST_TRANSFER_ACK     = 0x02;

    private final int FAST_TRANSFER_OK      = 0x00;
    private final int FAST_TRANSFER_ERROR   = 0x01;

    private final int ERROR     = -1;
    private final int TRY_AGAIN = 0;
    private final int OK        = 1;

    private final int SLEEP_TIME = 10; //ms

    public ST25DVTag mST25DVTag;

    private byte mTransferFunction;
    private byte mTransferCommand;


    public void setTransferListener(OnTransferListener listener) {
        mListener = listener;
    }


    public ST25DVTransferTask(int action, byte[] buffer, ST25DVTag tag) {
        mAction = action;
        mEvent = ST25DVTransferEvent.START;
        mState = State.INIT;
        mOffset = 0;

        if (buffer != null) {
            mBuffer = buffer;
            int length = buffer.length;

            if (length % 4 != 0) {
                length = 4 * (buffer.length / 4 + 1);
            }

            mBuffer = new byte[length];
            Arrays.fill(mBuffer, (byte) 0);

            System.arraycopy(buffer, 0, mBuffer, 0, buffer.length);
        }

        //HEADER SIZE + 8 bytes UID + 3 bytes (CMD + FLAG + ST code) + 1 byte size to write in mb
        // For optimization reasons we remove the 8 UID bytes in write...
        mMaxPayloadSizeTx = tag.getReaderInterface().getMaxTransmitLengthInBytes() -
                CHAINED_HEADER_SIZE  - 3 - 1;
        mMaxPayloadSizeRx = tag.getReaderInterface().getMaxReceiveLengthInBytes();

        mST25DVTag = tag;
    }

    @Override
    public void run() {

        int ret;
        ret = TRY_AGAIN;
        // check that MB is enabled and loop on MB status
        while (ret == TRY_AGAIN) {
            try {
                if (mST25DVTag.isMBEnabled(true)) ret = OK;
                else ret = ERROR;
            } catch (STException e) {
                ret = checkError(e);
                SystemClock.sleep(SLEEP_TIME * 5);
            }
        }
        if (ret == ERROR) {
            mTimeTransfer = (int) SystemClock.elapsedRealtime() - mTimeStamp;
            mListener.transferFinished(false, mTimeTransfer, null);
            mListener.transferOnProgress(0);
            mEvent = ST25DVTransferEvent.STOP;
            return;
        }

        while (mEvent == ST25DVTransferEvent.START
                || mEvent == ST25DVTransferEvent.RESUME
                || mEvent == ST25DVTransferEvent.PAUSE) {

            if (mEvent != ST25DVTransferEvent.PAUSE) {
                synchronized (this) {
                    switch (mState) {
                        case INIT:
                            STLog.i("INIT");
                            ret = prepare();
                            if (ret == OK) {
                                mState = State.TRANSFERING;
                                mTimeStamp = (int) System.nanoTime();;
                            }
                            break;

                        case TRANSFERING:
                            STLog.i("TRANSFERING");
                            switch(mAction) {
                                case FAST_CHRONO_DEMO_FUNCTION:
                                    // Ask for more data
                                    if (mOffset == mBuffer.length) {
                                        mBuffer = mListener.getDataToWrite();
                                        if (mBuffer != null)
                                            mOffset = 0;
                                        else
                                            mState = State.ENDING;
                                    }
                                    try {
                                        ret = sendSimpleData((byte) FAST_TRANSFER_COMMAND);
                                    } catch (Exception e) {
                                        ret = ERROR;
                                    }
                                    break;

                                case FAST_BASIC_TRANSFER_FUNCTION:
                                case FAST_IMAGE_UPLOAD_FUNCTION:
                                case FAST_FIRMWARE_UPDATE_FUNCTION:
                                    if (mOffset == mBuffer.length) {
                                        ret = OK;
                                        mState = State.CHECK_CRC;
                                    } else {
                                        try {
                                            ret = sendChainedData((byte) FAST_TRANSFER_COMMAND, false);
                                        } catch (Exception e) {
                                            ret = ERROR;
                                        }
                                    }
                                    break;

                                case FAST_IMAGE_DOWNLOAD_FUNCTION:
                                case FAST_RANDOM_TRANSFER_FUNCTION:
                                    if (mOffset == mBuffer.length) {
                                        ret = OK;
                                        mState = State.CHECK_CRC;
                                    } else {
                                        try {
                                            ret = readChainedData();
                                        } catch (Exception e) {
                                            ret = ERROR;
                                        }
                                    }
                                    break;

                                case FAST_PRESENT_PWD_FUNCTION:
                                    if (mOffset == mBuffer.length) {
                                        ret = OK;
                                        mState = State.ENDING;
                                    } else {
                                        try {
                                            ret = sendSimpleData((byte) FAST_TRANSFER_ANSWER);
                                        } catch (Exception e) {
                                            ret = ERROR;
                                        }
                                    }
                                    break;
                            }
                            if (ret == OK) {
                                // To do introduce listeners instead of
                                // method call
                                mListener.transferOnProgress(mOffset * 100 / mBuffer.length);
                            }

                            break;

                        case ENDING:
                            STLog.i("ENDING");
                            switch(mAction) {
                                case FAST_CHRONO_DEMO_FUNCTION:
                                    // Ask for more data
                                    if (mOffset == mBuffer.length) {
                                        mBuffer = mListener.getDataToWrite();
                                        if (mBuffer != null) {
                                            mOffset = 0;
                                            ret = sendSimpleData((byte) FAST_TRANSFER_COMMAND);
                                        }
                                    }
                                    break;

                                case FAST_PRESENT_PWD_FUNCTION:
                                    ret = checkPasswordAnswer();
                                    if (ret == OK) {
                                        STLog.i("transfer finished successfully");
                                        mListener.transferFinished(true, mTimeTransfer, null);
                                        mListener.transferOnProgress(0);
                                        mEvent = ST25DVTransferEvent.STOP;
                                    }
                                    break;

                                default:
                                    STLog.e("Error, state not expected for this action!");
                                    ret = ERROR;
                                    break;
                            }
                            break;

                        case CHECK_CRC:
                            STLog.i("CHECK_CRC");
                            switch(mAction) {
                                case FAST_BASIC_TRANSFER_FUNCTION:
                                case FAST_IMAGE_UPLOAD_FUNCTION:
                                case FAST_FIRMWARE_UPDATE_FUNCTION:
                                    ret = checkCrc();
                                    if (ret == OK) {
                                        mState = State.ACKNOWLEDGE;
                                    }
                                    break;

                                case FAST_IMAGE_DOWNLOAD_FUNCTION:
                                case FAST_RANDOM_TRANSFER_FUNCTION:
                                    ret = sendCrc(mTransferFunction);
                                    if (ret == OK) {
                                        mState = State.ACKNOWLEDGE;
                                    }
                                    break;

                                default:
                                    STLog.e("Error, no CRC verification expected for this action!");
                                    ret = ERROR;
                                    break;
                            }
                            break;

                        case ACKNOWLEDGE:
                            STLog.i("ACKNOWLEDGE");
                            switch(mAction) {
                                case FAST_BASIC_TRANSFER_FUNCTION:
                                case FAST_IMAGE_UPLOAD_FUNCTION:
                                case FAST_FIRMWARE_UPDATE_FUNCTION:
                                    ret = sendAck(true, (byte) mAction);
                                    if (ret == OK) {
                                        STLog.i("transferFinished successfully");
                                        mTimeTransfer = (int) System.nanoTime() - mTimeStamp;
                                        mListener.transferFinished(true, mTimeTransfer, null);
                                        mListener.transferOnProgress(0);
                                        mEvent = ST25DVTransferEvent.STOP;
                                    }
                                    break;

                                case FAST_IMAGE_DOWNLOAD_FUNCTION:
                                case FAST_RANDOM_TRANSFER_FUNCTION:
                                    ret = checkAck();
                                    if (ret == OK) {
                                        STLog.i("transfer finished successfully");
                                        mTimeTransfer = (int) System.nanoTime() - mTimeStamp;
                                        mListener.transferFinished(true, mTimeTransfer, mBuffer);
                                        mListener.transferOnProgress(0);
                                        mEvent = ST25DVTransferEvent.STOP;
                                    }
                                    break;

                                default:
                                    STLog.e("Error, no acknowledge expected for this action!");
                                    ret = ERROR;
                                    break;
                            }
                            break;
                    }
                }
                // A command has been executed
                if (ret == ERROR) {
                    STLog.e("transfer finished with error");
                    mListener.transferFinished(false, mTimeTransfer, null);
                    mListener.transferOnProgress(0);
                    mEvent = ST25DVTransferEvent.STOP;

                } else if (ret == TRY_AGAIN) {
                    // Sleep for a while and retry the same command
                    sleep_in_ms(SLEEP_TIME);
                }

            } else
                sleep_in_ms(100);
        }
    }

    private void sleep_in_ms(int time_in_ms) {
        try {
            Thread.sleep(time_in_ms);
            //SystemClock.sleep(time_in_ms);
        } catch (InterruptedException e) {
            STLog.e(e.getMessage());
        }
    }


    public int prepare() {
        switch(mAction) {
            case FAST_CHRONO_DEMO_FUNCTION:
                mBuffer = mListener.getDataToWrite();
                if (mBuffer != null)
                    return OK;
                break;

            case FAST_BASIC_TRANSFER_FUNCTION:
            case FAST_IMAGE_UPLOAD_FUNCTION:
            case FAST_FIRMWARE_UPDATE_FUNCTION:
            case FAST_PRESENT_PWD_FUNCTION:
                if (mBuffer != null) {
                    return OK;
                }
                break;

            case FAST_IMAGE_DOWNLOAD_FUNCTION:
            case FAST_RANDOM_TRANSFER_FUNCTION:
                int ret = readHeader();
                return ret;
        }

        return ERROR;
    }

    private final int CHAINED_HEADER_SIZE = 13;
    private final int SIMPLE_HEADER_SIZE = 5;

    private int checkError(STException e) {
        STException.STExceptionCode errorCode = e.getError();
        if (errorCode == STException.STExceptionCode.CONNECTION_ERROR
                || errorCode == STException.STExceptionCode.TAG_NOT_IN_THE_FIELD
                || errorCode == STException.STExceptionCode.CMD_FAILED
                || errorCode == STException.STExceptionCode.CRC_ERROR) {
            STLog.i("Last cmd failed with error code " +  errorCode + ": Try again the same cmd");
            return TRY_AGAIN;
        } else {
            // Transfer failed
            STLog.e(e.getMessage());
            return ERROR;
        }
    }

    private byte[] readMessage() throws STException {
        int length = mST25DVTag.readMailboxMessageLength() + 1;
        byte[] buffer;
        if (length > 0)
            buffer = new byte[length];
        else
            throw new STException(CMD_FAILED);

        byte[] tmpBuffer;
        int offset = 0;

        int size = length;

        if (size <= 0)
            throw new STException(CMD_FAILED);

        while (offset < length) {
            size = ((length - offset) > mMaxPayloadSizeRx)
                    ? mMaxPayloadSizeRx
                    : length - offset;
            tmpBuffer = mST25DVTag.readMailboxMessage((byte) offset, size - 1);
            if (tmpBuffer.length < (size + 1) || tmpBuffer[0] != 0)
                throw new STException(CMD_FAILED);
            System.arraycopy(tmpBuffer, 1, buffer, offset,
                    tmpBuffer.length - 1);
            offset += tmpBuffer.length - 1;
        }
        return buffer;

    }

    private int readHeader() {
        byte[] response = null;
        int length = 0;

        try {
            response = readMessage();
        } catch (STException e) {
            return checkError(e);
        }

        if (response == null)
            return ERROR;

        if (response.length < CHAINED_HEADER_SIZE)// || response[0] != 0)
            return ERROR;

        if (response[0] != FAST_IMAGE_DOWNLOAD_FUNCTION
                && response[0] != FAST_RANDOM_TRANSFER_FUNCTION)
            return ERROR;

        mTransferFunction = response[0];
        mTransferCommand = response[1];

        if (response[2] != FAST_TRANSFER_OK || response[3] != 0x01)
            return ERROR;

        length = ((response[4] << 24) & 0xFF000000)
                + ((response[5] << 16) & 0x00FF0000)
                + ((response[6] << 8) & 0x0000FF00)
                + (response[7] & 0xFF);

        if (length > 0)
            mBuffer = new byte[length];
        else
            return ERROR;

        int nbOfChunks = ((response[8] << 8) & 0xFF)
                + ((response[9]) & 0xFF);

        int chunk = ((response[10] << 8) & 0xFF)
                + ((response[11]) & 0xFF);

        length = response[12] & 0xFF;

        if (length > 0) {
            System.arraycopy(response, CHAINED_HEADER_SIZE, mBuffer, 0, length);
            mOffset += length;
        }

        return OK;
    }

    private int sendChainedHeader(byte[] frame, byte function, byte command, int length) {
        if (frame.length <= CHAINED_HEADER_SIZE)
            return ERROR;
        else {

            frame[0] = function;
            frame[1] = command;
            frame[2] = FAST_TRANSFER_OK;
            frame[3] = 0x01; // Chained
            frame[4] = (byte) ((mBuffer.length >> 24) & 0xFF);
            frame[5] = (byte) ((mBuffer.length >> 16) & 0xFF);
            frame[6] = (byte) ((mBuffer.length >> 8) & 0xFF);
            frame[7] = (byte) (mBuffer.length & 0xFF);

            int nbOfChunks = (mBuffer.length + mMaxPayloadSizeTx - 1) / mMaxPayloadSizeTx;
            frame[8] = (byte) ((nbOfChunks >> 8) & 0xFF);
            frame[9] = (byte) ((nbOfChunks) & 0xFF);

            // Numerotation start from 1
            int chunk = mOffset / mMaxPayloadSizeTx + 1;
            frame[10] = (byte) ((chunk >> 8) & 0xFF);
            frame[11] = (byte) ((chunk) & 0xFF);
            frame[12] = (byte) (length & 0xFF);
        }
        return OK;
    }

    private int sendSimpleHeader(byte[] frame, byte function, byte command, int length) {
        if (frame.length <= SIMPLE_HEADER_SIZE)
            return ERROR;
        else {

            frame[0] = function;
            frame[1] = command;
            frame[2] = FAST_TRANSFER_OK;
            frame[3] = 0x00; // Chained
            frame[4] = (byte) (length & 0xFF);
        }
        return OK;
    }

    private int readChainedData() {
        try {
            // true force a refresh
            if (!mST25DVTag.hasHostPutMsg(true))
                return TRY_AGAIN;

        } catch (STException e) {
            return checkError(e);
        }

        try {

            byte[] response = readMessage();

            if (response == null)
                return ERROR;

            if (response.length <= CHAINED_HEADER_SIZE)
                return ERROR;

            int length = response[CHAINED_HEADER_SIZE - 1] & 0xFF;

            // header size
            if (response.length != length + CHAINED_HEADER_SIZE)
                return ERROR;

            System.arraycopy(response, CHAINED_HEADER_SIZE, mBuffer, mOffset, length);
            mOffset += length;

            // We have read all the data of the mailbox. Leave some time for the host to fill it again
            sleep_in_ms(5);

            return OK;

        } catch (STException e) {
            return checkError(e);
        }

    }

    private int sendSimpleData(byte command) {

        try {
            if (mST25DVTag.hasRFPutMsg(true))
                return TRY_AGAIN;

        } catch (STException e) {
            return checkError(e);
        }

        byte response;
        byte[] frame = new byte[mMaxPayloadSizeTx + SIMPLE_HEADER_SIZE];
        int size = ERROR;

        try {

            size = (mBuffer.length - mOffset > mMaxPayloadSizeTx)
                    ? mMaxPayloadSizeTx
                    : mBuffer.length - mOffset;
            if (size <= 0)
                return ERROR;

            if (sendSimpleHeader(frame, (byte) mAction, command, size) == ERROR)
                return ERROR;

            System.arraycopy(mBuffer, mOffset, frame, SIMPLE_HEADER_SIZE, size);
            response = mST25DVTag.writeMailboxMessage(size + SIMPLE_HEADER_SIZE, frame);
            if (response == 0x00)
                mOffset += size;
            else
                return ERROR;

        } catch (STException e) {
            return checkError(e);
        }

        return OK;

    }


    private int sendChainedData(byte command, boolean checkRf) {

        if (checkRf) {
            try {
                if (mST25DVTag.hasRFPutMsg(true))
                    return TRY_AGAIN;

            } catch (STException e) {
                return checkError(e);
            }
        }

        byte response;
        byte[] frame = new byte[mMaxPayloadSizeTx + CHAINED_HEADER_SIZE];
        int size = ERROR;

        try {

            size = (mBuffer.length - mOffset > mMaxPayloadSizeTx)
                    ? mMaxPayloadSizeTx
                    : mBuffer.length - mOffset;
            if (size <= 0)
                return ERROR;

            if (sendChainedHeader(frame, (byte) mAction, command,
                    size) == ERROR)
                return ERROR;
            System.arraycopy(mBuffer, mOffset, frame, CHAINED_HEADER_SIZE,
                    size);

            response = mST25DVTag.writeMailboxMessage(size + CHAINED_HEADER_SIZE, frame, Iso15693Command.HIGH_DATA_RATE_MODE);
            if (response == 0x00)
                mOffset += size;
            else
                return ERROR;

            if (mOffset == mBuffer.length)
                mState = State.CHECK_CRC;
        } catch (STException e) {
            return checkError(e);
        }

        // We have filled the mailbox. Leave some time for the host to consume the data
        sleep_in_ms(5);

        return OK;
    }


    private long computeCrc() {
        try {
            return Crc.CRC(mBuffer);
        } catch (Exception e) {
            return ERROR;
        }
    }

    private int sendCrc(byte function) {

        try {
            if (mST25DVTag.hasRFPutMsg(true))
                return TRY_AGAIN;

        } catch (STException e) {
            return checkError(e);
        }

        byte response;
        byte[] frame = new byte[9];

        frame[0] = function;
        frame[1] = FAST_TRANSFER_ACK;
        frame[2] = 0x00;
        frame[3] = 0x00;
        frame[4] = 0x04; //Length

        long crc = computeCrc();
        frame[5] = (byte) ((crc & 0xFF000000) >> 24);
        frame[6] = (byte) ((crc & 0x00FF0000) >> 16);
        frame[7] = (byte) ((crc & 0x0000FF00) >> 8);
        frame[8] = (byte) (crc & 0x000000FF);

        STLog.i("sendCrc: " + String.valueOf(crc));

        try {
            response = mST25DVTag.writeMailboxMessage(frame.length, frame);
            if (response == (byte) 0x00)
                return OK;

        } catch (STException e) {
            return checkError(e);
        }
        return ERROR;
    }

    private int checkAck() {

        try {
            if (!mST25DVTag.hasHostPutMsg(true))
                return TRY_AGAIN;
        } catch (STException e) {
            return checkError(e);
        }

        byte[] response;
        int size = -1;

        STLog.i("checkAck");

        try {
            response = readMessage();

            if ((response != null) && (response.length >= 2) && (response[2] == 0x00)) {
                STLog.i("Tag acknowledge that CRC is OK");
                return OK;
            }

        } catch (STException e) {
            return checkError(e);
        }

        return ERROR;

    }

    private int checkPasswordAnswer() {
        try {
            if (!mST25DVTag.hasHostPutMsg(true))
                return TRY_AGAIN;

        } catch (STException e) {
            return checkError(e);
        }

        byte[] response;
        int size = -1;

        try {

            response = readMessage();

            if (response == null)
                return ERROR;

            if (response.length >= 3) {
                if (response[2] == 0x00)
                    return OK;
                else return ERROR;
            }


        } catch (STException e) {
            return checkError(e);
        }

        return OK;
    }


    private int checkCrc() {
        // Wait that the tag becomes ready to provide the CRC
        try {
            if (!mST25DVTag.hasHostPutMsg(true))
                return TRY_AGAIN;

        } catch (STException e) {
            return checkError(e);
        }

        try {
            // Compute the CRC on all the data received
            long crc = computeCrc();

            // Read the CRC provided by the tag
            byte[] response = readMessage();

            if ((response != null) && (response.length >= 9)) {
                if ((response[4] & 0xFF) == 4) {
                    if ( (response[5] == (byte) ((crc & 0xFF000000) >> 24)) &&
                            (response[6] == (byte) ((crc & 0x00FF0000) >> 16)) &&
                            (response[7] == (byte) ((crc & 0x0000FF00) >> 8)) &&
                            (response[8] == (byte) ((crc & 0x000000FF) ) ) ) {
                        STLog.i("CRC ok");
                        return OK;
                    } else {
                        STLog.e("Incorrect CRC!");
                    }
                } else {
                    STLog.e("checkCRC: Invalid response: " + Helper.convertHexByteArrayToString(response));
                }
            } else {
                STLog.e("checkCRC: Invalid response");
            }
            return ERROR;

        } catch (STException e) {
            return checkError(e);
        }
    }

    private int sendAck(boolean success, byte function) {

        try {
            if (mST25DVTag.hasRFPutMsg(true))
                return TRY_AGAIN;

        } catch (STException e) {
            return checkError(e);
        }

        byte response;
        byte[] frame = new byte[5];

        frame[0] = function;
        frame[1] = FAST_TRANSFER_ACK;
        if (success) {
            STLog.i("sendAck: FAST_TRANSFER_OK");
            frame[2] = FAST_TRANSFER_OK;
        } else {
            STLog.i("sendAck: FAST_TRANSFER_ERROR");
            frame[2] = FAST_TRANSFER_ERROR;
        }
        frame[3] = 0x00; // Chained
        frame[4] = 0;

        try {
            response = mST25DVTag.writeMailboxMessage(5, frame);
            if (response != (byte) 0x00)
                return ERROR;

        } catch (STException e) {
            return checkError(e);
        }

        return OK;

    }

    public void stop() {
        mEvent = ST25DVTransferEvent.STOP;
    }

    public void resume() {
        mEvent = ST25DVTransferEvent.RESUME;
    }

    public void start() {
        mEvent = ST25DVTransferEvent.START;
    }

    public void pause() {
        mEvent = ST25DVTransferEvent.PAUSE;
    }
}

