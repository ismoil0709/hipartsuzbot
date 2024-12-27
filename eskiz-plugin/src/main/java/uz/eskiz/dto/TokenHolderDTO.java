package uz.eskiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenHolderDTO {
    private String accessToken;
    private LocalDateTime expiresIn;

    public boolean isTokenExpired() {
        return LocalDateTime.now().isAfter(expiresIn);
    }
}
