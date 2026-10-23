package com.back.faq.repository;

import com.back.faq.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FaqRepository extends CrudRepository<Faq, Long> {

    @Query(value = """
            select * from faq
            where question &@~ :keyword or answer &@~ :keyword
            """, nativeQuery = true)
    List<Faq> searchByKeyword(@Param("keyword") String keyword);
}
