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

package com.st.st25sdk.tests.type5;

import com.st.st25sdk.STException;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type5.STType5Tag;

import org.junit.Assert;

public class Type5TestReadWriteAfi {

    static byte mAfiForTest = (byte) 0xAE;

    static public void run(STType5Tag stType5Tag) throws STException {

        byte initialAfi = stType5Tag.getAFI();
        stType5Tag.writeAFI(mAfiForTest);
        NFCTagUtils.invalidateCache(stType5Tag);
        byte newAfi = stType5Tag.getAFI();
        //Restore initial tag state
        stType5Tag.writeAFI(initialAfi);
        Assert.assertEquals(newAfi, mAfiForTest);
    }
}
