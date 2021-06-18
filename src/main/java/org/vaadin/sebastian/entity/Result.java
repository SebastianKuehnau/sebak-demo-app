package org.vaadin.sebastian.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    private Double price;

    public Result() {
    }

    public Result(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @JsonProperty("result")
    @SuppressWarnings("unchecked")
    private void resultDeserializer(Map<String, Object> result) {
        this.price = (Double) result.get("price");
    }
}
