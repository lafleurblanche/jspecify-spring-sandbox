package net.konohana.sakuya.sandbox.models;

import org.jspecify.annotations.NullUnmarked;

/**
 * @NullMarked パッケージ下で @NullUnmarked を適用したレガシー DTO。
 * JSpecify による non-null 強制が解除され、未注釈の getter/setter は
 * Kotlin からプラットフォーム型（String!）として解釈される。
 */
@NullUnmarked
public final class UnmarkedLegacyDto {

    private String rawPayload;
    private String legacyCode;

    public UnmarkedLegacyDto(String rawPayload, String legacyCode) {
        this.rawPayload = rawPayload;
        this.legacyCode = legacyCode;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public String getLegacyCode() {
        return legacyCode;
    }

    public void setLegacyCode(String legacyCode) {
        this.legacyCode = legacyCode;
    }
}
