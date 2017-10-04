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

import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;
import static com.st.st25sdk.STException.STExceptionCode.CONFIG_PASSWORD_NEEDED;
import static com.st.st25sdk.STException.STExceptionCode.ISO15693_BLOCK_IS_LOCKED;

import com.st.st25sdk.Helper;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.type5.Type5Tag;

public class Iso15693CustomCommand extends Iso15693Command implements Iso15693CustomCommandInterface {

    public Iso15693CustomCommand(RFReaderInterface reader, byte[] uid) {
        this(reader, uid, DEFAULT_FLAG, Type5Tag.DEFAULT_NBR_OF_BYTES_PER_BLOCK);
    }

    public Iso15693CustomCommand(RFReaderInterface reader, byte[] uid, byte flag) {
        this(reader, uid, flag, Type5Tag.DEFAULT_NBR_OF_BYTES_PER_BLOCK);
    }

    public Iso15693CustomCommand(RFReaderInterface reader, byte[] uid, int nbrOfBytesPerBlock) {
        this(reader, uid, DEFAULT_FLAG, nbrOfBytesPerBlock);
    }

    public Iso15693CustomCommand(RFReaderInterface reader, byte[] uid, byte flag, int nbrOfBytesPerBlock) {
        super(reader, uid, flag, nbrOfBytesPerBlock);
    }


    public static final byte ISO15693_CUSTOM_ST_CMD_READ_CONFIG = (byte) 0xA0;
    public static final byte ISO15693_CUSTOM_ST_CMD_WRITE_CONFIG = (byte) 0xA1;
    public static final byte ISO15693_CUSTOM_ST_CMD_READ_DYN_CONFIG = (byte) 0xAD;
    public static final byte ISO15693_CUSTOM_ST_CMD_WRITE_DYN_CONFIG = (byte) 0xAE;

    public static final byte ISO15693_CUSTOM_ST_CMD_WRITE_PASSWORD = (byte) 0xB1;
    public static final byte ISO15693_CUSTOM_ST_CMD_LOCK_SECTOR = (byte) 0xB2;
    public static final byte ISO15693_CUSTOM_ST_CMD_PRESENT_PASSWORD = (byte) 0xB3;
    public static final byte ISO15693_CUSTOM_ST_CMD_GET_RANDOM_NUMBER = (byte) 0xB4;

    public static final byte ISO15693_CUSTOM_ST_CMD_FAST_READ_SINGLE_BLOCK = (byte) 0xC0;
    public static final byte ISO15693_CUSTOM_ST_CMD_FAST_READ_MULTIPLE_BLOCK = (byte) 0xC3;
    public static final byte ISO15693_CUSTOM_ST_CMD_FAST_READ_DYN_CONFIG = (byte) 0xCD;
    public static final byte ISO15693_CUSTOM_ST_CMD_FAST_WRITE_DYN_CONFIG = (byte) 0xCE;

    public static final byte ISO15693_CUSTOM_ST_CMD_FAST_EXTENDED_READ_SINGLE_BLOCK = (byte) 0xC4;
    public static final byte ISO15693_CUSTOM_ST_CMD_FAST_EXTENDED_READ_MULTIPLE_BLOCK = (byte) 0xC5;

    public static final byte ISO15693_CUSTOM_ST_CMD_KILL = (byte) 0xA6;
    public static final byte ISO15693_CUSTOM_ST_CMD_WRITE_KILL = (byte) 0xB1;
    public static final byte ISO15693_CUSTOM_ST_CMD_LOCK_KILL = (byte) 0xB2;
    public static final byte ISO15693_CUSTOM_ST_PARAM_KILL_ACCESS = 0x00;

    public static final byte ISO15693_CUSTOM_ST_CMD_INVENTORY_INITIATED = (byte) 0xD1;
    public static final byte ISO15693_CUSTOM_ST_CMD_INITIATE = (byte) 0xD2;
    public static final byte ISO15693_CUSTOM_ST_CMD_FAST_INITIATE = (byte) 0xC2;
    public static final byte ISO15693_CUSTOM_ST_CMD_FAST_INVENTORY_INITIATED = (byte) 0xC1;

    //M24LR COMMANDS
    public static final byte ISO15693_CUSTOM_ST_CMD_WRITE_EH_CFG     = (byte) 0xA1;
    public static final byte ISO15693_CUSTOM_ST_CMD_SET_RST_EH_EN    = (byte) 0xA2;
    public static final byte ISO15693_CUSTOM_ST_CMD_CHECK_EH_EN      = (byte) 0xA3;
    public static final byte ISO15693_CUSTOM_ST_CMD_WRITE_DO_CFG     = (byte) 0xA4;

    //ST25DV COMMANDS
    public static final byte ISO15693_CUSTOM_ST_CMD_MANAGE_GPO = (byte) 0xA9;

    public static final byte ISO15693_CUSTOM_ST_CMD_MB_WRITE_MSG = (byte) 0xAA;
    public static final byte ISO15693_CUSTOM_ST_CMD_MB_READ_MSG_LENGTH = (byte) 0xAB;
    public static final byte ISO15693_CUSTOM_ST_CMD_MB_READ_MSG = (byte) 0xAC;
    public static final byte ISO15693_CUSTOM_ST_CMD_MB_FAST_WRITE_MSG = (byte) 0xCA;

    public static final byte ISO15693_CUSTOM_ST_CMD_MB_FAST_READ_MSG_LENGTH = (byte) 0xCB;
    public static final byte ISO15693_CUSTOM_ST_CMD_MB_FAST_READ_MSG = (byte) 0xCC;

    // ST25TV's command codes
    public static final byte ISO15693_CUSTOM_ST_CMD_SET_EAS          = (byte) 0xA2;
    public static final byte ISO15693_CUSTOM_ST_CMD_RESET_EAS        = (byte) 0xA3;
    public static final byte ISO15693_CUSTOM_ST_CMD_LOCK_EAS         = (byte) 0xA4;
    public static final byte ISO15693_CUSTOM_ST_CMD_ENABLE_EAS       = (byte) 0xA5;
    public static final byte ISO15693_CUSTOM_ST_CMD_WRITE_EAS_ID     = (byte) 0xA7;
    public static final byte ISO15693_CUSTOM_ST_CMD_WRITE_EAS_CONFIG = (byte) 0xA8;

    public static final byte ISO15693_CUSTOM_ST_CMD_ENABLE_CONFIDENTIAL_MODE = (byte) 0xBA;

    public static final byte ISO15693_CUSTOM_ST_CMD_INVENTORY_READ   = (byte) 0xD3;
    public static final byte ISO15693_CUSTOM_ST_CMD_FAST_INVENTORY_READ   = (byte) 0xD4;
    public static final byte ISO15693_CUSTOM_ST_CMD_READ_SIGNATURE   = (byte) 0xDB;

    public  static final byte ISO15693_CUSTOM_ST_PARAM_CONFIDENTIAL_ACCESS_CODE = 0x00;

    /**
     * {@inheritDoc}
     */
    @Override
    public byte kill(byte[] unencryptedKillCode) throws STException {
        return kill(unencryptedKillCode, mFlag, mUid);
    }

    /**
     *
     * @param unencryptedKillCode
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte kill(byte[] unencryptedKillCode, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        if (unencryptedKillCode == null ||  unencryptedKillCode.length != 4) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693CustomHeaderSize(flag);
        request = new byte[headerSize + 1 + 4];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_KILL;
        request[2] = Iso15693Protocol.STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, Iso15693Protocol.ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = ISO15693_CUSTOM_ST_PARAM_KILL_ACCESS;
        System.arraycopy(unencryptedKillCode, 0, request, headerSize + 1, unencryptedKillCode.length);

        response = transceive("kill", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeKill(byte[] unencryptedKillPassword) throws STException {
        return writeKill(unencryptedKillPassword, mFlag, mUid);
    }

    /**
     *
     * @param unencryptedKillPassword
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte writeKill(byte[] unencryptedKillPassword, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        if (unencryptedKillPassword == null || unencryptedKillPassword.length != 4) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693CustomHeaderSize(flag);
        request = new byte[headerSize + 1 + 4];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_WRITE_KILL;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = ISO15693_CUSTOM_ST_PARAM_KILL_ACCESS;
        System.arraycopy(unencryptedKillPassword, 0, request, headerSize + 1, unencryptedKillPassword.length);

        response = transceive("write kill", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte lockKill() throws STException {
        return lockKill(mFlag, mUid);
    }

    /**
     *
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte lockKill(byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int headerSize;
        byte lockKillProtectStatus = 0x01; // Mandatory value defined in LRi and ST25TV datasheets

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1 + 1];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_LOCK_KILL;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = ISO15693_CUSTOM_ST_PARAM_KILL_ACCESS;
        request[headerSize + 1] = lockKillProtectStatus;

        response = transceive("lock kill", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] initiate(byte flag) throws STException {
        byte[] request;
        int headerSize;

        headerSize = ISO15693_CUSTOM_ST_HEADER_SIZE;

        request = new byte[headerSize];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_INITIATE;
        request[2] = STM_MANUFACTURER_CODE;

        return transceive("initiate", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] inventoryInitiated(byte flag) throws STException {
        /* Default inventory configuration is:
         *    - Mask length of zero (so mask value doesn't matter)
         *    - No AFI field
         */
        return inventoryInitiated(flag, (byte) 0x00, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] inventoryInitiated(byte flag, byte maskLength, byte[] maskValue) throws STException {
        return buildInventoryInitiatedFrame(flag, maskLength, maskValue, false, (byte) 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] inventoryInitiated(byte flag, byte maskLength, byte[] maskValue, byte afiField) throws STException {
        return buildInventoryInitiatedFrame(flag, maskLength, maskValue, true, afiField);
    }

    /**
     *
     * @param flag
     * @param maskLength
     * @param maskValue
     * @param isAFISet
     * @param afiField
     * @return status byte
     * @throws STException
     */
    private byte[] buildInventoryInitiatedFrame(byte flag, byte maskLength, byte[] maskValue, boolean isAFISet, byte afiField) throws STException {
        byte[] request;
        int requestOptionSize = 0;
        int index = 0;

        if (maskLength != 0 && maskValue != null) {
            // Compute frame size based on the maskValue length passed a parameter
            requestOptionSize += maskValue.length;
        }

        if (isAFISet) {
            // Optional field
            requestOptionSize++;
        }

        // Frame size = header (flag + cmd) + maskLength + afi (optional) + mask (if maskLength != 0)
        request = new byte[ISO15693_CUSTOM_ST_HEADER_SIZE + 1 + requestOptionSize];

        request[index++] = flag;
        request[index++] = ISO15693_CUSTOM_ST_CMD_INVENTORY_INITIATED;
        request[index++] = STM_MANUFACTURER_CODE;

        if (isAFISet) {
            request[index++] = afiField;
        }
        request[index++] = maskLength;

        if (maskLength != 0 && maskValue != null) {
            // Value is Little Endian
            maskValue = Helper.reverseByteArray(maskValue);
            System.arraycopy(maskValue, 0, request, index, maskValue.length);
        }

        return transceive("inventoryInitiated", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastInitiate(byte flag) throws STException {
        byte[] request;
        int headerSize;

        headerSize = ISO15693_CUSTOM_ST_HEADER_SIZE;

        request = new byte[headerSize];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_FAST_INITIATE;
        request[2] = STM_MANUFACTURER_CODE;

        return transceive("fastInitiate", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastInventoryInitiated(byte flag) throws STException {
        /* Default inventory configuration is:
         *    - Mask length of zero (so mask value doesn't matter)
         *    - No AFI field
         */
        return fastInventoryInitiated(flag, (byte) 0x00, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastInventoryInitiated(byte flag, byte maskLength, byte[] maskValue) throws STException {
        return buildFastInventoryInitiatedFrame(flag, maskLength, maskValue, false, (byte) 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastInventoryInitiated(byte flag, byte maskLength, byte[] maskValue, byte afiField) throws STException {
        return buildFastInventoryInitiatedFrame(flag, maskLength, maskValue, true, afiField);
    }

    /**
     *
     * @param flag
     * @param maskLength
     * @param maskValue
     * @param isAFISet
     * @param afiField
     * @return status byte + Command response
     * @throws STException
     */
    private byte[] buildFastInventoryInitiatedFrame(byte flag, byte maskLength, byte[] maskValue, boolean isAFISet, byte afiField) throws STException {
        byte[] request;
        int requestOptionSize = 0;
        int index = 0;

        if (maskLength != 0 && maskValue != null) {
            // Compute frame size based on the maskValue length passed a parameter
            requestOptionSize += maskValue.length;
        }

        if (isAFISet) {
            // Optional field
            requestOptionSize++;
        }

        // Frame size = header (flag + cmd) + maskLength + afi (optional) + mask (if maskLength != 0)
        request = new byte[ISO15693_CUSTOM_ST_HEADER_SIZE + 1 + requestOptionSize];

        request[index++] = flag;
        request[index++] = ISO15693_CUSTOM_ST_CMD_FAST_INVENTORY_INITIATED;
        request[index++] = STM_MANUFACTURER_CODE;

        if (isAFISet) {
            request[index++] = afiField;
        }
        request[index++] = maskLength;

        if (maskLength != 0 && maskValue != null) {
            // Value is Little Endian
            maskValue = Helper.reverseByteArray(maskValue);
            System.arraycopy(maskValue, 0, request, index, maskValue.length);
        }

        return transceive("fastInventoryInitiated", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] inventoryRead(byte flag, byte blockAddress, byte nbrOfBlocks) throws STException {
        /* Default inventory configuration is:
         *    - Mask length of zero (so mask value doesn't matter)
         *    - No AFI field
         */
        return inventoryRead(flag, (byte) 0x00, null, blockAddress, nbrOfBlocks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] inventoryRead(byte flag, byte maskLength, byte[] maskValue,byte blockAddress, byte nbrOfBlocks) throws STException {
        return buildInventoryReadFrame(flag, maskLength, maskValue, false, (byte) 0x00, blockAddress, nbrOfBlocks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] inventoryRead(byte flag, byte maskLength, byte[] maskValue, byte afiField, byte blockAddress, byte nbrOfBlocks) throws STException {
        return buildInventoryReadFrame(flag, maskLength, maskValue, true, afiField, blockAddress, nbrOfBlocks);
    }

    /**
     *
     * @param flag
     * @param maskLength
     * @param maskValue
     * @param isAFISet
     * @param afiField
     * @param blockAddress
     * @param nbrOfBlocks
     * @return status byte + Command response
     * @throws STException
     */
    private byte[] buildInventoryReadFrame(byte flag, byte maskLength, byte[] maskValue, boolean isAFISet, byte afiField, byte blockAddress, byte nbrOfBlocks) throws STException {
        byte[] request;
        int requestOptionSize = 0;
        int index = 0;

        if (maskLength != 0 && maskValue != null) {
            // Compute frame size based on the maskValue length passed a parameter
            requestOptionSize += maskValue.length;
        }

        if (isAFISet) {
            // Optional field
            requestOptionSize++;
        }

        // Frame size = header (flag + cmd) + maskLength + afi (optional) + mask (if maskLength != 0) + firstBlockNumber + Block Number
        request = new byte[ISO15693_CUSTOM_ST_HEADER_SIZE + 1 + requestOptionSize + 1 + 1];

        request[index++] = flag;
        request[index++] = ISO15693_CUSTOM_ST_CMD_INVENTORY_READ;
        request[index++] = STM_MANUFACTURER_CODE;

        if (isAFISet) {
            request[index++] = afiField;
        }
        request[index++] = maskLength;

        if (maskLength != 0 && maskValue != null) {
            // Value is Little Endian
            maskValue = Helper.reverseByteArray(maskValue);
            System.arraycopy(maskValue, 0, request, index, maskValue.length);
        }

        request[index++] = blockAddress;
        request[index] = nbrOfBlocks;

        return transceive("inventoryRead", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastInventoryRead(byte flag, byte blockAddress, byte nbrOfBlocks) throws STException {
        /* Default inventory configuration is:
         *    - Mask length of zero (so mask value doesn't matter)
         *    - No AFI field
         */
        return fastInventoryRead(flag, (byte) 0x00, null, blockAddress, nbrOfBlocks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastInventoryRead(byte flag, byte maskLength, byte[] maskValue, byte blockAddress, byte nbrOfBlocks) throws STException {
        return buildFastInventoryReadFrame(flag, maskLength, maskValue, false, (byte) 0x00, blockAddress, nbrOfBlocks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastInventoryRead(byte flag, byte maskLength, byte[] maskValue, byte afiField, byte blockAddress, byte nbrOfBlocks) throws STException {
        return buildFastInventoryReadFrame(flag, maskLength, maskValue, true, afiField, blockAddress, nbrOfBlocks);
    }

    /**
     *
     * @param flag
     * @param maskLength
     * @param maskValue
     * @param isAFISet
     * @param afiField
     * @param blockAddress
     * @param nbrOfBlocks
     * @return status byte + Command response
     * @throws STException
     */
    private byte[] buildFastInventoryReadFrame(byte flag, byte maskLength, byte[] maskValue, boolean isAFISet, byte afiField, byte blockAddress, byte nbrOfBlocks) throws STException {
        byte[] request;
        int requestOptionSize = 0;
        int index = 0;

        if (maskLength != 0 && maskValue != null) {
            // Compute frame size based on the maskValue length passed a parameter
            requestOptionSize += maskValue.length;
        }

        if (isAFISet) {
            // Optional field
            requestOptionSize++;
        }

        // Frame size = header (flag + cmd) + maskLength + afi (optional) + mask (if maskLength != 0) + firstBlockNumber + Block Number
        request = new byte[ISO15693_CUSTOM_ST_HEADER_SIZE + 1 + requestOptionSize + 1 + 1];

        request[index++] = flag;
        request[index++] = ISO15693_CUSTOM_ST_CMD_FAST_INVENTORY_READ;
        request[index++] = STM_MANUFACTURER_CODE;

        if (isAFISet) {
            request[index++] = afiField;
        }
        request[index++] = maskLength;

        if (maskLength != 0 && maskValue != null) {
            // Value is Little Endian
            maskValue = Helper.reverseByteArray(maskValue);
            System.arraycopy(maskValue, 0, request, index, maskValue.length);
        }

        request[index++] = blockAddress;
        request[index] = nbrOfBlocks;

        return transceive("fastInventoryRead", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeConfig(byte configId, byte newAttributeValue) throws STException {
        return writeConfig(configId, newAttributeValue, mFlag, mUid);
    }

    /**
     *
     * @param configId
     * @param newAttributeValue
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte writeConfig(byte configId, byte newAttributeValue, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1 + 1]; // +1 for configId and +1 for newAttributeValue

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_WRITE_CONFIG;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = configId;
        request[headerSize + 1] = newAttributeValue;

        try {
            byte[] response = transceive("writeConfig", request);
            return response[0];
        } catch (STException e) {
            // In case of errorCode 0x12, the exception ISO15693_BLOCK_IS_LOCKED is raised.
            // For this writeConfig() command, it indicates that the Config Password is needed.
            if (e.getError() == ISO15693_BLOCK_IS_LOCKED) {
                throw new STException(CONFIG_PASSWORD_NEEDED);
            }

            // Other exceptions are unchanged
            throw(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeDynConfig(byte configId, byte newAttributeValue) throws STException {
        return writeDynConfig(configId, newAttributeValue, mFlag, mUid);
    }

    /**
     *
     * @param configId
     * @param newAttributeValue
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte writeDynConfig(byte configId, byte newAttributeValue, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1 + 1]; // +1 for configId and +1 for newAttributeValue

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_WRITE_DYN_CONFIG;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = configId;
        request[headerSize + 1] = newAttributeValue;

        try {
            byte[] response = transceive("writeDynConfig", request);
            return response[0];
        } catch (STException e) {
            // In case of errorCode 0x12, the exception ISO15693_BLOCK_IS_LOCKED is raised.
            // For this writeDynamicConfig() command, it indicates that the Config Password is needed.
            if (e.getError() == ISO15693_BLOCK_IS_LOCKED) {
                throw new STException(CONFIG_PASSWORD_NEEDED);
            }

            // Other exceptions are unchanged
            throw(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte fastWriteDynConfig(byte configId, byte newAttributeValue) throws STException {
        return fastWriteDynConfig(configId, newAttributeValue, mFlag, mUid);
    }

    /**
     *
     * @param configId
     * @param newAttributeValue
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte fastWriteDynConfig(byte configId, byte newAttributeValue, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);
        request = new byte[headerSize + 1 + 1]; // +1 for configId and +1 for newAttributeValue

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_FAST_WRITE_DYN_CONFIG;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag)) addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = configId;
        request[headerSize + 1] = newAttributeValue;

        try {
            byte[] response = transceive("fastWriteDynConfig", request);
            return response[0];
        } catch (STException e) {
            // In case of errorCode 0x12, the exception ISO15693_BLOCK_IS_LOCKED is raised.
            // For this writeDynamicConfig() command, it indicates that the Config Password is needed.
            if (e.getError() == ISO15693_BLOCK_IS_LOCKED) {
                throw new STException(CONFIG_PASSWORD_NEEDED);
            }

            // Other exceptions are unchanged
            throw(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getRandomNumber() throws STException {
        return getRandomNumber(mFlag, mUid);
    }

    /**
     *
     * @param flag
     * @param uid
     * @return status byte + Command response
     * @throws STException
     */
    public byte[] getRandomNumber(byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int headerSize;


        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_GET_RANDOM_NUMBER;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        response = transceive("getRandomNumber", request);
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readConfig(byte configId) throws STException {
        return readConfig(configId, mFlag, mUid);
    }

    public byte[] readConfig(byte configId, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1];    // +1 for configId

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_READ_CONFIG;
        request[2] = STM_MANUFACTURER_CODE;

        request[headerSize] = configId;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        response = transceive("readConfig", request);
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readDynConfig(byte configId) throws STException {
        return readDynConfig(configId, mFlag, mUid);
    }

    public byte[] readDynConfig(byte configId, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1];    // +1 for configId

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_READ_DYN_CONFIG;
        request[2] = STM_MANUFACTURER_CODE;

        request[headerSize] = configId;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        response = transceive("readDynConfig", request);
        return response;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastReadDynConfig(byte configId) throws STException {
        return fastReadDynConfig(configId, mFlag, mUid);
    }

    /**
     *
     * @param configId
     * @param flag
     * @param uid
     * @return status byte + Command response
     * @throws STException
     */
    public byte[] fastReadDynConfig(byte configId, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1];    // +1 for configId

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_FAST_READ_DYN_CONFIG;
        request[2] = STM_MANUFACTURER_CODE;

        request[headerSize] = configId;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        response = transceive("fastReadDynConfig", request);
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte presentPwd(byte passwordNumber, byte[] password) throws STException {
        return presentPwd(passwordNumber, password, mFlag, mUid);
    }

    /**
     *
     * @param passwordNumber
     * @param password
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte presentPwd(byte passwordNumber, byte[] password, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        if (password == null) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1 + password.length];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_PRESENT_PASSWORD;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = passwordNumber;
        System.arraycopy(password, 0, request, headerSize + 1, password.length);

        byte[] response = transceive("presentPwd", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writePwd(byte passwordNumber, byte[] newPassword) throws STException {
        return writePwd(passwordNumber, newPassword, mFlag, mUid);
    }

    /**
     *
     * @param passwordNumber
     * @param newPassword
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte writePwd(byte passwordNumber, byte[] newPassword, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        if (newPassword == null) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1 + newPassword.length];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_WRITE_PASSWORD;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = passwordNumber;
        System.arraycopy(newPassword, 0, request, headerSize + 1, newPassword.length);

        byte[] response = transceive("writePwd", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte lockSector(byte blockAddress, byte securityStatus) throws STException {
        return lockSector(blockAddress, securityStatus, mFlag, mUid);
    }

    /**
     *
     * @param blockAddress
     * @param securityStatus
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte lockSector(byte blockAddress, byte securityStatus, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        /* sector number 2 bytes
         * security status 1 byte
         */
        request = new byte[headerSize + 1 + 1];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_LOCK_SECTOR;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = blockAddress;
        request[headerSize + 1] = securityStatus;

        response = transceive("lockSector", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public  byte[] fastReadSingleBlock(byte blockAddress) throws STException {
        return fastReadSingleBlock(blockAddress, mFlag, mUid);
    }

    /**
     *
     * @param blockAddress
     * @param flag
     * @param uid
     * @return status byte + Command response
     * @throws STException
     */
    public  byte[] fastReadSingleBlock(byte blockAddress, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_FAST_READ_SINGLE_BLOCK;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = blockAddress;

        return transceive("fastReadSingleBlock", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastReadMultipleBlock(byte blockAddress, byte nbrOfBlocks) throws STException {
        return fastReadMultipleBlock(blockAddress, nbrOfBlocks, mFlag, mUid);
    }

    /**
     *
     * @param blockAddress
     * @param nbrOfBlocks
     * @param flag
     * @param uid
     * @return status byte + Command response
     * @throws STException
     */
    public byte[] fastReadMultipleBlock(byte blockAddress, byte nbrOfBlocks, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);
        request = new byte[headerSize + 1 + 1];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_FAST_READ_MULTIPLE_BLOCK;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag)) addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = blockAddress;
        request[headerSize + 1] = nbrOfBlocks;

        return transceive("fastReadMultipleBlock", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public  byte[] fastExtendedReadSingleBlock(byte[] blockAddress) throws STException {
        return fastExtendedReadSingleBlock(blockAddress, mFlag, mUid);
    }

    /**
     *
     * @param blockAddress
     * @param flag
     * @param uid
     * @return status byte + Command response
     * @throws STException
     */
    public byte[] fastExtendedReadSingleBlock(byte[] blockAddress, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        if ((blockAddress == null)) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 2];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_FAST_EXTENDED_READ_SINGLE_BLOCK;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = blockAddress[1];
        request[headerSize + 1] = blockAddress[0];

        return transceive("fastExtendedReadSingleBlock", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastExtendedReadMultipleBlock(byte[] blockAddress, byte[] nbrOfBlocks) throws STException {
        return fastExtendedReadMultipleBlock(blockAddress, nbrOfBlocks, mFlag, mUid);
    }

    /**
     *
     * @param blockAddress
     * @param nbrOfBlocks
     * @param flag
     * @param uid
     * @return status byte + Command response
     * @throws STException
     */
    public byte[] fastExtendedReadMultipleBlock(byte[] blockAddress, byte[] nbrOfBlocks, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        if ((blockAddress == null) || (blockAddress.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        if ((nbrOfBlocks == null) || (nbrOfBlocks.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693CustomHeaderSize(flag);
        request = new byte[headerSize + 2 + 2];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_FAST_EXTENDED_READ_MULTIPLE_BLOCK;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag)) addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = blockAddress[1];
        request[headerSize + 1] = blockAddress[0];
        request[headerSize + 2] = nbrOfBlocks[1];
        request[headerSize + 3] = nbrOfBlocks[0];

        return transceive("fastExtendedReadMultipleBlock", request);
    }

    /* ---------- M24LR --------------- */

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readCfg() throws STException {
        return readCfg(mFlag, mUid);
    }

    public byte[] readCfg( byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_READ_CONFIG;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        return transceive("readCfg", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeEHCfg(byte data, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        /* Data 1 byte
         */
        request = new byte[headerSize + 1];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_WRITE_EH_CFG;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = data;

        response = transceive("writeEHCfg", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeDOCfg(byte data, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        // Data 1 byte
        request = new byte[headerSize + 1];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_WRITE_DO_CFG;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = data;

        response = transceive("writeDOCfg", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte setRstEHEn(byte data, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        // Data 1 byte
        request = new byte[headerSize + 1];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_SET_RST_EH_EN;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = data;

        response = transceive("setRstEHEn", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] checkEHEn(byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        // Data 1 byte
        request = new byte[headerSize];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_CHECK_EH_EN;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        response = transceive("checkEHEn", request);
        return response;
    }

    /* ---------- ST25DV --------------- */

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeMsg(byte sizeInBytes, byte[] buffer) throws STException {
        return writeMsg(sizeInBytes, buffer, mFlag, mUid);
    }

    /**
     *
     * @param sizeInBytes
     * @param buffer
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte writeMsg(byte sizeInBytes, byte[] buffer, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        if (buffer == null || buffer.length < (sizeInBytes & 0xFF)) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1 + (sizeInBytes & 0xFF) + 1];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_MB_WRITE_MSG;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = sizeInBytes;

        System.arraycopy(buffer, 0, request, headerSize + 1, (sizeInBytes & 0xFF) + 1);

        byte[] response = transceive("writeMsg", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readMsg(byte offset, byte sizeInBytes) throws STException {
        return readMsg(offset, sizeInBytes, mFlag, mUid);
    }

    /**
     *
     * @param offset
     * @param sizeInBytes
     * @param flag
     * @param uid
     * @return status byte + Command response
     * @throws STException
     */
    public byte[] readMsg(byte offset, byte sizeInBytes, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;

        int headerSize;
        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1 + 1];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_MB_READ_MSG;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = offset;
        request[headerSize + 1] = sizeInBytes;

        response = transceive("readMsg", request);

        if (response.length == (sizeInBytes & 0xFF) + 2) {
            // Command successful
            return response;
        }

        throw new STException(CMD_FAILED, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readMsgLength() throws STException {
        return readMsgLength(mFlag, mUid);
    }

    /**
     *
     * @param flag
     * @param uid
     * @return status byte + Command response
     * @throws STException
     */
    public byte[] readMsgLength(byte flag, byte[] uid) throws STException {
        byte[] request;

        int headerSize;
        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_MB_READ_MSG_LENGTH;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);


        return transceive("readMsgLength", request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte fastWriteMsg(byte sizeInBytes, byte[] buffer) throws STException {
        return fastWriteMsg(sizeInBytes, buffer, mFlag, mUid);
    }

    /**
     *
     * @param sizeInBytes
     * @param buffer
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte fastWriteMsg(byte sizeInBytes, byte[] buffer, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        if (buffer == null || buffer.length < (sizeInBytes & 0xFF)) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1 + (sizeInBytes & 0xFF) + 1];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_MB_FAST_WRITE_MSG;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = sizeInBytes;

        System.arraycopy(buffer, 0, request, headerSize + 1, (sizeInBytes & 0xFF) + 1);

        byte[] response = transceive("fastWriteMsg", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastReadMsg(byte offset, byte sizeInBytes) throws STException {
        return fastReadMsg(offset, sizeInBytes, mFlag, mUid);
    }

    /**
     *
     * @param offset
     * @param sizeInBytes
     * @param flag
     * @param uid
     * @return status byte + Command response
     * @throws STException
     */
    public byte[] fastReadMsg(byte offset, byte sizeInBytes, byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;

        int headerSize;
        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1 + 1];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_MB_FAST_READ_MSG;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = offset;
        request[headerSize + 1] = sizeInBytes;

        response = transceive("fastReadMsg", request);

        if (response.length == (sizeInBytes & 0xFF) + 2) {
            // Command successful
            return response;
        }

        throw new STException(CMD_FAILED, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fastReadMsgLength() throws STException {
        return fastReadMsgLength(mFlag, mUid);
    }

    /**
     *
     * @param flag
     * @param uid
     * @return status byte + Command response
     * @throws STException
     */
    public byte[] fastReadMsgLength(byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;

        int headerSize;
        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_MB_FAST_READ_MSG_LENGTH;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        response = transceive("fastReadMsgLength", request);
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte manageGpo(byte value, byte flag, byte[] uid) throws STException {
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_MANAGE_GPO;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = value;

        byte[] response = transceive("manageGpo", request);
        return response[0];
    }

    /* ---------- ST25TV --------------- */

    /**
     * {@inheritDoc}
     */
    @Override
    public byte enableConfidentialMode(byte[] obfuscatedConfidentialPassword)  throws STException {
        return enableConfidentialMode(obfuscatedConfidentialPassword, mFlag, mUid);
    }

    /**
     *
     * @param obfuscatedConfidentialPassword
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte enableConfidentialMode(byte[] obfuscatedConfidentialPassword, byte flag, byte[] uid)  throws STException {
        byte[] request;

        if (obfuscatedConfidentialPassword == null || obfuscatedConfidentialPassword.length != 4) {
            throw new STException(BAD_PARAMETER);
        }

        // UID is mandatory for this command
        request = new byte[]{
                flag,
                ISO15693_CUSTOM_ST_CMD_ENABLE_CONFIDENTIAL_MODE,
                STM_MANUFACTURER_CODE,
                uid[7],
                uid[6],
                uid[5],
                uid[4],
                uid[3],
                uid[2],
                uid[1],
                uid[0],
                ISO15693_CUSTOM_ST_PARAM_CONFIDENTIAL_ACCESS_CODE,
                obfuscatedConfidentialPassword[0],
                obfuscatedConfidentialPassword[1],
                obfuscatedConfidentialPassword[2],
                obfuscatedConfidentialPassword[3]
        };

        byte[] response = transceive("enableConfidentialMode", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte setEas() throws STException {
        return setEas(mFlag, mUid);
    }

    /**
     *
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte setEas(byte flag, byte[] uid)  throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_SET_EAS;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        response = transceive("setEas", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte resetEas()  throws STException {
        return resetEas(mFlag, mUid);
    }

    /**
     *
     * @param flag
     * @param uid
     * @return array of bytes containing status + eventual error code
     * @throws STException
     */
    public byte resetEas(byte flag, byte[] uid)  throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_RESET_EAS;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        response = transceive("resetEas", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte lockEas()  throws STException {
        return lockEas(mFlag, mUid);
    }

    /**
     *
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte lockEas(byte flag, byte[] uid)  throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_LOCK_EAS;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        response = transceive("lockEas", request);
        return response[0];
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] enableEAS()  throws STException {
        return enableEAS(mFlag, mUid);
    }

    /**
     *
     * @param flag
     * @param uid
     * @return status byte + Command response
     * @throws STException
     */
    public byte[] enableEAS(byte flag, byte[] uid)  throws STException {
        byte[] response;
        byte[] request;
        int headerSize;
        // optionFlag: to be set to read the EAS_ID
        byte optionFlag = 0x40; // Bit 7
        byte mask = 0x0;

        headerSize = getIso15693CustomHeaderSize(flag);

        if ((flag & optionFlag) == optionFlag) {
            // Option flag is set: Add room for the EAS ID Mask length
            request = new byte[headerSize + 1];
        } else {
            request = new byte[headerSize];
        }

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_ENABLE_EAS;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        if ((flag & optionFlag) == optionFlag) {
            // Option flag is set: Add the EAS ID Mask length
            request[headerSize] = mask;
        }

        response = transceive("enableEAS", request);
        return response;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeEasId(byte[] easId)  throws STException {
        return writeEasId(easId, mFlag, mUid);
    }

    /**
     *
     * @param easId
     * @param flag
     * @param uid
     * @return status byte
     * @throws STException
     */
    public byte writeEasId(byte[] easId, byte flag, byte[] uid)  throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        if((easId == null) || (easId.length != 2)) {
            throw new STException(BAD_PARAMETER);
        }

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 2];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_WRITE_EAS_ID;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = easId[0];
        request[headerSize+1] = easId[1];

        response = transceive("writeEasId", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte writeEasConfig(byte config)  throws STException {
        return writeEasConfig(config, mFlag, mUid);
    }

    /**
     *
     * @param config
     * @param flag
     * @param uid
     * @return array of bytes containing status + eventual error code
     * @throws STException
     */
    public byte writeEasConfig(byte config, byte flag, byte[] uid)  throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize + 1];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_WRITE_EAS_CONFIG;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        request[headerSize] = config;

        response = transceive("writeEasConfig", request);
        return response[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readSignature() throws STException {
        return readSignature(mFlag, mUid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] readSignature(byte flag, byte[] uid) throws STException {
        byte[] response;
        byte[] request;
        int headerSize;

        headerSize = getIso15693CustomHeaderSize(flag);

        request = new byte[headerSize];

        request[0] = flag;
        request[1] = ISO15693_CUSTOM_ST_CMD_READ_SIGNATURE;
        request[2] = STM_MANUFACTURER_CODE;

        if (uidNeeded(flag))
            addUidToFrame(request, ISO15693_CUSTOM_ST_HEADER_SIZE, uid);

        response = transceive("readSignature", request);
        return response;
    }

}
