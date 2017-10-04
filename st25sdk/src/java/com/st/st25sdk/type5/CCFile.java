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

import com.st.st25sdk.CacheInterface;
import com.st.st25sdk.STException;

import static com.st.st25sdk.STException.STExceptionCode.INVALID_CCFILE;

public abstract class CCFile implements CacheInterface {
    /**
     * Cache management
     */
    protected int mCCLength;
    protected byte mMagicNumber;
    protected byte mMappingVersion;
    protected byte mReadAccess;
    protected byte mWriteAccess;
    protected int mDataAreaSize;
    protected int mBlockSize;
    protected boolean mSupports2ByteAddrMode;

    protected byte[] mBuffer;
    protected boolean mCacheActivated;
    protected boolean mCacheInvalidated;

    static public final byte CCFILE_SHORT_IDENTIFIER = (byte) 0xE1;
    static public final byte CCFILE_LONG_IDENTIFIER = (byte) 0xE2;
    static public final int CCFILE_DATA_AREA_SIZE_MULTIPLIER = 8;

    /** */

    public abstract byte[] read() throws STException;

    public abstract void write() throws STException;

    protected void initEmptyCCFile(int memSizeInBytes){
        // If number of (bytes/8) > 256, code CC File on 2 bytes
        if (memSizeInBytes >= 2048) {
            mBuffer = new byte[8];
            mBuffer[0] = CCFILE_LONG_IDENTIFIER;
            mBuffer[2] = 0x00;
            mBuffer[3] = (byte) 0x05; //Read multiple blocks mask (0x01) & rfu mask (0x04)
            mBuffer[4] = 0x00;//RFU
            mBuffer[5] = 0x00;//RFU
            mBuffer[6] = (byte) (((memSizeInBytes / CCFILE_DATA_AREA_SIZE_MULTIPLIER) & 0xFF00) >> 8);
            mBuffer[7] = (byte) ((memSizeInBytes / CCFILE_DATA_AREA_SIZE_MULTIPLIER) & 0xFF);

        }
        else {
            mBuffer = new byte[4];
            mBuffer[0] = CCFILE_SHORT_IDENTIFIER;
            mBuffer[2] = (byte) (memSizeInBytes / CCFILE_DATA_AREA_SIZE_MULTIPLIER);
            mBuffer[3] = (byte) 0x05; //Read multiple blocks mask (0x01) and RFU mask (0x04) for android native support...
        }

        mBuffer[1] = mMappingVersion = (byte) 0x40;

        mCCLength = mBuffer.length;
        mReadAccess = 0x00;
        mWriteAccess = 0x00;
        mDataAreaSize = memSizeInBytes;
    }

    public CCFile() {
        mCacheActivated = true;
        mCacheInvalidated = true;
    }

    /**
     * This function will indicate the expected CCFile length (in Bytes) for a given memory size
     * @param dataAreaSizeInBytes
     */
    public static int getExpectedCCFileLength(int dataAreaSizeInBytes) {
        // A CCFile on 4 bytes can manage tags with up to 256 * 8 = 2048 Bytes of memory
        if (dataAreaSizeInBytes >= 2048) {
            // CCFile on 8 Bytes
            return 8;
        } else {
            // CCFile on 4 Bytes
            return 4;
        }
    }

    /**
     * Returns info on CC length (4 or 8 bytes)
     * @return the CCLength
     */
    public int getCCLength() throws STException {
        checkCache();
        return mCCLength;
    }


    public byte getMagicNumber() throws STException {
        checkCache();
        return mMagicNumber;
    }


    public byte getCCMappingVersion() throws STException {
        checkCache();
        return mMappingVersion;
    }


    public byte getCCReadAccess() throws STException {
        checkCache();
        return mReadAccess;
    }


    public byte getCCWriteAccess() throws STException {
        checkCache();
        return mWriteAccess;
    }


    public int getDataAreaSize() throws STException {
        checkCache();
        return mDataAreaSize;
    }

    public int getBlockSize() throws STException {
        checkCache();
        return mBlockSize;
    }


    //Cache management

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
    public void updateCache() throws  STException {
        if (isCacheActivated()) {
            // Read CCFile (if the read succeed, the CCFile will be parsed and the cache will be updated)
            invalidateCache();
            byte[] buffer = read();
            parseCCFile(buffer);
            mCacheInvalidated = false;
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

    public void parseCCFile(byte[] buffer) throws STException {

        if(buffer.length < 3) {
            throw new STException(INVALID_CCFILE);
        }

        mMagicNumber = buffer[0];
        mSupports2ByteAddrMode = (buffer[0] == CCFILE_LONG_IDENTIFIER);

        mMappingVersion = (byte) (buffer[1] & 0xF0);
        mReadAccess = (byte) (buffer[1] & 0x0C);
        mWriteAccess = (byte) (buffer[1] & 0x03);

        if (buffer[2] == 0x00) {
            // 8-byte CC, extract data area size from bytes 6 and 7

            if(buffer.length < 8) {
                throw new STException(INVALID_CCFILE);
            }

            mCCLength = 8;
            mDataAreaSize = CCFILE_DATA_AREA_SIZE_MULTIPLIER * (((buffer[6] & 0xFF) << 8) + (buffer[7] & 0xFF));

        } else {
            // 4-byte CC, extract data area size from byte 2
            mCCLength = 4;
            mDataAreaSize = CCFILE_DATA_AREA_SIZE_MULTIPLIER * (buffer[2] & 0xFF);
        }

        if (buffer != mBuffer)
            mBuffer = buffer;
    }

    /**
     * Return true if the cache memory has to be updated
     * If the cache is deactivated, the update is always done
     * @return true if memory object has to be updated
     */


    private void checkCache() throws STException {
        if (!isCacheActivated()) {
            byte[] buffer = read();
            parseCCFile(buffer);
        }
        else if (!isCacheValid()) updateCache();
    }

}
