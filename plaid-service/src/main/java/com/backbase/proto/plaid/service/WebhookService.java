package com.backbase.proto.plaid.service;

import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.proto.plaid.configuration.PlaidConfigurationProperties;
import com.backbase.proto.plaid.model.Item;
import com.backbase.proto.plaid.model.Webhook;
import com.backbase.proto.plaid.repository.ItemRepository;
import com.backbase.proto.plaid.repository.WebhookRepository;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.ItemWebhookUpdateRequest;
import com.plaid.client.request.TransactionsRefreshRequest;
import com.plaid.client.response.ErrorResponse;
import com.plaid.client.response.ItemWebhookUpdateResponse;
import com.plaid.client.response.TransactionsRefreshResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Response;

/**
 * WebhookService:
 * Sets up and processes plaid webhook for used in backbase dbs
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookService {

    private final PlaidClient plaidClient;

    private final PlaidConfigurationProperties plaidConfigurationProperties;

    private final PlaidTransactionsService transactionsService;

    private final WebhookRepository webhookRepository;

    private final ItemRepository itemRepository;

    /**
     * Sets up the webhook with configurations
     *
     * @param accessToken provides authenticator in plaid
     * @param itemId identifies item for which the webhook is notifying for
     */
    public void setupWebhook(String accessToken, String itemId) {

        String webhookUrl = plaidConfigurationProperties.getWebhookBaseUrl() + "/webhook/" + itemId;

        log.info("Setting up webhook for item: {} with url:
        try {
            ItemWebhookUpdateResponse body = plaidClient.service().itemWebhookUpdate(new ItemWebhookUpdateRequest(accessToken, webhookUrl)).execute().body();
            log.info("Webhook Setup");
        } catch (IOException e) {
            throw new BadRequestException("Unable to setup web hook: ", e);
        }
    }

    /**
     * Processes the webhook for updates in transactions or the item
     *
     * @param webhook webhook being used
     */

    public void process(Webhook webhook) {
        log.info("Processing webhook: {} for item: {}", webhook.getWebhookType(), webhook.getItemId());
        webhook.setCreatedAt(LocalDateTime.now());
        webhookRepository.save(webhook);
        try {
            validateWebhook(webhook);
            switch (webhook.getWebhookType()) {
                case TRANSACTIONS:
                    processTransactions(webhook);
                    break;
                case ITEM:
                    processItem(webhook);
            }
            webhook.setCompleted(true);
        } catch (Exception exception) {
            exception.printStackTrace();
            webhook.setError(exception.getMessage());
        }
        webhookRepository.save(webhook);
    }

    /**
     * Validates the webhook to see if it is really coming from plaid, it throws an exception if it doesn't
     *
     * @param Webhook the webhook to be validated
     */
    private void validateWebhook(Webhook Webhook) {
        // Validate if web hook is really coming from plaid. Throw exception if it doesn't
    }

    /**
     * Processes the webhook for updates in transactions for an item, depending on the webhooks code it will update a
     * transaction differently
     *
     * @param webhook webhook to process for updates
     */
    private void processTransactions(Webhook webhook) {

        switch (webhook.getWebhookCode()) {
            case INITIAL_UPDATE: {
                // Fired when an Item's initial transaction pull is completed.
                // Note: The default pull is 30 days.
                log.info("Process Initial Update");
                transactionsService.ingestInitialUpdate(webhook.getItemId());
                break;
            }
            case HISTORICAL_UPDATE: {
                transactionsService.ingestHistoricalUpdate(webhook.getItemId());
                break;
            }
            case DEFAULT_UPDATE: {
                log.info("Process Default Update");
                transactionsService.ingestDefaultUpdate(webhook.getItemId());
                break;
            }
            case TRANSACTIONS_REMOVED: {
                log.info("Process transactions removed");
                transactionsService.removeTransactions(webhook.getItemId(), webhook.getRemovedTransactions());
            }
            default: {
                throw new BadRequestException("Not a valid web hook code");
            }
        }

    }

    /**
     * The webhook updates the Item database
     * @param Webhook that notifies Item updates
     */
    private void processItem(Webhook Webhook) {
        log.info("Webhook Acknowledged");
        //TODO: Update Item Database
    }

    /**
     * Refreshes the transactions for a specific item. Updates the transactions and processes them
     *
     * @param itemId identifies the set of transactions to be updated by which item it belongs to
     */
    @SneakyThrows
    public void refresh(String itemId) {
        log.info("Refreshing Transactions for: {}", itemId);
        Item item = itemRepository.findByItemId(itemId).orElseThrow(() -> new BadRequestException("Invalid item id: " + itemId));
        TransactionsRefreshRequest transactionsRefreshRequest = new TransactionsRefreshRequest(item.getAccessToken());
        transactionsRefreshRequest.clientId = plaidConfigurationProperties.getClientId();
        transactionsRefreshRequest.secret = plaidConfigurationProperties.getSecret();
        Response<TransactionsRefreshResponse> execute = plaidClient.service().transactionsRefresh(transactionsRefreshRequest).execute();
        if(execute.isSuccessful()) {
            TransactionsRefreshResponse body = execute.body();
            assert body != null;
            log.info("Refresh response: {}", body.getRequestId());
        } else {
            ErrorResponse errorResponse = plaidClient.parseError(execute);
            log.error("Error refreshing transactions: {}", errorResponse.getErrorMessage());
        }


    }
}
