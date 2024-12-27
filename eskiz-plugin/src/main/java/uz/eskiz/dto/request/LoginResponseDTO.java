package uz.eskiz.dto.request;


import lombok.Data;

@Data
public class LoginResponseDTO {
    private String message;
    private Data data;

    @lombok.Data
    public static class Data {
        private String token;
    }
}
