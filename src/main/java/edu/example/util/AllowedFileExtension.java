package edu.example.util;

public enum AllowedFileExtension {
    PDF,
    PNG,
    JPG;

    public static AllowedFileExtension caseIgnoreValueOf(String value) {
        for (AllowedFileExtension enumValue : values()) {
            if (enumValue.name().equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("No enum constant " + AllowedFileExtension.class + "." + value);
    }
}
