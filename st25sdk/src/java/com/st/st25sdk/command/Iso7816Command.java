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

import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;

import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;

public class Iso7816Command {

    public static final byte ISO7816_CMD_VERIFY = (byte) 0x20;
    public static final byte ISO7816_CMD_CHANGE_REF_DATA = (byte) 0x24;
    public static final byte ISO7816_CMD_DISABLE_VERIFY_REQ = (byte) 0x26;
    public static final byte ISO7816_CMD_ENABLE_VERIFY_REQ = (byte) 0x28;

    public static final int ISO7816_HEADER_SIZE = 4;

    public static byte[] OK = new byte[] {(byte) 0x90, (byte) 0x00};

    protected RFReaderInterface mReaderInterface;
    static final boolean DBG = true;


    public Iso7816Command(RFReaderInterface reader) {
        mReaderInterface = reader;
    }

    /**
     *
     * @param p1
     * @param p2
     * @param data
     * @return
     * @throws STException
     */
    public byte[] verify(byte cla, byte p1, byte p2, byte[] data) throws STException {
        byte[] frame;

        //Check if pwd is present
        if (data != null) {
            frame = new byte[data.length + ISO7816_HEADER_SIZE + 1];
            frame[4] = 0x10;
            System.arraycopy(data, 0, frame, 5, data.length);

        }
        else {
            frame = new byte[ISO7816_HEADER_SIZE + 1];
            frame[4] = 0;
        }

        frame[0] = cla;
        frame[1] = ISO7816_CMD_VERIFY;
        frame[2] = p1;
        frame[3] = p2;

        return transceive("Verify ", frame);
    }

    /**
     *
     * @param p1
     * @param p2
     * @param data
     * @return
     * @throws STException
     */
    public byte[] changeReferenceData(byte cla, byte p1, byte p2, byte[] data) throws STException {
        if (data == null)
            throw new STException(STException.STExceptionCode.BAD_PARAMETER);

        byte[] frame = new byte[data.length + ISO7816_HEADER_SIZE + 1];

        frame[0] = cla;
        frame[1] = ISO7816_CMD_CHANGE_REF_DATA;
        frame[2] = p1;
        frame[3] = p2;
        frame[4] = 0x10;

        System.arraycopy(data, 0, frame, 5, data.length);

        return transceive("Change reference data", frame);
    }

    /**
     *
     * @param p1
     * @param p2
     * @return
     * @throws STException
     */
    public byte[] enableVerificationReq(byte cla, byte p1, byte p2) throws STException {
        byte[] frame = new byte[ISO7816_HEADER_SIZE];

        frame[0] = cla;
        frame[1] = ISO7816_CMD_ENABLE_VERIFY_REQ;
        frame[2] = p1;
        frame[3] = p2;

        return transceive("Enable Verification Req ", frame);
    }

    /**
     *
     * @param p1
     * @param p2
     * @return
     * @throws STException
     */
    public byte[] disableVerificationReq(byte cla, byte p1, byte p2) throws STException {
        byte[] frame = new byte[ISO7816_HEADER_SIZE];

        frame[0] = cla;
        frame[1] = ISO7816_CMD_DISABLE_VERIFY_REQ;
        frame[2] = p1;
        frame[3] = p2;

        return transceive("Disable Verification Req ", frame);
    }


    public byte[] transceive(String commandName, byte[] data) throws STException {
        try {
            byte[] response = mReaderInterface.transceive(this.getClass().getSimpleName(), commandName, data);
            // Catch Iso errors
            Iso7816Type4RApduStatus.checkError(response);
            return response;
        } catch(Exception e) {
            // Catch all Java exceptions
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }
    }

}
