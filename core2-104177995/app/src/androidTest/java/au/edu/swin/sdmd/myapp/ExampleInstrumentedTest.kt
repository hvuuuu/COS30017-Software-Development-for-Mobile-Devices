package au.edu.swin.sdmd.myapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity>
            = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkBorrowButtonIsDisplayed() {
        onView(withId(R.id.borrow)).check(matches(isDisplayed()))
    }

    @Test
    fun checkNextButtonFunctionality() {
        // Click on the "Next" button
        onView(withId(R.id.next)).perform(click())

        // Check if the name of the boot has changed
        onView(withId(R.id.name)).check(matches(not(withText("Nike Mercurial Superfly 4 Black"))))
    }

    @Test
    fun bootInitialization() {
        val boot = Boot("Nike Mercurial Superfly 4 Black", 4.0, "su4", 800, "mercurial_black")

        assertEquals("Nike Mercurial Superfly 4 Black", boot.name)
        assertEquals(4.0, boot.rating, 0.001)
        assertEquals("su4", boot.cate)
        assertEquals(800, boot.price)
        assertEquals("mercurial_black", boot.image)
        assertNull(boot.dueBackDate)
    }
}