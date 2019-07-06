package vola.systers.com.android.activities;

import android.os.SystemClock;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import vola.systers.com.android.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import android.support.test.espresso.contrib.NavigationViewActions;
import static org.hamcrest.CoreMatchers.not;

public class NavigationMenuTest {
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

    @Test
    public void testIfTheNavigationDrawerIsHiddenInitially()
    {
        onView(withId(R.id.nav_view)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testOpenNavigationDrawer()
    {
        SystemClock.sleep(2000);
        onView(withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        SystemClock.sleep(1000);
        onView(withId(R.id.nav_view)).check(matches(isDisplayed()));
    }



    @Test
    public void testFabButton()
    {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.list)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickOnNavigationItem() {

        onView(withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_schedule));
        onView(withId(R.id.list)).check(matches(isDisplayed()));
    }


    
}