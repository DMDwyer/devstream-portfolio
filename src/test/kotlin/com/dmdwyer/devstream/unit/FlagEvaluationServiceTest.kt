package com.dmdwyer.devstream.unit

import com.dmdwyer.devstream.service.FlagEvaluationService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FlagEvaluationServiceTest {
    
    private val service = FlagEvaluationService()
    
    @Test
    fun `evaluateFlag returns enabled message when flag is enabled`() {
        val result = service.evaluateFlag("test-flag", true)

        assertThat(result).contains("enabled")
    }

    @Test
    fun `evaluateFlag returns disabled message when flag is disabled`() {
        val result = service.evaluateFlag("test-flag", false)

        assertThat(result).contains("disabled")
    }
}