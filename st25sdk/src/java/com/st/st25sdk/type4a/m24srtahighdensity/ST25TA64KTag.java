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

import com.st.st25sdk.CacheInterface;
import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;

public class ST25TA64KTag extends M24SRTAHighDensityTag implements CacheInterface, MultiAreaInterface {

    public ST25TA64KTag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface, uid);
        mName = "ST25TA64K";
        mMemSize = 8192;
    }

}
