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

package com.st.st25android;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.os.Looper;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.ndef.NDEFMsg;

import java.util.Arrays;
import java.util.List;


public class AndroidReaderInterface implements RFReaderInterface {

    protected Tag mAndroidTag;
    public NFCTag.NfcTagTypes mTagType;
    public AndroidNdefInterface mNdefInterface;
    protected int mRetryCounter = 0;
    protected TransceiveMode mTransceiveMode = TransceiveMode.NORMAL;

    public static AndroidReaderInterface newInstance(Tag tag) {

        NFCTag.NfcTagTypes type = decodeTagType(tag);


        switch (type) {
            case NFC_TAG_TYPE_V: {
                AndroidType5ReaderInterface tagInterface = new AndroidType5ReaderInterface(tag);
                tagInterface.mTagType = type;
                tagInterface.mNdefInterface = AndroidNdefInterface.newInstance(tagInterface);
                return tagInterface;
            }
            case NFC_TAG_TYPE_4A:
            case NFC_TAG_TYPE_4B:
            case NFC_TAG_TYPE_2: {
                AndroidType4ReaderInterface tagInterface = new AndroidType4ReaderInterface(tag);
                tagInterface.mTagType = type;
                tagInterface.mNdefInterface = AndroidNdefInterface.newInstance(tagInterface);
                return tagInterface;
            }
            case NFC_TAG_TYPE_A: {
                AndroidNFCAReaderInterface tagInterface = new AndroidNFCAReaderInterface(tag);
                tagInterface.mTagType = type;
                tagInterface.mNdefInterface = AndroidNdefInterface.newInstance(tagInterface);
                return tagInterface;
            }
            case NFC_TAG_TYPE_B: {
                AndroidNFCBReaderInterface tagInterface = new AndroidNFCBReaderInterface(tag);
                tagInterface.mTagType = type;
                tagInterface.mNdefInterface = AndroidNdefInterface.newInstance(tagInterface);
                return tagInterface;
            }
            case NFC_TAG_TYPE_F:
            default:
                break;
        }

        return null;
    }

    public AndroidReaderInterface(Tag tag) {
        mAndroidTag = tag;
    }

    public void setRetryCounter(int retryCounter) throws STException {
        if(retryCounter < 0) {
            throw new STException(STException.STExceptionCode.BAD_PARAMETER);
        }

        mRetryCounter = retryCounter;
    }

    @Override
    public byte[] transceive(Object obj, String commandName, byte[] data) throws STException {
        return null;
    }

    @Override
    public void setTransceiveMode(TransceiveMode mode) {
        mTransceiveMode = mode;
    }

    @Override
    public List<byte[]> getTransceivedData() {
        return null;
    }

    @Override
    public byte[] getLastTransceivedData() {
        return new byte[0];
    }

    @Override
    public TransceiveMode getTransceiveMode() {
        return mTransceiveMode;
    }

    @Override
    public NFCTag.NfcTagTypes decodeTagType(byte[] uid) throws STException {
        return decodeTagType(mAndroidTag);
    }

    public byte[] getId() {
        return mAndroidTag.getId();
    }

    public static NFCTag.NfcTagTypes decodeTagType(Tag tag) {
        NFCTag.NfcTagTypes lType = NFCTag.NfcTagTypes.NFC_TAG_TYPE_UNKNOWN;
        List<String> lTechList = Arrays.asList(tag.getTechList());
        String nfcTechPrefixStr = "android.nfc.tech.";
        // Try the Ndef technology
        Ndef lNdefTag = Ndef.get(tag);
        if (lNdefTag != null) {
            if (lNdefTag.getType().equals(Ndef.NFC_FORUM_TYPE_1)) {
                lType = NFCTag.NfcTagTypes.NFC_TAG_TYPE_1;
            } else if (lNdefTag.getType().equals(Ndef.NFC_FORUM_TYPE_2)) {
                lType = NFCTag.NfcTagTypes.NFC_TAG_TYPE_2;
            } else if (lNdefTag.getType().equals(Ndef.NFC_FORUM_TYPE_3)) {
                lType = NFCTag.NfcTagTypes.NFC_TAG_TYPE_3;
            } else if (lNdefTag.getType().equals(Ndef.NFC_FORUM_TYPE_4)) {
                if (lTechList.contains(nfcTechPrefixStr + "NfcA")) {
                    lType = NFCTag.NfcTagTypes.NFC_TAG_TYPE_4A;
                } else if (lTechList.contains(nfcTechPrefixStr + "NfcB")) {
                    lType = NFCTag.NfcTagTypes.NFC_TAG_TYPE_4B;
                }
            } else if (lTechList.contains(nfcTechPrefixStr + "NfcV")) {
                lType = NFCTag.NfcTagTypes.NFC_TAG_TYPE_V;
            }
        } else {
            // Try the IsoDep technology
            IsoDep lIsoDepTag = IsoDep.get(tag);
            if (lIsoDepTag != null) {
                if (lTechList.contains(nfcTechPrefixStr + "NfcA")) {
                    lType = NFCTag.NfcTagTypes.NFC_TAG_TYPE_4A;
                } else if (lTechList.contains(nfcTechPrefixStr + "NfcB")) {
                    lType = NFCTag.NfcTagTypes.NFC_TAG_TYPE_4B;
                }
            } else {
                // Try the underlying technologies
                if (lTechList.contains(nfcTechPrefixStr + "NfcA")) {
                    lType = NFCTag.NfcTagTypes.NFC_TAG_TYPE_A;
                } else if (lTechList.contains(nfcTechPrefixStr + "NfcB")) {
                    lType = NFCTag.NfcTagTypes.NFC_TAG_TYPE_B;
                } else if (lTechList.contains(nfcTechPrefixStr + "NfcF")) {
                    lType = NFCTag.NfcTagTypes.NFC_TAG_TYPE_F;
                } else if (lTechList.contains(nfcTechPrefixStr + "NfcV")) {
                    lType = NFCTag.NfcTagTypes.NFC_TAG_TYPE_V;
                }
            }
        }

        return lType;
    }

    public Tag getTag() {
        return mAndroidTag;
    }

    public String[] getTechList(byte[] uid) {
        return mAndroidTag.getTechList();
    }

    @Override
    public int getMaxTransmitLengthInBytes() {
        return 0;
    }

    @Override
    public int getMaxReceiveLengthInBytes() {
        return 0;
    }

    /**
     * Function indicating if the current thread is the UI Thread
     *
     * @return
     */
    public  boolean isUIThread() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            // On UI thread.
            return true;
        } else {
            // Not on UI thread.
            return false;
        }
    }

    /**
     * Function used to write a NDEF message in the tag through Android's NDEF class
     *
     * NB: This function is not used anymore
     *
     * @param TAG
     * @param msg
     * @throws STException
     */
    public void writeNdefMessage(String TAG, NDEFMsg msg) throws STException {
        if (mNdefInterface != null)
            mNdefInterface.writeNdefMessage(TAG, msg);
    }

    /**
     * Function used to read a NDEF message from the tag through Android's NDEF class
     *
     * NB: This function is not used anymore
     *
     * @param TAG
     * @return
     * @throws STException
     */
    public NDEFMsg readNdefMessage(String TAG) throws STException {
        if (mNdefInterface != null)
            return mNdefInterface.readNdefMessage(TAG);
        else
            return null;
    }


    /*********************************************************************************
     *
     *                          Multi-tag management
     *
     *********************************************************************************/
    @Override
    public List<byte[]> inventory(InventoryMode mode) throws STException {
        return null;
    }

}
