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
 * BtLeRecord is the class in charge of reading/writing Bluetooth Low Energy NDEF records.
 * Those records are used to implement Bluetooth Secure Simple Pairing (SSP) using NFC.
 * SSP describes the notion of OOB (Out Of Band) pairing, which exchanges Bluetooth OOB
 * data over the NFC channel.
 *
 * Reference document: NFCForum-AD-BluetoothSSP
 *
 * @author STMicroelectronics, (c) December 2016
 *
 */
public class BtLeRecord extends NDEFRecord {

    private String mBtDeviceName;
    private byte[] mBtMacAddress;
    private byte mBtMacAddressType;
    private byte[] mBtDeviceClass;
    private byte[] mBtUuidClassList;
    private byte mBtUuidClass;
    private byte[] mBtRoleList;
    private byte mBtRole;
    private byte[] mBtAppearenceData;
    private byte mBtAppearence;

    private byte[] mBuffer;

    /**
     * Constructor with no parameter
     */
    public BtLeRecord() {
        super();

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(NDEFRecord.RTD_BTLE_APP);

        mBtDeviceName = "";
        mBtMacAddress = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        mBtDeviceClass = null;
        mBtUuidClassList = null;
        mBtUuidClass = 0x00;
    }

    /**
     * BtLeRecord constructor.
     *
     * @param deviceName
     * @param macAddr Bluetooth Device address of the device to pair (Little Endian)
     * @param deviceClass Class of the Bluetooth device
     * @param uuidClass Service class information used to identify services supported by the device
     * @param uidClassList
     */
    public BtLeRecord(String deviceName,
                      byte[] macAddr,
                      byte[] deviceClass,
                      byte[] uuidClass,
                      byte uidClassList) {
        super();

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(NDEFRecord.RTD_BTLE_APP);

        mBtDeviceName = deviceName;
        mBtMacAddress = Arrays.copyOf(macAddr, macAddr.length);
        mBtDeviceClass = Arrays.copyOf(deviceClass, deviceClass.length);
        mBtUuidClassList = Arrays.copyOf(uuidClass, uuidClass.length);
        mBtUuidClass = uidClassList;
        setSR();
    }

    public BtLeRecord(ByteArrayInputStream inputStream) throws Exception{
        super(inputStream);
        byte[] payload = super.getPayload();

        if (payload == null) {
            throw new Exception("Invalid ndef data");
        }

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(NDEFRecord.RTD_BTLE_APP);

        parse(ByteBuffer.wrap(payload));

        if(DBG_NDEF_RECORD) {
            dbgCheckNdefRecordContent(payload);
        }
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
        return mBtMacAddress;
    }

    public byte[] getBTDeviceClass() {
        return mBtDeviceClass;
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
        mBtMacAddress = Arrays.copyOf(macAddr, macAddr.length);
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

    /**
     * @param type
     */
    public void setBTDeviceMacAddrType(byte type) {
        mBtMacAddressType = type;
    }

    /**
     * @return
     */
    public byte getBTDeviceMacAddrType() {
        return mBtMacAddressType;
    }

    /**
     * @param roleList
     */
    public void setBTRoleList(byte[] roleList) {
        mBtRoleList = roleList;
    }

    /**
     * @return
     */
    public byte[] getBTRoleList() {
        return mBtRoleList ;
    }

    /**
     * @param appearence
     */
    public void setBTAppearence(byte[] appearence) {
        mBtAppearenceData = appearence;
    }

    /**
     * @return
     */
    public byte[] getBTAppearence() {
        return mBtAppearenceData;
    }

    public byte getBTUuidClass() {
        return mBtUuidClass;
    }

    public byte[] getBTUuidClassList() {
        return mBtUuidClassList;
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
     * Builds a NDEF Bluetooth Low Energy record from a buffer.
     * Exceptions caused by invalid Bluetooth MAC address or
     * payload size too small are recorded in a log and discarded.
     *
     * @param payload
     */
    private void parse(ByteBuffer payload) throws Exception {

        try {

            payload.position(0);

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
                    case 0x1B:
                        byte[] address = new byte[6];
                        payload.get(address);
                        // ByteBuffer.order(LITTLE_ENDIAN) doesn't work for
                        // ByteBuffer.get(byte[]), so manually swap order
                        // Do not display last byte (public or random in case of BTLE)
                        for (int i = 0; i < 3; i++) {
                            byte temp = address[i];
                            address[i] = address[5 - i];
                            address[5 - i] = temp;
                        }
                        mBtMacAddress = address.clone();
                        mBtMacAddressType = payload.get();

                        break;
                    case 0x1C:
                        mBtRole = (byte) type;
                        mBtRoleList = new byte[len - 1];
                        payload.get(mBtRoleList);
                        break;
                    case 0x19:
                        mBtAppearence = (byte) type;
                        mBtAppearenceData = new byte[len -1];
                        payload.get(mBtAppearenceData);
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

        if (mBtMacAddress != null) {
            //We add mac addr type @ the end
            macAddr = new byte[mBtMacAddress.length + 1];
            for (int i = 0; i < mBtMacAddress.length; i++) {
                macAddr[i] = mBtMacAddress[mBtMacAddress.length - i - 1];
            }
            macAddr[mBtMacAddress.length] = mBtMacAddressType;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] eirBuffer;

        if (macAddr != null) {
            eirBuffer = fillEirBuffer(macAddr, (byte) 0x1B);
            outputStream.write(eirBuffer, 0, eirBuffer.length);
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

        if (mBtAppearenceData != null) {
            eirBuffer = fillEirBuffer(mBtAppearenceData, (byte) 0x19);
            outputStream.write(eirBuffer, 0, eirBuffer.length);
        }

        if (mBtRoleList != null) {
            eirBuffer = fillEirBuffer(mBtRoleList, (byte) 0x1C);
            outputStream.write(eirBuffer, 0, eirBuffer.length);
        }

        mBuffer = outputStream.toByteArray();
    }
}
