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

import static com.st.st25sdk.type4a.Type4Tag.AccessStatus.LOCKED_BY_PASSWORD;
import static com.st.st25sdk.type4a.Type4Tag.AccessStatus.NOT_AUTHORIZED;
import static com.st.st25sdk.type4a.Type4Tag.AccessStatus.NOT_LOCKED;
import static com.st.st25sdk.type4a.Type4Tag.AccessStatus.STATUS_UNKNOWN;
import static com.st.st25sdk.type4a.Type4Tag.Type4FileType.NDEF_FILE;

import com.st.st25sdk.CacheInterface;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STException.STExceptionCode;
import com.st.st25sdk.STLog;
import com.st.st25sdk.command.Iso7816Command;
import com.st.st25sdk.command.Type4Command;
import com.st.st25sdk.command.Type4CustomCommand;
import com.st.st25sdk.ndef.NDEFMsg;

public class STType4Tag extends Type4Tag implements CacheInterface, STType4PasswordInterface {

    protected Iso7816Command mIso7816Cmd;
    protected Type4CustomCommand mSTType4Cmd;
    protected STSysFileType4 mSysFile;

    public static final int READ_PASSWORD = 1;
    public static final int WRITE_PASSWORD = 2;
    public static final int SYS_FILE_IDENTIFIER = 0xE101;

    public STType4Tag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface, uid);
        mSTType4Cmd = new Type4CustomCommand(readerInterface, getCCMaxReadSize(), getCCMaxWriteSize());
        mIso7816Cmd = new Iso7816Command(readerInterface);
        mSysFile = new STSysFileType4(mType4Cmd);

        mTypeDescription = NFCTag.DYNAMIC_NFC_RFID_TAG;
        mCache.add(mSysFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readBytes(int offsetInBytes, int sizeInBytes) throws STException {
        synchronized (Type4Command.mLock) {
            int ndefFileId = mCCFile.getNdefFileId();
            selectFile(ndefFileId);
            // On STType4Tag, use extendedReadBinary command in order to be able to read Bytes
            // independently from the size of the NDEF file
            return mSTType4Cmd.extendedReadData(offsetInBytes, sizeInBytes);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readBytes(int fileId, int offsetInBytes, int sizeInBytes) throws STException {
        synchronized (Type4Command.mLock) {
            selectFile(fileId);
            // On STType4Tag, use extendedReadBinary command in order to be able to read Bytes
            // independentely from the size of the NDEF file
            return mSTType4Cmd.extendedReadData(offsetInBytes, sizeInBytes);
        }
    }

    @Override
    public void selectSysFile() throws STException {
        STLog.i("Select Sys File");
        mSysFile.select();
    }

    @Override
    public byte[] readSysFile() throws STException {
        return mSysFile.getData();
    }

    @Override
    public int getSysFileLength() throws STException {
        return mSysFile.getLength();
    }

    @Override
    public int getMemSizeInBytes() throws STException {
        return mSysFile.getMemSizeInBytes();
    }

    @Override
    public  byte getICRef() throws STException {
        return mSysFile.getICRef();
    }

    /**
     * Get the current read access rights for this file
     * @param fileId
     * @return
     * @throws STException
     */
    @Override
    public AccessStatus getFileReadAccessStatus(int fileId) throws STException {
        AccessStatus readAccessStatus;
        byte readAccess =  getFileReadAccess(fileId);

        if (readAccess == (byte) 0x00) {
            readAccessStatus = NOT_LOCKED;
        } else if (readAccess == (byte) 0x80) {
            readAccessStatus = LOCKED_BY_PASSWORD;
        } else if (readAccess == (byte) 0xFE) {
            readAccessStatus = NOT_AUTHORIZED;
        } else {
            readAccessStatus = STATUS_UNKNOWN;
        }

        return readAccessStatus;
    }

    /**
     * Get the current write access rights for this file
     * @param fileId
     * @return
     * @throws STException
     */
    @Override
    public AccessStatus getFileWriteAccessStatus(int fileId) throws STException {
        AccessStatus writeAccessStatus;
        byte writeAccess =  getFileWriteAccess(fileId);

        if (writeAccess == (byte) 0x00) {
            writeAccessStatus = NOT_LOCKED;
        } else if (writeAccess == (byte) 0xFF) {
            writeAccessStatus = NOT_AUTHORIZED;
        } else if (writeAccess == (byte) 0x80) {
            writeAccessStatus = LOCKED_BY_PASSWORD;
        } else {
            writeAccessStatus = STATUS_UNKNOWN;
        }

        return writeAccessStatus;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Before using the functions below, a selectFile() should be done.

    public void verify(byte cla, byte p1, byte p2, byte[] password) throws STException {
        mIso7816Cmd.verify(cla, p1, p2, password);
    }

    public void verifyReadPassword(byte[] readPassword) throws STException {
        mIso7816Cmd.verify((byte) 0x00, (byte) 0x00, (byte) 0x01, readPassword);
    }

    public void verifyWritePassword(byte[] writePassword) throws STException {
        mIso7816Cmd.verify((byte) 0x00, (byte) 0x00, (byte) 0x02, writePassword);
    }

    public void changeReferenceData(byte p1, byte p2, byte[] newPassword) throws STException {
        mIso7816Cmd.changeReferenceData((byte) 0x00, p1, p2, newPassword);
    }

    public void changeReadPassword(byte[] newReadPassword) throws STException {
        mIso7816Cmd.changeReferenceData((byte) 0x00, (byte) 0x00, (byte) 0x01, newReadPassword);
    }

    public void changeWritePassword(byte[] newWritePassword) throws STException {
        mIso7816Cmd.changeReferenceData((byte) 0x00, (byte) 0x00, (byte) 0x02, newWritePassword);
    }


    public void enableVerificationReq(byte cla, byte p1, byte p2) throws STException {
        mIso7816Cmd.enableVerificationReq(cla, p1, p2);
    }


    public void lockRead() throws STException {
        byte cla = (byte) 0x00;
        mIso7816Cmd.enableVerificationReq(cla, (byte) 0x00, (byte) 0x01);
    }

    public void lockReadPermanently() throws STException {
        byte cla = (byte) 0xA2;
        mIso7816Cmd.enableVerificationReq(cla, (byte) 0x00, (byte) 0x01);
    }

    public void lockWrite() throws STException {
        byte cla = (byte) 0x00;
        mIso7816Cmd.enableVerificationReq(cla, (byte) 0x00, (byte) 0x02);
    }

    public void lockWritePermanently() throws STException {
        byte cla = (byte) 0xA2;
        mIso7816Cmd.enableVerificationReq(cla, (byte) 0x00, (byte) 0x02);
    }

    public void unlockRead() throws STException {
        mIso7816Cmd.disableVerificationReq((byte) 0x00, (byte) 0x00, (byte) 0x01);
    }

    public void disableVerificationReq(byte cla, byte p1, byte p2) throws STException {
        mIso7816Cmd.disableVerificationReq(cla, p1, p2);
    }

    public void unlockWrite() throws STException {
        mIso7816Cmd.disableVerificationReq((byte) 0x00, (byte) 0x00, (byte) 0x02);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Functions calling ST commands
    public byte[] enablePermanentState(byte p1, byte p2) throws STException {
        return mSTType4Cmd.enablePermanentState(p1, p2);
    }

    public byte[] extendedReadBinary(byte p1, byte p2, byte length) throws STException {
        return mSTType4Cmd.extendedReadBinary(p1, p2, length);
    }

    public byte[] updateFileType(byte data) throws STException {
        return mSTType4Cmd.updateFileType(data);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Functions calling SelectFile() and then doing the requested action.

    /**
     * This function will do the following actions in an atomic manner:
     * - select the indicated file
     * - present the read password
     * - read the requested bytes
     *
     * @param fileId : File identifier.
     * @param byteAddress
     * @param sizeInBytes
     * @param readPassword
     * @return
     * @throws STException
     */
    public byte[] readBytes(int fileId, int byteAddress, int sizeInBytes, byte[] readPassword) throws STException {
        synchronized (Type4Command.mLock) {
            selectFile(fileId);
            verifyReadPassword(fileId, readPassword);
            return mSTType4Cmd.readData(byteAddress, sizeInBytes);
        }
    }

    /**
     * This function will do the following actions in an atomic manner:
     * - select the indicated file
     * - present the write password
     * - write the requested bytes
     *
     * @param fileId : File identifier.
     * @param byteAddress
     * @param data
     * @param writePassword
     * @throws STException
     */
    public void writeBytes(int fileId, int byteAddress, byte[] data, byte[] writePassword) throws STException {
        synchronized (Type4Command.mLock) {
            selectFile(fileId);
            verifyWritePassword(writePassword);
            mSTType4Cmd.writeData(byteAddress, data);
        }
    }

    /**
     * This function will do the following actions in an atomic manner:
     * - select the indicated file
     * - present the read password
     * - read the NDEF message
     *
     * @param fileId : File identifier.
     * @param readPassword
     * @return
     * @throws STException
     */
    public NDEFMsg readNdefMessage(int fileId, byte[] readPassword) throws STException {
        NDEFMsg ndefMsg;
        NdefFileType4 ndefFileType4  = getNdefFileCached(fileId);

        if (mCache.isCacheActivated() && mCache.isCacheValid(ndefFileType4)) {
            //remove from cache is a precaution as if it is in the cache no error should happen...
            mCache.remove(ndefFileType4);
            ndefMsg = ndefFileType4.read(readPassword);
            mCache.add(ndefFileType4);
            return ndefMsg;
        }

        //File is not in the cache or is not valid so remove it
        mCache.remove(ndefFileType4);
        ndefFileType4 = new NdefFileType4(this, fileId);
        ndefMsg = ndefFileType4.read(readPassword);
        mNdefFileList.put(fileId, ndefFileType4);
        mCache.add(ndefFileType4);
        return ndefMsg;
    }

    /**
     * This function will do the following actions in an atomic manner:
     * - select the indicated file
     * - present the read password
     * - write the NDEF message
     *
     * @param fileId : File identifier.
     * @param msg
     * @param writePassword
     * @throws STException
     */
    public void writeNdefMessage(int fileId, NDEFMsg msg, byte[] writePassword) throws STException {
        int ndefMsgLength;
        try {
            ndefMsgLength = msg.getLength();
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(STException.STExceptionCode.INVALID_NDEF_DATA);
        }

        /* Check NDEF file max size is big enough */
        if(getCCFileTlv(fileId).getMaxFileSize() < ndefMsgLength)
        {
            throw new STException(STExceptionCode.NDEF_MESSAGE_TOO_BIG);
        }

        NdefFileType4 ndefFileType4 = mNdefFileList.get(fileId);
        if (ndefFileType4 != null) {
            ndefFileType4.write(msg, writePassword);
            return;
        }

        // File is not in the cache so creates a new one
        ndefFileType4 = new NdefFileType4(this, fileId);
        ndefFileType4.write(msg, writePassword);
        mCache.add(ndefFileType4);
    }

    @Override
    public void lockRead(int fileId, byte[] writePassword) throws STException {
        synchronized (Type4Command.mLock) {
            selectFile(fileId);
            verifyWritePassword(writePassword);
            lockRead();
        }
    }

    @Override
    public void lockReadPermanently(int fileId, byte[] writePassword) throws STException {
        synchronized (Type4Command.mLock) {
            selectFile(fileId);
            verifyWritePassword(writePassword);
            lockReadPermanently();
        }
    }

    @Override
    public void lockWrite(int fileId, byte[] writePassword) throws STException {
        synchronized (Type4Command.mLock) {
            selectFile(fileId);
            verifyWritePassword(writePassword);
            lockWrite();
        }
    }

    @Override
    public void lockWritePermanently(int fileId, byte[] writePassword) throws STException {
        synchronized (Type4Command.mLock) {
            selectFile(fileId);
            verifyWritePassword(writePassword);
            lockWritePermanently();
        }
    }

    @Override
    public void unlockRead(int fileId, byte[] writePassword) throws STException {
        synchronized (Type4Command.mLock) {
            selectFile(fileId);
            verifyWritePassword(writePassword);
            unlockRead();
        }
    }

    @Override
    public void unlockWrite(int fileId, byte[] writePassword) throws STException {
        synchronized (Type4Command.mLock) {
            selectFile(fileId);
            verifyWritePassword(writePassword);
            unlockWrite();
        }
    }

    @Override
    public boolean isReadPasswordRequested(int fileId) throws STException {
        boolean isReadPasswordRequested;

        synchronized (Type4Command.mLock) {
            try {
                selectFile(fileId);
                mIso7816Cmd.verify((byte) 0x00, (byte) 0x00, (byte) 0x01, null);
                isReadPasswordRequested = false;

            } catch (STException e) {
                switch (e.getError()) {
                    case PASSWORD_NEEDED:
                        isReadPasswordRequested = true;
                        break;

                    default:
                        // Other exceptions are unchanged
                        throw(e);
                }
            }
        }

        return isReadPasswordRequested;
    }

    @Override
    public boolean isWritePasswordRequested(int fileId) throws STException {
        boolean isWritePasswordRequested;

        synchronized (Type4Command.mLock) {
            try {
                selectFile(fileId);
                mIso7816Cmd.verify((byte) 0x00, (byte) 0x00, (byte) 0x02, null);
                isWritePasswordRequested = false;

            } catch (STException e) {
                switch (e.getError()) {
                    case PASSWORD_NEEDED:
                        isWritePasswordRequested = true;
                        break;

                    default:
                        // Other exceptions are unchanged
                        throw(e);
                }
            }
        }

        return isWritePasswordRequested;
    }

    @Override
    public void verifyReadPassword(int fileId, byte[] readPassword) throws STException {
        synchronized (Type4Command.mLock) {
            selectFile(fileId);
            verifyReadPassword(readPassword);
        }
    }

    @Override
    public void verifyWritePassword(int fileId, byte[] writePassword) throws STException {
        synchronized (Type4Command.mLock) {
            selectFile(fileId);
            verifyWritePassword(writePassword);
        }
    }

    @Override
    public void changeReadPassword(int fileId, byte[] newReadPassword, byte[] currentWritePassword) throws STException {
        synchronized (Type4Command.mLock) {
            selectFile(fileId);
            verifyWritePassword(currentWritePassword);
            changeReadPassword(newReadPassword);
        }
    }

    @Override
    public void changeWritePassword(int fileId, byte[] newWritePassword, byte[] currentWritePassword) throws STException {
        synchronized (Type4Command.mLock) {
            selectFile(fileId);
            verifyWritePassword(currentWritePassword);
            changeWritePassword(newWritePassword);
        }
    }

    @Override
    public int getReadPasswordLengthInBytes(int fileId) throws STException {
        return 16;
    }

    @Override
    public int getWritePasswordLengthInBytes(int fileId) throws STException {
        return 16;
    }

    /**
     * Change the type of a file.
     * @param fileId
     */
    public void setFileType(int fileId, Type4FileType fileType) throws STException {
        byte data;

        synchronized (Type4Command.mLock) {
            selectFile(fileId);

            if(fileType == NDEF_FILE) {
                data = 0x04;
            } else {
                data = 0x05;
            }
            updateFileType(data);

            // CC File's cache should be flushed when file types are changing
            mCCFile.invalidateCache();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////

}
