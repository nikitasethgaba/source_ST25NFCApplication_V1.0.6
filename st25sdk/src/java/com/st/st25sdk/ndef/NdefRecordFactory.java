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
import java.util.Arrays;

import static com.st.st25sdk.ndef.NDEFRecord.RTD_ANDROID_APP;
import static com.st.st25sdk.ndef.NDEFRecord.RTD_BTLE_APP;
import static com.st.st25sdk.ndef.NDEFRecord.RTD_BT_APP;
import static com.st.st25sdk.ndef.NDEFRecord.RTD_SMS;
import static com.st.st25sdk.ndef.NDEFRecord.RTD_VCARD_APP;
import static com.st.st25sdk.ndef.NDEFRecord.RTD_WIFI_APP;


@SuppressWarnings("DefaultFileTemplate")
public class NdefRecordFactory {

    private enum NdefRecordType {
        EMPTY_RECORD_TYPE,
        SMS_RECORD_TYPE,
        EMAIL_RECORD_TYPE,
        TEXT_RECORD_TYPE,
        URI_RECORD_TYPE,
        VCARD_RECORD_TYPE,
        WIFI_RECORD_TYPE,
        BT_RECORD_TYPE,
        BTLE_RECORD_TYPE,
        AAR_RECORD_TYPE,
        MIME_RECORD_TYPE,
        EXTERNAL_RECORD_TYPE,
        UNKNOWN_TYPE
    }

    /**
     * Function parsing a ByteArrayInputStream and extracting the FIRST NDEFRecord.
     * @param inputStream
     * @return
     */
    static public NDEFRecord getNdefRecord(ByteArrayInputStream inputStream) throws Exception {

        inputStream.mark(0);
        NDEFRecord record = new NDEFRecord(inputStream);

        NdefRecordFactory.NdefRecordType ndefRecordType = getNdefRecordType(record);

        inputStream.reset();
        // Instantiate the type of record corresponding to this NDEF Record
        switch(ndefRecordType) {
            case TEXT_RECORD_TYPE:
                record = new TextRecord(inputStream);
                break;
            case URI_RECORD_TYPE:
                record = new UriRecord(inputStream);
                break;
            case SMS_RECORD_TYPE:
                record = new SmsRecord(inputStream);
                break;
            case EMAIL_RECORD_TYPE:
                record = new EmailRecord(inputStream);
                break;
            case VCARD_RECORD_TYPE:
                record = new VCardRecord(inputStream);
                break;
            case WIFI_RECORD_TYPE:
                record = new WifiRecord(inputStream);
                break;
            case BT_RECORD_TYPE:
                record = new BtRecord(inputStream);
                break;
            case BTLE_RECORD_TYPE:
                record = new BtLeRecord(inputStream);
                break;
            case AAR_RECORD_TYPE:
                record = new AarRecord(inputStream);
                break;
            case MIME_RECORD_TYPE:
                record = new MimeRecord(inputStream);
                break;
            case EXTERNAL_RECORD_TYPE:
                record = new ExternalRecord(inputStream);
                break;
            case EMPTY_RECORD_TYPE:
                record = new EmptyRecord(inputStream);
                break;

            case UNKNOWN_TYPE:
            default:
                record = null;
                break;
        }

        return record;
    }



    static private NdefRecordType getNdefRecordType(NDEFRecord record) throws Exception {
        NdefRecordType recordType = NdefRecordType.EXTERNAL_RECORD_TYPE;
        short tnf = record.getTnf();

        if (tnf == NDEFRecord.TNF_EMPTY) {
            return NdefRecordType.EMPTY_RECORD_TYPE;
        }

        byte[] typeData = record.getType();
        if(typeData == null) {
            throw new Exception("Invalid ndef data");
        }

        String type = new String(typeData);

        if (tnf == NDEFRecord.TNF_WELLKNOWN) {
            if (Arrays.equals(type.getBytes(), NDEFRecord.RTD_URI)) {
                byte[] payload = record.getPayload();
                if (payload != null) {
                    if (payload[0] == SmsRecord.ID) { //payload.startsWith(SmsRecord.SCHEME))
                        return NdefRecordType.SMS_RECORD_TYPE;
                    } else if (payload[0] == EmailRecord.ID) {
                        return NdefRecordType.EMAIL_RECORD_TYPE;
                    } else {
                        return NdefRecordType.URI_RECORD_TYPE;
                    }
                }
                else return NdefRecordType.UNKNOWN_TYPE;
            } else if (Arrays.equals(type.getBytes(), NDEFRecord.RTD_TEXT)) {
                return NdefRecordType.TEXT_RECORD_TYPE;
            }
        }

        if (tnf == NDEFRecord.TNF_EXTERNAL) {
            if (Arrays.equals(type.getBytes(), RTD_ANDROID_APP)) {
                return NdefRecordType.AAR_RECORD_TYPE;
            } else {
                return NdefRecordType.EXTERNAL_RECORD_TYPE;
            }
        }
        if (tnf == NDEFRecord.TNF_URI) {
            return NdefRecordType.URI_RECORD_TYPE;
        }

        if (tnf == NDEFRecord.TNF_MEDIA) {
            if (Arrays.equals(type.getBytes(), RTD_SMS)) {
                return NdefRecordType.SMS_RECORD_TYPE;
            } else if (Arrays.equals(type.getBytes(), RTD_VCARD_APP)) {
                return NdefRecordType.VCARD_RECORD_TYPE;
            }else if (Arrays.equals(type.getBytes(), RTD_BT_APP)) {
                return NdefRecordType.BT_RECORD_TYPE;
            } else if (Arrays.equals(type.getBytes(), RTD_BTLE_APP)) {
                return NdefRecordType.BTLE_RECORD_TYPE;
            } else if (Arrays.equals(type.getBytes(), RTD_WIFI_APP)) {
                return NdefRecordType.WIFI_RECORD_TYPE;
            } else {
                return NdefRecordType.MIME_RECORD_TYPE;
            }
        }

        if (tnf == NDEFRecord.TNF_UNKNOWN) {
            return NdefRecordType.MIME_RECORD_TYPE;
        }

        return recordType;
    }



}
