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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Helper to compute CRC from
 * The aim of an error detection technique is to enable the receiver of a
 * message transmitted through a noisy (error-introducing) channel to
 * determine whether the message has been corrupted. To do this, the
 * transmitter constructs a value (called a checksum) that is a function
 * of the message, and appends it to the message. The receiver can then
 * use the same function to calculate the checksum of the received
 * message and compare it with the appended checksum to see if the
 * message was correctly received.
 * @author STMicroelectronics
 * @version 1.0
 * @since October 2013
 *
 */

public class Crc {

    private static long BITMASK(int x) {
        return 1L << (x);
    }

    /******************************************************************************/

    private static long reflect(long v, int b) {
        /* Returns the value v with the bottom b [0,32] bits reflected. */
        /* Example: reflect(0x3e23L,3) == 0x3e26 */
        int i;
        long t = v;
        for (i = 0; i < b; i++) {
            if ((t & 1L) != 0)
                v |= BITMASK((b - 1) - i);
            else
                v &= ~BITMASK((b - 1) - i);
            t >>= 1;
        }
        return v;
    }

    /******************************************************************************/

    private static long widmask(CrcModel cm)
    /* Returns a longword whose value is (2^p_cm->cm_width)-1. */
    /* The trick is to do this portably (e.g. without doing <<32). */
    {
        return (((1L << (cm.width - 1)) - 1L) << 1) | 1L;
    }

    /******************************************************************************/

    private static void cm_ini(CrcModel p_cm) {
        p_cm.reg = p_cm.init;

    }

    /******************************************************************************/

    private static void cm_nxt(CrcModel cm, int ch) {

        int i;
        long uch = ch;
        long topbit = BITMASK(cm.width - 1);

        if (cm.refin)
            uch = reflect(uch, 8);
        cm.reg ^= (uch << (cm.width - 8));
        for (i = 0; i < 8; i++) {
            if ((cm.reg & topbit) != 0)
                cm.reg = (cm.reg << 1) ^ cm.poly;
            else
                cm.reg <<= 1;
            cm.reg &= widmask(cm);
        }
    }

    /******************************************************************************/

    /*
     * void cm_blk (CrcModel cm,blk_adr,long blk_len){
     *
     * p_ubyte_ blk_adr; ulong blk_len;
     *
     * while (blk_len--) cm_nxt(cm,*blk_adr++); }
     */
    /******************************************************************************/

    private static long cm_crc(CrcModel cm) {
        if (cm.refot) {
            return cm.xorot ^ reflect(cm.reg, cm.width);
        } else {
            return cm.xorot ^ cm.reg;
        }
    }

    /******************************************************************************/

    static long cm_tab(CrcModel cm, int index) {
        int i;
        long r;
        long topbit = BITMASK(cm.width - 1);
        long inbyte = index;

        if (cm.refin)
            inbyte = reflect(inbyte, 8);
        r = inbyte << (cm.width - 8);
        for (i = 0; i < 8; i++)
            if ((r & topbit) != 0)
                r = (r << 1) ^ cm.poly;
            else
                r <<= 1;
        if (cm.refin)
            r = reflect(r, cm.width);
        return r & widmask(cm);
    }

    public static long CRC(File file) throws IOException {
        CrcModel crc_model = new CrcModel();
        int word_to_do = 0;
        byte[] buffer = new byte[4];
        char byte_to_do;
        int i, nbread;

        // Values for the STM32F generator.

        crc_model.width = 32; // 32-bit CRC
        crc_model.poly = 0x04C11DB7; // CRC-32 polynomial
        crc_model.init = 0xFFFFFFFF; // CRC initialized to 1's
        crc_model.refin = false; // CRC calculated MSB first
        crc_model.refot = false; // Final result is not bit-reversed
        crc_model.xorot = 0x00000000; // Final result XOR'ed with this

        cm_ini(crc_model);

        InputStream in = new FileInputStream(file);

        while ((nbread = in.read(buffer, 0, 4)) != -1) {

            for (int k = 0; k < buffer.length; k++) {
                word_to_do += (buffer[k] & 0xffL) << (8 * k);
            }

            // STLog.i("word_to_do ="+ Long.toHexString((word_to_do)));
            if (nbread > 0) {
                for (i = 0; i < 4; i++) {
                    // We calculate a *byte* at a time. If the CRC is MSB first
                    // we
                    // do the next MS byte and vica-versa.

                    if (!crc_model.refin) {
                        // MSB first. Do the next MS byte.

                        byte_to_do = (char) ((word_to_do & 0xFF000000) >>> 24);
                        word_to_do <<= 8;

                    } else {
                        // LSB first. Do the next LS byte.

                        byte_to_do = (char) (word_to_do & 0x000000FF);
                        word_to_do >>= 8;
                    }
                    // STLog.i("word_to_do ="+ Long.toHexString((word_to_do)));
                    // STLog.i("byte_to_do ="+
                    // Integer.toHexString(((int)byte_to_do)));
                    cm_nxt(crc_model, byte_to_do);
                }
            }
        }
        in.close();

        return cm_crc(crc_model);

    }

    /**
     * Compute the CRC - CRC-32 polynomial
     * @param data data on which the CRC is calculated
     *
     * @return 32 bits CRC value
     * @throws IOException
     */
    public static long CRC(byte data[]) throws IOException {
        CrcModel crc_model = new CrcModel();
        int word_to_do = 0;
        byte[] buffer = new byte[4];
        char byte_to_do;
        int i;

        // Values for the STM32F generator.

        crc_model.width = 32; // 32-bit CRC
        crc_model.poly = 0x04C11DB7; // CRC-32 polynomial
        crc_model.init = 0xFFFFFFFF; // CRC initialized to 1's
        crc_model.refin = false; // CRC calculated MSB first
        crc_model.refot = false; // Final result is not bit-reversed
        crc_model.xorot = 0x00000000; // Final result XOR'ed with this

        cm_ini(crc_model);

        for (int l = 0; l < data.length; l += 4) {
            Arrays.fill(buffer, (byte) 0x00);

            for (i = l; ((i < (data.length)) && (i < (l + 4))); i++)
                buffer[i - l] = data[i];

            for (int k = 0; k < buffer.length; k++) {
                word_to_do += (buffer[k] & 0xffL) << (8 * k);
            }

            for (i = 0; ((i < 4) && ((i + l) < data.length)); i++) {
                // We calculate a *byte* at a time. If the CRC is MSB first we
                // do the next MS byte and vica-versa.

                if (!crc_model.refin) {
                    // MSB first. Do the next MS byte.

                    byte_to_do = (char) ((word_to_do & 0xFF000000) >>> 24);
                    word_to_do <<= 8;

                } else {
                    // LSB first. Do the next LS byte.

                    byte_to_do = (char) (word_to_do & 0x000000FF);
                    word_to_do >>= 8;
                }
                // STLog.i("word_to_do ="+ Long.toHexString((word_to_do)));
                // STLog.i("byte_to_do ="+
                // Integer.toHexString(((int)byte_to_do)));
                cm_nxt(crc_model, byte_to_do);
            }
        }
        return cm_crc(crc_model);
    }

    /**
     * Compute a CRC16 as defined in ISO/IEC 13239.
     *
     * @param data : Byte array containing the data to proceed (LSB first)
     * @param dataLengthToProceed : This parameter allows to compute the CRC on part of the data.
     *                              It should not exceed data.length.
     * @return
     */
    public static int crc16LsbFirst(byte[] data, int dataLengthToProceed) {
        final int POLYNOMIAL = 0x8408;      // x^16 + x^12 + x^5 + 1
        final int PRESET_VALUE = 0xFFFF;

        if(dataLengthToProceed > data.length) {
            STLog.e("Error during CRC16 calculation!");
            return 0;
        }

        int crc = PRESET_VALUE;
        for (int i = 0; i < dataLengthToProceed; i++) {
            crc ^= (data[i] & 0xFF);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >>> 1) ^ POLYNOMIAL;
                } else {
                    crc = (crc >>> 1);
                }
            }
        }

        crc = ~crc;

        return crc & 0xFFFF;
    }

}
