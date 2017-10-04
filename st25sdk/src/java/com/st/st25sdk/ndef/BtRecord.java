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

import com.st.st25sdk.STLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;


/**
 * BtRecord is the class in charge of reading/writing Bluetooth NDEF records.
 * Those records are used to implement Bluetooth Secure Simple Pairing (SSP) using NFC.
 * SSP describes the notion of OOB (Out Of Band) pairing, which exchanges Bluetooth OOB
 * data over the NFC channel.
 *
 * Reference document: NFCForum-AD-BluetoothSSP
 *
 * @author STMicroelectronics, (c) December 2016
 *
 */
public class BtRecord extends NDEFRecord {

    private String mBtDeviceName;
    private byte[] mBtMacAddr;
    private byte[] mBtDeviceClass;
    private byte[] mBtUuidClassList;
    private byte mBtUuidClass;
    private byte[] mBuffer;

    /**
     * Constructor with no parameter.
     */
    public BtRecord() {
        super();

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(NDEFRecord.RTD_BT_APP);

        mBtDeviceName = "";
        mBtMacAddr = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        mBtDeviceClass = null;
        mBtUuidClassList = null;
        mBtUuidClass = 0x00;
    }

    /**
     * BtRecord constructor.
     *
     * @param deviceName
     * @param macAddr Bluetooth Device address of the device to pair (Little Endian)
     * @param deviceClass Class of the Bluetooth device
     * @param uuidClass Service class information used to identify services supported by the device
     * @param uidClassList
     */
    public BtRecord(String deviceName,
                    byte[] macAddr,
                    byte[] deviceClass,
                    byte[] uuidClass,
                    byte uidClassList) {
        super();

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(NDEFRecord.RTD_BT_APP);

        mBtDeviceName = deviceName;
        mBtMacAddr = Arrays.copyOf(macAddr, macAddr.length);
        mBtDeviceClass = Arrays.copyOf(deviceClass, deviceClass.length);
        mBtUuidClassList = Arrays.copyOf(uuidClass, uuidClass.length);
        mBtUuidClass = uidClassList;

        setSR();
    }

    BtRecord(ByteArrayInputStream inputStream) throws Exception {
        super(inputStream);
        byte[] payload = super.getPayload();

        if (payload == null) {
            throw new Exception("Invalid ndef data");
        }

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(NDEFRecord.RTD_BT_APP);

        parse(ByteBuffer.wrap(payload));

        if(DBG_NDEF_RECORD) {
            dbgCheckNdefRecordContent(payload);
        }
    }

    public byte[] getBTDeviceClass() {
        return mBtDeviceClass;
    }

    public byte getBtUuidClass() {
        return mBtUuidClass;
    }

    public byte[] getBtUuidClassList() {
        return mBtUuidClassList;
    }

    /**
     * @return
     */
    public String getBTDeviceName() {
        return mBtDeviceName;
    }

    /**
     * @return
     */
    public byte[] getBTDeviceMacAddr() {
        return mBtMacAddr;
    }

    /**
     * @param name
     */
    public void setBTDeviceName(String name) {
        mBtDeviceName = name;
    }

    /**
     * @param macAddr Bluetooth device MAC address
     */
    public void setBTDeviceMacAddr(byte[] macAddr) {
        mBtMacAddr = Arrays.copyOf(macAddr, macAddr.length);
    }

    /**
     * @param uuidClassList
     */
    public void setBTUuidClassList(byte[] uuidClassList) {
        mBtUuidClassList = uuidClassList;
    }

    /**
     * @param deviceClass
     */
    public void setBTDeviceClass(byte[] deviceClass) {
        mBtDeviceClass = deviceClass;
    }

    /**
     * @param uuidClass
     */
    public void setBtUuidClass(byte uuidClass) {
        mBtUuidClass = uuidClass;
    }

    // NB: The Payload is computed on the fly every times we need it
    @Override
    public byte[] getPayload() {
        byte[] payload = null;

        export2Bt();

        if (mBuffer != null) {
            //setId(new byte[0]);
            payload = mBuffer;
        }

        return payload;
    }

    /**
     * Builds a NDEF Bluetooth record from a buffer.
     * Exceptions caused by invalid Bluetooth MAC address or
     * payload size too small are recorded in a log and discarded.
     *
     * @param payload
     */
    private void parse(ByteBuffer payload) throws Exception {

        try {
            payload.position(2);
            byte[] address = new byte[6];
            payload.get(address);
            // ByteBuffer.order(LITTLE_ENDIAN) doesn't work for
            // ByteBuffer.get(byte[]), so manually swap order
            for (int i = 0; i < 3; i++) {
                byte temp = address[i];
                address[i] = address[5 - i];
                address[5 - i] = temp;
            }
            mBtMacAddr = address.clone();
            mBtDeviceName = null;

            while (payload.remaining() > 0) {
                byte[] nameBytes;

                int len = payload.get();
                int type = payload.get();

                if(len <= 0) {
                    throw new Exception("Invalid ndef data");
                }

                switch (type) {
                    case 0x08:  // short local name
                        nameBytes = new byte[len - 1];
                        payload.get(nameBytes);
                        mBtDeviceName = new String(nameBytes, Charset.forName("UTF-8"));
                        break;
                    case 0x09:  // long local name
                        if (mBtDeviceName != null) break;  // prefer short name
                        nameBytes = new byte[len - 1];
                        payload.get(nameBytes);
                        mBtDeviceName = new String(nameBytes, Charset.forName("UTF-8"));
                        break;
                    case 0x0D: //Class of device - 3 Bytes with Service Class / Major Device Class / Minor Device Class
                        mBtDeviceClass = new byte[3];
                        payload.get(mBtDeviceClass);
                        break;
                    case 0x02: //16-bit un-complete Service Class UUID list
                        mBtUuidClass = (byte) 0x02;
                        mBtUuidClassList = new byte[len - 1];
                        payload.get(mBtUuidClassList);
                        break;
                    case 0x03://16-bit complete Service Class UUID list
                    case 0x04: //32-bit complete Service Class UUID list
                    case 0x05://64-bit un-complete Service Class UUID list
                    case 0x06://128-bit un-complete Service Class UUID list
                    case 0x07://256-bit un-complete Service Class UUID list
                        mBtUuidClass = (byte) type;
                        mBtUuidClassList = new byte[len - 1];
                        payload.get(mBtUuidClassList);
                        break;
                    default:
                        payload.position(payload.position() + len - 1);
                        break;
                }
            }
        } catch (IllegalArgumentException e) {
            STLog.e("BT OOB: invalid BT address");
        } catch (BufferUnderflowException e) {
            STLog.e("BT OOB: payload shorter than expected");
        }
    }


    /**
     * @param input
     * @param id
     * @return
     */
    private byte[] fillEirBuffer(byte[] input, byte id) {

        if ((input != null) && (input.length != 0)) {
            ByteBuffer output;
            // eir data length = 1 + eir data local type name = 1
            output = ByteBuffer.allocate(1 + 1 + input.length);
            // size of payload
            output.put((byte) ((1 + input.length) & 0xFF));
            output.put(id); // Eir Complete local name type
            output.put(input, 0, input.length);
            return output.array();
        }

        return null;
    }

    /**
     * Prepares mBuffer EIR data for transmission to the Bluetooth device
     */
    private void export2Bt() {
        mBuffer = null;
        byte[] macAddr = null;

        if (mBtMacAddr != null) {
            macAddr = new byte[mBtMacAddr.length];
            for (int i = 0; i < mBtMacAddr.length; i++) {
                macAddr[i] = mBtMacAddr[mBtMacAddr.length - i - 1];
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] eirBuffer;

        if (macAddr != null) {
            outputStream.write(macAddr, 0, macAddr.length);
        }

        if (mBtDeviceName != null && mBtDeviceName.length() > 0) {
            eirBuffer = fillEirBuffer(mBtDeviceName.getBytes(), (byte) 0x09);
            outputStream.write(eirBuffer, 0, eirBuffer.length);
        }

        if (mBtDeviceClass != null) {
            eirBuffer = fillEirBuffer(mBtDeviceClass, (byte) 0x0D);
            outputStream.write(eirBuffer, 0, eirBuffer.length);
        }

        if (mBtUuidClassList != null) {
            eirBuffer = fillEirBuffer(mBtUuidClassList, mBtUuidClass);
            outputStream.write(eirBuffer, 0, eirBuffer.length);
        }

        // oob optionalDataLength = 2;
        int size = outputStream.size() + 2;
        if (size > 0) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            byteBuffer.put((byte) (size & 0xFF));
            byteBuffer.put((byte) ((size & 0xFF00) >> 8));
            byteBuffer.put(outputStream.toByteArray());
            mBuffer = byteBuffer.array();
        }
    }
}
