package com.financial.funds.transfer.repository

import com.financial.funds.transfer.model.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<Account, Long>