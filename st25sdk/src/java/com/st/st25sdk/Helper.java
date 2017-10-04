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
import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;
import static com.st.st25sdk.STException.STExceptionCode.FILE_EMPTY;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

//**** SUMMARY *****
// stringForceDigit
// input : String "23"
// input : Int 4
// output : String"0023"
// convertByteToHexString
// input :  byte 0x0F
// output : String "0F"
// ConvertHexByteArrayToStrin
// input :  byte[] { 0X0F ; 0X43 ; 0xA4 ; ...}
// output : String "0F 43 A4 ..."
// formatStringAddressStart
// input : String "0F"
// input : DataDevice
// output: String  "000F"
// convertIntToHexFormatString
// input : Int 2047
// output : String "7FF"
// formatStringNbBlock
// input :  String "2"
// output : String "02"
// ConvertStringToHexByte
// input : String "43"
// output : byte { 0X43 }
// convertIntTo2BytesHexaFormat
// input : Int 1876
// output : byte[] {0x07, 0x54}
// convertHexStringToByteArray
// input : String "0F43BB079A"
// output : byte[] { 0X0F ; 0X43; 0xBB; 0x07; 0x9A }
// convertByteArrayToHexString
// input : byte[] { 0X0F ; 0X43; 0xBB; 0x07; 0x9A }
// output : String "0F43BB079A"
// convert2BytesHexaFormatToInt
// input : byte[] {0x07, 0x54}
// output : Int 1876
// ConvertStringToInt
// input : String "0754"
// output : Int 1876
// FormatDisplayReadBlock
// input : byte[] ReadMultipleBlockAnswer & byte[]AddressStart
// output : String "Block 0 : 32 FF EE 44"
// reverseByteArray
// input: byte[] {0xE0, 0x02, 0x4D, 0xFF}
// output: byte[] {0xFF, 0x4D, 0x02, 0x0E}

//@opt nodefillcolor #ffd300
public class Helper {

    //***********************************************************************/
    //* the function Format a String with the right number of digit
    //* Example : stringForceDigit("23",4) -> returns "0023"
    //* Example : stringForceDigit("54",7) -> returns "0000054"
    //***********************************************************************/
    public static String stringForceDigit(String sStringToFormat, int nbOfDigit) {
        if (sStringToFormat == null) {
            return "";
        }
        String sStringFormated = sStringToFormat.replaceAll(" ", "");

        if (sStringFormated.length() == 4) {
            return sStringFormated;
        } else if (sStringFormated.length() < nbOfDigit) {
            while (sStringFormated.length() != nbOfDigit) {
                sStringFormated = "0".concat(sStringFormated);
            }
        }

        return sStringFormated;
    }

    //***********************************************************************/
    //* the function Convert byte value to a "2-char String" Format
    //* Example : convertByteToHexString((byte)0X0F) -> returns "0F"
    //***********************************************************************/
    public static String convertByteToHexString(byte byteToConvert) {
        int value = (byteToConvert & 0xFF);
        return String.format("%02x", value).toUpperCase();
    }

    //***********************************************************************/
    //* the function Convert byte Array to a "String" Formated with spaces
    //* Example : convertHexByteArrayToString({ 0X0F, 0X43 }) -> returns "0F 43"
    //***********************************************************************/
    public static String convertHexByteArrayToString(byte[] byteArrayToConvert) {
        String convertedByte = "";

        if (byteArrayToConvert == null) {
            return convertedByte;
        }

        for (byte aByte : byteArrayToConvert) {
            if (aByte < 0) {
                convertedByte += Integer.toString(aByte + 256, 16) + " ";
            } else if (aByte <= 15) {
                convertedByte += "0" + Integer.toString(aByte, 16) + " ";
            } else {
                convertedByte += Integer.toString(aByte, 16) + " ";
            }
        }

        return convertedByte;
    }

    //***********************************************************************/
    //* The function prints a byte Array in hexadecimal format
    //* Example : printHexByteArray({ 0X0F, 0X43 }) will print "0F 43"
    //***********************************************************************/
    public static void printHexByteArray(String msg, byte[] byteArrayToPrint) {
        boolean endOfBuffer = false;
        final int chunkSize = 32;

        if (byteArrayToPrint == null) return;

        STLog.i(msg + " (" + byteArrayToPrint.length + " bytes): ");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayToPrint);

        // Read the data by chunks of 32 bytes and print them in hexadecimal format
        while(!endOfBuffer) {
            byte[] buffer = new byte[chunkSize];

            int nbrOfBytesRead = byteArrayInputStream.read(buffer, 0, buffer.length);

            if(nbrOfBytesRead == chunkSize) {
                // Print those 32 hexadecimal values
                STLog.i(convertHexByteArrayToString(buffer));

            } else {
                // We have reached the end of the buffer
                endOfBuffer = true;

                if(nbrOfBytesRead > 0) {
                    // Buffer contains some valid data followed by several 0x00 til the end of the buffer

                    // Create a new buffer containing only the valid data
                    byte[] data = Arrays.copyOf(buffer, nbrOfBytesRead);


                    // Print the hexadecimal values
                    STLog.i(convertHexByteArrayToString(data));
                }
            }
        }

    }

    //***********************************************************************/
    //* the function verify and convert the start address from the EditText
    //* in order to not read out of memory range and code String on 4chars.
    //* Example : formatStringAddressStart ("0F") -> returns "000F"
    //* Example : formatStringAddressStart ("FFFF") -> returns "07FF"
    //***********************************************************************/
    public static String formatStringAddressStart(String stringToFormat, String ma) {
        String stringFormated;
        stringFormated = stringForceDigit(stringToFormat, 4);

        if (stringFormated.length() > 4) {
            stringFormated = ma.replace(" ", "");
        }

        int iAddressStart = convertStringToInt(stringFormated);
        int iAddresStartMax = convertStringToInt(stringForceDigit(ma, 4));

        if (iAddressStart > iAddresStartMax) {
            iAddressStart = iAddresStartMax;
        }

        stringFormated = convertIntToHexFormatString(iAddressStart);


        return stringFormated.toUpperCase();
    }

    //***********************************************************************/
    //* the function convert an Int value to a String with Hexadecimal format
    //* Example : convertIntToHexFormatString (2047) -> returns "7FF"
    //***********************************************************************/
    public static String convertIntToHexFormatString(int iNumberToConvert) {
        return String.format("%04x", iNumberToConvert).toUpperCase();
    }

    //***********************************************************************/
    //* the function verify and convert the NbBlock from the EditText (HEXA)
    //* in order to not read out of memory range and code String on 4chars.
    //* Example : formatStringAddressStart ("0F") -> returns "000F"
    //* Example : formatStringAddressStart ("FFFF") -> returns "07FF"
    //***********************************************************************/
    public static String formatStringNbBlock(String stringToformat, String sAddressStart, String ma) {
        String sNbBlockToRead = stringToformat;
        sNbBlockToRead = stringForceDigit(sNbBlockToRead, 4);

        if (sNbBlockToRead.length() > 4) {
            sNbBlockToRead = ma.replace(" ", "");
        }

        int iNbBlockToRead = convertStringToInt(sNbBlockToRead);
        int iAddressStart = convertStringToInt(sAddressStart);
        int iAddresStartMax = convertStringToInt(stringForceDigit(ma, 4));

        if (iAddressStart + iNbBlockToRead > iAddresStartMax) {
            iNbBlockToRead = iAddresStartMax - iAddressStart + 1;
        }
        /*
        else if(iNbBlockToRead > iAddresStartMax)
        {
            iNbBlockToRead = iAddresStartMax +1;
        }
         */

        sNbBlockToRead = convertIntToHexFormatString(iNbBlockToRead);
        sNbBlockToRead = stringForceDigit(sNbBlockToRead, 4);

        return sNbBlockToRead;
    }

    //***********************************************************************/
    //* the function verify and convert the NbBlock from the EditText (DECIMAL)
    //* in order to not read out of memory range and code String on 4chars.
    //* Example : formatStringAddressStart ("01") -> returns "0001"
    //* Example : formatStringAddressStart ("9999") -> returns "2048"
    //***********************************************************************/
    public static String formatStringNbBlockInteger(String stringToformat, String sAddressStart, String ma) {
        String sNbBlockToRead = stringToformat;
        sNbBlockToRead = stringForceDigit(sNbBlockToRead, 4);

        if (sNbBlockToRead.length() > 4) {
            sNbBlockToRead = ma.replace(" ", "");
        }

        int iNbBlockToRead = Integer.parseInt(sNbBlockToRead);
        int iAddressStart = convertStringToInt(sAddressStart);
        int iAddresStartMax = convertStringToInt(stringForceDigit(ma, 4));

        if (iAddressStart + iNbBlockToRead > iAddresStartMax + 1) {
            iNbBlockToRead = iAddresStartMax - iAddressStart + 1;
        }
        /*
            else if(iNbBlockToRead > iAddresStartMax)
            {
                iNbBlockToRead = iAddresStartMax +1;
            }
         */

        sNbBlockToRead = Integer.toString(iNbBlockToRead, 10);
        sNbBlockToRead = stringForceDigit(sNbBlockToRead, 4);

        return sNbBlockToRead;
    }

    //***********************************************************************/
    //* the function converts a String containing a hex dump to a byte array
    //* Example : "0F43BB079A" -> { 0X0F ; 0X43; 0xBB; 0x07; 0x9A }
    //* source: http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    //***********************************************************************/
    public static byte[] convertHexStringToByteArray(String s) {
        if (s == null) {
            return null;
        }

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < (len - 1); i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    //***********************************************************************/
    //* the function converts a byte array into a String containing a hex dump
    //* Example : { 0X0F ; 0X43; 0xBB; 0x07; 0x9A } -> "0F43BB079A"
    //* source: http://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    //***********************************************************************/
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String convertByteArrayToHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    //***********************************************************************/
    //* the function converts a String containing decimal into an integer
    //* Example : "1234" -> { 1234 }
    //***********************************************************************/
    public static int convertStringToInt(String txt) {
        return Integer.parseInt(txt);
    }

    //***********************************************************************/
    //* the function converts a String containing hexadecimal into an integer
    //* Example : "43A89" -> { 43A89 }
    //***********************************************************************/
    public static int convertHexStringToInt(String hexNumber) throws STException {
        if (hexNumber == null || hexNumber.isEmpty()) {
            throw new STException(BAD_PARAMETER);
        }

        return Integer.parseInt(hexNumber, 16);
    }

    //***********************************************************************/
    //* the function converts a "4-char String" to a two bytes format
    //* Example : "43" -> { 0X43 }
    //***********************************************************************/
    public static byte convertHexStringToByte(String hexNumber) throws STException {
        if (hexNumber == null || hexNumber.isEmpty()) {
            throw new STException(BAD_PARAMETER);
        }

        int value = convertHexStringToInt(hexNumber);

        if (value > 0xFF) {
            throw new STException(BAD_PARAMETER);
        }

        return (byte) value;
    }

    //***********************************************************************/
    //* the function Convert Int value to a "2 bytes Array" Format
    //*  (decimal)1876 == (hexadecimal)0754
    //* Example : convertIntTo2BytesHexaFormat (1876) -> returns {0x07, 0x54}
    //***********************************************************************/
    public static byte[] convertIntTo2BytesHexaFormat(int numberToConvert) {
        byte[] convertedNumber = new byte[2];

        convertedNumber[0] = (byte) ((numberToConvert & 0xFF00) >> 8);
        convertedNumber[1] = (byte) ((numberToConvert & 0xFF));

        return convertedNumber;
    }

    /**
     * Converts a big-endian 2-byte Array To int Format
     * (decimal)1876 = (hexadecimal)0754
     * Example: convert2BytesHexaFormatToInt {0x07, 0x54} -> returns 1876
     * @param arrayToConvert big-endian byte array to convert
     * @return integer value
     */
    public static int convert2BytesHexaFormatToInt(byte[] arrayToConvert) {
        int convertedNumber = 0;

        if (arrayToConvert != null) {
            convertedNumber += (arrayToConvert[1] & 0xFF);
            convertedNumber += (arrayToConvert[0] & 0xFF) * 256;
        }

        return convertedNumber;
    }

    /**
     *
     * @param addressStart
     * @param length
     * @return
     */
    public static String[] buildArrayBlocks(byte[] addressStart, int length) {
        String array[] = new String[length];

        int add = addressStart[1];

        if (addressStart[1] < 0)
            add = (addressStart[1] + 256);

        if (addressStart[0] < 0)
            add += (256 * (addressStart[0] + 256));
        else
            add += (256 * addressStart[0]);

        for (int i = 0; i < length; i++) {
            if (i == 14) {
                i = 14;
            }
            array[i] = "Block  " + convertIntToHexFormatString(i + add).toUpperCase();
        }

        return array;
    }

    /**
     * Read the next Byte of a ByteArrayInputStream and throws a STException if there is no more byte
     *
     * @param byteArrayInputStream
     * @return
     */
    public static Byte readNextByteOfByteArrayInputStream(ByteArrayInputStream byteArrayInputStream) throws STException {
        int result = byteArrayInputStream.read();
        if (result == -1) {
            // InputStream is empty!
            throw new STException(FILE_EMPTY);
        } else {
            return (byte) result;
        }
    }

    /**
     * Read the next Block of a ByteArrayInputStream and throws a STException if there is no more byte
     *
     * @param byteArrayInputStream
     * @param nbrOfBytesPerBlock
     * @return
     * @throws STException
     */
    public static byte[] readNextBlockOfByteArrayInputStream(ByteArrayInputStream byteArrayInputStream, int nbrOfBytesPerBlock) throws STException {
        byte[] block = new byte[nbrOfBytesPerBlock];

        for(int i=0; i< block.length; i++) {
            block[i] = readNextByteOfByteArrayInputStream(byteArrayInputStream);
        }

        return block;
    }

    /**
     * Read N bytes of a ByteArrayInputStream and throws a STException if there is no more byte
     *
     * @param byteArrayInputStream
     * @param len
     * @return
     */
    public static byte[] readByteArrayInputStream(ByteArrayInputStream byteArrayInputStream, int len) throws STException {
        byte[] result = new byte[len];

        try {
            int nbrOfByteRead = byteArrayInputStream.read(result, 0, result.length);
            if (nbrOfByteRead != len) {
                // InputStream doesn't contain enough bytes!
                throw new STException(FILE_EMPTY);
            } else {
                return result;
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            throw new STException(CMD_FAILED);
        }
    }


    /**
     * Concatenate two byte arrays
     *
     * @param array1
     * @param array2
     * @return
     */
    public static byte[] concatenateByteArrays(byte[] array1, byte[] array2) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            outputStream.write(array1);
            outputStream.write(array2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    /**
     * Concatenate two byte arrays. Special version where the second array is a single byte
     *
     * @param array1
     * @param array2
     * @return
     */
    public static byte[] concatenateByteArrays(byte[] array1, byte array2) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            outputStream.write(array1);
            outputStream.write(array2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    /**
     * Reverses a byte array.
     *
     * Example: reverseByteArray ({0xE0, 0x02, 0x4D, 0xFF})  {0xFF, 0x4D, 0x02, 0x0E}
     *
     * @return array with content reversed
     */
    public static byte[] reverseByteArray(byte[] uid) {
        byte[] ret = new byte[uid.length];

        for (int i = 0; i < uid.length; i++)
            ret[i] = uid[uid.length - 1 - i];

        return ret;
    }

    /**
     * Divide 2 integers and round the result to the upper integer
     * @param num
     * @param den
     * @return
     */
    public static int divisionRoundedUp(int num, int den) throws STException {
        if (den == 0) {
            throw new STException(BAD_PARAMETER);
        }

        return (num + den - 1) / den;
    }

}
