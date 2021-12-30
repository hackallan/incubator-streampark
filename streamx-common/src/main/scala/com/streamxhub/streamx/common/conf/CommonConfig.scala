/*
 * Copyright (c) 2019 The StreamX Project
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamxhub.streamx.common.conf

/**
 * Common Configuration
 *
 * @author Al-assad
 */
object CommonConfig {

  val STREAMX_WORKSPACE_LOCAL: ConfigOption = ConfigOption(
    key = "streamx.workspace.local",
    defaultValue = "/streamx",
    classType = classOf[java.lang.String])

  val STREAMX_WORKSPACE_REMOTE: ConfigOption = ConfigOption(
    key = "streamx.workspace.remote",
    defaultValue = "/streamx",
    classType = classOf[java.lang.String])

  val STREAMX_HADOOP_USER_NAME: ConfigOption = ConfigOption(
    key = "streamx.hadoop-user-name",
    defaultValue = "hdfs",
    classType = classOf[java.lang.String])

  val DOCKER_IMAGE_NAMESPACE: ConfigOption = ConfigOption(
    key = "streamx.docker.register.image-namespace",
    defaultValue = "streamx",
    classType = classOf[java.lang.String],
    description = "namespace for docker image used in docker building env and target image register")

  val DOCKER_MAX_CONNECTIONS: ConfigOption = ConfigOption(
    key = "streamx.docker.http-client.max-connections",
    defaultValue = 100L,
    classType = classOf[java.lang.Long],
    description = "instantiating max connections for DockerHttpClient")

  val DOCKER_CONNECTION_TIMEOUT_SEC: ConfigOption = ConfigOption(
    key = "streamx.docker.http-client.connection-timeout-sec",
    defaultValue = 100L,
    classType = classOf[java.lang.Long],
    description = "instantiating connection timeout for DockerHttpClient")

  val DOCKER_RESPONSE_TIMEOUT_SEC: ConfigOption = ConfigOption(
    key = "streamx.docker.http-client.response-timeout-sec",
    defaultValue = 120L,
    classType = classOf[java.lang.Long],
    description = "instantiating connection timeout for DockerHttpClient")

  val MAVEN_REMOTE_URL: ConfigOption = ConfigOption(
    key = "streamx.maven.remote-url",
    defaultValue = "https://repo1.maven.org/maven2/",
    classType = classOf[java.lang.String],
    description = "maven repository used for built-in compilation")

}