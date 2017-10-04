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
import java.nio.charset.Charset;
import java.util.Scanner;


/**
 *  Implements NFC Forum VCard NDEF message
 *  Refers to  http://www.rfc-editor.org/info/rfc6350 vCard Format Specification
 */
public class VCardRecord extends NDEFRecord {

    private final String VCARD_DELIMITER_RETURN_STRING = "\n";

    /**
     * Private members for the NDEF VCard object
     */

    /** Specify the components of the name of the object the vCard represents, vCard name */
    private String mName;
    /** mNumber contains the phone number stored in the vCard object */
    private String mNumber;
    /** mNickName contains the nickname stored in the vCard object  */
    private String mNickName;
    /** mFormattedName contains the formatted name stored in the vCard object */
    private String mFormattedName;
    /** mEmail contains the email stored in the vCard object */
    private String mEmail;
    /** mStructPostalAddr contains the postal address stored in the vCard object */
    private String mStructPostalAddr;
    /** mWebSiteAddr contains the  web site URL stored in the vCard object */
    private String mWebSiteAddr;
    /** mPhoto contains the picture stored in the vCard object */
    private String mPhoto;


    /**
     * Constructor
     */
    public VCardRecord() {
        super();

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(NDEFRecord.RTD_VCARD_APP);

        mName = "";

        mNumber = null;
        mNickName = null;
        mFormattedName = null;
        mEmail = null;
        mStructPostalAddr = null;
        mWebSiteAddr = null;
        mPhoto = null;
    }

    public VCardRecord(ByteArrayInputStream inputStream) throws Exception {
        super(inputStream);
        byte[] payload = super.getPayload();

        if (payload == null) {
            throw new Exception("Invalid ndef data");
        }

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(NDEFRecord.RTD_VCARD_APP);

        VCardParser vCardParser = new VCardParser(payload);
        vCardParser.parse();

        if(mName == null) {
            // mName should not be null
            throw new Exception("Invalid ndef data");
        }


        if(DBG_NDEF_RECORD) {
            dbgCheckNdefRecordContent(payload);
        }
    }

    /**
     * Set the vCard as a plain text formatted contact information, typically pertaining to a single
     * contact or group of contacts.  The content consists of one or more lines in the vCard format
     * @param vcard vCard is a file format standard for electronic business cards.
     *              All vCards begin with BEGIN:VCARD and end with END:VCARD.
     *              Refers to vCard 2.1 standard for available fields.
     *              Managed fields: Name, Email, Adr, Tel, URL, Photo
     */
    public void setVcard(String vcard) {
        // Parse the VCard to decode its content.
        VCardParser  parser = new VCardParser(vcard);
        parser.parse();
    }

    /**
     * Get the phone number
     * @return Phone number field  as a string.
     * The canonical number string for a telephone number for telephony communication with the vCard object
     */
    public String getNumber ()
    {
        return mNumber;
    }

    /**
     * Get the name
     * @return Name field  as a string
     */
    public String getName()
    {
        return mName;
    }

    /**
     * Get the Formatted name
     * @return FormatedName field as a String
     */
    public String getFormattedName()
    {
        return mFormattedName;
    }

    /**
     * Get the nickname
     * @return nickname as String
     */
    public String getNickName()
    {
        return mNickName;
    }

    /**
     * Get the Email
     * @return Email Address as a string. The address for electronic mail communication with the vCard object.
     */
    public String getEmail() {return mEmail;   }

    /**
     * Get the postal Address
     * @return Postal address as a string
     */
    public String getStructPostalAddr()
    {
        return mStructPostalAddr;
    }

    /**
     * Get the WebsiteAddress
     * @return Web site address as string. A URL pointing to a website that represents the person in some way.
     */
    public String getWebSiteAddr()
    {
        return mWebSiteAddr;
    }

    /**
     * Get the VCard formatted as VERSION:2.1
     * @return Vcard String formatted as VERSION:2.1
     */
    public String getVcard()
    {
        // Convert the decoded fields into a formatted VCard.
        return export2VCard();
    }

    /**
     * Get the VCard image formatted as a Base64 encoded block of text
     * @return Picture An image or photograph of the individual associated with the vCard.
     * Embedded data in the vCard as a Base64 encoded block of text.
     */
    public String getPhoto()
    {
        return mPhoto;
    }

    /**
     * Set the Phone number as string
     * @param number The canonical number string for a telephone number for telephony communication with the vCard object
     */
    public void setNumber( String number) { mNumber= number;}

    /**
     * Set the vCard name as string
     * @param name Provides a textual representation of the SOURCE property
     */
    public void setName(String name) { mName = name;}


    /**
     * Set Formatted Name
     * @param formattedName  corresponding to the Formatted Name [FN] of the object the vCard represents
     */
    public void setFormattedName(String formattedName) { mFormattedName = formattedName;}

    /**
     * Set the NickName
     * @param nickName the text corresponding to the NICKNAME of the object the vCard represents
     */
    public void setNickName(String nickName) { mNickName = nickName;}

    /**
     * Set the Email
     * @param email The address for electronic mail communication with the vCard object.
     */
    public void setEmail(String email) {mEmail = email;}

    /**
     * Set the Postal Address of the vCard
     * @param structPostalAddr A structured representation of the physical delivery address for the vCard object.
     */
    public void setSPAddr(String structPostalAddr) {mStructPostalAddr= structPostalAddr;}

    /**
     * Set the Web site (URL)
     * @param webSiteAddr A URL pointing to a website that represents the person in some way
     */
    public void setWebSite(String webSiteAddr) {mWebSiteAddr = webSiteAddr;}

    /**
     * Set the Photo as a Base64 encoded block of text
     * @param photo An image or photograph of the individual associated with the vCard.
     *              Picture embedded in the vCard as a Base64 encoded block of text.
     */
    public void setPhoto(String photo) { mPhoto = photo;}




    /**
     * Build VCard in plain text
     */
    private String export2VCard() {

        String returnStr = VCARD_DELIMITER_RETURN_STRING;
        String vcardString;

        vcardString = "BEGIN:VCARD" + returnStr + "VERSION:2.1" + returnStr;

        if (mName != null) {
            vcardString =  vcardString + "N:;" + mName +";;;" + returnStr;
            vcardString =  vcardString + "FN:" + mName + returnStr;
        }
        else {
            STLog.e("Invalid VCard! Name should not be null!");
            return "";
        }

        if (mEmail != null) {
            vcardString =  vcardString + "EMAIL;WORK:" + mEmail +returnStr;
        }

        if (mStructPostalAddr != null) {
            vcardString =  vcardString + "ADR:" + mStructPostalAddr + ";;;;;;;" + returnStr;
        }

        if (mNumber != null) {
            vcardString = vcardString + "TEL;CELL:" + mNumber + returnStr;
        }

        if (mWebSiteAddr != null) {
            vcardString = vcardString + "URL:" + mWebSiteAddr + returnStr;
        }

        // added for tests purpose of multiple URL for VCard
        /*if ((mVCardHnd.getWebSite() != null) && (!mVCardHnd.getWebSite().isEmpty())) {
            vcardString = vcardString + "X-URL:" + mVCardHnd.getWebSite() + returnStr;
        }*/

        if (mPhoto != null) {
            vcardString = vcardString + "PHOTO;JPEG;ENCODING=BASE64:" + mPhoto + returnStr;
        }

        vcardString = vcardString + "END:VCARD";

        return vcardString;
    }

    // NB: The Payload is computed on the fly every times we need it
    @Override
    public byte[] getPayload() {
        byte[] payload = null;

        String vcard = getVcard();

        if (vcard != null) {
            payload = vcard.getBytes(Charset.forName("US-ASCII"));
        }

        return payload;
    }


    /**
     * VCardParser class
     */
    private class VCardParser {
        private final String mString;
        private Scanner mScanner;

        //constructor

        /**
         *
         * @param buffer Constructor with a VCard as byte array
         */
        public VCardParser(byte[] buffer) {
            mString = new String(buffer);
            if (!mString.isEmpty()) {
                mScanner = new Scanner(mString);
            }
        }

        /**
         *
         * @param string Constructor with a VCard as string
         */
        public VCardParser(String string) {
            mString = string;
            if (!mString.isEmpty()) {
                mScanner = new Scanner(mString);
            }
        }


        // populate VCard Handler from the
        public int parse() {
            mScanner.useDelimiter(VCARD_DELIMITER_RETURN_STRING);
            while (mScanner.hasNext()) {
                String item = mScanner.next();
                //STLog.i(item);
                String property[] = item.split(":");
                if (property.length < 2) {
                    continue;
                }
                String propertyName[] = property[0].split(";");
                String propertyValue[] = property[1].split(";");

                switch (propertyName[0]) {
                    case "BEGIN":
                        //STLog.i("BEGIN Flag detected!");
                        break;
                    case "END":
                        //STLog.i("END Flag detected!");
                        break;
                    case "VERSION":
                        //STLog.i("VERSION Flag detected!");
                        //STLog.i("Current Version is  " + propertyValue[0]);
                        break;
                    case "N":
                        //STLog.i("NAME Flag detected!");
                        //STLog.i("Current NAME is  " + Arrays.toString(propertyValue));
                        setName(appendStringFromArrayString(propertyValue));

                        break;
                    case "FN":
                        //STLog.i("FULL NAME Flag detected!");
                        //STLog.i("Current FULL NAME is" + Arrays.toString(propertyValue));
                        setFormattedName(appendStringFromArrayString(propertyValue));
                        break;
                    case "EMAIL":
                        //STLog.i("EMAIL Flag detected!");
                        //STLog.i("Current EMAIL is" + Arrays.toString(propertyValue));
                        setEmail(appendStringFromArrayString(propertyValue));
                        break;
                    case "NICKNAME":
                        //STLog.i("NICKNAME Flag detected!");
                        //STLog.i("Current EMAIL is" + Arrays.toString(propertyValue));
                        setNickName(appendStringFromArrayString(propertyValue));
                        break;
                    case "TEL":
                        //STLog.i("TEL Flag detected!");
                        //STLog.i("Current TEL is" + Arrays.toString(propertyValue));
                        setNumber(appendStringFromArrayString(propertyValue));
                        break;
                    case "ADR":
                        //STLog.i("ADR Flag detected!");
                        //STLog.i("Current ADR is" + Arrays.toString(propertyValue));
                        setSPAddr(appendStringFromArrayString(propertyValue));
                        break;
                    case "URL":
                        //STLog.i("WebSite URI Flag detected!");
                        //STLog.i("Current URI is" + Arrays.toString(propertyValue));
                        setWebSite(appendStringFromArrayString(propertyValue));
                        break;
                    case "PHOTO":
                        String imageString = propertyValue[0];
                        boolean _break = false;
                        while ((mScanner.hasNext()) && !_break) {
                            item = mScanner.next();
                            //STLog.i(item);
                            property = item.split(":");
                            if (property.length == 1) {
                                imageString = imageString + property[0];
                            } else {
                                _break = true;
                            }
                        }


                        //STLog.i("PHOTO Flag detected!");
                        //STLog.i("Current Photo is" + Arrays.toString(propertyValue));
                        setPhoto(imageString /*appendStringFromArrayString(propertyValue)*/);
                        break;
                    default:
                        //STLog.i("Token " + propertyName[0] + " not yet handled!");
                        break;
                }

            }
            return 0;
        }

        private String appendStringFromArrayString(String[] stringArray) {
            StringBuilder result = new StringBuilder();
            for (String aString : stringArray) {
                result.append(aString);
            }
            return result.toString();
        }

    }
}

