package com.crypto.PortfolioTracker.ENUMs;

import lombok.Getter;

@Getter

public enum EtherScanChain {

    ETHEREUM(1),
    OPTIMISM(10),
    BINANCE_SMART_CHAIN(56),
    POLYGON(137),
    BASE(8453),
    ARBITRUM(42161),
    AVALANCHE(43114),
    SEPOLIA(11155111);

    private final int id;

    EtherScanChain(int id) {
        this.id = id;
    }

}