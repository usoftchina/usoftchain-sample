package com.usoftchina.chain.dashboard.dao;

import com.usoftchina.chain.dashboard.entity.Block;
import com.usoftchina.chain.dashboard.entity.Transaction;
import com.usoftchina.chain.dashboard.entity.TransactionAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author yingp
 * @date 2018/9/14
 */
@Repository
public class TransactionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 按ID查询
     *
     * @param transactionID
     * @return
     */
    public Transaction findOne(String transactionID) {
        try {
            Transaction tx = jdbcTemplate.queryForObject("select `transaction_id` as transactionID,`nonce`,`valid`," +
                            "`validation_code` as validationCode,`timestamp`,`block_hash` as blockHash,`channel` " +
                            "from `cc_transaction` where `transaction_id`=?",
                    new BeanPropertyRowMapper<>(Transaction.class), transactionID);
            tx.setTransactionActions(findTransactionActions(transactionID));
            return tx;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 按区块查询交易
     *
     * @param blockHash
     * @return
     */
    public List<Transaction> findByBlockHash(String blockHash) {
        try {
            List<Transaction> txs = jdbcTemplate.query("select `transaction_id` as transactionID,`nonce`,`valid`," +
                            "`validation_code` as validationCode,`timestamp`,`block_hash` as blockHash,`channel` " +
                            "from `cc_transaction` where `block_hash`=? order by `timestamp` desc",
                    new BeanPropertyRowMapper<>(Transaction.class), blockHash);
            if (!CollectionUtils.isEmpty(txs)) {
                txs.forEach(tx -> tx.setTransactionActions(findTransactionActions(tx.getTransactionID())));
            }
            return txs;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private List<TransactionAction> findTransactionActions(String transactionID) {
        try {
            return jdbcTemplate.query("select `id`,`transaction_id` as transactionID,`response_status` as responseStatus," +
                            "`response_message` as responseMessage,`endorsements_count` as endorsementsCount,`status`,`payload`,`args` " +
                            "from `cc_transaction_action` where `transaction_id`=? order by `id`",
                    new BeanPropertyRowMapper<>(TransactionAction.class), transactionID);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 保存
     *
     * @param tx
     */
    public void save(Transaction tx) {
        jdbcTemplate.update("insert into `cc_transaction`(`transaction_id`,`nonce`,`valid`,`validation_code`," +
                        "`timestamp`,`block_hash`,`channel`) values (?,?,?,?,?,?,?)", tx.getTransactionID(), tx.getNonce(), tx.isValid(),
                tx.getValidationCode(), tx.getTimestamp(), tx.getBlockHash(), tx.getChannel());
        if (!CollectionUtils.isEmpty(tx.getTransactionActions())) {
            saveTransactionActions(tx.getTransactionActions());
        }
    }

    private void saveTransactionActions(final List<TransactionAction> actions) {
        jdbcTemplate.batchUpdate("insert into `cc_transaction_action`(`transaction_id`,`response_status`,`response_message`," +
                "`endorsements_count`,`status`,`payload`,`args`) values (?,?,?,?,?,?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                TransactionAction action = actions.get(i);
                ps.setString(1, action.getTransactionID());
                ps.setInt(2, action.getResponseStatus());
                ps.setString(3, action.getResponseMessage());
                ps.setInt(4, action.getEndorsementsCount());
                ps.setInt(5, action.getStatus());
                ps.setString(6, action.getPayload());
                ps.setString(7, action.getArgs());
            }

            @Override
            public int getBatchSize() {
                return actions.size();
            }
        });
    }

    /**
     * 按channel分页查找
     *
     * @param channel
     * @param pageable
     * @return
     */
    public Page<Transaction> pagingByChannel(String channel, Pageable pageable) {
        try {
            int total = jdbcTemplate.queryForObject("select count(1) from `cc_transaction` where `channel`=?", Integer.class, channel);
            List<Transaction> content = jdbcTemplate.query("select `transaction_id` as transactionID,`nonce`,`valid`," +
                            "`validation_code` as validationCode,`timestamp`,`block_hash` as blockHash,`channel` " +
                            "from `cc_transaction` where `channel`=? order by `timestamp` desc limit ?,?",
                    new BeanPropertyRowMapper<>(Transaction.class), channel, pageable.getPageNumber() * pageable.getPageSize(),
                    pageable.getPageSize());
            return new PageImpl<>(content, pageable, total);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
