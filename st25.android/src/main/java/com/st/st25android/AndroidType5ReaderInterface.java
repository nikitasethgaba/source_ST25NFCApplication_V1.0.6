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
import android.nfc.tech.NfcV;
import android.util.Log;

import com.st.st25sdk.Helper;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;

import java.io.IOException;
import java.util.Arrays;

import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;
import static com.st.st25sdk.STException.STExceptionCode.TAG_CMD_CALLED_FROM_UI_THREAD;
import static com.st.st25sdk.STException.STExceptionCode.TAG_NOT_IN_THE_FIELD;
import static com.st.st25sdk.STException.STExceptionCode.TRANSCEIVE_EVAL_MODE;


public class AndroidType5ReaderInterface extends AndroidReaderInterface implements RFReaderInterface {

    static final boolean DBG = true;
    NfcV mTag;


    public AndroidType5ReaderInterface(Tag tag) {
        super(tag);
        mTag =  NfcV.get(tag);
    }

    public byte[] transceive(Object TAG, String commandName, byte[] data) throws STException {
        byte[] buffer;

        if (DBG) {
            if (isUIThread()) {
                throw new STException(TAG_CMD_CALLED_FROM_UI_THREAD);
            }
        }

        if (mTransceiveMode == TransceiveMode.EVAL) {
            String frame = String.format("==> EVAL " + commandName + " command: %s", Helper.convertHexByteArrayToString(data));
            Log.d((String) TAG, frame);

            byte[] dataArray = Arrays.copyOf(data, data.length);
            throw new STException(TRANSCEIVE_EVAL_MODE, dataArray);
        }

        // Ensure that one command at a time is executed
        if (!mTag.isConnected()) {
            try {
                mTag.close();
                mTag.connect();
            } catch (IOException e) {
                // Connection error. It is likely that the tag is no more in the field
                throw new STException(TAG_NOT_IN_THE_FIELD);
            }
        }

        int retryCounter = mRetryCounter;

        while (retryCounter >= 0) {
            try {
                if (DBG) {
                    String frame = String.format("==> Send " + commandName + " command: %s", Helper.convertHexByteArrayToString(data));
                    Log.d((String) TAG, frame);
                }


                buffer = mTag.transceive(data);
                if (DBG) {
                    String frame = String.format("Response: %s", Helper.convertHexByteArrayToString(buffer));
                    Log.d((String) TAG, frame);
                }

                return buffer;

            }
            catch (android.nfc.TagLostException e) {
                if(retryCounter == 0) {
                    throw new STException(TAG_NOT_IN_THE_FIELD);
                }
            }
            catch (Exception e) {
                // transceive command failed
                throw new STException(e);
            }

            Log.d((String) TAG, "Transceive failed. Trying again...");
            retryCounter --;
        }

        throw new STException(CMD_FAILED);
    }

    @Override
    public int getMaxTransmitLengthInBytes() {
        NfcV tag = NfcV.get(mAndroidTag);
        return tag.getMaxTransceiveLength();
    }

    @Override
    public int getMaxReceiveLengthInBytes() {
        NfcV tag = NfcV.get(mAndroidTag);
        return tag.getMaxTransceiveLength();
    }

}

