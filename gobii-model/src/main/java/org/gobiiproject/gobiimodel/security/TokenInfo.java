package org.gobiiproject.gobiimodel.security;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token data adapter
 */

@Data
@NoArgsConstructor
public class TokenInfo {

    private String username;
    private String accessToken;
    private long expiry;
    
}