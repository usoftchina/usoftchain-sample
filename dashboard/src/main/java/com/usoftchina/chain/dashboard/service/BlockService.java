package com.usoftchina.chain.dashboard.service;

import com.usoftchina.chain.dashboard.dao.BlockRepository;
import com.usoftchina.chain.dashboard.entity.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yingp
 * @date 2018/9/14
 */
@Service
public class BlockService {

    @Autowired
    private BlockRepository blockRepository;

    public List<Block> pagingByChannel(String channel, Pageable pageable) {
        return blockRepository.pagingByChannel(channel, pageable);
    }
}
