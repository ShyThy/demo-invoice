package com.oxchains.billing.rest;

import com.oxchains.billing.rest.common.ChaincodeUriBuilder;
import com.oxchains.billing.rest.common.PresentAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import static com.oxchains.billing.domain.BillActions.BILL_REVOKE;
import static com.oxchains.billing.domain.BillActions.GET_REVOCATION;
import static com.oxchains.billing.rest.common.ClientResponse2ServerResponse.toPayloadTransformedServerResponse;
import static com.oxchains.billing.rest.common.ClientResponse2ServerResponse.toServerResponse;
import static com.oxchains.billing.util.ArgsUtil.args;
import static com.oxchains.billing.util.ResponseUtil.chaincodeInvoke;
import static com.oxchains.billing.util.ResponseUtil.chaincodeQuery;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

/**
 * @author aiet
 */
@Component
public class RevocationHandler extends ChaincodeUriBuilder {

  public RevocationHandler(@Autowired WebClient client,
                           @Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder) {
    super(client, uriBuilder.build().toString());
  }

  /* POST /bill/revocation */
  public Mono<ServerResponse> create(ServerRequest request) {
    return request.bodyToMono(PresentAction.class)
        .flatMap(revokeAction -> chaincodeInvoke(client, buildUri(args(BILL_REVOKE, revokeAction)))
            .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  public Mono<ServerResponse> get(ServerRequest request) {
    final String uid = request.pathVariable("uid");
    return chaincodeQuery(client, buildUri(args(GET_REVOCATION, uid)))
        .flatMap(clientResponse -> Mono.just(toPayloadTransformedServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }
}
