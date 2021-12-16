package org.vaadin.sebastian.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.ExtractingResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.vaadin.sebastian.entity.Result;

import java.time.Duration;
import java.util.Optional;

@Service
public class BitcoinService {

    private RestTemplate restTemplate;

    Logger log = LoggerFactory.getLogger(BitcoinService.class);

    @Autowired
    public BitcoinService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(3000))
                .setReadTimeout(Duration.ofMillis(3000))
                .errorHandler(new ExtractingResponseErrorHandler())
                .build();
    }

    @Async
    public ListenableFuture<Optional<Result>> fetchBitcoinPriceAsync() {
        try {
            Result forObject = restTemplate.getForObject("https://api.cryptowat.ch/markets/kraken/btceur/price", Result.class);
            return AsyncResult.forValue(Optional.of(forObject));
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return AsyncResult.forExecutionException(ex);
        }
    }
}
