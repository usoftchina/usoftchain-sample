package com.usoftchina.chain.dashboard.dao;

import com.usoftchina.chain.dashboard.entity.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yingp
 * @date 2018/9/14
 */
@Repository
public class BlockRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 按hash查找
     *
     * @param blockHash
     * @return
     */
    public Block findOne(String blockHash) {
        try {
            return jdbcTemplate.queryForObject("select `block_hash` as blockHash,`previous_hash` as previousHash," +
                            "`data_hash` as dataHash,`block_number` as blockNumber,`transaction_count` as transactionCount," +
                            "`channel` from `cc_block` where `block_hash`=?",
                    new BeanPropertyRowMapper<>(Block.class), blockHash);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 保存
     *
     * @param block
     */
    public void save(Block block) {
        jdbcTemplate.update("insert into `cc_block`(`block_hash`,`previous_hash`,`data_hash`,`block_number`," +
                        "`transaction_count`,`channel`) values (?,?,?,?,?,?)", block.getBlockHash(), block.getPreviousHash(),
                block.getDataHash(), block.getBlockNumber(), block.getTransactionCount(), block.getChannel());
    }

    /**
     * 按channel分页查找
     *
     * @param channel
     * @param pageable
     * @return
     */
    public Page<Block> pagingByChannel(String channel, Pageable pageable) {
        try {
            int total = jdbcTemplate.queryForObject("select count(1) from `cc_block` where `channel`=?", Integer.class, channel);
            List<Block> content = jdbcTemplate.query("select `block_hash` as blockHash,`previous_hash` as previousHash," +
                            "`data_hash` as dataHash,`block_number` as blockNumber,`transaction_count` as transactionCount," +
                            "`channel` from `cc_block` where `channel`=? order by `block_number` desc limit ?,?",
                    new BeanPropertyRowMapper<>(Block.class), channel, pageable.getPageNumber() * pageable.getPageSize(),
                    pageable.getPageSize());
            return new PageImpl<>(content, pageable, total);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}
