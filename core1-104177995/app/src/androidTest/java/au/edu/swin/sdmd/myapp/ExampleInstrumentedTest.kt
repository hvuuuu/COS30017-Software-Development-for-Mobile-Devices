import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import au.edu.swin.sdmd.myapp.DiceRoller
import au.edu.swin.sdmd.myapp.MainActivity
import au.edu.swin.sdmd.myapp.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
class MockDiceRoller(private val fixedRolls: List<Int>) : DiceRoller {
    private var index = 0
    override fun roll(): Int {
        val result = fixedRolls[index % fixedRolls.size]
        index++
        return result
    }
}
@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private var myAddButton = 0
    private var mySubtractButton = 0
    private var myRollButton = 0
    private var myResetButton = 0
    private var myScoreTextField = 0

    @Before
    fun initValidString() {
        // Please set your id names here.
        myAddButton = R.id.add_button
        mySubtractButton = R.id.subtract_button
        myRollButton = R.id.roll_button
        myResetButton = R.id.reset_button
        myScoreTextField = R.id.score
    }

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private fun setMockDiceRoller(fixedRolls: List<Int>) {
        mActivityScenarioRule.scenario.onActivity { activity ->
            activity.setDiceRoller(MockDiceRoller(fixedRolls))
        }
    }

    @Test
    fun clickRollAddButton3Times() {
        setMockDiceRoller(listOf(2, 3, 5)) // Fixed dice rolls

        val addButton = onView(withId(myAddButton))
        val rollButton = onView(withId(myRollButton))

        for (i in 1..3) {
            rollButton.perform(click())
            addButton.perform(click())
        }

        val textView = onView(withId(myScoreTextField))
        textView.check(matches(withText("10"))) // 2 + 3 + 5 = 10
    }

    @Test
    fun clickRollAddSubtractButton3Times() {
        setMockDiceRoller(listOf(6, 3, 5)) // Fixed dice rolls

        val addButton = onView(withId(myAddButton))
        val subtractButton = onView(withId(mySubtractButton))
        val rollButton = onView(withId(myRollButton))

        rollButton.perform(click())
        addButton.perform(click())
        rollButton.perform(click())
        subtractButton.perform(click())
        rollButton.perform(click())
        addButton.perform(click())

        val textView = onView(withId(myScoreTextField))
        textView.check(matches(withText("8"))) // 6 - 3 + 5 = 8
    }

    @Test
    fun testLowerLimitsOfScore() {
        setMockDiceRoller(listOf(6, 2, 4)) // Fixed dice rolls

        val addButton = onView(withId(myAddButton))
        val subtractButton = onView(withId(mySubtractButton))
        val rollButton = onView(withId(myRollButton))

        rollButton.perform(click())
        addButton.perform(click())
        rollButton.perform(click())
        subtractButton.perform(click())
        rollButton.perform(click())
        subtractButton.perform(click())

        val textView = onView(withId(myScoreTextField))
        textView.check(matches(withText("0"))) // 6 - 2 - 4 = 0
    }

    @Test
    fun testResetButton() {
        setMockDiceRoller(listOf(3, 4, 3)) // Fixed dice rolls

        val addButton = onView(withId(myAddButton))
        val resetButton = onView(withId(myResetButton))
        val rollButton = onView(withId(myRollButton))

        for (i in 1..3) {
            rollButton.perform(click())
            addButton.perform(click())
        }

        resetButton.perform(click())

        val textView = onView(withId(myScoreTextField))
        textView.check(matches(withText("0"))) // Reset score to 0
    }

    @Test
    fun testScoreOnRotation() {
        setMockDiceRoller(listOf(2, 3, 5)) // Fixed dice rolls

        val addButton = onView(withId(myAddButton))
        val rollButton = onView(withId(myRollButton))

        for (i in 1..3) {
            rollButton.perform(click())
            addButton.perform(click())
        }

        val textView = onView(withId(myScoreTextField))
        textView.check(matches(withText("10"))) // 2 + 3 + 5 = 10

        mActivityScenarioRule.scenario.onActivity { activity ->
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        textView.check(matches(withText("10"))) // Score should remain 10 after rotation
    }

    @Test
    fun testScoreOnRotationWithClick() {
        // Mock dice rolls to 2, 3, and 5
        setMockDiceRoller(listOf(2, 3, 5))

        val addButton = onView(withId(myAddButton))
        val rollButton = onView(withId(myRollButton))

        // Perform roll and add actions twice (expecting score to be 5)
        for (i in 1..2) {
            rollButton.perform(click())
            addButton.perform(click())
        }

        // Check the score before rotation (expecting score to be 5)
        val textView = onView(withId(myScoreTextField))
        textView.check(matches(withText("5")))

        // Rotate the screen
        mActivityScenarioRule.scenario.onActivity { activity ->
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        // Re-mock the dice roller to ensure consistent behavior after rotation
        setMockDiceRoller(listOf(5))

        // Perform another roll and add action (expecting score to be 10)
        rollButton.perform(click())
        addButton.perform(click())

        // Check the score after rotation and additional roll/add
        val textView2 = onView(withId(myScoreTextField))
        textView2.check(matches(withText("10")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}