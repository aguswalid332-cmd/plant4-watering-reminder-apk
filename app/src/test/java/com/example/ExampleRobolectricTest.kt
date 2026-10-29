package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.engine.CyberEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Cyber-X VIP", appName)
  }

  @Test
  fun `test cyber engine seed cracking and prediction`() {
    val engine = CyberEngine()
    val seedResult = engine.crackSeed()
    assertNotNull(seedResult)
    assertTrue(seedResult.seedHex.startsWith("0x"))
    assertTrue(seedResult.matchRate >= 99.0)

    val prediction = engine.calculatePrediction("202610291001", listOf(1, 3, 7, 9, 2))
    assertNotNull(prediction)
    assertTrue(prediction.digit in 0..9)
    assertTrue(prediction.size == "BESAR" || prediction.size == "KECIL")
  }
}
