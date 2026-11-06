package net.konohana.sakuya.sandbox.models;

import org.jspecify.annotations.Nullable;

public final class LegacyStationDto {

    private final String staCode;
    private final @Nullable String staName;

    public LegacyStationDto(String staCode, @Nullable String staName) {
        this.staCode = staCode;
        this.staName = staName;
    }

    public String getStaCode() {
        return staCode;
    }

    public @Nullable String getStaName() {
        return staName;
    }
}
