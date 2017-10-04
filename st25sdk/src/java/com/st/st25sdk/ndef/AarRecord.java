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

/**
 *  Implements AAR NDEF message
 *  Introduced in Android 4.0 (API level 14), an Android Application Record (AAR) provides a stronger certainty that your application is started
 *  when an NFC tag is scanned.
 *  You can add an AAR to any NDEF record of your NDEF message, because Android searches the entire NDEF message for AARs.
 *  If it finds an AAR, it starts the application based on the package name inside the AAR.
 *  If the application is not present on the device, Google Play is launched to download the application.
 *  Refers to  https://developer.android.com/guide/topics/connectivity/nfc/nfc.html#well_known
 */
public class AarRecord extends NDEFRecord {


    /**
     * the package name of an application embedded inside an NDEF record
     */
    private String mAar;


    /**
     * Default constructor
     */
    public AarRecord() {
        super();

        setTnf(NDEFRecord.TNF_EXTERNAL);
        setType("android.com:pkg".getBytes());

        mAar = "";

        setId(new byte[0]);
    }

    /**
     * AarRecord Constructor with AAR package name
     * @param aar The package name of an application embedded inside an NDEF record
     */
    public AarRecord(String aar) {
        super();

        setTnf(NDEFRecord.TNF_EXTERNAL);
        setType("android.com:pkg".getBytes());

        mAar = aar;

        setSR();

        setId(new byte[0]);
    }

    public AarRecord(ByteArrayInputStream inputStream) throws Exception{
        super(inputStream);
        byte[] payload = super.getPayload();

        if (payload == null) {
            throw new Exception("Invalid ndef data");
        }

        setTnf(NDEFRecord.TNF_EXTERNAL);
        setType("android.com:pkg".getBytes());

        if (payload != null)
            mAar = new String(payload);
        else
            mAar = "";

        setId(new byte[0]);

        if(DBG_NDEF_RECORD) {
            dbgCheckNdefRecordContent(payload);
        }

    }


    /**
     * Set the AAR package name
     * @param aar Package name of the Android Application Record
     */
    public void setAar(String aar) {
        mAar = aar;
    }

    /**
     * Get the AAR package name
     * @return String containing AAR package name
     */
    public String  getAar() {
        return mAar;
    }


    // NB: The Payload is computed on the fly every times we need it
    @Override
    public byte[] getPayload() {
        byte[] payload = null;

        if (mAar != null) {
            payload = mAar.getBytes();
        }

        return payload;
    }
}
