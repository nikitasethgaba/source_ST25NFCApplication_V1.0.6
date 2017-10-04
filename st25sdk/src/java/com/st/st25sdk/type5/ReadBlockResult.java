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

package com.st.st25sdk.type5;

import java.util.Arrays;


/**
 * Class containing the response of Iso15693 readBlocks() used for Type5 and Vicinity commands.
 *
 * blockSecurityStatus is initialized with 0xFF and is
 * - set to 0 when a block is not locked
 * - set to 1 when a block is read-only
 *
 * data is an array containing the block's data.
 *
 * @author STMicroelectronics
 *
 */
public class ReadBlockResult {
    public byte[] blockSecurityStatus;
    public byte[] data;

    public ReadBlockResult(int numberOfBlocks, int nbrOfBytesPerBlock) {

        blockSecurityStatus = new byte[numberOfBlocks];
        data = new byte[numberOfBlocks * nbrOfBytesPerBlock];

        // Initialize BSS buffer with 0xFF
        Arrays.fill(blockSecurityStatus, (byte) 0xFF);
    }
}
