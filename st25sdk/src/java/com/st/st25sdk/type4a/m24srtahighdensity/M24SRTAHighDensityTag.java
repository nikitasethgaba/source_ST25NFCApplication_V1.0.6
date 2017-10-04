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

package com.st.st25sdk.type4a.m24srtahighdensity;

import static com.st.st25sdk.STException.STExceptionCode.INVALID_NDEF_DATA;

import com.st.st25sdk.CacheInterface;
import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso7816Type4RApduStatus;
import com.st.st25sdk.ndef.EmptyRecord;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.ndef.NDEFRecord;
import com.st.st25sdk.type4a.STType4MultiAreaTag;

public class M24SRTAHighDensityTag extends STType4MultiAreaTag implements CacheInterface, MultiAreaInterface {


    public M24SRTAHighDensityTag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface, uid);
        Iso7816Type4RApduStatus.mIgnoreSw2 = true;
        mSysFile = new SysFileM24SRTAHighDensity(mType4Cmd);
        mTypeDescription = NFCTag.NFC_RFID_TAG;
        mCache.add(mSysFile);
    }

    public int getNDEFFileNumber() throws STException {
        return ((SysFileM24SRTAHighDensity) mSysFile).getNDEFFileNumber();
    }


    ///////////////// Multi Files API /////////////////

    @Override
    public int getNbrOfFiles() throws STException {
        return ((SysFileM24SRTAHighDensity) mSysFile).getNDEFFileNumber() + 1;
    }

    /**
     * Configure the tag with the specified number of files
     * @param fileNbr
     */
    public void setNbrOfFiles(int fileNbr) throws STException {

        selectSysFile();

        byte[] data = new byte[1];
        data[0] = (byte) (0xFF & (fileNbr-1));
        mSTType4Cmd.updateBinary((byte) 0, (byte) 0x07, data);

        // CC File's cache should be flushed when the number of files is changing
        mCCFile.invalidateCache();
    }

    public int getMaxNumberOfFiles() {
        return 8;
    }

    ///////////////////    Multi-Area wrapper   ///////////////////////

    @Override
    public int getMaxNumberOfAreas() {
        return getMaxNumberOfFiles();
    }

    @Override
    public void setNumberOfAreas(int nbOfAreas) throws STException {
        setNbrOfFiles(nbOfAreas);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    public void writeNdefMessage(NDEFMsg msg) throws STException {
        int ndefFileId = mCCFile.getNdefFileId();
        applyM24SRChange(msg);
        super.writeNdefMessage(ndefFileId, msg);
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
    @Override
    public void writeNdefMessage(int fileId, NDEFMsg msg, byte[] writePassword) throws STException {
        applyM24SRChange(msg);
        super.writeNdefMessage(fileId, msg, writePassword);
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
    @Override
    public void writeNdefMessage(int fileId, NDEFMsg msg) throws STException {
        applyM24SRChange(msg);
        super.writeNdefMessage(fileId, msg);
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
    @Override
    public void _writeNdefMessage(int fileId, NDEFMsg msg) throws STException {
        applyM24SRChange(msg);
        super._writeNdefMessage(fileId, msg);
    }



    /**
     * Modify the message array bytes and return a modified message array bytes using one of the following patch
     * the SR flag Or
     * the IL flag Or
     * the CF flag Or
     * add an empty record if previous fix not possible
     * @param msg Ndef message
     */
    private void applyM24SRChange(NDEFMsg msg) throws STException {

        int msgLength;
        try {
            msgLength = msg.getLength();
        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(STException.STExceptionCode.INVALID_NDEF_DATA);
        }

        if (msgLength % mType4Cmd.getMaxCApduDataSize() == 36 ||
                msgLength % mType4Cmd.getMaxCApduDataSize() == 34) {
            try {
                NDEFRecord record = msg.getNDEFRecord(0);
                if (record.getTnf() == 0x01) {
                    if (record.getSR()) {
                        if (record.getPayloadLength() < 256) {
                            record.setSR(false);
                            msg.updateRecord(record, 0);
                            return;

                        }
                    }

                    if (!record.getIL()) {
                        record.setId(new byte[]{0x00, 0x01});
                        msg.updateRecord(record, 0);
                        return;
                    }

                    record.setCF(true);
                    msg.updateRecord(record, 0);
                    byte[] chunk_record = new byte[] {0x56, 0x00, 0x03,0x00,0x00,0x00};
                    msg.addRecord(new NDEFMsg(chunk_record).getNDEFRecord(0));
                    return;
                }

                EmptyRecord emptyRecord= new EmptyRecord();
                msg.addRecord(emptyRecord);
            } catch (Exception e) {
                e.printStackTrace();
                throw new STException(INVALID_NDEF_DATA);
            }
        }
    }
}
