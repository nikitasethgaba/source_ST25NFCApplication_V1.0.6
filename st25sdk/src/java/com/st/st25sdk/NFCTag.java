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

package com.st.st25sdk;


import com.st.st25sdk.ndef.NDEFMsg;

import java.util.Hashtable;

public abstract class NFCTag {

    private RFReaderInterface mReaderInterface = null;

    protected String mName;
    public byte[] mUid;
    public String mTypeDescription;
    public String mDescription;
    public int mMemSize;//in bytes

    protected TagAddressingMode mAddressingMode;
    public enum TagAddressingMode {
        NON_ADDRESSED,
        ADDRESSED,
        SELECT
    }

    protected NDEFMsg mNdefMsg;

    public enum NfcTagTypes {
        NFC_TAG_TYPE_UNKNOWN, NFC_TAG_TYPE_1, NFC_TAG_TYPE_2, NFC_TAG_TYPE_3, NFC_TAG_TYPE_4A,
        NFC_TAG_TYPE_4B, NFC_TAG_TYPE_A, NFC_TAG_TYPE_B, NFC_TAG_TYPE_F, NFC_TAG_TYPE_V
    }


    public static Hashtable<NfcTagTypes, String> NFCTagTypeDescription = new Hashtable<NfcTagTypes, String>() {
        {
            put(NfcTagTypes.NFC_TAG_TYPE_UNKNOWN, "Unknown tag type");
            put(NfcTagTypes.NFC_TAG_TYPE_1, "NFC Forum Type 1 tag");
            put(NfcTagTypes.NFC_TAG_TYPE_2, "NFC Forum Type 2 tag");
            put(NfcTagTypes.NFC_TAG_TYPE_3, "NFC Forum Type 3 tag");
            put(NfcTagTypes.NFC_TAG_TYPE_4A, "NFC Forum Type 4A tag");
            put(NfcTagTypes.NFC_TAG_TYPE_4B, "NFC Forum Type 4B tag");
            put(NfcTagTypes.NFC_TAG_TYPE_A, "ISO/IEC 14443A / ISO/IEC 18092 tag");
            put(NfcTagTypes.NFC_TAG_TYPE_B, "ISO/IEC 14443B tag");
            put(NfcTagTypes.NFC_TAG_TYPE_F, "FeliCa tag (JIS X6319-4)");
            put(NfcTagTypes.NFC_TAG_TYPE_V, "NFC Forum type V - ISO/IEC 15693 tag");
        }
    };

    public static String DYNAMIC_NFC_RFID_TAG = "Dynamic NFC/RFID tag";
    public static String NFC_RFID_TAG = "NFC/RFID tag";

    public NFCTag() {
    }

    public NFCTag(RFReaderInterface readerInterface) {
        mReaderInterface = readerInterface;
    }

    /**
     * @return the mReaderInterface
     */
    public RFReaderInterface getReaderInterface() {
        return mReaderInterface;
    }

    public abstract int getCCFileLength() throws STException;

    public abstract byte getCCMagicNumber() throws STException;

    public abstract byte getCCMappingVersion() throws STException;

    public abstract byte getCCReadAccess() throws STException;

    public abstract byte getCCWriteAccess() throws STException;

    public abstract int getCCMemorySize() throws STException;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        try {
            if (getUidString().equals(((NFCTag) obj).getUidString()))
                return true;
        } catch (STException e) {
            e.printStackTrace();
        }

        return false;
    }

    public abstract void writeNdefMessage(NDEFMsg msg) throws STException;

    public abstract NDEFMsg readNdefMessage() throws STException;

    public abstract byte[] readCCFile() throws STException;

    public abstract void writeCCFile() throws STException;

    public abstract void selectCCFile() throws STException;

    public abstract void initEmptyCCFile() throws STException;

    public abstract int getSysFileLength() throws STException;

    public  byte getICRef() throws STException {
        return 0;
    }

    public abstract int getMemSizeInBytes() throws STException;

    /**
     * Read a number of Bytes at a given address
     *
     * WARNING: In case of read error, the command will return what has been read so the byte array
     *          may contain less bytes than requested.
     *
     * @param byteAddress
     * @param sizeInBytes
     * @return
     * @throws STException
     */
    public abstract byte[] readBytes(int byteAddress, int sizeInBytes) throws STException;

    /**
     * Write some Bytes at a given address
     *
     * WARNING: After this write, the cache may not be aligned anymore with tag's memory content so
     *          it is recommended to invalidate the cache.
     *
     * @param byteAddress
     * @param data
     * @throws STException
     */
    public abstract void writeBytes(int byteAddress, byte[] data) throws STException;

    public String getManufacturerName() throws STException {
        return TagHelper.getManufacturerName(this);
    }

    public String getUidString() throws STException {
        return Helper.convertByteArrayToHexString(mUid);
    }

    public  byte[] getUid() throws STException {
        return mUid;
    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }

    public String getTypeDescription() {
        return mTypeDescription;
    }

    public String getDescription() {
        return mDescription;
    }

    public String[] getTechList() {
        return mReaderInterface.getTechList(mUid);
    }

    public NfcTagTypes getType() throws STException {
        NfcTagTypes tagType = NfcTagTypes.NFC_TAG_TYPE_UNKNOWN;
        try {
            tagType = mReaderInterface.decodeTagType(getUid());
        } catch (STException e) {
            e.printStackTrace();
        }
        return tagType;
    }

    public abstract byte[] readSysFile() throws STException;

}
