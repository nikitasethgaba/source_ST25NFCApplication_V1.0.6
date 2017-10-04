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

import com.st.st25sdk.command.Iso15693CustomCommand;

import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.NOT_IMPLEMENTED;

public class STDynamicRegister extends STRegister {

    public class STDynamicRegisterField extends STRegisterField {
        /**
         * Constructor
         *
         * @param     name register field Name
         * @param     description register field Description
         * @param   mask mask value to indicate the bits that will be used to store and retrieve the data for that field
         */
        public STDynamicRegisterField (
                String name,
                String description,
                int       mask) {
            super(name,description,mask);
        }

        /**
         * Returns the value for the BitField
         * @param   useFastCommand Set to true to use the fast command to access the dynamic register
         * @return  integer with BitField value
         */
        public int getValue(boolean useFastCommand) throws STException {
            int registerValue = getRegisterValue(useFastCommand);
            return computeField(registerValue);
        }

        /**
         * Replaces the bits with new values given in parameter.
         * @param   fieldValue new field value
         * @param   useFastCommand Set to true to use the fast command to access the dynamic register
         */
        public void setValue(int fieldValue, boolean useFastCommand) throws STException {
            int registerValue = getRegisterValue(useFastCommand);
            int newRegisterValue = computeRegisterValue(registerValue,fieldValue);
            setRegisterValue(newRegisterValue,useFastCommand);
        }


    }

    public STDynamicRegister(Iso15693CustomCommand iso15693CustomCommand,
            byte registerAddress,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize){

        super(iso15693CustomCommand,
                registerAddress,
                registerName,
                registerContentDescription,
                registerAccessRights,
                registerDataSize);
    }


    /////////// Getters - Setters of the raw register value //////////////////


    /**
     * Generic function to write a register value.
     * The value is passed as an int so that this generic function can
     * work for every kind of registers (8 bits, 16 bits or even more)
     * @param value register value
     * @throws STException {@link}STException
     */
    @Override
    public void setRegisterValue(int value) throws STException {
        setRegisterValue(value, false);
    }

    /**
     * Generic function to write a register value.
     * The value is passed as an int so that this generic function can
     * work for every kind of registers (8 bits, 16 bits or even more)
     * @param value register value
     * @param useFastCommand Set to true to use the fast command to access the dynamic register
     * @throws STException {@link}STException
     */
    public void setRegisterValue(int value, boolean useFastCommand) throws STException {

        switch(mRegisterDataSize) {
            case REGISTER_DATA_ON_8_BITS:
                if (value > 0xFF) {
                    STLog.e("This register supports only 8 bits data!");
                    throw new STException(BAD_PARAMETER);
                }
                byte registerValue = (byte) value;

                if (useFastCommand) {
                    mIso15693CustomCommand.fastWriteDynConfig(mRegisterAddress, registerValue);
                } else {
                    mIso15693CustomCommand.writeDynConfig(mRegisterAddress, registerValue);
                }
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
     * @throws STException {@link}STException
     */
    @Override
    protected int readTagRegisterValue() throws STException {
        return readTagRegisterValue(false);
    }

    /**
     * Private function actually reading the register value from the tag
     * @param useFastCommand Set to true to use the fast command to access the dynamic register
     * @return register value
     * @throws STException {@link}STException
     */
    protected int readTagRegisterValue(boolean useFastCommand) throws STException {
        byte[] response;

        if (useFastCommand) {
            response = mIso15693CustomCommand.fastReadDynConfig(mRegisterAddress);
        } else {
            response = mIso15693CustomCommand.readDynConfig(mRegisterAddress);
        }

        if (response.length < 2) {
            throw new STException(STException.STExceptionCode.CMD_FAILED, response);
        }
        // response[0] is the status byte
        mRegisterValue = response[1] & 0xFF;

        // The read was successful so the cache is now valid
        mCacheInvalidated = false;

        return mRegisterValue;
    }

    /**
     * Generic function to get register value.
     * The value is returned as an int so that this generic function can
     * work for every kind of registers (8 bits, 16 bits or even more)
     * @param useFastCommand Set to true to use the fast command to access the dynamic register
     * @return register value
     * @throws STException {@link}STException
     */
    public int getRegisterValue(boolean useFastCommand) throws STException {
        int registerValue;

        if (!mCacheActivated || mCacheInvalidated) {
            // Data should be read from the tag, updates mRegisterValue
            registerValue = readTagRegisterValue(useFastCommand);
        } else {
            // Return the value contained in the cache
            registerValue = mRegisterValue;
        }

        switch(mRegisterDataSize) {
            default:
            case REGISTER_DATA_ON_8_BITS:
                registerValue = registerValue & 0xFF;
                break;

            case REGISTER_DATA_ON_16_BITS:
                registerValue = registerValue & 0xFFFF;
                break;
        }

        return registerValue;
    }

    public STDynamicRegisterField getDynRegisterField(String fieldName) throws STException {
        return (STDynamicRegisterField) getRegisterField(fieldName);
    }

    /////////////////////////////////////////////////////////////////

}
