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

package com.st.st25nfc.type5.st25dv02kw;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.STFragment;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.type5.ST25DV02KWTag;

import static com.st.st25nfc.type5.st25dv02kw.ST25DV02KWPwmCtrlFragment.Pwm.PWM1;
import static com.st.st25nfc.type5.st25dv02kw.ST25DV02KWPwmCtrlFragment.Pwm.PWM2;

public class ST25DV02KWPwmCtrlFragment extends STFragment {

    public ST25DV02KWTag myTag = null;
    static final String TAG = "PwmCtrlConfig";

    private View mView;
    private byte[] mPwm1Control;
    private byte[] mPwm2Control;


    RadioButton mPwm1EnableButton;
    private boolean mPwm1IsPushed;
    private EditText mPwm1FreqEditText;
    private EditText mPwm1DutyCycleEditText;
    private TextView mPwm1ResolutionEditText;
    private TextView mPwm1PeriodTextView;
    private TextView mPwm1PulseWidthTextView;

    RadioButton mPwm2EnableButton;
    private boolean mPwm2IsPushed;
    private EditText mPwm2FreqEditText;
    private EditText mPwm2DutyCycleEditText;
    private TextView mPwm2ResolutionEditText;
    private TextView mPwm2PeriodTextView;
    private TextView mPwm2PulseWidthTextView;

    public enum Pwm {PWM1, PWM2};

    public static ST25DV02KWPwmCtrlFragment newInstance(Context context) {
        ST25DV02KWPwmCtrlFragment f = new ST25DV02KWPwmCtrlFragment();
        /* If needed, pass some argument to the fragment
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        */

        // Set the title of this fragment
        f.setTitle(context.getResources().getString(R.string.configuration));


        return f;
    }

    public ST25DV02KWPwmCtrlFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_st25dv02kw_pwm_ctrl, container, false);

        mView = view;

        mPwm1EnableButton = (RadioButton) mView.findViewById(R.id.EnableValueRadioButton);
        mPwm1IsPushed = false;
        mPwm1EnableButton.setChecked(mPwm1IsPushed);
        mPwm1EnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPwm1IsPushed)
                    mPwm1IsPushed = false;
                else
                    mPwm1IsPushed = true;
                mPwm1EnableButton.setChecked(mPwm1IsPushed);
                updateRegisterValues(PWM1);
            }
        });

        mPwm2EnableButton = (RadioButton) mView.findViewById(R.id.EnableValueRadioButton2);
        mPwm2IsPushed = false;
        mPwm2EnableButton.setChecked(mPwm2IsPushed );
        mPwm2EnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPwm2IsPushed)
                    mPwm2IsPushed = false;
                else
                    mPwm2IsPushed = true;
                mPwm2EnableButton.setChecked(mPwm2IsPushed);
                updateRegisterValues(PWM2);
            }
        });


        mPwm1FreqEditText = (EditText) mView.findViewById(R.id.FreqValueEditText);
        mPwm2FreqEditText = (EditText) mView.findViewById(R.id.FreqValueEditText2);
        mPwm1DutyCycleEditText = (EditText) mView.findViewById(R.id.DutyCycleValueEditText);
        mPwm2DutyCycleEditText = (EditText) mView.findViewById(R.id.DutyCycleValueEditText2);
        mPwm1ResolutionEditText = (TextView) mView.findViewById(R.id.ResolutionValueTextView);
        mPwm2ResolutionEditText = (TextView) mView.findViewById(R.id.ResolutionValueTextView2);
        mPwm1PeriodTextView = (TextView) mView.findViewById(R.id.PeriodValueTextView);
        mPwm2PeriodTextView = (TextView) mView.findViewById(R.id.PeriodValueTextView2);
        mPwm1PulseWidthTextView = (TextView) mView.findViewById(R.id.PulseWidthValueTextView);
        mPwm2PulseWidthTextView = (TextView) mView.findViewById(R.id.PulseWidthValueTextView2);


        mPwm1FreqEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateRegisterValues(PWM1);
            }
        });

        mPwm1DutyCycleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateRegisterValues(PWM1);
            }
        });


        mPwm2FreqEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateRegisterValues(PWM2);
            }
        });

        mPwm2DutyCycleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateRegisterValues(PWM2);
            }
        });

        mPwm1Control = new byte[4];
        mPwm2Control = new byte[4];
        fillView();
        return (View) view;
    }

    private void updateRegisterValues(Pwm  pwm)
    {
        boolean pwmEnable;
        TextView periodToUpdate;
        TextView pulseWidthToUpdate;
        int freq;
        int dutyCycle;
        String text;

        switch (pwm) {
            case PWM1:
                periodToUpdate = mPwm1PeriodTextView;
                pulseWidthToUpdate = mPwm1PulseWidthTextView;
                pwmEnable = mPwm1EnableButton.isChecked() ? true:false;
                text  = mPwm1FreqEditText.getText().toString();
                if (!text.equals(""))
                    try {
                        freq = Integer.parseInt(text);
                    }
                    catch (Exception e) {
                        STLog.e("Bad frequence" + e.getMessage());
                        showToast("Incorrect frequence");
                        return;
                    }
                else
                    freq = 0;
                text  = mPwm1DutyCycleEditText.getText().toString();
                if (!text.equals(""))
                    try {
                        dutyCycle = Integer.parseInt(text);
                    }
                    catch (Exception e) {
                        STLog.e("Bad dutyCycle" + e.getMessage());
                        showToast("Incorrect dutyCycle");
                        return;
                    }
                else
                    dutyCycle = 0;
                break;
            case PWM2:
                periodToUpdate = mPwm2PeriodTextView;
                pulseWidthToUpdate = mPwm2PulseWidthTextView;
                pwmEnable = mPwm2EnableButton.isChecked() ? true:false;
                text = mPwm2FreqEditText.getText().toString();
                if (!text.equals(""))
                    try {
                        freq = Integer.parseInt(text);
                    }
                    catch (Exception e) {
                        STLog.e("Bad frequence" + e.getMessage());
                        showToast("Incorrect frequence");
                        return;
                    }
                else
                    freq = 0;
                text = mPwm2DutyCycleEditText.getText().toString();
                if (!text.equals(""))
                    try {
                        dutyCycle = Integer.parseInt(text);
                    }
                    catch (Exception e) {
                        STLog.e("Bad dutyCycle" + e.getMessage());
                        showToast("Incorrect dutyCycle");
                        return;
                    }
                else
                    dutyCycle = 0;
                break;
            default:
                return;
        }

        if ((dutyCycle >100 || dutyCycle < 0) || (freq > 31250 || freq < 488)) {
            setNaPeriodPulseWidth(periodToUpdate, pulseWidthToUpdate);
            return;
        }

        int period = (int) (1000000000/(freq*ST25DV02KWTag.ST25DV02KW_PWM_RESOLUTION_NS));
        int pulseWidth = period * dutyCycle /100;



        if (pwm == PWM1) {
            myTag.computeControlFromPeriodAndPulseWidth(mPwm1Control, period, pulseWidth, pwmEnable);
        }
        else {
            myTag.computeControlFromPeriodAndPulseWidth(mPwm2Control, period, pulseWidth, pwmEnable);
        }

        periodToUpdate.setText(String.format("0x%x", period));
        pulseWidthToUpdate.setText(String.format("0x%x", pulseWidth));

    };


    private void setNaPeriodPulseWidth(TextView periodView, TextView pulseWidthView) {
        periodView.setText("NA");
        pulseWidthView.setText("NA");
    }
    @Override
    public void onResume() {
        super.onResume();
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
        myTag = (ST25DV02KWTag) ((STFragmentListener) context).getTag();

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
                    mTagDescription = ((NFCTag) myTag).getDescription();
                    mTagType = ((NFCTag) myTag).getTypeDescription();
                    try {
                        initRegisterValues(PWM1);
                        initRegisterValues(PWM2);
                    }
                    catch (STException e) {
                        mPwm2ResolutionEditText.setText(String.format("%d", ST25DV02KWTag.ST25DV02KW_PWM_RESOLUTION_NS));
                    }
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

                if (mView != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mTagNameTextView.setText(mTagName);
                            mTagTypeTextView.setText(mTagDescription);
                            mTagDescriptionTextView.setText(mTagType);

                            int period = myTag.computePeriodFromControl(mPwm1Control);
                            int pulseWidth = myTag.computePulseWidthFromControl(mPwm1Control);
                            int freq = myTag.computeFreqFromControl(mPwm1Control);
                            int dutyCycle = myTag.computeDutyCycleFromControl(mPwm1Control);

                            mPwm1IsPushed = myTag.isPwmEnable(mPwm1Control);
                            mPwm1EnableButton.setChecked(mPwm1IsPushed);

                            mPwm1ResolutionEditText.setText(String.format("%f", ST25DV02KWTag.ST25DV02KW_PWM_RESOLUTION_NS));
                            mPwm1FreqEditText.setText(String.format("%d", freq));
                            mPwm1DutyCycleEditText.setText(String.format("%d", dutyCycle));
                            mPwm1PeriodTextView.setText(String.format("0x%x", period));
                            mPwm1PulseWidthTextView.setText(String.format("0x%x", pulseWidth));


                            period = myTag.computePeriodFromControl(mPwm2Control);
                            pulseWidth = myTag.computePulseWidthFromControl(mPwm2Control);
                            freq = myTag.computeFreqFromControl(mPwm2Control);
                            dutyCycle = myTag.computeDutyCycleFromControl(mPwm2Control);

                            mPwm2IsPushed = myTag.isPwmEnable(mPwm2Control);
                            mPwm2EnableButton.setChecked(mPwm2IsPushed);

                            mPwm2EnableButton.setChecked(mPwm2IsPushed);
                            mPwm2ResolutionEditText.setText(String.format("%f", ST25DV02KWTag.ST25DV02KW_PWM_RESOLUTION_NS));
                            mPwm2FreqEditText.setText(String.format("%d", freq));
                            mPwm2DutyCycleEditText.setText(String.format("%d", dutyCycle));
                            mPwm2PeriodTextView.setText(String.format("0x%x", period));
                            mPwm2PulseWidthTextView.setText(String.format("0x%x", pulseWidth));

                        }
                    });
                }
            }
        }

        new Thread(new ContentView()).start();
    }

    public void updateTag()  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    myTag.writePwm1Control(mPwm1Control);
                    myTag.writePwm2Control(mPwm2Control);
                }
                catch (STException e) {
                    showToast("Failed update pwm controls");
                    return;
                }
                showToast("pwm controls update success");
            }
        }).start();

    }

    private void initRegisterValues(Pwm pwm) throws STException {
        byte[] pwmControl = null;

        try {
            if (pwm == PWM1) {
                pwmControl = mPwm1Control;
                mPwm1Control = myTag.readPwm1Control();
            } else if (pwm == PWM2) {
                pwmControl = mPwm2Control;
                mPwm2Control = myTag.readPwm2Control();
            }
        }
        catch (STException e) {
            showToast("Failed to initialize pwm control");
            if (pwmControl != null) {
                for (int i = 0; i < pwmControl.length; i++)
                    pwmControl[i] = 0x00;
            }
        }
    }
}


