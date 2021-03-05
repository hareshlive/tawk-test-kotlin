package com.app.tawktest
import android.content.Context
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.app.tawktest.ui.UserProfileActivity
import junit.framework.Assert.assertEquals
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

class NoteUnitTest {

    private val FAKE_STRING = "Please enter your note"
    @Mock
    var mMockContext: Context? = null

    @Test
    fun readStringFromContext_LocalizedString() {
        val myObjectUnderTest = UserProfileActivity()

        // ...when the string is returned from the object under test...
        val result: String = myObjectUnderTest.validate("").toString()

        // ...then the result should be the expected one.
        assertThat(result, `is`(FAKE_STRING))
    }

}