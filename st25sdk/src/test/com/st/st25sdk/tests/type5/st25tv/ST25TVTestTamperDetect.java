package com.st.st25sdk.tests.type5.st25tv;


import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.type5.ST25TVTag;

import org.junit.Assert;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ST25TVTestTamperDetect {

    static public void run(ST25TVTag st25TVTag) throws STException {

        STRegister tamperDetectRegister = st25TVTag.getRegister(ST25TVTag.ST25TV_REGISTER_TAMPER_CONFIGURATION);

        int tamperValue = tamperDetectRegister.getRegisterField("TAMPER_STATE").getValue();

        assertThat(tamperValue, anyOf(is(0), is(1)));

        // retrieve the same information through the st25TVTag API
        boolean isTamperDetected = st25TVTag.getRegisterTamperConfiguration().isTamperDetected();
        // Check that both info is consistent
        if(isTamperDetected) {
            Assert.assertEquals(0, tamperValue);
        } else {
            Assert.assertEquals(1, tamperValue);
        }

    }
}
