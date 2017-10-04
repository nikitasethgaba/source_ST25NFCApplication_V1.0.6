package com.st.st25sdk.tests.type5.st25tv;

import com.st.st25sdk.STException;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.type5.ST25TVTag;

import static com.st.st25sdk.type5.ST25TVTag.ST25TV_AREA1_PASSWORD_ID;

public class ST25TVUtils {

    static void presentConfigurationPassword(ST25TVTag st25TVTag, byte[] password) throws STException {

        // Get a 16 bits random number
        byte[] randomNumber = st25TVTag.getRandomNumber();
        TagHelper.xorBetweenPwdAndRandomNbr(password, randomNumber);

        // Enter Config password
        st25TVTag.presentPassword(ST25TVTag.ST25TV_CONFIGURATION_PASSWORD_ID, password);
    }


    /**
     * Warning: This function is NOT locking permanently the Configuration registers.
     *          It is only reactivating the configuration protection which means that the
     *          configuration password should be presented before any change in configuration registers.
     */
    static void lockConfiguration(ST25TVTag st25TVTag) {

        // Trick to be sure thar the Configuration area is locked: Present another password (: Area1 pwd)
        byte[] password = new byte[]{0x00, 0x00, 0x00, 0x01};
        try {
            st25TVTag.presentPassword(ST25TV_AREA1_PASSWORD_ID, password);
        } catch (STException e) {
            // Ignore any exception
        }

    }

}
