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

public interface CacheInterface {


    /**
     * Invalidate the cache. Force an update
     */
    void invalidateCache();

    /**
     * Make valid the cache.
     */
    void validateCache();

    /**
     * Activate the cache for the object
     */
    void activateCache();

    /**
     * Deactivate the cache for the object
     */
    void deactivateCache();

    /**
     * Force an update of cache when cache activated.
     * Cache has to be activated or the function has no effect.
     * @throws STException {@link}STException
     */
    void updateCache() throws STException;

    /**
     *
     * @return false if cache is invalidated
     */
    boolean isCacheValid();

    /**
     *
     * @return true if cache is activated
     */
    boolean isCacheActivated();

}
