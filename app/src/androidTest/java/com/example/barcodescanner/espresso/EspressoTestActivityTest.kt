package com.example.barcodescanner.espresso

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.core.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EspressoTestActivityTest {

    @get:Rule
    val rule = ActivityScenarioRule(EspressoTestActivity::class.java)

    @Test
    fun buttonClick_showsToast() {
        // Para ver que el titulo se muestra bien
        onView(withId(R.id.espressoTitle)).check(matches(isDisplayed()))
        // Hace click en el boton que puse en el XML para testearlo y ver si sale e toast
        onView(withId(R.id.actionButton)).perform(click())
        // Segun vi no se puede verificar Toast facilmente sin extras pero el click se ejecuta
    }
}
