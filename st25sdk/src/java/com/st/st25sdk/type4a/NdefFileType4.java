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

package com.st.st25sdk.type4a;

import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso7816Command;
import com.st.st25sdk.command.NdefType4Command;
import com.st.st25sdk.command.Type4Command;
import com.st.st25sdk.ndef.NDEFMsg;

public class NdefFileType4 extends FileType4 {

    private NDEFMsg mNdefMsg;
    protected Iso7816Command mIso7816Cmd;
    protected NdefType4Command mNdefType4Cmd;

    public NdefFileType4(Type4Tag tag, int fileId) throws STException{
        super(tag.getType4Command(), fileId);
        mIso7816Cmd = new Iso7816Command(tag.getReaderInterface());
        try {
            mNdefType4Cmd = new NdefType4Command(tag.getReaderInterface(), tag.getApduMaxReadSize(), tag.getApduMaxWriteSize());
        }
        catch (STException e) {
            e.printStackTrace();
            throw new STException(STException.STExceptionCode.INVALID_NDEF_DATA);
        }
    }


    /**
     * This method just executes type 4 command without any select
     * @param msg
     * @throws STException
     */
    public void writeMsg(NDEFMsg msg) throws  STException{
        if (canWrite()) {
            mNdefType4Cmd.writeNdefMessage(msg);
            mNdefMsg = msg.copy();
        }
        else {
            throw new STException(STException.STExceptionCode.CMD_FAILED);
        }
    }


    public void write(NDEFMsg msg) throws STException {
        write(msg, null);
    }


    public void write(NDEFMsg msg, byte[] writePassword) throws STException {
        // The user is not allowed to write the message, even after presenting a password.
        if (canWrite()) {
            synchronized (Type4Command.mLock) {
                select();
                if (writePassword != null)
                    mIso7816Cmd.verify((byte) 0x00, (byte) 0x00, (byte) 0x02, writePassword);
                writeMsg(msg);
            }
        }
        else
            throw new STException(STException.STExceptionCode.CMD_FAILED);
    }

    public NDEFMsg readMsg() throws STException {
        if (mNdefMsg != null) {
            // The user is not allowed to read the message, even after presenting a password.
            if (canRead())
                return mNdefMsg.copy();
            else
                throw new STException(STException.STExceptionCode.CMD_FAILED);
        }

        mNdefMsg = mNdefType4Cmd.readNdefMessage();

        if (mNdefMsg != null) {
            return mNdefMsg.copy();
        } else {
            return null;
        }
    }

    public NDEFMsg read() throws STException {
        return read(null);
    }

    public NDEFMsg read(byte[] readPassword) throws STException {
        if (mNdefMsg != null) {
            //The user is not allowed to read the message, even presenting a password.
            if (canRead())
                return mNdefMsg.copy();
            else
                throw new STException(STException.STExceptionCode.CMD_FAILED);
        }

        synchronized (Type4Command.mLock) {
            select();
            if (readPassword != null)
                mIso7816Cmd.verify((byte) 0x00, (byte) 0x00, (byte) 0x01, readPassword);
            return readMsg();
        }
    }

    private boolean canRead() {
        return !(mReadAccess != 0x00 && ((mReadAccess & 0xF0) == 0x00));
    }

    private boolean canWrite() {
        return !((mWriteAccess != 0x00 && ((mWriteAccess & 0xF0) == 0x00)) || (mWriteAccess == (byte) 0xFF));
    }
}

