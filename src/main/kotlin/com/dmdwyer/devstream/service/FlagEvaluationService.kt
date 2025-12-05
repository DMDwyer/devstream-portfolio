package com.dmdwyer.devstream.service

import org.springframework.stereotype.Service

@Service
open class FlagEvaluationService {

    open fun evaluateFlag(flagKey: String, enabled: Boolean): String {
        return if (enabled) {
            "Flag '$flagKey' is enabled."
        } else {
            "Flag '$flagKey' is disabled."
        }
    }
}