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
import java.util.Locale;


/**
 * EmailRecord is the class in charge of reading/writing Email NDEF records.
 * As defined in NFC Forum, NDEF Email record are defined as an URI NDEF record with URI prefix = 0x06
 *
 * @author STMicroelectronics, (c) September 2016
 *
 */
public class EmailRecord extends NDEFRecord {

    public final static int ID = 0x06;
    private String mContact;
    private String mSubject;
    private String mMessage;

    private Locale mLocale;
    private boolean mUtf8Enc = true;

    /**
     * EmailRecord Constructors
     */
    // Constructors
    public EmailRecord() {
        super();

        setTnf(NDEFRecord.TNF_WELLKNOWN);
        setType(NDEFRecord.RTD_URI);

        mContact = "";
        mSubject = "";
        mMessage = "";
        mLocale  = Locale.getDefault();
        mUtf8Enc = true;
    }

    /**
     * EmailRecord Constructor
     * Create a new NDEF Message containing an Email record
     * @param contact The "To" field of the email.
     * @param subject The Subject field of the email.
     * @param message The Body field of the email.
     */
    public EmailRecord(String contact, String subject, String message)  {
        super();

        setTnf(NDEFRecord.TNF_WELLKNOWN);
        setType(NDEFRecord.RTD_URI);

        mContact = contact;
        mSubject = subject;
        mMessage = message;
        mLocale = Locale.getDefault();
        mUtf8Enc = true;

        setSR();
    }

    public EmailRecord(ByteArrayInputStream inputStream) throws Exception{
        super(inputStream);
        byte[] payload = super.getPayload();

        if (payload == null) {
            throw new Exception("Invalid ndef data");
        }

        setTnf(NDEFRecord.TNF_WELLKNOWN);
        setType(NDEFRecord.RTD_URI);

        String tmpmessage;
        String message;
        // Patch for Compatibility of encoding/decoding
        mUtf8Enc = (payload[0] >> 7 == 0);

        if (mUtf8Enc) {
            message = new String(payload, Charset.forName("UTF-8"));
        } else {
            message = new String(payload, Charset.forName("UTF-16"));
        }


        String [] contactScissor=message.split("\\?",2);

        if (!contactScissor[0].isEmpty()) // else stay with an empty message
        {
            mContact = contactScissor[0].substring(1); // remove first char
            if ((contactScissor.length>1) && (!contactScissor[1].isEmpty())) // with have subject &/| message
            {
                if (contactScissor[1].matches("(subject=).*") || contactScissor[1].matches("subject=*"))
                {
                    // Check contactScissor length before accessing to contactScissor[1]
                    if (contactScissor.length < 2) {
                        throw new Exception("Invalid ndef data");
                    }
                    String [] subjectScissor = contactScissor[1].split("subject="); // remove split string

                    // Check subjectScissor length before accessing to subjectScissor[1]
                    if (subjectScissor.length < 2) {
                        throw new Exception("Invalid ndef data");
                    }
                    String [] messageScissor = subjectScissor[1].split("\\&body=",2);

                    if (!messageScissor[0].isEmpty()) {
                        mSubject = messageScissor[0];

                        // Check messageScissor length before accessing to messageScissor[1]
                        if (messageScissor.length < 2) {
                            throw new Exception("Invalid ndef data");
                        }

                        mMessage = ((messageScissor.length>1) && (!messageScissor[1].isEmpty()))?messageScissor[1]:"";
                    }
                    else {
                        mMessage = subjectScissor[0];
                    }
                }
                else
                {
                    tmpmessage = contactScissor[1];
                    String [] subjectScissor=tmpmessage.split("&body=",2);
                    if ((subjectScissor.length>1) && (!subjectScissor[1].isEmpty())) {
                        mMessage = subjectScissor[1];
                        tmpmessage = subjectScissor[0];
                        subjectScissor=tmpmessage.split("subject=",2);
                        if ((subjectScissor.length>1) && (!subjectScissor[1].isEmpty())) {
                            mSubject = subjectScissor[1];
                        }
                        else
                            mSubject = subjectScissor[0];
                    }
                    else
                        mMessage = subjectScissor[0];
                }
            }
        }

        if(DBG_NDEF_RECORD) {
            dbgCheckNdefRecordContent(payload);
        }

    }

    /**
     * Gets the "To" Field of the email NDEF record
     * @return String with "To" field info.
     */
    public String getContact() { return mContact; }

    /**
     * Sets the "To" Field of the email NDEF record
     * @param contact The "To" field of the email.
     */
    public void setContact(String contact) {
        mContact = contact;
    }

    /**
     * Gets the subject Field of the email NDEF record
     * @return String with subject info.
     */
    public String getSubject() {
        return mSubject;
    }

    /**
     * Sets the subject Field of the email NDEF record
     * @param subject The Subject field of the email.
     */
    public void setSubject(String subject) {
        mSubject = subject;
    }

    /**
     * Gets the Body field of the email NDEF record
     * @return String with email body info.
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Sets the Body field of the email NDEF record
     * @param message The Body field of the email.
     */
    public void setMessage(String message) {
        mMessage = message;
    }


    // NB: The Payload is computed on the fly every times we need it
    @Override
    public byte[] getPayload() {


        // Check if there is a valid Contact
        if(mContact != null) {
            ByteBuffer payload;
            String fullMessage = mContact + "?subject=" + mSubject + "&body=" + mMessage;

            payload = ByteBuffer.allocate(1 + fullMessage.getBytes(Charset.forName("US-ASCII")).length); //add 1 for the URI Prefix
            payload.put((byte) ID); //prefixes with URI ID
            payload.put(fullMessage.getBytes(Charset.forName("US-ASCII")));
            return payload.array();
        }

        return null;
    }


}
