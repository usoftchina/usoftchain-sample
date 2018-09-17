package com.usoftchina.chain.dashboard.dao;

import com.usoftchina.chain.dashboard.entity.Blockchain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yingp
 * @date 2018/9/14
 */
@Repository
public class BlockchainRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查找全部
     *
     * @return
     */
    public List<Blockchain> findAll() {
        try {
            return jdbcTemplate.query("select * from `cc_blockchain` order by `channel`",
                    new BeanPropertyRowMapper<>(Blockchain.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 按channel查找
     *
     * @param channel
     * @return
     */
    public Blockchain findOne(String channel) {
        try {
            return jdbcTemplate.queryForObject("select * from `cc_blockchain` where `channel`=?",
                    new BeanPropertyRowMapper<>(Blockchain.class), channel);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 保存、更新
     *
     * @param blockchain
     */
    public void saveOrUpdate(Blockchain blockchain) {
        if (null != findOne(blockchain.getChannel())) {
            jdbcTemplate.update("update `cc_blockchain` set `height`=?,`current_block_hash`=?,`previous_block_hash`=? " +
                    "where `channel`=?", blockchain.getHeight(), blockchain.getCurrentBlockHash(), blockchain.getPreviousBlockHash(), blockchain.getChannel());
        } else {
            jdbcTemplate.update("insert into `cc_blockchain`(`height`,`current_block_hash`,`previous_block_hash`,`channel`) " +
                    "values (?,?,?,?)", blockchain.getHeight(), blockchain.getCurrentBlockHash(), blockchain.getPreviousBlockHash(), blockchain.getChannel());
        }
    }

}
