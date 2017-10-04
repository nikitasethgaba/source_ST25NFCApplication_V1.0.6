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

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;

import com.st.st25sdk.STException;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.ndef.NDEFRecord;

import java.io.IOException;
import java.util.List;

import static com.st.st25sdk.STException.STExceptionCode.INVALID_NDEF_DATA;


public class AndroidNdefInterface {

    public Tag mAndroidTag;
    byte[] mPayload;

    public static AndroidNdefInterface newInstance(RFReaderInterface readerInterface) {
        AndroidReaderInterface tagGenericTech = (AndroidReaderInterface) readerInterface;
        return new AndroidNdefInterface(tagGenericTech);
    }

    public AndroidNdefInterface(AndroidReaderInterface tag) {
        mAndroidTag = tag.getTag();

    }


    public void writeNdefMessage(String TAG, NDEFMsg msg) throws STException {

        NdefMessage ndefMsg = getNdefMessage(msg);
        byte[] rawMsg = ndefMsg.toByteArray();

        NdefFormatable ndefFormatable = NdefFormatable.get(mAndroidTag);
        Ndef ndefTag = Ndef.get(mAndroidTag);

        if (ndefFormatable != null) {
            if (!ndefFormatable.isConnected()) {
                try {
                    ndefFormatable.close();
                    ndefFormatable.connect();
                } catch (IOException e) {
                    String eMsg = e.getMessage();
                    if (eMsg == null) {
                        throw new STException("Error of connection");
                    } else
                        throw new STException(eMsg);
                }

            }

            try {
                ndefFormatable.format(ndefMsg);
            } catch (Exception e) {
                throw new STException(e);
            }
        } else if (ndefTag != null) {
            if (!ndefTag.isConnected()) {
                try {
                    ndefTag.close();
                    ndefTag.connect();
                } catch (IOException e) {
                    String eMsg = e.getMessage();
                    if (eMsg == null) {
                        throw new STException("Error of connection");
                    } else
                        throw new STException(eMsg);
                }

            }

            try {
                ndefTag.writeNdefMessage(ndefMsg);
            } catch (Exception e) {
                throw new STException(e);
            }
        } else {
            Log.e(TAG, "writeNdefMessage error: tag is read-only");
            throw new STException("No formatable tag");
        }
    }

    private NdefRecord[] getNdefRecord(List<NDEFRecord> records) throws STException {

        byte[] payload;
        NdefRecord[] ndefRecords = null;
        int size = records.size();

        try {
            if (size == 1) {
                NdefRecord ndefRecord = new NdefRecord(
                        records.get(0).getTnf(), records.get(0).getType(), new byte[0], records.get(0).getPayload());

                ndefRecords = new NdefRecord[]{ndefRecord};

            } else if (size > 1) {
                ndefRecords = new NdefRecord[size];

                for (int i = 0; i < size; i++) {
                    NdefRecord ndefRecord = new NdefRecord(
                            records.get(i).getTnf(), records.get(i).getType(), new byte[0], records.get(i).getPayload());

                    if (ndefRecord == null) {
                        throw new NullPointerException("records cannot contain null");
                    }
                    ndefRecords[i] = ndefRecord;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(INVALID_NDEF_DATA);
        }

        return ndefRecords;
    }


    private NdefMessage getNdefMessage(NDEFMsg msg) throws STException {
        NdefMessage ndefMsg = null;

        NdefRecord[] records = getNdefRecord(msg.getNDEFRecords());
        ndefMsg = new NdefMessage(records);

        return ndefMsg;
    }

    public NDEFMsg readNdefMessage(String TAG) throws STException {
        Ndef ndefTag = Ndef.get(mAndroidTag);
        NdefMessage ndefMessage = null;
        NDEFMsg returnMsg = null;

        if (ndefTag != null) {
            if (!ndefTag.isConnected()) {
                try {
                    ndefTag.close();
                    ndefTag.connect();
                } catch (IOException e) {
                    String eMsg = e.getMessage();
                    if (eMsg == null) {
                        throw new STException("Error of connection");
                    } else
                        throw new STException(eMsg);
                }

            }

            try {
                //ndefMessage = ndefTag.getCachedNdefMessage();
                if (ndefMessage == null) {
                    ndefMessage = ndefTag.getNdefMessage();
                }

            } catch (Exception e) {
                throw new STException(e);
            }
        } else {
            Log.e(TAG, "writeNdefMessage error: tag is read-only");
            throw new STException("No formatable tag");
        }

        if (ndefMessage != null) {
            byte[] rawMsg = ndefMessage.toByteArray();

            try {
                returnMsg = new NDEFMsg(rawMsg);
            }
            catch (Exception e) {
                throw new STException(e.getMessage());
            }


            /*NdefRecord[] records = ndefMessage.getRecords();
            for(NdefRecord r :records) {
                byte[] buffer = r.getPayload();

                NDEFRecord ndefRecord = new NDEFRecord(buffer);
                String msg = new String(ndefRecord.getPayload());
                returnMsg = new NDEFText(msg);
                Log.e(TAG,msg);
                Log.e(TAG,rawMsg.toString());
            }*/
        }
        return returnMsg;

    }

}
