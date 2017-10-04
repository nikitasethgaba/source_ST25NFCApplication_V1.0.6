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

package com.st.st25sdk;

import java.util.List;

public interface RFReaderInterface {

    int mLock = 0;

    enum InventoryMode {
        NFC_TYPE_4A,
        NFC_TYPE_4B,
        NFC_TYPE_5
    }

    static enum TransceiveMode {
        NORMAL,      // Send RF command and returns data from tag
        EVAL,        // Return the RF command frame inside a TRANSCEIVE_EVAL_MODE STException
        RECORD       // NORMAL mode + the executed RF command is recorded
    }

    /**
     * @param obj object shared between the application and the reader implementation
     * @param commandName commandName
     * @param data        data to transceive
     * @return
     * @throws STException When the reader is in EVAL mode, the transceive() method must throw
     * a TRANSCEIVE_EVAL_MODE STException along with the frame associated to the command
     */
    byte[] transceive(Object obj, String commandName, byte[] data) throws STException;

    /**
     * Sets transceive to the specified mode
     * NORMAL: Send RF command and returns data from tag
     * EVAL: the transceive command will throw a TRANSCEIVE_EVAL_MODE STException and set
     * data corresponding to the frame sent by the reader
     * RECORD: the transceive command will be excuted and the
     * data corresponding to the frame sent by the reader is saved
     * @param mode
     */
    void setTransceiveMode(TransceiveMode mode);

    /**
     * Provides the commands run since the transceive mode was set to RECORD
     * @param mode
     */
    public List<byte[]> getTransceivedData();

    /**
     * Provides the last command run when the transceive mode was set to RECORD
     * If no command was recorded, returns an empty byte array (new byte[] {}).
     * @param mode
     */
    public byte[] getLastTransceivedData();

    /**
     *
     * @return current reader's transceive mode
     */
    TransceiveMode getTransceiveMode();

    /**
     * decodeTagType must return the type of the tag specified by the given uid + set the reader
     * in the proper mode of operation for this tag
     * @param uid byte array
     * @return type of NFC tag
     */
    NFCTag.NfcTagTypes decodeTagType(byte[] uid) throws STException;

    /**
     *
     * @return
     */
    String[] getTechList(byte[] uid);

    /**
     *
     * @return the maximum number of bytes that the RF reader can transmit in one command
     */
    int getMaxTransmitLengthInBytes();

    /**
     *
     * @return the maximum number of bytes that the RF reader can receive in one command
     */
    int getMaxReceiveLengthInBytes();



    /*********************************************************************************
     *
     *                          Multi-tag management
     *
     *********************************************************************************/

    /**
     * Returns a list of detected tags.
     * Each byte array in the list is in the LSByte-first order.
     * Example: element {0xF0, 0xC8, 0xD6, 0x01, 0x04, 0x26, 0x02, 0xE0} for UID = E002260401D6C8F0
     *
     * @param mode allows the reader to switch between different ISO/NFC standards
     *             such as NFC Type 4 or NFC Type 5
     * @return List of current detected tag UIDs
     * @throws STException
     */
    List<byte[]> inventory(InventoryMode mode) throws STException;

}
