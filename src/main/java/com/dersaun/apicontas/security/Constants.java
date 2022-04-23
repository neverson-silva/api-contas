package com.dersaun.apicontas.security;

public interface Constants {
    long ACCESS_TOKEN_VALIDITY_SECONDS = 12*60*60;
    long ACCESS_REFRESH_TOKEN_VALIDITY_SECONDS = 48*60*60;
    String SIGNING_KEY = "h2yj12j9AcIAb6rKkkrrRDMnchpdhjmFdDsnMjd7FHOUqeHCXDOh04yGXX4m";
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    String AUTHORITIES_KEY = "scopes";
}
