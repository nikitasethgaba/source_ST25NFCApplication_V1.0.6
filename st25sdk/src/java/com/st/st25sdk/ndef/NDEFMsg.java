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

package com.st.st25sdk.ndef;


import com.st.st25sdk.STLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class describing the content of one NDEF message.
 * This NDEF message can contain a few NDEF records.
 */
//public class NDEFMsg implements Parcelable {
public class NDEFMsg implements Serializable {

    protected List<NDEFRecord> mNDEFRecords;

    public static final byte NDEF_IDENTIFIER = 0x03;
    public static final byte NDEF_TERMINATOR = (byte) 0xFE;

    private String invalid_data = "Invalid ndef data";

    public NDEFMsg(byte[] buffer) throws Exception {
        mNDEFRecords = new ArrayList<>();
        if (buffer != null)
            parseRecords(buffer, 0);
    }

    public NDEFMsg() {
        mNDEFRecords = new ArrayList<>();
    }

    public NDEFMsg(NDEFRecord record) {
        mNDEFRecords = new ArrayList<>();
        mNDEFRecords.add(0, record);
    }

    public List<NDEFRecord> getNDEFRecords() {
        return mNDEFRecords;
    }

    public int getNbrOfRecords() {
        return mNDEFRecords.size();
    }

    private void parseNDEFMsg(byte[] buffer) {
        //init(0);
    }

    public NDEFRecord getNDEFRecord(int position) {
        return mNDEFRecords.get(position);
    }

    public String getPayload() throws Exception {
        String msg = "";

        for (NDEFRecord r : mNDEFRecords) {
            msg += new String(r.getPayload());
        }

        return msg;
    }

    public byte[] serialize() throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        for (NDEFRecord record : mNDEFRecords) {
            byte[] data;

            data = record.serialize();

            if (data != null)
                buffer.write(data, 0, data.length);
        }

        return buffer.toByteArray();
    }

     private void parseRecords(byte[] buffer, int offset) throws Exception {
        NDEFRecord record;

	    if (buffer == null)
	        throw new Exception("Invalid buffer");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer, 0, buffer.length);


        while (inputStream.available() > 0) {
            record = NdefRecordFactory.getNdefRecord(inputStream);
            if(record != null) {
                addRecord(record);
            }
            else
                throw new Exception("Invalid buffer in parseRecords");

        }

    }

    /**
     * Add a record to the end of ndef message.
     * CF not yet used. To be implemented
     *
     * @param record
     */
    public void addRecord(NDEFRecord record) {
        if(record == null) {
            STLog.e("Invalid record!");
            return;
        }

        if (mNDEFRecords.isEmpty()) {
            mNDEFRecords.add(record);
            mNDEFRecords.get(0).setMB(true);
            mNDEFRecords.get(0).setME(true);
        } else {
            mNDEFRecords.get(0).setMB(true);
            mNDEFRecords.get(0).setME(false);
            mNDEFRecords.add(record);

            mNDEFRecords.get(mNDEFRecords.size() - 1).setMB(false);
            mNDEFRecords.get(mNDEFRecords.size() - 1).setME(true);
        }

    }


    public void deleteRecord(int position) {
        int size = mNDEFRecords.size();

        //No record available or bad parameter
        if ((size == 0) || (position >= size) || (position < 0))
            return;

        if (size > 1) {
            //First element but not last
            if (position == 0) {
                mNDEFRecords.get(1).setMB(true);
                mNDEFRecords.get(size - 1).setME(true);
            }//else if last element but not first
            else if (position == (size - 1)) {
                mNDEFRecords.get(size - 2).setME(true);
            }
        }

        mNDEFRecords.remove(position);
    }



    public void updateRecord(NDEFRecord record, int position) {

        int size = mNDEFRecords.size();

        if ((position >= size) || (position < 0))
            return;

        mNDEFRecords.set(position, record);

        //Update MB and ME in case...
        mNDEFRecords.get(0).setMB(true);

        if (size > 1) {
            mNDEFRecords.get(0).setME(false);
            mNDEFRecords.get(size - 1).setME(true);
        }
        else {
            mNDEFRecords.get(0).setME(true);
        }

    }

    /* public static final Parcelable.Creator<NDEFMsg> CREATOR
            = new Parcelable.Creator<NDEFMsg>() {
        public NDEFMsg createFromParcel(Parcel in) {
            return newInstance(in.createByteArray());

        }

        @Override
        public NDEFMsg[] newArray(int size) {
            return new NDEFMsg[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(serialize());
    }*/

    private void writeObject(java.io.ObjectOutputStream out) throws Exception {
        byte[] buffer = serialize();
        if (buffer != null) {
            out.write(buffer);
            out.flush();
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws Exception {
        int inAvailable  = in.available();

        if (inAvailable > 0) {
            try {
                int len = (in.available() > 1000) ? 1000 : inAvailable;
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inAvailable);
                int size;
                byte[] readBuffer = new byte[len];

                while ((size = in.read(readBuffer, 0, len)) >= 0) {
                    outputStream.write(readBuffer, 0, size);
                }

                mNDEFRecords = new ArrayList<>();
                parseRecords(outputStream.toByteArray(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new Exception("No data available");
        }
    }

    private void readObjectNoData() throws ObjectStreamException {
    }

    @Override
    public boolean equals(Object obj)  {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        byte[]  buffer;
        byte[]  objBuffer;
        try {
            buffer = serialize();
            objBuffer = ((NDEFMsg) obj).serialize();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return Arrays.equals(buffer, objBuffer);
    }

    public void setNDEFRecord(){}


    public int getLength() throws Exception {
        int length;
        byte[] msgBuffer;

        msgBuffer = serialize();

        if(msgBuffer != null) {
            length = msgBuffer.length;
        } else {
            length = 0;
        }

        return length;
    }


    public byte[] formatType5() throws Exception {

        if (getNDEFRecords().size() != 0) {

            byte[] msgBuffer;
            msgBuffer = serialize();

            // +3/5 = ndef id + ndef length (1 or 3 bytes) + FE terminal code (TLV)
            int length = msgBuffer.length;

            ByteBuffer byteBuffer;

            if (length > 0xFE) {
                // 2 bytes length
                byteBuffer = ByteBuffer.allocate(length + 5);
                byteBuffer.put(new byte[]{NDEF_IDENTIFIER, (byte) 0xFF, (byte) ((length & 0xFF00) >> 8), (byte) (length & 0xFF)});


            } else {
                // 1 byte length
                byteBuffer = ByteBuffer.allocate(length + 3);
                byteBuffer.put(new byte[]{NDEF_IDENTIFIER, (byte) length});
            }
            byteBuffer.put(msgBuffer, 0, length);
            byteBuffer.put(NDEF_TERMINATOR);
            return byteBuffer.array();

        }
        return new byte[]{0x00, 0X00, 0x00, 0x00, 0x00};
    }


    public NDEFMsg copy()  {
        byte buffer[];
        try {
            buffer = this.serialize();
            return new NDEFMsg(buffer);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

