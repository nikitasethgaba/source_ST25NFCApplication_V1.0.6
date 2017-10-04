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

package com.st.st25nfc.type4;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.STFragment;
import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.type4a.Type4Tag;

import static com.st.st25nfc.type4.AreasListFragment.ActionStatus.AREA_PROTECTED_BY_PWD;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READABLE_AND_WRITABLE;

public class AreasListFragment extends STFragment implements AdapterView.OnItemClickListener {

    RadioButton mSingleAreaRadioButton;
    RadioButton mTwoAreasRadioButton;
    RadioButton mThreeAreasRadioButton;
    RadioButton mFourAreasRadioButton;
    RadioButton mFiveAreasRadioButton;
    RadioButton mSixAreasRadioButton;
    RadioButton mSevenAreasRadioButton;
    RadioButton mEightAreasRadioButton;

    MultiAreaInterface mMultiAreaInterface;

    enum ActionStatus {
        ACTION_SUCCESSFUL,
        ACTION_FAILED,
        TAG_NOT_IN_THE_FIELD,
        AREA_PROTECTED_BY_PWD
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.activity_stm24ta_areas_config, container, false);

        Button updateTagButton = (Button) mView.findViewById(R.id.updateTagButton);
        updateTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askUserConfirmation();
            }
        });

        mSingleAreaRadioButton = (RadioButton) mView.findViewById(R.id.singleAreaRadioButton);
        mTwoAreasRadioButton = (RadioButton) mView.findViewById(R.id.twoAreasRadioButton);
        mThreeAreasRadioButton = (RadioButton) mView.findViewById(R.id.threeAreasRadioButton);
        mFourAreasRadioButton = (RadioButton) mView.findViewById(R.id.fourAreasRadioButton);
        mFiveAreasRadioButton = (RadioButton) mView.findViewById(R.id.fiveAreasRadioButton);
        mSixAreasRadioButton = (RadioButton) mView.findViewById(R.id.sixAreasRadioButton);
        mSevenAreasRadioButton = (RadioButton) mView.findViewById(R.id.sevenAreasRadioButton);
        mEightAreasRadioButton = (RadioButton) mView.findViewById(R.id.eightAreasRadioButton);

        // This activity is only launched for tag implementing MultiAreaInterface
        mMultiAreaInterface = (MultiAreaInterface) myTag;

        int nbOfAreas;
        try {
            nbOfAreas = mMultiAreaInterface.getNumberOfAreas();
        } catch (STException e) {
            nbOfAreas = 1;
        }

        switch (nbOfAreas) {
            case 1:
                mSingleAreaRadioButton.setChecked(true);
                break;
            case 2:
                mTwoAreasRadioButton.setChecked(true);
                break;
            case 3:
                mThreeAreasRadioButton.setChecked(true);
                break;
            case 4:
                mFourAreasRadioButton.setChecked(true);
                break;
            case 5:
                mFiveAreasRadioButton.setChecked(true);
                break;
            case 6:
                mSixAreasRadioButton.setChecked(true);
                break;
            case 7:
                mSevenAreasRadioButton.setChecked(true);
                break;
            case 8:
                mEightAreasRadioButton.setChecked(true);
                break;
        }
        return mView;
    }




    private void askUserConfirmation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle("Confirmation needed");

        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure you want to update the memory mapping ? Data will be lost")
                .setCancelable(true)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        setNumberOfAreas();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void setNumberOfAreas() {
        int nbrOfAreas;

        // We have checked that no area is protected. We can now change the number of areas
        if (mSingleAreaRadioButton.isChecked()) {
            nbrOfAreas = 1;
        } else if (mTwoAreasRadioButton.isChecked()) {
            nbrOfAreas = 2;
        } else if (mThreeAreasRadioButton.isChecked()) {
            nbrOfAreas = 3;
        } else if (mFourAreasRadioButton.isChecked()) {
            nbrOfAreas = 4;
        } else if (mFiveAreasRadioButton.isChecked()) {
            nbrOfAreas = 5;
        } else if (mSixAreasRadioButton.isChecked()) {
            nbrOfAreas = 6;
        } else if (mSevenAreasRadioButton.isChecked()) {
            nbrOfAreas = 7;
        } else if (mEightAreasRadioButton.isChecked()) {
            nbrOfAreas = 8;
        } else {
            // invalid number of areas
            return;
        }

        new asyncTaskSetNbrOfAreas().execute(nbrOfAreas);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    private class asyncTaskSetNbrOfAreas extends AsyncTask<Integer, Void, ActionStatus> {
        int mNbrOfAreas;

        public asyncTaskSetNbrOfAreas() {

        }

        @Override
        protected ActionStatus doInBackground(Integer... param) {
            ActionStatus result = ActionStatus.ACTION_FAILED;
            mNbrOfAreas = param[0];

            try {

                // It is possible to change the number of areas only if all areas are unprotected
                int currentNbrOfAreas = mMultiAreaInterface.getNumberOfAreas();
                for(int area = 1; area <= currentNbrOfAreas; area++) {
                    TagHelper.ReadWriteProtection readWriteProtection = mMultiAreaInterface.getReadWriteProtection(area);

                    if(readWriteProtection != READABLE_AND_WRITABLE) {
                        return AREA_PROTECTED_BY_PWD;
                    }
                }

                mMultiAreaInterface.setNumberOfAreas(mNbrOfAreas);

                ((Type4Tag) myTag).invalidateCache();
                showToast(R.string.tag_updated);

                result = ActionStatus.ACTION_SUCCESSFUL;

            } catch (STException e) {
                switch (e.getError()) {
                    case TAG_NOT_IN_THE_FIELD:
                        showToast(R.string.tag_not_in_the_field);
                        break;
                    default:
                        e.printStackTrace();
                        showToast(R.string.error_while_updating_the_tag);
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case ACTION_SUCCESSFUL:
                    showToast(R.string.tag_updated);
                    break;

                case AREA_PROTECTED_BY_PWD:
                    // Inform the user that it is not possible to change the number of area while some areas are protected
                    displayInformationPopup();
                    break;

                case ACTION_FAILED:
                    showToast(R.string.error_while_reading_the_tag);
                    break;

                case TAG_NOT_IN_THE_FIELD:
                    showToast(R.string.tag_not_in_the_field);
                    break;
            }

            return;
        }

        private void displayInformationPopup() {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            // set title
            alertDialogBuilder.setTitle("Warning");

            // set dialog message
            alertDialogBuilder
                    .setMessage("The number of areas cannot be changed while some areas are protected in read or in write. Please remove the area protections first.")
                    .setCancelable(true)
                    .setPositiveButton("Continue",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });

            // create alert dialog


            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }

    }


}
