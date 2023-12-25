package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import java.util.List;

record Ledger(ClientId clientId, List<LedgerAccount> accounts) {}
