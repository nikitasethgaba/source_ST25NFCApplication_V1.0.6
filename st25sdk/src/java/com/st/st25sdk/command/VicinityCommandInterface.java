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

public interface VicinityCommandInterface {

    /**
     * Read Single Block command as defined in NFC Forum Type 5 specification.
     *  Reads the content of one memory block (command code 0x20).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress 2-byte address of the block to read
     * @return array of bytes = 1 byte response flag + the content of the block
     * @throws STException
     */
    byte[] readSingleBlock(byte[] blockAddress)
            throws STException;

    /**
     * Read Single Block command as defined in NFC Forum Type 5 specification.
     *  Reads the content of one memory block (command code 0x20).
     * @param blockAddress 2-byte address of the block to read (LSByte first)
     * @param flag Request flag for the command. The protocol extension flag should be set to 1.
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return array of bytes = 1 byte response flag + the content of the block
     * @throws STException
     */
    byte[] readSingleBlock(byte[] blockAddress, byte flag, byte[] uid)
            throws STException;

    /**
     * Write Single Block command as defined in NFC Forum Type 5 specification.
     *  Updates the content of one memory block (command code 0x21).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress 2-byte address of the block to write
     * @param buffer Data to write
     * @return 1 byte response for status
     * @throws STException
     */
    byte writeSingleBlock(byte[] blockAddress, byte[] buffer)
            throws STException;

    /**
     * Write Single Block command as defined in NFC Forum Type 5 specification.
     *  Updates the content of one memory block (command code 0x21).
     * @param blockAddress 2-byte address of the block to write (LSByte first)
     * @param buffer Data to write
     * @param flag Request flag for the command. The protocol extension flag should be set to 1.
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException
     */
    byte writeSingleBlock(byte[] blockAddress, byte[] buffer, byte flag, byte[] uid)
            throws STException;

    /**
     * Read Multiple Block command as defined in NFC Forum Type 5 specification.
     *  Read the content of one or more memory blocks (command code 0x23).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress 2-byte address of the first block to read
     * @return array of bytes = 1 byte response flag + the content of the selected block
     * @throws STException
     */
    byte[] readMultipleBlock(byte[] blockAddress, byte nbrOfBlocks)
            throws STException;

    /**
     * Read Multiple Block command as defined in NFC Forum Type 5 specification.
     *  Read the content of one or more memory blocks (command code 0x23).
     * @param blockAddress 2-byte address of the first block to read (LSByte first)
     * @param nbrOfBlocks The number of blocks in the request is one less than the number of blocks that the VICC shall return in its response.
     * @param flag Request flag for the command. The protocol extension flag should be set to 1.
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return array of bytes = 1 byte response flag + the content of the selected block
     * @throws STException
     */
    byte[] readMultipleBlock(byte[] blockAddress, byte nbrOfBlocks, byte flag, byte[] uid)
            throws STException;

    /**
     * Custom Fast Read Single Block command.
     *  Reads the content of one memory block (command code 0xC0).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress 2-byte address of the block to read
     * @return array of bytes = 1 byte response flag + the content of the block
     * @throws STException
     */
    byte[] fastReadSingleBlock(byte[] blockAddress)
            throws STException;

    /**
     * Custom Fast Read Single Block command.
     *  Reads the content of one memory block (command code 0x20).
     * @param blockAddress 2-byte address of the block to read (LSByte first)
     * @param flag Request flag for the command. The protocol extension flag should be set to 1.
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return array of bytes = 1 byte response flag + the content of the block
     * @throws STException
     */
    byte[] fastReadSingleBlock(byte[] blockAddress, byte flag, byte[] uid)
            throws STException;

    /**
     * Custom Fast Read Multiple Block command.
     *  Read the content of one or more memory blocks (command code 0xC3).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress 2-byte address of the first block to read
     * @return array of bytes = 1 byte response flag + the content of the selected block
     * @throws STException
     */
    byte[] fastReadMultipleBlock(byte[] blockAddress, byte nbrOfBlocks)
            throws STException;

    /**
     *  Custom Fast Read Multiple Block command.
     *  Read the content of one or more memory blocks (command code 0x23).
     * @param blockAddress 2-byte address of the first block to read (LSByte first)
     * @param nbrOfBlocks The number of blocks in the request is one less than the number of blocks that the VICC shall return in its response.
     * @param flag Request flag for the command. The protocol extension flag should be set to 1.
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return array of bytes = 1 byte response flag + the content of the selected block
     * @throws STException
     */
    byte[] fastReadMultipleBlock(byte[] blockAddress, byte nbrOfBlocks, byte flag, byte[] uid)
            throws STException;

    /**
     *
     * @param blockAddress: One of the block address inside the sector
     * @param securityStatus
     * @return
     * @throws STException
     */
    byte lockSector(byte[] blockAddress, byte securityStatus) throws STException;

    byte[] getMultipleBlockSecStatus(byte[] firstBlock, byte[] nbOfBlocks)
            throws STException;

    byte[] fastReadSingleBlock(byte blockOffset) throws STException;

    byte[] fastReadMultipleBlock(byte blockOffset, byte nbrOfBlocks)
            throws STException;

    byte[] getSystemInfo() throws STException;
}
