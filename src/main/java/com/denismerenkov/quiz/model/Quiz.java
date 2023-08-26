
package com.denismerenkov.quiz.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.denismerenkov.quiz.model.Result;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "response_code",
    "results"
})

public class Quiz {

    @JsonProperty("response_code")
    private int responseCode;
    @JsonProperty("results")
    private List<Result> results = new ArrayList<Result>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Quiz() {
    }

    /**
     * 
     * @param results
     * @param responseCode
     */
    public Quiz(int responseCode, List<Result> results) {
        super();
        this.responseCode = responseCode;
        this.results = results;
    }

    @JsonProperty("response_code")
    public int getResponseCode() {
        return responseCode;
    }

    @JsonProperty("response_code")
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    @JsonProperty("results")
    public List<Result> getResults() {
        return results;
    }

    @JsonProperty("results")
    public void setResults(List<Result> results) {
        this.results = results;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public List<String> quizFormatCSV(){
        List<String> strings = new ArrayList<>();
        for (Result result : results) {
            strings.add(result.toCSV());
        }
        return strings;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "responseCode=" + responseCode +
                ", results=" + results +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
