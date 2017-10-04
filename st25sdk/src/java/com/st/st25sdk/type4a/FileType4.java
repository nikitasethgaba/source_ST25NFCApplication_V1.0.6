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
import com.st.st25sdk.command.Type4Command;

public class FileType4 {

    protected int mFileId;
    protected int mFileSize; //2 bytes
    protected byte mReadAccess; //1 byte
    protected byte mWriteAccess; //1 byte

    protected Type4Command mType4Command;



    public FileType4(Type4Command type4Command, int fileId) {
        mType4Command = type4Command;
        mFileId = fileId;
    }

    public FileType4(Type4Command type4Command, int fileId, int size, byte readAccess, byte writeAccess) {
        mType4Command = type4Command;
        mFileId = fileId;

        mFileSize = size;
        mReadAccess = readAccess;
        mWriteAccess = writeAccess;
    }


    public int getFileId() { return mFileId;}

    public byte getReadAccess() { return mReadAccess;}

    public byte getWriteAccess() { return mWriteAccess;}

    public int getSize() { return mFileSize;}

    /**
     * This function writes some bytes into the NDEF file. It can be used when the caller wants
     * to handle the file selection himself.
     *
     * PREREQUISITE: The caller is responsible for selecting the NDEF file before calling
     *               this function.
     *
     * @param offsetInBytes : Offset in Bytes from the beginning of the file.
     * @param data          : Data to write
     * @throws STException
     */
    public void write(int offsetInBytes, byte[] data) throws STException {
        synchronized (Type4Command.mLock) {
            mType4Command.selectFile(mFileId);
            mType4Command.writeData(offsetInBytes, data);
        }
    }

    /**
     * Read the data in currently selected file (at specified fileOffset)
     * @param fileOffset : Offset in currently selected file
     * @param sizeInBytes :  Amount of data to read
     * @return
     * @throws STException
     */
    public byte[] read(int fileOffset, int sizeInBytes) throws STException {
        byte[] response;

        synchronized (Type4Command.mLock) {
            mType4Command.selectFile(mFileId);
            response = mType4Command.readData(fileOffset, sizeInBytes);
        }

        return response;
    }


    /**
     * Doing in a raw select application + selectFile.
     * @return
     * @throws STException
     */
    public byte[] select()  throws STException {
        synchronized (Type4Command.mLock) {

            mType4Command.selectNdefTagApplication();
            return mType4Command.selectFile(mFileId);
        }

    }

    /**
     * Just doing the select without the select application
     * @return
     * @throws STException
     */
    public byte[] selectFile()  throws STException {
        return mType4Command.selectFile(mFileId);
    }
}
