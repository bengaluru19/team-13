package vola.systers.com.android.activities;

import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import vola.systers.com.android.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class SignInActivityTest {

    @Rule
    public ActivityTestRule<SignInActivity> signInActivityTestRule = new ActivityTestRule<SignInActivity>(SignInActivity.class);

    private String incorrectEmail = "invalid_mail@gmail.com";
    private String invalidPassword="incorrect@1234";
    private String email = "testing@systers.com";
    private String password ="testing1234";
    private String emptyPassword="";
    private String emptyEmail ="";
    private String invalidEmail="123456//.com";

    @Test
    public void testWithInvalidCredentials()
    {
        // input some text in the edit text
        onView(withId(R.id.input_email)).perform(typeText(incorrectEmail));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.input_password)).perform(typeText(invalidPassword));
        Espresso.closeSoftKeyboard();
        // perform button click
        onView(withId(R.id.btn_login)).perform(click()).check(matches(isDisplayed()));
 }

    @Test
    public void testWithValidCredentials()
    {
        // input some text in the edit text
        onView(withId(R.id.input_email)).perform(typeText(email));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.input_password)).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        // perform button click
        onView(withId(R.id.btn_login)).perform(click());
        SystemClock.sleep(3000);
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }

    @Test
    public void testWithNoPassword()
    {
        // input some text in the edit text
        onView(withId(R.id.input_email)).perform(typeText(email));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.input_password)).perform(typeText(emptyPassword));
        Espresso.closeSoftKeyboard();
        // perform button click
        onView(withId(R.id.btn_login)).perform(click()).check(matches(isDisplayed()));
        onView(withId(R.id.btn_login)).perform(click()).check(matches(isDisplayed()));
    }

    @Test
    public void testWithNoEmailNoPassword()
    {
        // input some text in the edit text
        onView(withId(R.id.input_email)).perform(typeText(emptyEmail));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.input_password)).perform(typeText(emptyPassword));
        Espresso.closeSoftKeyboard();
        // perform button click
        onView(withId(R.id.btn_login)).perform(click()).check(matches(isDisplayed()));
        onView(withId(R.id.btn_login)).perform(click()).check(matches(isDisplayed()));
    }

    @Test
    public void testWithNoEmail()
    {
        // input some text in the edit text
        onView(withId(R.id.input_email)).perform(typeText(emptyEmail));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.input_password)).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        // perform button click
        onView(withId(R.id.btn_login)).perform(click()).check(matches(isDisplayed()));
        onView(withId(R.id.btn_login)).perform(click()).check(matches(isDisplayed()));
    }

    @Test
    public void testWithInvalidEmail()
    {
        // input some text in the edit text
        onView(withId(R.id.input_email)).perform(typeText(invalidEmail));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.input_password)).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        // perform button click
        onView(withId(R.id.btn_login)).perform(click()).check(matches(isDisplayed()));
        onView(withId(R.id.btn_login)).perform(click()).check(matches(isDisplayed()));
    }
}