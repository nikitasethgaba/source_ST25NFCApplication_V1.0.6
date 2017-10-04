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


public interface Type4CustomCommandInterface {

    /**
     *
     * @param p1 0x01 enable the read protection of NDEF
     * @param p2 0x02 enable the write protection of NDEF
     * @return array of bytes R-apdu
     * @throws STException
     */
    byte[] enablePermanentState(byte p1, byte p2) throws STException;

    /**
     *
     * @param p1 0x01 disable the read protection of NDEF
     * @param p2 0x02 disable the write protection of NDEF
     * @return array of bytes R-apdu
     * @throws STException
     */
    byte[] disablePermanentState(byte p1, byte p2) throws STException;


    /**
     * This command is similar to readBinary but can read beyond the length of selected file.
     * @param p1 offset in the file selected
     * @param p2 offset in the file selected
     * @return array of data read or exception if an error occurs
     */
    byte[] extendedReadBinary(byte p1, byte p2, byte length) throws STException;

    /**
     * This command allows the user to modify the file type of a selected file
     * to Proprietary File (0x05) or NDEF File (0x04). This command is executed
     * only when the application and the file are selected and access right have
     * previously been set to 0x00
     * @param data 0x04 or 0x05
     * @return the R APDU
     */
    byte[] updateFileType(byte data) throws STException;

    /**
     * Generates a negative pulse on the GPO pin. Starts @end of the RF cmd and
     * ends @start of the RF response
     * @return array of bytes R-apdu
     * @throws STException
     */
    byte[] sendInterrupt() throws STException;


    /**
     *
     * @param data 0x00 the GPO is set low. 0x01 the GPO is released
     * @return array of bytes R-apdu
     * @throws STException
     */
    byte[] setStateControl(byte data) throws STException;

    /**
     * Update the counter configuration in System file
     * @param counterConfigurationValue Counter configuration byte value
     * @return array of bytes R-apdu
     * @throws STException
     */
    byte[] setConfigCounter(byte counterConfigurationValue) throws STException;

    }
