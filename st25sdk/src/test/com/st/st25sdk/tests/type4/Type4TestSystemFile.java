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

package com.st.st25sdk.tests.type4;

import static org.junit.Assert.assertEquals;

import com.st.st25sdk.Helper;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.type4a.STType4Tag;
import com.st.st25sdk.type4a.m24srtahighdensity.M24SRTag;

public class Type4TestSystemFile {

    static public void run(STType4Tag stType4Tag) throws STException {

        byte[] sysFile = stType4Tag.readSysFile();

        int sysFileLength = stType4Tag.getSysFileLength();
        STLog.i("sysFileLength: " + sysFileLength);

        // icRef is converted to int for conveniency
        int icRef = (stType4Tag.getICRef() & 0xFF);
        STLog.i("icRef: 0x" + Helper.convertIntToHexFormatString(icRef));

        int memSizeInBytes = stType4Tag.getMemSizeInBytes();
        STLog.i("memSizeInBytes: " + memSizeInBytes);

        switch(icRef) {
            case 0x86:  // M24SR04
                assertEquals(18, sysFileLength);
                assertEquals(512, memSizeInBytes);
                break;

            case 0x84:  // M24SR64
                assertEquals(18, sysFileLength);
                assertEquals(8192, memSizeInBytes);
                break;

            case 0xC4:  // ST25TA64K
                assertEquals(18, sysFileLength);
                assertEquals(8192, memSizeInBytes);
                break;

            case 0xA2:  // ST25TA02K
                assertEquals(18, sysFileLength);
                assertEquals(256, memSizeInBytes);
                break;
        }

        if (stType4Tag instanceof M24SRTag) {
            M24SRTag m24SRTag = (M24SRTag) stType4Tag;

            byte i2cProtected = m24SRTag.getI2CProtected();
            STLog.i("i2cProtected: 0x" + Helper.convertByteToHexString(i2cProtected));

            byte i2cWatchdog = m24SRTag.getI2CWatchdog();
            STLog.i("i2cWatchdog: 0x" + Helper.convertByteToHexString(i2cWatchdog));

            byte gpo = m24SRTag.getGpo();
            STLog.i("gpo: 0x" + Helper.convertByteToHexString(gpo));

            byte rfEnabled = m24SRTag.getRfEnabled();
            STLog.i("rfEnabled: 0x" + Helper.convertByteToHexString(rfEnabled));

            // NB: No Assert is done because we can't write those fields and we can't predict their values
        }
    }


}
