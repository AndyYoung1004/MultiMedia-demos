package com.example.multimedia.player;

public interface ExtractorsFactory {
    ExtractorsFactory EMPTY = () -> new IExtractor[] {};

    IExtractor[] createExtractors();
}
