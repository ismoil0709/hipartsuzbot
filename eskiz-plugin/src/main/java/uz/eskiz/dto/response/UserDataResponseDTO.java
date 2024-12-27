package uz.eskiz.dto.response;

import lombok.Data;

@Data
public class UserDataResponseDTO {
    private String status;
    private User data;

    @Data
    public static class User {
        private int id;
        private String createdAt;
        private String updatedAt;
        private String name;
        private String email;
        private String role;
        private String status;
        private boolean isVip;
        private double balance;
    }
}

