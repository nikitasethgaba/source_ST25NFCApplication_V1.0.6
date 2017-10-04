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

public interface STVicinityConfigInterface {
    
    /**
     * readCfg command as defined in M24LR specification.
     * @return 1 byte response for status + 1 byte for the Configuration byte
     * @throws STException
     */
    public byte[] readCfg() throws STException;
    
   
    /**
     * Write EHCfg command as defined in M24LR specification.
     * @param data Data to write.
     * @return 1 byte response for status
     * @throws STException
     */
    public byte writeEHCfg(byte data) throws STException;
    
   
    /**
     * Write DOCfg command as defined in M24LR specification.
     * @param data Data to write.
     * @return 1 byte response for status
     * @throws STException
     */
    public byte writeDOCfg(byte data) throws STException;
    
    
    /**
     * Set RstEHEn command as defined in M24LR specification.
     * @param data Data to write.  0=Reset 1=Set
     * @return 1 byte response for status
     * @throws STException
     */
    public byte setRstEHEn(byte data) throws STException;
    
    /**
     * Check EHEn command as defined in M24LR specification.
     * @return checkEnable data response
     * @return array of bytes = 1 byte response flag + 1 byte data
     * @throws STException
     */
    public byte[] checkEHEn() throws STException;
}
