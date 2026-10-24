package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.CheapFindsRepository
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
    assertEquals("CheapFinds", appName)
  }

  @Test
  fun `generate promo kit generates valid affiliate link and script`() {
    val repo = CheapFindsRepository()
    val kit = repo.generatePromoKit(
      rawUrl = "https://lazada.com.ph/products/a96-dual-sim-8gb-ram",
      tone = "Viral UGC",
      targetPlatform = "TikTok"
    )

    assertNotNull(kit.id)
    assertTrue(kit.affiliateUrl.contains("aff_id=george_finds"))
    assertTrue(kit.affiliateUrl.contains("sub_id=tiktok"))
    assertTrue(kit.scriptHook.isNotBlank())
    assertTrue(kit.socialCaption.isNotBlank())
  }
}
