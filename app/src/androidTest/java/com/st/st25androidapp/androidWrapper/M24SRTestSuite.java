package com.st.st25androidapp.androidWrapper;

import android.util.Log;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.tests.type4.M24SR.M24SRTests;
import com.st.st25sdk.type4a.m24srtahighdensity.M24SR64KTag;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Runs all unit tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({M24SRTests.class})
public class M24SRTestSuite {

    private static final String TAG = "M24SRTestSuite";

    @BeforeClass
    public static void onceExecutedBeforeAllTests() throws STException {
        boolean result;
        NFCTag tag;
        int tagMemSizeInBytes;
        M24SR64KTag m24srTag;
        String tagName;

        // NB: Called before the instanciation of MainActivity
        Log.v(TAG, "@BeforeClass: onceExecutedBeforeAllTests");

        result = AndroidHelper.startApplication();
        assertTrue(result);

        tag = AndroidHelper.selectTagSlot(AndroidHelper.Slot.SLOT4_TAG_ST25TA);
        assertNotNull(tag);

        try {
            m24srTag = (M24SR64KTag) tag;
        } catch (ClassCastException e) {
            throw new STException("### This is not a M24SR64KTag ! ###");
        }

        // Pass the tag to JUnit
        M24SRTests.setTag(m24srTag);

        Log.v(TAG, "M24SR64KTag tests initialization done successfully");
    }

}
