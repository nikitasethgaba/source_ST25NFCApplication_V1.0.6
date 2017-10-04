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
import static com.st.st25sdk.STException.STExceptionCode.NOT_SUPPORTED;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_GENERIC_TYPE5_AND_ISO15693;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_LRi1K;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_LRi2K;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_LRi512;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_LRi64;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_LRiS2K;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_LRiS64K;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_M24LR04E_R;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_M24LR16E_R;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_M24LR64E_R;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_M24LR64_R;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_ST25DV02K_W;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_ST25DV04K_I;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_ST25DV04K_J;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_ST25DV16K_I;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_ST25DV16K_J;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_ST25DV64K_I;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_ST25DV64K_J;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_ST25TV02K;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_ST25TV02K_EH;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_ST25TV512;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_ST_ST25TV64K;
import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_UNKNOWN;
import static com.st.st25sdk.command.Iso15693Protocol.STM_MANUFACTURER_CODE;

import com.st.st25sdk.command.Iso15693Command;
import com.st.st25sdk.command.VicinityCommand;
import com.st.st25sdk.iso14443sr.STIso14443SRTag;
import com.st.st25sdk.type4a.Type4Tag;


public class TagHelper {

    protected static final byte DSFID_MASK = 0x01;
    protected static final byte AFI_MASK = 0x02;
    protected static final byte VICC_MEM_SIZE_MASK = 0x04;
    protected static final byte ICREF_MASK = 0x08;
    protected static final byte PROTOCOL_CODE_ISO14443B = (byte) 0xD0;
    protected static final byte PROTOCOL_CODE_ISO15693 = (byte) 0xE0;


    static public String getManufacturerName(NFCTag tag) throws STException {
        return ICManufacturers[getManufacturerId(tag.getUid(), tag.getType())];
    }


    // IC Manufacturers codes, as defined in ISO/IEC 7816-6
    public static String[] ICManufacturers = { /* 0x00 */ "Unknown", /* 0x01 */ "Motorola",
            /* 0x02 */ "STMicroelectronics", /* 0x03 */ "Hitachi Ltd", /* 0x04 */ "NXP Semiconductors",
            /* 0x05 */ "Infineon Technologies", /* 0x06 */ "Cylink", /* 0x07 */ "Texas Instruments",
            /* 0x08 */ "Fujitsu Limited", /* 0x09 */ "Matsushita Electronics Corporation", /* 0x0A */ "NEC",
            /* 0x0B */ "Oki Electric Industry Co. Ltd", /* 0x0C */ "Toshiba Corp.",
            /* 0x0D */ "Mitsubishi Electric Corp.", /* 0x0E */ "Samsung Electronics Co. Ltd",
            /* 0x0F */ "Hyundai Electronics Industries Co. Ltd", /* 0x10 */ "LG-Semiconductors Co. Ltd",
            /* 0x11 */ "Emosyn-EM Microelectronics", /* 0x12 */ "Inside Technology",
            /* 0x13 */ "ORGA Kartensysteme GmbH", /* 0x14 */ "SHARP Corporation", /* 0x15 */ "ATMEL",
            /* 0x16 */ "EM Microelectronic-Marin SA", /* 0x17 */ "KSW Microtec GmbH", /* 0x18 */ "Unknown",
            /* 0x19 */ "XICOR, Inc.", /* 0x1A */ "Sony Corporation",
            /* 0x1B */ "Malaysia Microelectronic Solutions Sdn Bhd (MY)", /* 0x1C */ "Emosyn (US)",
            /* 0x1D */ "Shanghai Fudan Microelectronics Co Ltd (CN)", /* 0x1E */ "Magellan Technology Pty Limited (AU)",
            /* 0x1F */ "Melexis NV BO (CH)", /* 0x20 */ "Renesas Technology Corp (JP)", /* 0x21 */ "TAGSYS (FR)",
            /* 0x22 */ "Transcore (US)", /* 0x23 */ "Shanghai Belling Corp Ltd (CN)",
            /* 0x24 */ "Masktech Germany GmbH (DE)", /* 0x25 */ "Innovision Research and Technology",
            /* 0x26 */ "Hitachi ULSI Systems Co Ltd (JP)", /* 0x27 */ "Cypak AB (SE)", /* 0x28 */ "Ricoh (JP)",
            /* 0x29 */ "ASK (FR)", /* 0x2A */ "Unicore Microsystems LLC (RU)",
            /* 0x2B */ "Dallas semiconductor/Maxim (US)", /* 0x2C */ "Impinj Inc (US)",
            /* 0x2D */ "RightPlug Alliance (US)", /* 0x2E */ "Broadcom Corporation (US)",
            /* 0x2F */ "MStar Semiconductor Inc (TW)", /* 0x30 */ "BeeDar Technology Inc (US)",
            /* 0x31 */ "RFIDsec (DK)", /* 0x32 */ "Schweizer Electronic AG (DE)",
            /* 0x33 */ "AMIC Technology Corp (TW)", /* 0x34 */ "Mikron JSC (RU)",
            /* 0x35 */ "Fraunhofer Institute for Photonic Microsystems (DE)", /* 0x36 */ "IDS Microship AG (CH)",
            /* 0x37 */ "Kovio (US)", /* 0x38 */ "AHMT Microelectronic Ltd (CH)",
            /* 0x39 */ "Silicon Craft Technology (TH)", /* 0x3A */ "Advanced Film Device Inc. (JP)",
            /* 0x3B */ "Nitecrest Ltd (UK)", /* 0x3C */ "Verayo Inc. (US)", /* 0x3D */ "HID Global (US)",
            /* 0x3E */ "Productivity Engineering Gmbh (DE)", /* 0x3F */ "AMS (Austria Microsystems)",
            /* 0x40 */ "Gemalto SA (FR)", /* 0x41 */ "Renesas Electronics Corporation (JP)",
            /* 0x42 */ "3Alogics Inc (KR)", /* 0x43 */ "Top TroniQ Asia Limited (Hong Kong)",
            /* 0x44 */ "Gentag Inc (USA)"
    };

    // Products internal identifiers
    public enum ProductID {
        PRODUCT_UNKNOWN,


        /***** Type 4A *****/
        PRODUCT_ST_M24SR02_Y,
        PRODUCT_ST_M24SR04,
        PRODUCT_ST_M24SR16_Y,
        PRODUCT_ST_M24SR64_Y,

        PRODUCT_ST_ST25TA512,     // E5
        PRODUCT_ST_ST25TA512_G,   // E4
        PRODUCT_ST_ST25TA512_K,   // D5

        PRODUCT_ST_ST25TA02K,     // E2
        PRODUCT_ST_ST25TA02K_G,   // E3
        PRODUCT_ST_ST25TA02K_P,   // A2
        PRODUCT_ST_ST25TA02K_GP,  // A3
        PRODUCT_ST_ST25TA02K_D,   // F2
        PRODUCT_ST_ST25TA02K_GD,  // F3

        PRODUCT_ST_ST25TA16K,     // C5

        PRODUCT_ST_ST25TA64K,     // C4

        /***** Former T4A tags *****/
        PRODUCT_ST_SRTAG2K,
        PRODUCT_ST_SRTAG2K_D,
        PRODUCT_ST_M24SR01,
        PRODUCT_ST_M24SR08,
        PRODUCT_ST_M24SR32,
        PRODUCT_ST_T24SR64,
        PRODUCT_ST_ST25TA16K_D,   // B5
        PRODUCT_ST_ST25TA64K_D,   // D2
        PRODUCT_ST_ST25TA04K,     // C6
        PRODUCT_ST_ST25TA04K_D,   // B6
        PRODUCT_ST_ST25TA02K_N,   // B2
        PRODUCT_ST_ST25TA01K_G,   // E1
        PRODUCT_ST_ST25TA01K_GD,  // F1
        PRODUCT_ST_ST25TA256_G,   // E0
        PRODUCT_ST_ST25TA256_GD,  // F0
        PRODUCT_GENERIC_TYPE4,

        /***** ISO14443SR type *****/
        PRODUCT_ST_ST25TB512_AC,
        PRODUCT_ST_ST25TB512_AT,
        PRODUCT_ST_ST25TB02K,
        PRODUCT_ST_ST25TB04K,

        /***** Former ISO14443SR tags *****/
        PRODUCT_ST_SRi512,
        PRODUCT_ST_SRi2K,
        PRODUCT_ST_SRi4K,
        PRODUCT_ST_SRT512,
        PRODUCT_ST_SRiX4K,

        PRODUCT_GENERIC_ISO14443SR,


        /***** Type 5 *****/
        PRODUCT_ST_LRi64,
        PRODUCT_ST_LRi512,
        PRODUCT_ST_LRi1K,
        PRODUCT_ST_LRi2K,

        PRODUCT_ST_LRiS2K,
        PRODUCT_ST_LRiS64K,

        PRODUCT_ST_M24LR01E_R,
        PRODUCT_ST_M24LR02E_R,
        PRODUCT_ST_M24LR04E_R,
        PRODUCT_ST_M24LR08E_R,
        PRODUCT_ST_M24LR16E_R,
        PRODUCT_ST_M24LR32E_R,
        PRODUCT_ST_M24LR64E_R,
        PRODUCT_ST_M24LR64_R,
        PRODUCT_ST_M24LR128E_R,
        PRODUCT_ST_M24LR256E_R,

        PRODUCT_ST_ST25DV64K_I,
        PRODUCT_ST_ST25DV64K_J,
        PRODUCT_ST_ST25DV16K_I,
        PRODUCT_ST_ST25DV16K_J,
        PRODUCT_ST_ST25DV04K_I,
        PRODUCT_ST_ST25DV04K_J,

        PRODUCT_ST_ST25TV02K,    //23
        PRODUCT_ST_ST25TV512,    //23
        PRODUCT_ST_ST25TV02K_EH, //34
        PRODUCT_ST_ST25DV02K_W,  //38
        PRODUCT_ST_ST25TV64K,    //48

        PRODUCT_GENERIC_TYPE5_AND_ISO15693,
        PRODUCT_GENERIC_TYPE5,

        /***** Other *****/
        PRODUCT_ST_RX95HF,
        PRODUCT_ST_ST95HF;

        // Override toString for a better display
        @Override
        public String toString() {
            if(name().startsWith("PRODUCT_ST_")) {
                return name().substring(11).replace('_', '-');
            } else if (name().startsWith("PRODUCT_GENERIC_")) {
                return name().substring(8);
            } else {
                return name();
            }
        }
    }


    static private int getManufacturerId(byte[] uid, NFCTag.NfcTagTypes tagType) throws STException {
        int id = 0;

        switch (tagType) {
            case NFC_TAG_TYPE_4B:
            case NFC_TAG_TYPE_V:
                id = uid[1] & 0xFF;
                break;

            case NFC_TAG_TYPE_4A:
                // ISO/IEC 14443 type A defines UID in section 6.5.4:
                // - single size UID (4 bytes) begins with 0x08 for random generated
                // number,
                // some other values are proprietary ones, others are RFU
                // - double (7 bytes) and triple size UIDs begin with 1 byte for
                // Manufacturer ID
                if (uid.length >= 7)
                    id = uid[0] & 0xFF;
                break;

                // Other tags are not supported for the moment
                // IC Manufacturer ID cannot be derived from an ISO/IEC 18092 tag, as
                // NFCID1 and NFCID2 are not standardized that way
            case NFC_TAG_TYPE_1:
            case NFC_TAG_TYPE_2:
            case NFC_TAG_TYPE_3:
            case NFC_TAG_TYPE_A:
                // Same for ISO/IEC 14443 type B tags, which PUPI is randomly
                // generated (ISO/IEC 14443-3, section 7.9.2);
                // we also don't know if the ID given by Android is PUPI or assigned
                // CID...
            case NFC_TAG_TYPE_B:
                // For JIS X6319-4, NFCID2 is defined in section 7.6.2:
                // - 8 bytes long
                // - 2 first bytes compose IC manufacturer code... but this code is
                // not defined in this spec...
            case NFC_TAG_TYPE_F:
            default:
                break;

        }

        // Prevent from any potential problem in case value has been badly read
        if (id > ICManufacturers.length) {
            id = 0;
        }

        return id;
    }

    static private int getProductCode(byte[] uid, NFCTag.NfcTagTypes tagType) throws STException {
        int productCode = 0;
        switch (tagType) {
            case NFC_TAG_TYPE_4B:
                if ((getManufacturerId(uid, tagType) == STM_MANUFACTURER_CODE) && (uid[0] == PROTOCOL_CODE_ISO14443B)) {
                    // Check that UID starts with D002
                    productCode = uid[2];
                } else
                    throw new STException(NOT_SUPPORTED);
                break;
            case NFC_TAG_TYPE_V:
                if (getManufacturerId(uid, tagType) == STM_MANUFACTURER_CODE)
                    productCode = uid[2];
                else
                    productCode = 1;
                break;

            case NFC_TAG_TYPE_4A:
                if (uid.length >= 7 && getManufacturerId(uid, tagType) == STM_MANUFACTURER_CODE)
                    productCode = uid[1] & 0xFF;
                break;
                // Other tags are not supported for the moment
                // IC Manufacturer ID cannot be derived from an ISO/IEC 18092 tag, as
                // NFCID1 and NFCID2 are not standardized that way
            case NFC_TAG_TYPE_1:
            case NFC_TAG_TYPE_2:
            case NFC_TAG_TYPE_3:
            case NFC_TAG_TYPE_A:
                // Same for ISO/IEC 14443 type B tags, which PUPI is randomly
                // generated (ISO/IEC 14443-3, section 7.9.2);
                // we also don't know if the ID given by Android is PUPI or assigned
                // CID...
            case NFC_TAG_TYPE_B:
                // For JIS X6319-4, NFCID2 is defined in section 7.6.2:
                // - 8 bytes long
                // - 2 first bytes compose IC manufacturer code... but this code is
                // not defined in this spec...
            case NFC_TAG_TYPE_F:
            default:
                break;
        }

        return productCode;
    }


    /**
     * Identify product
     *
     * @param readerInterface : tag interface to use to get the product ID
     * @param uid:
     * @return ProductID
     * @throws STException
     */
    static public ProductID identifyProduct(RFReaderInterface readerInterface, byte[] uid) throws STException {
        ProductID productId;

        // Use ICRef (from System Info) to identify this product
        productId = identifyTypeVProduct(readerInterface, uid);

        if (productId == PRODUCT_UNKNOWN) {
            productId = identifyType4Product(readerInterface, uid);
        }

        if (productId == PRODUCT_UNKNOWN) {
            productId = identifyIso14443SRProduct(readerInterface, uid);
        }

        if (productId == PRODUCT_UNKNOWN) {
            STLog.e("Product unknown!");
        }

        return productId;
    }

    static public ProductID identifyTypeVProduct(RFReaderInterface readerInterface, byte[] uid) {
        int productCode = 0;
        int icManufacturer = 0;
        ProductID productId = PRODUCT_UNKNOWN;
        Iso15693Command iso15693Command = new Iso15693Command(readerInterface, uid);

        // Identification of ST's Type5 tags
        try {

            icManufacturer = getManufacturerId(uid,NFCTag.NfcTagTypes.NFC_TAG_TYPE_V);
            productCode = getProductCode(uid,NFCTag.NfcTagTypes.NFC_TAG_TYPE_V);
            byte[] response = iso15693Command.getSystemInfo();

            // Min size for system info response is 10 Bytes (1 byte of flags, 1 byte of infoFlags and 8 bytes of UID)
            if (response.length >= 10) {
                if (icManufacturer == STM_MANUFACTURER_CODE) {
                    byte icRef = getStIcRefFromSystemInfo(response);
                    int nbrOfBlocks = getNbrOfBlocksFromSystemInfo(response);
                    productId = getType5ProductId(readerInterface, uid, icRef, nbrOfBlocks);
                } else {
                    // This tag is not from ST
                    productId = PRODUCT_GENERIC_TYPE5_AND_ISO15693;
                }
            }

        } catch (STException e) {
            if ((icManufacturer == STM_MANUFACTURER_CODE)
                    && ((productCode == 0x04) || (productCode == 0x05) || (productCode == 0x06) || (productCode == 0x07))
                    ) {
                // LRI512 doesn't support GetSystemInfo but can be recognized with its specific Product Code (4, 5, 6 or 7)
                productId = PRODUCT_ST_LRi512;
            } else {
                // This tag doesn't support the ISO15693's get system info command or is not a ST Type5 Tag
                productId = PRODUCT_UNKNOWN;
            }
        }

        if (productId == PRODUCT_UNKNOWN) {
            // Identification of ST's Vicinity tags
            try {
                VicinityCommand vicinityCommand = new VicinityCommand(readerInterface, uid);
                byte[] response = vicinityCommand.getSystemInfo();

                byte icRef = getStIcRefFromExtendedOrVicinitySystemInfo(response);
                // The nbrOfBlocks is not always present in vicinity tags system info and we don't need
                //  it for the identification of the tag so it doesn't matter.
                byte nbrOfBlocks = 0;
                productId = getType5ProductId(readerInterface, uid, icRef, nbrOfBlocks);

            } catch (STException e) {
                // This tag doesn't support the ISO15693's get system info command or is not a ST Vicinity Tag
                productId = PRODUCT_UNKNOWN;
            }
        }

        // Identification of tags using extendedGetSystemInfo
        if (productId == PRODUCT_UNKNOWN) {
            try {
                byte[] response = iso15693Command.extendedGetSystemInfo();

                // Min size for system info response is 10 Bytes (1 byte of flags, 1 byte of infoFlags and 8 bytes of UID)
                if (response.length >= 10) {
                    icManufacturer = response[8];

                    if (icManufacturer == STM_MANUFACTURER_CODE) {
                        byte icRef = getStIcRefFromExtendedOrVicinitySystemInfo(response);
                        // The nbrOfBlocks is not always present in vicinity tags system info and we don't need
                        //  it for the identification of the tag so it doesn't matter.
                        byte nbrOfBlocks = 0;
                        productId = getType5ProductId(readerInterface, uid, icRef, nbrOfBlocks);
                    } else {
                        // This tag is not from ST
                        productId = PRODUCT_GENERIC_TYPE5_AND_ISO15693;
                    }
                }

            } catch (STException e) {
                productId = PRODUCT_UNKNOWN;
            }
        }
        return productId;
    }

    // Parse the ISO15693's system info to get ST's ICRef
    static private byte getStIcRefFromSystemInfo(byte[] buffer) throws STException {

        if (buffer.length < 2) {
            throw new STException(BAD_PARAMETER);
        }

        int icRefOffset = 10;
        byte infoFlags = buffer[1];

        if ((infoFlags & ICREF_MASK) != ICREF_MASK) {
            // No ICRef
            throw new STException(BAD_PARAMETER);
        }

        if ((infoFlags & DSFID_MASK) == DSFID_MASK) {
            icRefOffset++;
        }

        if ((infoFlags & AFI_MASK) == AFI_MASK) {
            icRefOffset++;
        }

        if ((infoFlags & VICC_MEM_SIZE_MASK) == VICC_MEM_SIZE_MASK) {
            // MemSize is on 2 bytes
            icRefOffset = icRefOffset + 2;
        }

        // Check that buffer contains enough items to access to the ICRef
        if (buffer.length < (icRefOffset + 1)) {
            throw new STException(BAD_PARAMETER);
        }

        byte icRef = buffer[icRefOffset];
        STLog.i("icRef = 0x" + Helper.convertByteToHexString(icRef));

        return icRef;
    }


    // Parse the ISO15693's system info to get number of blocks contained in the memory
    static private int getNbrOfBlocksFromSystemInfo(byte[] buffer) throws STException {
        int nbrOfBlocks, blockSizeInBytes;

        if (buffer.length < 2) {
            throw new STException(BAD_PARAMETER);
        }

        int memSizeOffset = 10;
        byte infoFlags = buffer[1];

        if ((infoFlags & DSFID_MASK) == DSFID_MASK) {
            memSizeOffset++;
        }

        if ((infoFlags & AFI_MASK) == AFI_MASK) {
            memSizeOffset++;
        }

        if ((infoFlags & VICC_MEM_SIZE_MASK) != VICC_MEM_SIZE_MASK) {
            // MemSize not present
            return 0;
        }

        // Check that the response contains enough items to read the Mem Size field.
        // This field is on 2 bytes so we're going to read the data at memSizeOffset and (memSizeOffset+1) offsets.
        if ((memSizeOffset + 1) < buffer.length) {
            nbrOfBlocks = (buffer[memSizeOffset]  & 0xFF) + 1;
            blockSizeInBytes = (buffer[memSizeOffset+1] & 0xFF) + 1;

        } else {
            throw new STException(BAD_PARAMETER);
        }

        STLog.i("nbrOfBlocks = " + nbrOfBlocks);
        STLog.i("blockSizeInBytes = " + blockSizeInBytes);

        return nbrOfBlocks;
    }

    // Parse the system info response of a Vicinity or a high density type 5 tag to get ST's ICRef
    static private byte getStIcRefFromExtendedOrVicinitySystemInfo(byte[] buffer) throws STException {
        // Min size is 10 Bytes (1 byte of flags, 1 byte of infoFlags and 8 bytes of UID)
        if (buffer.length < 10) {
            throw new STException(BAD_PARAMETER);
        }

        int icRefOffset = 10;
        byte infoFlags = buffer[1];
        byte icManufacturer = buffer[8];

        if (icManufacturer != STM_MANUFACTURER_CODE) {
            // This is not a ST tag
            throw new STException(BAD_PARAMETER);
        }

        if ((infoFlags & ICREF_MASK) != ICREF_MASK) {
            // No ICRef
            throw new STException(BAD_PARAMETER);
        }

        if ((infoFlags & DSFID_MASK) == DSFID_MASK) {
            icRefOffset++;
        }

        if ((infoFlags & AFI_MASK) == AFI_MASK) {
            icRefOffset++;
        }

        if ((infoFlags & VICC_MEM_SIZE_MASK) == VICC_MEM_SIZE_MASK) {
            // On high density and Vicinity tags, MemSize is on 3 bytes
            icRefOffset = icRefOffset + 3;
        }

        // Check that buffer contains enough items to access to the ICRef
        if (buffer.length < (icRefOffset + 1)) {
            throw new STException(BAD_PARAMETER);
        }

        byte icRef = buffer[icRefOffset];
        STLog.i("icRef = 0x" + Helper.convertByteToHexString(icRef));

        return icRef;
    }

    static private ProductID getType5ProductId(RFReaderInterface readerInterface, byte[] uid, byte icRef, int nbrOfBlocks) {
        ProductID productId;

        switch(icRef) {
            // LRi and LRiS family
            case 0x14:
            case 0x15:
            case 0x16:
                productId = PRODUCT_ST_LRi64;
                break;
            case 0x20:
            case 0x21:
            case 0x22:
                productId = PRODUCT_ST_LRi2K;
                break;
            case 0x28:
            case 0x29:
            case 0x2A:
                productId = PRODUCT_ST_LRiS2K;
                break;
            case 0x40:
            case 0x41:
            case 0x42:
                productId = PRODUCT_ST_LRi1K;
                break;
            case 0x44:
                productId = PRODUCT_ST_LRiS64K;
                break;

                // M24LR family
            case 0x2C:
                productId = PRODUCT_ST_M24LR64_R;
                break;
            case 0x5E:
                productId = PRODUCT_ST_M24LR64E_R;
                break;
            case 0x4E:
                productId = PRODUCT_ST_M24LR16E_R;
                break;
            case 0x5A:
                productId = PRODUCT_ST_M24LR04E_R;
                break;

                // ST25DV family
            case 0x24:
            case 0x26:
                // For ST25DV, the memory size is needed to conclude. It should be obtained from "extended get system info"
                productId = identifyST25DVProduct(readerInterface, uid);
                break;

                // ST25TV family
            case 0x23:
                // For ST25TV, the memory size is needed to conclude
                if(nbrOfBlocks == 64) {
                    productId = PRODUCT_ST_ST25TV02K;
                } else if(nbrOfBlocks == 16) {
                    productId = PRODUCT_ST_ST25TV512;
                } else {
                    productId = PRODUCT_UNKNOWN;
                }
                break;
            case 0x34:
                productId = PRODUCT_ST_ST25TV02K_EH;
                break;
            case 0x38:
                productId = PRODUCT_ST_ST25DV02K_W;
                break;
            case 0x48:
                productId = PRODUCT_ST_ST25TV64K;
                break;

            default:
                productId = PRODUCT_GENERIC_TYPE5_AND_ISO15693;
                break;
        }

        return productId;
    }

    static private ProductID identifyST25DVProduct(RFReaderInterface readerInterface, byte[] uid) {
        ProductID productId;

        // A read of "extended get system info" is necessary to identify what kind of ST25DV tag it is
        try {
            Iso15693Command iso15693Command = new Iso15693Command(readerInterface, uid);
            byte[] response = iso15693Command.extendedGetSystemInfo();

            // Min response size is 10 Bytes (1 byte of flags, 1 byte of infoFlags and 8 bytes of UID)
            if (response.length < 10) {
                throw new STException(BAD_PARAMETER);
            }

            int viccMemSizeOffset = 10;
            byte infoFlags = response[1];

            if ((infoFlags & DSFID_MASK) == DSFID_MASK) {
                viccMemSizeOffset++;
            }

            if ((infoFlags & AFI_MASK) == AFI_MASK) {
                viccMemSizeOffset++;
            }

            // Check that the response contains enough items to read the viccMemSize
            // viccMemSize is on 3 bytes but only the first 2 bytes matter because they contain the number of blocks
            if (response.length < (viccMemSizeOffset + 3)) {
                throw new STException(BAD_PARAMETER);
            }

            int nbrOfBlocks = ((response[viccMemSizeOffset + 1] & 0xFF) << 8) + (response[viccMemSizeOffset] & 0xFF);

            byte productCode = (byte) getProductCode(uid,NFCTag.NfcTagTypes.NFC_TAG_TYPE_V);
            // Default is Unknown
            productId = PRODUCT_UNKNOWN;
            if (nbrOfBlocks == 0x7F) {
                if(productCode == 0x24) {
                    productId = PRODUCT_ST_ST25DV04K_I;
                } else if(productCode == 0x25) {
                    productId = PRODUCT_ST_ST25DV04K_J;
                }
            } else if (nbrOfBlocks == 0x1FF) {
                if(productCode == 0x26) {
                    productId = PRODUCT_ST_ST25DV16K_I;
                } else if(productCode == 0x27) {
                    productId = PRODUCT_ST_ST25DV16K_J;
                }
            } else if (nbrOfBlocks == 0x7FF) {
                if(productCode == 0x26) {
                    productId = PRODUCT_ST_ST25DV64K_I;
                } else if(productCode == 0x27) {
                    productId = PRODUCT_ST_ST25DV64K_J;
                }
            }

        } catch (STException e) {
            e.printStackTrace();
            productId = PRODUCT_UNKNOWN;
        }

        return productId;
    }

    static public ProductID identifyType4Product(RFReaderInterface readerInterface, byte[] uid) {
        ProductID productId;

        try {
            Type4Tag type4Tag = new Type4Tag(readerInterface, uid);

            NFCTag.NfcTagTypes tagType = type4Tag.getType();
            int productCode = getProductCode(uid, tagType);
            int manufacturerID = getManufacturerId(uid, tagType);

            productId = getType4ProductId(manufacturerID, productCode);
        }
        catch (STException e) {
            // This tag doesn't seem to be a Type 4A tag.
            productId = PRODUCT_UNKNOWN;
        }

        return productId;
    }

    static public ProductID getType4ProductId(int manufacturerID, int productCode) {
        ProductID productId = ProductID.PRODUCT_GENERIC_TYPE4;

        if (manufacturerID == STM_MANUFACTURER_CODE) {
            // ISO14443A ST products
            if ((productCode == 0x82) || (productCode == 0x8A)) {
                productId = ProductID.PRODUCT_ST_M24SR02_Y;
            } else if (productCode == 0x86) {
                productId = ProductID.PRODUCT_ST_M24SR04;
            } else if ((productCode == 0x85) || (productCode == 0x8D)) {
                productId = ProductID.PRODUCT_ST_M24SR16_Y;
            } else if ((productCode == 0x84) || (productCode == 0x8C)) {
                productId = ProductID.PRODUCT_ST_M24SR64_Y;
            } else if (productCode == 0x80) {
                productId = ProductID.PRODUCT_ST_RX95HF;
            } else if (productCode == 0xE5) {
                productId = ProductID.PRODUCT_ST_ST25TA512;
            } else if (productCode == 0xD5) {
                productId = ProductID.PRODUCT_ST_ST25TA512_K;
            } else if (productCode == 0xE4) {
                productId = ProductID.PRODUCT_ST_ST25TA512_G;       // ST25TA02K cut 2.2
            } else if (productCode == 0xE2) {
                productId = ProductID.PRODUCT_ST_ST25TA02K;
            } else if (productCode == 0xE3) {
                productId = ProductID.PRODUCT_ST_ST25TA02K_G;       // ST25TA02K cut 2.2
            } else if (productCode == 0xA2) {
                productId = ProductID.PRODUCT_ST_ST25TA02K_P;
            } else if (productCode == 0xA3) {
                productId = ProductID.PRODUCT_ST_ST25TA02K_GP;       // ST25TA02K cut 2.2
            } else if (productCode == 0xF2) {
                productId = ProductID.PRODUCT_ST_ST25TA02K_D;
            } else if (productCode == 0xF3) {
                productId = ProductID.PRODUCT_ST_ST25TA02K_GD;       // ST25TA02K cut 2.2
            } else if (productCode == 0xC5) {
                productId = ProductID.PRODUCT_ST_ST25TA16K;

            } else if (productCode == 0xC4) {
                productId = ProductID.PRODUCT_ST_ST25TA64K;
            }

            // OLD T4A products replaced ...
            //else if (productCode == 0xC4) {
            //    productId = ProductID.PRODUCT_ST_T24SR64;
            else if (productCode == 0xC2) {
                productId = ProductID.PRODUCT_ST_SRTAG2K;
                //} else if (productCode == 0xD2) {
                //    productId = ProductID.PRODUCT_ST_SRTAG2K;
            } else if (productCode == 0x81) {
                productId = ProductID.PRODUCT_ST_M24SR01;
            } else if (productCode == 0x83) {
                productId = ProductID.PRODUCT_ST_M24SR08;
            } else if (productCode == 0x87) {
                productId = ProductID.PRODUCT_ST_M24SR32;
            } else if (productCode == 0xB5) {
                productId = ProductID.PRODUCT_ST_ST25TA16K_D;
            } else if (productCode == 0xC6) {
                productId = ProductID.PRODUCT_ST_ST25TA04K;
            } else if (productCode == 0xB6) {
                productId = ProductID.PRODUCT_ST_ST25TA04K_D;
            } else if (productCode == 0xB2) {
                productId = ProductID.PRODUCT_ST_ST25TA02K_N;
            } else if (productCode == 0xE1) {
                productId = ProductID.PRODUCT_ST_ST25TA01K_G;
            } else if (productCode == 0xF1) {
                productId = ProductID.PRODUCT_ST_ST25TA01K_GD;
            } else if (productCode == 0xE0) {
                productId = ProductID.PRODUCT_ST_ST25TA256_G;
            } else if (productCode == 0xF0) {
                productId = ProductID.PRODUCT_ST_ST25TA256_GD;
            } else if (productCode == 0xD2) {
                productId = ProductID.PRODUCT_ST_ST25TA64K_D;
            }
        }

        return productId;
    }

    static public ProductID identifyIso14443SRProduct(RFReaderInterface readerInterface, byte[] uid) {
        ProductID productId;
        try {
            STIso14443SRTag type4Tag = new STIso14443SRTag(readerInterface, uid);

            NFCTag.NfcTagTypes tagType = type4Tag.getType();
            int productCode = getProductCode(uid, tagType);
            int manufacturerID = getManufacturerId(uid, tagType);

            productId = getIso14443SRProductId(manufacturerID, productCode);
        }
        catch (STException e) {
            // This tag doesn't seem to be a Iso14443SR tag.
            productId = PRODUCT_UNKNOWN;
        }

        return productId;
    }

    static public ProductID getIso14443SRProductId(int manufacturerID, int productCode) {
        ProductID productId = ProductID.PRODUCT_UNKNOWN;

        if (manufacturerID == STM_MANUFACTURER_CODE) {
            // Iso14443SR
            if (productCode == 0x1C || productCode == 0x1D || productCode == 0x1E) {
                productId = ProductID.PRODUCT_ST_SRi4K;
            } else if (productCode == 0x3C || productCode == 0x3D || productCode == 0x3E) {
                productId = ProductID.PRODUCT_ST_SRi2K;
            } else if (productCode == 0x18 || productCode == 0x19 || productCode == 0x1A) {
                productId = ProductID.PRODUCT_ST_SRi512;
            } else if (productCode == 0x30 || productCode == 0x31 || productCode == 0x32) {
                productId = ProductID.PRODUCT_ST_SRT512;
            } else if (productCode == 0x1F) {
                productId = ProductID.PRODUCT_ST_ST25TB04K;
            } else if (productCode == 0x3F) {
                productId = ProductID.PRODUCT_ST_ST25TB02K;
            } else if (productCode == 0x1B) {
                productId = ProductID.PRODUCT_ST_ST25TB512_AC;
            } else if (productCode == 0x33) {
                productId = ProductID.PRODUCT_ST_ST25TB512_AT;
            } else if (productCode == 0x0D) {
                productId = ProductID.PRODUCT_ST_SRiX4K;
            } else {
                productId = ProductID.PRODUCT_GENERIC_ISO14443SR;
            }
        }

        return productId;
    }


    /**
     * @param password on 4 or 8 Bytes
     * @param randomNumber on 2 Bytes
     * @return true in case of success
     */
    public static boolean xorBetweenPwdAndRandomNbr(byte[] password, final byte[] randomNumber) {

        if (randomNumber.length != 2) {
            STLog.e("Invalid randomNumber length: " + randomNumber.length);
            return false;
        }

        switch (password.length) {
            case 4:
                // 32 bits passwords
                password[0] = (byte) (password[0] ^ randomNumber[0]);
                password[1] = (byte) (password[1] ^ randomNumber[1]);
                password[2] = (byte) (password[2] ^ randomNumber[0]);
                password[3] = (byte) (password[3] ^ randomNumber[1]);
                break;
            case 8:
                // 64 bits passwords
                password[0] = (byte) (password[0] ^ randomNumber[0]);
                password[1] = (byte) (password[1] ^ randomNumber[1]);
                password[2] = (byte) (password[2] ^ randomNumber[0]);
                password[3] = (byte) (password[3] ^ randomNumber[1]);
                password[4] = (byte) (password[4] ^ randomNumber[0]);
                password[5] = (byte) (password[5] ^ randomNumber[1]);
                password[6] = (byte) (password[6] ^ randomNumber[0]);
                password[7] = (byte) (password[7] ^ randomNumber[1]);
                break;
            default:
                STLog.e("Invalid password length: " + password.length);
                return false;
        }
        return true;
    }

    public static enum ReadWriteProtection {
        READABLE_AND_WRITABLE,
        READABLE_AND_WRITE_PROTECTED_BY_PWD,
        READ_AND_WRITE_PROTECTED_BY_PWD,
        READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE,
        READABLE_AND_WRITE_IMPOSSIBLE,         // For ST25DV RFA1SS
        WRITEABLE_AND_READ_PROTECTED_BY_PWD    // This mode is pointless but can be set on ST Type4 tags
    }
}
