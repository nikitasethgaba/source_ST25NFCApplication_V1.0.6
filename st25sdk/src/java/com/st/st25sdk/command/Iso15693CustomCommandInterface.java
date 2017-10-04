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

public interface Iso15693CustomCommandInterface  {

    /**
     *
     * @param unencryptedKillCode kill code byte array
     * @return status byte
     * @throws STException
     */
    byte kill(byte[] unencryptedKillCode) throws STException;

    /**
     *
     * @param unencryptedKillPassword unencrypted kill password
     * @return status byte
     * @throws STException
     */
    byte writeKill(byte[] unencryptedKillPassword) throws STException;

    /**
     *
     * @return status byte
     * @throws STException
     */
    byte lockKill() throws STException;

    /**
     *
     * @param configId configuration Id
     * @return status byte + Register value byte
     * @throws STException
     */
    byte[] readConfig(byte configId) throws STException;

    /**
     * readCfg command for tags having a single register
     * @return status byte + Config value byte
     * @throws STException
     */
    byte[] readCfg() throws STException;

    /**
     *
     * @param configId
     * @param newAttributeValue
     * @return status byte
     * @throws STException
     */
    byte writeConfig(byte configId, byte newAttributeValue) throws STException;

    /**
     *
     * @param configId
     * @return status byte + Register value byte
     * @throws STException
     */
    byte[] readDynConfig(byte configId) throws STException;

    /**
     *
     * @param configId
     * @return
     * @throws STException
     */
    byte[] fastReadDynConfig(byte configId) throws STException;

    /**
     *
     * @param configId
     * @param newAttributeValue
     * @return status byte
     * @throws STException
     */
    byte writeDynConfig(byte configId, byte newAttributeValue) throws STException;


    /**
     *
     * @param configId
     * @param newAttributeValue
     * @return status byte
     * @throws STException
     */
    byte fastWriteDynConfig(byte configId, byte newAttributeValue) throws STException;

    /**
     *
     * @return status byte + response data
     * @throws STException
     */
    byte[] getRandomNumber() throws STException;

    /**
     *
     * @param passwordNumber
     * @param newPassword
     * @return status byte
     * @throws STException
     */
    byte writePwd(byte passwordNumber, byte[] newPassword) throws STException;

    /**
     * Unlock a feature by using its passwordNumber.
     * @param passwordNumber
     * @param password : password[0] contains the MSByte
     * @return status byte
     * @throws STException
     */
    byte presentPwd(byte passwordNumber, byte[] password) throws STException;

    /**
     *
     * @param blockAddress: one of the block address inside the sector
     * @param securityStatus
     * @return status byte
     * @throws STException
     */
    byte lockSector(byte blockAddress, byte securityStatus) throws STException;

    /**
     * Custom Fast Read Single Block command.
     *  Reads the content of one memory block (command code 0xC0).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress byte address of the block to read
     * @return array of bytes = 1 byte response flag + the content of the block
     * @throws STException
     */
    byte[] fastReadSingleBlock(byte blockAddress) throws STException;

    /**
     * Custom Fast Read Multiple Block command.
     *  Read the content of one or more memory blocks (command code 0xC3).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress byte address of the first block to read
     * @return array of bytes = 1 byte response flag + the content of the selected block
     * @throws STException
     */
    byte[] fastReadMultipleBlock(byte blockAddress, byte nbrOfBlocks) throws STException;

    /**
     * Custom Fast Extended Read Single Block command.
     *  Reads the content of one memory block (command code 0xC4).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress byte address of the block to read
     * @return array of bytes = 1 byte response flag + the content of the block
     * @throws STException
     */
    byte[] fastExtendedReadSingleBlock(byte[] blockAddress) throws STException;

    /**
     * Custom Fast Extended Read Multiple Block command.
     *  Read the content of one or more memory blocks (command code 0xC5).
     *  Uses the default flag that was set in TagCommand.setFlag().
     *  Uses the default uid that was set in TagCommand.setUid().
     * @param blockAddress byte address of the first block to read
     * @return array of bytes = 1 byte response flag + the content of the selected block
     * @throws STException
     */
    byte[] fastExtendedReadMultipleBlock(byte[] blockAddress, byte[] nbrOfBlocks) throws STException;

    /* - - - - M24LR - - - - - */

    /**
     * Write EHCfg command as defined in M24LR specification.
     * @param data Data to write
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException
     */
    byte writeEHCfg(byte data, byte flag, byte[] uid) throws STException;

    /**
     * Write DOCfg command as defined in M24LR specification.
     * @param data Data to write
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException
     */
    byte writeDOCfg(byte data, byte flag, byte[] uid) throws STException;

    /**
     * Set RstEHEn command as defined in M24LR specification.
     * @param data Data to write
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return 1 byte response for status
     * @throws STException
     */
    byte setRstEHEn(byte data, byte flag, byte[] uid) throws STException;

    /**
     * Check EHEn command as defined in M24LR specification.
     * @param flag Request flag for the command
     * @param uid Tag's UID (used only if the Select or Addressed mode bits are set in the flag)
     * @return checkEnable data response
     * @return array of bytes = 1 byte response flag + 1 byte data
     * @throws STException
     */
    byte[] checkEHEn(byte flag, byte[] uid) throws STException;

    /**
     *
     * @param sizeInBytes
     * @param buffer
     * @return status byte
     * @throws STException
     */
    byte writeMsg(byte sizeInBytes, byte[] buffer) throws STException;

    /**
     *
     * @param offset
     * @param sizeInBytes
     * @return status byte + Command response
     * @throws STException
     */
    byte[] readMsg(byte offset, byte sizeInBytes) throws STException;

    /**
     *
     * @return Command status byte + Command response
     * @throws STException
     */
    byte[] readMsgLength() throws STException;

    /**
     *
     * @param sizeInBytes
     * @param buffer
     * @return status byte
     * @throws STException
     */
    byte fastWriteMsg(byte sizeInBytes, byte[] buffer) throws STException;


    /**
     *
     * @param offset
     * @param sizeInBytes
     * @return status byte + Command response
     * @throws STException
     */
    byte[] fastReadMsg(byte offset, byte sizeInBytes) throws STException;

    /**
     *
     * @return Command status byte + Command response
     * @throws STException
     */
    byte[] fastReadMsgLength() throws STException;

    /**
     *
     * @param value
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    byte manageGpo(byte value, byte flag, byte[] uid) throws STException;

    /**
     *
     * @param obfuscatedConfidentialPassword
     * @return status byte
     * @throws STException
     */
    byte enableConfidentialMode(byte[] obfuscatedConfidentialPassword)  throws STException;

    /**
     * Function used to enable the EAS feature
     * @return status byte
     * @throws STException
     */
    byte setEas() throws STException;

    /**
     * Function used to disable the EAS feature
     * @return status byte
     * @throws STException
     */
    byte resetEas()  throws STException;

    /**
     * Function used to permanently lock the EAS feature.
     * Warning: This is irreversible.
     *
     * @return status byte
     * @throws STException
     */
    byte lockEas()  throws STException;

    /**
     * Enable EAS (read EAS Telegram or EAS Id)
     * @return status byte + Command response
     * @throws STException
     */
    byte[] enableEAS()  throws STException;

    /**
     * Function used to write an EAS_ID into the tag.
     * @param easId : 2 Bytes array containing the EAS_ID
     *                easId[0] contains the LSB and easId[1] the MSB
     * @return status byte
     * @throws STException
     */
    byte writeEasId(byte[] easId)  throws STException;

    /**
     * Write the EAS configuration indicating the telegram length
     * @param config
     * @return status byte
     * @throws STException
     */
    byte writeEasConfig(byte config)  throws STException;

    /**
     *
     * @return status byte + Command response
     * @throws STException
     */
    byte[] readSignature() throws STException;

    /**
     *
     * @param flag
     * @param uid
     * @return status byte + Command response
     * @throws STException
     */
    byte[] readSignature(byte flag, byte[] uid) throws STException;

    /**
     *
     * @param flag
     * @return status byte + Command response
     * @throws STException
     */
    byte[] initiate(byte flag) throws STException;

    /**
     *
     * @param flag 1 byte with the inventory initiated command's request flags
     * @return status byte + Command response
     * @throws STException
     */
    byte[] inventoryInitiated(byte flag) throws STException;

    /**
     *
     * @param flag 1 byte with the inventory initiated command's request flags
     * @param maskLength 1 byte for the anti-collision mask length
     * @param maskValue 4-byte value of the mask value if the mask length is different from 0
     * @return status byte + Command response
     * @throws STException
     */
    byte[] inventoryInitiated(byte flag, byte maskLength, byte[] maskValue) throws STException;

    /**
     *
     * @param flag 1 byte with the inventory initiated command's request flags
     * @param maskLength 1 byte for the anti-collision mask length
     * @param maskValue 4-byte value of the mask value if the mask length is different from 0
     * @param afiField 1-byte AFI value
     * @return status byte + Command response
     * @throws STException
     */
    byte[] inventoryInitiated(byte flag, byte maskLength, byte[] maskValue, byte afiField) throws STException;

    /**
     *
     * @param flag
     * @return status byte + Command response
     * @throws STException
     */
    byte[] fastInitiate(byte flag) throws STException;

    /**
     *
     * @param flag 1 byte with the inventory initiated command's request flags
     * @return status byte + Command response
     * @throws STException
     */
    byte[] fastInventoryInitiated(byte flag) throws STException;

    /**
     *
     * @param flag 1 byte with the inventory initiated command's request flags
     * @param maskLength 1 byte for the anti-collision mask length
     * @param maskValue 4-byte value of the mask value if the mask length is different from 0
     * @return status byte + Command response
     * @throws STException
     */
    byte[] fastInventoryInitiated(byte flag, byte maskLength, byte[] maskValue) throws STException;

    /**
     *
     * @param flag 1 byte with the inventory initiated command's request flags
     * @param maskLength 1 byte for the anti-collision mask length
     * @param maskValue 4-byte value of the mask value if the mask length is different from 0
     * @param afiField 1-byte AFI value
     * @return status byte + Command response
     * @throws STException
     */
    byte[] fastInventoryInitiated(byte flag, byte maskLength, byte[] maskValue, byte afiField) throws STException;

    /**
     *
     * @param flag 1 byte with the inventory initiated command's request flags
     * @param blockAddress
     * @param nbrOfBlocks
     * @return status byte + Command response
     * @throws STException
     */
    byte[] inventoryRead(byte flag, byte blockAddress, byte nbrOfBlocks) throws STException;

    /**
     *
     * @param flag
     * @param maskLength
     * @param maskValue
     * @param blockAddress
     * @param nbrOfBlocks
     * @return status byte + Command response
     * @throws STException
     */
    byte[] inventoryRead(byte flag, byte maskLength, byte[] maskValue, byte blockAddress, byte nbrOfBlocks) throws STException;

    /**
     *
     * @param flag 1 byte with the inventory initiated command's request flags
     * @param maskLength 1 byte for the anti-collision mask length
     * @param maskValue 4-byte value of the mask value if the mask length is different from 0
     * @param afiField 1-byte AFI value
     * @param blockAddress
     * @param nbrOfBlocks
     * @return status byte + Command response
     * @throws STException
     */
    byte[] inventoryRead(byte flag, byte maskLength, byte[] maskValue, byte afiField, byte blockAddress, byte nbrOfBlocks) throws STException;

    /**
     *
     * @param flag 1 byte with the inventory initiated command's request flags
     * @param blockAddress
     * @param nbrOfBlocks
     * @return status byte + Command response
     * @throws STException
     */
    byte[] fastInventoryRead(byte flag, byte blockAddress, byte nbrOfBlocks) throws STException;

    /**
     *
     * @param flag 1 byte with the inventory initiated command's request flags
     * @param maskLength 1 byte for the anti-collision mask length
     * @param maskValue 4-byte value of the mask value if the mask length is different from 0
     * @return status byte + Command response
     * @throws STException
     */
    byte[] fastInventoryRead(byte flag, byte maskLength, byte[] maskValue, byte blockAddress, byte nbrOfBlocks) throws STException;

    /**
     *
     * @param flag 1 byte with the inventory initiated command's request flags
     * @param maskLength 1 byte for the anti-collision mask length
     * @param maskValue 4-byte value of the mask value if the mask length is different from 0
     * @param afiField 1-byte AFI value
     * @param blockAddress
     * @param nbrOfBlocks
     * @return status byte + Command response
     * @throws STException
     */
    byte[] fastInventoryRead(byte flag, byte maskLength, byte[] maskValue, byte afiField, byte blockAddress, byte nbrOfBlocks) throws STException;

}
