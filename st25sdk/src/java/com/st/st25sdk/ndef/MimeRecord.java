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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;


/**
 * Implements NFC Forum MIME NDEF message
 */
public class MimeRecord extends NDEFRecord {

    private NdefMimeIdCode mMimeID;
    private String mMime;

    private byte[] mMimeData;
    private boolean mUtf8 = true;

    public static final String DEFAULT_MIME_TYPE_FORMAT = "parameter/value";


    public enum NdefMimeIdCode {
        /** */NDEF_MIME_NONE,
        /**
         * Extension : .aac  document type: AAC fichier audio
         */NDEF_MIME_AUDIO_AAC,
        /**
         * Extension : .abw  document type: document AbiWord
         */NDEF_MIME_APPLICATION_X_ABIWORD,
        /**
         * Extension : .avi  document type: AVI: Audio Video Interleave
         */NDEF_MIME_VIDEO_X_MSVIDEO,
        /**
         * Extension : .azw  document type: Amazon Kindle eBook format
         */NDEF_MIME_APPLICATION_VND_AMAZON_EBOOK,
        /**
         * Extension : .bin  document type: Tous les fichiers avec données binaires
         */NDEF_MIME_APPLICATION_OCTET_STREAM,
        /**
         * Extension : .bz  document type: Archive BZip
         */NDEF_MIME_APPLICATION_X_BZIP,
        /**
         * Extension : .bz2  document type: Archive BZip2
         */NDEF_MIME_APPLICATION_X_BZIP2,
        /**
         * Extension : .csh  document type: Script C-Shell
         */NDEF_MIME_APPLICATION_X_CSH,
        /**
         * Extension : .css  document type: Cascading Style Sheets (CSS)
         */NDEF_MIME_TEXT_CSS,
        /**
         * Extension : .csv  document type: Comma-separated values (CSV)
         */NDEF_MIME_TEXT_CSV,
        /**
         * Extension : .doc  document type: Microsoft Word
         */NDEF_MIME_APPLICATION_MSWORD,
        /**
         * Extension : .epub  document type: Electronic publication (EPUB)
         */NDEF_MIME_APPLICATION_EPUB_ZIP,
        /**
         * Extension : .gif  document type: Graphics Interchange Format (GIF) (image animée)
         */NDEF_MIME_IMAGE_GIF,
        /**
         * Extension : .htm  document type: HyperText Markup Language (HTML)
         */NDEF_MIME_TEXT_HTML,
        /**
         * Extension : .ico  document type: Format icone
         */NDEF_MIME_IMAGE_X_ICON,
        /**
         * Extension : .ics  document type: Format iCalendar
         */NDEF_MIME_TEXT_CALENDAR,
        /**
         * Extension : .jar  document type: Archive Java (JAR)
         */NDEF_MIME_APPLICATION_JAVA_ARCHIVE,
        /**
         * Extension : .jpeg  document type: Image JPEG
         */NDEF_MIME_IMAGE_JPEG,
        /**
         * Extension : .js  document type: JavaScript (ECMAScript)
         */NDEF_MIME_APPLICATION_JS,
        /**
         * Extension : .json  document type: Format JSON
         */NDEF_MIME_APPLICATION_JSON,
        /**
         * Extension : .mid  document type: Musical Instrument Digital Interface (MIDI)
         */NDEF_MIME_AUDIO_MIDI,
        /**
         * Extension : .mpeg  document type: Vidéo MPEG
         */NDEF_MIME_VIDEO_MPEG,
        /**
         * Extension : .mpkg  document type: Installeur de packet Apple
         */NDEF_MIME_APPLICATION_VND_APPLE_INSTALLER_XML,
        /**
         * Extension : .odp  document type: Document de présentation OpenDocument
         */NDEF_MIME_APPLICATION_VND_OASIS_OPENDOCUMENT_PRESENTATION,
        /**
         * Extension : .ods  document type: OpenDocuemnt spreadsheet document
         */NDEF_MIME_APPLICATION_VND_OASIS_OPENDOCUMENT_SPREADSHEET,
        /**
         * Extension : .odt  document type: Document texte OpenDocument
         */NDEF_MIME_APPLICATION_VND_OASIS_OPENDOCUMENT_TEXT,
        /**
         * Extension : .oga  document type: Son OGG
         */NDEF_MIME_AUDIO_OGG,
        /**
         * Extension : .ogv  document type: Vidéo OGG
         */NDEF_MIME_VIDEO_OGG,
        /**
         * Extension : .ogx  document type: OGG
         */NDEF_MIME_APPLICATION_OGG,
        /**
         * Extension : .pdf  document type: Adobe Portable Document Format (PDF)
         */NDEF_MIME_APPLICATION_PDF,
        /**
         * Extension : .ppt  document type: Microsoft PowerPoint
         */NDEF_MIME_APPLICATION_VND_MS_POWERPOINT,
        /**
         * Extension : .rar  document type: Archive RAR
         */NDEF_MIME_APPLICATION_X_RAR_COMPRESSED,
        /**
         * Extension : .rtf  document type: Rich Text Format (RTF)
         */NDEF_MIME_APPLICATION_RTF,
        /**
         * Extension : .sh  document type: Bourne shell script
         */NDEF_MIME_APPLICATION_X_SH,
        /**
         * Extension : .svg  document type: Scalable Vector Graphics (SVG)
         */NDEF_MIME_IMAGE_SVG_XML,
        /**
         * Extension : .swf  document type: Small web format (SWF) or Adobe Flash document
         */NDEF_MIME_APPLICATION_X_SHOCKWAVE_FLASH,
        /**
         * Extension : .tar  document type: Archive Tape (TAR)
         */NDEF_MIME_APPLICATION_X_TAR,
        /**
         * Extension : .tif  document type: Tagged Image File Format (TIFF)
         */NDEF_MIME_IMAGE_TIFF,
        /**
         * Extension : .ttf  document type: Police TrueType
         */NDEF_MIME_APPLICATION_X_FONT_TTF,
        /**
         * Extension : .vsd  document type: Microsft Visio
         */NDEF_MIME_APPLICATION_VND_VISIO,
        /**
         * Extension : .wav  document type: Son Waveform
         */NDEF_MIME_AUDIO_X_WAV,
        /**
         * Extension : .weba  document type: Son WEBM
         */NDEF_MIME_AUDIO_WEBM,
        /**
         * Extension : .webm  document type: Vidéo WEBM
         */NDEF_MIME_VIDEO_WEBM,
        /**
         * Extension : .webp  document type: Image WEBP
         */NDEF_MIME_IMAGE_WEBP,
        /**
         * Extension : .woff  document type: Web Open Font Format (WOFF)
         */NDEF_MIME_APPLICATION_X_FONT_WOFF,
        /**
         * Extension : .xhtml  document type: XHTML
         */NDEF_MIME_APPLICATION_XHTML_XML,
        /**
         * Extension : .xls  document type: Microsoft Excel
         */NDEF_MIME_APPLICATION_VND_MS_EXCEL,
        /**
         * Extension : .xml  document type: XML
         */NDEF_MIME_APPLICATION_XML,
        /**
         * Extension : .xul  document type: XUL
         */NDEF_MIME_APPLICATION_VND_MOZILLA_XUL_XML,
        /**
         * Extension : .zip  document type: Archive ZIP
         */NDEF_MIME_APPLICATION_ZIP,
        /**
         * Extension : .3gp  document type: Son et vidéo 3GPP
         */NDEF_MIME_VIDEO_3GPP,
        /**
         * Extension :   document type:
         */NDEF_MIME_AUDIO_3GPP,
        /**
         * Extension : .3g2  document type: Son et vidéo 3GPP2
         */NDEF_MIME_VIDEO_3GPP2,
        /**
         * Extension :   document type:
         */NDEF_MIME_AUDIO_3GPP2,
        /**
         * Extension : .7z  document type: Archive 7-zip
         */NDEF_MIME_APPLICATION_X_7Z_COMPRESSED,
    }

    private static final  LinkedHashMap<String, NdefMimeIdCode> sMimeCodesList =
            new LinkedHashMap<String, NdefMimeIdCode>() {{
                put(DEFAULT_MIME_TYPE_FORMAT, NdefMimeIdCode.NDEF_MIME_NONE);
                put("audio/aac", NdefMimeIdCode.NDEF_MIME_AUDIO_AAC);
                put("application/x-abiword", NdefMimeIdCode.NDEF_MIME_APPLICATION_X_ABIWORD);
                put("video/x-msvideo", NdefMimeIdCode.NDEF_MIME_VIDEO_X_MSVIDEO);
                put("application/vnd.amazon.ebook", NdefMimeIdCode.NDEF_MIME_APPLICATION_VND_AMAZON_EBOOK);
                put("application/octet-stream", NdefMimeIdCode.NDEF_MIME_APPLICATION_OCTET_STREAM);
                put("application/x-bzip", NdefMimeIdCode.NDEF_MIME_APPLICATION_X_BZIP);
                put("application/x-bzip2", NdefMimeIdCode.NDEF_MIME_APPLICATION_X_BZIP2);
                put("application/x-csh", NdefMimeIdCode.NDEF_MIME_APPLICATION_X_CSH);
                put("text/css", NdefMimeIdCode.NDEF_MIME_TEXT_CSS);
                put("text/csv", NdefMimeIdCode.NDEF_MIME_TEXT_CSV);
                put("application/msword", NdefMimeIdCode.NDEF_MIME_APPLICATION_MSWORD);
                put("application/epub+zip", NdefMimeIdCode.NDEF_MIME_APPLICATION_EPUB_ZIP);
                put("image/gif", NdefMimeIdCode.NDEF_MIME_IMAGE_GIF);
                put("text/html", NdefMimeIdCode.NDEF_MIME_TEXT_HTML);
                put("image/x-icon", NdefMimeIdCode.NDEF_MIME_IMAGE_X_ICON);
                put("text/calendar", NdefMimeIdCode.NDEF_MIME_TEXT_CALENDAR);
                put("application/java-archive", NdefMimeIdCode.NDEF_MIME_APPLICATION_JAVA_ARCHIVE);
                put("image/jpeg", NdefMimeIdCode.NDEF_MIME_IMAGE_JPEG);
                put("application/js", NdefMimeIdCode.NDEF_MIME_APPLICATION_JS);
                put("application/json", NdefMimeIdCode.NDEF_MIME_APPLICATION_JSON);
                put("audio/midi", NdefMimeIdCode.NDEF_MIME_AUDIO_MIDI);
                put("video/mpeg", NdefMimeIdCode.NDEF_MIME_VIDEO_MPEG);
                put("application/vnd.apple.installer+xml", NdefMimeIdCode.NDEF_MIME_APPLICATION_VND_APPLE_INSTALLER_XML);
                put("application/vnd.oasis.opendocument.presentation", NdefMimeIdCode.NDEF_MIME_APPLICATION_VND_OASIS_OPENDOCUMENT_PRESENTATION);
                put("application/vnd.oasis.opendocument.spreadsheet", NdefMimeIdCode.NDEF_MIME_APPLICATION_VND_OASIS_OPENDOCUMENT_SPREADSHEET);
                put("application/vnd.oasis.opendocument.text", NdefMimeIdCode.NDEF_MIME_APPLICATION_VND_OASIS_OPENDOCUMENT_TEXT);
                put("audio/ogg", NdefMimeIdCode.NDEF_MIME_AUDIO_OGG);
                put("video/ogg", NdefMimeIdCode.NDEF_MIME_VIDEO_OGG);
                put("application/ogg", NdefMimeIdCode.NDEF_MIME_APPLICATION_OGG);
                put("application/pdf", NdefMimeIdCode.NDEF_MIME_APPLICATION_PDF);
                put("application/vnd.ms-powerpoint", NdefMimeIdCode.NDEF_MIME_APPLICATION_VND_MS_POWERPOINT);
                put("application/x-rar-compressed", NdefMimeIdCode.NDEF_MIME_APPLICATION_X_RAR_COMPRESSED);
                put("application/rtf", NdefMimeIdCode.NDEF_MIME_APPLICATION_RTF);
                put("application/x-sh", NdefMimeIdCode.NDEF_MIME_APPLICATION_X_SH);
                put("image/svg+xml", NdefMimeIdCode.NDEF_MIME_IMAGE_SVG_XML);
                put("application/x-shockwave-flash", NdefMimeIdCode.NDEF_MIME_APPLICATION_X_SHOCKWAVE_FLASH);
                put("application/x-tar", NdefMimeIdCode.NDEF_MIME_APPLICATION_X_TAR);
                put("image/tiff", NdefMimeIdCode.NDEF_MIME_IMAGE_TIFF);
                put("application/x-font-ttf", NdefMimeIdCode.NDEF_MIME_APPLICATION_X_FONT_TTF);
                put("application/vnd.visio", NdefMimeIdCode.NDEF_MIME_APPLICATION_VND_VISIO);
                put("audio/x-wav", NdefMimeIdCode.NDEF_MIME_AUDIO_X_WAV);
                put("audio/webm", NdefMimeIdCode.NDEF_MIME_AUDIO_WEBM);
                put("video/webm", NdefMimeIdCode.NDEF_MIME_VIDEO_WEBM);
                put("image/webp", NdefMimeIdCode.NDEF_MIME_IMAGE_WEBP);
                put("application/x-font-woff", NdefMimeIdCode.NDEF_MIME_APPLICATION_X_FONT_WOFF);
                put("application/xhtml+xml", NdefMimeIdCode.NDEF_MIME_APPLICATION_XHTML_XML);
                put("application/vnd.ms-excel", NdefMimeIdCode.NDEF_MIME_APPLICATION_VND_MS_EXCEL);
                put("application/xml", NdefMimeIdCode.NDEF_MIME_APPLICATION_XML);
                put("application/vnd.mozilla.xul+xml", NdefMimeIdCode.NDEF_MIME_APPLICATION_VND_MOZILLA_XUL_XML);
                put("application/zip", NdefMimeIdCode.NDEF_MIME_APPLICATION_ZIP);
                put("video/3gpp", NdefMimeIdCode.NDEF_MIME_VIDEO_3GPP);
                put("audio/3gpp", NdefMimeIdCode.NDEF_MIME_AUDIO_3GPP);
                put("video/3gpp2", NdefMimeIdCode.NDEF_MIME_VIDEO_3GPP2);
                put("audio/3gpp2", NdefMimeIdCode.NDEF_MIME_AUDIO_3GPP2);
                put("application/x-7z-compressed", NdefMimeIdCode.NDEF_MIME_APPLICATION_X_7Z_COMPRESSED);

            }};

    /**
     * MimeRecord Constructors.
     */
    public MimeRecord() {
        super();

        mMimeID = NdefMimeIdCode.NDEF_MIME_NONE;
        mMime = getMimeString(mMimeID);
        mMimeData = null;
        mUtf8 = true;

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(mMime.getBytes());
    }

    /**
     * @param mimeID   Mime Identifier Codes
     * @param mimeData MIME data as bytes
     */
    public MimeRecord(NdefMimeIdCode mimeID, byte[] mimeData) {
        super();

        mMimeID = mimeID;
        mMime = getMimeString(mMimeID);
        mMimeData = mimeData;

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(mMime.getBytes());

        setSR();
    }

    public MimeRecord(ByteArrayInputStream inputStream) throws Exception{
        super(inputStream);
        byte[] payload = super.getPayload();
        byte[] mime = super.getType();

        if (payload == null) {
            throw new Exception("Invalid ndef data");
        }

        parse(mime, payload);

        setTnf(NDEFRecord.TNF_MEDIA);
        setType(mMime.getBytes());

        if(DBG_NDEF_RECORD) {
            dbgCheckNdefRecordContent(payload);
        }
    }


    /**
     * Returns array of Mime code list
     *
     * @return String Array of Mime code list
     */
    public static ArrayList<String> getMimeCodesList() {
        return new ArrayList<>(Collections.synchronizedSet(sMimeCodesList.keySet()));
    }

    /**
     * Gets Mime Code ID specified by its string
     *
     * @param codeStr Mime Code ID defined as String
     * @return Mime Identifier Codes
     * @see NdefMimeIdCode
     */
    public static NdefMimeIdCode getMimeCodeFromStr(String codeStr) {
        return sMimeCodesList.get(codeStr);
    }

    /**
     * Gets Mime Code ID Index
     *
     * @param mimeCode URI Identifier Codes
     * @return Mime Code ID Index
     * @see NdefMimeIdCode
     */
    public static int getMimeCodePositionInList(NdefMimeIdCode mimeCode) {
        return mimeCode.ordinal();
    }

    /**
     * Gets the Mime Code ID.
     *
     * @return NDEF Mime code as defined in NdefMimeIdCode
     * @see NdefMimeIdCode
     */
    public NdefMimeIdCode getMimeID() {
        return mMimeID;
    }


    /**
     * Sets a Mime Code ID for with the MimeRecord object
     *
     * @param mimeID Mime Code ID
     * @see NdefMimeIdCode
     */
    public void setMimeID(NdefMimeIdCode mimeID) {

        mMimeID = mimeID;
        mMime = getMimeString(mMimeID);
    }

    /**
     * Gets Mime associated data info.
     *
     * @return a byte array containing MIME data
     */
    public byte[] getContent() {
        return mMimeData;
    }

    /**
     * Sets Mime content info.
     *
     * @param content MIME data as bytes
     */
    public void setContent(byte[] content) {
        mMimeData = content;
    }


    private void parse(byte[] mime, byte[] buffer) {
        String payload = new String(mime);
        mMime = DEFAULT_MIME_TYPE_FORMAT;
        mMimeID = sMimeCodesList.get(mMime);
        for (String k : sMimeCodesList.keySet()) {
            if (payload.contentEquals(k)) {
                // found the Mime type
                mMime = k;
                mMimeID = sMimeCodesList.get(k);
                break;
            }
        }
        //byte[] mimeType = mMime.getBytes(Charset.forName("US-ASCII"));
        int slashIndex = mMime.indexOf('/');
        if (slashIndex == 0) throw new IllegalArgumentException("mimeType must have major type");
        if (slashIndex == mMime.length() - 1) {
            throw new IllegalArgumentException("mimeType must have minor type");
        }

        setHexa(buffer);
    }

    private void setHexa(byte[] hexa) {
        mMimeData = hexa;
    }

    private String getMimeString(NdefMimeIdCode idCode) {
        String mime = "";
        for (String k : sMimeCodesList.keySet()) {
            // found the Mime type
            if (idCode == sMimeCodesList.get(k)) {
                mime = k;
                break;
            }
        }
        return mime;
    }

    // NB: The Payload is computed on the fly every times we need it
    @Override
    public byte[] getPayload() {
        return mMimeData;
    }

}
