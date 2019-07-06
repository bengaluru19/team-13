package vola.systers.com.android.activities;

import android.os.SystemClock;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import vola.systers.com.android.R;
import vola.systers.com.android.model.Event;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.equalTo;
import android.support.test.espresso.contrib.NavigationViewActions;

public class SchedulePageTest {
    @Rule
    public ActivityTestRule<MenuActivity> NavigationMenuTestRule = new ActivityTestRule<MenuActivity>(MenuActivity.class);

    private static ViewAction actionOpenDrawer() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(DrawerLayout.class);
            }

            @Override
            public String getDescription() {
                return "open drawer";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((DrawerLayout) view).openDrawer(GravityCompat.START);
            }
        };
    }

    private static Matcher<Object> getEventItemName(final Matcher<String> itemMatcher){

        return new BoundedMatcher<Object, Event>(Event.class) {
            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("Event with name: ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(Event event) {
                return itemMatcher.matches(event.getName());
            }
        };
    }

    @Test
    public void testPerformOnScroll(){
        onView(withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        SystemClock.sleep(1000);
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_schedule));
        SystemClock.sleep(1000);
        onData(getEventItemName(equalTo
                ("Event Gsoc Testing"))).inAdapterView(withId(R.id.list)).perform(click());

    }

    @Test
    public void testOpenNavigationDrawerInSchedulePage() {
        onView(withId(R.id.drawer_layout)).perform(actionOpenDrawer()).check(matches(isDisplayed()));
    }

}
