package com.financial.funds.transfer.controller

import com.financial.funds.transfer.model.TransferRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/transfer")
class TransferController() {

    @PostMapping
    suspend fun transfer(@RequestBody req: TransferRequest) {

    }
}