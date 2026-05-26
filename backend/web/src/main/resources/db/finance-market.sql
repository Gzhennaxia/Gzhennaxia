-- 理财模块：行情日 K（标普 500 等）

CREATE TABLE IF NOT EXISTS fin_market_symbol (
    id           BIGSERIAL PRIMARY KEY,
    symbol       VARCHAR(32)  NOT NULL,
    name         VARCHAR(100) NOT NULL,
    market_type  VARCHAR(20)  NOT NULL DEFAULT 'INDEX',
    currency     VARCHAR(3)   DEFAULT 'USD',
    data_source  VARCHAR(32)  NOT NULL,
    external_id  VARCHAR(64)  NOT NULL,
    remark       TEXT,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_fin_market_symbol UNIQUE (symbol)
);

CREATE TABLE IF NOT EXISTS fin_market_daily (
    id           BIGSERIAL PRIMARY KEY,
    symbol_id    BIGINT         NOT NULL,
    trade_date   DATE           NOT NULL,
    open_price   DECIMAL(18, 4),
    high_price   DECIMAL(18, 4),
    low_price    DECIMAL(18, 4),
    close_price  DECIMAL(18, 4) NOT NULL,
    volume       BIGINT,
    source       VARCHAR(32)    NOT NULL,
    create_time TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_fin_market_daily UNIQUE (symbol_id, trade_date)
);

CREATE INDEX IF NOT EXISTS idx_fin_market_daily_date ON fin_market_daily (symbol_id, trade_date DESC);

CREATE TABLE IF NOT EXISTS fin_market_sync_log (
    id            BIGSERIAL PRIMARY KEY,
    symbol_id     BIGINT       NOT NULL,
    sync_type     VARCHAR(20)  NOT NULL,
    start_date    DATE,
    end_date      DATE,
    rows_affected INT          DEFAULT 0,
    status        VARCHAR(20)  NOT NULL,
    message       TEXT,
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO fin_market_symbol (symbol, name, market_type, currency, data_source, external_id)
SELECT 'SP500', '标普 500', 'INDEX', 'USD', 'FRED', 'SP500'
WHERE NOT EXISTS (SELECT 1 FROM fin_market_symbol WHERE symbol = 'SP500');
