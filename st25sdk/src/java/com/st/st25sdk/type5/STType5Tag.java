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


import com.st.st25sdk.CCFileInterface;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.SysFileInterface;
import com.st.st25sdk.command.Iso15693CustomCommand;
import com.st.st25sdk.command.NdefCommandInterface;


public class STType5Tag extends Type5Tag implements CCFileInterface, SysFileInterface, NdefCommandInterface {

    protected SysFile mSysFile;
    protected Iso15693CustomCommand mIso15693CustomCommand;

    public STType5Tag(RFReaderInterface readerInterface, byte[] uid) {
        super(readerInterface, uid);

        mName = "Iso15693/NFC Type 5 Tag";

        mIso15693CustomCommand = new Iso15693CustomCommand(readerInterface, uid);
        mSysFile = new SysFileType5(mIso15693CustomCommand);
        mCache.add(mSysFile);
    }

    @Override
    public byte select() throws STException {
        return select(mType5Cmd.getFlag());
    }
    @Override
    public byte select(byte flag) throws STException {
        return mType5Cmd.select(flag, getUid());
    }

    @Override
    public int getSysFileLength() throws STException {
        return mSysFile.getSysLength();
    }

    @Override
    public byte getDSFID() throws STException {
        return mSysFile.getDSFID();
    }

    public byte[] getVICCCommandList() throws STException {
        byte[] viccCmdListSupported;

        if (mSysFile instanceof SysFileType5) {
            viccCmdListSupported = ((SysFileType5) mSysFile).getVICCCommandList();
        } else {
            viccCmdListSupported = new byte[0];
        }

        return viccCmdListSupported;
    }

    public boolean isVICCCommandListSupported() {
        boolean isViccCmdListSupported = false;

        if (mSysFile instanceof SysFileType5) {
            isViccCmdListSupported = ((SysFileType5) mSysFile).mVICCCommandListSupported;
        }

        return isViccCmdListSupported;
    }

    @Override
    public byte getAFI() throws STException {
        return mSysFile.getAFI();
    }


    @Override
    public byte getICRef() throws STException {
        return mSysFile.getICRef();
    }

    @Override
    public int getNumberOfBlocks() throws STException {
        return (mSysFile.getNumberOfBlocks() + 1);
    }

    @Override
    public int getMemSizeInBytes() throws STException {
        return getNumberOfBlocks() * getBlockSizeInBytes();
    }

    //////////////////////////// Non Type5 Commands ////////////////////////////

    public byte[] extendedGetSystemInfo() throws STException {
        return extendedGetSystemInfo((byte) 0x7F, mIso15693Cmd.getFlag());
    }
    public byte[] extendedGetSystemInfo(byte parameter, byte flag) throws STException {
        return mIso15693Cmd.extendedGetSystemInfo(parameter, flag, getUid());
    }

    public byte resetToReady() throws STException {
        return resetToReady(mIso15693Cmd.getFlag());
    }
    public byte resetToReady(byte flag) throws STException {
        return mIso15693Cmd.resetToReady(flag, getUid());
    }

    public byte writeAFI(byte value) throws STException {
        return writeAFI(value, mIso15693Cmd.getFlag());
    }
    public byte writeAFI(byte value, byte flag) throws STException {
        byte result = mIso15693Cmd.writeAFI(value, flag, getUid());

        // AFI value has been changed successfully. Update the info contained in the sysFile
        mSysFile.setAFI(value);

        return result;
    }

    public byte lockAFI() throws STException {
        return lockAFI(mIso15693Cmd.getFlag());
    }
    public byte lockAFI(byte flag) throws STException {
        return mIso15693Cmd.lockAFI(flag, getUid());
    }

    public byte writeDSFID(byte value) throws STException {
        return writeDSFID(value, mIso15693Cmd.getFlag());
    }
    public byte writeDSFID(byte value, byte flag) throws STException {
        byte result = mIso15693Cmd.writeDSFID(value, flag, getUid());

        // DSFID value has been changed successfully. Update the info contained in the sysFile
        mSysFile.setDSFID(value);

        return result;
    }

    public byte lockDSFID() throws STException {
        return lockDSFID(mIso15693Cmd.getFlag());
    }
    public byte lockDSFID(byte flag) throws STException {
        return mIso15693Cmd.lockDSFID(flag, getUid());
    }

    @Override
    public byte[] readSysFile() throws STException {
        return getSystemInfo();
    }
    public byte[] readSysFile(byte flag) throws STException {
        return getSystemInfo(flag);
    }

    public byte[] getSystemInfo() throws STException {
        return getSystemInfo(mIso15693Cmd.getFlag());
    }
    public byte[] getSystemInfo(byte flag) throws STException {
        return mIso15693Cmd.getSystemInfo(flag, getUid());
    }

    public byte[] getMultipleBlockSecurityStatus(byte blockAddress, byte numberOfBlocks) throws STException {
        return getMultipleBlockSecurityStatus(blockAddress, numberOfBlocks, mIso15693Cmd.getFlag());
    }
    public byte[] getMultipleBlockSecurityStatus(byte blockAddress, byte numberOfBlocks, byte flag) throws STException {
        return mIso15693Cmd.getMultipleBlockSecStatus(blockAddress, numberOfBlocks, flag, getUid());
    }

    /**
     * Reads back a single block of data from a tag.
     * The data rate of the response is twice as fast as a readSingleBlock command.
     *
     * @param blockAddress Address of the block to read
     * @return
     * @throws STException
     */
    public byte[] fastReadSingleBlock(byte blockAddress) throws STException {
        return fastReadSingleBlock(blockAddress, mIso15693CustomCommand.getFlag());
    }

    /**
     * Reads back a single block of data from a tag.
     * The data rate of the response is twice as fast as a readSingleBlock command.
     *
     * @param blockAddress Address of the block to read
     * @param flag When the Option bit is set, the response includes the Block Security Status
     * @return
     * @throws STException
     */
    public byte[] fastReadSingleBlock(byte blockAddress, byte flag) throws STException {
        return mIso15693CustomCommand.fastReadSingleBlock(blockAddress, flag, getUid());
    }

    /**
     * Reads back a multiple blocks of data from a tag. The number of blocks read is numberOfBlocks + 1.
     * The data rate of the response is twice as fast as a readMultipleBlock command.
     *
     * @param blockAddress Address of the first block to read
     * @param numberOfBlocks Set to target blocks to read minus 1. The tag will actually return numberOfBlocks + 1 blocks.
     * @return
     * @throws STException
     */
    public byte[] fastReadMultipleBlock(byte blockAddress, byte numberOfBlocks) throws STException {
        return fastReadMultipleBlock(blockAddress, numberOfBlocks, mIso15693CustomCommand.getFlag());
    }

    /**
     * Reads back a multiple blocks of data from a tag. The number of blocks read is numberOfBlocks + 1.
     * The data rate of the response is twice as fast as a readMultipleBlock command.
     *
     * @param blockAddress Address of the first block to read
     * @param numberOfBlocks Set to target blocks to read minus 1. The tag will actually return numberOfBlocks + 1 blocks.
     * @param flag When the Option bit is set, the response includes the Block Security Status
     * @return
     * @throws STException
     */
    public byte[] fastReadMultipleBlock(byte blockAddress, byte numberOfBlocks, byte flag) throws STException {
        return mIso15693CustomCommand.fastReadMultipleBlock(blockAddress, numberOfBlocks, flag, getUid());
    }

}
