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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;


public class UriRecord extends NDEFRecord {

    // Implements NFC Forum RTD URI NDEF message

    /**
     * URI Identifier Codes to be used in place of some of the standard URI headers
     */
    public enum NdefUriIdCode {
        /** 0 0x00 N/A. No prepending is done, and the URI field contains the unabridged URI. */
        NDEF_RTD_URI_ID_NO_PREFIX,
        /** 1 0x01 http://www. */
        NDEF_RTD_URI_ID_HTTP_WWW,
        /** 2 0x02 https://www.*/
        NDEF_RTD_URI_ID_HTTPS_WWW,
        /** 3 0x03 http:// */
        NDEF_RTD_URI_ID_HTTP,
        /** 4 0x04 https:// */
        NDEF_RTD_URI_ID_HTTPS,
        /** 5 0x05 tel: */
        NDEF_RTD_URI_ID_TEL,
        /** 6 0x06 mailto: */
        NDEF_RTD_URI_ID_MAILTO,
        /** 7 0x07 ftp://anonymous:anonymous@ */
        NDEF_RTD_URI_ID_FTP_ANONYMOUS,
        /** 8 0x08 ftp://ftp. */
        NDEF_RTD_URI_ID_FTP_FTP,
        /** 9 0x09 ftps:// */
        NDEF_RTD_URI_ID_FTPS,
        /** 10 0x0A sftp:// */
        NDEF_RTD_URI_ID_SFTP,
        /** 11 0x0B smb:// */
        NDEF_RTD_URI_ID_SMB,
        /** 12 0x0C nfs:// */
        NDEF_RTD_URI_ID_NFS,
        /** 13 0x0D ftp:// */
        NDEF_RTD_URI_ID_FTP,
        /** 14 0x0E dav:// */
        NDEF_RTD_URI_ID_DAV,
        /** 15 0x0F news: */
        NDEF_RTD_URI_ID_NEWS,
        /** 16 0x10 telnet:// */
        NDEF_RTD_URI_ID_TELNET,
        /** 17 0x11 imap: */
        NDEF_RTD_URI_ID_IMAP,
        /** 18 0x12 rtsp:// */
        NDEF_RTD_URI_ID_RTSP,
        /** 19 0x13 urn: */
        NDEF_RTD_URI_ID_URN,
        /** 20 0x14 pop: */
        NDEF_RTD_URI_ID_POP,
        /** 21 0x15 sip: */
        NDEF_RTD_URI_ID_SIP,
        /** 22 0x16 sips: */
        NDEF_RTD_URI_ID_SIPS,
        /** 23 0x17 tftp: */
        NDEF_RTD_URI_ID_TFTP,
        /** 24 0x18 btspp:// */
        NDEF_RTD_URI_ID_BTSPP,
        /** 25 0x19 btl2cap:// */
        NDEF_RTD_URI_ID_BTL2CAP,
        /** 26 0x1A btgoep:// */
        NDEF_RTD_URI_ID_BTGOEP,
        /** 27 0x1B tcpobex:// */
        NDEF_RTD_URI_ID_TCP_OBEX,
        /** 28 0x1C irdaobex:// */
        NDEF_RTD_URI_ID_IRDA_OBEX,
        /** 29 0x1D file:// */
        NDEF_RTD_URI_ID_FILE,
        /** 30 0x1E urn:epc:id: */
        NDEF_RTD_URI_ID_URN_EPC_ID,
        /** 31 0x1F urn:epc:tag: */
        NDEF_RTD_URI_ID_URN_EPC_TAG,
        /** 32 0x20 urn:epc:pat: */
        NDEF_RTD_URI_ID_URN_EPC_PAT,
        /** 33 0x21 urn:epc:raw: */
        NDEF_RTD_URI_ID_URN_EPC_RAW,
        /** 34 0x22 urn:epc: */
        NDEF_RTD_URI_ID_URN_EPC,
        /** 35 0x23 urn:nfc: */
        NDEF_RTD_URI_ID_URN_NFC
    }

    private static final  LinkedHashMap<String, NdefUriIdCode> sUriCodesList =
            new LinkedHashMap<String, NdefUriIdCode>() {{
                // 0 0x00 N/A. No prepending is done, and the URI field contains the unabridged URI.
                put("", NdefUriIdCode.NDEF_RTD_URI_ID_NO_PREFIX);
                //1 0x01 http://www.
                put("http://www.", NdefUriIdCode.NDEF_RTD_URI_ID_HTTP_WWW);
                //2 0x02 https://www.
                put("https://www.", NdefUriIdCode.NDEF_RTD_URI_ID_HTTPS_WWW);
                //3 0x03 http://
                put("http://", NdefUriIdCode.NDEF_RTD_URI_ID_HTTP);
                //4 0x04 https://
                put("https://", NdefUriIdCode.NDEF_RTD_URI_ID_HTTPS);
                //5 0x05 tel:
                put("tel:", NdefUriIdCode.NDEF_RTD_URI_ID_TEL);
                //6 0x06 mailto:
                put("mailto:", NdefUriIdCode.NDEF_RTD_URI_ID_MAILTO);
                //7 0x07 ftp://anonymous:anonymous@
                put("ftp://anonymous:anonymous@", NdefUriIdCode.NDEF_RTD_URI_ID_FTP_ANONYMOUS);
                //8 0x08 ftp://ftp.
                put("ftp://ftp.", NdefUriIdCode.NDEF_RTD_URI_ID_FTP_FTP);
                //9 0x09 ftps://
                put("ftps://", NdefUriIdCode.NDEF_RTD_URI_ID_FTPS);
                //10 0x0A sftp://
                put("sftp://", NdefUriIdCode.NDEF_RTD_URI_ID_SFTP);
                //11 0x0B smb://
                put("smb://", NdefUriIdCode.NDEF_RTD_URI_ID_SMB);
                //12 0x0C nfs://
                put("nfs://", NdefUriIdCode.NDEF_RTD_URI_ID_NFS);
                //13 0x0D ftp://
                put("ftp://", NdefUriIdCode.NDEF_RTD_URI_ID_FTP);
                //14 0x0E dav://
                put("dav://", NdefUriIdCode.NDEF_RTD_URI_ID_DAV);
                //15 0x0F news:
                put("news:", NdefUriIdCode.NDEF_RTD_URI_ID_NEWS);
                //16 0x10 telnet://
                put("telnet://", NdefUriIdCode.NDEF_RTD_URI_ID_TELNET);
                //17 0x11 imap:
                put("imap:", NdefUriIdCode.NDEF_RTD_URI_ID_IMAP);
                //18 0x12 rtsp://
                put("rtsp://", NdefUriIdCode.NDEF_RTD_URI_ID_RTSP);
                //19 0x13 urn:
                put("urn:", NdefUriIdCode.NDEF_RTD_URI_ID_URN);
                //20 0x14 pop:
                put("pop:", NdefUriIdCode.NDEF_RTD_URI_ID_POP);
                //21 0x15 sip:
                put("sip:", NdefUriIdCode.NDEF_RTD_URI_ID_SIP);
                //22 0x16 sips:
                put("sips:", NdefUriIdCode.NDEF_RTD_URI_ID_SIPS);
                //23 0x17 tftp:
                put("tftp:", NdefUriIdCode.NDEF_RTD_URI_ID_TFTP);
                //24 0x18 btspp://
                put("btspp://", NdefUriIdCode.NDEF_RTD_URI_ID_BTSPP);
                //25 0x19 btl2cap://
                put("btl2cap://", NdefUriIdCode.NDEF_RTD_URI_ID_BTL2CAP);
                //26 0x1A btgoep://
                put("btgoep://", NdefUriIdCode.NDEF_RTD_URI_ID_BTGOEP);
                //27 0x1B tcpobex://
                put("tcpobex://", NdefUriIdCode.NDEF_RTD_URI_ID_TCP_OBEX);
                //28 0x1C irdaobex://
                put("irdaobex://", NdefUriIdCode.NDEF_RTD_URI_ID_IRDA_OBEX);
                //29 0x1D file://
                put("file://", NdefUriIdCode.NDEF_RTD_URI_ID_FILE);
                //30 0x1E urn:epc:id:
                put("urn:epc:id:", NdefUriIdCode.NDEF_RTD_URI_ID_URN_EPC_ID);
                //31 0x1F urn:epc:tag:
                put("urn:epc:tag:", NdefUriIdCode.NDEF_RTD_URI_ID_URN_EPC_TAG);
                //32 0x20 urn:epc:pat:
                put("urn:epc:pat:", NdefUriIdCode.NDEF_RTD_URI_ID_URN_EPC_PAT);
                //33 0x21 urn:epc:raw:
                put("urn:epc:raw:", NdefUriIdCode.NDEF_RTD_URI_ID_URN_EPC_RAW);
                //34 0x22 urn:epc:
                put("urn:epc:", NdefUriIdCode.NDEF_RTD_URI_ID_URN_EPC);
                //35 0x23 urn:nfc:
                put("urn:nfc:", NdefUriIdCode.NDEF_RTD_URI_ID_URN_NFC);
            }};

    private NdefUriIdCode mID;
    private String mUri;

    /**
     * UriRecord Constructors
     */
    public UriRecord() {
        super();

        setTnf(NDEFRecord.TNF_WELLKNOWN);
        setType(NDEFRecord.RTD_URI);

        mID = NdefUriIdCode.NDEF_RTD_URI_ID_NO_PREFIX;
        mUri = "";
    }

    /**
     * UriRecord Constructor
     * Create a new NDEF Message containing a URI with URI code and URI string info.
     * @param uriID NDEF Uri Code ID
     * @param uri URI string info
     * @see NdefUriIdCode
     */
    public UriRecord(NdefUriIdCode uriID, String uri)  {
        super();

        setTnf(NDEFRecord.TNF_WELLKNOWN);
        setType(NDEFRecord.RTD_URI);

        mID = uriID;
        mUri = uri;

        setSR();
    }

    public UriRecord(ByteArrayInputStream inputStream) throws Exception {
        super(inputStream);
        byte[] payload = super.getPayload();

        if (payload == null) {
            throw new Exception("Invalid ndef data");
        }

        setTnf(NDEFRecord.TNF_WELLKNOWN);
        setType(NDEFRecord.RTD_URI);

        int position = 0xFF & payload[0];

        if (position < NdefUriIdCode.values().length) {
            mID = NdefUriIdCode.values()[position];
        } else {
            mID = NdefUriIdCode.NDEF_RTD_URI_ID_NO_PREFIX;
        }

        String uri = new String(payload);
        //The first byte of payload is the prefix ID set above
        mUri = uri.substring(1);

        if(DBG_NDEF_RECORD) {
            dbgCheckNdefRecordContent(payload);
        }
    }


    /**
     * Gets the URI Code ID.
     * @return NDEF URI code as defined in NdefUriIdCode
     * @see NdefUriIdCode
     */
    public NdefUriIdCode getUriID() {
        return mID;
    }

    /**
     * Sets a URI Code ID for with the UriRecord object
     * @param uriID URI Code ID
     * @see NdefUriIdCode
     */
    public void setUriID(NdefUriIdCode uriID) {
        mID = uriID;
    }

    /**
     * Gets URI String info.
     * The returned string does not contain the URI code ID.
     * @return a string containing the URI information
     */
    public String getContent() {
        return mUri;
    }

    /**
     * Sets URI String info.
     * @param uri String with URI information
     */
    public void setContent(String uri) {
        mUri = uri;
    }

    /**
     * Returns array of URI code list
     * @return String Array of URI code list
     */
    public static ArrayList<String> getUriCodesList() {
        return new ArrayList<>(Collections.synchronizedSet(sUriCodesList.keySet()));
    }

    /**
     * Gets URI Code ID specified by its string
     * @param codeStr URI Code ID defined as String
     * @return URI Identifier Codes
     * @see NdefUriIdCode
     */
    public static NdefUriIdCode getUriCodeFromStr(String codeStr) {
        return sUriCodesList.get(codeStr);
    }

    /**
     * Gets URI Code ID Index
     * @param uriCode URI Identifier Codes
     * @return URI Code ID Index
     * @see NdefUriIdCode
     */
    public static int getUriCodePositionInList(NdefUriIdCode uriCode) {
        return uriCode.ordinal();
    }


    // NB: The Payload is computed on the fly every times we need it
    @Override
    public byte[] getPayload() {
        ByteBuffer payload;

        if (mUri != null) {
            // As per RTD Text spec, RTD content is:
            // 1 byte: URI identifier code (see above sUriCodesList)
            // n bytes: URI in UTF-8

            // Implementation of the "TNF_WELL_KNOWN with RTD_URI" use case ("Creating the NdefRecord manually"),
            // as advised in "NFC Basics" developers guide, on http://developer.android.com
            // (https://developer.android.com/guide/topics/connectivity/nfc/nfc.html)
            // Prepare language and its encoding, as per the input parameters
            byte[] uriField = mUri.getBytes(Charset.forName("US-ASCII"));
            byte[] id = new byte[]{(byte) mID.ordinal()};

            payload = ByteBuffer.allocate(uriField.length + 1);

            payload.put(id[0]);
            payload.put(uriField);
            return payload.array();
        }

        return null;
    }

}
