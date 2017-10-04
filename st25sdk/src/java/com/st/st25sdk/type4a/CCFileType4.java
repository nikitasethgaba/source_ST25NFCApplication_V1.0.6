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

import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;
import static com.st.st25sdk.STException.STExceptionCode.INVALID_CCFILE;
import static com.st.st25sdk.type4a.Type4Tag.TYPE4_CC_FILE_IDENTIFIER;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.st.st25sdk.CacheInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Type4Command;

public class CCFileType4 extends FileType4 implements CacheInterface {

    /**
     * Cache management
     */
    protected int mLength;
    protected byte mMappingVersion;
    protected int mMaxReadSize;
    protected int mMaxWriteSize;
    protected List<ControlTlv> mTlv;

    protected byte[] mBuffer;
    protected boolean mCacheActivated;
    protected boolean mCacheInvalidated;

    /** */


    protected  void initEmptyCCfile(int memSize) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(0x0F);
        mLength = 0x0F;
        byteBuffer.put((byte) 0x00);
        byteBuffer.put((byte) 0x0F);

        mMappingVersion = (byte) 0x20;
        byteBuffer.put((byte) 0x20);

        mMaxReadSize = 246;
        mMaxWriteSize = 246;
        byteBuffer.put((byte) 0x00);
        byteBuffer.put((byte) 0xF6);
        byteBuffer.put((byte) 0X00);
        byteBuffer.put((byte) 0xF6);



        byte[] tlv = new byte[8];
        tlv[0] = 0x04; //Type defined by type 4
        tlv[1] = 0x06; //Length defined by Type 4
        tlv[2] = 0x00; //FileId
        tlv[3] = 0x01; //FileId
        tlv[4] = (byte) ((memSize / 8) & 0xFF00); //max ndef file size
        tlv[5] = (byte) ((memSize / 8) & 0xFF); //max ndef file size
        tlv[6] = 0x00; //read access
        tlv[7] = 0x00; //write access

        byteBuffer.put(tlv, 0, tlv.length);
        mBuffer = byteBuffer.array();
    }

    public CCFileType4(Type4Command type4Command) {
        super(type4Command, TYPE4_CC_FILE_IDENTIFIER);
        mCacheActivated = true;
        mCacheInvalidated = true;
    }

    public int readLength() throws STException {

        if (!mCacheActivated || mCacheInvalidated) {
            mLength = 0;

            byte[] buffer;
            byte length = (byte) 0x02;


            synchronized (Type4Command.mLock) {
                select();
                buffer = mType4Command.readBinary((byte) 0x00, (byte) 0x00, length);
            }

            if (buffer[0] == (byte) 0x00)
                mLength = ((buffer[0] & 0xFF) << 8) + (buffer[1] & 0xFF);

        }
        return mLength;
    }


    public byte[] read() throws STException {

        if (!mCacheActivated || mCacheInvalidated) {

            byte[] buffer = null;

            try {
                int sizeInBytes;
                synchronized (Type4Command.mLock) {
                    sizeInBytes = readLength();
                    if (sizeInBytes > 0)
                        buffer = mType4Command.readData(0, sizeInBytes);
                }

                if (buffer != null)
                    return Arrays.copyOfRange(buffer, 0, buffer.length);
                else
                    return null;

            } catch (Exception e) {
                e.printStackTrace();
                throw new STException(CMD_FAILED);
            }
        }
        return mBuffer;
    }

    protected void parseCCFile(byte[] buffer) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        mLength = ((byteBuffer.get() & 0xFF) << 8);
        mLength +=  (byteBuffer.get() & 0xFF);
        mMappingVersion = (byte) (byteBuffer.get() & 0xF0);
        mMaxReadSize = ((byteBuffer.get() & 0xFF) << 8);
        mMaxReadSize +=  (byteBuffer.get() & 0xFF);
        mMaxWriteSize = ((byteBuffer.get() & 0xFF) << 8);
        mMaxWriteSize +=  (byteBuffer.get() & 0xFF);

        if (mTlv == null) {
            mTlv = new ArrayList<>();
        }
        mTlv.clear();

        byte[] tlv = new byte[8];

        while (byteBuffer.limit() - byteBuffer.position() > 7) {
            byteBuffer.get(tlv, 0, 8);
            mTlv.add(ControlTlv.newInstance(tlv));
        }

        mBuffer = byteBuffer.array();
        mLength = mBuffer.length;
    }



    public byte getCCMappingVersion() throws STException {
        checkCache();
        return mMappingVersion;
    }

    public int getMaxReadSize() throws STException {
        checkCache();
        return mMaxReadSize;
    }

    public int getMaxWriteSize() throws STException {
        checkCache();
        return mMaxWriteSize;
    }

    /**
     * Get a TLV block based on the position of this block in the CC File
     * @param pos : requested TLV block. pos is in the range 0 to N-1 (where N is the number of TLV blocks)
     * @return
     * @throws STException
     */
    public ControlTlv getTlv(int pos) throws STException {
        checkCache();

        // mTlv is now filled
        // Check that the requested item exists
        if (pos >= mTlv.size()) {
            throw new STException(BAD_PARAMETER);
        }

        return mTlv.get(pos);

    }

    /**
     * Get a TLV block based on the FileId
     * @param fileId
     * @return
     * @throws STException
     */
    public ControlTlv getFileTlv(int fileId) throws STException {
        int nbrOfTlv = getNbOfTlv();

        // Go through all the TLV blocks and look for one with the requested fileId
        for (int i=0; i<nbrOfTlv; i++){
            ControlTlv fileTlv = mTlv.get(i);
            if (fileTlv.getFileId() == fileId) {
                return fileTlv;
            }
        }

        // The fileId was not found in the TLV blocks
        throw new STException(INVALID_CCFILE);
    }


    public int getNbOfTlv() throws STException {
        checkCache();
        return mTlv.size();
    }

    /**
     * Return the FileId of the file referenced in the first TLV block of the CC File.
     * This NDEF file is mandatory in a Type4 tag.
     * @return
     * @throws STException
     */
    public int getNdefFileId() throws STException {
        checkCache();

        if (mTlv.size() <= 0) {
            // TLV block should contain at least one block!
            throw new STException(INVALID_CCFILE);
        }

        return getTlv(0).getFileId();
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
    public void updateCache() throws STException{
        if (isCacheActivated()) {
            invalidateCache();
            byte[] buffer = read();
            if (buffer != null) {
                parseCCFile(buffer);
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

    private void checkCache() throws STException {
        if (!isCacheActivated()) {
            byte[] buffer = read();
            parseCCFile(buffer);
        } else if (!isCacheValid()) updateCache();
    }

}
