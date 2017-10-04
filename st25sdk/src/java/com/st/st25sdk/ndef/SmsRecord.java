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



import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;


public class SmsRecord extends NDEFRecord {

    public final static String SCHEME = "sms:";
    public final static String FIELDNAME = "body";

    public final static int ID = 0x00;
    private String mPhoneNumber;
    private String mMessage;


    public SmsRecord() {
        super();

        setTnf(NDEFRecord.TNF_WELLKNOWN);
        setType(NDEFRecord.RTD_URI);

        mPhoneNumber = "";
        mMessage = "";
    }

    public SmsRecord(String phoneNumber, String message)  {
        super();

        setTnf(NDEFRecord.TNF_WELLKNOWN);
        setType(NDEFRecord.RTD_URI);

        mPhoneNumber = phoneNumber;
        mMessage = message;

        setSR();
    }

    public SmsRecord(ByteArrayInputStream inputStream) throws Exception{
        super(inputStream);
        byte[] payload = super.getPayload();

        if (payload == null) {
            throw new Exception("Invalid ndef data");
        }

        setTnf(NDEFRecord.TNF_WELLKNOWN);
        setType(NDEFRecord.RTD_URI);

        //offset 1 for ID
        String uri = new String(payload, 1, payload.length -1);
        if (uri.startsWith(SCHEME)) {
            uri = uri.substring(SCHEME.length(), uri.length());
            int index = uri.indexOf( "?" + FIELDNAME + "=");
            if (index > 0)
                mPhoneNumber = uri.substring(0, index);
            else
                mPhoneNumber = "";
            index = uri.indexOf("=");
            mMessage = uri.substring(index + 1, uri.length());
        } else {
            mPhoneNumber = "";
            mMessage = "";
        }

        if(DBG_NDEF_RECORD) {
            dbgCheckNdefRecordContent(payload);
        }
    }


    public String getContact() {
        return mPhoneNumber;
    }

    public void setContact(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getMessage() { return mMessage; }

    public void setMessage(String message) {
        mMessage = message;
    }


    // NB: The Payload is computed on the fly every times we need it
    @Override
    public byte[] getPayload() {


        // Check if there is a SMS
        if (mPhoneNumber != null) {
            ByteBuffer payload;
            String fullMessage = mPhoneNumber + "?" + FIELDNAME + "=" + mMessage;
            String smsString = SCHEME;

            payload = ByteBuffer.allocate(smsString.getBytes(Charset.forName("US-ASCII")).length + fullMessage.getBytes(Charset.forName("US-ASCII")).length + 1);
            payload.put((byte) SmsRecord.ID); //prefixes with URI ID
            payload.put(smsString.getBytes(Charset.forName("US-ASCII")));
            payload.put(fullMessage.getBytes(Charset.forName("US-ASCII")));
            return payload.array();
        }

        return null;
    }
}

