package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.dto.OrderDto;

import java.util.List;

@Service
public interface OrderService {

    void create(OrderDto order);

    OrderDto getById(Long id);

    List<OrderDto> getAll();

}
