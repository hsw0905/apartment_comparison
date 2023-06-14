package me.harry.apartment_comparison.domain.repository;

import me.harry.apartment_comparison.domain.model.BlackList;
import org.springframework.data.repository.CrudRepository;

public interface BlackListRepository extends CrudRepository<BlackList, String> {
}
