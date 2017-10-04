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

package com.st.st25nfc.generic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import com.st.st25nfc.generic.util.UIHelper;
import com.st.st25nfc.generic.util.UIHelper.STFragmentId;

import java.util.List;


public class STPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private List<STFragmentId> mSTFragmentId;
    private static final String TAG = "STPagerAdapter";

    public STPagerAdapter(FragmentManager fm, Context context, List<STFragmentId>  stFragmentId) {
        super(fm);
        mContext = context;
        mSTFragmentId = stFragmentId;

        //Log.v(TAG, "Creation of a STPagerAdapter with " + mSTFragmentId.size() + " pages");
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        fragment = UIHelper.getSTFragment(mContext, mSTFragmentId.get(i));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        if (mSTFragmentId == null) {
            Log.e(TAG, "Invalid mSTFragmentId!");
            return 0;
        }

        return mSTFragmentId.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title;

        title = UIHelper.getSTFragment(mContext, mSTFragmentId.get(position)).getTitle();
        return title;
    }
}





