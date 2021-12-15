DROP DATABASE IF EXISTS trading_engine;
CREATE DATABASE trading_engine DEFAULT CHARACTER SET utf8mb4;
USE trading_engine;

-- ----------------------------
--  Table structure for order
-- ----------------------------
DROP TABLE
    IF EXISTS `order`;

CREATE TABLE `order`
(
    id              BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL
        COMMENT 'ID',
    uid             CHAR(8)                               NOT NULL
        COMMENT '用户UID',
    client_order_no VARCHAR(64)                           NOT NULL
        COMMENT '客户端订单号',
    symbol          VARCHAR(64)                           NOT NULL
        COMMENT '交易对',
    side            VARCHAR(4)                            NOT NULL
        COMMENT '买卖方向',
    type            VARCHAR(10)                           NOT NULL
        COMMENT '订单类型',
    price           DECIMAL(20, 12)                       NOT NULL DEFAULT 0
        COMMENT '委托价格',
    quantity        DECIMAL(20, 12)                       NOT NULL
        COMMENT '委托数量',
    trade_quantity  DECIMAL(20, 12)                       NOT NULL DEFAULT 0
        COMMENT '成交数量',
    status          VARCHAR(20)                           NOT NULL
        COMMENT '状态',
    create_time     TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '创建时间',
    update_time     TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        COMMENT '更新时间',
    UNIQUE INDEX (uid, client_order_no)
)
    COMMENT '订单表';

-- ----------------------------
--  Table structure for trade
-- ----------------------------
DROP TABLE
    IF EXISTS `trade`;

CREATE TABLE `trade`
(
    id             BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL
        COMMENT 'ID',
    taker_order_id BIGINT(20)                            NOT NULL
        COMMENT 'Taker订单ID',
    maker_order_id BIGINT(20)                            NOT NULL
        COMMENT 'Maker订单ID',
    symbol         VARCHAR(64)                           NOT NULL
        COMMENT '交易对',
    price          DECIMAL(20, 12)                       NOT NULL
        COMMENT '成交价格',
    quantity       DECIMAL(20, 12)                       NOT NULL
        COMMENT '成交数量',
    taker_fee      DECIMAL(20, 12)                       NOT NULL
        COMMENT 'Taker手续费',
    maker_fee      DECIMAL(20, 12)                       NOT NULL
        COMMENT 'Maker手续费',
    create_time    TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '创建时间',
    update_time    TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        COMMENT '更新时间'
)
    COMMENT '交易表';

-- ----------------------------
--  Table structure for symbol_config
-- ----------------------------
DROP TABLE
    IF EXISTS `symbol_config`;

CREATE TABLE `symbol_config`
(
    id             BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL
        COMMENT 'ID',
    symbol         VARCHAR(64)                           NOT NULL
        COMMENT '交易对',
    taker_fee_rate DECIMAL(20, 12)                       NOT NULL
        COMMENT 'Taker手续费率',
    maker_fee_rate DECIMAL(20, 12)                       NOT NULL
        COMMENT 'Maker手续费率',
    price_scale    INT                                   NOT NULL
        COMMENT '价格精度',
    quantity_scale INT                                   NOT NULL
        COMMENT '数量精度',
    sort           INT                                   NOT NULL
        COMMENT '排序',
    enable         TINYINT                               NOT NULL DEFAULT 1
        COMMENT '是否可用',
    create_time    TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '创建时间',
    update_time    TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        COMMENT '更新时间',
    UNIQUE INDEX (symbol)
)
    COMMENT '交易对配置表';

INSERT INTO symbol_config (symbol, taker_fee_rate, maker_fee_rate, price_scale, quantity_scale, sort)
VALUES ('BTCUSDT', 0.0002, 0.0004, 4, 8, 0),
       ('ETHUSDT', 0.0002, 0.0004, 4, 8, 1);

-- ----------------------------
--  Table structure for user
-- ----------------------------
DROP TABLE
    IF EXISTS user;

CREATE TABLE user
(
    id          BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL
        COMMENT 'ID',
    uid         VARCHAR(8)                            NOT NULL
        COMMENT 'UID',
    email       VARCHAR(128)                          NOT NULL
        COMMENT '邮箱',
    password    VARCHAR(128)                          NOT NULL DEFAULT ''
        COMMENT '密码',
    salt        VARCHAR(128)                          NOT NULL DEFAULT ''
        COMMENT '密码盐',
    enable      TINYINT                               NOT NULL DEFAULT 1
        COMMENT '是否可用',
    create_time TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '创建时间',
    update_time TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        COMMENT '更新时间',
    UNIQUE KEY (`uid`),
    UNIQUE KEY (`email`)
)
    COMMENT '用户表';

INSERT INTO user (uid, email)
    VALUE ('00000000', 'java@kangyonggan.com');

-- ----------------------------
--  Table structure for user_account
-- ----------------------------
DROP TABLE
    IF EXISTS user_account;

CREATE TABLE user_account
(
    id            BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL
        COMMENT 'ID',
    uid           VARCHAR(8)                            NOT NULL
        COMMENT 'UID',
    account_type  VARCHAR(16)                           NOT NULL
        COMMENT '账户类型',
    currency      VARCHAR(16)                           NOT NULL
        COMMENT '币种',
    total_amount  DECIMAL(20, 12)                       NOT NULL DEFAULT 0
        COMMENT '总资产',
    frozen_amount DECIMAL(20, 12)                       NOT NULL DEFAULT 0
        COMMENT '冻结资产',
    enable        TINYINT                               NOT NULL DEFAULT 1
        COMMENT '是否可用',
    create_time   TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '创建时间',
    update_time   TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        COMMENT '更新时间',
    UNIQUE KEY (`uid`, `account_type`, `currency`)
)
    COMMENT '用户账户表';

INSERT INTO user_account (uid, account_type, currency, total_amount, frozen_amount)
VALUES ('00000000', 'SPOT', 'USDT', 100000, 0);

-- ----------------------------
--  Table structure for user_account_log
-- ----------------------------
DROP TABLE
    IF EXISTS user_account_log;

CREATE TABLE user_account_log
(
    id           BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL
        COMMENT 'ID',
    uid          VARCHAR(8)                            NOT NULL
        COMMENT 'UID',
    order_no     VARCHAR(64)                           NOT NULL
        COMMENT '订单号',
    account_type VARCHAR(16)                           NOT NULL
        COMMENT '账户类型',
    symbol       VARCHAR(64)                           NOT NULL
        COMMENT '交易对',
    type         VARCHAR(16)                           NOT NULL
        COMMENT '类型',
    amount       DECIMAL(20, 12)                       NOT NULL DEFAULT 0
        COMMENT '金额',
    status       VARCHAR(16)                           NOT NULL
        COMMENT '状态',
    enable       TINYINT                               NOT NULL DEFAULT 1
        COMMENT '是否可用',
    create_time  TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '创建时间',
    update_time  TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        COMMENT '更新时间',
    KEY (`uid`),
    KEY (`order_no`)
)
    COMMENT '用户账户日志表';

-- ----------------------------
--  Table structure for permission
-- ----------------------------
DROP TABLE
    IF EXISTS permission;

CREATE TABLE permission
(
    id          BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL
        COMMENT 'ID',
    uid         VARCHAR(8)                            NOT NULL
        COMMENT 'UID',
    api_key     VARCHAR(64)                           NOT NULL
        COMMENT 'ApiKey',
    secret_key  VARCHAR(128)                          NOT NULL
        COMMENT 'SecretKey',
    white_list  VARCHAR(256)                          NOT NULL DEFAULT ''
        COMMENT '白名单',
    remark      VARCHAR(64)                           NOT NULL DEFAULT ''
        COMMENT '备注',
    enable      TINYINT                               NOT NULL DEFAULT 1
        COMMENT '是否可用',
    create_time TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '创建时间',
    update_time TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        COMMENT '更新时间',
    UNIQUE KEY (api_key)
)
    COMMENT '权限表';

