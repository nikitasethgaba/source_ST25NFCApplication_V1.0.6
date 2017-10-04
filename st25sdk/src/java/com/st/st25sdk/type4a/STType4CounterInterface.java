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

package com.st.st25sdk.type4a;

import com.st.st25sdk.STException;

public interface STType4CounterInterface {

    /**
     * Get the configuration byte used for the counter
     * @return configuration byte
     * @throws STException
     */
    byte getEventCounter() throws STException;

    /**
     * Get the counter bytes
     * @return byte array of the counter
     * @throws STException
     */
    byte[] getCounterBytes() throws STException;

    /**
     * Get the counter value
     * @return int counter value
     * @throws STException
     */
    int getCounterValue() throws STException;

    /**
     * Lock the counter
     * @throws STException
     */
    void lockCounter() throws STException;

    /**
     * Check if counter is locked
     * @return
     * @throws STException
     */
    boolean isCounterLocked() throws STException;

    /**
     * Enable counter functionality
     * @throws STException
     */
    void enableCounter() throws STException;

    /**
     * Check if counter is enabled
     * @throws STException
     */
    boolean isCounterEnabled() throws STException;

    /**
     * Disable counter functionality
     * @throws STException
     */
    void disableCounter() throws STException;

    /**
     * Set the counter in Read mode
     * @throws STException
     */
    void incrementCounterOnRead() throws STException;
    boolean isCounterIncrementedOnRead() throws STException;

    /**
     * Set the counter in Write mode
     * @throws STException
     */
    void incrementCounterOnWrite() throws STException;
    boolean isCounterIncrementedOnWrite() throws STException;
}
