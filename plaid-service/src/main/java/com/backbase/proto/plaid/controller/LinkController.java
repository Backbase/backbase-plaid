package com.backbase.proto.plaid.controller;

import com.backbase.proto.plaid.api.LinkApi;
import com.backbase.proto.plaid.model.PlaidLinkRequest;
import com.backbase.proto.plaid.model.PlaidLinkResponse;
import com.backbase.proto.plaid.model.SetAccessTokenRequest;
import com.backbase.proto.plaid.service.PlaidLinkService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * LinkController:
 * This uses link api to call link end points that allow a link to be initialised
 */
@RestController
@Slf4j

public class LinkController implements LinkApi {
    /**
     * Plaid link service contains the client the webhook and all the tools required to communicate with
     * Plaid
     */
    private final PlaidLinkService plaidLinkService;

    /**
     * @param plaidLinkService initialises link service
     */
    public LinkController(PlaidLinkService plaidLinkService) {
        this.plaidLinkService = plaidLinkService;
        log.info("Plaid Controller created");
    }

    /**
     * Sends a request for a plaid link token the request being parsed in and the link then stored.
     *
     * @param plaidLinkRequest  (optional) contains the request body for fetching a plaid link
     * @return http response indicating the success of the call
     */
    @Override
    public ResponseEntity<PlaidLinkResponse> requestPlaidLink(@Valid PlaidLinkRequest plaidLinkRequest) {
        log.info("Requesting Plaid Link: {}", plaidLinkRequest);
        PlaidLinkResponse plaidLink = plaidLinkService.createPlaidLink(plaidLinkRequest);
        return ResponseEntity.ok(plaidLink);
    }

    /**
     * Sends a request for the access token using a parsed request and returns and entity that can be stored in a
     * database
     *
     * @param setAccessTokenRequest  (optional) contains the request body for fetching the access token, the body
     *                               contains the public key
     * @return http response indicating the success of the call
     */
    @Override
    public ResponseEntity<Void> setPublicAccessToken(@Valid SetAccessTokenRequest setAccessTokenRequest) {
        log.info("Set Plaid Public Token: {}", setAccessTokenRequest);
        plaidLinkService.setPublicAccessToken(setAccessTokenRequest);
        return ResponseEntity.accepted().build();
    }


}
