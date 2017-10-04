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

/**
 * This class consists exclusively of methods that map the NFC Forum Type 5 Tag RF commands.
 *
 * <p> All methods (at the exception of <tt>select</tt>) use a block address as
 * parameter. The first block of memory starts at address 0. The number of bytes
 * per block can be determined by the length of the response of readSingleBlock (without BSS)
 * minus 1 byte (for the response flag) when the command is successful.
 *
 * <p> All commands return a status flag in the first byte of their response.
 * The content of this response flag is described in the Type 5 specification.
 * When a command is successful, the flag value is 0x00.
 *
 * <p> The methods of this class all throw a <tt>STException</tt> returned by
 * the RF transceive() command implemented for each reader.
 *
 * @author STMicroelectronics, June 14th 2016
 *
 */
public interface Type5CommandInterface {

    byte NFC_TYPE5_CMD_READ_SINGLE_BLOCK = (byte) 0x20;
    byte NFC_TYPE5_CMD_WRITE_SINGLE_BLOCK = (byte) 0x21;
    byte NFC_TYPE5_CMD_LOCK_SINGLE_BLOCK = (byte) 0x22;
    byte NFC_TYPE5_CMD_READ_MULTIPLE_BLOCK = (byte) 0x23;
    byte NFC_TYPE5_CMD_EXTENDED_READ_SINGLE_BLOCK = (byte) 0x30;
    byte NFC_TYPE5_CMD_EXTENDED_WRITE_SINGLE_BLOCK = (byte) 0x31;
    byte NFC_TYPE5_CMD_EXTENDED_LOCK_SINGLE_BLOCK = (byte) 0x32;
    byte NFC_TYPE5_CMD_EXTENDED_READ_MULTIPLE_BLOCK = (byte) 0x33;
    byte NFC_TYPE5_CMD_SELECT = (byte) 0x25;
    byte NFC_TYPE5_CMD_SLPV = (byte) 0x02;

    /**
     * Read Single Block command as defined in NFC Forum Type 5 specification.
     *  Reads the content of one memory block (command code 0x20).
     * @param blockAddress Address of the block to read
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return array of bytes = 1 byte response flag + the content of the block (Little endian, or LSByte first)
     * @throws STException
     */
    byte[] readSingleBlock(byte blockAddress, byte flag, byte[] uid)
            throws STException;


    /**
     * Write Single Block command as defined in NFC Forum Type 5 specification.
     *  Updates the content of one memory block (command code 0x21).
     * @param blockAddress Address of the block to write
     * @param buffer Data to write (Little endian, or LSByte first)
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException
     */
    byte writeSingleBlock(byte blockAddress, byte[] buffer, byte flag, byte[] uid)
            throws STException;


    /**
     * Lock Single Block command as defined in NFC Forum Type 5 specification.
     *  Locks the content of one memory block (command code 0x22).
     * @param blockAddress Address of the block to lock
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException
     */
    byte lockSingleBlock(byte blockAddress, byte flag, byte[] uid)
            throws STException;

    /**
     * Read Multiple Block command as defined in NFC Forum Type 5 specification.
     *  Read the content of one or more memory blocks (command code 0x23).
     * @param blockAddress Address of the first block to read
     * @param numberOfBlocks The number of blocks in the request is one less than the number of blocks that the VICC shall return in its response.
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return array of bytes = 1 byte response flag + the content of the selected block (Little endian, or LSByte first)
     * @throws STException
     */
    byte[] readMultipleBlock(byte blockAddress, byte numberOfBlocks, byte flag, byte[] uid)
            throws STException;

    /**
     * Extended Read Single Block command as defined in NFC Forum Type 5 specification.
     *  Read the content of one block in an Extended memory (command code 0x30).
     * @param blockAddress Address of the block to read (2-byte address, LSByte first)
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return array of bytes = 1 byte response flag + the content of the selected block (Little endian, or LSByte first)
     * @throws STException
     */
    byte[] extendedReadSingleBlock(byte[] blockAddress, byte flag, byte[] uid)
            throws STException;

    /**
     * Extended Write Single Block command as defined in NFC Forum Type 5 specification.
     *  Update the content of one block in an Extended memory (command code 0x31).
     * @param blockAddress Address of the block to write (2-byte address, LSByte first)
     * @param buffer data to write (Little endian, or LSByte first)
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException
     */
    byte extendedWriteSingleBlock(byte[] blockAddress, byte[] buffer, byte flag, byte[] uid)
            throws STException;

    /**
     * Extended Lock Single Block command as defined in NFC Forum Type 5 specification.
     *  Lock the content of one block in an Extended memory (command code 0x32).
     * @param blockAddress Address of the block to lock (2-byte address)
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException
     */
    byte extendedLockSingleBlock(byte[] blockAddress, byte flag, byte[] uid)
            throws STException;

    /**
     * Extended Read Multiple Block command as defined in NFC Forum Type 5 specification.
     *  Read the content of one or more blocks in an Extended memory (command code 0x33).
     * @param blockAddress Address of the first block to read (2-byte address, LSByte first)
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return array of bytes = 1 byte response flag + the content of the selected blocks (Little endian, or LSByte first)
     * @throws STException
     */
    byte[] extendedReadMultipleBlock(byte[] blockAddress, byte[] numberOfBlocks, byte flag, byte[] uid)
            throws STException;

    /**
     * Select command as defined in NFC Forum Type 5 specification.
     *  Sets one Type 5 Tag into the Selected state (command code 0x25).
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException
     */
    byte select(byte flag, byte[] uid)
            throws STException;

    /**
     * SLPV_REQ command as defined in NFC Forum Type 5 specification.
     *  Sets one Type 5 Tag into the QUIET state (command code 0x02).
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException
     */
    byte stayQuiet(byte flag, byte[] uid)
            throws STException;

}
