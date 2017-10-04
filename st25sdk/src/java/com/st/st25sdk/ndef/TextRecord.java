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
 *  Implements NFC Forum Text NDEF message
 *  Refers to  NFCForum-TS-RTD_Text_1.0 http://members.nfc-forum.org/apps/group_public/document.php?document_id=5080
 */
public class TextRecord extends NDEFRecord {

    private String mText;
    private Locale mLocale;
    private boolean mUtf8 = true;

    /**
     * TextRecord Constructors.
     * Reference specification: NFCForum-TS-RTD_Text_1.0
     */
    public TextRecord() {
        super();

        setTnf(NDEFRecord.TNF_WELLKNOWN);
        setType(NDEFRecord.RTD_TEXT);

        mText = "";
        mLocale = Locale.getDefault();
        mUtf8 = true;
    }

    /**
     * TextRecord Constructor with Text info
     *  Reference specification: NFCForum-TS-RTD_Text_1.0
     *  By default, the text is encoded with default Language code English,
     *  and in UTF8 encoding type.
     * @param text Text to write in NDEF
     */
    public TextRecord(String text) {
        super();

        setTnf(NDEFRecord.TNF_WELLKNOWN);
        setType(NDEFRecord.RTD_TEXT);

        mText = text;
        mLocale = Locale.getDefault();
        mUtf8 = true;
    }

    /**
     * TextRecord Constructor with Text to encode, language code and encoding type
     * Reference specification: NFCForum-TS-RTD_Text_1.0
     * @param text      The text to encode.
     * @param locale    The language code
     * @param utf8      The Encoding Type
     */
    public TextRecord(String text, Locale locale, boolean utf8) {
        super();

        setTnf(NDEFRecord.TNF_WELLKNOWN);
        setType(NDEFRecord.RTD_TEXT);

        mText = text;
        mLocale = locale;
        mUtf8 = utf8;

        setSR();
    }

    public TextRecord(ByteArrayInputStream inputStream) throws Exception {
        super(inputStream);
        byte[] payload = super.getPayload();

        if (payload == null) {
            throw new Exception("Invalid ndef data");
        }

        setTnf(NDEFRecord.TNF_WELLKNOWN);
        setType(NDEFRecord.RTD_TEXT);

        //status as defined below
        //just jump the length so far...
        int lengthOfLanguageCode = 0x1F & payload[0];
        String text = new String(payload);

        // Skip the status byte and the Language code
        if ((lengthOfLanguageCode + 1) < text.length()) {
            mText = text.substring(lengthOfLanguageCode + 1, text.length());
        }
        else mText = "";

        mLocale = Locale.getDefault();
        mUtf8 = true;

        if(DBG_NDEF_RECORD) {
            dbgCheckNdefRecordContent(payload);
        }
    }


    /**
     * Set a NDEF Text record containing UTF-8 text data.
     *  The language code and encoding type must be specified in constructor
     * @param text The text to be encoded in the record. Will be represented in UTF-8 format.
     */
    public void setText(String text) {
        mText = text;
    }

    /**
     * Get the Text inside this TextRecord.
     * @return String containing text from TextRecord Message.
     */
    public String getText() {
        return mText;
    }

    // NB: The Payload is computed on the fly every times we need it
    @Override
    public byte[] getPayload() {
        ByteBuffer payload;

        if (mText != null) {

            // As per RTD Text spec, RTD content is:
            // 1 byte: status byte:
            //        |        7         |  6  |  5   4   3   2   1   0 |
            //        | 0: Text in UTF-8 | RFU | IANA lang code length  |
            //        | 1: Text in UTF16 | (0) |                        |
            // n bytes: ISO/IANA language code.
            //          Examples: “fi?, “en-US?, “fr-CA?, “jp?. Encoding is US-ASCII.
            // m bytes: The actual text. Encoding is the one of status byte

            // Implementation of the "TNF_WELL_KNOWN with RTD_TEXT" use case,
            // as advised in "NFC Basics" developers guide, on http://developer.android.com
            // (https://developer.android.com/guide/topics/connectivity/nfc/nfc.html)
            // Prepare language and its encoding, as per the input parameters
            byte[] langBytes = mLocale.getLanguage().getBytes(Charset.forName("US-ASCII"));
            Charset utfEncoding = mUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");

            // Turn the text as per the encoding
            // TODO: RTD_Text spec says:
            // Control characters (0x00-0x1F in UTF-8) should be removed prior to display, except for newline,
            // line feed (0x0D, 0x0A) and tab (0x08) characters. Markup MUST NOT be embedded (please use
            // the “text/xhtml? or other suitable MIME types). The Text record should be considered to be equal
            // to the MIME type “text/plain; format=fixed?.
            // Line breaks in the text MUST be represented using the CRLF (so-called DOS convention, the
            // sequence 0x0D,0x0A in UTF-8). The device may deal with the tab character as it wishes.
            // White space other than newline and tab SHOULD be collapsed, i.e., multiple space characters are
            // to be considered a single space character.
            // To be applied !!!
            byte[] textBytes = mText.getBytes(utfEncoding);

            // Build the RTD Text buffer
            payload = ByteBuffer.allocate(1 + langBytes.length + textBytes.length);

            // - Status byte
            int utfBit = mUtf8 ? 0 : (1 << 7);
            char status = (char) (utfBit + langBytes.length);
            payload.put((byte) status);
            // - ISO/IANA language code
            payload.put(langBytes);
            // - payload
            payload.put(textBytes);
            return payload.array();
        }

        return null;
    }

}
