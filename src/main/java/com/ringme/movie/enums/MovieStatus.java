package com.ringme.movie.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum MovieStatus {
    DELETED(-1, "Deleted"),
    WAITING_TO_CONVERT(1, "Waiting to Convert"),
    CONVERTING(2, "Converting"),
    WAITING_FOR_APPROVAL(9, "Waiting for Approval"),
    APPROVED(12, "Approved"),
    CONVERT_ERROR(3, "Convert Error"),
    REJECTED(8, "Rejected");

    private final int code;
    private final String description;

    @JsonValue
    public int getCode() {
        return code;
    }

    @JsonCreator
    public static MovieStatus fromCode(int code) {
        return Arrays.stream(MovieStatus.values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status code: " + code));
    }
}
