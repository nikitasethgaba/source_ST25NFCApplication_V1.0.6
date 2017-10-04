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

import java.util.ArrayList;
import java.util.List;

public class TagCache
{
    private boolean mCacheActivated = true;

    private class CachableObject {
        private Object mObj;
        private String mObjecName;
        private boolean mCacheActivated;
        private boolean mCacheInvalidated;

        public CachableObject(Object obj, String objecName) {
            mObj = obj;
            mObjecName = objecName;
            mCacheActivated = true;
            //For optims reasons when an object is created to be put in the cache it is valid
            mCacheInvalidated = false;
        }

        public String getObjecName() {
            return mObjecName;
        }

    }

    // Enable this boolean if you want to see a trace every times the cache is updated
    public static boolean DBG_CACHE_MANAGER = false;

    private List<CacheInterface> mCacheInterface;
    private List<CachableObject> mCachableObjects;


    public TagCache() {
        mCacheInterface = new ArrayList<>();
        mCachableObjects = new ArrayList<>();
    }

    public void add(Object obj) {
        if (obj == null) return;

        if (contains(obj)) return;

        if (obj instanceof CacheInterface) {
            mCacheInterface.add((CacheInterface) obj);
        } else {
            String objectName = obj.toString();
            mCachableObjects.add(new CachableObject(obj, objectName));
        }

        if(DBG_CACHE_MANAGER) {
            show_cache_content();
        }
    }


    public void remove(Object obj) {
        if (obj == null) return;

        if (obj instanceof CacheInterface) {
            for (CacheInterface it : mCacheInterface) {
                if (it == obj) {
                    mCacheInterface.remove(it);

                    if(DBG_CACHE_MANAGER) {
                        show_cache_content();
                    }

                    return;
                }
            }
        }
        else {
            for (CachableObject it : mCachableObjects) {
                if (it.mObj == obj) {
                    // Warning: 'it' is the object encapsulating 'obj'. This 'it' that should be removed from the list
                    mCachableObjects.remove(it);

                    if(DBG_CACHE_MANAGER) {
                        show_cache_content();
                    }

                    return;
                }
            }
        }

        if(DBG_CACHE_MANAGER) {
            show_cache_content();
        }
    }

    public boolean contains(Object obj) {
        if (obj == null) return false;

        for(CachableObject it :mCachableObjects) {
            if (it.mObj == obj) {
                return true;
            }
        }

        for(CacheInterface it :mCacheInterface) {
            if (it == obj) {
                return true;
            }
        }
        return false;
    }

    public void invalidateCache(Object obj) {
        if (obj == null) return;

        for (CachableObject it : mCachableObjects) {
            if (it.mObj == obj) {
                it.mCacheInvalidated = true;
                return;
            }
        }
        for(CacheInterface it :mCacheInterface) {
            if (it == obj) {
                it.invalidateCache();
                return;
            }
        }
    }

    public void invalidateCache() {
        for(CacheInterface it :mCacheInterface) {
            it.invalidateCache();
        }

        for (CachableObject it : mCachableObjects) {
            it.mCacheInvalidated = true;
        }
    }

    public void validateCache(Object obj) {
        if (obj == null) return;

        for (CachableObject it : mCachableObjects) {
            if (it.mObj == obj) {
                it.mCacheInvalidated = false;
                return;
            }
        }
        for(CacheInterface it :mCacheInterface) {
            if (it == obj) {
                it.validateCache();
                return;
            }
        }
    }

    public void validateCache() {
        for(CacheInterface it :mCacheInterface) {
            it.validateCache();
        }

        for (CachableObject it : mCachableObjects) {
            it.mCacheInvalidated = false;
        }
    }

    public void activateCache() {
        for(CachableObject it :mCachableObjects) {
            it.mCacheActivated = true;
        }
        for(CacheInterface it :mCacheInterface) {
            it.activateCache();
        }
        mCacheActivated = true;
    }


    public void activateCache(Object obj) {
        if (obj == null) return;

        for (CachableObject it : mCachableObjects) {
            if (it.mObj == obj) {
                it.mCacheActivated = true;
                return;
            }
        }
        for(CacheInterface it :mCacheInterface) {
            if (it == obj) {
                it.activateCache();
                return;
            }
        }
    }

    public void deactivateCache() {
        for(CachableObject it :mCachableObjects) {
            it.mCacheActivated = false;
        }
        for(CacheInterface it :mCacheInterface) {
            it.deactivateCache();
        }
        mCacheActivated = false;
    }

    public void deactivateCache(Object obj)  {
        if (obj == null) return;

        for (CachableObject it : mCachableObjects) {
            if (it.mObj == obj) {
                it.mCacheActivated = false;
                return;
            }
        }
        for(CacheInterface it :mCacheInterface) {
            if (it == obj) {
                it.deactivateCache();
                return;
            }
        }
    }

    public void updateCache() throws STException {
        for(CacheInterface it :mCacheInterface) {
            it.updateCache();
        }

        for (CachableObject it : mCachableObjects) {
                it.mCacheInvalidated = true;
        }
    }

    private void show_cache_content() {

        STLog.w(" ");
        STLog.w("\n\nCachableObjects:");
        for (CachableObject obj : mCachableObjects) {
            STLog.w(obj.getObjecName());
        }

        STLog.w("CacheInterface:");
        for(CacheInterface obj :mCacheInterface) {
            STLog.w(obj.toString());
        }
    }

    public boolean isCacheValid(Object obj) {
        if (obj == null) return false;

        for (CachableObject it : mCachableObjects) {
            if (it.mObj == obj) {
                return !it.mCacheInvalidated;
            }
        }
        for(CacheInterface it :mCacheInterface) {
            if (it == obj) {
                return it.isCacheValid();
            }
        }
        return false;
    }

    public boolean isCacheValid() {
        for (CachableObject it : mCachableObjects) {
            if (it.mCacheInvalidated)
                return false;
        }
        for(CacheInterface it :mCacheInterface) {
            if (!it.isCacheValid())
                return false;
        }
        return true;
    }

    public boolean isCacheActivated(Object obj) {
        if (obj == null) return false;

        for (CachableObject it : mCachableObjects) {
            if (it.mObj == obj) {
                return it.mCacheActivated;
            }
        }
        for(CacheInterface it :mCacheInterface) {
            if (it == obj) {
                return it.isCacheActivated();
            }
        }
        return false;
    }

    public boolean isCacheActivated() {
        return mCacheActivated;
    }
}
