package com.viniciuskegler.salesapp.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestResponsePage<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestResponsePage(@JsonProperty("content") List<T> content,
                            @JsonProperty("page") RestPage page) {
        super(content, PageRequest.of(page.number, page.size), page.totalElements);
    }

    public RestResponsePage(List<T> content) {
        super(content);
    }

    public RestResponsePage() {
        super(List.of());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RestPage(
            @JsonProperty("totalElements") long totalElements,
            @JsonProperty("number") int number,
            @JsonProperty("size") int size) {
    }

}