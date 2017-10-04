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

package com.st.st25sdk.command;

import com.st.st25sdk.STException;

public interface Iso15693CommandInterface {

    /********************* NFC Forum Type5 commands *********************/

    /**
     * Read Single Block command as defined in NFC Forum Type 5 specification.
     *  Reads the content of one memory block (command code 0x20).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress Address of the block to read
     * @return array of bytes = 1 byte response flag + the content of the block (Little endian, or LSByte first)
     * @throws STException {@link}STException
     */
    byte[] readSingleBlock(byte blockAddress)
            throws STException;
    /**
     * Read Single Block command as defined in NFC Forum Type 5 specification.
     *  Reads the content of one memory block (command code 0x20).
     * @param blockAddress Address of the block to read
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return array of bytes = 1 byte response flag + the content of the block (Little endian, or LSByte first)
     * @throws STException {@link}STException
     */
    byte[] readSingleBlock(byte blockAddress, byte flag, byte[] uid)
            throws STException;


    /**
     * Write Single Block command as defined in NFC Forum Type 5 specification.
     *  Updates the content of one memory block (command code 0x21).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress Address of the block to write
     * @param buffer Data to write (Little endian, or LSByte first)
     * @return 1 byte response for status
     * @throws STException {@link}STException
     */
    byte writeSingleBlock(byte blockAddress, byte[] buffer)
            throws STException;
    /**
     * Write Single Block command as defined in NFC Forum Type 5 specification.
     *  Updates the content of one memory block (command code 0x21).
     * @param blockAddress Address of the block to write
     * @param buffer Data to write (Little endian, or LSByte first)
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException {@link}STException
     */
    byte writeSingleBlock(byte blockAddress, byte[] buffer, byte flag, byte[] uid)
            throws STException;


    /**
     * Write Multiple Block command as defined in NFC Forum Type 5 specification.
     *  Write the content of one or more memory blocks (command code 0x24).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress Address of the block to write
     * @param nbrOfBlocks The number of blocks in the request is one less than the number of blocks expected in the response.
     * @param buffer Data to write (Little endian, or LSByte first)
     * @return 1 byte response for status
     * @throws STException {@link}STException
     */
    byte writeMultipleBlock(byte blockAddress, byte nbrOfBlocks, byte[] buffer)
            throws STException;

    /**
     * Write Multiple Block command as defined in NFC Forum Type 5 specification.
     *  Write the content of one or more memory blocks (command code 0x24).
     * @param blockAddress Address of the block to write
     * @param nbrOfBlocks The number of blocks in the request is one less than the number of blocks expected in the response.
     * @param buffer Data to write (Little endian, or LSByte first)
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException {@link}STException
     */
    byte writeMultipleBlock(byte blockAddress, byte nbrOfBlocks, byte[] buffer, byte flag, byte[] uid)
            throws STException;

    /**
     * Extended Write Multiple Block command as defined in NFC Forum Type 5 specification.
     *  Write the content of one or more memory blocks (command code 0x24).
     * @param blockAddress Address of the block to write
     * @param nbrOfBlocks The number of blocks in the request is one less than the number of blocks expected in the response.
     * @param buffer Data to write (Little endian, or LSByte first)
     * @return 1 byte response for status
     * @throws STException {@link}STException
     */
    byte extendedWriteMultipleBlock(byte[] blockAddress, byte[] nbrOfBlocks, byte[] buffer)
            throws STException;

    /**
     * Extended Write Multiple Block command as defined in NFC Forum Type 5 specification.
     *  Write the content of one or more memory blocks (command code 0x34).
     * @param blockAddress Address of the block to write
     * @param nbrOfBlocks The number of blocks in the request is one less than the number of blocks expected in the response.
     * @param buffer Data to write (Little endian, or LSByte first)
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException
     */
    byte extendedWriteMultipleBlock(byte[] blockAddress, byte[] nbrOfBlocks, byte[] buffer, byte flag, byte[] uid)
            throws STException;

    /**
     * Lock Single Block command as defined in NFC Forum Type 5 specification.
     *  Locks the content of one memory block (command code 0x22).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress Address of the block to lock
     * @return 1 byte response for status
     * @throws STException {@link}STException
     */
    byte lockBlock(byte blockAddress)
            throws STException;
    /**
     * Lock Single Block command as defined in NFC Forum Type 5 specification.
     *  Locks the content of one memory block (command code 0x22).
     * @param blockAddress Address of the block to lock
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException {@link}STException
     */
    byte lockBlock(byte blockAddress, byte flag, byte[] uid)
            throws STException;


    /**
     * Read Multiple Block command as defined in NFC Forum Type 5 specification.
     *  Read the content of one or more memory blocks (command code 0x23).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress Address of the first block to read
     * @param nbrOfBlocks The number of blocks in the request is one less than the number of blocks that the VICC shall return in its response.
     * @return array of bytes = 1 byte response flag + the content of the selected block (Little endian, or LSByte first)
     * @throws STException {@link}STException
     */
    byte[] readMultipleBlock(byte blockAddress, byte nbrOfBlocks)
            throws STException;
    /**
     * Read Multiple Block command as defined in NFC Forum Type 5 specification.
     *  Read the content of one or more memory blocks (command code 0x23).
     * @param blockAddress Address of the first block to read
     * @param nbrOfBlocks The number of blocks in the request is one less than the number of blocks that the VICC shall return in its response.
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return array of bytes = 1 byte response flag + the content of the selected block
     * @throws STException {@link}STException
     */
    byte[] readMultipleBlock(byte blockAddress, byte nbrOfBlocks, byte flag, byte[] uid)
            throws STException;


    /**
     * Extended Read Single Block command as defined in NFC Forum Type 5 specification.
     *  Read the content of one block in an Extended memory (command code 0x30).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress Address of the block to read (2-byte address)
     * @return array of bytes = 1 byte response flag + the content of the selected block (Little endian, or LSByte first)
     * @throws STException {@link}STException
     */
    byte[] extendedReadSingleBlock(byte[] blockAddress)
            throws STException;
    /**
     * Extended Read Single Block command as defined in NFC Forum Type 5 specification.
     *  Read the content of one block in an Extended memory (command code 0x30).
     * @param blockAddress Address of the block to read (2-byte address)
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return array of bytes = 1 byte response flag + the content of the selected block (Little endian, or LSByte first)
     * @throws STException {@link}STException
     */
    byte[] extendedReadSingleBlock(byte[] blockAddress, byte flag, byte[] uid)
            throws STException;

    /**
     * Extended Write Single Block command as defined in NFC Forum Type 5 specification.
     *  Update the content of one block in an Extended memory (command code 0x31).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress Address of the block to write (2-byte address)
     * @param buffer data to write (Little endian, or LSByte first)
     * @return 1 byte response for status
     * @throws STException {@link}STException
     */
    byte extendedWriteSingleBlock(byte[] blockAddress, byte[] buffer)
            throws STException;
    /**
     * Extended Write Single Block command as defined in NFC Forum Type 5 specification.
     *  Update the content of one block in an Extended memory (command code 0x31).
     * @param blockAddress Address of the block to write (2-byte address)
     * @param buffer data to write (Little endian, or LSByte first)
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException {@link}STException
     */
    byte extendedWriteSingleBlock(byte[] blockAddress, byte[] buffer, byte flag, byte[] uid)
            throws STException;

    /**
     * Extended Lock Single Block command as defined in NFC Forum Type 5 specification.
     *  Lock the content of one block in an Extended memory (command code 0x32).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress Address of the block to lock (2-byte address)
     * @return 1 byte response for status
     * @throws STException {@link}STException
     */
    byte extendedLockSingleBlock(byte[] blockAddress)
            throws STException;
    /**
     * Extended Lock Single Block command as defined in NFC Forum Type 5 specification.
     *  Lock the content of one block in an Extended memory (command code 0x32).
     * @param blockAddress Address of the block to lock (2-byte address)
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException {@link}STException
     */
    byte extendedLockSingleBlock(byte[] blockAddress, byte flag, byte[] uid)
            throws STException;


    /**
     * Extended Read Multiple Block command as defined in NFC Forum Type 5 specification.
     *  Read the content of one or more blocks in an Extended memory (command code 0x33).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress Address of the first block to read (2-byte address, LSByte first)
     * @param numberOfBlocks The number of blocks in the request is one less than the number of blocks that the VICC shall return in its response. (2-byte address, LSByte first)
     * @return array of bytes = 1 byte response flag + the content of the selected blocks (Little endian, or LSByte first)
     * @throws STException {@link}STException
     */
    byte[] extendedReadMultipleBlock(byte[] blockAddress, byte[] numberOfBlocks)
            throws STException;
    /**
     * Extended Read Multiple Block command as defined in NFC Forum Type 5 specification.
     *  Read the content of one or more blocks in an Extended memory (command code 0x33).
     * @param blockAddress Address of the first block to read (2-byte address, LSByte first)
     * @param numberOfBlocks The number of blocks in the request is one less than the number of blocks that the VICC shall return in its response. (2-byte address, LSByte first)
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return array of bytes = 1 byte response flag + the content of the selected blocks (Little endian, or LSByte first)
     * @throws STException {@link}STException
     */
    byte[] extendedReadMultipleBlock(byte[] blockAddress, byte[] numberOfBlocks, byte flag, byte[] uid)
            throws STException;


    /**
     * Select command as defined in NFC Forum Type 5 specification.
     *  Sets one Type 5 Tag into the Selected state (command code 0x25).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @return 1 byte response for status
     * @throws STException {@link}STException
     */
    byte select()
            throws STException;
    /**
     * Select command as defined in NFC Forum Type 5 specification.
     *  Sets one Type 5 Tag into the Selected state (command code 0x25).
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException {@link}STException
     */
    byte select(byte flag, byte[] uid)
            throws STException;


    /********************* Other ISO15693 commands *********************/

    /**
     *
     * @param flag 1 byte with the inventory command's request flags
     * @return
     * @throws STException {@link}STException
     */
    byte[] inventory(byte flag)
            throws STException;
    /**
     *
     * @param flag 1 byte with the inventory command's request flags
     * @param maskLength 1 byte for the anti-collision mask length
     * @param maskValue 4-byte value of the mask value if the mask length is different from 0
     * @return
     * @throws STException {@link}STException
     */
    byte[] inventory(byte flag, byte maskLength, byte[] maskValue)
            throws STException;
    /**
     *
     * @param flag 1 byte with the inventory command's request flags
     * @param maskLength 1 byte for the anti-collision mask length
     * @param maskValue 4-byte value of the mask value if the mask length is different from 0
     * @param afiField 1-byte AFI value
     * @return
     * @throws STException {@link}STException
     */
    byte[] inventory(byte flag, byte maskLength, byte[] maskValue, byte afiField)
            throws STException;

    /**
     * @return 1 byte status
     * @throws STException
     */
    byte stayQuiet()
            throws STException;
    /**
     * @param flag 1 byte request flag
     * @param uid byte array UID value
     * @return 1 byte status
     * @throws STException {@link}STException
     */
    byte stayQuiet(byte flag, byte[] uid)
            throws STException;

    /**
     * @return 1 byte status
     * @throws STException {@link}STException
     */
    byte resetToReady()
            throws STException;

    /**
     * @param flag 1 byte request flag
     * @param uid byte array UID value
     * @return 1 byte status
     * @throws STException {@link}STException
     */
    byte resetToReady(byte flag, byte[] uid)
            throws STException;

    /**
     * @param value
     * @return 1 byte status
     * @throws STException {@link}STException
     */
    byte writeAFI(byte value)
            throws STException;
    /**
     * @param flag 1 byte request flag
     * @param uid byte array UID value
     * @return 1 byte status
     * @throws STException {@link}STException
     */
    byte writeAFI(byte value, byte flag, byte[] uid)
            throws STException;

    /**
     * Lock AFI value.
     * @return
     * @throws STException
     */
    byte lockAFI()
            throws STException;

    /**
     * Lock AFI value.
     * @param flag 1 byte request flag
     * @param uid byte array UID value
     * @return
     * @throws STException {@link}STException
     */
    byte lockAFI(byte flag, byte[] uid)
            throws STException;

    /**
     * @return 1 byte status
     * @throws STException
     */
    byte writeDSFID(byte value)
            throws STException;
    /**
     * @param flag 1 byte request flag
     * @param uid byte array UID value
     * @return 1 byte status
     * @throws STException {@link}STException
     */
    byte writeDSFID(byte value, byte flag, byte[] uid)
            throws STException;

    /**
     * Lock DSFID value.
     * @return
     * @throws STException
     */
    byte lockDSFID()
            throws STException;

    /**
     * Lock DSFID value.
     * @param flag 1 byte request flag
     * @param uid byte array UID value
     * @return
     * @throws STException {@link}STException
     */
    byte lockDSFID(byte flag, byte[] uid)
            throws STException;

    /**
     * Send getSystemInfo command to the tag
     * @return tag's information
     * @throws STException {@link}STException
     */
    byte[] getSystemInfo()
            throws STException;
    /**
     * Send getSystemInfo command to the tag
     * @param flag 1 byte request flag
     * @param uid byte array UID value
     * @return tag's information
     * @throws STException {@link}STException
     */
    byte[] getSystemInfo(byte flag, byte[] uid)
            throws STException;

    /**
     * Send extendedGetSystemInfo command to the tag
     * @return tag's extended information, depending on the parameter byte value
     * @throws STException
     */
    byte[] extendedGetSystemInfo()
            throws STException;
    /**
     * Send extendedGetSystemInfo command to the tag
     * @param parameters
     * @return tag's extended information, depending on the parameter byte value
     * @throws STException {@link}STException
     */
    byte[] extendedGetSystemInfo(byte parameters)
            throws STException;
    /**
     * Send extendedGetSystemInfo command to the tag
     * @param parameters
     * @param flag 1 byte request flag
     * @param uid byte array UID value
     * @return tag's extended information, depending on the parameter byte value
     * @throws STException {@link}STException
     */
    byte[] extendedGetSystemInfo(byte parameters, byte flag, byte[] uid)
            throws STException;

    /**
     * Send getMultipleBlockSecStatus command to the tag
     * @param firstBlock first block
     * @param nbOfBlocks number of blocks
     * @return tag's extended information, depending on the parameter byte value
     * @throws STException {@link}STException
     */
    byte[] getMultipleBlockSecStatus(byte firstBlock, byte nbOfBlocks)
            throws STException;
    /**
     * Send getMultipleBlockSecStatus command to the tag
     * @param firstBlock first block
     * @param nbOfBlocks nbmber of blocks
     * @param flag 1 byte request flag
     * @param uid byte array UID value
     * @return tag's extended information, depending on the parameter byte value
     * @throws STException {@link}STException
     */
    byte[] getMultipleBlockSecStatus(byte firstBlock, byte nbOfBlocks, byte flag, byte[] uid)
            throws STException;

    /**
     * Send extendedGetMultipleBlockSecStatus command to the tag
     * @param firstBlock first block
     * @param nbOfBlocks nbmber of blocks
     * @return tag's extended information, depending on the parameter byte value
     * @throws STException {@link}STException
     */
    byte[] extendedGetMultipleBlockSecStatus(byte[] firstBlock, byte[] nbOfBlocks)
            throws STException;
    /**
     * Send extendedGetMultipleBlockSecStatus command to the tag
     * @param firstBlock first block
     * @param nbOfBlocks nbmber of blocks
     * @param flag 1 byte request flag
     * @param uid byte array UID value
     * @return tag's extended information, depending on the parameter byte value
     * @throws STException {@link}STException
     */
    byte[] extendedGetMultipleBlockSecStatus(byte[] firstBlock, byte[] nbOfBlocks, byte flag, byte[] uid)
            throws STException;
}
