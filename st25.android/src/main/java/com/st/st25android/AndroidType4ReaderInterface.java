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
import android.util.Log;

import com.st.st25sdk.Helper;
import com.st.st25sdk.STException;
import com.st.st25sdk.RFReaderInterface;

import java.io.IOException;
import java.util.Arrays;

import static com.st.st25sdk.STException.STExceptionCode.TRANSCEIVE_EVAL_MODE;

public class AndroidType4ReaderInterface extends AndroidReaderInterface implements RFReaderInterface {

    static final boolean DBG = true;
    private IsoDep mIsoDep;


    public AndroidType4ReaderInterface(Tag tag) {
        super(tag);
        mIsoDep = IsoDep.get(tag);
    }

    public byte[] transceive(Object TAG, String commandName, byte[] data) throws STException {
        byte[] buffer;

        if (DBG) {
            if (isUIThread()) {
                throw new STException(STException.STExceptionCode.TAG_CMD_CALLED_FROM_UI_THREAD);
            }
        }

        if (mTransceiveMode == TransceiveMode.EVAL) {
            String frame = String.format("==> EVAL " + commandName + " command: %s", Helper.convertHexByteArrayToString(data));
            Log.d((String) TAG, frame);

            byte[] dataArray = Arrays.copyOf(data, data.length);
            throw new STException(TRANSCEIVE_EVAL_MODE, dataArray);
        }

        if (!mIsoDep.isConnected()) {
            try {
                mIsoDep.close();
                mIsoDep.connect();
            } catch (IOException e) {
                // Connection error. It is likely that the tag is no more in the field
                throw new STException(STException.STExceptionCode.TAG_NOT_IN_THE_FIELD);
            }
        }

        try {
            if (DBG) {
                String frame = String.format("==> Send " + commandName + " command: %s", Helper.convertHexByteArrayToString(data));
                Log.d((String) TAG, frame);
            }


            buffer = mIsoDep.transceive(data);

            if (DBG) {
                String frame = String.format("Response: %s", Helper.convertHexByteArrayToString(buffer));
                Log.d((String) TAG, frame);
            }

            return buffer;

        } catch (Exception e) {
            // transceive command failed
            throw new STException(e);
        }
    }

    @Override
    public int getMaxTransmitLengthInBytes() {
        return mIsoDep.getMaxTransceiveLength();
    }

    @Override
    public int getMaxReceiveLengthInBytes() {
        return mIsoDep.getMaxTransceiveLength();
    }

}
