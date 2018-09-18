CREATE TABLE IF NOT EXISTS `cc_blockchain`
(
`channel` varchar(255),
`current_block_hash` varchar(255),
`previous_block_hash` varchar(255),
`height` int,
PRIMARY KEY(`channel`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;
/
CREATE TABLE IF NOT EXISTS `cc_block`
(
`block_hash` varchar(255),
`previous_hash` varchar(255),
`data_hash` varchar(255),
`block_number` int,
`transaction_count` int,
`channel` varchar(255),
PRIMARY KEY(`block_hash`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;
/
CREATE TABLE IF NOT EXISTS `cc_transaction`
(
`transaction_id` varchar(255),
`nonce` varchar(255),
`valid` int,
`validation_code` char,
`timestamp` datetime,
`block_hash` varchar(255),
`channel` varchar(255),
PRIMARY KEY(`transaction_id`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;
/
CREATE TABLE IF NOT EXISTS `cc_transaction_action`
(
`id` int AUTO_INCREMENT,
`transaction_id` varchar(255),
`response_status` int,
`response_message` varchar(1000),
`endorsements_count` int,
`status` int,
`payload` varchar(1000),
`args` varchar(255),
PRIMARY KEY(`id`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;