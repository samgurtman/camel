/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.netty4.http.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.netty4.NettyConstants;
import org.apache.camel.component.netty4.handlers.ClientChannelHandler;
import org.apache.camel.component.netty4.http.NettyHttpProducer;

/**
 * Netty HTTP {@link org.apache.camel.component.netty.handlers.ClientChannelHandler} that handles the response combing
 * back from the HTTP server, called by this client.
 *
 */
public class HttpClientChannelHandler extends ClientChannelHandler {
    private final NettyHttpProducer producer;

    public HttpClientChannelHandler(NettyHttpProducer producer) {
        super(producer);
        this.producer = producer;
    }

    @Override
    protected Message getResponseMessage(Exchange exchange, ChannelHandlerContext ctx, Object message) throws Exception {
        FullHttpResponse response = (FullHttpResponse) message;
        if (!HttpHeaders.isKeepAlive(response)) {
            // just want to make sure we close the channel if the keepAlive is not true
            exchange.setProperty(NettyConstants.NETTY_CLOSE_CHANNEL_WHEN_COMPLETE, true);
        }
        // use the binding
        return producer.getEndpoint().getNettyHttpBinding().toCamelMessage(response, exchange, producer.getConfiguration());
    }
}
