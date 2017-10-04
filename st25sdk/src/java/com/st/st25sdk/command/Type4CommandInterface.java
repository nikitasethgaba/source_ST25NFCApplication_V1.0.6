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

public interface Type4CommandInterface {

    /**
     * Function allowing to select a file by FileId.
     *
     * Warning: It is the caller responsibility to use a lock to prevent simultaneous accesses
     *          to the tag.
     *
     * @param fileId   File identifier
     * @return
     * @throws STException
     */
    byte[] selectFile(int fileId) throws STException;

    /**
     * @param p1   param as defined by Type 4
     * @param p2   param as defined by Type 4
     * @param data File identifier
     * @return array of bytes R-apdu
     * @throws STException
     */
    byte[] select(byte p1, byte p2, byte[] data) throws STException;

    /**
     * @param p1     param as defined by Type 4
     * @param p2     param as defined by Type 4
     * @param length to read
     * @return array of bytes R-apdu
     * @throws STException
     */
    byte[] readBinary(byte p1, byte p2, byte length) throws STException;

    /**
     * @param p1   param as defined by Type 4
     * @param p2   param as defined by Type 4
     * @param data data to write
     * @return array of bytes R-apdu
     * @throws STException
     */
    byte[] updateBinary(byte p1, byte p2, byte[] data) throws STException;

    /**
     *
     * @return array of bytes R-apdu
     * @throws STException
     */
    byte[] selectNdefTagApplication() throws STException;

    /**
     * @return array of bytes R-apdu
     * @throws STException
     */
    byte[] selectNdef() throws STException;

}
