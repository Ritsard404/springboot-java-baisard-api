package com.ritsard.baisard.utils.converter.cryptions.aes;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = AesCryptoComponent.AesDecryptionSerializer.class)
@JsonDeserialize(using = AesCryptoComponent.AesEncryptionDeserializer.class)
public @interface AesEncryption {
}

