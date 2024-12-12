package uz.hiparts.hipartsuz.util;

import lombok.experimental.UtilityClass;
import uz.hiparts.hipartsuz.dto.ProductCreateUpdateDto;
import uz.hiparts.hipartsuz.dto.ProductDto;
import uz.hiparts.hipartsuz.model.Branch;
import uz.hiparts.hipartsuz.model.Order;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class UtilLists {
    public static Map<Long, Order> orderMap = new HashMap<>();
    public static Map<Long, ProductCreateUpdateDto> productCreate = new HashMap<>();
    public static Map<Long, Branch> branchCreate = new HashMap<>();
    public static Map<Long, ProductDto> productUpdate = new HashMap<>();
}
