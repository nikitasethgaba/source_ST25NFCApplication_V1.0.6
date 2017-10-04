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

import java.util.Arrays;

public class STException extends Throwable {

    public enum STExceptionCode {
        INVALID_ERROR_CODE,
        INVALID_DATA,
        INVALID_CCFILE,
        INVALID_NDEF_DATA,
        NDEF_MESSAGE_TOO_BIG,
        BAD_PARAMETER,
        TAG_CMD_CALLED_FROM_UI_THREAD,
        TAG_NOT_IN_THE_FIELD,
        CONFIG_PASSWORD_NEEDED,
        INVALID_BLOCK_ADDRESS,
        MEMORY_ALLOCATION_FAILURE,
        CMD_FAILED,
        FILE_EMPTY,
        CONNECTION_ERROR,
        NOT_IMPLEMENTED,
        NOT_SUPPORTED,
        CRC_ERROR,
        RFREADER_FAILURE,
        BLOCK_LOCKED,
        ISO15693_CMD_NOT_SUPPORTED,
        ISO15693_CMD_NOT_RECOGNIZED,
        ISO15693_CMD_OPTION_NOT_SUPPORTED,
        ISO15693_BLOCK_NOT_AVAILABLE,
        ISO15693_BLOCK_ALREADY_LOCKED,
        ISO15693_BLOCK_IS_LOCKED,
        ISO15693_BLOCK_PROGRAMMING_FAILED,
        ISO15693_BLOCK_LOCKING_FAILED,
        ISO15693_BLOCK_PROTECTED,

        //Type 4/Iso exceptions
        INS_FIELD_NOT_SUPPORTED,
        CLASS_NOT_SUPPORTED,
        INVALID_P1_P2,
        FILE_APPLICATION_NOT_SUPPORTED,
        INVALID_CMD_PARAM,
        INVALID_DATA_PARAM,
        DATA_NOT_USABLE,
        WRONG_SECURITY_STATUS,
        INVALID_CMD_FOR_FILE,
        INVALID_USE_CONTEXT,
        WRONG_LENGTH,
        UPDATE_ERROR,
        WRONG_PASSWORD,
        PASSWORD_NEEDED,
        EOF,
        FILE_OVERFLOW,
        UNKNOWN_ANSWER,
        PERMANENTLY_LOCKED,
        TRANSCEIVE_EVAL_MODE,
    }

    Exception mException;
    private STExceptionCode mErrorCode = STExceptionCode.INVALID_ERROR_CODE;
    private byte[] mErrorData = null;



    /**
     * Create STException from a String error message
     * @param msg
     */
    public STException(String msg) {
        mException = new Exception((msg));
    }

    /**
     * Create STException from a Java exception
     * @param e
     */
    public STException(Exception e) {
        super(e);
        mException = e;
    }

    /**
     * Create STException from an STException error code
     * @param errorCode
     */
    public STException(STExceptionCode errorCode) {
        mException = null;
        mErrorCode = errorCode;
    }

    /**
     * Create STException from an STException error code
     * Pass error data as parameter (Iso response bytes for example)
     *
     * @param errorCode
     * @param errorData
     */
    public STException(STExceptionCode errorCode, byte[] errorData) {
        this(errorCode);
        mErrorData = Arrays.copyOf(errorData, errorData.length);
    }

    /**
     *
     */
    @Override
    public String getMessage() {
        if (mException != null) {
            return mException.getMessage();
        } else {
            // Exception raised with an error code
            // TODO: Create a hash table converting each error code into a string
            //       For the moment we only display the error number
            return "Error " + mErrorCode;
        }
    }

    /**
     * Return the error code associated with the STException
     * @return
     */
    public STExceptionCode getError() {
        return mErrorCode;
    }

    /**
     * Return the error data associated with the STException or null if no data
     * @return
     */
    public byte[] getErrorData() {
        return mErrorData;
    }
}
