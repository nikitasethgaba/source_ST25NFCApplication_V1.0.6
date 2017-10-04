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

import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.command.VicinityMemoryCommand;

import java.nio.ByteBuffer;

import static com.st.st25sdk.STException.STExceptionCode.INVALID_CCFILE;
import static com.st.st25sdk.STException.STExceptionCode.INVALID_DATA;

public class CCFileVicinity extends CCFile {

    protected VicinityMemoryCommand mVicinityCommand;

    public CCFileVicinity(VicinityMemoryCommand vicinityCommand) {
        super();
        mVicinityCommand = vicinityCommand;
    }


    @Override
    public byte[] read() throws STException {

        if (!mCacheActivated || mCacheInvalidated) {
            ReadBlockResult readResponse;
            byte[] buffer;

            // Read first CCFile Block
            readResponse = mVicinityCommand.readBlocks(0x00, 0x01);
            buffer = readResponse.data;
            // buffer contains the block's data
            // Test buffer size versus the minimum possible value allowed by the ISO standard
            if (buffer.length < 4) {
                throw new STException(INVALID_DATA);
            }

            if (mBlockSize == 0) {
                // This is the first CCFile block that we read for this tag.
                // We use the length of the response to know the size of a block.
                // The block size is the size of the data array in the response.
                mBlockSize = buffer.length;
                STLog.i("mBlockSize = " + mBlockSize);
            }

            // No exception -> The command was successful
            // Now check the message content
            if (buffer[0] == CCFILE_SHORT_IDENTIFIER || buffer[0] == CCFILE_LONG_IDENTIFIER) {
                if (buffer[2] == 0x00) {
                    mCCLength = 8;
                } else {
                    mCCLength = 4;
                }
            } else {
                // This is NOT a valid CCFile
                throw new STException(INVALID_CCFILE);
            }

            ByteBuffer ccFileContent = ByteBuffer.allocate(mCCLength);
            // Copy the first block of the CCFile
            ccFileContent.put(buffer, 0, mBlockSize);


            if (mBlockSize < mCCLength) {
                // More data in CCFile than a block can contain.
                // Read the second block of the CC File
                readResponse = mVicinityCommand.readBlocks(0x01, 1);
                buffer = readResponse.data;

                ccFileContent.put(buffer, 0, mBlockSize);
            }

            mBuffer = ccFileContent.array();
        }

        return mBuffer;
    }

        @Override
    public void write() throws STException {
        mVicinityCommand.writeBlocks(0x00, mBuffer);
    }

}
