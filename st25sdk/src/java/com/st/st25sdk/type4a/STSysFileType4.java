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

import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;
import static com.st.st25sdk.type4a.STType4Tag.SYS_FILE_IDENTIFIER;

import java.util.Arrays;

import com.st.st25sdk.CacheInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Type4Command;

public class STSysFileType4 extends  FileType4 implements CacheInterface {


    protected int mLength;
    protected byte mICRef;
    protected byte[] mUid = new byte[7];
    protected int mMemorySizeInBytes;
    protected byte mProductCode;

    protected byte[] mBuffer;
    protected boolean mCacheActivated;
    protected boolean mCacheInvalidated;

    public STSysFileType4(Type4Command type4Command) {
        super(type4Command, SYS_FILE_IDENTIFIER);
        mCacheActivated = true;
        mCacheInvalidated = true;
    }


    public int readLength() throws STException {

        if (!mCacheActivated || mCacheInvalidated) {

            byte[] buffer;
            byte length = (byte) 0x02;

            synchronized (Type4Command.mLock) {
                try {

                    select();
                    buffer = mType4Command.readBinary((byte) 0x00, (byte) 0x00, length);

                    if (buffer[0] == (byte) 0x00)
                        mLength = ((buffer[0] & 0xFF) << 8) + (buffer[1] & 0xFF);
                    else
                        throw new STException("Tag application not found");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new STException(CMD_FAILED);
                }
            }
        }
        return mLength;
    }

    public byte[] read() throws STException {

        if (!mCacheActivated || mCacheInvalidated) {

            byte[] buffer = null;

            synchronized (Type4Command.mLock) {
                try {
                    int sizeInBytes = readLength();

                    if (sizeInBytes > 0)
                        buffer = mType4Command.readData(0, sizeInBytes);

                    if (buffer != null)
                        return Arrays.copyOfRange(buffer, 0, buffer.length);
                    else
                        return null;

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new STException(CMD_FAILED);
                }
            }
        }
        return mBuffer;
    }

    /**
     * Send readSysFile command to the tag
     * @return 1 byte status
     * @throws STException
     */
    public byte[] getData() throws STException {
        checkCache();
        return mBuffer;
    }


    public int getLength() throws STException {
        checkCache();
        return mLength;
    }

    public int getMemSizeInBytes() throws STException {
        checkCache();
        return mMemorySizeInBytes;
    }

    public byte getICRef() throws STException {
        checkCache();
        return mICRef;
    }

    @Override
    public void invalidateCache() {
        mCacheInvalidated = true;
    }

    @Override
    public void validateCache() {
        mCacheInvalidated = false;
    }

    @Override
    public void activateCache() {
        mCacheActivated = true;
        mCacheInvalidated = true;
    }

    @Override
    public void deactivateCache() {
        mCacheActivated = false;
    }

    @Override
    public void updateCache() throws STException {
        if (isCacheActivated()) {
            invalidateCache();
            byte[] buffer = read();
            if (buffer != null) {
                parseSysFile(buffer);
                mCacheInvalidated = false;
            }
        }
    }

    @Override
    public boolean isCacheValid(){
        return !mCacheInvalidated;
    }

    @Override
    public boolean isCacheActivated(){
        return mCacheActivated;
    }

    protected void checkCache() throws STException {
        if (!isCacheActivated()) {
            byte[] buffer = read();
            parseSysFile(buffer);

        } else if (!isCacheValid()) updateCache();
    }

    protected void parseSysFile(byte[] buffer) {}
}
