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

package com.st.st25nfc.type5.st25dv;

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
import com.st.st25sdk.STException;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.TagHelper.ReadWriteProtection;
import com.st.st25sdk.type5.ST25DVTag;

import static com.st.st25sdk.MultiAreaInterface.AREA1;
import static com.st.st25sdk.MultiAreaInterface.AREA2;
import static com.st.st25sdk.MultiAreaInterface.AREA3;
import static com.st.st25sdk.MultiAreaInterface.AREA4;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READABLE_AND_WRITABLE;

public class ST25DVAreaSecurityStatusFragment extends STFragment {

    public ST25DVTag myTag = null;

    private View mView;

    // Memory Conf grid
    private final int AREA1_ROW = 1;
    private final int AREA2_ROW = 2;
    private final int AREA3_ROW = 3;
    private final int AREA4_ROW = 4;

    private final int READ_CONF_COLUMN = 1;
    private final int WRITE_CONF_COLUMN = 2;

    public static ST25DVAreaSecurityStatusFragment newInstance(Context context) {
        ST25DVAreaSecurityStatusFragment f = new ST25DVAreaSecurityStatusFragment();
        /* If needed, pass some argument to the fragment
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        */

        // Set the title of this fragment
        f.setTitle(context.getResources().getString(R.string.area_security_status));

        return f;
    }

    public ST25DVAreaSecurityStatusFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_st25dv_area_security_status, container, false);

        mView = view;
        fillView();

        Button changePwdPermissionsButton = (Button) mView.findViewById(R.id.changePwdPermissionsButton);
        changePwdPermissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ST25DVChangeAreasPasswordActivity.class);
                startActivity(intent);
            }
        });

        Button changePermissionsButton = (Button) mView.findViewById(R.id.changePermissionsButton);
        changePermissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ST25DVChangePermissionsActivity.class);
                startActivity(intent);
            }
        });

        Button presentPasswordsButton = (Button) mView.findViewById(R.id.presentPasswordsButton);
        presentPasswordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ST25DVPresentPwdActivity.class);
                startActivity(intent);
            }
        });

        Button changePasswordsButton = (Button) mView.findViewById(R.id.changePasswordsButton);
        changePasswordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ST25DVChangePwdActivity.class);
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
                    final int numberOfAreas = myTag.getNumberOfAreas();
                    boolean multipleArea = false;
                    if (numberOfAreas >1) {
                        multipleArea = true;
                    }
                    final boolean isMemoryConfiguredInSingleArea = multipleArea;
                    TagHelper.ReadWriteProtection a1ss = READABLE_AND_WRITABLE;
                    TagHelper.ReadWriteProtection a2ss = READABLE_AND_WRITABLE;
                    ReadWriteProtection a3ss = READABLE_AND_WRITABLE;
                    ReadWriteProtection a4ss = READABLE_AND_WRITABLE;
                    for (int area = AREA1; area <= numberOfAreas; area++) {
                        switch(area) {
                            case 1:
                                a1ss = myTag.getReadWriteProtection(area);
                                break;
                            case 2:
                                a2ss = myTag.getReadWriteProtection(area);
                                break;
                            case 3:
                                a3ss = myTag.getReadWriteProtection(area);
                                break;
                            case 4:
                                a4ss = myTag.getReadWriteProtection(area);
                                break;
                        }

                    }

                    final ReadWriteProtection area1ReadWriteProtection = a1ss;
                    final ReadWriteProtection area2ReadWriteProtection = a2ss;
                    final ReadWriteProtection area3ReadWriteProtection = a3ss;
                    final TagHelper.ReadWriteProtection area4ReadWriteProtection = a4ss;

                    // Update the GridArray (this should be done by the UI thread)
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            refreshGridArrayDisplay(numberOfAreas, area1ReadWriteProtection, area2ReadWriteProtection
                                    , area3ReadWriteProtection, area4ReadWriteProtection);
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

    private int getAreaRow(int area) {
        int marea = AREA1;
        switch (area) {
            case 1:
                marea = AREA1_ROW;
                break;
            case 2:
                marea = AREA2_ROW;
                break;
            case 3:
                marea = AREA3_ROW;
                break;
            case 4:
                marea = AREA4_ROW;
                break;
        }
        return marea;
    }

    private ReadWriteProtection getAreaSecurityStatus(int area, ReadWriteProtection area1ReadWriteProtection,
                                                      ReadWriteProtection area2ReadWriteProtection,
                                                      TagHelper.ReadWriteProtection area3ReadWriteProtection,
                                                      TagHelper.ReadWriteProtection area4ReadWriteProtection) {
        TagHelper.ReadWriteProtection areaSS = area1ReadWriteProtection;
        switch(area) {
            case AREA1: areaSS = area1ReadWriteProtection;
                break;
            case AREA2: areaSS = area2ReadWriteProtection;
                break;
            case AREA3: areaSS = area3ReadWriteProtection;
                break;
            case AREA4: areaSS = area4ReadWriteProtection;
                break;
        }
        return areaSS;
    }

    private void refreshGridArrayDisplay(int nbAreas,
                                         TagHelper.ReadWriteProtection area1ReadWriteProtection,
                                         ReadWriteProtection area2ReadWriteProtection,
                                         TagHelper.ReadWriteProtection area3ReadWriteProtection,
                                         TagHelper.ReadWriteProtection area4ReadWriteProtection) {
        TextView commonTxtView;
        int nbrOfMemoryAreas = nbAreas;

        commonTxtView = (TextView) mView.findViewById(R.id.memConfTextView);
        commonTxtView.setText(" " + nbAreas + " Areas");

        GridLayout gridLayout = (GridLayout) mView.findViewById(R.id.memConfGridLayout);

        gridLayout.removeAllViews();
        gridLayout.setColumnCount(3);
        gridLayout.setRowCount(nbrOfMemoryAreas + 1); // +1 because there is the table header

        // Add header on row 0
        addTextToGridLayoutCell(gridLayout, 0, READ_CONF_COLUMN, getResources().getString(R.string.read_permission));
        addTextToGridLayoutCell(gridLayout, 0, WRITE_CONF_COLUMN, getResources().getString(R.string.write_permission));

        // Build "Area i" information
        for(int area = AREA1; area <= nbrOfMemoryAreas; area++) {
            addTextToGridLayoutCell(gridLayout, getAreaRow(area), 0, "Area " + getAreaRow(area));
            switch (getAreaSecurityStatus(area, area1ReadWriteProtection, area2ReadWriteProtection, area3ReadWriteProtection, area4ReadWriteProtection)) {
                case READABLE_AND_WRITABLE:
                    addTextToGridLayoutCell(gridLayout, getAreaRow(area), READ_CONF_COLUMN, getResources().getString(R.string.not_protected));
                    addTextToGridLayoutCell(gridLayout, getAreaRow(area), WRITE_CONF_COLUMN, getResources().getString(R.string.not_protected));
                    break;
                case READABLE_AND_WRITE_PROTECTED_BY_PWD:
                    addTextToGridLayoutCell(gridLayout, getAreaRow(area), READ_CONF_COLUMN, getResources().getString(R.string.not_protected));
                    addTextToGridLayoutCell(gridLayout, getAreaRow(area), WRITE_CONF_COLUMN, getResources().getString(R.string.pwd_protected));
                    break;
                case READ_AND_WRITE_PROTECTED_BY_PWD:
                    addTextToGridLayoutCell(gridLayout, getAreaRow(area), READ_CONF_COLUMN, getResources().getString(R.string.pwd_protected));
                    addTextToGridLayoutCell(gridLayout, getAreaRow(area), WRITE_CONF_COLUMN, getResources().getString(R.string.pwd_protected));
                    break;
                case READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE:
                    addTextToGridLayoutCell(gridLayout, getAreaRow(area), READ_CONF_COLUMN, getResources().getString(R.string.pwd_protected));
                    addTextToGridLayoutCell(gridLayout, getAreaRow(area), WRITE_CONF_COLUMN, getResources().getString(R.string.permanently_protected));
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
        myTag = (ST25DVTag) ((STFragmentListener) context).getTag();

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
                    mTagName = myTag.getName();
                    mTagDescription = myTag.getDescription();
                    mTagType = myTag.getTypeDescription();
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


