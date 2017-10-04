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

package com.st.st25sdk.type4a.m24srtahighdensity;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;

public class M24SR02KTag extends M24SRTag {

    public M24SR02KTag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface, uid);
        mName = "M24SR02K";
        mTypeDescription = NFCTag.DYNAMIC_NFC_RFID_TAG;
        mMemSize = 256;
    }

}
