package com.st.st25sdk.tests.generic;


import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.ndef.AarRecord;
import com.st.st25sdk.ndef.BtLeRecord;
import com.st.st25sdk.ndef.BtRecord;
import com.st.st25sdk.ndef.EmailRecord;
import com.st.st25sdk.ndef.EmptyRecord;
import com.st.st25sdk.ndef.ExternalRecord;
import com.st.st25sdk.ndef.MimeRecord;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.ndef.NDEFRecord;
import com.st.st25sdk.ndef.SmsRecord;
import com.st.st25sdk.ndef.TextRecord;
import com.st.st25sdk.ndef.UriRecord;
import com.st.st25sdk.ndef.VCardRecord;
import com.st.st25sdk.ndef.WifiRecord;

import static com.st.st25sdk.ndef.MimeRecord.NdefMimeIdCode.NDEF_MIME_VIDEO_X_MSVIDEO;
import static com.st.st25sdk.ndef.UriRecord.NdefUriIdCode.NDEF_RTD_URI_ID_HTTP_WWW;
import static org.junit.Assert.fail;

/**
 * This test checks the robustness of the NDEF parser (in case of error in the byte array)
 */
public class NFCTagTestNdefParser {

    // When set to false, the test runs for about 700ms on Android
    // When set to true, all the possible values are tested and the test runs for 1min 11 seconds on Android
    static boolean performIntensiveTesting = false;

    static public void run(NFCTag tag) throws STException, Exception {

        try {

            // During this test, we turn OFF "DBG_NDEF_RECORD" because we generate intended errors
            setNdefRecordDebugCode(false);

            // Build a dummy NDEFMsg containing every kind of NDEF records
            NDEFMsg ndefMsg = new NDEFMsg();

            TextRecord textRecord = new TextRecord("a");
            ndefMsg.addRecord(textRecord);

            UriRecord uriRecord = new UriRecord(NDEF_RTD_URI_ID_HTTP_WWW, "b");
            ndefMsg.addRecord(uriRecord);

            EmailRecord emailRecord = new EmailRecord("d", "e", "f");
            ndefMsg.addRecord(emailRecord);

            SmsRecord smsRecord = new SmsRecord("1", "g");
            ndefMsg.addRecord(smsRecord);

            AarRecord aarRecord = new AarRecord("h");
            ndefMsg.addRecord(aarRecord);

            byte[] macAddr = new byte[] {0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
            BtRecord btRecord = new BtRecord();
            btRecord.setBTDeviceName("i");
            btRecord.setBTDeviceMacAddr(macAddr);
            ndefMsg.addRecord(btRecord);

            BtLeRecord btLeRecord = new BtLeRecord();
            btLeRecord.setBTDeviceName("j");
            btLeRecord.setBTDeviceMacAddr(macAddr);
            ndefMsg.addRecord(btLeRecord);

            EmptyRecord emptyRecord = new EmptyRecord();
            ndefMsg.addRecord(emptyRecord);

            ExternalRecord externalRecord = new ExternalRecord();
            ndefMsg.addRecord(externalRecord);

            byte[] mimeData = new byte[] { 0x55 };
            MimeRecord mimeRecord = new MimeRecord(NDEF_MIME_VIDEO_X_MSVIDEO, mimeData);
            ndefMsg.addRecord(mimeRecord);

            VCardRecord vCardRecord =  new VCardRecord();
            ndefMsg.addRecord(vCardRecord);

            WifiRecord wifiRecord = new WifiRecord();
            wifiRecord.setSSID("1");
            wifiRecord.setAuthType(1);  // WPA/WPA2 PSK
            wifiRecord.setEncrType(2);  // AES
            wifiRecord.setEncrKey("2");
            ndefMsg.addRecord(wifiRecord);

            // Convert the NDEF to a byte[]
            byte[] ndefData = ndefMsg.serialize();

            // Check the NDEF parser behavior when corrupting one byte at a time in the byte array.

            // NB: This is testNdefReconstruction() that will check if the NDEF reconstruction is
            //     working or failing. In case of Exception, it will check if it is an accepted one.
            for(int index=0; index<ndefData.length; index++) {
                byte originalValue = ndefData[index];

                if(performIntensiveTesting) {
                    // Long test checking all the possible values
                    for(int value=0; value<=0xFF; value++) {
                        ndefData[index] = (byte) (value & 0xFF);
                        testNdefReconstruction(index, ndefData);
                    }

                } else {
                    // Short test

                    // Test with 0xFF value
                    ndefData[index] = (byte) 0xFF;
                    testNdefReconstruction(index, ndefData);

                    // Test with 0x00 value
                    ndefData[index] = 0x00;
                    testNdefReconstruction(index, ndefData);

                    // Test with ~ of current value
                    ndefData[index] = (byte) ~(ndefData[index]);
                    testNdefReconstruction(index, ndefData);
                }

                // Restore the original value
                ndefData[index] = originalValue;
            }

        }
        finally {
            // Reenable "DBG_NDEF_RECORD"
            setNdefRecordDebugCode(true);
        }

    }

    /**
     * Function checking the behavior of the NDEF Parser when reconstructing a NDEF Msg
     * from the byte array passed in argument.
     * @param byteCorrupted
     * @param ndefData
     */
    static private void testNdefReconstruction(int byteCorrupted, byte[] ndefData) {

        try {
            NDEFMsg ndefMsg = new NDEFMsg(ndefData);
            // No error seen

        } catch (Exception e) {
            // Here we can get some Exceptions like:
            //   "java.lang.Exception: Invalid ndef data"
            //   "java.lang.Exception: Payload too long"
            //   "java.lang.Exception: No data available"
            //   ...etc
            //
            // They are raised by our SDK and are normal errors due to the corruptions
            // that we have done in the byte array.
            //
            // There can be also some other errors like "java.lang.ArrayIndexOutOfBoundsException".
            // Those errors are not accepted because the SDK should have detected the problem
            // and raised a clean Exception.

            // Check that the exception starts with "java.lang.Exception:"
            if (!e.toString().startsWith("java.lang.Exception:")) {
                e.printStackTrace();
                fail("Corruption of byte " + byteCorrupted + ": Unexpected Exception " + e.toString() );
            }
        }
    }

    static private void setNdefRecordDebugCode(boolean value) {

        try {
            NDEFRecord.class.getField("DBG_NDEF_RECORD").set(null, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
