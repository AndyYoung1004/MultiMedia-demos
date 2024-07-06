package com.example.multimedia.player;

public class DefaultExtractorsFactory implements ExtractorsFactory {
    private static final int[] DEFAULT_EXTRACTORS_ORDER =
            new int[] {

            };
    @Override
    public IExtractor[] createExtractors() {
        IExtractor[] result = new IExtractor[1];
        result[0] = new AVExtractor();
        return result;
    }
}
