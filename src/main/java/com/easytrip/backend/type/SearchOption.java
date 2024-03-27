package com.easytrip.backend.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchOption {

    TITLE("title"),
    CONTENT("content"),
    TITLE_AND_CONTENT("titleAndContent"),
    NICKNAME("nickname");

    private final String value;



}
