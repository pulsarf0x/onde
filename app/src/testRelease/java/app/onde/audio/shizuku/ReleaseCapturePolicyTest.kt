package app.onde.audio.shizuku

import app.onde.audio.BuildConfig
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ReleaseCapturePolicyTest {
    @Test fun releaseNeverOffersPrivilegedCapture() = runBlocking {
        assertFalse(BuildConfig.ENABLE_SHIZUKU)
        ShizukuManager.refresh()
        ShizukuManager.bind()
        ShizukuManager.requestPermission()
        assertEquals(ShizukuManager.Status.NOT_INSTALLED, ShizukuManager.status.value)
        assertNull(ShizukuManager.privileged.value)
        assertNull(ShizukuManager.awaitPrivileged())
    }
}
