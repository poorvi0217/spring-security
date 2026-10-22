package com.spring.springSecurity.dto;

public record TokenResponse (String accessToken , String tokenType , long  expireInSeconds){
}
