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

package com.st.st25sdk.ndef;

import com.st.st25sdk.Helper;
import com.st.st25sdk.STLog;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class NDEFRecord implements Serializable {

    // Enable this if you want to enable debug code checking that NDEFRecord decoding followed by a reconstruction leads to the original byte array.
    public static boolean DBG_NDEF_RECORD = false;

    private boolean mMB;         // Message Begin - set in the first record
    private boolean mME;         // Message End - set in the last record
    private boolean mCF;         // Chunk Flag - set either in the first record chunck or in the middle record chunck.
    private boolean mSR;         // Short record flag.
    private boolean mIL;         // set if mIDLength (ID_Length) field is present in the header as a single octet

    private short mTnf;          // indicates the structure of the value of the mtype field
    private int mTypeLength;     // specifies the length in octets of the type field

    private int mIDLength;       // 8-bit integer specifying the length in octets of the ID field
    private byte[] mType;        // type field that must follow the structure, encoding and format implied by mTnf
    private byte[] mID;          // Identifier in the form of a URI reference [RFC 3986] - middle, final records must not have this field
    private byte[] mPayload;     // Application payload field


    public static final byte[] RTD_TEXT = {0x54};
    public static final byte[] RTD_URI = {0x55};
    public static final byte[] RTD_SMART_POSTER = {0x53, 0x70};
    public static final byte[] RTD_ALTERNATIVE_CARRIER = {0x61, 0x63};
    public static final byte[] RTD_HANDOVER_CARRIER = {0x48, 0x63};
    public static final byte[] RTD_HANDOVER_REQUEST = {0x48, 0x72};
    public static final byte[] RTD_HANDOVER_SELECT = {0x48, 0x73};
    public static final byte[] RTD_ANDROID_APP = "android.com:pkg".getBytes();

    public static final byte[] RTD_BTLE_APP = "application/vnd.bluetooth.le.oob".getBytes();
    public static final byte[] RTD_BT_APP = "application/vnd.bluetooth.ep.oob".getBytes();
    public static final byte[] RTD_VCARD_APP = "text/x-vCard".getBytes();
    public static final byte[] RTD_WIFI_APP = "application/vnd.wfa.wsc".getBytes();

    public static final byte[] RTD_SMS = "sms:".getBytes();

    public static final short TNF_EMPTY = 0x00;
    public static final short TNF_WELLKNOWN = 0x01;
    public static final short TNF_MEDIA = 0x02;
    public static final short TNF_URI = 0x03;
    public static final short TNF_EXTERNAL = 0x04;
    public static final short TNF_UNKNOWN = 0x05;
    public static final short TNF_UNCHANGED = 0x06;
    public static final short TNF_RFU = 0x07;


    public NDEFRecord() {
        mMB = mME = true;
        mCF = mIL = false;
        mSR = true;
        mTnf = TNF_EMPTY;
        mTypeLength = mIDLength = 0;
        mType = mID;
    }

    /**
     * Used when creating a NDEFRecord from another NDEFRecord.
     * @param record
     */
    public NDEFRecord(NDEFRecord record) {
        mMB = record.getMB();
        mME = record.getME();
        mCF = record.getCF();
        mIL = record.getIL();
        mSR = record.getSR();

        mTnf = record.getTnf();

        mTypeLength = record.getTypeLength();
        mIDLength = record.getIDLength();
        mType = record.getType();
        mID = record.getID();

    }

    /**
     * Constructor used when initializing a NDEFRecord from a byte array.
     * @param buffer
     * @throws Exception
     */
    public NDEFRecord(byte[] buffer) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
        ndefRecordInit(byteArrayInputStream);
    }

    public NDEFRecord(ByteArrayInputStream  inputStream) throws Exception {
        ndefRecordInit(inputStream);
    }

    public int getSize() throws Exception {

        int payloadLength = getPayloadLength();
        //first byte + type length
        int size = 1 + 1 + mTypeLength;

        if (mIL)
            size += 1 + mIDLength;

        if ((payloadLength > 255) && mSR)
            throw  new Exception("Record bad formatted");

        size += payloadLength;

        if (mSR)
            return size + 1;
        else
            return size + 4;
    }

    /**
     * Internal function doing the parsing of NDEF Record data passed to the constructor.
     * @param byteArrayInputStream
     * @throws Exception
     */
    private void ndefRecordInit(ByteArrayInputStream byteArrayInputStream) throws Exception {
        int payloadLength;

        /*
                  NDEF Record Header:
        |---------------------------------------|
        |  7 |  6 |  5 |  4 |  3 |  2 |  1 |  0 |
        | MB | ME | CF | SR | IL |      TNF     |
        |            TYPE LENGTH                |
        |            PAYLOAD LENGTH 3           |
        |            PAYLOAD LENGTH 2           |
        |            PAYLOAD LENGTH 1           |
        |            PAYLOAD LENGTH 0           |
        |            ID LENGTH                  |
        |            TYPE                       |
        |            ID                         |
        |            PAYLOAD                    |
        |---------------------------------------|
        */

        // Read the byte containing the info about this record (MB, ME, TNF...etc)
        byte recordCtx = readNextByte(byteArrayInputStream);

        mMB = ((recordCtx & 0x80) == 0x80);
        mME = ((recordCtx & 0x40) == 0x40);
        mCF = ((recordCtx & 0x20) == 0x20);
        mSR = ((recordCtx & 0x10) == 0x10);
        mIL = ((recordCtx & 0x08) == 0x08);

        switch (recordCtx & 0x07) {
            case 0x00:
                mTnf = NDEFRecord.TNF_EMPTY;
                break;
            case 0x01:
                mTnf = NDEFRecord.TNF_WELLKNOWN;
                break;
            case 0x02:
                mTnf = NDEFRecord.TNF_MEDIA;
                break;
            case 0x03:
                mTnf = NDEFRecord.TNF_URI;
                break;
            case 0x04:
                mTnf = NDEFRecord.TNF_EXTERNAL;
                break;
            case 0x05:
                mTnf = NDEFRecord.TNF_UNKNOWN;
                break;
            case 0x06:
                mTnf = NDEFRecord.TNF_UNCHANGED;
                break;
            case 0x07:
                mTnf = NDEFRecord.TNF_RFU;
                break;
            default:
                mTnf = NDEFRecord.TNF_RFU;
                break;
        }

        mTypeLength = readNextByte(byteArrayInputStream) & 0xFF;

        if (mSR) {
            // 1-byte payload length
            payloadLength = readNextByte(byteArrayInputStream) & 0xFF;
        } else {
            // 4-byte payload length
            byte payloadLength3 = readNextByte(byteArrayInputStream);
            byte payloadLength2 = readNextByte(byteArrayInputStream);
            byte payloadLength1 = readNextByte(byteArrayInputStream);
            byte payloadLength0 = readNextByte(byteArrayInputStream);

            payloadLength = ((payloadLength3 & 0xFF) << 24) +
                             ((payloadLength2 & 0xFF) << 16) +
                             ((payloadLength1 & 0xFF) <<  8) +
                             (payloadLength0 & 0xFF);
        }

        if (mIL) {
            // ID Length's field is present in the record
            mIDLength = readNextByte(byteArrayInputStream) & 0xFF;
        } else {
            mIDLength = 0;
        }

        // Move to Type field
        if (mTypeLength != 0) {
            mType = readNextBytes(byteArrayInputStream, 0, mTypeLength);
        }

        if (mIDLength != 0) {
            mID = readNextBytes(byteArrayInputStream, 0, mIDLength);
        }

        if (payloadLength != 0) {
            mPayload = readNextBytes(byteArrayInputStream, 0, payloadLength);
        } else {
            STLog.w("Warning: Payload is null!");
        }
    }


    public void setMB(boolean flag) {
        mMB = flag;
    }

    public void setME(boolean flag) {
        mME = flag;
    }

    public void setCF(boolean flag) {
        mCF = flag;
    }

    public void setIL(boolean flag) {
        mIL = flag;
    }

    public void setSR(boolean flag) throws Exception {
        if(flag && (getPayloadLength() > 255)) {
            throw new Exception("Payload too long");
        }
        mSR = flag;
    }

    protected void setSR() {
        int payloadLength;

        try {
            payloadLength = getPayloadLength();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        mSR = (payloadLength <= 255);
    }

    void setTnf(short tnf) {
        mTnf = tnf;
    }

    public void setTypeLength(int length) {
        mTypeLength = length;
    }

    public void setIDLength(int length) {
        mIDLength = length;
        if (length != 0)
            setIL(true);
    }

    public void setType(byte[] type) {
        mType  = Arrays.copyOf(type, type.length);
        mTypeLength = type.length;
    }

    public void setId(byte[] id) {
        mID = Arrays.copyOf(id, id.length);
        mIDLength = id.length;
        setIL(true);
    }

    public boolean getMB() {
        return mMB;
    }

    public boolean getME() {
        return mME;
    }

    public boolean getCF() {
        return mCF;
    }

    public boolean getSR() {
        return mSR;
    }

    public boolean getIL() {
        return mIL;
    }

    public short getTnf() {
        return mTnf;
    }

    public int getTypeLength() {
        return mTypeLength;
    }

    public int getPayloadLength() throws Exception {
        // NB: PayloadLength is computed on the fly from the current payload
        byte[] payload = getPayload();
        if (payload != null)
            return payload.length;
        else
            return 0;
    }

    public int getIDLength() {
        return mIDLength;
    }

    public byte[] getType() {
        return mType;
    }

    public byte[] getID() {
        return mID;
    }

    public byte[] getPayload() throws Exception {
        return mPayload;
    }

    public byte[] serialize() throws Exception {

        byte header = (byte) 0x00;
        byte idLength = (byte) 0x00;
        byte typeLength = (byte) (mTypeLength & 0xFF);
        int ndefLength = 2;

        header |= (byte) mTnf;

        // Retrieve the payload, the payloadLend and the SR boolean (indicating if it is a Short Record)
        byte[] payload = getPayload();
        int payloadLength;

        if(payload != null) {
            payloadLength = payload.length;
        } else {
            payloadLength = 0;
        }

        //Force mSR to false
        if (payloadLength > 255) mSR = false;

        if (mMB)
            header |= (byte) 0x80;
        else
            header &= (byte) 0x7F;

        if (mME)
            header |= (byte) 0x40;
        else
            header &= (byte) 0xBF;

        if (mCF)
            header |= (byte) 0x20;
        else
            header &= (byte) 0xDF;

        if (mSR) {
            header |= (byte) 0x10;
            ndefLength++;
        } else {
            header &= (byte) 0xEF;
            ndefLength += 4;
        }

        if (mIL) {
            header |= (byte) 0x08;
            idLength = (byte) mIDLength;
            ndefLength += 1;
        } else
            header &= (byte) 0xF7;


        if (mTnf == TNF_EMPTY) {
            // In the case of an empty tnf the following fields are = 0 and ndefLength do not take into account Type/ID/Payload
            // Type/ID/Payload length  = 0 and value omitted from record
            return new byte[] {header, 0x00, 0x00, 0x00};

        } else {
            if (mType != null) ndefLength += mType.length;
            if (mID != null) ndefLength += mID.length;
            if (payload != null) ndefLength += payloadLength;

            ByteBuffer byteBuffer = ByteBuffer.allocate(ndefLength);

            byteBuffer.put(header);
            byteBuffer.put(typeLength);
            if (mSR) {
                byteBuffer.put((byte) (payloadLength & 0xFF));
            } else {
                byte payloadLengthField[] = {0x00, 0x00, 0x00, 0x00};

                payloadLengthField[0] = (byte) ((payloadLength & 0xFF000000) >> 24);
                payloadLengthField[1] = (byte) ((payloadLength & 0x00FF0000) >> 16);
                payloadLengthField[2] = (byte) ((payloadLength & 0x0000FF00) >> 8);
                payloadLengthField[3] = (byte) ((payloadLength & 0x000000FF));

                byteBuffer.put(payloadLengthField);
            }

            if (mIL) {
                byteBuffer.put((byte) (idLength & 0xFF));
            }

            if (mType != null) {
                byteBuffer.put(mType);
            }

            if (mID != null) {
                byteBuffer.put(mID);
            }

            if (payload != null) {
                byteBuffer.put(payload);
            }
            return byteBuffer.array();
        }
    }

    private byte readNextByte(ByteArrayInputStream byteArrayInputStream) throws Exception {
        int data = byteArrayInputStream.read();

        if(data == -1) {
            throw new Exception("Invalid ndef data");
        } else {
            return (byte) (data & 0xFF);
        }
    }

    private byte[] readNextBytes(ByteArrayInputStream byteArrayInputStream, int offset, int length) throws Exception {

        if ((length + offset) > byteArrayInputStream.available())
            throw new Exception("Invalid ndef data");

        byte[] data = new byte[length];

        int nbrOfBytesRead = byteArrayInputStream.read(data, offset, length);

        if(nbrOfBytesRead == length) {
            return data;
        } else {
            throw new Exception("Invalid ndef data");
        }
    }

    /**
     * This function is ONLY USED FOR DEBUG.
     *
     * It checks that the payload of this NDEF record is identical to the original NDEFRecord payload.
     * It allows to check that payload parsing and serialization are correct.
     *
     * @param originalPayload
     */
    protected void dbgCheckNdefRecordContent(byte[] originalPayload) {

        byte[] currentPayload = new byte[0];

        try {
            currentPayload = getPayload();

            if(!Arrays.equals(currentPayload, originalPayload)) {

                STLog.w(" ");
                STLog.w("Warning! "+ this + " doesn't look the same as the record that has been used to generate it!");
                STLog.w("Original payload : " + Helper.convertHexByteArrayToString(originalPayload));
                STLog.w("Record payload   : " + Helper.convertHexByteArrayToString(currentPayload));
                STLog.w(" ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

