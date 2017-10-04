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

import static com.st.st25sdk.STException.STExceptionCode.NOT_SUPPORTED;
import static com.st.st25sdk.type4a.Type4Tag.AccessStatus.NOT_AUTHORIZED;
import static com.st.st25sdk.type4a.Type4Tag.AccessStatus.NOT_LOCKED;
import static com.st.st25sdk.type4a.Type4Tag.AccessStatus.STATUS_UNKNOWN;
import static com.st.st25sdk.type4a.Type4Tag.Type4FileType.NDEF_FILE;
import static com.st.st25sdk.type4a.Type4Tag.Type4FileType.PROPRIETARY_FILE;

import java.util.Arrays;
import java.util.LinkedHashMap;

import com.st.st25sdk.CCFileInterface;
import com.st.st25sdk.CacheInterface;
import com.st.st25sdk.Helper;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STException.STExceptionCode;
import com.st.st25sdk.STLog;
import com.st.st25sdk.SysFileInterface;
import com.st.st25sdk.TagCache;
import com.st.st25sdk.command.NdefCommandInterface;
import com.st.st25sdk.command.Type4Command;
import com.st.st25sdk.command.Type4CommandInterface;
import com.st.st25sdk.ndef.NDEFMsg;

public class Type4Tag extends NFCTag implements CCFileInterface, SysFileInterface, NdefCommandInterface, Type4CommandInterface, CacheInterface {


    public static final int TYPE4_CC_FILE_IDENTIFIER  = 0xE103;
    public static final int TYPE4_INVALID_FILE_IDENTIFIER  = 0xFFFF;

    public static final int TYPE4_MAX_RADPU_SIZE  = 246;
    public static final int TYPE4_MAX_CAPDU_SIZE  = 246;

    protected CCFileType4 mCCFile;
    protected Type4Command mType4Cmd;

    protected TagCache mCache;

    protected LinkedHashMap<Integer, NdefFileType4> mNdefFileList;


    public enum Type4FileType {
        NDEF_FILE,
        PROPRIETARY_FILE
    }

    public enum AccessStatus {
        NOT_LOCKED,
        LOCKED_BY_PASSWORD,
        NOT_AUTHORIZED,      // No access possible even with a password
        STATUS_UNKNOWN
    }


    public Type4Tag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface);

        mName = "NFC Type 4A tag";
        mDescription = "NFC type 4A - ISO/IEC 14443A";

        //Creation with default values , then after creation/read of CCFile tune the right values
        mType4Cmd = new Type4Command(readerInterface, TYPE4_MAX_RADPU_SIZE, TYPE4_MAX_CAPDU_SIZE);
        // Warning: This should be after the instanciation of mType4Cmd
        mCCFile = new CCFileType4(mType4Cmd);

        // Update max sizes for read and write commands
        mType4Cmd.setMaxRApduDataSize(getCCMaxReadSize());
        mType4Cmd.setMaxCApduDataSize(getCCMaxWriteSize());

        mUid = Arrays.copyOf(uid, uid.length);

        mCache = new TagCache();
        mCache.add(mCCFile);

        mNdefFileList = new LinkedHashMap<>();
    }

    public Type4Command getType4Command() {
        return mType4Cmd;
    }

    @Override
    public int getCCFileLength() throws STException {
        return mCCFile.readLength();
    }

    @Override
    public byte getCCMagicNumber() throws STException {
        return 0;
    }

    @Override
    public byte[] readCCFile() throws STException {
        return mCCFile.read();
    }

    @Override
    public void writeCCFile() throws STException {
        // On Type4, CCFile is read only
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public void initEmptyCCFile() {
        mCCFile.initEmptyCCfile(mMemSize);
    }

    @Override
    public byte getCCMappingVersion() throws STException {
        return mCCFile.getCCMappingVersion();
    }

    @Override
    public byte getCCReadAccess() throws STException {
        return 0;
    }

    @Override
    public byte getCCWriteAccess() throws STException {
        return 0;
    }

    @Override
    public int getCCMemorySize() throws STException {
        return 0;
    }

    public int getCCMaxReadSize() throws STException {
        return mCCFile.getMaxReadSize();
    }

    public int getCCMaxWriteSize() throws STException {
        return mCCFile.getMaxWriteSize();
    }

    public int getApduMaxReadSize() throws STException {
        return mType4Cmd.getMaxRApduDataSize();
    }

    public int getApduMaxWriteSize() throws STException {
        return mType4Cmd.getMaxCApduDataSize();
    }

    public ControlTlv getCCTlv() throws STException {
        return mCCFile.getTlv(0);
    }

    // Warning: pos is in the range 0 to N-1 (where N is the number of TLV blocks)
    //          So pos = area - 1
    public ControlTlv getCCTlv(int pos) throws STException {
        return mCCFile.getTlv(pos);
    }

    public ControlTlv getCCFileTlv(int fileId) throws STException {
        return mCCFile.getFileTlv(fileId);
    }

    public int getNbOfTlv() throws STException {
        return mCCFile.getNbOfTlv();
    }

    /**
     * Function allowing to select a file by FileId.
     *
     * Warning: It is the caller responsibility to use a lock to prevent simultaneous accesses
     *          to the tag.
     *
     * @param fileId   File identifier
     * @return
     * @throws STException
     */
    @Override
    public byte[] selectFile(int fileId) throws STException {
        byte[] response = mType4Cmd.selectFile(fileId);
        STLog.i("File 0x" + Helper.convertIntToHexFormatString(fileId) + " is now selected");
        return response;
    }

    /**
     * Function allowing to select a file by FileId or by name.
     *
     * Warning: It is the caller responsibility to use a lock to prevent simultaneous accesses
     *          to the tag.
     *
     * @param p1   param as defined by Type 4
     * @param p2   param as defined by Type 4
     * @param data File identifier
     * @return
     * @throws STException
     */
    @Override
    public byte[] select(byte p1, byte p2, byte[] data) throws STException {
        return mType4Cmd.select(p1, p2, data);
    }

    /**
     * Read some data from the currently selected file.
     *
     * Warning: It is the caller responsibility to use a lock to prevent simultaneous accesses
     *          to the tag.
     *
     * @param p1     offset as defined by Type 4
     * @param p2     offset as defined by Type 4
     * @param length size to read
     * @return
     * @throws STException
     */
    @Override
    public byte[] readBinary(byte p1, byte p2, byte length) throws STException {
        return mType4Cmd.readBinary(p1, p2, length);
    }

    /**
     * Write some data to the currently selected file.
     *
     * Warning: It is the caller responsibility to use a lock to prevent simultaneous accesses
     *          to the tag.
     *
     * @param p1   offset as defined by Type 4
     * @param p2   offset as defined by Type 4
     * @param data data to write
     * @return
     * @throws STException
     */
    @Override
    public byte[] updateBinary(byte p1, byte p2, byte[] data) throws STException {
        return mType4Cmd.updateBinary(p1, p2, data);
    }

    @Override
    public byte[] selectNdef() throws STException {
        synchronized (Type4Command.mLock) {
            int ndefFileId = mCCFile.getNdefFileId();
            selectNdefTagApplication();
            byte[] response = mType4Cmd.selectFile(ndefFileId);
            STLog.i("File 0x" + Helper.convertIntToHexFormatString(ndefFileId) + " is now selected");
            return response;
        }
    }

    @Override
    public byte[] selectNdefTagApplication() throws STException {
        return mType4Cmd.selectNdefTagApplication();
    }

    @Override
    public void selectCCFile() throws STException {
        STLog.i("Select CC File");
        synchronized (Type4Command.mLock) {
            mCCFile.select();
        }
    }


    /**
     * This function reads some bytes from the NDEF file. It can be used when the caller wants
     * to handle the file selection himself.
     *
     * PREREQUISITE: The caller is responsible for selecting the NDEF file before calling
     *               this function.
     *
     * @param offsetInBytes : Offset in Bytes from the beginning of the file.
     * @param sizeInBytes   : Number of Bytes to read.
     * @return
     * @throws STException
     */
    public byte[] readData(int offsetInBytes, int sizeInBytes) throws STException {
        return mType4Cmd.readData(offsetInBytes, sizeInBytes);
    }

    public void writeData(int offsetInBytes, byte[] data) throws STException {
        mType4Cmd.writeData(offsetInBytes, data);
    }


    @Override
    public int getSysFileLength() throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public byte[] readSysFile() throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public void selectSysFile() throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public void invalidateCache() {
        mCache.invalidateCache();
    }

    @Override
    public void validateCache() { mCache.validateCache();}

    @Override
    public void activateCache() {
        mCache.activateCache();
    }

    @Override
    public void deactivateCache() {
        mCache.deactivateCache();
    }

    @Override
    public void updateCache() throws STException {
        mCache.updateCache();
    }

    @Override
    public boolean isCacheValid(){
        return mCache.isCacheValid();
    }

    @Override
    public boolean isCacheActivated(){
        return mCache.isCacheActivated();
    }

    @Override
    public int getMemSizeInBytes() throws STException {
        return 0;
    }

    /**
     * Get the FileId of the NDEF file. This NDEF file is mandatory for Type4 tags.
     * It is defined by the first TLV block of the CC File.
     *
     * @return
     * @throws STException
     */
    public int getNdefFileId() throws STException {
        return mCCFile.getNdefFileId();
    }

    /**
     * Indicates the number of files present on this tag.
     * @return
     * @throws STException
     */
    public int getNbrOfFiles() throws STException {
        return mCCFile.getNbOfTlv();
    }

    /**
     * Returns the list of files defined in the TLV blocks of the CC File.
     * The returned list is a list of fileId.
     * A file can be a NDEF File or a Proprietary file. Use getFileType() to know the file Type.
     * @return
     * @throws STException
     */
    public int[] getFileIdList() throws STException {
        int nbrOfFiles = getNbrOfFiles();
        int[] fileIdList = new int[nbrOfFiles];

        for (int i=0; i<nbrOfFiles; i++){
            fileIdList[i] = mCCFile.getTlv(i).getFileId();
        }

        return fileIdList;
    }

    /**
     * Indicates if the file is a NDEF file or Proprietary file.
     * @param fileId
     * @return
     */
    public Type4FileType getFileType(int fileId) throws STException {
        Type4FileType type4FileType;

        ControlTlv fileTlv = mCCFile.getFileTlv(fileId);

        byte fileType =  fileTlv.getType();
        if(fileType == 0x04) {
            type4FileType = NDEF_FILE;
        } else {
            type4FileType = PROPRIETARY_FILE;
        }

        return type4FileType;
    }

    /**
     * Get the max file size indicated in the TLV block of the CC File
     * @param fileId
     * @return
     * @throws STException
     */
    public int getMaxFileSize(int fileId) throws STException {
        ControlTlv fileTlv = mCCFile.getFileTlv(fileId);

        return fileTlv.getMaxFileSize();
    }

    /**
     * Get the current read access rights for this file as a hexadecimal value (1 byte)
     * @param fileId
     * @return
     * @throws STException
     */
    public byte getFileReadAccess(int fileId) throws STException {
        ControlTlv fileTlv = mCCFile.getFileTlv(fileId);

        return fileTlv.getReadAccess();
    }

    /**
     * Get the current write access rights for this file as a hexadecimal value (1 byte)
     * @param fileId
     * @return
     * @throws STException
     */
    public byte getFileWriteAccess(int fileId) throws STException {
        ControlTlv fileTlv = mCCFile.getFileTlv(fileId);

        return fileTlv.getWriteAccess();
    }

    /**
     * Get the current read access rights for this file
     * @param fileId
     * @return
     * @throws STException
     */
    public AccessStatus getFileReadAccessStatus(int fileId) throws STException {
        AccessStatus readAccessStatus;
        byte readAccess =  getFileReadAccess(fileId);

        if (readAccess == (byte) 0x00) {
            readAccessStatus = NOT_LOCKED;
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
    public AccessStatus getFileWriteAccessStatus(int fileId) throws STException {
        AccessStatus writeAccessStatus;
        byte writeAccess =  getFileWriteAccess(fileId);

        if (writeAccess == (byte) 0x00) {
            writeAccessStatus = NOT_LOCKED;
        } else if (writeAccess == (byte) 0xFF) {
            writeAccessStatus = NOT_AUTHORIZED;
        } else {
            writeAccessStatus = STATUS_UNKNOWN;
        }

        return writeAccessStatus;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This function will do the following actions in an ATOMIC manner:
     * - select the NDEF file
     * - write a NDEF message into the file
     *
     * @param msg : NDEF message to write.
     * @throws STException
     */
    @Override
    public void writeNdefMessage(NDEFMsg msg) throws STException {
        int ndefFileId = mCCFile.getNdefFileId();
        writeNdefMessage(ndefFileId, msg);
    }

    /**
     * This function will do the following actions in an ATOMIC manner:
     * - select the indicated file
     * - write a NDEF message into the file
     *
     * @param fileId : File identifier.
     * @param msg    : NDEF message to write.
     * @throws STException
     */
    public void writeNdefMessage(int fileId, NDEFMsg msg) throws STException {
        int ndefLength;
        try {
            ndefLength = msg.getLength();
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(STException.STExceptionCode.INVALID_NDEF_DATA);
        }

        /* Check NDEF file max size is big enough */
        if(getCCFileTlv(fileId).getMaxFileSize() < ndefLength)
        {
            throw new STException(STExceptionCode.NDEF_MESSAGE_TOO_BIG);
        }

        NdefFileType4 ndefFileType4 = mNdefFileList.get(fileId);
        if (ndefFileType4 != null) {
            mCache.remove(ndefFileType4);
            ndefFileType4.write(msg);
            mCache.add(ndefFileType4);
            return;
        }

        // File is not in the cache so creates a new one
        ndefFileType4 = new NdefFileType4(this, fileId);
        ndefFileType4.write(msg);
        mCache.add(ndefFileType4);
    }



    /**
     * This function writes a NDEF message into the NDEF file. It can be used when the caller wants
     * to handle the file selection himself.
     *
     * PREREQUISITE: The caller is responsible for selecting the NDEF file before calling
     *               this function.
     *
     * @param fileId: fileId selected by the application
     * @param msg : NDEF message to write.
     * @throws STException
     */
    public void _writeNdefMessage(int fileId, NDEFMsg msg) throws STException {
        int ndefLength;
        try {
            ndefLength = msg.getLength();
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(STException.STExceptionCode.INVALID_NDEF_DATA);
        }

        //At this stage the select is done by the application
        //That is the main difference with the writeNdefMessage
        //the writeMsg method from ndefFileType4 needs to have
        //the select commands passed before

        /* Check NDEF file max size is big enough */
        if(getCCFileTlv(fileId).getMaxFileSize() < ndefLength)
        {
            throw new STException(STExceptionCode.NDEF_MESSAGE_TOO_BIG);
        }

        NdefFileType4 ndefFileType4 = mNdefFileList.get(fileId);
        if (ndefFileType4 != null) {
            mCache.remove(ndefFileType4);
            ndefFileType4.writeMsg(msg);
            mCache.add(ndefFileType4);
            return;
        }

        ndefFileType4 = new NdefFileType4(this, fileId);
        ndefFileType4.writeMsg(msg);
        mCache.add(ndefFileType4);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This function will do the following actions in an ATOMIC manner:
     * - select the NDEF file
     * - Read the NDEF message from the file
     *
     * @return
     * @throws STException
     */
    @Override
    public NDEFMsg readNdefMessage() throws STException {
        int ndefFileId = mCCFile.getNdefFileId();
        return readNdefMessage(ndefFileId);
    }

    /**
     * This function will do the following actions in an ATOMIC manner:
     * - select the indicated file
     * - read the NDEF message
     *
     * @param fileId : File identifier.
     * @return
     * @throws STException
     */
    public NDEFMsg readNdefMessage(int fileId) throws STException {

        NDEFMsg ndefMsg;
        NdefFileType4 ndefFileType4 = getNdefFileCached(fileId);

        if (mCache.isCacheActivated() && mCache.isCacheValid(ndefFileType4)) {
            //remove from cache is a precaution as if it is in the cache no error should happen...
            mCache.remove(ndefFileType4);
            ndefMsg = ndefFileType4.read();
            mCache.add(ndefFileType4);
            return ndefMsg;
        }

        //File is not in the cache or is not valid so remove it
        mCache.remove(ndefFileType4);
        ndefFileType4 = new NdefFileType4(this, fileId);
        ndefMsg = ndefFileType4.read();
        mNdefFileList.put(fileId, ndefFileType4);
        mCache.add(ndefFileType4);
        return ndefMsg;
    }

    /**
     * This function read a NDEF message from the NDEF file. It can be used when the caller wants
     * to handle the file selection himself.
     *
     * PREREQUISITE: The caller is responsible for selecting the NDEF file before calling
     *               this function.
     *
     * @param fileId file selected by the application
     * @throws STException
     */
    public NDEFMsg _readNdefMessage(int fileId) throws STException {
        //At this stage the select is done by the application
        //That is the main difference with the readNdefMessage
        //the readMsg method from ndefFileType4 needs to have
        // the select commands passed before

        NDEFMsg ndefMsg;
        NdefFileType4 ndefFileType4 = getNdefFileCached(fileId);

        if (ndefFileType4 != null) {
            //remove from cache is a precaution as if it is in the cache no error should happen...
            mCache.remove(ndefFileType4);
            ndefMsg = ndefFileType4.readMsg();
            mCache.add(ndefFileType4);
            return ndefMsg;
        }


        ndefFileType4 = new NdefFileType4(this, fileId);
        ndefMsg = ndefFileType4.readMsg();
        mNdefFileList.put(fileId, ndefFileType4);
        mCache.add(ndefFileType4);
        return ndefMsg;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This function will do the following actions in an ATOMIC manner:
     * - select the NDEF file
     * - read the requested bytes
     *
     * @param offsetInBytes : Offset in Bytes from the beginning of the file.
     * @param sizeInBytes   : Number of Bytes to read.
     * @return
     * @throws STException
     */
    @Override
    public byte[] readBytes(int offsetInBytes, int sizeInBytes) throws STException {
        synchronized (Type4Command.mLock) {
            int ndefFileId = mCCFile.getNdefFileId();
            return readBytes(ndefFileId, offsetInBytes, sizeInBytes);
        }

    }

    /**
     * This function will do the following actions in an ATOMIC manner:
     * - select the indicated file
     * - read the requested bytes
     *
     * @param fileId : File identifier.
     * @param offsetInBytes : Offset in Bytes from the beginning of the file.
     * @param sizeInBytes   : Number of Bytes to read.
     * @return
     * @throws STException
     */
    public byte[] readBytes(int fileId, int offsetInBytes, int sizeInBytes) throws STException {
        return new FileType4(mType4Cmd, fileId).read(offsetInBytes, sizeInBytes);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This function will do the following actions in an ATOMIC manner:
     * - select the NDEF file
     * - write the requested bytes
     *
     * @param offsetInBytes : Offset in Bytes from the beginning of the file.
     * @param data          : Data to write
     * @throws STException
     */
    @Override
    public void writeBytes(int offsetInBytes, byte[] data) throws STException {
        synchronized (Type4Command.mLock) {
            int ndefFileId = mCCFile.getNdefFileId();
            writeBytes(ndefFileId, offsetInBytes, data);
        }
    }

    /**
     * This function will do the following actions in an ATOMIC manner:
     * - select the indicated file
     * - write the requested bytes
     *
     * WARNING: After this write, the cache may not be aligned anymore with file's content so
     *          it is recommended to invalidate the cache.
     *
     * @param fileId        : File identifier.
     * @param offsetInBytes : Offset in Bytes from the beginning of the file.
     * @param data          : Data to write
     * @throws STException
     */
    public void writeBytes(int fileId, int offsetInBytes, byte[] data) throws STException {
        new FileType4(mType4Cmd, fileId).write(offsetInBytes, data);
    }

    protected NdefFileType4 getNdefFileCached(int fileId) {
        NdefFileType4 ndefFileType4 = mNdefFileList.get(fileId);
        if (ndefFileType4 != null && mCache.contains(ndefFileType4)) {
            return ndefFileType4;
        }

        return null;
    }
}

