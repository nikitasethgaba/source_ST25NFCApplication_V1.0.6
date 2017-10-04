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

package com.st.st25sdk.type5;

import com.st.st25sdk.STException;

public interface STType5PasswordInterface {

    enum PasswordLength {
        PWD_ON_32_BITS,
        PWD_ON_64_BITS,
        PWD_ON_96_BITS,
        PWD_ON_128_BITS
    }


    /**
     * Presents a byte[] value for a given password
     * @param passwordNumber
     * @param password password[0] contains the MSByte
     * @throws STException
     */
    void presentPassword(byte passwordNumber, byte[] password) throws STException;


    /**
     * Changes the password corresponding to a given passwordNumber.
     * Usually a correct password must be presented for this command to be successful.
     * @param passwordNumber
     * @param newPassword newPassword[0] contains the MSByte
     * @throws STException
     */
    void writePassword(byte passwordNumber, byte[] newPassword) throws STException;


    /**
     * Returns the length of the specified password
     * @param passwordNumber
     * @throws STException
     */
    PasswordLength getPasswordLength(byte passwordNumber) throws STException;


    /**
     * Indicates the passwordNumber to use to change the tag's configuration
     * @return
     */
    byte getConfigurationPasswordNumber() throws STException;


    /**
     * Returns the number of the password assigned to a selected area.
     *
     * @param Area or sector number
     * @return The identifier of the password (not the password itself)
     *         0: area/sector is not protected by a password
     *         n: area/sector is protected by password #n
     * @throws STException
     */
    byte getPasswordNumber(int area) throws STException;


    /**
     * Assigns a password to an area.
     * The configuration password must be successfully presented beforehand
     *
     * @param Area or sector number
     * @param passwordNumber The identifier of the password (not the password itself).
     * @throws STException
     */
    void setPasswordNumber(int area, byte passwordNumber) throws STException;

}
