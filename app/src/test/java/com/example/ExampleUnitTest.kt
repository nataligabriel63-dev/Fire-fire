package com.example

import com.example.data.model.DpiCalculator
import com.example.data.model.EdpiStatus
import com.example.data.model.HudFingerLayout
import com.example.data.model.HudGenerator
import com.example.data.repository.WeaponRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testWeaponRepositoryLoadsWeapons() {
        val weapons = WeaponRepository.weapons
        assertTrue("Weapons list should not be empty", weapons.isNotEmpty())

        val m1887 = WeaponRepository.getWeaponById("m1887")
        assertNotNull(m1887)
        assertEquals("M1887 (Doze Nova)", m1887.name)

        val mp40 = WeaponRepository.getWeaponById("mp40")
        assertNotNull(mp40)
        assertEquals("MP40", mp40.name)

        val deagle = WeaponRepository.getWeaponById("deagle")
        assertNotNull(deagle)
        assertEquals("Desert Eagle (Águia)", deagle.name)
    }

    @Test
    fun testEdpiCalculatorAndStatus() {
        val calculatedEdpi = DpiCalculator.calculateEdpi(dpi = 600, inGameGeralSensi = 180)
        assertEquals(1080, calculatedEdpi)

        val statusHigh = DpiCalculator.getEdpiStatus(1080)
        assertEquals(EdpiStatus.HIGH_COMPETITIVE, statusHigh)

        val statusBalanced = DpiCalculator.getEdpiStatus(750)
        assertEquals(EdpiStatus.BALANCED_PRO, statusBalanced)

        val statusOver = DpiCalculator.getEdpiStatus(1600)
        assertEquals(EdpiStatus.OVER_AIM, statusOver)
    }

    @Test
    fun testHudGeneratorProducesSynchronizedHud() {
        val m1887 = WeaponRepository.getWeaponById("m1887")
        val generatedHud = HudGenerator.generateHudForSensi(
            weapon = m1887,
            sensi = m1887.proPreset,
            dpi = 620,
            layout = HudFingerLayout.TWO_FINGERS
        )

        assertNotNull(generatedHud)
        assertTrue(generatedHud.elements.isNotEmpty())
        assertTrue(generatedHud.calculatedButtonSize in 38..65)
        assertTrue(generatedHud.calculatedButtonYPercent > 0.70f)
    }
}
