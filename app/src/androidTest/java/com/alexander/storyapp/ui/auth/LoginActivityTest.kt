package com.alexander.storyapp.ui.auth

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.alexander.storyapp.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class LoginActivityTest{
    private val email = "alex@alex.com"
    private val password = "password"

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setup(){
        ActivityScenario.launch(LoginActivity::class.java)
    }

    @Test
    fun loginTest(){
        Espresso.onView(withId(R.id.ed_login_email))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.ed_login_email)).perform(
            ViewActions.replaceText(email),
        )

        Espresso.onView(withId(R.id.ed_login_password))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.ed_login_password)).perform(
            ViewActions.replaceText(password),
        )

        Espresso.onView(withId(R.id.btn_login))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.btn_login)).perform(ViewActions.click())
    }
}