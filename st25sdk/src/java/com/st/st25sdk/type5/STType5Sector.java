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
import com.st.st25sdk.STException;
import com.st.st25sdk.SectorInterface;
import com.st.st25sdk.command.Iso15693CustomCommand;
import com.st.st25sdk.command.Iso15693Protocol;
import com.st.st25sdk.command.VicinityCommand;

import java.nio.ByteBuffer;

import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;


public class STType5Sector implements CacheInterface, SectorInterface  {

    protected boolean mCacheActivated;
    protected boolean mCacheInvalidated;

    private ByteBuffer mSectorSecStatus = null;

    private Iso15693Protocol mIso15693Command;
    private int mNbOfSectors;
    private int mNbOfBlocksPerSector;


    public STType5Sector(Iso15693Protocol protocol, int nbOfSectors, int nbOfBlocksPerSector) throws STException {
        mCacheActivated = true;
        mCacheInvalidated = true;

        mIso15693Command = protocol;
        mNbOfSectors = nbOfSectors;
        mNbOfBlocksPerSector = nbOfBlocksPerSector;

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
    }

    @Override
    public void deactivateCache() {
        mCacheActivated = false;
    }

    @Override
    public void updateCache() throws STException {
        if (mCacheActivated) {
            read();
            mCacheInvalidated = false;
        }
    }

    public void read() throws STException {
        // No need to read every block to know the Sector Security Status.
        // Reading only one block at the beginning of each sector is enough.

        //Buffer containing response command
        byte[] buf = null;

        // Gets number of Sectors and nbr of blocks per sector.
        // NextSector@ = SectorNb x nbrOfBlockPerSector
        int totalNbOfSectorToRead = getNumberOfSectors();
        int nbrOfBlockPerSector = getNumberOfBlocksPerSector();
        int blocksIndex;

        if (mSectorSecStatus == null)
            mSectorSecStatus = ByteBuffer.allocate(totalNbOfSectorToRead);

        for (int indexSector = 0; indexSector < totalNbOfSectorToRead; indexSector++) {
            blocksIndex = indexSector * nbrOfBlockPerSector;

            if (mIso15693Command instanceof VicinityCommand) {
                buf = ((VicinityCommand) mIso15693Command).getMultipleBlockSecStatus(Helper.convertIntTo2BytesHexaFormat(blocksIndex), Helper.convertIntTo2BytesHexaFormat(0));
            }
            else if (mIso15693Command instanceof Iso15693CustomCommand){
                buf = ((Iso15693CustomCommand) mIso15693Command).getMultipleBlockSecStatus((byte) blocksIndex, (byte) (0x00));
            }

            if (buf != null && (buf[0] == 0x00)) {
                mSectorSecStatus.put(buf, 1, 1);
            }
            else
                throw new STException(CMD_FAILED, buf);
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
    @Override
    public int getNumberOfSectors() {
        return mNbOfSectors;
    }

    @Override
    public int getNumberOfBlocksPerSector() {
        return mNbOfBlocksPerSector;
    }

    @Override
    public byte getSecurityStatus(int sector) throws STException {
        checkCache();
        return mSectorSecStatus.array()[sector];
    }

    @Override
    public byte[] getSecurityStatus() throws STException {
        checkCache();
        return mSectorSecStatus.array();
    }

    @Override
    public void setSecurityStatus(int sector, byte value) throws STException {
        if (mIso15693Command instanceof VicinityCommand) {
            ((VicinityCommand) mIso15693Command).lockSector(Helper.convertIntTo2BytesHexaFormat(sector*mNbOfBlocksPerSector), value);
        }
        else if (mIso15693Command instanceof Iso15693CustomCommand){
            ((Iso15693CustomCommand) mIso15693Command).lockSector((byte) (sector*mNbOfBlocksPerSector), value);
        }
    }

    private void checkCache() throws STException {
        if (!mCacheActivated) {
            read();
        }
        else if (mCacheInvalidated) updateCache();
    }
}
