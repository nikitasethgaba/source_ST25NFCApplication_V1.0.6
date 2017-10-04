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

import java.util.List;

/**
 * Register interface
 *
 */
public interface RegisterInterface {

    /**
     * Get List of static registers
     * @return List of STRegister object
     */
    List<STRegister> getRegisterList();

    /**
     * Get List of Dynamic registers
     * @return List of STRegister object
     */
    List<STRegister> getDynamicRegisterList();

    /**
     * Get static register object
     * @param registerAddress register Address
     * @return STRegister object
     */
    STRegister getRegister(int registerAddress);
}
