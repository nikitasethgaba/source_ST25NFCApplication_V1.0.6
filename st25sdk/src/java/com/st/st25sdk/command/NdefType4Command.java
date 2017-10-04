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

package com.st.st25sdk.command;

import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.ndef.NDEFMsg;

import static com.st.st25sdk.STException.STExceptionCode.INVALID_NDEF_DATA;

public class NdefType4Command {

    private Type4Command mType4Command;

    public NdefType4Command(RFReaderInterface readerInterface, int maxRApduSize, int maxCApduSize) {
        super();
        mType4Command = new Type4Command(readerInterface, maxRApduSize, maxCApduSize);
    }

    public NDEFMsg readNdefMessage() throws STException {
        NDEFMsg ndefMsg;
        byte[] buffer;
        int sizeInBytes;

        synchronized (Type4Command.mLock) {
            buffer = mType4Command.readBinary((byte) 0x00, (byte) 0x00, (byte) 0x02);

            sizeInBytes = (0xFF00 & (buffer[0] << 8)) + (0xFF & buffer[1]);
            buffer = mType4Command.readData(0x02, sizeInBytes);

            try {
                ndefMsg = new NDEFMsg(buffer);
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new STException(STException.STExceptionCode.INVALID_NDEF_DATA);
            }
        }

        return ndefMsg;
    }

    public void writeNdefMessage(NDEFMsg msg) throws STException {
        byte[] buffer;
        byte[] length;


        if (msg.getNDEFRecords().size() > 0) {
            try {
                buffer = msg.serialize();
            } catch (Exception e) {
                e.printStackTrace();
                throw new STException(INVALID_NDEF_DATA);
            }
            length = new byte[]{(byte) ((0xFF00 & buffer.length) >> 8), (byte) (0xFF & buffer.length)};
        } else {
            buffer = new byte[]{0x00};
            length = new byte[]{0x00, 0x01};
        }


        synchronized (Type4Command.mLock) {
            //Following NFC specifications to write an NDEF:
            // 1) write id + length = 0
            mType4Command.updateBinary((byte) 0x00, (byte) 0x00, new byte[] {0x00, 0x00});

            // 2) write the payload

            mType4Command.writeData(0x02, buffer);

            // 3) write id + correct length
            mType4Command.updateBinary((byte) 0x00, (byte) 0x00, length);
        }
    }

}
