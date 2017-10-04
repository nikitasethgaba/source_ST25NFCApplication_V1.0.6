package com.st.st25sdk.tests.type5.st25tv;


import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.type5.ST25TVTag;

import org.junit.Assert;

import static com.st.st25sdk.type5.ST25TVTag.ST25TV_AREA1_PASSWORD_ID;
import static com.st.st25sdk.type5.ST25TVTag.ST25TV_CONFIGURATION_PASSWORD_ID;
import static org.junit.Assert.fail;

public class ST25TVTestCounter {

    static public void run(ST25TVTag st25TVTag) throws STException {
        byte[] password;

        // Read Counter Configuration
        STRegister counterConfigRegister = st25TVTag.getRegister(ST25TVTag.ST25TV_REGISTER_WRITE_COUNTER_CONFIGURATION);
        int configValue = counterConfigRegister.getRegisterValue();

        // Check that isCounterEnabled is consistent with configValue
        boolean isCounterEnabled = st25TVTag.isCounterEnabled();
        if((configValue & 0x01) == 0x01) {
            // 1b: Counter is enabled
            Assert.assertEquals(true, isCounterEnabled);
        } else {
            // 0b: Counter is disabled
            Assert.assertEquals(false, isCounterEnabled);
        }

        // Read Counter Value
        int counterValue = st25TVTag.readCounterValue();

        // Enable the Counter in order to start from a predictable state
        password = new byte[] {0x00, 0x00, 0x00, 0x00};
        ST25TVUtils.presentConfigurationPassword(st25TVTag, password);
        st25TVTag.enableCounter(true);

        //Ensure that the Configuration area is protected by password
        ST25TVUtils.lockConfiguration(st25TVTag);

        // Try changing the Counter value without presenting the Configuration password
        try {
            st25TVTag.enableCounter(false);
            fail("An exception should have been raised");
        } catch (STException e) {
            Assert.assertEquals(STException.STExceptionCode.CONFIG_PASSWORD_NEEDED, e.getError());
        }

        // Present the Configuration password
        password = new byte[] {0x00, 0x00, 0x00, 0x00};
        ST25TVUtils.presentConfigurationPassword(st25TVTag, password);

        // Disable Counter and check the counter configuration
        st25TVTag.enableCounter(false);
        isCounterEnabled = st25TVTag.isCounterEnabled();
        Assert.assertEquals(false, isCounterEnabled);

        // Enable Counter and check the counter configuration
        st25TVTag.enableCounter(true);
        isCounterEnabled = st25TVTag.isCounterEnabled();
        Assert.assertEquals(true, isCounterEnabled);

        // Reset Counter (which also disable it)
        st25TVTag.resetCounter();

        isCounterEnabled = st25TVTag.isCounterEnabled();
        Assert.assertEquals(false, isCounterEnabled);

        // Check Counter value
        counterValue = st25TVTag.readCounterValue();
        Assert.assertEquals(0, counterValue);
    }

}
