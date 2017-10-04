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

package com.st.st25sdk.command;

import com.st.st25sdk.STException;

public class Iso7816Type4RApduStatus {

    public String       mError;
    public STException.STExceptionCode mSTExceptionCode;
    public byte mSW1;
    public byte mSW2;

    private boolean      mStatusError;


    static public boolean mIgnoreSw2 = false;
    // PROCESS COMPLETE
    static final byte SW1_NORMALPROCESSING =(byte)0x90;
    static final byte SW2_NORMALPROCESSING =(byte)0x00;

    static final byte SW1_NORMALPROCESSINGREMAINDATA =(byte)0x61;

    // Warning DEF - PROCESS COMPLETE
    static final byte SW1_WARNINGPROCESSING_NVMUNCHANGED = (byte) 0x62;
    static final byte SW2_WARNTRIGGEDBYCARDB = (byte) 0x02;
    static final byte SW2_WARNTRIGGEDBYCARDE = (byte) 0x80;
    static final byte SW2_WARNTDATAMAYCORRUPT = (byte) 0x81;
    static final byte SW2_WARNEOFREACHED = (byte) 0x82;
    static final byte SW2_WARNSELFILERELEASED = (byte) 0x83;
    static final byte SW2_WARNFILTECTRLERR = (byte) 0x84;


    static final byte SW1_WARNINGPROCESSING_NVMCHANGED = (byte) 0x63;


    // Execution Error
    static final byte SW1_EXECERROR_NVMUNCHANGED = (byte)0x64;

    static final byte SW1_EXECERROR_NVMCHANGED = (byte)0x65;

    static final byte SW1_EXECERROR_SECURITY = (byte)0x66;


    // Checked Error
    static final byte SW1_CHECKERROR_WRONGLENGTH = (byte)0x67;

    static final byte SW1_CHECKERROR_FUNCLANOTSUPPORTED = (byte)0x68;

    static final byte SW1_CHECKERROR_CMDNOTALLOWED = (byte)0x69;

    static final byte SW1_CHECKERROR_WRONGPARAMETER = (byte)0x6A;

    static final byte SW1_CHECKERROR_WRONGPARAMETER2 = (byte)0x6B;

    static final byte SW1_CHECKERROR_WRONGLEFIELD = (byte)0x6C;

    static final byte SW1_CHECKERROR_INSNOTSUPPORTED = (byte)0x6D;

    static final byte SW1_CHECKERROR_CLANOTSUPPORTED = (byte)0x6E;

    static final byte SW1_CHECKERROR_UNKNOWNERR = (byte)0x6F;


    public Iso7816Type4RApduStatus(byte[] response) throws STException {
        parseResponse(response);
    }

    public Iso7816Type4RApduStatus(byte SW1, byte SW2, String meaning, STException.STExceptionCode code)
    {
        mSW1 = SW1;
        mSW2 = SW2;
        mError = meaning;
        mStatusError = true;
        mSTExceptionCode = code;
    }

    static final Iso7816Type4RApduStatus[] ISO_7816_TYPE_4_ERRORS = {
            new Iso7816Type4RApduStatus((byte)0x62, (byte)0x80, "File overflow (Le error)",
                    STException.STExceptionCode.FILE_OVERFLOW),
            new Iso7816Type4RApduStatus((byte)0x62, (byte)0x82, "End of file or record reached before reading Le bytes",
                    STException.STExceptionCode.EOF),
            new Iso7816Type4RApduStatus((byte)0x63, (byte)0x00, "A password is required",
                    STException.STExceptionCode.PASSWORD_NEEDED),
            new Iso7816Type4RApduStatus((byte)0x63, (byte)0xCF, "The password transmitted is incorrect",
                    STException.STExceptionCode.WRONG_PASSWORD),
            new Iso7816Type4RApduStatus((byte)0x65, (byte)0x81, "Unsuccessful updating",
                    STException.STExceptionCode.UPDATE_ERROR),
            new Iso7816Type4RApduStatus((byte)0x67, (byte)0x00, "Wrong length",
                    STException.STExceptionCode.WRONG_LENGTH),
            new Iso7816Type4RApduStatus((byte)0x69, (byte)0x85, "Condition of use not satisfied - (e.g. no NDEF file Was selected",
                    STException.STExceptionCode.INVALID_USE_CONTEXT),
            new Iso7816Type4RApduStatus((byte)0x69, (byte)0x81, "Command Incompatible with file structure",
                    STException.STExceptionCode.INVALID_CMD_FOR_FILE),
            new Iso7816Type4RApduStatus((byte)0x69, (byte)0x82, "Security status not satisfied",
                    STException.STExceptionCode.WRONG_SECURITY_STATUS),
            new Iso7816Type4RApduStatus((byte)0x69, (byte)0x84, "Reference data not usable",
                    STException.STExceptionCode.INVALID_DATA_PARAM),
            new Iso7816Type4RApduStatus((byte)0x6A, (byte)0x80, "Incorrect Parameter in cmd data field",
                    STException.STExceptionCode.INVALID_CMD_PARAM),
            new Iso7816Type4RApduStatus((byte)0x6A, (byte)0x82, "File or Application Not found",
                    STException.STExceptionCode.FILE_APPLICATION_NOT_SUPPORTED),
            new Iso7816Type4RApduStatus((byte)0x6A, (byte)0x86, "Incorrect Parameter P1-P2",
                    STException.STExceptionCode.INVALID_P1_P2),
            new Iso7816Type4RApduStatus((byte)0x6E, (byte)0x00, "Class not supported",
                    STException.STExceptionCode.CLASS_NOT_SUPPORTED),
            new Iso7816Type4RApduStatus((byte)0x6D, (byte)0x00, "INS field not supported",
                    STException.STExceptionCode.INS_FIELD_NOT_SUPPORTED),
            new Iso7816Type4RApduStatus((byte)0x00, (byte)0x01, "Tag Unreacheable",
                    STException.STExceptionCode.TAG_NOT_IN_THE_FIELD), // Android Specific
            new Iso7816Type4RApduStatus((byte)0x00, (byte)0x00, "unsupported Error",
                    STException.STExceptionCode.INVALID_ERROR_CODE)
    };



    private void parseResponse(byte[] response) throws STException {
        if (response.length < 2) {
            throw new STException(STException.STExceptionCode.UNKNOWN_ANSWER, response);
        }

        mSW1 = response[response.length - 2];
        mSW2 = response[response.length - 1];

        if ((mSW1 == (byte)0x90) && ((mSW2 == (byte)0x00) || mIgnoreSw2)) {
            mSTExceptionCode = null;
            mError = "Command Completed";
            mStatusError = false;
            //case mSW2 != 0 but mIgnoreSw2 set
            response[response.length - 1] = 0x00;
            return;
        }

        mStatusError = true;

        int i = 0;
        while (i < ISO_7816_TYPE_4_ERRORS.length && !ISO_7816_TYPE_4_ERRORS[i].equals(this))
            i++;

        if (i == ISO_7816_TYPE_4_ERRORS.length) {
            mSTExceptionCode = STException.STExceptionCode.UNKNOWN_ANSWER;
        }
        else
            mSTExceptionCode = ISO_7816_TYPE_4_ERRORS[i].mSTExceptionCode;
    }

    /**
     *
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && !((mSW1 != ((Iso7816Type4RApduStatus) obj).mSW1) || (mSW2 != ((Iso7816Type4RApduStatus) obj).mSW2));

    }

    /**
     *
     * @return
     */
    public boolean isError() {
        return mStatusError;
    }

    /**
     *
     * @param response
     * @throws STException
     */
    public static void checkError(byte[] response) throws STException {
        Iso7816Type4RApduStatus status = new Iso7816Type4RApduStatus(response);
        if (status.isError()) {
            throw new STException(status.mSTExceptionCode, response);
        }
    }
}
