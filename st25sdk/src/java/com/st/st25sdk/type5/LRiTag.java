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

package com.st.st25sdk.type5;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso15693CustomKillCommandInterface;

public class LRiTag extends STType5Tag  implements Iso15693CustomKillCommandInterface {

    public LRiTag(RFReaderInterface readerInterface, byte[] uid) {
        super(readerInterface, uid);

        mTypeDescription = NFCTag.NFC_RFID_TAG;
    }

    //////////////////////////// KILL COMMANDS  ///////////////////////////////////

    @Override
    public byte kill(byte[] unencryptedKillCode)  throws STException {
        return mIso15693CustomCommand.kill(unencryptedKillCode);
    }

    @Override
    public byte writeKill(byte[] unencryptedKillPassword)  throws STException {
        return mIso15693CustomCommand.writeKill(unencryptedKillPassword);
    }

    @Override
    public byte lockKill()  throws STException {
        return mIso15693CustomCommand.lockKill();
    }

    //////////////////////////////// INITIATE COMMANDS  ////////////////////////////

    public byte[] initiate(byte flag) throws STException {
        return mIso15693CustomCommand.initiate(flag);
    }

    public byte[] inventoryInitiated(byte flag) throws STException {
        return mIso15693CustomCommand.inventoryInitiated(flag);
    }
    public byte[] inventoryInitiated(byte flag, byte maskLength, byte[] maskValue) throws STException {
        return mIso15693CustomCommand.inventoryInitiated(flag, maskLength, maskValue);
    }
    public byte[] inventoryInitiated(byte flag, byte maskLength, byte[] maskValue, byte afiField) throws STException {
        return mIso15693CustomCommand.inventoryInitiated(flag, maskLength, maskValue, afiField);
    }

    public byte[] fastInitiate(byte flag) throws STException{
        return mIso15693CustomCommand.fastInitiate(flag);
    }

    public byte[] fastInventoryInitiated(byte flag) throws STException {
        return mIso15693CustomCommand.fastInventoryInitiated(flag);
    }
    public byte[] fastInventoryInitiated(byte flag, byte maskLength, byte[] maskValue) throws STException {
        return mIso15693CustomCommand.fastInventoryInitiated(flag, maskLength, maskValue);
    }
    public byte[] fastInventoryInitiated(byte flag, byte maskLength, byte[] maskValue, byte afiField) throws STException {
        return mIso15693CustomCommand.fastInventoryInitiated(flag, maskLength, maskValue, afiField);
    }
}
