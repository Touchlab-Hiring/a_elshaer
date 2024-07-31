package co.touchlab.dogify

import android.content.pm.ActivityInfo
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.touchlab.dogify.SwipeRefreshLayoutMatchers.isRefreshing
import co.touchlab.dogify.ui.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
        // Launch the activity
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Inject
    lateinit var fakeDogBreedsRepository: FakeDogBreedsRepository

    @Test
    fun swipeRefreshLayout_showsOnLaunch() {
        onView(withId(R.id.swipe_refresh_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun recyclerView_showsOnLaunch() {
        onView(withId(R.id.breed_list)).check(matches(isDisplayed()))
    }

    @Test
    fun swipeToRefresh_loadsData() {
        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown())
        // Check if the RecyclerView got populated (you might need to wait for loading to complete)
        onView(withId(R.id.breed_list)).check(matches(hasMinimumChildCount(4)))
    }

    @Test
    fun clickOnRetry_showsData() {
        // Simulate an error state first, then click on retry
        // This requires your ViewModel to simulate error then success state
        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown()) // Assuming this triggers a load that will fail
        // Wait for error state and click on retry action in Snackbar
        onView(withText(R.string.retry)).perform(click())
        // Verify the data is shown in RecyclerView
        onView(withId(R.id.breed_list)).check(matches(hasMinimumChildCount(4)))
    }

    @Test
    fun recyclerView_scrollToItem_checkItemText() {
        // Scroll to position
        onView(withId(R.id.breed_list)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                4
            )
        )
        // Check that item at position has specific text
        onView(withText("dog 4")).check(matches(isDisplayed()))
    }

    @Test
    fun dataPersistsThroughConfigurationChange() {
        onView(withId(R.id.breed_list)).check(matches(hasMinimumChildCount(4)))

        // Trigger a configuration change, like a screen rotation
        ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        // Verify the data is still displayed after the configuration change
        onView(withId(R.id.breed_list)).check(matches(hasMinimumChildCount(4)))
    }

    @Test
    fun loadingIndicator_ShowsDuringRefresh_HidesAfterRefresh() {
        // Trigger refresh
        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown())
        // After data load, check if the loading indicator is not displayed
        onView(withId(R.id.breed_list)).check(matches(hasMinimumChildCount(4)))

        onView(withId(R.id.swipe_refresh_layout)).check(matches(not(isRefreshing())))
    }

}

object SwipeRefreshLayoutMatchers {
    @JvmStatic
    fun isRefreshing(): Matcher<View> {
        return object : BoundedMatcher<View, SwipeRefreshLayout>(
            SwipeRefreshLayout::class.java
        ) {

            override fun describeTo(description: Description) {
                description.appendText("is refreshing")
            }

            override fun matchesSafely(view: SwipeRefreshLayout): Boolean {
                return view.isRefreshing
            }
        }
    }
}
