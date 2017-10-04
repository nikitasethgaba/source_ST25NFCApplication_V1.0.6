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

package com.st.st25sdk.type4a;

public class ControlTlv {

    public byte mType;
    public int mLength;
    public int mFileIdentifier;
    public int mMaxNdefFileSize; //2 bytes
    public byte mReadAccess; //1 byte
    public byte mWriteAccess; //1 byte

    public static final byte PERMANENTLY_LOCKED = (byte) 0xFF;
    public static final byte LOCKED_BY_PASSWORD = (byte) 0x80;

    public static ControlTlv newInstance (byte[] buffer) {
        if (buffer.length != 0x08)
            return null;
        ControlTlv tlv = new ControlTlv(buffer[0], buffer[1] & 0xFF);
        tlv.parse(buffer);

        return tlv;
    }


    public ControlTlv(byte type, int length) {
        mType = type; //defined by Type 4
        mLength = length; //defined by Type 4
    }

    public void parse(byte[] buffer) {
        mFileIdentifier = ((buffer[2] & 0xFF) << 8) + (buffer[3] & 0xFF);
        mMaxNdefFileSize = ((buffer[4] & 0xFF) << 8) + (buffer[5] & 0xFF);
        mReadAccess = buffer[6];
        mWriteAccess = buffer[7];

    }

    public byte getType() { return mType;}

    public int getLength() { return mLength;}

    public int getFileId() { return mFileIdentifier;}

    public int getMaxFileSize() { return mMaxNdefFileSize;}

    public byte getReadAccess() { return mReadAccess;}

    public byte getWriteAccess() { return mWriteAccess;}

}
