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


import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STException.STExceptionCode;
import com.st.st25sdk.STLog;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.ndef.NDEFMsg;

import java.util.ArrayList;
import java.util.List;

import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.INVALID_CCFILE;


public abstract class STType5MultiAreaTag extends STType5Tag implements MultiAreaInterface, STType5PasswordInterface {

    protected List<STAreaContent> mAreaList;

    public  STType5MultiAreaTag(RFReaderInterface readerInterface, byte[] uid) {
        super(readerInterface, uid);
        mName = "ST Type 5 tag";
        mAreaList = new ArrayList<>();
    }

    /**
     * Init "mArea" list that can be used to read or write an area.
     */
    protected void initAreaList() throws STException {

        for (STAreaContent it: mAreaList) {
            mCache.remove(it);
        }
        mAreaList.clear();

        // Create as many (CCFile + NdefType5Container) as areas on this tag.
        int maxNumberOfAreas = getNumberOfAreas();

        for(int area = AREA1 ; area <= maxNumberOfAreas; area++) {
            // AreaContent is just a holder to contain a CCFile + a NdefCmd
            STAreaContent stAreaContent = null;

            try {
                stAreaContent = new STAreaContent(this, getAreaOffsetInBytes(area), getAreaSizeInBytes(area));
            } catch (STException e) {
                STLog.e("Failed to initialize Area " + area);
            }

            // Add this element to the list (even if it doesn't contain yet a valid CCFile + NDEF data)
            if (stAreaContent != null) {
                mAreaList.add(stAreaContent);
                mCache.add(stAreaContent);
            }
        }
    }


    ////////////////////////////  RawData functions  ////////////////////////////////////

    @Override
    public int getMaxNumberOfAreas() {
        return 0;
    }

    @Override
    public int getNumberOfAreas() throws STException {
        throw new STException(STExceptionCode.NOT_IMPLEMENTED);
    }

    @Override
    public void setNumberOfAreas(int nbOfAreas) throws STException {
        throw new STException(STExceptionCode.NOT_IMPLEMENTED);
    }

    @Override
    public int getAreaSizeInBytes(int area) throws STException {
        throw new STException(STExceptionCode.NOT_IMPLEMENTED);
    }

    @Override
    public int getAreaOffsetInBytes(int area) throws STException {
        throw new STException(STExceptionCode.NOT_IMPLEMENTED);
    }


    @Override
    public int getAreaOffsetInBlocks(int area) throws STException {
        throw new STException(STExceptionCode.NOT_IMPLEMENTED);
    }

    @Override
    public int getAreaFromBlockAddress(int blockOffset) throws STException {
        throw new STException(STExceptionCode.NOT_IMPLEMENTED);
    }

    /**
     * Read a number of Bytes at a given offset of an area.
     * @param area
     * @param offsetInBytes
     * @param sizeInBytes
     * @return
     * @throws STException
     */
    public byte[] readBytes(int area, int offsetInBytes, int sizeInBytes) throws STException {


        if (area > getNumberOfAreas()) {
            throw new STException(BAD_PARAMETER);
        }

        if ((offsetInBytes + sizeInBytes) > getAreaSizeInBytes(area)) {
            throw new STException(BAD_PARAMETER);
        }

        int byteAddress = getAreaOffsetInBytes(area) + offsetInBytes;

        return readBytes(byteAddress, sizeInBytes);
    }

    /**
     * Write some Bytes at a given offset of an area.
     * @param area
     * @param offsetInBytes
     * @param data
     * @throws STException
     */
    public void writeBytes(int area, int offsetInBytes, byte[] data) throws STException {

        if (area > getNumberOfAreas()) {
            throw new STException(BAD_PARAMETER);
        }

        if ((data.length + offsetInBytes) > getAreaSizeInBytes(area)) {
            throw new STException(BAD_PARAMETER);
        }

        int byteAddress = getAreaOffsetInBytes(area) + offsetInBytes;
        writeBytes(byteAddress, data);
    }

    /////////////////////////////  NDEF management  ///////////////////////////////

    @Override
    public NDEFMsg readNdefMessage(int area) throws STException {
        NDEFMsg ndefMsg = null;

        // The first area is managed by the standard readNdefMessage() function and not by Type5MultiAreaContainer
        if (area == AREA1) {
            return readNdefMessage();
        }

        if (area > getNumberOfAreas()) {
            throw new STException(BAD_PARAMETER);
        }

        // Warning: Items of mAreaList are in the range 0 to N-1 whereas area is in the range 1 to N
        STAreaContent areaContent = mAreaList.get(area-1);
        if (areaContent != null) {
            ndefMsg = areaContent.readNdefMessage();
        }

        return ndefMsg;
    }

    @Override
    public void writeNdefMessage(int area, NDEFMsg msg) throws STException {
        // The first area is managed by the standard writeNdefMessage() function and not by Type5MultiAreaContainer
        if (area == AREA1) {
            writeNdefMessage(msg);
            return;
        }

        if (area > getNumberOfAreas()) {
            throw new STException(BAD_PARAMETER);
        }

        // Warning: Items of mAreaList are in the range 0 to N-1 whereas area is in the range 1 to N
        STAreaContent areaContent = mAreaList.get(area-1);
        areaContent.writeNdefMessage(msg);
    }

    @Override
    public void writeNdefMessage(NDEFMsg msg, byte flag) throws STException {
        // Length of the bytes containing the "type" and "length" of the TLV block containing the NDEF
        int tlSize = 2;
        int terminatorTlvLength = 1;
        int ccfileLength;

        // In case of MultiArea tag, we should prevent this writeNdefMessage() command to overwrite the next Area
        // NB: This code is executed only for AREA1. Other Areas go through areaContent.
        int areaSize = getAreaSizeInBytes(AREA1);

        try {
            ccfileLength = getCCFileLength();
        } catch (STException e) {
            if (e.getError().equals(INVALID_CCFILE)) {
                initEmptyCCFile();
                if (mNdefMsg != null)
                {
                    mCache.remove(mNdefMsg);
                }
                writeCCFile();
                ccfileLength = getCCFileLength();
            } else
                throw e;
        }

        if (ccfileLength != 0) {
            int ndefLength;
            try {
                ndefLength = msg.getLength();
            } catch (Exception e) {
                e.printStackTrace();
                throw new STException(STExceptionCode.INVALID_NDEF_DATA);
            }

            if ((ccfileLength + tlSize + ndefLength + terminatorTlvLength) <= areaSize)
            {
                if (mNdefMsg != null)
                {
                    mCache.remove(mNdefMsg);
                }
                mNdefCmd.writeNdefMessage((byte) ccfileLength / getBlockSizeInBytes(), msg, flag, mUid);
            } else {
                throw new STException(STException.STExceptionCode.NDEF_MESSAGE_TOO_BIG);
            }
        } else {
            throw new STException(STException.STExceptionCode.INVALID_CCFILE);
        }

        mNdefMsg = msg.copy();
        mCache.add(mNdefMsg);
    }

    @Override
    public abstract byte getPasswordNumber(int area) throws STException;

    @Override
    public abstract void setPasswordNumber(int area, byte passwordNumber) throws STException;

    @Override
    public abstract TagHelper.ReadWriteProtection getReadWriteProtection(int area) throws STException;

    @Override
    public abstract void setReadWriteProtection(int area, TagHelper.ReadWriteProtection protection) throws STException;

    @Override
    public abstract void setReadWriteProtection(int area, TagHelper.ReadWriteProtection protection, byte[] password) throws STException;

    @Override
    public abstract void presentPassword(byte passwordNumber, byte[] password) throws STException;

    @Override
    public abstract void writePassword(byte passwordNumber, byte[] newPassword) throws STException;

    @Override
    public abstract PasswordLength getPasswordLength(byte passwordNumber) throws STException;

    @Override
    public abstract PasswordLength getAreaPasswordLength(int area) throws STException;

    @Override
    public abstract byte getConfigurationPasswordNumber() throws STException;

}
