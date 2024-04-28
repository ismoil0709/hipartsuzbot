package uz.hiparts.hipartsuz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class AddressDto {
    private Double lat;
    private Double lon;
    private String name;
    @JsonProperty("display_name")
    private String displayName;
}
