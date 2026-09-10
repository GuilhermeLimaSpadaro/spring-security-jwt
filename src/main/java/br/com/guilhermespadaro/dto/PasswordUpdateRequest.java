package br.com.guilhermespadaro.dto;

public record PasswordUpdateRequest(String previousPassword, String newPassword) {
}
