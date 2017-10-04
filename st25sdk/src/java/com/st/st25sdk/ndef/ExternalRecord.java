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
import java.util.Scanner;


/**
 * Implements NFC Forum External NDEF message
 */
public class ExternalRecord extends NDEFRecord {

    private String mDomain;
    private String mExternalType;

    private byte[] mExternalData;
    private boolean mUtf8 = true;

    public static final String DEFAULT_EXTERNAL_TYPE_FORMAT = "type";
    public static final String DEFAULT_EXTERNAL_DOMAIN_FORMAT = "domain";
    public static String DEFAULT_EXTERNAL_FORMAT = DEFAULT_EXTERNAL_DOMAIN_FORMAT+ ":" + DEFAULT_EXTERNAL_TYPE_FORMAT;


    /**
     * ExternalRecord Constructors.
     */
    public ExternalRecord() {
        super();

        setTnf(NDEFRecord.TNF_EXTERNAL);
        // setType() is done by setExternalDomain() and setExternalType()

        setExternalDomain(DEFAULT_EXTERNAL_DOMAIN_FORMAT);
        setExternalType(DEFAULT_EXTERNAL_TYPE_FORMAT);
        mExternalData = null;
        mUtf8 = true;
    }

    public ExternalRecord(ByteArrayInputStream inputStream) throws Exception{
        super(inputStream);
        byte[] payload = super.getPayload();
        byte[] domainType = super.getType();

        if (payload == null) {
            throw new Exception("Invalid ndef data");
        }

        setTnf(NDEFRecord.TNF_EXTERNAL);

        mExternalData = payload;

        parse(formatToAscii(domainType));

        if(DBG_NDEF_RECORD) {
            dbgCheckNdefRecordContent(payload);
        }
    }


    /**
     * Gets External associated data.
     *
     * @return a byte array containing External data
     */
    public byte[] getContent() {
        return mExternalData;
    }

    /**
     * Sets External content data.
     *
     * @param content External data as bytes
     */
    public void setContent(byte[] content) {
        mExternalData = content;
    }


    private char getChar(byte myByte) {
        char myChar = ' ';

        if(myByte > 0x20) {
            myChar = (char) (myByte & 0xFF);
        }

        return myChar;
    }
    private String formatToAscii(byte[] content) {
        String data = "";
        char charx;
        for (byte aByte : content) {
            charx = getChar(aByte);
            data = data + String.format("%c", charx);
        }
        return data;
    }

    /**
     * Set the domain
     * @param domain domain-name of issuing organization
     */
    public void setExternalDomain(String domain) {
        this.mDomain = domain.trim().toLowerCase(Locale.ROOT);

        // mDomain as changed so the record type should be updated
        updateRecordType();
    }

    /**
     * Set the type
     * @param externalType domain-specific type of data
     */
    public void setExternalType(String externalType) {
        this.mExternalType = externalType.trim().toLowerCase(Locale.ROOT);

        // mExternalType as changed so the record type should be updated
        updateRecordType();
    }

    /**
     * Function setting the record type according to mDomain and mExternalType values
     */
    private void updateRecordType() {
        if (mDomain != null && mExternalType != null) {
            Charset utfEncoding = mUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");

            byte[] byteDomain = mDomain.getBytes(utfEncoding);
            byte[] byteType = mExternalType.getBytes(utfEncoding);
            ByteBuffer bb = ByteBuffer.allocate(byteDomain.length + 1 + byteType.length);
            bb.put(byteDomain);
            bb.put(":".getBytes(utfEncoding));
            bb.put(byteType);
            setType(bb.array());
        }
    }

    /**
     * Get the domain-name of issuing organization
     * @return domain as string
     */
    public String getExternalDomain() {
        return mDomain;
    }

    /**
     * get domain-specific type of data
     * @return type as string
     */
    public String getExternalType() {
        return mExternalType;
    }

    private void parse(String externalDomain) {
        Scanner scanner;
        if (externalDomain != null) {
            scanner = new Scanner(externalDomain);
            String EXTERNAL_DELIMITER_RETURN_STRING = "\n";
            scanner.useDelimiter(EXTERNAL_DELIMITER_RETURN_STRING);
            while (scanner.hasNext()) {
                String item = scanner.next();
                String property[] = item.split(":");
                if (property.length < 2) {
                    continue;
                }
                String domain = property[0];
                String externalType = property[1];

                setExternalDomain(domain);
                setExternalType(externalType);
            }
        }
    }

    // NB: The Payload is computed on the fly every times we need it
    @Override
    public byte[] getPayload() {
        byte[] payload;

        // Check if there is a valid External item
        if (mDomain == null || mExternalType == null || (mDomain.length() == 0) || (mExternalType.length() == 0)) {
            return null;
        }
        payload = mExternalData;

        return payload;
    }

}
