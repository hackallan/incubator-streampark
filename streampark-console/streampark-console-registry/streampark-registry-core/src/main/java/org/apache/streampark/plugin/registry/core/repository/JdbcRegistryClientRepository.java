/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.streampark.plugin.registry.core.repository;

import org.apache.streampark.plugin.registry.core.mapper.JdbcRegistryClientHeartbeatMapper;
import org.apache.streampark.plugin.registry.core.model.DO.JdbcRegistryClientHeartbeat;
import org.apache.streampark.plugin.registry.core.model.DTO.JdbcRegistryClientHeartbeatDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Repository
public class JdbcRegistryClientRepository {

    @Autowired
    private JdbcRegistryClientHeartbeatMapper jdbcRegistryClientHeartbeatMapper;

    public List<JdbcRegistryClientHeartbeatDTO> queryAll() {
        return jdbcRegistryClientHeartbeatMapper.selectAll()
            .stream()
            .map(JdbcRegistryClientHeartbeatDTO::fromJdbcRegistryClientHeartbeat)
            .collect(Collectors.toList());
    }

    public void deleteByIds(List<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return;
        }
        jdbcRegistryClientHeartbeatMapper.deleteBatchIds(clientIds);
    }

    public boolean updateById(JdbcRegistryClientHeartbeatDTO jdbcRegistryClientHeartbeatDTO) {
        JdbcRegistryClientHeartbeat jdbcRegistryClientHeartbeat =
            JdbcRegistryClientHeartbeatDTO.toJdbcRegistryClientHeartbeat(jdbcRegistryClientHeartbeatDTO);
        return jdbcRegistryClientHeartbeatMapper.updateById(jdbcRegistryClientHeartbeat) == 1;
    }

    public void insert(JdbcRegistryClientHeartbeatDTO jdbcRegistryClient) {
        checkNotNull(jdbcRegistryClient.getId());
        JdbcRegistryClientHeartbeat jdbcRegistryClientHeartbeat =
            JdbcRegistryClientHeartbeatDTO.toJdbcRegistryClientHeartbeat(jdbcRegistryClient);
        jdbcRegistryClientHeartbeatMapper.insert(jdbcRegistryClientHeartbeat);

    }
}
