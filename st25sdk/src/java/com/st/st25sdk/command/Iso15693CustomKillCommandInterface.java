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

public interface Iso15693CustomKillCommandInterface {

    /**
     * Use this command to kill the tag.
     *
     * WARNING: This action is irreversible, it will not be possible to recover the tag.
     *
     * @param unencryptedKillCode
     * @return status byte
     * @throws STException
     */
    byte kill(byte[] unencryptedKillCode) throws STException;

    /**
     * Command allowing to change the kill password.
     *
     * WARNING: the kill password should then be locked by calling lockKill()
     *
     * @param unencryptedKillPassword
     * @return status byte
     * @throws STException
     */
    byte writeKill(byte[] unencryptedKillPassword) throws STException;

    /**
     * Command allowing to lock the kill password. It is no more possible to change it.
     * @return status byte
     * @throws STException
     */
    byte lockKill() throws STException;

}
