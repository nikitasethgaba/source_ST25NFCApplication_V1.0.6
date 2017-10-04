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
import com.st.st25sdk.type5.ReadBlockResult;

public interface VicinityMemoryCommandInterface {

    /**
     * Read function with Block granularity
     *
     * If the option bit is set on the flag parameter, the Block Security Status
     * byte array of ReadBlockResult is filled with the blockSecurityStatus byte
     * value for each requested block:
     *   blockSecurityStatus = 0 for unlocked block
     *   blockSecurityStatus = 1 for awrite-locked block (read only)
     *   blockSecurityStatus = FF if not read
     *
     * @param firstBlockAddress
     * @param sizeInBlocks
     * @return
     * @throws STException
     */
    ReadBlockResult readBlocks(int firstBlockAddress, int sizeInBlocks) throws STException;

    /**
     * Read function with Block granularity
     *
     * If the option bit is set on the flag parameter, the Block Security Status
     * byte array of ReadBlockResult is filled with the blockSecurityStatus byte
     * value for each requested block:
     *   blockSecurityStatus = 0 for unlocked block
     *   blockSecurityStatus = 1 for awrite-locked block (read only)
     *   blockSecurityStatus = FF if not read
     *
     * @param firstBlockAddress
     * @param sizeInBlocks
     * @param flag
     * @param uid
     * @return
     * @throws STException
     */
    ReadBlockResult readBlocks(int firstBlockAddress, int sizeInBlocks, byte flag, byte[] uid) throws STException;

    /**
     * Write function with Block granularity
     * @param firstBlockAddress
     * @param data
     * @throws STException
     */
    void writeBlocks(int firstBlockAddress, byte[] data) throws STException;

    /**
     * Write function with Block granularity
     * @param firstBlockAddress
     * @param data
     * @param flag
     * @param uid
     * @throws STException
     */
    void writeBlocks(int firstBlockAddress, byte[] data, byte flag, byte[] uid) throws STException;

    /**
     * Read function with Byte granularity.
     *
     * @param byteAddress
     * @param sizeInBytes
     * @return
     * @throws STException
     */
    byte[] readBytes(int byteAddress, int sizeInBytes) throws STException;

    /**
     * Read function with Byte granularity.
     *
     * If the option bit is set on the flag parameter, readBytes() will ignore it.
     * To read the BSS byte, use readBlocks() instead.
     *
     * @param byteAddress
     * @param sizeInBytes
     * @param flag
     * @param uid
     * @return
     * @throws STException
     */
    byte[] readBytes(int byteAddress, int sizeInBytes, byte flag, byte[] uid) throws STException;

    /**
     * Write function with Byte granularity
     * @param byteAddress
     * @param data
     * @throws STException
     */
    void writeBytes(int byteAddress, byte[] data) throws STException;

    /**
     * Write function with Byte granularity
     * @param byteAddress
     * @param data
     * @param flag
     * @param uid
     * @throws STException
     */
    void writeBytes(int byteAddress, byte[] data, byte flag, byte[] uid) throws STException;

}
