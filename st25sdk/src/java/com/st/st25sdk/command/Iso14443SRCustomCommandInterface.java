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

import com.st.st25sdk.STException;

public interface Iso14443SRCustomCommandInterface {
    /**
     * initiate() is used to initiate the anticollision sequence of the ST Iso14443SR-type tag.
     * On receiving the initiate() command, all devices in Ready state switch to the INVENTORY state,
     * set a new 8-bit chipId random value, and return their Chip_ID value.
     * This command is useful when only one tag in Ready state is present in the reader field range.
     * It speeds up the Chip_ID search process.
     *
     * @return chipId 8-bit random number returned by the only tag in the RF field
     * @throws STException {@link}STException
     */
    byte initiate() throws STException;


    /**
     * After receiving the pCall16() command, the ST Iso14443SR-type tag generates a new random chip slot
     * number value.
     * A slotNumber is defined as the 4 least significant bites of the chipId - value between 0 and 15.
     * If this slotNumber value = 0b000, the tag returns its chipID value.
     * If slotNumber != 0b0000, the tag doesn't answer.
     *
     * When used with the slotMarker() command, pCall16() allows the reader to search for all the chipIDs
     * of tags present in the RF field.
     *
     * @return chipID 8-bit random number returned by the tag that responds in slot 0
     * @throws STException {@link}STException
     */
    byte pCall16() throws STException;


    /**
     * After receiving the slotMarker({@link}slotNumber) command, the ST Iso14443SR-type tag compares the 4 least
     * significant bits of its chipID to {@link}slotNumber.
     * If they match, the tag returns its chipID.
     * If not, the tag doesn't answer.
     *
     * The anti-collision sequence starts with the pCall16() command, then sends slotMarker({@link}slotNumber)
     * commands, varying {@link}slotNumber from 1 to 15 and collecting tag chipIds in the process.
     * This anticollision sequence is repeated until no collision is detected.
     *
     * @param slotNumber Only the 4 least significant bits are passed as parameter
     * @return chipID 8-bit random number returned by the tag that responds in slot {@link}slotNumber
     * @throws STException {@link}STException
     */
    byte slotMarker(byte slotNumber) throws STException;


    /**
     * The select({@link}chipID) command moves the targeted ST Iso14443SR-type tag into the SELECTED state.
     * Until the select() command is sent, the tag will only respond to initiate(), pCall16() and slotMarker()
     * commands.
     *
     * When a ST Iso14443SR-type tag receives a select({@link}chipID) command with a {@link}chipID that does not match its
     * own, it moves into the DESELECTED state.
     *
     * @param chipID 8-bit random number
     * @return chipID 8-bit random number returned by the SELECTED tag
     * @throws STException {@link}STException
     */
    byte select(byte chipID) throws STException;


    /**
     * After receiving the completion() command, any ST Iso14443SR-type tag in the SELECTED state transitions
     * to the DEACTIVATED state and stops decoding any new command.
     * The tag moves out of the DEACTIVATED state only after it is removed from the RF field.
     * Another ST Iso14443SR-type tag can then be accessed through a select() command without having to
     * move the other tag from the RF field.
     * Tags not in the SELECTED state ignore the completion() command.
     *
     * @throws STException {@link}STException
     */
    void completion() throws STException;


    /**
     * After receiving the resetToInventory() command, all ST Iso14443SR-type tag in the SELECTED state revert
     * to the INVENTORY state.
     * This command is useful when two ST Iso14443SR-type tags with the same cipID happen to be SELECTED at
     * the same time. Forcing a new anticollision sequence allows the tags to generate new random chipIds.
     * Tags not in the SELECTED state ignore the resetToInventory() command.
     *
     * @throws STException {@link}STException
     */
    void resetToInventory() throws STException;


    /**
     * Reads the desired memory blocks of the ST iso14443SR-type tag in the SELECTED state.
     * Data bytes are transmitted with the least significant byte first.
     *
     * Tags that are not in the SELECTED state will not reply to the readBlock() command.
     *
     * @param blockAddress Memory location to read. If blockAddress is bigger than the tag's memory capacity,
     *  the tag will not respond.
     * @return 4 data bytes contained in the desired memory block.
     * @throws STException {@link}STException
     */
    byte[] readBlock(byte blockAddress) throws STException;


    /**
     * Writes the four bytes of {@link}buffer at address {@link}blockAddress of the SELECTED ST Iso14443SR-type tag
     * (provided that the block is available and not write-protected).
     * Data bytes to write must be transmitted with the least significant byte first.
     *
     * There is no tag response after a writeBlock() command. The reader must check that data was correctly
     * programmed by reading the memory location after programming time tW defined in the datasheet.
     *
     * Tags that are not in the SELECTED state will not reply to the writeBlock() command.
     *
     * @param blockAddress Memory location to write. If {@link}blockAddress is bigger than the tag's memory capacity,
     *  the tag will not respond. The system area is located at address 255.
     * @param buffer 4-byte little endian data
     * @throws STException {@link}STException
     */
    void writeBlock(byte blockAddress, byte[] buffer) throws STException;


    /**
     * Asks the SELECTED ST Iso14443SR-type tag for its 8-byte UID (transmitted LSB first).
     *
     * Tags that are not in the SELECTED state will not reply to the getUid() command.
     *
     * @return 8-byte little-endian UID
     * @throws STException {@link}STException
     */
    byte[] getUid() throws STException;
}
