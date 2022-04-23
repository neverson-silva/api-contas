package com.dersaun.apicontas.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRefreshRequest implements Serializable {

    @NotEmpty
    private String refreshToken;
}
