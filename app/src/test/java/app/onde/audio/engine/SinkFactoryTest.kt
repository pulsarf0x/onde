package app.onde.audio.engine

import app.onde.audio.discovery.AirPlayDevice
import org.junit.Assert.*
import org.junit.Test

/** Routing fixtures verify protocol selection, not compatibility with physical speakers. */
class SinkFactoryTest {
    private fun receiver(features: Map<String, String>, version: Int = 2, raopPort: Int? = 5000) =
        AirPlayDevice("Test speaker", "192.0.2.1", 7000, "test-id", features = features,
            protocolVersion = version, raopPort = raopPort)

    @Test fun fairplayRaopWithTransientAirplay2SelectsAirplay2() {
        val device = receiver(mapOf("et" to "0,3,5", "features" to "0x0,0x10040"))
        assertEquals(Transport.AIRPLAY2, SinkFactory.transportFor(device))
        // A failed AirPlay 2 session must not try an unsupported encrypted RAOP route.
        assertNull(SinkFactory.alternative(device, Transport.AIRPLAY2))
    }

    @Test fun fairplayOnlyWithoutAirplay2IsRejectedBeforeCapture() {
        val device = receiver(mapOf("et" to "0,3,5"), version = 1)
        assertEquals(Transport.UNSUPPORTED, SinkFactory.transportFor(device))
    }

    @Test fun legacyRsaReceiverStillUsesRaop() {
        val device = receiver(mapOf("et" to "0,1"), version = 1)
        assertEquals(Transport.RAOP, SinkFactory.transportFor(device))
    }

    @Test fun airplay2ShairportUsesItsSupportedRaopEndpoint() {
        val device = receiver(mapOf("et" to "0,1", "features" to "0x0,0x10040", "model" to "Shairport Sync"))
        assertEquals(Transport.RAOP, SinkFactory.transportFor(device, preferAirPlay2 = true))
    }
}
