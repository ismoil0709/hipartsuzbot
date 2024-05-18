package uz.hiparts.hipartsuz.util;

import lombok.experimental.UtilityClass;
import uz.hiparts.hipartsuz.dto.ProductCreateUpdateDto;
import uz.hiparts.hipartsuz.model.Order;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class UtilLists {
    public static Map<Long, Order> orderMap = new HashMap<>();
    public static Map<Long,Map<String,String>> confirmCodeMap = new HashMap<>();
    public static Map<Long,String> userPhoneNumberMap = new HashMap<>();
    public static Map<Long, ProductCreateUpdateDto> productCreateUpdateDtoMap = new HashMap<>();
}
