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
import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;


/**
 * WifiRecord is the class in charge of reading/writing WiFi NDEF records.
 * Those records are used to implement WiFi Direct Device Pairing using NFC.
 *
 * @author STMicroelectronics, (c) December 2016
 *
 */
public class WifiRecord extends NDEFRecord {

    private int mNetworkIndex;
    private String mSSID;
    private int mNetAuthType;
    private int mNetEncrType;
    private String mEncrKey;
    private String mMacAddr;
    private int mKeySharable;

    private byte[] mBuffer;

    /**
     *
     */
    public WifiRecord() {
        super();

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(NDEFRecord.RTD_WIFI_APP);

        mNetworkIndex = 1;
        mSSID ="";
        mNetAuthType = 0;
        mNetEncrType = 0;
        mEncrKey = "";
        mMacAddr = "";
        mKeySharable = 0;

        setId(new byte[0]);
    }

    /**
     * @param ssid
     * @param authType
     * @param encrType
     * @param password
     */
    public WifiRecord(String ssid,
                      int authType,
                      int encrType,
                      String password) {
        super();

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(NDEFRecord.RTD_WIFI_APP);

        mNetworkIndex = 1;
        mSSID = ssid;
        mNetAuthType = authType;
        mNetEncrType = encrType;
        mEncrKey = password;
        mMacAddr = "";
        mKeySharable = 0;

        setId(new byte[0]);
        setSR();
    }

    public WifiRecord(ByteArrayInputStream inputStream) throws Exception {
        super(inputStream);
        byte[] payload = super.getPayload();

        if (payload == null) {
            throw new Exception("Invalid ndef data");
        }

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(NDEFRecord.RTD_WIFI_APP);

        parse(ByteBuffer.wrap(payload));

        setId(new byte[0]);

        if(DBG_NDEF_RECORD) {
            dbgCheckNdefRecordContent(payload);
        }
    }


    /**
     * @param type
     */
    public void setAuthType(int type) {
        mNetAuthType = type;
    }

    /**
     * @return
     */
    public int getAuthType() {
        return mNetAuthType;
    }

    /**
     * @param type
     */
    public void setEncrType(int type) {
        mNetEncrType = type;
    }

    /**
     * @return
     */
    public int getEncrType() {
        return mNetEncrType;
    }

    /**
     * @param key
     */
    public void setEncrKey(String key) {
        mEncrKey = key;
    }

    /**
     * @return
     */
    public String getEncrKey() {
        return mEncrKey;
    }


    /**
     * @param ssid
     */
    public void setSSID(String ssid) {
        /*_mCredential.*/
        mSSID = ssid;
    }

    /**
     * @return
     */
    public String getSSID() {
        return mSSID;
    }

    // NB: The Payload is computed on the fly every times we need it
    @Override
    public byte[] getPayload() throws Exception {
        export2Wifi();
        return mBuffer;
    }

    /**
     * @param payload
     */
    private void parse(ByteBuffer payload) throws Exception {

        byte[] version;
        byte[] indexNet;
        byte[] SSID;
        byte[] authNet;
        byte[] encryptNet;
        byte[] networkKey;
        byte[] macAddr;
        byte[] vendorExtension;

        int temp;

        payload.position(0);
        try {
            while (payload.remaining() > 0) {

                if(payload.get() != (byte) 0x10) {
                    STLog.e("Missing ID Attribute");
                }

                byte type = payload.get();
                int len = ((payload.get() & 0xFF)<<8) + (payload.get() & 0xFF) ;

                if(len <= 0) {
                    throw new Exception("Invalid ndef data");
                }

                switch (type) {
                    case 0x4A:  // Version ID
                        version = new byte[len];
                        payload.get(version);
                        break;
                    case 0x26:  // Index Attribute
                        indexNet = new byte[len];
                        payload.get(indexNet);
                        temp = 0;
                        for (int i = 0; i < len; i++)
                        {
                            temp = temp << 8;
                            temp = temp + (indexNet[i] & 0xFF);
                        }
                        this.mNetworkIndex = temp;
                        break;
                    case 0x45:  // SSID Attributes
                        SSID = new byte[len];
                        payload.get(SSID);
                        try {
                            mSSID = new String(SSID, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0x03:  // Auth Attribute
                        authNet = new byte[len];
                        payload.get(authNet);
                        temp = 0;
                        for (int i = 0; i < len; i++)
                        {
                            temp = temp << 8;
                            temp = temp + (authNet[i] & 0xFF);
                        }
                        this.mNetAuthType = temp;
                        break;
                    case 0x0F:  // Encryp Attribute
                        encryptNet = new byte[len];
                        payload.get(encryptNet);
                        temp = 0;
                        for (int i = 0; i < len; i++)
                        {
                            temp = temp << 8;
                            temp = temp + (encryptNet[i] & 0xFF);
                        }
                        this.mNetEncrType = temp;
                        break;
                    case 0x27:  // Network key attribute
                        networkKey = new byte[len];
                        payload.get(networkKey);
                        try {
                            mEncrKey = new String(networkKey, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0x20:  // Mac Address Attribute
                        macAddr = new byte[len];
                        payload.get(macAddr);
                        try {
                            mMacAddr = new String(macAddr, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0x49:  // Vendor Extension attribute - we may have several vendor extensions
                        vendorExtension = new byte[len];
                        payload.get(vendorExtension);
                        break;
                    case 0x0E:  // Credential
                        break;
                    default:
                        payload.position(payload.position() + len - 1);
                        break;
                }
            }
        } catch (IllegalArgumentException e) {
            STLog.e("Wifi : invalid Wifi parameter");
        } catch (BufferUnderflowException e) {
            STLog.e("Wifi: payload shorter than expected");
        }
    }


    /**
     *
     */
    private void export2Wifi() throws Exception {
        mBuffer = null;

        byte[] version = {(byte) 0x10, (byte) 0x4A, (byte) 0x00, (byte) 0x01, (byte) 0x10};
        byte[] credential = {(byte) 0x10, (byte) 0x0E, (byte) 0x00, (byte) 0x00}; // to update once credential is built


        byte[] indexNet = {(byte) 0x10, (byte) 0x26, (byte) 0x00, (byte) 0x01, (byte) 0x01};
        ByteBuffer ssid;
        byte[] AttribIDSSID = {(byte) 0x10, (byte) 0x45};
        byte[] authNet = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00}; // open Config : 0x00 / WPAPSK : 0x01
        byte[] encryptNet = {(byte) 0x10, (byte) 0x0F, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00};
        ByteBuffer networkKey = null;
        byte[] defaultNetworkKey = {(byte) 0x10, (byte) 0x27};
        byte[] macAddr = {(byte) 0x10, (byte) 0x20, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        ByteBuffer vendorExtension;
        byte[] defaultVendorExtension1 = {(byte) 0x10, (byte) 0x49, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x37, (byte) 0x2A, (byte) 0x02, (byte) 0x01, (byte) 0x01};
        byte[] defaultVendorExtension2 = {(byte) 0x10, (byte) 0x49, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x37, (byte) 0x2A, (byte) 0x00, (byte) 0x01, (byte) 0x20};

        int idAttribLength = 2;
        int sizeParameterLength = 2;
        int credentialTokenLength = 0;

        credentialTokenLength += indexNet.length;

        if((mSSID == null) || (mSSID.getBytes() == null)) {
            throw new Exception("Invalid ndef data");
        }

        // SSID
        ssid = ByteBuffer.allocate(idAttribLength + sizeParameterLength + /*_mCredential.*/mSSID.getBytes().length);
        ssid.put(AttribIDSSID);
        ssid.put((byte) ((mSSID.getBytes().length & 0xFF00) >> 8));
        ssid.put((byte) ((mSSID.getBytes().length & 0xFF)));
        ssid.put(mSSID.getBytes());

        credentialTokenLength += ssid.position();

        // authNet
        authNet[5] = (byte) (mNetAuthType & 0xFF);
        credentialTokenLength += authNet.length;

        // encryptNet
        encryptNet[5] = (byte) (mNetEncrType & 0xFF);
        credentialTokenLength += encryptNet.length;

        // networkKey
        if ((mEncrKey != null) && (!mEncrKey.isEmpty())) {
            networkKey = ByteBuffer.allocate(idAttribLength + sizeParameterLength + mEncrKey.getBytes().length);
            networkKey.put(defaultNetworkKey);
            networkKey.put((byte) ((mEncrKey.getBytes().length & 0xFF00) >> 8));
            networkKey.put((byte) (mEncrKey.getBytes().length & 0xFF));
            networkKey.put(mEncrKey.getBytes());
            credentialTokenLength += networkKey.position();
        }

        // macAddr
        credentialTokenLength += macAddr.length;

        //Vendor Extension
        vendorExtension = ByteBuffer.allocate(defaultVendorExtension1.length + defaultVendorExtension2.length);
        vendorExtension.put(defaultVendorExtension1);
        vendorExtension.put(defaultVendorExtension2);
        credentialTokenLength += vendorExtension.position();

        credential[2] = (byte) ((credentialTokenLength & 0xFF00) >> 8);
        credential[3] = (byte) ((credentialTokenLength & 0xFF));

        int payloadLength = credentialTokenLength + version.length + credential.length;
        ByteBuffer buffer = ByteBuffer.allocate(payloadLength);

        buffer.put(version);
        buffer.put(credential);
        buffer.put(indexNet);
        buffer.put(ssid.array());
        buffer.put(authNet);
        buffer.put(encryptNet);

        if (networkKey != null) {
            buffer.put(networkKey.array());
        }

        buffer.put(macAddr);
        buffer.put(vendorExtension.array());

        mBuffer = buffer.array();

    }
}
