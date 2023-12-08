package com.Nunbody.domain.member.repository;


import com.Nunbody.domain.member.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}

