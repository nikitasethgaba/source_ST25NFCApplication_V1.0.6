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

import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.type5.STType5PasswordInterface.PasswordLength;

/**
 * Interface specific to Tags supporting several memory areas
 */
public interface MultiAreaInterface {

    // Areas are numbered from 1 to N
    int AREA1 = 1;
    int AREA2 = 2;
    int AREA3 = 3;
    int AREA4 = 4;

    /**
     * Returns the maximum number of areas supported on a tag.
     *
     * @return
     */
    int getMaxNumberOfAreas();

    /**
     * Returns the current number of areas defined on a tag.
     *
     * @return
     */
    int getNumberOfAreas() throws STException;

    /**
     * Defines a number of areas on a tag.
     *
     */
    void setNumberOfAreas(int nbOfAreas) throws STException;

    /**
     * Returns the size in bytes of a given area.
     *
     * @param area
     * @return
     * @throws STException
     */
    int getAreaSizeInBytes(int area) throws STException;

    /**
     * Returns the first byte address of the area.
     *
     * @param area
     * @return
     * @throws STException
     */
    int getAreaOffsetInBytes(int area) throws STException;


    /**
     * Returns the first block address of the area.
     *
     * @param area
     * @return
     * @throws STException
     */
    int getAreaOffsetInBlocks(int area) throws STException;

    /**
     * Returns the area number for a given block offset.
     *
     * @param blockOffset
     * @return area number of the specified block address
     * @throws STException
     */
    int getAreaFromBlockAddress(int blockOffset) throws STException;

    /**
     *
     * @param area
     * @return
     * @throws STException
     */
    NDEFMsg readNdefMessage(int area) throws STException;

    /**
     *
     * @param area
     * @param msg
     * @throws STException
     */
    void writeNdefMessage(int area, NDEFMsg msg) throws STException;


    /**
     * Returns an enum describing the access rights defined for a specific area.
     *
     * @param area
     * @return
     * @throws STException
     */
    TagHelper.ReadWriteProtection getReadWriteProtection(int area) throws STException;

    /**
     * Sets the access rights for the specified area.
     *
     * @param area
     * @param protection
     * @throws STException
     */
    void setReadWriteProtection(int area, TagHelper.ReadWriteProtection protection) throws STException;

    /**
     * Sets the access rights for the specified area.
     * The password is used to authorize the modification of the settings of this Area.
     *
     * @param area
     * @param protection
     * @param password Configuration password
     * @throws STException
     */
    void setReadWriteProtection(int area, TagHelper.ReadWriteProtection protection, byte[] password) throws STException;

    /**
     * Returns the length of the password used by the specified area
     * @param area
     * @return
     * @throws STException BAD_PARAMETER if area does not exist or if it is not protected by a password
     */
    PasswordLength getAreaPasswordLength(int area) throws STException;

}
