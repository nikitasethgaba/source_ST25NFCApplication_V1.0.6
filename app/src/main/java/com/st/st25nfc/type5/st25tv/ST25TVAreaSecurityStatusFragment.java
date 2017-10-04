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

package com.st.st25nfc.type5.st25tv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.STFragment;
import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.TagHelper;

public class ST25TVAreaSecurityStatusFragment extends STFragment {

    public MultiAreaInterface myTag = null;

    private View mView;

    // Memory Conf grid
    private final int AREA1_ROW = 1;
    private final int AREA2_ROW = 2;
    private final int READ_CONF_COLUMN = 1;
    private final int WRITE_CONF_COLUMN = 2;

    public static ST25TVAreaSecurityStatusFragment newInstance(Context context) {
        ST25TVAreaSecurityStatusFragment f = new ST25TVAreaSecurityStatusFragment();
        /* If needed, pass some argument to the fragment
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        */

        // Set the title of this fragment
        f.setTitle(context.getResources().getString(R.string.configuration));

        return f;
    }

    public ST25TVAreaSecurityStatusFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_st25tv_area_security_status, container, false);

        mView = view;
        fillView();

        Button changePermissionsButton = (Button) mView.findViewById(R.id.changePermissionsButton);
        changePermissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ST25TVChangePermissionsActivity.class);
                startActivity(intent);
            }
        });

        Button presentPasswordsButton = (Button) mView.findViewById(R.id.presentPasswordsButton);
        presentPasswordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ST25TVPresentPwdActivity.class);
                startActivity(intent);
            }
        });

        Button changePasswordsButton = (Button) mView.findViewById(R.id.changePasswordsButton);
        changePasswordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ST25TVChangePwdActivity.class);
                startActivity(intent);
            }
        });

        return (View) view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getAreasInformation();
    }

    /**
     * Get the number of areas and the permission of each one.
     */
    private void getAreasInformation() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // Retrieve the number of areas and the permissions of each one
                    final boolean isMemoryConfiguredInSingleArea = (myTag.getNumberOfAreas() > 1 ) ? false : true;

                    final TagHelper.ReadWriteProtection area1ReadWriteProtection = myTag.getReadWriteProtection(1);
                    final TagHelper.ReadWriteProtection area2ReadWriteProtection = myTag.getReadWriteProtection(2);

                    // Update the GridArray (this should be done by the UI thread)
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            refreshGridArrayDisplay(isMemoryConfiguredInSingleArea, area1ReadWriteProtection, area2ReadWriteProtection);
                        }
                    });

                } catch (STException e) {
                    switch (e.getError()) {
                        case TAG_NOT_IN_THE_FIELD:
                            showToast(R.string.tag_not_in_the_field);
                            break;
                        default:
                            e.printStackTrace();
                            showToast(R.string.error_while_reading_the_tag);
                    }
                }
            }
        }).start();
    }


    private void refreshGridArrayDisplay(boolean isMemoryConfiguredInSingleArea,
                                         TagHelper.ReadWriteProtection area1ReadWriteProtection,
                                         TagHelper.ReadWriteProtection area2ReadWriteProtection) {
        TextView commonTxtView;
        int nbrOfMemoryAreas;

        commonTxtView = (TextView) mView.findViewById(R.id.memConfTextView);
        if (isMemoryConfiguredInSingleArea) {
            commonTxtView.setText(R.string.one_area);
            nbrOfMemoryAreas = 1;
        } else {
            commonTxtView.setText(R.string.two_areas);
            nbrOfMemoryAreas = 2;
        }

        GridLayout gridLayout = (GridLayout) mView.findViewById(R.id.memConfGridLayout);

        gridLayout.removeAllViews();
        gridLayout.setColumnCount(3);
        gridLayout.setRowCount(nbrOfMemoryAreas + 1); // +1 because there is the table header

        // Add header on row 0
        addTextToGridLayoutCell(gridLayout, 0, READ_CONF_COLUMN, getResources().getString(R.string.read_permission));
        addTextToGridLayoutCell(gridLayout, 0, WRITE_CONF_COLUMN, getResources().getString(R.string.write_permission));

        // Build "Area 1" information
        addTextToGridLayoutCell(gridLayout, AREA1_ROW, 0, getResources().getString(R.string.area1));
        switch (area1ReadWriteProtection) {
            case READABLE_AND_WRITABLE:
                addTextToGridLayoutCell(gridLayout, AREA1_ROW, READ_CONF_COLUMN, getResources().getString(R.string.not_protected));
                addTextToGridLayoutCell(gridLayout, AREA1_ROW, WRITE_CONF_COLUMN, getResources().getString(R.string.not_protected));
                break;
            case READABLE_AND_WRITE_PROTECTED_BY_PWD:
                addTextToGridLayoutCell(gridLayout, AREA1_ROW, READ_CONF_COLUMN, getResources().getString(R.string.not_protected));
                addTextToGridLayoutCell(gridLayout, AREA1_ROW, WRITE_CONF_COLUMN, getResources().getString(R.string.pwd_protected));
                break;
            case READ_AND_WRITE_PROTECTED_BY_PWD:
                addTextToGridLayoutCell(gridLayout, AREA1_ROW, READ_CONF_COLUMN, getResources().getString(R.string.pwd_protected));
                addTextToGridLayoutCell(gridLayout, AREA1_ROW, WRITE_CONF_COLUMN, getResources().getString(R.string.pwd_protected));
                break;
            case READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE:
                addTextToGridLayoutCell(gridLayout, AREA1_ROW, READ_CONF_COLUMN, getResources().getString(R.string.pwd_protected));
                addTextToGridLayoutCell(gridLayout, AREA1_ROW, WRITE_CONF_COLUMN, getResources().getString(R.string.permanently_protected));
                break;
        }

        if (nbrOfMemoryAreas > 1) {
            // Build "Area 2" information
            addTextToGridLayoutCell(gridLayout, AREA2_ROW, 0, getResources().getString(R.string.area2));

            switch (area2ReadWriteProtection) {
                case READABLE_AND_WRITABLE:
                    addTextToGridLayoutCell(gridLayout, AREA2_ROW, READ_CONF_COLUMN, getResources().getString(R.string.not_protected));
                    addTextToGridLayoutCell(gridLayout, AREA2_ROW, WRITE_CONF_COLUMN, getResources().getString(R.string.not_protected));
                    break;
                case READABLE_AND_WRITE_PROTECTED_BY_PWD:
                    addTextToGridLayoutCell(gridLayout, AREA2_ROW, READ_CONF_COLUMN, getResources().getString(R.string.not_protected));
                    addTextToGridLayoutCell(gridLayout, AREA2_ROW, WRITE_CONF_COLUMN, getResources().getString(R.string.pwd_protected));
                    break;
                case READ_AND_WRITE_PROTECTED_BY_PWD:
                    addTextToGridLayoutCell(gridLayout, AREA2_ROW, READ_CONF_COLUMN, getResources().getString(R.string.pwd_protected));
                    addTextToGridLayoutCell(gridLayout, AREA2_ROW, WRITE_CONF_COLUMN, getResources().getString(R.string.pwd_protected));
                    break;
                case READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE:
                    addTextToGridLayoutCell(gridLayout, AREA2_ROW, READ_CONF_COLUMN, getResources().getString(R.string.pwd_protected));
                    addTextToGridLayoutCell(gridLayout, AREA2_ROW, WRITE_CONF_COLUMN, getResources().getString(R.string.permanently_protected));
                    break;
            }
        }
    }

    private void addTextToGridLayoutCell(GridLayout gridLayout, int x, int y, String txt) {

        TextView myTextView = new TextView(getActivity());
        myTextView.setText(txt);
        //myTextView.setBackgroundColor(0x888888);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.setGravity(Gravity.CENTER);
        //layoutParams.setGravity(Gravity.FILL_HORIZONTAL);
        //layoutParams.setGravity(Gravity.RIGHT);
        layoutParams.rightMargin = 20;
        layoutParams.topMargin = 20;
        layoutParams.columnSpec = GridLayout.spec(y);
        layoutParams.rowSpec = GridLayout.spec(x);

        gridLayout.addView(myTextView, layoutParams);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public void onAttach(Context context) {
        super.onAttach(context);
        myTag = (MultiAreaInterface) ((STFragmentListener) context).getTag();

    }

    public void fillView() {
        class ContentView implements Runnable {

            TextView mTagNameTextView;
            TextView mTagDescriptionTextView;
            TextView mTagTypeTextView;

            String mTagName;
            String mTagDescription;
            String mTagType;

            private void fillView() {

                if (myTag != null) {
                    mTagName = ((NFCTag) myTag).getName();
                    mTagDescription = ((NFCTag)myTag).getDescription();
                    mTagType = ((NFCTag)myTag).getTypeDescription();
                }
                if (mView != null) {
                    mTagNameTextView = (TextView) mView.findViewById(R.id.model_header);
                    mTagTypeTextView = (TextView) mView.findViewById(R.id.model_type);
                    mTagDescriptionTextView = (TextView) mView.findViewById(R.id.model_description);
                }
            }

            @Override
            public void run() {
                fillView();

                if(mView != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mTagNameTextView.setText(mTagName);
                            mTagTypeTextView.setText(mTagDescription);
                            mTagDescriptionTextView.setText(mTagType);
                        }
                    });
                }
            }
        }

        new Thread(new ContentView()).start();
    }

}


