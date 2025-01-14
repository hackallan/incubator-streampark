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

package org.apache.streampark.plugin.registry.core.server;

import org.apache.streampark.plugin.registry.core.JdbcRegistryProperties;
import org.apache.streampark.plugin.registry.core.LockUtils;
import org.apache.streampark.plugin.registry.core.model.DTO.JdbcRegistryLockDTO;
import org.apache.streampark.plugin.registry.core.repository.JdbcRegistryLockRepository;
import org.apache.streampark.registry.api.thread.ThreadUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JdbcRegistryLockManager implements IJdbcRegistryLockManager {

    private final JdbcRegistryProperties jdbcRegistryProperties;
    private final JdbcRegistryLockRepository jdbcRegistryLockRepository;

    // lockKey -> LockEntry
    private final Map<String, LockEntry> jdbcRegistryLockHolderMap = new HashMap<>();

    public JdbcRegistryLockManager(JdbcRegistryProperties jdbcRegistryProperties,
                                   JdbcRegistryLockRepository jdbcRegistryLockRepository) {
        this.jdbcRegistryProperties = jdbcRegistryProperties;
        this.jdbcRegistryLockRepository = jdbcRegistryLockRepository;
    }

    @Override
    public void acquireJdbcRegistryLock(Long clientId, String lockKey) {
        String lockOwner = LockUtils.getLockOwner();
        while (true) {
            LockEntry lockEntry = jdbcRegistryLockHolderMap.get(lockKey);
            if (lockEntry != null && lockOwner.equals(lockEntry.getLockOwner())) {
                return;
            }
            JdbcRegistryLockDTO jdbcRegistryLock = JdbcRegistryLockDTO.builder()
                .lockKey(lockKey)
                .clientId(clientId)
                .lockOwner(lockOwner)
                .createTime(new Date())
                .build();
            try {
                jdbcRegistryLockRepository.insert(jdbcRegistryLock);
                if (jdbcRegistryLock != null) {
                    jdbcRegistryLockHolderMap.put(lockKey, LockEntry.builder()
                        .lockKey(lockKey)
                        .lockOwner(lockOwner)
                        .jdbcRegistryLock(jdbcRegistryLock)
                        .build());
                    return;
                }
                log.debug("{} acquire the lock {} success", lockOwner, lockKey);
            } catch (DuplicateKeyException duplicateKeyException) {
                // The lock is already exist, wait it release.
                continue;
            }
            log.debug("Acquire the lock {} failed try again", lockKey);
            // acquire failed, wait and try again
            ThreadUtils.sleep(jdbcRegistryProperties.getHeartbeatRefreshInterval().toMillis());
        }
    }

    @Override
    public boolean acquireJdbcRegistryLock(Long clientId, String lockKey, long timeout) {
        String lockOwner = LockUtils.getLockOwner();
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start <= timeout) {
            LockEntry lockEntry = jdbcRegistryLockHolderMap.get(lockKey);
            if (lockEntry != null && lockOwner.equals(lockEntry.getLockOwner())) {
                return true;
            }
            JdbcRegistryLockDTO jdbcRegistryLock = JdbcRegistryLockDTO.builder()
                .lockKey(lockKey)
                .clientId(clientId)
                .lockOwner(lockOwner)
                .createTime(new Date())
                .build();
            try {
                jdbcRegistryLockRepository.insert(jdbcRegistryLock);
                if (jdbcRegistryLock != null) {
                    jdbcRegistryLockHolderMap.put(lockKey, LockEntry.builder()
                        .lockKey(lockKey)
                        .lockOwner(lockOwner)
                        .jdbcRegistryLock(jdbcRegistryLock)
                        .build());
                    return true;
                }
                log.debug("{} acquire the lock {} success", lockOwner, lockKey);
            } catch (DuplicateKeyException duplicateKeyException) {
                // The lock is already exist, wait it release.
                continue;
            }
            log.debug("Acquire the lock {} failed try again", lockKey);
            // acquire failed, wait and try again
            ThreadUtils.sleep(jdbcRegistryProperties.getHeartbeatRefreshInterval().toMillis());
        }
        return false;
    }

    @Override
    public void releaseJdbcRegistryLock(Long clientId, String lockKey) {
        LockEntry lockEntry = jdbcRegistryLockHolderMap.get(lockKey);
        if (lockEntry == null) {
            return;
        }
        if (!clientId.equals(lockEntry.getJdbcRegistryLock().getClientId())) {
            throw new UnsupportedOperationException(
                "The client " + clientId + " is not the lock owner of the lock: " + lockKey);
        }
        jdbcRegistryLockRepository.deleteById(lockEntry.getJdbcRegistryLock().getId());
        jdbcRegistryLockHolderMap.remove(lockKey);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LockEntry {

        private String lockKey;
        private String lockOwner;
        private JdbcRegistryLockDTO jdbcRegistryLock;
    }
}
