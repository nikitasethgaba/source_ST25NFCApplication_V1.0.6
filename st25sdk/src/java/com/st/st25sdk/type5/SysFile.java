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
import com.st.st25sdk.Helper;
import com.st.st25sdk.NFCTag.TagAddressingMode;
import com.st.st25sdk.STException;

import java.nio.ByteBuffer;

public abstract class SysFile implements CacheInterface {

    /**
     * Cache management
     */
    protected int mLength;
    protected boolean mDSFIDSupported;
    protected boolean mAFISupported;
    protected boolean mVICCMemSizesupported;
    protected boolean mICRefSupported;

    protected byte mDSFID;
    protected byte mAFI;
    protected byte mICRef;

    protected int mNumberOfBlocks;
    protected int mBlockSize;

    protected byte[] mUid = new byte[8];
    protected TagAddressingMode mAddressingMode;

    protected byte[] mBuffer;
    protected boolean mCacheActivated;
    protected boolean mCacheInvalidated;

    public abstract byte[] read() throws STException;


    public SysFile() {
        mCacheActivated = true;
        mCacheInvalidated = true;
    }


    /**
     * @return the mUid
     */
    public byte[] getUid() throws STException {
        checkCache();
        return mUid;
    }

    public byte getDSFID() throws STException {
        checkCache();
        return mDSFID;
    }

    public void setDSFID(byte dsfid) {
        mDSFID = dsfid;
    }

    public byte getAFI() throws STException {
        checkCache();
        return mAFI;
    }

    public void setAFI(byte afi) {
        mAFI = afi;
    }

    public byte getICRef() throws STException {
        checkCache();
        return mICRef;
    }

    public int getNumberOfBlocks() throws STException {
        checkCache();
        return mNumberOfBlocks;
    }

    public int getBlockSizeInBytes() throws STException {
        checkCache();
        return mBlockSize;
    }

    public int getSysLength() throws STException {
        checkCache();
        return mLength;
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
        if (mCacheActivated) {
            invalidateCache();
            byte[] buffer = read();
            parseSysFile(buffer);
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

    protected void checkCache() throws STException {
        if (!isCacheActivated()) {
            byte[] buffer = read();
            parseSysFile(buffer);
        }
        else if (!isCacheValid()) updateCache();
    }

    protected static final byte DSFID_MASK = 0x01;
    protected static final byte AFI_MASK = 0x02;
    protected static final byte VICC_MEM_SIZE_MASK = 0x04;
    protected static final byte ICREF_MASK = 0x08;


    protected void parseSysFile(byte[] buffer) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byte sysInfoByte = byteBuffer.get();

        byteBuffer.get(mUid, 0, 8);

        // Put UID in correct order
        mUid = Helper.reverseByteArray(mUid);

        if ((sysInfoByte & DSFID_MASK) == DSFID_MASK) {
            mDSFIDSupported = true;
            mDSFID = byteBuffer.get();
        } else
            mDSFIDSupported = false;

        if ((sysInfoByte & AFI_MASK) == AFI_MASK) {
            mAFISupported = true;
            mAFI = byteBuffer.get();
        } else
            mAFISupported = false;

        if ((sysInfoByte & VICC_MEM_SIZE_MASK) == VICC_MEM_SIZE_MASK) {
            mVICCMemSizesupported = true;
            mNumberOfBlocks = (byteBuffer.get() & 0xFF);
            mBlockSize = (byteBuffer.get() & 0x1F) + 1;
        } else
            mVICCMemSizesupported = false;

        if ((sysInfoByte & ICREF_MASK) == ICREF_MASK) {
            mICRefSupported = true;
            mICRef = byteBuffer.get();
        } else
            mICRefSupported = false;

        mBuffer = byteBuffer.array();
        mLength = mBuffer.length;
    }
}
