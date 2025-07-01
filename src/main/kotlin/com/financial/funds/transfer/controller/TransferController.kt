package com.financial.funds.transfer.controller

import com.financial.funds.transfer.model.TransferRequest
import com.financial.funds.transfer.service.TransferService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/transfer")
class TransferController(
    private val service: TransferService
) {

    @PostMapping
     fun transfer(@RequestBody req: TransferRequest) {
        service.transfer(req.from, req.to, req.amount)
    }
}