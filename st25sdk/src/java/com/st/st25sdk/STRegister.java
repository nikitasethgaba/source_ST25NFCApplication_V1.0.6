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

import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.NOT_IMPLEMENTED;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.BitField;

import com.st.st25sdk.command.Iso15693CustomCommand;

public class STRegister implements CacheInterface {

    public class STRegisterField  {

        protected String    mName;
        protected String    mDescription;
        protected int       mMask;
        protected BitField  mBitField;

        // private variables used to compute bit numbers from mask value.
        private   int       mBitStart = -1;
        private   int       mBitEnd   = 0 ;

        /**
         * Constructor
         *
         * @param     name register field Name
         * @param     description register field Description
         * @param   mask mask value to indicate the bits that will be used to store and retrieve the data for that field
         */
        public STRegisterField(
                String name,
                String description,
                int       mask) {

            mName            = name;
            mDescription     = description;
            mMask            = mask;
            mBitField        = new BitField(mask);

            getBitStartEnd();
        }

        /////////// Getters - Setters of the register field  class //////////////////
        /**
         * Returns field name
         * @return  String with Field Name
         */
        public String getName() {
            return mName;
        }

        /**
         * Returns Field Name Description
         * @return  String with Description Name
         */
        public String getDescription() {
            return mDescription;
        }

        /**
         * Returns field mask
         * @return  integer with mask value
         */
        public int getMask() {
            return mMask;
        }

        /**
         * Returns the value for the BitField
         * @return  integer with BitField value
         */
        public int getValue() throws STException {
            int registerValue = getRegisterValue();
            return computeField(registerValue);
        }

        /**
         * Replaces the bits with new values given in parameter.
         * @param     fieldValue new field value
         */
        public void setValue(int fieldValue) throws STException {
            int registerValue = getRegisterValue();
            int newRegisterValue = computeRegisterValue(registerValue,fieldValue);
            setRegisterValue(newRegisterValue);
        }

        /**
         * Replaces in the given register value the bits with new values given in parameter.
         * This function doesn't update the register
         * @param     registerValue the register value
         * @param     fieldValue new field value
         */
        public int computeRegisterValue(int registerValue, int fieldValue) {
            return mBitField.setValue(registerValue,fieldValue);
        }

        /**
         * Gets the field value from a provided register value.
         * This function doesn't update the register
         * @param     registerValue the register value
         */
        public int computeField(int registerValue) {
            return mBitField.getValue(registerValue);
        }

        /**
         * Returns the bit start index of register field
         * @return  integer with bit start index
         */
        public int getBitStart(){
            return(mBitStart);
        }

        /**
         * Returns the bit end index of register field
         * @return  integer with bit end index
         */
        public int getBitEnd(){
            return(mBitEnd);
        }

        /////////// Internal methods  //////////////////
        /**
         * return bit index from mask value.
         */
        private void getBitStartEnd(){
            int    maskSize = 8 ; // 8 bits by default
            if (mMask > 0xff ) maskSize=16;
            for (int i=0 ; i < maskSize ; i++){
                if ((mMask & (1L << i)) != 0)
                {
                    if (mBitStart == -1 ){
                        mBitStart = i ;
                    }
                    mBitEnd = i;
                }
            }
        }
    }

    public interface RegisterListener {
        void registerChange() throws STException;
    }

    public enum RegisterAccessRights {
        REGISTER_READ_ONLY,
        REGISTER_READ_WRITE
    }

    public enum RegisterDataSize {
        REGISTER_DATA_ON_8_BITS,
        REGISTER_DATA_ON_16_BITS
    }

    protected byte mRegisterAddress;
    protected int mRegisterValue;

    protected String mRegisterName;
    protected String mRegisterContentDescription;
    protected RegisterAccessRights mRegisterAccessRights;
    protected RegisterDataSize mRegisterDataSize;
    protected LinkedHashMap<String, STRegisterField> mRegisterFields;
    protected boolean mCacheActivated;
    protected boolean mCacheInvalidated;

    protected Iso15693CustomCommand mIso15693CustomCommand;

    // List of Listeners monitoring any change of this register value
    protected List<RegisterListener> mListenerList = new ArrayList<>();


    public STRegister(Iso15693CustomCommand iso15693CustomCommand,
            byte registerAddress,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        this (iso15693CustomCommand,
                registerAddress,
                registerName,
                registerContentDescription,
                registerAccessRights,
                registerDataSize,
                null);
    }

    public STRegister(Iso15693CustomCommand iso15693CustomCommand,
            byte registerAddress,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize,
            List<STRegisterField> registerFields) {

        mIso15693CustomCommand = iso15693CustomCommand;
        mRegisterAddress = registerAddress;
        mRegisterName = registerName;
        mRegisterContentDescription = registerContentDescription;
        mRegisterAccessRights = registerAccessRights;
        mRegisterDataSize = registerDataSize;

        mCacheActivated = true;
        mCacheInvalidated = true;

        List<STRegisterField> fieldList;

        mRegisterFields = new LinkedHashMap<>();


        fieldList = new ArrayList<>();
        int nbOfBit = (registerDataSize == STRegister.RegisterDataSize.REGISTER_DATA_ON_8_BITS) ? 8 : 16;
        for (int i = 0; i < nbOfBit; i++) {

            String fieldName = String.format("bit%d", i);
            String fieldDesc = String.format("bit %d description", i);
            fieldList.add(new STRegisterField(fieldName, fieldDesc, (1 << i)));
        }

        // Keep fields as a Map
        createFieldHash(fieldList);

    }


    protected void createFieldHash (List<STRegisterField> fieldList)
    {
        if (fieldList == null)
            return;

        mRegisterFields.clear();

        for (STRegisterField registerField : fieldList) {
            mRegisterFields.put(registerField.getName(), registerField);
        }
    }

    public byte getRegisterAddress() {
        return mRegisterAddress;
    }

    public String getRegisterName() {
        return mRegisterName;
    }

    public String getRegisterContentDescription() {
        return mRegisterContentDescription;
    }

    public RegisterAccessRights getRegisterAccessRights() {
        return mRegisterAccessRights;
    }

    public RegisterDataSize getRegisterDataSize() {
        return mRegisterDataSize;
    }

    public List<STRegisterField> getRegisterFields() {
        return new ArrayList<>(mRegisterFields.values());
    }

    public STRegisterField getRegisterField(String fieldName) throws STException {
        STRegisterField registerField = mRegisterFields.get(fieldName);
        if (registerField != null) {
            return registerField;
        } else {
            throw new STException(BAD_PARAMETER);
        }
    }

    /////////// Getters - Setters of the raw register value //////////////////

    /**
     * Generic function to get register value.
     * The value is returned as an int so that this generic function can
     * work for every kind of registers (8 bits, 16 bits or even more)
     * @return register value
     * @throws STException {@link}STException
     */
    public int getRegisterValue() throws STException {
        int registerValue;

        if (!isCacheActivated() || !isCacheValid()) {
            // Data should be read from the tag, updates mRegisterValue
            registerValue = readTagRegisterValue();
        } else {
            // Return the value contained in the cache
            registerValue = mRegisterValue;
        }

        switch(mRegisterDataSize) {
            case REGISTER_DATA_ON_16_BITS:
                registerValue = registerValue & 0xFFFF;
                break;

            case REGISTER_DATA_ON_8_BITS:
            default:
                registerValue = registerValue & 0xFF;
                break;
        }

        return registerValue;
    }

    /**
     * Generic function to write a register value.
     * The value is passed as an int so that this generic function can
     * work for every kind of registers (8 bits, 16 bits or even more)
     * @param value register value
     * @throws STException {@link}STException
     */
    public void setRegisterValue(int value) throws STException {

        switch(mRegisterDataSize) {
            case REGISTER_DATA_ON_8_BITS:
                if (value > 0xFF) {
                    STLog.e("This register supports only 8 bits data!");
                    throw new STException(BAD_PARAMETER);
                }
                byte registerValue = (byte) value;

                mIso15693CustomCommand.writeConfig(mRegisterAddress, registerValue);
                break;

            case REGISTER_DATA_ON_16_BITS:
                // Not implemented yet because it requires some modifications of the writeConfig
                // function to support data on more than 8 bits.
                throw new STException(NOT_IMPLEMENTED);
        }

        // If we reach this point, it means that the write was successful so we can save the value written
        mRegisterValue = value;

        // Cache is now valid
        mCacheInvalidated = false;

        notifyListeners();
    }

    /**
     * Private function actually reading the register value from the tag
     * @return register value
     * @throws STException {@link}STException
     */
    protected int readTagRegisterValue() throws STException {
        byte[] response = mIso15693CustomCommand.readConfig(mRegisterAddress);

        if (response.length < 2 ) {
            throw new STException(STException.STExceptionCode.CMD_FAILED, response);
        }

        // response[0] is the status byte
        mRegisterValue = response[1] & 0xFF;

        // The read was successful so the cache is now valid
        mCacheInvalidated = false;

        return mRegisterValue;
    }

    //////////////////////////  Listeners   //////////////////////

    /**
     * Function allowing to subscribe to receive a notification when this register is changing.
     * @param newListener listener
     */
    public void addRegisterListener(RegisterListener newListener) {
        mListenerList.add(newListener);
    }

    /**
     * Notify all the Listeners that the register value has changed
     * @throws STException {@link}STException
     */
    protected void notifyListeners() throws STException {
        STLog.i("Register " + mRegisterName + " has changed. New value: 0x" + Integer.toHexString(mRegisterValue));

        for (RegisterListener listener : mListenerList) {
            listener.registerChange();
        }
    }

    //////////////////////////  Cache management   //////////////////////

    @Override
    public void invalidateCache() {
        mCacheInvalidated = true;
    }

    @Override
    public void validateCache() {
        mCacheInvalidated = false;
    }

    @Override
    public void activateCache() {
        mCacheActivated = true;
        mCacheInvalidated = true;
    }

    @Override
    public void deactivateCache() {
        mCacheActivated = false;
    }

    @Override
    public void updateCache() throws STException {
        readTagRegisterValue();
    }

    @Override
    public boolean isCacheValid() {
        return !mCacheInvalidated;
    }

    @Override
    public boolean isCacheActivated() {
        return mCacheActivated;
    }

    /////////////////////////////////////////////////////////////////

}
